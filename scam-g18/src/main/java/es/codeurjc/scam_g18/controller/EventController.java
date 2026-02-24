package es.codeurjc.scam_g18.controller;

import es.codeurjc.scam_g18.service.EventService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

import es.codeurjc.scam_g18.service.TagService;
import es.codeurjc.scam_g18.model.Event;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private TagService tagService;

    private final es.codeurjc.scam_g18.service.UserService userService;

    public EventController(EventService eventService, TagService tagService,
            es.codeurjc.scam_g18.service.UserService userService) {
        this.eventService = eventService;
        this.tagService = tagService;
        this.userService = userService;
    }

    @GetMapping("/events")
    public String events(Model model, @RequestParam(required = false) String search,
            @RequestParam(required = false) List<String> tags) {
        model.addAttribute("events", eventService.getEventsViewData(search, tags));
        model.addAttribute("search", search);
        model.addAttribute("tagsView", tagService.getTagsView(tags));

        return "events";
    }

    @GetMapping("/event/{id}")
    public String showEvent(Model model, @PathVariable long id) {
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

        model.addAttribute("canEdit", canManage);
        model.addAttribute("canDelete", canManage);

        return "event";
    }

    @PostMapping("/event/{id}/delete")
    public String deleteEvent(@PathVariable long id) {
        userService.getCurrentAuthenticatedUser()
                .ifPresent(currentUser -> eventService.deleteEventIfAuthorized(id, currentUser));
        return "redirect:/events";
    }

    @GetMapping("/event/{id}/edit")
    public String editEventForm(Model model, @PathVariable long id) {
        var eventOpt = eventService.getEventById(id);
        if (eventOpt.isPresent()) {
            Event event = eventOpt.get();

            var currentUserOpt = userService.getCurrentAuthenticatedUser();
            if (currentUserOpt.isPresent() && eventService.canManageEvent(event, currentUserOpt.get())) {
                model.addAttribute("event", event);
                model.addAttribute("startDateStr", event.getStartDate().toLocalDate().toString());
                model.addAttribute("startTimeStr", event.getStartDate().toLocalTime().toString());
                model.addAttribute("endDateStr", event.getEndDate().toLocalDate().toString());
                model.addAttribute("endTimeStr", event.getEndDate().toLocalTime().toString());
                return "editEvent";
            }
        }
        return "redirect:/events";
    }

    @PostMapping("/event/{id}/edit")
    public String updateEvent(
            @PathVariable long id,
            Event eventUpdate,
            @RequestParam(required = false) org.springframework.web.multipart.MultipartFile imageFile)
            throws java.io.IOException, java.sql.SQLException {

        if (hasInvalidEventData(eventUpdate)) {
            return "redirect:/event/" + id + "/edit";
        }

        var currentUserOpt = userService.getCurrentAuthenticatedUser();
        if (currentUserOpt.isPresent()) {
            boolean updated = eventService.updateEventIfAuthorized(id, eventUpdate, currentUserOpt.get(), imageFile);
            if (updated) {
                return "redirect:/event/" + id;
            }
        }
        return "redirect:/events";
    }

    @GetMapping("/events/new")
    public String newEventForm(Model model) {
        return "createEvent";
    }

    @PostMapping("/event/new")
    public String createEvent(
            Event event,
            @RequestParam(required = false) org.springframework.web.multipart.MultipartFile imageFile)
            throws java.io.IOException, java.sql.SQLException {

        if (hasInvalidEventData(event)) {
            return "redirect:/events/new";
        }

        var currentUserOpt = userService.getCurrentAuthenticatedUser();
        if (currentUserOpt.isEmpty()) {
            return "redirect:/login";
        }

        eventService.createEventFromForm(event, currentUserOpt.get(), imageFile);

        return "redirect:/events";
    }

    private boolean hasInvalidEventData(Event event) {
        if (event == null) {
            return true;
        }
        if (event.getTitle() == null || event.getTitle().isBlank()) {
            return true;
        }
        if (event.getDescription() == null || event.getDescription().isBlank()) {
            return true;
        }
        if (event.getCategory() == null || event.getCategory().isBlank()) {
            return true;
        }
        if (event.getLocationName() == null || event.getLocationName().isBlank()) {
            return true;
        }
        if (event.getPrice() == null || event.getPrice() < 0) {
            return true;
        }
        if (event.getCapacity() == null || event.getCapacity() <= 0) {
            return true;
        }
        if (event.getStartDateStr() == null || event.getStartDateStr().isBlank()) {
            return true;
        }
        if (event.getStartTimeStr() == null || event.getStartTimeStr().isBlank()) {
            return true;
        }
        if (event.getEndDateStr() == null || event.getEndDateStr().isBlank()) {
            return true;
        }
        if (event.getEndTimeStr() == null || event.getEndTimeStr().isBlank()) {
            return true;
        }
        return false;
    }
}
