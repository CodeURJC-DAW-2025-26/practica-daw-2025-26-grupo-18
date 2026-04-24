import { Container } from "react-bootstrap";
import { useNavigate, useLoaderData } from "react-router";
import CourseForm from "~/components/CourseForm";
import { getCourseById, updateCourse } from "~/services/courseService";
import type { CourseDTO } from "~/dtos/CourseDTO";
import type { LoaderFunctionArgs } from "react-router";

export async function clientLoader({ params }: LoaderFunctionArgs) {
  const id = Number(params.id);
  const data = await getCourseById(id);
  return { 
    course: {
      ...data.course,
      modules: data.modules
    } as unknown as CourseDTO 
  };
}
clientLoader.hydrate = true;

import { Link } from "react-router";

export default function EditCourse() {
  const { course } = useLoaderData<typeof clientLoader>();
  const navigate = useNavigate();

  const handleSubmit = async (data: any, imageFile?: File) => {
    try {
      await updateCourse(course.id, data, data.tagNames || [], imageFile);
      navigate(`/new/courses/${course.id}`);
    } catch (error) {
      console.error(error);
      alert("Error al actualizar el curso: " + (error instanceof Error ? error.message : String(error)));
    }
  };

  return (
    <main className="main">
      <div className="page-title light-background mb-4">
        <Container>
          <nav className="breadcrumbs mb-2">
            <ol className="list-unstyled d-flex gap-2 m-0 p-0 small">
              <li><Link to="/new">Inicio</Link></li>
              <li><Link to="/new/courses">Cursos</Link></li>
              <li className="current text-muted">Editar Curso</li>
            </ol>
          </nav>
          <h1 className="m-0 h2 fw-bold">Editar Curso</h1>
        </Container>
      </div>

      <section className="section py-4">
        <Container>
          <CourseForm initialData={course} onSubmit={handleSubmit} />
        </Container>
      </section>
    </main>
  );
}
