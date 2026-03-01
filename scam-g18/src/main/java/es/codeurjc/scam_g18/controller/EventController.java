package es.codeurjc.scam_g18.controller;

import java.util.List;

import es.codeurjc.scam_g18.service.EventService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import es.codeurjc.scam_g18.service.TagService;
import es.codeurjc.scam_g18.model.Event;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class EventController {

    private static final int PAGE_SIZE = 10;

    @Autowired
    private EventService eventService;

    @Autowired
    private TagService tagService;

    private final es.codeurjc.scam_g18.service.UserService userService;

    // Builds the controller with required services to manage events.
    public EventController(EventService eventService, TagService tagService,
            es.codeurjc.scam_g18.service.UserService userService) {
        this.eventService = eventService;
        this.tagService = tagService;
        this.userService = userService;
    }

    // Displays the event listing with search and tag filters.
    @GetMapping("/events")
    public String events(Model model, @RequestParam(required = false) String search,
            @RequestParam(required = false) List<String> tags) {
        Long currentUserId = userService.getCurrentAuthenticatedUser().map(user -> user.getId()).orElse(null);
        model.addAttribute("events", eventService.getEventsViewData(search, tags, currentUserId, 0, PAGE_SIZE));
        model.addAttribute("hasMoreEvents", eventService.getTotalPublishedEventsCount(search, tags) > PAGE_SIZE);
        model.addAttribute("search", search);
        model.addAttribute("tagsView", tagService.getTagsView(tags));

        return "events";
    }

    // AJAX endpoint for event pagination
    @GetMapping("/api/events")
    @org.springframework.web.bind.annotation.ResponseBody
    public org.springframework.http.ResponseEntity<List<java.util.Map<String, Object>>> getEventsApi(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(defaultValue = "0") int page) {
        Long currentUserId = userService.getCurrentAuthenticatedUser().map(user -> user.getId()).orElse(null);
        List<java.util.Map<String, Object>> events = eventService.getEventsViewData(search, tags, currentUserId, page,
            PAGE_SIZE);
        return org.springframework.http.ResponseEntity.ok(events);
    }

    // Displays events purchased by the authenticated user.
    @GetMapping("/events/purchased")
    public String purchasedEvents(Model model, java.security.Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        var userOpt = userService.getCurrentAuthenticatedUser();
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }

        var purchasedEvents = eventService.getPurchasedEventsViewData(userOpt.get().getId());
        model.addAttribute("events", purchasedEvents);
        model.addAttribute("hasPurchasedEvents", !purchasedEvents.isEmpty());

        return "purchasedEvents";
    }

    // Displays event detail and current user's management/purchase permissions.
    @GetMapping("/event/{id}")
    public String showEvent(Model model, @PathVariable long id, @RequestParam(required = false) String error) {
        var eventOpt = eventService.getEventById(id);
        if (eventOpt.isEmpty()) {
            return "redirect:/events";
        }

        Event event = eventOpt.get();
        var eventData = eventService.getEventDetailViewData(id);

        model.addAttribute("event", eventData);

        boolean canManage = userService.getCurrentAuthenticatedUser()
                .map(currentUser -> eventService.canManageEvent(event, currentUser))
                .orElse(false);

        boolean alreadyPurchased = userService.getCurrentAuthenticatedUser()
                .map(currentUser -> eventService.hasUserPurchasedEvent(currentUser.getId(), event.getId()))
                .orElse(false);

        model.addAttribute("canEdit", canManage);
        model.addAttribute("canDelete", canManage);
        model.addAttribute("errorFull", "full".equals(error));
        model.addAttribute("alreadyPurchased", alreadyPurchased);

        return "event";
    }

    // Deletes an event when the current user is authorized.
    @PostMapping("/event/{id}/delete")
    public String deleteEvent(@PathVariable long id) {
        userService.getCurrentAuthenticatedUser()
                .ifPresent(currentUser -> eventService.deleteEventIfAuthorized(id, currentUser));
        return "redirect:/events";
    }

    // Displays the event edit form if the user can manage it.
    @GetMapping("/event/{id}/edit")
    public String editEventForm(Model model, @PathVariable long id) {
        try {
            var eventOpt = eventService.getEventById(id);
            if (eventOpt.isPresent()) {
                Event event = eventOpt.get();

                var currentUserOpt = userService.getCurrentAuthenticatedUser();
                if (currentUserOpt.isPresent() && eventService.canManageEvent(event, currentUserOpt.get())) {
                    model.addAllAttributes(eventService.getEventEditViewData(event));

                    return "editEvent";
                } else {
                    System.out.println("User not authorized or current user is null for event: " + id);
                }
            } else {
                System.out.println("Event not found for ID: " + id);
            }
        } catch (Exception e) {
            System.err.println("Error rendering editEvent form: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/events";
    }

    // Updates an existing event when data is valid and permissions are granted.
    @PostMapping("/event/{id}/edit")
    public String updateEvent(
            @PathVariable long id,
            Event eventUpdate,
            org.springframework.validation.BindingResult bindingResult,
            @RequestParam(required = false) List<String> tagNames,
            @RequestParam(required = false) org.springframework.web.multipart.MultipartFile imageFile,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes)
            throws java.io.IOException, java.sql.SQLException {

        String validationErrors = eventService.validateEventData(eventUpdate, imageFile, false,
                bindingResult.hasErrors());
        if (validationErrors != null) {
            redirectAttributes.addFlashAttribute("errorMessage", validationErrors);
            return "redirect:/event/" + id + "/edit";
        }

        var currentUserOpt = userService.getCurrentAuthenticatedUser();
        if (currentUserOpt.isPresent()) {
            boolean updated = eventService.updateEventIfAuthorized(id, eventUpdate, currentUserOpt.get(), imageFile,
                    tagNames);
            if (updated) {
                return "redirect:/event/" + id;
            }
        }
        return "redirect:/events";
    }

    // Displays the form to create a new event.
    @GetMapping("/events/new")
    public String newEventForm(Model model) {
        model.addAttribute("allTagsView", tagService.getTagsView(null));
        return "createEvent";
    }

    // Creates a new event with its data and optional image.
    @PostMapping("/event/new")
    public String createEvent(
            Event event,
            org.springframework.validation.BindingResult bindingResult,
            @RequestParam(required = false) List<String> tagNames,
            @RequestParam(required = false) org.springframework.web.multipart.MultipartFile imageFile,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes)
            throws java.io.IOException, java.sql.SQLException {

        String validationErrors = eventService.validateEventData(event, imageFile, true, bindingResult.hasErrors());
        if (validationErrors != null) {
            redirectAttributes.addFlashAttribute("errorMessage", validationErrors);
            return "redirect:/events/new";
        }

        var currentUserOpt = userService.getCurrentAuthenticatedUser();
        if (currentUserOpt.isEmpty()) {
            return "redirect:/login";
        }

        eventService.createEventFromForm(event, currentUserOpt.get(), imageFile, tagNames);

        return "redirect:/events";
    }
}
