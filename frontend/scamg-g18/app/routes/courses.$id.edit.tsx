import { Container } from "react-bootstrap";
import { useNavigate, useLoaderData } from "react-router";
import CourseForm from "~/components/CourseForm";
import { getCourseById, updateCourse } from "~/services/courseService";
import type { CourseDTO } from "~/dtos/CourseDTO";
import type { ClientLoaderArgs } from "react-router";

export async function clientLoader({ params }: ClientLoaderArgs) {
  const id = Number(params.id);
  const course = await getCourseById(id);
  return { course: course as unknown as CourseDTO };
}

export default function EditCourse() {
  const { course } = useLoaderData<typeof clientLoader>();
  const navigate = useNavigate();

  const handleSubmit = async (data: CourseDTO, imageFile?: File) => {
    try {
      await updateCourse(course.id, data, [], imageFile);
      navigate(`/new/courses/${course.id}`);
    } catch (error) {
      console.error(error);
      alert("Error al actualizar el curso: " + (error instanceof Error ? error.message : String(error)));
    }
  };

  return (
    <Container className="py-5">
      <div className="mb-4">
        <h2 className="display-5 text-primary">Editar Curso</h2>
        <p className="text-secondary">Actualiza los detalles de "{course.title}"</p>
      </div>
      <CourseForm initialData={course} onSubmit={handleSubmit} />
    </Container>
  );
}
