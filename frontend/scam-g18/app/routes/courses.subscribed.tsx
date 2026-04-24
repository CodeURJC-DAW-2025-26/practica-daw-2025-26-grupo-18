import { useState, useEffect } from "react";
import { Container, Row, Col, Badge, Spinner, Alert } from "react-bootstrap";
import { Link } from "react-router";
import { getSubscribedCourses } from "~/services/courseService";
import { getGlobalData } from "~/services/globalService";
import type { GlobalDataDTO } from "~/dtos/GlobalDataDTO";

export default function SubscribedCoursesPage() {
  const [courses, setCourses] = useState<Record<string, any>[]>([]);
  const [globalData, setGlobalData] = useState<GlobalDataDTO | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchCourses = async () => {
    try {
      const data = await getSubscribedCourses();
      setCourses(data);
    } catch {
      setError("Error al cargar los cursos suscritos. Inténtalo de nuevo.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    getGlobalData().then(setGlobalData).catch(() => { });
    fetchCourses();
  }, []);

  return (
    <main className="main">
      {/* Page Title */}
      <div className="page-title light-background mb-4">
        <Container>
          <nav className="breadcrumbs mb-2">
            <ol className="list-unstyled d-flex gap-2 m-0 p-0">
              <li><Link to="/new">Inicio</Link></li>
              <li className="current">Cursos suscritos</li>
            </ol>
          </nav>
          <h1 className="m-0">Mis cursos suscritos</h1>
        </Container>
      </div>

      {/* Catalog */}
      <section className="courses-catalog section py-4">
        <Container data-aos="fade-up">
          {/* Content */}
          {loading ? (
            <div className="text-center py-5">
              <div className="spinner-border text-primary" role="status" style={{ color: "var(--accent-color)" }}>
                <span className="visually-hidden">Cargando...</span>
              </div>
            </div>
          ) : error ? (
            <Alert variant="danger" className="rounded-4 shadow-sm border-0">{error}</Alert>
          ) : courses.length === 0 ? (
            <div className="text-center py-5 bg-white rounded-4 shadow-sm border border-dashed">
              <i className="bi bi-journal-x display-1 text-muted opacity-25 mb-3 d-block"></i>
              <h3 className="text-dark fw-bold">No tienes cursos suscritos</h3>
              <p className="text-muted">Explora nuestro catálogo para encontrar el curso perfecto para ti.</p>
              <Link to="/new/courses" className="btn btn-primary mt-3 rounded-pill px-4">Explorar Cursos</Link>
            </div>
          ) : (
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
                      <Badge bg="success" className="rounded-pill px-3 py-1 fw-medium">Suscrito</Badge>
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
                        Continuar aprendiendo
                      </Link>
                    </div>
                  </div>
                </article>
              ))}
            </div>
          )}
        </Container>
      </section>
    </main>
  );
}
