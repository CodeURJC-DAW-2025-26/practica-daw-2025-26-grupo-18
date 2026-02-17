package es.codeurjc.scam_g18.service;

import es.codeurjc.scam_g18.model.Event;
import es.codeurjc.scam_g18.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public String getPriceInEuros(Event event) {
        if (event.getPriceCents() == null)
            return "0.00";
        return String.format("%.2f", event.getPriceCents() / 100.0);

    public java.util.Optional<Event> getEventById(long id) {
        return eventRepository.findById(id);
    }

    public void saveEvent(Event event) {
        eventRepository.save(event);
    }

    public void deleteEvent(long id) {
        eventRepository.deleteById(id);
    }
}
