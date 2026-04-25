import { Container, Row, Col, Badge, Accordion, Button, ProgressBar, ListGroup, Card } from "react-bootstrap";
import { useLoaderData, useNavigate, Link } from "react-router";
import { getCourseById } from "~/services/courseService";
import { addCourseToCart } from "~/services/cartService";
import { getCourseImageUrl, getUserProfileImageUrl } from "~/utils/imageUrls";
import { useGlobalStore } from "~/stores/globalStore";
import type { LoaderFunctionArgs } from "react-router";
import type { CourseDetailDTO, ModuleDTO, LessonDTO } from "~/dtos/CourseDTO";
import { GET_COURSE_AGES, GET_COURSE_GENDERS, GET_COURSE_TAGS } from "../constants/constants";
import Chart from "../components/Chart";
import { useEffect, useState } from "react";

export async function clientLoader({ params }: LoaderFunctionArgs) {
  const id = Number(params.id);
  const data = await getCourseById(id);
  return data as CourseDetailDTO;
}
clientLoader.hydrate = true;

export default function CourseDetail() {
  const data = useLoaderData<typeof clientLoader>();
  const navigate = useNavigate();

  if (!data || !data.course) {
    return <Container className="py-5 text-center"><h3>Cargando curso...</h3></Container>;
  }

  const {
    course,
    modules,
    reviews,
    isSuscribedToCourse: isSubscribed,
    canEdit: isOwner,
    courseProgressPercentage,
    averageRating,
    ratingCount,
    reviewsNumber,
    averageRatingStars,
    hasSubscribers,
    priceInEuros
  } = data;

  const handleSubscribe = async () => {
    try {
      await addCourseToCart(course.id);
      navigate("/new/cart");
    } catch (error) {
      alert("Error al suscribirse: " + (error instanceof Error ? error.message : String(error)));
    }
  };
  const [currentSlide, setCurrentSlide] = useState(0);
  const globalData = useGlobalStore((state) => state.globalData);
  const isUserLoggedIn = globalData?.isUserLoggedIn ?? false;
  const userId = isUserLoggedIn ? (globalData?.userId ?? null) : null;

  const slides = [
    { info: GET_COURSE_AGES },
    { info: GET_COURSE_GENDERS },
    ...(isUserLoggedIn ? [{ info: GET_COURSE_TAGS }] : [])
  ];

  // Si el usuario se desloguea estando en la slide de tags (índice 2), volver al inicio
  useEffect(() => {
    if (currentSlide >= slides.length) {
      setCurrentSlide(0);
    }
  }, [isUserLoggedIn, slides.length, currentSlide]);

  const nextSlide = () => setCurrentSlide((prev) => (prev + 1) % slides.length);

  const prevSlide = () => setCurrentSlide((prev) => (prev - 1 + slides.length) % slides.length);
  return (
    <main className="main">
      <div className="container mt-3 pt-3">
        <nav aria-label="breadcrumb">
          <ol className="breadcrumb mt-2">
            <li className="breadcrumb-item"><Link to="/new">Inicio</Link></li>
            <li className="breadcrumb-item"><Link to="/new/courses">Cursos</Link></li>
            <li className="breadcrumb-item active" aria-current="page">{course.title}</li>
          </ol>
        </nav>
      </div>

      <section id="course-details" className="section pt-3">
        <Container data-aos="fade-up">
          <Row className="gy-5">
            <Col lg={8}>
              <div className="mb-5">
                <div className="d-flex justify-content-between align-items-start mb-2">
                  <span className="badge" style={{ backgroundColor: "var(--accent-color)" }}>Actualizado {course.updatedAt}</span>
                  <div className="d-flex gap-2">
                    {isOwner && (
                      <>
                        <Link to={`/new/courses/${course.id}/edit`} className="btn btn-outline-secondary btn-sm">
                          <i className="bi bi-pencil me-1"></i> Editar curso
                        </Link>
                        <button className="btn btn-outline-danger btn-sm" onClick={async () => {
                          if (confirm('¿Seguro que deseas eliminar este curso?')) {
                            try {
                              const { deleteCourse } = await import('~/services/courseService');
                              await deleteCourse(course.id);
                              navigate("/new/courses");
                            } catch (error) {
                              alert("Error al eliminar el curso: " + (error instanceof Error ? error.message : String(error)));
                            }
                          }
                        }}>
                          <i className="bi bi-trash me-1"></i> Eliminar curso
                        </button>
                      </>
                    )}
                  </div>
                </div>
                <h1 className="display-5 fw-bold text-dark mb-3">{course.title}</h1>
                <p className="lead text-muted">{course.shortDescription}</p>

                <div className="d-flex flex-wrap align-items-center gap-3 text-secondary mt-3 small">
                  <div className="d-flex align-items-center">
                    <i className="bi bi-person-circle me-2 fs-5"></i>
                    <span>Por {course.creator?.username}</span>
                  </div>
                  <div className="vr"></div>
                  <div className="d-flex align-items-center text-warning">
                    <span className="fw-bold me-1 text-dark">{averageRating}</span>
                    {/* Estrellas */}
                    {[...Array(5)].map((_, i) => (
                      <i key={i} className={`bi bi-star${i < Math.floor(Number(averageRating)) ? "-fill" : ""}`}></i>
                    ))}
                    <span className="text-muted ms-1">( {reviewsNumber} reseñas ) </span>
                  </div>
                  <div className="vr"></div>
                  <div><i className="bi bi-people me-1"></i>{course.subscribersNumber} estudiantes</div>
                  <div className="vr"></div>
                  <div><i className="bi bi-globe me-1"></i>{course.language}</div>
                </div>

                {!isSubscribed && (
                  <div className="mt-4 mx-auto stats-modern-wrap" style={{ maxWidth: "430px" }}>
                    <h6 className="mb-2 stats-modern-title" style={{ textAlign: "center", color: "#6c757d", fontWeight: 700 }}>
                      <i className="bi bi-bar-chart-line me-1"></i> Estadísticas
                    </h6>
                    {hasSubscribers ? (
                      <div className="card stats-modern-card" style={{ border: 0, borderRadius: "16px", boxShadow: "0 12px 28px rgba(16, 24, 40, 0.08)", background: "linear-gradient(180deg, rgba(217, 109, 60, 0.08) 0%, rgba(255, 255, 255, 1) 52%)", overflow: "hidden" }}>
                        <div className="card-body" style={{ padding: "0.75rem 0.75rem 1.2rem" }}>
                          {/* Placeholder para carrusel de stats */}
                          <div id="courseStatsCarousel" className="carousel slide">
                            <div className="carousel-inner pb-2">
                              {slides.map((slide, index) => (
                                <div key={index} className={`carousel-item ${index === currentSlide ? 'active' : ''}`}>
                                  <div className="stats-modern-frame" style={{ height: "300px", display: "flex", justifyContent: "center", alignItems: "center" }}>
                                    <Chart
                                      info={slide.info}
                                      infoUser={userId ?? 0}
                                      infoCourse={course.id}
                                    />
                                  </div>
                                </div>
                              ))}
                            </div>

                            {/* Controles del carrusel */}
                            {slides.length > 1 && (
                              <>
                                <button className="carousel-control-prev" type="button" onClick={prevSlide} style={{ filter: "invert(1)" }}>
                                  <span className="carousel-control-prev-icon" aria-hidden="true"></span>
                                  <span className="visually-hidden">Anterior</span>
                                </button>
                                <button className="carousel-control-next" type="button" onClick={nextSlide} style={{ filter: "invert(1)" }}>
                                  <span className="carousel-control-next-icon" aria-hidden="true"></span>
                                  <span className="visually-hidden">Siguiente</span>
                                </button>
                              </>
                            )}
                          </div>

                        </div>
                      </div>
                    ) : (
                      <div className="card stats-modern-card text-center py-5">
                        <i className="bi bi-people text-muted mb-2" style={{ fontSize: "3rem", opacity: 0.5 }}></i>
                        <h6 className="fw-bold text-dark mb-1">Aún no hay nadie registrado</h6>
                        <p className="text-muted small mb-0">¡Puedes ser el primero!</p>
                      </div>
                    )}
                  </div>
                )}

              </div>

              <div className="card border-0 shadow-sm mb-5 bg-light">
                <div className="card-body p-4">
                  <h3 className="h5 fw-bold mb-4">Temas relacionados</h3>
                  <div className="d-flex flex-wrap gap-2 mb-4">
                    {Array.isArray(course.tags) && course.tags.map((tag: { id: number; name: string }) => (
                      <Link
                        key={tag.name || tag.id}
                        to={`/new/courses?tags=${tag.name}`}
                        className="badge bg-white text-dark border rounded-pill text-decoration-none px-3 py-2"
                      >
                        <i className="bi bi-tag me-1" style={{ color: "var(--accent-color)" }}></i>{tag.name}
                      </Link>
                    ))}
                    {(!Array.isArray(course.tags) || course.tags.length === 0) && (
                      <span className="badge text-bg-light border rounded-pill px-3 py-2">Sin etiquetas disponibles</span>
                    )}
                  </div>

                  <h3 className="h5 fw-bold mb-4">Lo que aprenderás</h3>
                  <ul className="feature-list list-unstyled">
                    {course.learningPoints?.map((point, idx) => (
                      <li key={idx} className="mb-2">
                        <i className="bi bi-check-circle-fill me-2" style={{ color: "var(--accent-color)" }}></i>
                        {point}
                      </li>
                    ))}
                  </ul>
                </div>
              </div>

              {isSubscribed && (
                <>
                  <div className="mt-4 mb-5 mx-auto stats-modern-wrap">
                    <h6 className="mb-2 stats-modern-title"><i className="bi bi-pie-chart-fill me-1"></i> Mi Progreso</h6>
                    <div className="card stats-modern-card">
                      <div className="card-body">
                        <div className="stats-modern-frame" style={{ height: "210px", width: "100%", borderRadius: "12px", background: "#ffffff", border: "1px solid rgba(0, 0, 0, 0.06)", display: "flex", alignItems: "center", justifyContent: "center" }}>
                          {/* Grafico */}
                          <span className="text-muted small">Cargando gráfico de progreso...</span>
                        </div>
                      </div>
                    </div>
                  </div>

                  <div className="card border-0 shadow-sm mb-4 bg-light">
                    <div className="card-body p-3 p-md-4">
                      <div className="d-flex flex-wrap align-items-center justify-content-between gap-2 mb-2">
                        <h4 className="h6 fw-bold mb-0">Tu progreso en este curso</h4>
                        <strong>{courseProgressPercentage}%</strong>
                      </div>
                      <ProgressBar now={courseProgressPercentage} variant="success" style={{ height: "10px" }} />
                    </div>
                  </div>
                </>
              )}

              <h3 className="h4 mb-4 fw-bold">Contenido del curso</h3>
              <Accordion defaultActiveKey="0" id="courseAccordion" className="mb-5">
                {modules.map((module: ModuleDTO, mIdx) => (
                  <Accordion.Item eventKey={mIdx.toString()} key={module.id} className="border mb-2 rounded shadow-sm">
                    <Accordion.Header>
                      <span className="fw-bold">Módulo {module.orderIndex}: {module.title}</span>
                    </Accordion.Header>
                    <Accordion.Body className="p-0 bg-white border-top">
                      <ListGroup variant="flush">
                        {module.lessons?.map((lesson: LessonDTO) => (
                          <ListGroup.Item key={lesson.id} className="d-flex justify-content-between align-items-center px-4 py-3 border-bottom bg-white">
                            <div className="d-flex align-items-center">
                              {(isSubscribed || isOwner) ? (
                                <>
                                  <i className="bi bi-play-circle me-3 fs-5 text-primary"></i>
                                  <a href={`/course/${course.id}/lesson/${lesson.id}/video`} className="text-decoration-none fw-semibold text-dark">{lesson.title}</a>
                                </>
                              ) : (
                                <>
                                  <i className="bi bi-lock me-3 fs-5 text-secondary"></i>
                                  <span className="text-muted">{lesson.title}</span>
                                </>
                              )}
                            </div>
                            <div className="d-flex align-items-center gap-2">
                              {lesson.completed && <Badge bg="success" pill className="px-2">Completada</Badge>}
                              <span className="text-muted small">10:00</span>
                            </div>
                          </ListGroup.Item>
                        ))}
                      </ListGroup>
                    </Accordion.Body>
                  </Accordion.Item>
                ))}
              </Accordion>

              <h3 className="h4 mb-3 fw-bold">Requisitos previos</h3>
              <ul className="mb-5">
                {course.prerequisites?.map((req, idx) => <li key={idx} className="mb-2">{req}</li>)}
              </ul>

              <h3 className="h4 mb-3 fw-bold">Descripción</h3>
              <div className="mb-5 text-secondary">
                <p>{course.longDescription}</p>
              </div>

              <h3 className="h4 mb-4 fw-bold">Instructor</h3>
              <Card className="border-0 shadow-sm mb-5">
                <Card.Body className="p-4 d-flex flex-column flex-md-row gap-4 align-items-center align-items-md-start">
                  <img src={getUserProfileImageUrl(course.creator?.id || 0)} alt="Instructor" className="rounded-circle" style={{ width: "100px", height: "100px", objectFit: "cover" }} />
                  <div className="text-center text-md-start">
                    <h4 className="h5 fw-bold mb-1">{course.creator?.username}</h4>
                    <p className="small mb-2" style={{ color: "var(--accent-color)" }}>{course.creator?.currentGoal}</p>
                    <p className="small text-muted mb-3">{course.creator?.shortDescription}</p>
                    <h6 className="small fw-bold text-uppercase text-muted mt-3">Más cursos de {course.creator?.username}:</h6>
                    <div className="d-flex gap-2 justify-content-center justify-content-md-start">
                      {course.creator?.otherCourses?.map(c => (
                        <Link key={c.id} to={`/new/courses/${c.id}`} className="badge bg-light text-dark border text-decoration-none">{c.title}</Link>
                      ))}
                    </div>
                  </div>
                </Card.Body>
              </Card>

              <h3 className="h4 mb-4 fw-bold">Reseñas de estudiantes</h3>
              {reviews?.map((review: any, idx: number) => (
                <div key={idx} className="vstack gap-4 mb-4">
                  <div className="card border-0 bg-light p-3">
                    <div className="d-flex justify-content-between align-items-center mb-2">
                      <div className="d-flex align-items-center gap-2">
                        <img src={getUserProfileImageUrl(review.user.id)} className="rounded-circle" style={{ width: "35px", height: "35px", objectFit: "cover" }} alt={review.user.username} />
                        <strong>{review.user.username}</strong>
                      </div>
                      <div className="text-warning small">
                        {[...Array(5)].map((_, i) => <i key={i} className={`bi bi-star${i < review.stars ? "-fill" : ""}`}></i>)}
                      </div>
                    </div>
                    <p className="mb-0 text-secondary small">"{review.content}"</p>
                  </div>
                </div>
              ))}
            </Col>

            <Col lg={4}>
              <div className="sticky-top" style={{ top: "100px", zIndex: 10 }}>
                <article className="price-card border-0 shadow-lg h-auto mb-4 bg-white rounded-4 overflow-hidden">
                  <div className="card-head pt-4 pb-2 text-center">
                    <img src={getCourseImageUrl(course.id)} alt={course.title} className="img-fluid rounded-4 mb-3 px-3" />
                    <h3 className="title fs-4 mb-0">Acceso Completo</h3>
                    <div className="price-wrap mt-3 mb-3 justify-content-center">
                      <span className="price display-4 fw-bold text-dark">{priceInEuros} €</span>
                    </div>
                  </div>

                  <div className="px-4 pb-4">
                    <div className="cta mb-4">
                      {isSubscribed ? (
                        <Button disabled className="w-100 py-3 fw-bold fs-5" style={{ backgroundColor: "#6c757d", borderColor: "#6c757d", color: "white", cursor: "not-allowed", opacity: 0.8 }}>
                          Suscrito
                        </Button>
                      ) : (
                        <Button onClick={handleSubscribe} className="w-100 py-3 fw-bold fs-5" style={{ backgroundColor: "#d96d3c", borderColor: "#d96d3c", color: "white" }}>
                          Suscribirse ahora
                        </Button>
                      )}
                    </div>

                    <p className="fw-bold text-dark mb-2 small text-uppercase">Este curso incluye:</p>
                    <ul className="feature-list list-unstyled small text-secondary mb-4">
                      <li className="mb-2 d-flex align-items-center gap-2"><i className="bi bi-camera-video" style={{ color: "var(--accent-color)" }}></i> {course.videoHours} horas de video</li>
                      <li className="mb-2 d-flex align-items-center gap-2"><i className="bi bi-file-earmark-arrow-down" style={{ color: "var(--accent-color)" }}></i> {course.downloadableResources} recursos descargables</li>
                      <li className="mb-2 d-flex align-items-center gap-2"><i className="bi bi-trophy" style={{ color: "var(--accent-color)" }}></i> Certificado digital</li>
                      <li className="mb-2 d-flex align-items-center gap-2"><i className="bi bi-infinity" style={{ color: "var(--accent-color)" }}></i> Acceso de por vida</li>
                    </ul>
                  </div>
                </article>
              </div>
            </Col>
          </Row>
        </Container>
      </section>
    </main>
  );
}
