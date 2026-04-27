import { Container } from "react-bootstrap";
import { useNavigate, useLoaderData } from "react-router";
import CourseForm from "~/components/CourseForm";
import { getCourseById, updateCourse } from "~/services/courseService";
import type { CourseDTO } from "~/dtos/CourseDTO";
import type { LoaderFunctionArgs } from "react-router";

import { loadGlobalDataIntoStore } from "~/services/globalService";
import { redirect } from "react-router";

function normalizeCourseForEdit(data: any): CourseDTO {
  const baseCourse = data?.course ?? data ?? {};
  const modules = data?.modules ?? baseCourse.modules ?? [];

  const normalizedModules = modules.map((module: any) => ({
    ...module,
    description: module.description ?? "",
    lessons: (module.lessons ?? []).map((lesson: any) => ({
      ...lesson,
      description: lesson.description ?? "",
      videoUrl: lesson.videoUrl ?? "",
      orderIndex: lesson.orderIndex ?? 0,
    })),
  }));

  return {
    ...baseCourse,
    modules: normalizedModules,
  } as CourseDTO;
}

export async function clientLoader({ params }: LoaderFunctionArgs) {
  const globalData = await loadGlobalDataIntoStore();
  if (!globalData?.isUserLoggedIn) {
    return redirect("/new/login");
  }
  if (!globalData?.canCreateCourse && !globalData?.isAdmin) {
    return redirect(`/new/error?message=${encodeURIComponent("Necesitas tener el plan de creador para editar cursos.")}`);
  }

  const id = Number(params.id);
  const data = await getCourseById(id);

  if (!data.canEdit && !globalData?.isAdmin) {
    return redirect(`/new/error?message=${encodeURIComponent("No tienes permiso para editar este curso. Solo el creador del curso o un administrador pueden hacerlo.")}`);
  }

  return {
    course: normalizeCourseForEdit(data),
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
