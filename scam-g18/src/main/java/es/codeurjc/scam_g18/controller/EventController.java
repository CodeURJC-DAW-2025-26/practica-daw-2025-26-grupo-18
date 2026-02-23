package es.codeurjc.scam_g18.controller;

import es.codeurjc.scam_g18.service.EventService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

import es.codeurjc.scam_g18.service.TagService;
import org.springframework.web.bind.annotation.RequestParam;

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
        var eventData = eventService.getEventDetailViewData(id);
        if (eventData == null) {
            return "redirect:/events";
        }

        model.addAttribute("event", eventData);
        return "event";
    }

    @GetMapping("/events/new")
    public String newEventForm(Model model) {
        return "createEvent";
    }

    @org.springframework.web.bind.annotation.PostMapping("/events/new")
    public String createEvent(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String startDateStr,
            @RequestParam String startTimeStr,
            @RequestParam String endDateStr,
            @RequestParam String endTimeStr,
            @RequestParam String locationName,
            @RequestParam Double price,
            @RequestParam Integer capacity,
            @RequestParam String category,
            @RequestParam(required = false) org.springframework.web.multipart.MultipartFile image,
            @RequestParam(required = false) List<String> sessionTimes,
            @RequestParam(required = false) List<String> sessionTitles,
            @RequestParam(required = false) List<String> sessionDescriptions,
            @RequestParam(required = false) List<String> speakerNames)
            throws java.io.IOException, java.sql.SQLException {

        es.codeurjc.scam_g18.model.Event event = new es.codeurjc.scam_g18.model.Event();
        event.setTitle(title);
        event.setDescription(description);
        event.setPriceCents((int) (price * 100));
        event.setCapacity(capacity);
        event.setCategory(category);
        event.setStatus(es.codeurjc.scam_g18.model.EventStatus.PUBLISHED);

        // Dates
        java.time.LocalDateTime start = java.time.LocalDateTime.of(java.time.LocalDate.parse(startDateStr),
                java.time.LocalTime.parse(startTimeStr));
        java.time.LocalDateTime end = java.time.LocalDateTime.of(java.time.LocalDate.parse(endDateStr),
                java.time.LocalTime.parse(endTimeStr));
        event.setStartDate(start);
        event.setEndDate(end);

        // Location
        es.codeurjc.scam_g18.model.Location location = new es.codeurjc.scam_g18.model.Location();
        location.setName(locationName);
        location.setCity("Madrid"); // Default for now, as form only asks for name
        location.setCountry("Spain");
        event.setLocation(location);

        // Creator
        userService.getCurrentAuthenticatedUser().ifPresent(event::setCreator);

        // Sessions
        if (sessionTimes != null) {
            for (int i = 0; i < sessionTimes.size(); i++) {
                String desc = (sessionDescriptions != null && i < sessionDescriptions.size())
                        ? sessionDescriptions.get(i)
                        : "";
                event.getSessions().add(
                        new es.codeurjc.scam_g18.model.EventSession(sessionTimes.get(i), sessionTitles.get(i), desc));
            }
        }

        // Speakers
        if (speakerNames != null) {
            event.setSpeakers(speakerNames);
        }

        eventService.createEvent(event, image);

        return "redirect:/events";
    }
}