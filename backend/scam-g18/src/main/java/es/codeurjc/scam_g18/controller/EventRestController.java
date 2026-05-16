package es.codeurjc.scam_g18.controller;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import es.codeurjc.scam_g18.dto.EventDTO;
import es.codeurjc.scam_g18.dto.EventMapper;
import es.codeurjc.scam_g18.model.Event;
import es.codeurjc.scam_g18.model.User;
import es.codeurjc.scam_g18.service.EventService;
import es.codeurjc.scam_g18.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/events")
@Tag(name = "Event API", description = "Event browsing, detail and management endpoints")
public class EventRestController {

    private static final int PAGE_SIZE = 10;

    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;

    @Autowired
    private EventMapper eventMapper;

    @GetMapping("/")
    @Operation(summary = "List events", description = "Returns paginated published events with optional search and tag filters.")
    public ResponseEntity<List<Map<String, Object>>> getEvents(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(defaultValue = "0") int page) {
        Long currentUserId = userService.getCurrentAuthenticatedUser().map(User::getId).orElse(null);
        List<Map<String, Object>> events = eventService.getEventsViewData(search, tags, currentUserId, page, PAGE_SIZE);
        return ResponseEntity.ok(events);
    }

    @GetMapping(value = "/locations", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Search locations", description = "Searches event locations using the external geocoding service.")
    public ResponseEntity<String> searchLocations(@RequestParam("q") String query) {
        return ResponseEntity.ok(eventService.searchLocations(query));
    }

    @GetMapping("/purchases")
    @Operation(summary = "List purchased events", description = "Returns events purchased by the authenticated user.")
    public ResponseEntity<List<Map<String, Object>>> purchasedEvents() {
        var userOpt = userService.getCurrentAuthenticatedUser();
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var purchasedEvents = eventService.getPurchasedEventsViewData(userOpt.get().getId());
        return ResponseEntity.ok(purchasedEvents);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get event detail", description = "Returns a full event detail payload including management and purchase flags.")
    public ResponseEntity<Map<String, Object>> getEvent(@PathVariable long id) {
        var eventOpt = eventService.getEventById(id);
        if (eventOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var eventData = eventService.getEventDetailViewData(id);
        
        Optional<User> currentUserOpt = userService.getCurrentAuthenticatedUser();
        boolean canManage = currentUserOpt
                .map(currentUser -> eventService.canManageEvent(eventOpt.get(), currentUser))
                .orElse(false);

        boolean alreadyPurchased = currentUserOpt
                .map(currentUser -> eventService.hasUserPurchasedEvent(currentUser.getId(), id))
                .orElse(false);

        eventData.put("canEdit", canManage);
        eventData.put("canDelete", canManage);
        eventData.put("alreadyPurchased", alreadyPurchased);

        return ResponseEntity.ok(eventData);
    }

    @PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create event", description = "Creates an event from multipart data, optionally including image and tags.")
    public ResponseEntity<Object> createEvent(
            @RequestPart("event") EventDTO eventDTO,
            @RequestParam(required = false) List<String> tagNames,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {
            
        var currentUserOpt = userService.getCurrentAuthenticatedUser();
        if (currentUserOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Event event = eventMapper.toDomain(eventDTO);
        
        String validationErrors = eventService.validateEventData(event, imageFile, true, false);
        if (validationErrors != null) {
            return ResponseEntity.badRequest().body(Map.of("error", validationErrors));
        }

        try {
            eventService.createEventFromForm(event, currentUserOpt.get(), imageFile, tagNames);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                    .buildAndExpand(event.getId()).toUri();
            return ResponseEntity.created(location).body(eventMapper.toDTO(event));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error creating event: " + e.getMessage()));
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update event", description = "Updates an event if the current user is authorized.")
    public ResponseEntity<Object> updateEvent(
            @PathVariable long id, 
            @RequestPart("event") EventDTO eventDTO,
            @RequestParam(required = false) List<String> tagNames,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {
            
        var currentUserOpt = userService.getCurrentAuthenticatedUser();
        if (currentUserOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Event eventUpdate = eventMapper.toDomain(eventDTO);
        
        String validationErrors = eventService.validateEventData(eventUpdate, imageFile, false, false);
        if (validationErrors != null) {
            return ResponseEntity.badRequest().body(Map.of("error", validationErrors));
        }

        try {
            boolean updated = eventService.updateEventIfAuthorized(id, eventUpdate, currentUserOpt.get(), imageFile, tagNames);
            if (updated) {
                var updatedEventOpt = eventService.getEventById(id);
                return ResponseEntity.ok(eventMapper.toDTO(updatedEventOpt.get()));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Not authorized or event not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error updating event: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete event", description = "Deletes an event if the current user is authorized.")
    public ResponseEntity<Object> deleteEvent(@PathVariable long id) {
        var currentUserOpt = userService.getCurrentAuthenticatedUser();
        if (currentUserOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boolean deleted = eventService.deleteEventIfAuthorized(id, currentUserOpt.get());
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Not authorized or event not found"));
        }
    }
}
