import { useState } from "react";
import { Container, Form, InputGroup, Button, Badge, Alert } from "react-bootstrap";
import { Link, useLoaderData, useSearchParams } from "react-router";
import { getCourses } from "~/services/courseService";
import { loadGlobalDataIntoStore } from "~/services/globalService";
import { useAuthStore } from "~/stores/authStore";
import type { LoaderFunctionArgs } from "react-router";

type CoursesLoaderData = {
  initialCourses: Array<Record<string, any>>;
  initialSearch: string;
};

export async function clientLoader({ request }: LoaderFunctionArgs): Promise<CoursesLoaderData> {
  const url = new URL(request.url);
  const search = url.searchParams.get("search") ?? "";

  await loadGlobalDataIntoStore();
  const initialCourses = await getCourses(0, search || undefined);

  return {
    initialCourses,
    initialSearch: search,
  };
}

clientLoader.hydrate = true;

export default function CoursesPage() {
  const PAGE_SIZE = 10;

  const { initialCourses, initialSearch } = useLoaderData<typeof clientLoader>();
  const [searchParams, setSearchParams] = useSearchParams();
  const [courses, setCourses] = useState<Array<Record<string, any>>>(initialCourses);
  const canCreateCourse = useAuthStore((state) => state.user?.canCreateCourse ?? false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(initialCourses.length === PAGE_SIZE);
  const [search, setSearch] = useState(initialSearch || searchParams.get("search") || "");

  const fetchCourses = async (currentPage: number, currentSearch: string, append: boolean) => {
    try {
      const data = await getCourses(currentPage, currentSearch || undefined);
      if (append) {
        setCourses((prev) => [...prev, ...data]);
      } else {
        setCourses(data);
      }
      setHasMore(data.length === PAGE_SIZE);
    } catch {
      setError("Error al cargar los cursos. Inténtalo de nuevo.");
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setPage(0);
    setSearchParams(search ? { search } : {});
    fetchCourses(0, search, false);
  };

  const handleLoadMore = () => {
    const nextPage = page + 1;
    setPage(nextPage);
    fetchCourses(nextPage, search, true);
  };

  return (
    <main className="main">
      {/* Page Title */}
      <div className="page-title light-background mb-4">
        <Container>
          <nav className="breadcrumbs mb-2">
            <ol className="list-unstyled d-flex gap-2 m-0 p-0">
              <li><Link to="/new">Inicio</Link></li>
              <li className="current">Cursos</li>
            </ol>
          </nav>
          <h1 className="m-0">Cursos disponibles</h1>
        </Container>
      </div>

      {/* Catalog */}
      <section className="courses-catalog section py-4">
        <Container data-aos="fade-up">
          {/* Search & controls */}
          <Form onSubmit={handleSearch} className="mb-5">
            <div className="course-controls p-4 bg-white rounded-4 shadow-sm border">
              <div className="d-flex flex-wrap gap-4 align-items-center justify-content-between">
                <div className="flex-grow-1" style={{ maxWidth: "600px" }}>
                  <InputGroup className="shadow-sm border rounded-pill overflow-hidden">
                    <InputGroup.Text className="bg-white border-0 ps-3">
                      <i className="bi bi-search text-muted" />
                    </InputGroup.Text>
                    <Form.Control
                      type="search"
                      placeholder="Buscar por título, descripción o categorías..."
                      className="border-0 shadow-none py-2 px-3"
                      style={{ fontSize: "1rem" }}
                      value={search}
                      onChange={(e) => setSearch(e.target.value)}
                    />
                    <Button
                      variant="primary"
                      type="submit"
                      className="px-4 fw-bold"
                      style={{ background: "var(--accent-color)", borderColor: "var(--accent-color)" }}
                    >
                      Buscar
                    </Button>
                  </InputGroup>
                </div>

                {canCreateCourse && (
                  <Link
                    to="/new/courses/new"
                    className="btn btn-primary btn-lg fw-bold px-4 rounded-pill shadow-sm"
                    style={{ background: "var(--accent-color)", border: "none" }}
                  >
                    <i className="bi bi-plus-circle me-2" />Crear Nuevo Curso
                  </Link>
                )}
              </div>
            </div>
          </Form>

          {/* Content */}
          {error ? (
            <Alert variant="danger" className="rounded-4 shadow-sm border-0">{error}</Alert>
          ) : courses.length === 0 ? (
            <div className="text-center py-5 bg-white rounded-4 shadow-sm border border-dashed">
              <i className="bi bi-journal-x display-1 text-muted opacity-25 mb-3 d-block"></i>
              <h3 className="text-dark fw-bold">No se encontraron cursos</h3>
              <p className="text-muted">Prueba con otros términos de búsqueda.</p>
            </div>
          ) : (
            <>
              <div className="course-list d-flex flex-column gap-4" id="courseList">
                {courses.map((course) => (
                  <article key={course.id} className="course-card-full p-4 bg-white rounded-4 shadow-sm border-0 position-relative overflow-hidden">
                    <div className="course-card-header d-flex justify-content-between align-items-start mb-3">
                      <div>
                        <h3 className="course-card-title h4 fw-bold mb-2">
                          <Link to={`/new/courses/${course.id}`} className="text-dark text-decoration-none">
                            {course.title}
                          </Link>
                        </h3>
                        <div className="course-card-meta d-flex flex-wrap gap-3 align-items-center text-muted small">
                          <span className="meta-item"><i className="bi bi-person-circle me-1 opacity-75"></i> Por <strong>{course.creatorUsername}</strong></span>
                          {course.averageRating > 0 && (
                            <span className="meta-item"><i className="bi bi-star-fill text-warning me-1"></i> {course.averageRating} ({course.reviewsNumber} reseñas)</span>
                          )}
                        </div>
                      </div>
                      <div className="d-flex flex-column align-items-end gap-2">
                        {course.isSubscribed && (
                          <Badge bg="success" className="rounded-pill px-3 py-1 fw-medium">Suscrito</Badge>
                        )}
                        <div className="course-card-price h3 fw-bold m-0" style={{ color: "var(--accent-color)" }}>
                          {course.price === 0 ? "Gratis" : `${course.priceInEuros}€`}
                        </div>
                      </div>
                    </div>

                    <p className="course-card-desc text-muted mb-4 line-clamp-2">
                      {course.shortDescription}
                    </p>

                    <div className="course-card-footer d-flex flex-wrap align-items-center gap-4 text-muted small border-top pt-4 mt-auto">
                      <div className="d-flex align-items-center gap-1">
                        <i className="bi bi-play-circle text-accent" style={{ color: "var(--accent-color)" }}></i>
                        <span>{course.videoHours}h de vídeo</span>
                      </div>
                      <div className="d-flex align-items-center gap-1">
                        <i className="bi bi-journal-text text-accent" style={{ color: "var(--accent-color)" }}></i>
                        <span>{course.learningPoints?.length || 0} aprendizajes</span>
                      </div>
                      <div className="d-flex align-items-center gap-1">
                        <i className="bi bi-translate text-accent" style={{ color: "var(--accent-color)" }}></i>
                        <span>{course.language}</span>
                      </div>
                      <div className="ms-auto">
                        <Link
                          to={`/new/courses/${course.id}`}
                          className="btn btn-outline-primary btn-sm px-4 fw-bold rounded-pill"
                          style={{ borderColor: "var(--accent-color)", color: "var(--accent-color)" }}
                        >
                          Más información
                        </Link>
                      </div>
                    </div>
                  </article>
                ))}
              </div>

              {hasMore && (
                <div className="text-center mt-5">
                  <Button
                    variant="primary"
                    size="lg"
                    onClick={handleLoadMore}
                    className="px-5 fw-bold rounded-pill shadow-sm"
                    style={{ background: "var(--accent-color)", border: "none" }}
                  >
                    Ver más cursos
                  </Button>
                </div>
              )}
            </>
          )}
        </Container>
      </section>
    </main>
  );
}
