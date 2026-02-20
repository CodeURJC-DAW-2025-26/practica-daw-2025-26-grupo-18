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

        model.addAttribute("event", eventData);
        return "event";
    }
}