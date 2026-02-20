package es.codeurjc.scam_g18.controller;

import es.codeurjc.scam_g18.service.EventService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

import es.codeurjc.scam_g18.service.TagService;
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

        Map<String, Object> eventData = new HashMap<>();
        eventData.put("id", event.getId());
        eventData.put("title", event.getTitle());
        eventData.put("description", event.getDescription());
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
            String loc = event.getLocation().getCity();
            if (event.getLocation().getName() != null && !event.getLocation().getName().isEmpty()) {
                loc += ", " + event.getLocation().getName();
            }
            eventData.put("locationName", loc);
        } else {
            eventData.put("locationName", "Online");
        }

        eventData.put("tags", event.getTags());
        // Agrego category por si acaso es útil, y el status
        eventData.put("category", event.getCategory());

        model.addAttribute("event", eventData);
        return "event";
    }
}
