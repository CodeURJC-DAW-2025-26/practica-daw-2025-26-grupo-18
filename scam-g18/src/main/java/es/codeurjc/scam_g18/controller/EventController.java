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

    // Construye el controlador con los servicios necesarios para gestionar eventos.
    public EventController(EventService eventService, TagService tagService,
            es.codeurjc.scam_g18.service.UserService userService) {
        this.eventService = eventService;
        this.tagService = tagService;
        this.userService = userService;
    }

    // Muestra el listado de eventos con búsqueda y filtros por etiquetas.
    @GetMapping("/events")
    public String events(Model model, @RequestParam(required = false) String search,
            @RequestParam(required = false) List<String> tags) {
        Long currentUserId = userService.getCurrentAuthenticatedUser().map(user -> user.getId()).orElse(null);
        model.addAttribute("events", eventService.getEventsViewData(search, tags, currentUserId));
        model.addAttribute("search", search);
        model.addAttribute("tagsView", tagService.getTagsView(tags));

        return "events";
    }

    // Muestra los eventos comprados por el usuario autenticado.
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

    // Muestra el detalle de un evento y permisos de gestión/compra del usuario
    // actual.
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

    // Elimina un evento cuando el usuario actual está autorizado.
    @PostMapping("/event/{id}/delete")
    public String deleteEvent(@PathVariable long id) {
        userService.getCurrentAuthenticatedUser()
                .ifPresent(currentUser -> eventService.deleteEventIfAuthorized(id, currentUser));
        return "redirect:/events";
    }

    // Muestra el formulario de edición de un evento si el usuario puede
    // gestionarlo.
    @GetMapping("/event/{id}/edit")
    public String editEventForm(Model model, @PathVariable long id) {
        var eventOpt = eventService.getEventById(id);
        if (eventOpt.isPresent()) {
            Event event = eventOpt.get();

            var currentUserOpt = userService.getCurrentAuthenticatedUser();
            if (currentUserOpt.isPresent() && eventService.canManageEvent(event, currentUserOpt.get())) {
                model.addAttribute("event", event);
                model.addAttribute("startDateStr",
                        event.getStartDate() != null ? event.getStartDate().toLocalDate().toString() : "");
                model.addAttribute("startTimeStr",
                        event.getStartDate() != null ? event.getStartDate().toLocalTime().toString() : "");
                model.addAttribute("endDateStr",
                        event.getEndDate() != null ? event.getEndDate().toLocalDate().toString() : "");
                model.addAttribute("endTimeStr",
                        event.getEndDate() != null ? event.getEndDate().toLocalTime().toString() : "");
                model.addAttribute("locationName",
                        event.getLocation() != null && event.getLocation().getName() != null
                                ? event.getLocation().getName()
                                : "");
                model.addAttribute("priceValue",
                        event.getPriceCents() != null
                                ? String.format(java.util.Locale.US, "%.2f", event.getPriceCents() / 100.0)
                                : "0.00");
                model.addAttribute("isConferencia", "Conferencia".equalsIgnoreCase(event.getCategory()));
                model.addAttribute("isWebinar", "Webinar".equalsIgnoreCase(event.getCategory()));
                model.addAttribute("isTaller", "Taller".equalsIgnoreCase(event.getCategory()));
                model.addAttribute("isNetworking", "Networking".equalsIgnoreCase(event.getCategory()));
                return "editEvent";
            }
        }
        return "redirect:/events";
    }

    // Actualiza un evento existente cuando los datos son válidos y hay permisos.
    @PostMapping("/event/{id}/edit")
    public String updateEvent(
            @PathVariable long id,
            Event eventUpdate,
            org.springframework.validation.BindingResult bindingResult,
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
            boolean updated = eventService.updateEventIfAuthorized(id, eventUpdate, currentUserOpt.get(), imageFile);
            if (updated) {
                return "redirect:/event/" + id;
            }
        }
        return "redirect:/events";
    }

    // Muestra el formulario para crear un nuevo evento.
    @GetMapping("/events/new")
    public String newEventForm(Model model) {
        return "createEvent";
    }

    // Crea un nuevo evento con sus datos e imagen opcional.
    @PostMapping("/event/new")
    public String createEvent(
            Event event,
            org.springframework.validation.BindingResult bindingResult,
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

        eventService.createEventFromForm(event, currentUserOpt.get(), imageFile);

        return "redirect:/events";
    }
}
