package es.codeurjc.scam_g18.service;

import es.codeurjc.scam_g18.model.Event;
import es.codeurjc.scam_g18.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

        String imageUrl = "/img/descarga.jpg";
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
        eventData.put("speakers", event.getSpeakers());
        eventData.put("sessions", event.getSessions());

        return eventData;
    }
}
