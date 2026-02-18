package es.codeurjc.scam_g18.controller;

import es.codeurjc.scam_g18.model.Event;
import es.codeurjc.scam_g18.service.EventService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/events")
    public String events(Model model) {
        List<Event> allEvents = eventService.getAllEvents();
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
        return "events";
    }
}
