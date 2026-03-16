package es.codeurjc.scam_g18.dto;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;

import es.codeurjc.scam_g18.model.Course;

@Mapper(componentModel = "spring")
public interface CourseMapper {
    CourseDTO toDTO(Course course);
    Course toDomain(CourseDTO courseDTO);
    List<CourseDTO> toDTOs(Collection<Course> courses);
}
