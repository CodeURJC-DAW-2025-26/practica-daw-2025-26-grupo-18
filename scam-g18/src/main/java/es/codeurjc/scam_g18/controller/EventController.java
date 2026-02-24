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
import es.codeurjc.scam_g18.model.User;
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

        // Permissions for Edit/Delete
        boolean isAdmin = false;
        boolean isCreator = false;

        var currentUserOpt = userService.getCurrentAuthenticatedUser();
        if (currentUserOpt.isPresent()) {
            User currentUser = currentUserOpt.get();
            isAdmin = currentUser.getRoles().stream()
                    .anyMatch(role -> role.getName().equals("ADMIN"));
            isCreator = event.getCreator() != null && event.getCreator().getId().equals(currentUser.getId());
        }

        model.addAttribute("canEdit", isAdmin || isCreator);
        model.addAttribute("canDelete", isAdmin || isCreator);

        return "event";
    }

    @PostMapping("/event/{id}/delete")
    public String deleteEvent(@PathVariable long id) {
        var eventOpt = eventService.getEventById(id);
        if (eventOpt.isPresent()) {
            Event event = eventOpt.get();
            var currentUserOpt = userService.getCurrentAuthenticatedUser();

            if (currentUserOpt.isPresent()) {
                User currentUser = currentUserOpt.get();
                boolean isAdmin = currentUser.getRoles().stream()
                        .anyMatch(role -> role.getName().equals("ADMIN"));
                boolean isCreator = event.getCreator() != null
                        && event.getCreator().getId().equals(currentUser.getId());

                if (isAdmin || isCreator) {
                    eventService.deleteEvent(id);
                }
            }
        }
        return "redirect:/events";
    }

    @GetMapping("/event/{id}/edit")
    public String editEventForm(Model model, @PathVariable long id) {
        var eventOpt = eventService.getEventById(id);
        if (eventOpt.isPresent()) {
            Event event = eventOpt.get();
            var currentUserOpt = userService.getCurrentAuthenticatedUser();

            if (currentUserOpt.isPresent()) {
                User currentUser = currentUserOpt.get();
                boolean isAdmin = currentUser.getRoles().stream()
                        .anyMatch(role -> role.getName().equals("ADMIN"));
                boolean isCreator = event.getCreator() != null
                        && event.getCreator().getId().equals(currentUser.getId());

                if (isAdmin || isCreator) {
                    model.addAttribute("event", event);
                    // We need to format dates for the form
                    model.addAttribute("startDateStr", event.getStartDate().toLocalDate().toString());
                    model.addAttribute("startTimeStr", event.getStartDate().toLocalTime().toString());
                    model.addAttribute("endDateStr", event.getEndDate().toLocalDate().toString());
                    model.addAttribute("endTimeStr", event.getEndDate().toLocalTime().toString());
                    return "editEvent";
                }
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

        var eventOpt = eventService.getEventById(id);
        if (eventOpt.isPresent()) {
            Event event = eventOpt.get();
            var currentUserOpt = userService.getCurrentAuthenticatedUser();

            if (currentUserOpt.isPresent()) {
                User currentUser = currentUserOpt.get();
                boolean isAdmin = currentUser.getRoles().stream()
                        .anyMatch(role -> role.getName().equals("ADMIN"));
                boolean isCreator = event.getCreator() != null
                        && event.getCreator().getId().equals(currentUser.getId());

                if (isAdmin || isCreator) {
                    event.setTitle(eventUpdate.getTitle());
                    event.setDescription(eventUpdate.getDescription());
                    if (eventUpdate.getPrice() != null) {
                        event.setPriceCents((int) (eventUpdate.getPrice() * 100));
                    }
                    event.setCapacity(eventUpdate.getCapacity());
                    event.setCategory(eventUpdate.getCategory());

                    if (eventUpdate.getStartDateStr() != null && eventUpdate.getStartTimeStr() != null) {
                        java.time.LocalDateTime start = java.time.LocalDateTime.of(
                                java.time.LocalDate.parse(eventUpdate.getStartDateStr()),
                                java.time.LocalTime.parse(eventUpdate.getStartTimeStr()));
                        event.setStartDate(start);
                    }

                    if (eventUpdate.getEndDateStr() != null && eventUpdate.getEndTimeStr() != null) {
                        java.time.LocalDateTime end = java.time.LocalDateTime.of(
                                java.time.LocalDate.parse(eventUpdate.getEndDateStr()),
                                java.time.LocalTime.parse(eventUpdate.getEndTimeStr()));
                        event.setEndDate(end);
                    }

                    if (eventUpdate.getLocationName() != null) {
                        if (event.getLocation() != null) {
                            event.getLocation().setName(eventUpdate.getLocationName());
                        } else {
                            es.codeurjc.scam_g18.model.Location location = new es.codeurjc.scam_g18.model.Location();
                            location.setName(eventUpdate.getLocationName());
                            location.setCity("Madrid");
                            location.setCountry("Spain");
                            event.setLocation(location);
                        }
                    }

                    // Sessions
                    event.getSessions().clear();
                    if (eventUpdate.getSessionTimes() != null) {
                        for (int i = 0; i < eventUpdate.getSessionTimes().size(); i++) {
                            String desc = (eventUpdate.getSessionDescriptions() != null
                                    && i < eventUpdate.getSessionDescriptions().size())
                                            ? eventUpdate.getSessionDescriptions().get(i)
                                            : "";
                            event.getSessions().add(
                                    new es.codeurjc.scam_g18.model.EventSession(eventUpdate.getSessionTimes().get(i),
                                            eventUpdate.getSessionTitles().get(i), desc));
                        }
                    }

                    if (eventUpdate.getSpeakerNames() != null) {
                        event.setSpeakers(eventUpdate.getSpeakerNames());
                    }

                    eventService.createEvent(event, imageFile);
                    return "redirect:/event/" + id;
                }
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

        if (event.getPrice() != null) {
            event.setPriceCents((int) (event.getPrice() * 100));
        }
        event.setStatus(es.codeurjc.scam_g18.model.Status.PUBLISHED);

        // Dates
        if (event.getStartDateStr() != null && event.getStartTimeStr() != null) {
            java.time.LocalDateTime start = java.time.LocalDateTime.of(
                    java.time.LocalDate.parse(event.getStartDateStr()),
                    java.time.LocalTime.parse(event.getStartTimeStr()));
            event.setStartDate(start);
        }

        if (event.getEndDateStr() != null && event.getEndTimeStr() != null) {
            java.time.LocalDateTime end = java.time.LocalDateTime.of(
                    java.time.LocalDate.parse(event.getEndDateStr()),
                    java.time.LocalTime.parse(event.getEndTimeStr()));
            event.setEndDate(end);
        }

        // Location
        if (event.getLocationName() != null) {
            es.codeurjc.scam_g18.model.Location location = new es.codeurjc.scam_g18.model.Location();
            location.setName(event.getLocationName());
            location.setCity("Madrid");
            location.setCountry("Spain");
            event.setLocation(location);
        }

        // Creator
        userService.getCurrentAuthenticatedUser().ifPresent(event::setCreator);

        // Sessions
        if (event.getSessionTimes() != null) {
            for (int i = 0; i < event.getSessionTimes().size(); i++) {
                String desc = (event.getSessionDescriptions() != null && i < event.getSessionDescriptions().size())
                        ? event.getSessionDescriptions().get(i)
                        : "";
                event.getSessions().add(
                        new es.codeurjc.scam_g18.model.EventSession(event.getSessionTimes().get(i),
                                event.getSessionTitles().get(i), desc));
            }
        }

        // Speakers
        if (event.getSpeakerNames() != null) {
            event.setSpeakers(event.getSpeakerNames());
        }

        eventService.createEvent(event, imageFile);

        return "redirect:/events";
    }
}
