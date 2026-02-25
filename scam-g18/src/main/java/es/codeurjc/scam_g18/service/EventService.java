package es.codeurjc.scam_g18.service;

import es.codeurjc.scam_g18.model.Event;
import es.codeurjc.scam_g18.model.EventSession;
import es.codeurjc.scam_g18.model.Status;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

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

    public String getPriceInEuros(Event event) {
        if (event.getPriceCents() == null)
            return "0.00";
        return String.format("%.2f", event.getPriceCents() / 100.0);
    }

    public Optional<Event> getEventById(long id) {
        return eventRepository.findById(id);
    }

    public List<Map<String, Object>> getEventsViewData(String keyword, List<String> tags) {
        List<Event> allEvents = searchEvents(keyword, tags);
        List<Map<String, Object>> eventsData = new ArrayList<>();

        for (Event event : allEvents) {
            if (event.getStatus() != Status.PUBLISHED) {
                continue;
            }
            eventsData.add(buildEventCardData(event));
        }

        return eventsData;
    }

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
        if (event.getImage() != null) {
            imageUrl = imageService.getConnectionImage(event.getImage());
        }
        eventData.put("image", imageUrl);

        return eventData;
    }

    public void saveEvent(Event event) {
        eventRepository.save(event);
    }

    public void deleteEvent(long id) {
        eventRepository.deleteById(id);
    }

    public boolean canManageEvent(Event event, User user) {
        if (event == null || user == null) {
            return false;
        }

        boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getName().equals("ADMIN"));
        boolean isCreator = event.getCreator() != null && event.getCreator().getId().equals(user.getId());
        return isAdmin || isCreator;
    }

    public boolean hasUserPurchasedEvent(Long userId, Long eventId) {
        if (userId == null || eventId == null) {
            return false;
        }
        return eventRegistrationRepository.existsByUserIdAndEventId(userId, eventId);
    }

    public boolean deleteEventIfAuthorized(long id, User user) {
        var eventOpt = getEventById(id);
        if (eventOpt.isPresent() && canManageEvent(eventOpt.get(), user)) {
            deleteEvent(id);
            return true;
        }
        return false;
    }

    public boolean updateEventIfAuthorized(long id, Event eventUpdate, User user,
            org.springframework.web.multipart.MultipartFile imageFile)
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
        createEvent(event, imageFile);
        return true;
    }

    public void createEventFromForm(Event event, User creator,
            org.springframework.web.multipart.MultipartFile imageFile)
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

        if (event.getLocationName() != null) {
            es.codeurjc.scam_g18.model.Location location = new es.codeurjc.scam_g18.model.Location();
            location.setName(event.getLocationName());
            location.setCity("Madrid");
            location.setCountry("Spain");
            event.setLocation(location);
        }

        event.getSessions().clear();
        if (event.getSessionTimes() != null && event.getSessionTitles() != null) {
            int sessionCount = Math.min(event.getSessionTimes().size(), event.getSessionTitles().size());
            for (int i = 0; i < sessionCount; i++) {
                String desc = (event.getSessionDescriptions() != null && i < event.getSessionDescriptions().size())
                        ? event.getSessionDescriptions().get(i)
                        : "";
                event.getSessions().add(new EventSession(event.getSessionTimes().get(i), event.getSessionTitles().get(i), desc));
            }
        }

        if (event.getSpeakerNames() != null) {
            event.setSpeakers(event.getSpeakerNames());
        }

        createEvent(event, imageFile);
    }

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

        if (source.getLocationName() != null) {
            if (target.getLocation() != null) {
                target.getLocation().setName(source.getLocationName());
            } else {
                es.codeurjc.scam_g18.model.Location location = new es.codeurjc.scam_g18.model.Location();
                location.setName(source.getLocationName());
                location.setCity("Madrid");
                location.setCountry("Spain");
                target.setLocation(location);
            }
        }

        target.getSessions().clear();
        if (source.getSessionTimes() != null && source.getSessionTitles() != null) {
            int sessionCount = Math.min(source.getSessionTimes().size(), source.getSessionTitles().size());
            for (int i = 0; i < sessionCount; i++) {
                String desc = (source.getSessionDescriptions() != null && i < source.getSessionDescriptions().size())
                        ? source.getSessionDescriptions().get(i)
                        : "";
                target.getSessions().add(new EventSession(source.getSessionTimes().get(i), source.getSessionTitles().get(i), desc));
            }
        }

        if (source.getSpeakerNames() != null) {
            target.setSpeakers(source.getSpeakerNames());
        }
    }

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
}
