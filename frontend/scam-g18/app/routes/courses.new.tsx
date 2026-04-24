import { Container } from "react-bootstrap";
import { useNavigate } from "react-router";
import CourseForm from "~/components/CourseForm";
import { createCourse } from "~/services/courseService";
import type { CourseDTO } from "~/dtos/CourseDTO";

import { Link } from "react-router";

export default function NewCourse() {
  const navigate = useNavigate();

  const handleSubmit = async (data: any, imageFile?: File) => {
    try {
      const newCourse = await createCourse(data, data.tagNames || [], imageFile);
      navigate(`/new/courses/${newCourse.id}`);
    } catch (error) {
      console.error(error);
      alert("Error al crear el curso: " + (error instanceof Error ? error.message : String(error)));
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
              <li className="current text-muted">Crear Curso</li>
            </ol>
          </nav>
          <h1 className="m-0 h2 fw-bold">Crear Nuevo Curso</h1>
        </Container>
      </div>

      <section className="section py-4">
        <Container>
          <CourseForm onSubmit={handleSubmit} />
        </Container>
      </section>
    </main>
  );
}
