import { Container } from "react-bootstrap";
import { useNavigate } from "react-router";
import CourseForm from "~/components/CourseForm";
import { createCourse } from "~/services/courseService";
import type { CourseDTO } from "~/dtos/CourseDTO";

export default function NewCourse() {
  const navigate = useNavigate();

  const handleSubmit = async (data: CourseDTO, imageFile?: File) => {
    try {
      const newCourse = await createCourse(data, [], imageFile);
      navigate(`/new/courses/${newCourse.id}`);
    } catch (error) {
      console.error(error);
      alert("Error al crear el curso: " + (error instanceof Error ? error.message : String(error)));
    }
  };

  return (
    <Container className="py-5">
      <div className="mb-4">
        <h2 className="display-5 text-primary">Crear Nuevo Curso</h2>
        <p className="text-secondary">Rellena los detalles para publicar tu nuevo contenido.</p>
      </div>
      <CourseForm onSubmit={handleSubmit} />
    </Container>
  );
}
