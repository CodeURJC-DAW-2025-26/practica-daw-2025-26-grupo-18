package es.codeurjc.scam_g18.controller;

import es.codeurjc.scam_g18.model.Event;
import es.codeurjc.scam_g18.service.EventService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.codeurjc.scam_g18.service.TagService;
import es.codeurjc.scam_g18.model.Tag;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class EventController {

    private final EventService eventService;
    private final TagService tagService;

    public EventController(EventService eventService, TagService tagService) {
        this.eventService = eventService;
        this.tagService = tagService;
    }

    @GetMapping("/events")
    public String events(Model model, @RequestParam(required = false) String search,
            @RequestParam(required = false) List<String> tags) {
        List<Event> allEvents = eventService.searchEvents(search, tags);
        List<Map<String, Object>> eventsData = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        for (Event event : allEvents) {
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("id", event.getId());
            eventData.put("title", event.getTitle());
            eventData.put("description", event.getDescription());
            eventData.put("priceEuros", eventService.getPriceInEuros(event));

            if (event.getStartDate() != null) {
                eventData.put("formattedDate", event.getStartDate().format(dateFormatter));

                String timeStr = event.getStartDate().format(timeFormatter);
                if (event.getEndDate() != null) {
                    timeStr += " - " + event.getEndDate().format(timeFormatter);
                }
                eventData.put("formattedTime", timeStr);
            } else {
                eventData.put("formattedDate", "Fecha por confirmar");
                eventData.put("formattedTime", "--:--");
            }

            if (event.getLocation() != null) {
                String loc = event.getLocation().getCity();
                if (event.getLocation().getName() != null && !event.getLocation().getName().isEmpty()) {
                    loc += ", " + event.getLocation().getName();
                }
                eventData.put("locationName", loc);
            } else {
                eventData.put("locationName", "Online");
            }

            eventData.put("tags", event.getTags());

            eventsData.add(eventData);
        }

        model.addAttribute("events", eventsData);
        model.addAttribute("search", search);

        // Prepara los tags para la vista
        List<Map<String, Object>> tagsView = new ArrayList<>();
        List<Tag> allTags = tagService.getAllTags();
        for (Tag tag : allTags) {
            Map<String, Object> tagMap = new HashMap<>();
            tagMap.put("name", tag.getName());
            boolean isActive = tags != null && tags.contains(tag.getName());
            tagMap.put("active", isActive);
            tagsView.add(tagMap);
        }
        model.addAttribute("tagsView", tagsView);

        return "events";
    }

    @GetMapping("/event/{id}")
    public String showEvent(Model model, @PathVariable long id) {
        Event event = eventService.getEventById(id).orElse(null);
        if (event == null) {
            return "redirect:/events";
        }

        Map<String, Object> eventData = new HashMap<>();
        eventData.put("id", event.getId());
        eventData.put("title", event.getTitle());
        eventData.put("description", event.getDescription());
        eventData.put("capacity", event.getCapacity());
        eventData.put("priceEuros", eventService.getPriceInEuros(event));

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        if (event.getStartDate() != null) {
            eventData.put("formattedDate", event.getStartDate().format(dateFormatter));
            String timeStr = event.getStartDate().format(timeFormatter);
            if (event.getEndDate() != null) {
                timeStr += " - " + event.getEndDate().format(timeFormatter);
            }
            eventData.put("formattedTime", timeStr);
        } else {
            eventData.put("formattedDate", "Fecha por confirmar");
            eventData.put("formattedTime", "--:--");
        }

        if (event.getLocation() != null) {
            eventData.put("isLocation", true);
            eventData.put("locationName", event.getLocation().getName());
            eventData.put("locationCity", event.getLocation().getCity());
            eventData.put("locationAddress", event.getLocation().getAddress());
            eventData.put("locationCountry", event.getLocation().getCountry());
            eventData.put("locationLat", event.getLocation().getLatitude());
            eventData.put("locationLon", event.getLocation().getLongitude());
        } else {
            eventData.put("isLocation", false);
        }

        eventData.put("tags", event.getTags());
        // Agrego category por si acaso es útil, y el status
        eventData.put("category", event.getCategory());
        model.addAttribute("event", eventData);
        return "event";
    }
}
