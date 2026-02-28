package es.codeurjc.scam_g18.service;

import es.codeurjc.scam_g18.model.Event;
import es.codeurjc.scam_g18.model.EventRegistration;
import es.codeurjc.scam_g18.model.EventSession;
import es.codeurjc.scam_g18.model.Status;
import es.codeurjc.scam_g18.model.Tag;
import es.codeurjc.scam_g18.model.User;
import es.codeurjc.scam_g18.repository.EventRepository;
import es.codeurjc.scam_g18.repository.EventRegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ImageService imageService;

    @Autowired
    private es.codeurjc.scam_g18.repository.LocationRepository locationRepository;

    @Autowired
    private EventRegistrationRepository eventRegistrationRepository;

    @Autowired
    private es.codeurjc.scam_g18.repository.TagRepository tagRepository;

    @Autowired
    private TagService tagService;

    // Obtiene todos los eventos.
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    // Busca eventos por palabra clave y etiquetas.
    public List<Event> searchEvents(String keyword, List<String> tags) {
        if ((keyword == null || keyword.trim().isEmpty()) && (tags == null || tags.isEmpty())) {
            return getAllEvents();
        }
        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        }
        if (tags != null && tags.isEmpty()) {
            tags = null;
        }
        return eventRepository.findByKeywordAndTags(keyword, tags);
    }

    // Formatea el precio de un evento en euros.
    public String getPriceInEuros(Event event) {
        if (event.getPriceCents() == null)
            return "0.00";
        return String.format("%.2f", event.getPriceCents() / 100.0);
    }

    // Busca un evento por id.
    public Optional<Event> getEventById(long id) {
        return eventRepository.findById(id);
    }

    // Construye los datos de listado de eventos publicados para la vista.
    public List<Map<String, Object>> getEventsViewData(String keyword, List<String> tags, Long userId) {
        List<Event> allEvents = searchEvents(keyword, tags);
        List<Event> publishedEvents = new ArrayList<>();

        for (Event event : allEvents) {
            if (event.getStatus() == Status.PUBLISHED) {
                publishedEvents.add(event);
            }
        }

        Set<String> subscribedTagNames = getSubscribedEventTagNames(userId);
        if (!subscribedTagNames.isEmpty()) {
            publishedEvents.sort(
                    Comparator.comparingInt((Event event) -> countMatchingTags(event.getTags(), subscribedTagNames))
                            .reversed()
                            .thenComparing(Event::getTitle, String.CASE_INSENSITIVE_ORDER));
        }

        List<Map<String, Object>> eventsData = new ArrayList<>();

        for (Event event : publishedEvents) {
            Map<String, Object> eventData = buildEventCardData(event);
            boolean isSubscribed = userId != null
                    && eventRegistrationRepository.existsByUserIdAndEventId(userId, event.getId());
            eventData.put("isSubscribed", isSubscribed);
            eventsData.add(eventData);
        }

        return eventsData;
    }

    // Construye los datos de eventos comprados por un usuario.
    public List<Map<String, Object>> getPurchasedEventsViewData(Long userId) {
        List<EventRegistration> registrations = eventRegistrationRepository.findByUserId(userId);
        List<Map<String, Object>> purchasedEvents = new ArrayList<>();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        for (EventRegistration registration : registrations) {
            Event event = registration.getEvent();
            if (event == null) {
                continue;
            }

            Map<String, Object> eventData = new HashMap<>();
            eventData.put("id", event.getId());
            eventData.put("title", event.getTitle());
            eventData.put("description", event.getDescription());

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

            if (event.getLocation() != null && event.getLocation().getName() != null
                    && !event.getLocation().getName().isBlank()) {
                eventData.put("locationName", event.getLocation().getName());
            } else {
                eventData.put("locationName", "Online");
            }

            purchasedEvents.add(eventData);
        }

        return purchasedEvents;
    }

    // Construye el detalle de un evento para la vista.
    public Map<String, Object> getEventDetailViewData(long id) {
        Optional<Event> eventOpt = getEventById(id);
        if (eventOpt.isEmpty()) {
            return null;
        }

        Event event = eventOpt.get();
        Map<String, Object> eventData = buildEventCardData(event);
        eventData.put("capacity", event.getCapacity());
        eventData.put("attendeesCount", event.getAttendeesCount());
        eventData.put("remainingSeats", event.getRemainingSeats());
        eventData.put("soldOut", !event.hasAvailableSeats());

        String imageUrl = "/img/default_img.png";
        if (event.getId() != null) {
            imageUrl = "/images/events/" + event.getId();
        }
        eventData.put("image", imageUrl);

        return eventData;
    }

    // Guarda un evento.
    public void saveEvent(Event event) {
        eventRepository.save(event);
    }

    // Elimina un evento por id.
    public void deleteEvent(long id) {
        eventRepository.deleteById(id);
    }

    // Comprueba si un usuario puede gestionar un evento.
    public boolean canManageEvent(Event event, User user) {
        if (event == null || user == null) {
            return false;
        }

        boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getName().equals("ADMIN"));
        boolean isCreator = event.getCreator() != null && event.getCreator().getId().equals(user.getId());
        return isAdmin || isCreator;
    }

    // Comprueba si un usuario ya compró un evento.
    public boolean hasUserPurchasedEvent(Long userId, Long eventId) {
        if (userId == null || eventId == null) {
            return false;
        }
        return eventRegistrationRepository.existsByUserIdAndEventId(userId, eventId);
    }

    // Elimina un evento solo si el usuario está autorizado.
    public boolean deleteEventIfAuthorized(long id, User user) {
        var eventOpt = getEventById(id);
        if (eventOpt.isPresent() && canManageEvent(eventOpt.get(), user)) {
            deleteEvent(id);
            return true;
        }
        return false;
    }

    // Actualiza un evento solo si el usuario está autorizado.
    public boolean updateEventIfAuthorized(long id, Event eventUpdate, User user,
            org.springframework.web.multipart.MultipartFile imageFile, List<String> tagNames)
            throws java.io.IOException, java.sql.SQLException {
        var eventOpt = getEventById(id);
        if (eventOpt.isEmpty()) {
            return false;
        }

        Event event = eventOpt.get();
        if (!canManageEvent(event, user)) {
            return false;
        }

        applyEventFormData(event, eventUpdate);

        event.getTags().clear();
        if (tagNames != null && !tagNames.isEmpty()) {
            for (String tagName : tagNames) {
                Tag tag = tagRepository.findByName(tagName).orElseGet(() -> tagRepository.save(new Tag(tagName)));
                event.getTags().add(tag);
            }
        }

        createEvent(event, imageFile);
        return true;
    }

    // Crea un evento desde datos de formulario.
    public void createEventFromForm(Event event, User creator,
            org.springframework.web.multipart.MultipartFile imageFile, List<String> tagNames)
            throws java.io.IOException, java.sql.SQLException {
        if (event.getPrice() != null) {
            event.setPriceCents((int) (event.getPrice() * 100));
        }
        event.setStatus(Status.PENDING_REVIEW);
        event.setCreator(creator);
        event.setAttendeesCount(0);

        if (event.getStartDateStr() != null && event.getStartTimeStr() != null) {
            LocalDateTime start = LocalDateTime.of(
                    LocalDate.parse(event.getStartDateStr()),
                    LocalTime.parse(event.getStartTimeStr()));
            event.setStartDate(start);
        }

        if (event.getEndDateStr() != null && event.getEndTimeStr() != null) {
            LocalDateTime end = LocalDateTime.of(
                    LocalDate.parse(event.getEndDateStr()),
                    LocalTime.parse(event.getEndTimeStr()));
            event.setEndDate(end);
        }

        if (event.getLocationName() != null && !event.getLocationName().isBlank()) {
            es.codeurjc.scam_g18.model.Location location = new es.codeurjc.scam_g18.model.Location();
            location.setName(event.getLocationName());
            location.setAddress(event.getLocationAddress());
            location.setCity(event.getLocationCity() != null ? event.getLocationCity() : "Madrid");
            location.setCountry(event.getLocationCountry() != null ? event.getLocationCountry() : "Spain");
            location.setLatitude(event.getLocationLatitude());
            location.setLongitude(event.getLocationLongitude());
            event.setLocation(location);
        }

        event.getSessions().clear();
        if (event.getSessionTimes() != null && event.getSessionTitles() != null) {
            int sessionCount = Math.min(event.getSessionTimes().size(), event.getSessionTitles().size());
            for (int i = 0; i < sessionCount; i++) {
                String desc = (event.getSessionDescriptions() != null && i < event.getSessionDescriptions().size())
                        ? event.getSessionDescriptions().get(i)
                        : "";
                event.getSessions()
                        .add(new EventSession(event.getSessionTimes().get(i), event.getSessionTitles().get(i), desc));
            }
        }

        if (event.getSpeakerNames() != null) {
            event.setSpeakers(event.getSpeakerNames());
        }

        event.getTags().clear();
        if (tagNames != null && !tagNames.isEmpty()) {
            for (String tagName : tagNames) {
                Tag tag = tagRepository.findByName(tagName).orElseGet(() -> tagRepository.save(new Tag(tagName)));
                event.getTags().add(tag);
            }
        }

        createEvent(event, imageFile);
    }

    // Persiste un evento y su imagen/ubicación.
    public void createEvent(Event event, org.springframework.web.multipart.MultipartFile imageFile)
            throws java.io.IOException, java.sql.SQLException {
        if (imageFile != null && !imageFile.isEmpty()) {
            event.setImage(imageService.saveImage(imageFile));
        }

        if (event.getLocation() != null && event.getLocation().getId() == null) {
            locationRepository.save(event.getLocation());
        }

        eventRepository.save(event);
    }

    // Aplica los datos editables del formulario sobre un evento existente.
    private void applyEventFormData(Event target, Event source) {
        target.setTitle(source.getTitle());
        target.setDescription(source.getDescription());

        if (source.getPrice() != null) {
            target.setPriceCents((int) (source.getPrice() * 100));
        }

        target.setCapacity(source.getCapacity());
        target.setCategory(source.getCategory());

        if (source.getStartDateStr() != null && source.getStartTimeStr() != null) {
            LocalDateTime start = LocalDateTime.of(
                    LocalDate.parse(source.getStartDateStr()),
                    LocalTime.parse(source.getStartTimeStr()));
            target.setStartDate(start);
        }

        if (source.getEndDateStr() != null && source.getEndTimeStr() != null) {
            LocalDateTime end = LocalDateTime.of(
                    LocalDate.parse(source.getEndDateStr()),
                    LocalTime.parse(source.getEndTimeStr()));
            target.setEndDate(end);
        }

        if (source.getLocationName() != null && !source.getLocationName().isBlank()) {
            if (target.getLocation() != null) {
                target.getLocation().setName(source.getLocationName());
                target.getLocation().setAddress(source.getLocationAddress());
                target.getLocation().setCity(source.getLocationCity());
                target.getLocation().setCountry(source.getLocationCountry());
                target.getLocation().setLatitude(source.getLocationLatitude());
                target.getLocation().setLongitude(source.getLocationLongitude());
            } else {
                es.codeurjc.scam_g18.model.Location location = new es.codeurjc.scam_g18.model.Location();
                location.setName(source.getLocationName());
                location.setAddress(source.getLocationAddress());
                location.setCity(source.getLocationCity() != null ? source.getLocationCity() : "Madrid");
                location.setCountry(source.getLocationCountry() != null ? source.getLocationCountry() : "Spain");
                location.setLatitude(source.getLocationLatitude());
                location.setLongitude(source.getLocationLongitude());
                target.setLocation(location);
            }
        } else if (source.getLocationName() != null && source.getLocationName().isBlank()) {
            // If location name is provided as empty string, it might mean it's online
            target.setLocation(null);
        }

        target.getSessions().clear();
        if (source.getSessionTimes() != null && source.getSessionTitles() != null) {
            int sessionCount = Math.min(source.getSessionTimes().size(), source.getSessionTitles().size());
            for (int i = 0; i < sessionCount; i++) {
                String desc = (source.getSessionDescriptions() != null && i < source.getSessionDescriptions().size())
                        ? source.getSessionDescriptions().get(i)
                        : "";
                target.getSessions()
                        .add(new EventSession(source.getSessionTimes().get(i), source.getSessionTitles().get(i), desc));
            }
        }

        if (source.getSpeakerNames() != null) {
            target.setSpeakers(source.getSpeakerNames());
        }
    }

    public Map<String, Object> getEventDetailViewData(long eventId) {
        var eventOpt = getEventById(eventId);
        if (eventOpt.isEmpty()) {
            return null;
        }

        Event ev = eventOpt.get();
        Map<String, Object> data = new HashMap<>();
        data.put("id", ev.getId());
        data.put("title", ev.getTitle());
        data.put("description", ev.getDescription());
        data.put("priceInEuros", getPriceInEuros(ev));
        data.put("tags", ev.getTags());

        data.put("capacity", ev.getCapacity() == null ? "Ilimitada" : ev.getCapacity());
        data.put("attendeesCount", ev.getAttendeesCount());

        if (ev.getCapacity() != null) {
            data.put("remainingSeats", Math.max(0, ev.getCapacity() - ev.getAttendeesCount()));
            data.put("isFull", ev.getAttendeesCount() >= ev.getCapacity());
        }

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        if (ev.getStartDate() != null) {
            data.put("startDateFormatted", ev.getStartDate().format(dateFormatter));
            data.put("startTimeFormatted", ev.getStartDate().format(timeFormatter));
        }

        if (ev.getEndDate() != null) {
            data.put("endDateFormatted", ev.getEndDate().format(dateFormatter));
            data.put("endTimeFormatted", ev.getEndDate().format(timeFormatter));
            data.put("durationDays", java.time.temporal.ChronoUnit.DAYS.between(ev.getStartDate().toLocalDate(),
                    ev.getEndDate().toLocalDate()) + 1);
        }

        if (ev.getLocation() != null) {
            data.put("locationName", ev.getLocation().getName());
            data.put("locationAddress", ev.getLocation().getAddress());
            data.put("locationCity", ev.getLocation().getCity());
            data.put("locationCountry", ev.getLocation().getCountry());
            data.put("locationLatitude", ev.getLocation().getLatitude());
            data.put("locationLongitude", ev.getLocation().getLongitude());
        }

        data.put("creatorUsername", ev.getCreator() != null ? ev.getCreator().getUsername() : "Desconocido");
        data.put("category", ev.getCategory() == null ? "Sin Categoría" : ev.getCategory());
        data.put("sessions", ev.getSessions() == null ? new ArrayList<>() : ev.getSessions());

        List<Map<String, Object>> speakersList = new ArrayList<>();
        if (ev.getSpeakers() != null) {
            for (String speaker : ev.getSpeakers()) {
                Map<String, Object> s = new HashMap<>();
                s.put("name", speaker);
                s.put("role", "Ponente");
                speakersList.add(s);
            }
        }
        data.put("speakers", speakersList);

        return data;
    }

    // Prepara los datos necesarios para la vista de edición de evento.
    public Map<String, Object> getEventEditViewData(Event event) {
        Map<String, Object> data = new HashMap<>();
        data.put("event", event);
        data.put("startDateStr", event.getStartDate() != null ? event.getStartDate().toLocalDate().toString() : "");
        data.put("startTimeStr", event.getStartDate() != null ? event.getStartDate().toLocalTime().toString() : "");
        data.put("endDateStr", event.getEndDate() != null ? event.getEndDate().toLocalDate().toString() : "");
        data.put("endTimeStr", event.getEndDate() != null ? event.getEndDate().toLocalTime().toString() : "");
        data.put("locationName",
                event.getLocation() != null && event.getLocation().getName() != null ? event.getLocation().getName()
                        : "");
        data.put("priceValue",
                event.getPriceCents() != null
                        ? String.format(java.util.Locale.US, "%.2f", event.getPriceCents() / 100.0)
                        : "0.00");
        data.put("isConferencia", "Conferencia".equalsIgnoreCase(event.getCategory()));
        data.put("isWebinar", "Webinar".equalsIgnoreCase(event.getCategory()));
        data.put("isTaller", "Taller".equalsIgnoreCase(event.getCategory()));
        data.put("isNetworking", "Networking".equalsIgnoreCase(event.getCategory()));
        data.put("locationAddress", event.getLocation() != null ? event.getLocation().getAddress() : "");
        data.put("locationCity", event.getLocation() != null ? event.getLocation().getCity() : "");
        data.put("locationCountry", event.getLocation() != null ? event.getLocation().getCountry() : "");
        data.put("locationLat", event.getLocation() != null ? event.getLocation().getLatitude() : "");
        data.put("locationLon", event.getLocation() != null ? event.getLocation().getLongitude() : "");

        if (event.getLocation() != null) {
            event.setLocationLatitude(event.getLocation().getLatitude());
            event.setLocationLongitude(event.getLocation().getLongitude());
        }

        List<String> selectedTags = event.getTags().stream().map(es.codeurjc.scam_g18.model.Tag::getName).toList();
        data.put("allTagsView", tagService.getTagsView(selectedTags));

        return data;
    }

    // Construye el mapa de datos reutilizable para tarjetas y detalle de evento.
    private Map<String, Object> buildEventCardData(Event event) {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("id", event.getId());
        eventData.put("title", event.getTitle());
        eventData.put("description", event.getDescription());
        eventData.put("priceEuros", getPriceInEuros(event));

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
            Double latitude = event.getLocation().getLatitude();
            Double longitude = event.getLocation().getLongitude();
            eventData.put("isLocation", true);
            eventData.put("locationName", event.getLocation().getName() != null ? event.getLocation().getName() : "");
            eventData.put("locationCity", event.getLocation().getCity() != null ? event.getLocation().getCity() : "");
            eventData.put("locationAddress",
                    event.getLocation().getAddress() != null ? event.getLocation().getAddress() : "");
            eventData.put("locationCountry",
                    event.getLocation().getCountry() != null ? event.getLocation().getCountry() : "");
            eventData.put("locationLat", latitude);
            eventData.put("locationLon", longitude);
            eventData.put("hasCoordinates", latitude != null && longitude != null);
        } else {
            eventData.put("isLocation", false);
            eventData.put("hasCoordinates", false);
            eventData.put("locationName", "Online");
            eventData.put("locationCity", "");
            eventData.put("locationAddress", "");
            eventData.put("locationCountry", "");
        }

        eventData.put("tags", event.getTags());
        eventData.put("speakers", event.getSpeakers());
        eventData.put("sessions", event.getSessions());

        return eventData;
    }

    // Obtiene etiquetas de eventos previamente comprados por el usuario.
    private Set<String> getSubscribedEventTagNames(Long userId) {
        Set<String> subscribedTagNames = new HashSet<>();
        if (userId == null) {
            return subscribedTagNames;
        }

        List<EventRegistration> registrations = eventRegistrationRepository.findByUserId(userId);
        for (EventRegistration registration : registrations) {
            Event subscribedEvent = registration.getEvent();
            if (subscribedEvent == null || subscribedEvent.getTags() == null) {
                continue;
            }

            for (Tag tag : subscribedEvent.getTags()) {
                if (tag != null && tag.getName() != null && !tag.getName().isBlank()) {
                    subscribedTagNames.add(tag.getName().trim().toLowerCase());
                }
            }
        }

        return subscribedTagNames;
    }

    // Cuenta coincidencias de etiquetas entre candidato y preferencias del usuario.
    private int countMatchingTags(Set<Tag> candidateTags, Set<String> subscribedTagNames) {
        if (candidateTags == null || candidateTags.isEmpty() || subscribedTagNames.isEmpty()) {
            return 0;
        }

        int matches = 0;
        for (Tag tag : candidateTags) {
            if (tag == null || tag.getName() == null) {
                continue;
            }

            String normalizedName = tag.getName().trim().toLowerCase();
            if (!normalizedName.isBlank() && subscribedTagNames.contains(normalizedName)) {
                matches++;
            }
        }

        return matches;
    }

    public String validateEventData(Event event, org.springframework.web.multipart.MultipartFile imageFile,
            boolean isNew, boolean hasBindingErrors) {
        List<String> errors = new ArrayList<>();

        if (hasBindingErrors) {
            errors.add("Error: Por favor, verifique que todos los campos numéricos tengan valores correctos.");
        }

        if (event == null)
            return "Error: Datos del evento nulos.";

        if (event.getTitle() == null || event.getTitle().isBlank())
            errors.add("El título es obligatorio.");
        if (event.getDescription() == null || event.getDescription().isBlank())
            errors.add("La descripción detallada es obligatoria.");
        if (event.getCategory() == null || event.getCategory().isBlank())
            errors.add("El tipo de evento es obligatorio.");
        if (event.getLocationName() == null || event.getLocationName().isBlank())
            errors.add("La ubicación es obligatoria.");
        if (event.getPrice() == null || event.getPrice() < 0)
            errors.add("El precio no puede ser negativo.");
        if (event.getCapacity() == null || event.getCapacity() <= 0)
            errors.add("La capacidad debe ser mayor a 0.");

        if (event.getStartDateStr() == null || event.getStartDateStr().isBlank() ||
                event.getStartTimeStr() == null || event.getStartTimeStr().isBlank() ||
                event.getEndDateStr() == null || event.getEndDateStr().isBlank() ||
                event.getEndTimeStr() == null || event.getEndTimeStr().isBlank()) {
            errors.add("Las fechas y horas de inicio y fin son obligatorias.");
        } else {
            try {
                LocalDateTime start = LocalDateTime.of(LocalDate.parse(event.getStartDateStr()),
                        LocalTime.parse(event.getStartTimeStr()));
                LocalDateTime end = LocalDateTime.of(LocalDate.parse(event.getEndDateStr()),
                        LocalTime.parse(event.getEndTimeStr()));
                if (end.isBefore(start)) {
                    errors.add("La fecha y hora de fin no puede ser anterior a la de inicio.");
                }
            } catch (Exception e) {
                errors.add("Formato de fecha u hora no válido.");
            }
        }

        if (isNew && (imageFile == null || imageFile.isEmpty())) {
            errors.add("Debe proporcionar una imagen para el evento.");
        }

        if (errors.isEmpty())
            return null;
        return String.join("<br>", errors);
    }
}
