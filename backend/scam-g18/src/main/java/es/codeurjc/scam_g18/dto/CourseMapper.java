package es.codeurjc.scam_g18.dto;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;

import es.codeurjc.scam_g18.model.Course;
import es.codeurjc.scam_g18.model.Lesson;
import es.codeurjc.scam_g18.model.Module;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface CourseMapper {
    CourseDTO toDTO(Course course);
    Course toDomain(CourseDTO courseDTO);
    List<CourseDTO> toDTOs(Collection<Course> courses);

    ModuleDTO toDTO(Module module);
    Module toDomain(ModuleDTO moduleDTO);

    LessonDTO toDTO(Lesson lesson);
    Lesson toDomain(LessonDTO lessonDTO);
}
