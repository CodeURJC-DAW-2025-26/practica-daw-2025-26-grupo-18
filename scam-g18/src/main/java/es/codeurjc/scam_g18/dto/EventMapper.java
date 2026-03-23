package es.codeurjc.scam_g18.dto;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;

import es.codeurjc.scam_g18.model.Event;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface EventMapper {
    EventDTO toDTO(Event event);
    Event toDomain(EventDTO eventDTO);
    List<EventDTO> toDTOs(Collection<Event> events);
}
