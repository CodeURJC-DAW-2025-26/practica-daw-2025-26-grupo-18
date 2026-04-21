import { Container, Row, Col, Badge, Card, Accordion, Button, ProgressBar } from "react-bootstrap";
import { useLoaderData, useNavigate, Link } from "react-router";
import { getCourseById, subscribeToCourse } from "~/services/courseService";
import { getCourseImageUrl, getUserProfileImageUrl } from "~/utils/imageUrls";
import { useGlobalStore } from "~/stores/globalStore";
import type { LoaderFunctionArgs } from "react-router";
import type { CourseDetailDTO } from "~/dtos/CourseDTO";

export async function clientLoader({ params }: LoaderFunctionArgs) {
  const id = Number(params.id);
  const data = await getCourseById(id);
  
  return data as CourseDetailDTO;
}
clientLoader.hydrate = true;

export default function CourseDetail() {
  const data = useLoaderData<typeof clientLoader>();
  const navigate = useNavigate();
  const globalData = useGlobalStore((state) => state.globalData);

  // Desestructuramos según la estructura real del backend
  const { 
    course, 
    modules, 
    reviews, 
    isSuscribedToCourse: isSubscribed, 
    canEdit: isOwner,
    courseProgressPercentage 
  } = data;

  const handleSubscribe = async () => {
    try {
      await subscribeToCourse(course.id);
      navigate("/new/cart");
    } catch (error) {
      alert("Error al suscribirse: " + (error instanceof Error ? error.message : String(error)));
    }
  };

  return (
    <main className="main">
      {/* Page Title & Breadcrumbs */}
      <div className="page-title light-background mb-0">
        <Container>
          <nav className="breadcrumbs mb-4">
            <ol className="list-unstyled d-flex gap-2 m-0 p-0 small">
              <li><Link to="/new">Inicio</Link></li>
              <li><Link to="/new/courses">Cursos</Link></li>
              <li className="current text-muted">{course.title}</li>
            </ol>
          </nav>
        </Container>
      </div>

      <section id="course-details" className="course-details section pt-4 pb-5">
        <Container data-aos="fade-up">
          <Row className="gy-5">
            {/* Main Content */}
            <Col lg={8}>
              <div className="course-header mb-5">
                <div className="d-flex justify-content-between align-items-start mb-3">
                  <Badge bg="primary" className="px-3 py-2 rounded-pill mb-2" style={{ background: "var(--accent-color)" }}>
                    {course.category || "Desarrollo"}
                  </Badge>
                  {isOwner && (
                    <Link to={`/new/courses/${course.id}/edit`} className="btn btn-outline-secondary btn-sm rounded-pill px-3">
                      <i className="bi bi-pencil me-1"></i> Editar curso
                    </Link>
                  )}
                </div>
                <h1 className="display-5 fw-bold text-dark mb-3">{course.title}</h1>
                <p className="lead text-muted">{course.shortDescription}</p>
                <div className="course-meta d-flex flex-wrap gap-4 text-secondary mt-4 pb-4 border-bottom">
                  <span><i className="bi bi-star-fill text-warning me-1"></i> {data.averageRating} ({data.ratingCount} valoraciones)</span>
                  <span><i className="bi bi-people me-1"></i> {course.subscribersNumber} estudiantes</span>
                  <span><i className="bi bi-translate me-1"></i> {course.language}</span>
                  <span><i className="bi bi-patch-check me-1"></i> Última actualización: {course.updatedAt}</span>
                </div>
              </div>

              {/* Learning Points */}
              <div className="mb-5 py-4 bg-light rounded-4 p-4 border">
                <h3 className="h4 fw-bold mb-4">Lo que aprenderás</h3>
                <Row className="g-3">
                  {course.learningPoints.map((point: string, idx: number) => (
                    <Col md={6} key={idx} className="d-flex gap-2">
                      <i className="bi bi-check2 text-success fs-5"></i>
                      <span>{point}</span>
                    </Col>
                  ))}
                </Row>
              </div>

              {/* Course Content (Modules) */}
              <div className="mb-5">
                <h3 className="h4 fw-bold mb-4">Contenido del curso</h3>
                <Accordion defaultActiveKey="0" className="shadow-sm rounded-4 overflow-hidden border">
                  {modules.map((module: any, mIdx: number) => (
                    <Accordion.Item eventKey={mIdx.toString()} key={module.id} className="border-0 border-bottom">
                      <Accordion.Header>
                        <div className="w-100 d-flex justify-content-between pe-3">
                          <span className="fw-bold">{module.title}</span>
                          <span className="text-muted small">{module.lessons?.length || 0} lecciones</span>
                        </div>
                      </Accordion.Header>
                      <Accordion.Body className="p-0">
                        <ListGroup variant="flush">
                          {module.lessons?.map((lesson: any) => (
                            <ListGroup.Item key={lesson.id} className="py-3 px-4 d-flex justify-content-between align-items-center border-0 border-bottom-light">
                              <div className="d-flex align-items-center gap-3">
                                {isSubscribed ? (
                                  <Link to={`/new/courses/${course.id}/lessons/${lesson.id}`} className="text-decoration-none text-dark d-flex align-items-center gap-3">
                                    <i className="bi bi-play-circle fs-5 text-primary"></i>
                                    <span>{lesson.title}</span>
                                  </Link>
                                ) : (
                                  <>
                                    <i className="bi bi-lock-fill text-muted fs-5"></i>
                                    <span className="text-muted">{lesson.title}</span>
                                  </>
                                )}
                              </div>
                              {lesson.completed && <i className="bi bi-check-circle-fill text-success fs-5"></i>}
                            </ListGroup.Item>
                          ))}
                        </ListGroup>
                      </Accordion.Body>
                    </Accordion.Item>
                  ))}
                </Accordion>
              </div>

              {/* Prerequisites */}
              <div className="mb-5 py-4 border-top">
                <h3 className="h4 fw-bold mb-4">Requisitos</h3>
                <ul className="list-unstyled">
                  {course.prerequisites?.map((req: string, idx: number) => (
                    <li key={idx} className="mb-2 d-flex gap-3 align-items-start">
                      <i className="bi bi-circle-fill text-muted mt-2" style={{ fontSize: "6px" }}></i>
                      <span>{req}</span>
                    </li>
                  ))}
                </ul>
              </div>

              {/* Description */}
              <div className="mb-5 py-4 border-top">
                <h3 className="h4 fw-bold mb-4">Descripción</h3>
                <div className="course-description" style={{ lineHeight: "1.8" }}>
                  {course.longDescription}
                </div>
              </div>

              {/* Reviews */}
              <div className="mb-5 py-4 border-top">
                <h3 className="h4 fw-bold mb-4">Valoraciones de estudiantes</h3>
                <Row className="g-4">
                  {reviews?.map((review: any, idx: number) => (
                    <Col md={6} key={idx}>
                      <Card className="h-100 border-0 shadow-sm rounded-4 p-3 bg-light">
                        <div className="d-flex align-items-center gap-3 mb-3">
                          <div className="avatar rounded-circle overflow-hidden bg-secondary text-white d-flex align-items-center justify-content-center" style={{ width: "45px", height: "45px" }}>
                            {review.user?.image?.url ? (
                              <img src={getUserProfileImageUrl(review.user.id)} alt={review.user.username} className="w-100 h-100 object-fit-cover" />
                            ) : (
                              <span>{review.user?.initials || "U"}</span>
                            )}
                          </div>
                          <div>
                            <h6 className="mb-0 fw-bold">{review.user?.username || "Anónimo"}</h6>
                            <div className="stars text-warning small">
                              {[...Array(5)].map((_, i) => (
                                <i key={i} className={`bi bi-star${i < review.stars ? "-fill" : ""}`}></i>
                              ))}
                            </div>
                          </div>
                        </div>
                        <p className="text-muted small mb-0">{review.content}</p>
                      </Card>
                    </Col>
                  ))}
                </Row>
              </div>
            </Col>

            {/* Sidebar */}
            <Col lg={4}>
              <aside className="course-sidebar sticky-top" style={{ top: "100px" }}>
                <Card className="border-0 shadow-lg rounded-4 overflow-hidden">
                  <div className="position-relative">
                    <Card.Img variant="top" src={getCourseImageUrl(course.id)} alt={course.title} />
                    {!isSubscribed && (
                      <div className="position-absolute top-50 start-50 translate-middle">
                        <div className="bg-white rounded-circle p-3 shadow-lg" style={{ cursor: "pointer", opacity: 0.9 }}>
                          <i className="bi bi-play-fill display-4 text-primary"></i>
                        </div>
                      </div>
                    )}
                  </div>
                  <Card.Body className="p-4">
                    {isSubscribed ? (
                      <div className="mb-4">
                        <div className="d-flex justify-content-between align-items-center mb-2">
                          <h4 className="fw-bold mb-0">Tu progreso</h4>
                          <span className="badge bg-success">{courseProgressPercentage}%</span>
                        </div>
                        <ProgressBar now={courseProgressPercentage} variant="success" className="rounded-pill" style={{ height: "10px" }} />
                        <Button
                          as={Link}
                          to={`/new/courses/${course.id}/lessons/1`} // Simplificado: debería ir a la última lección vista
                          variant="primary"
                          className="w-100 mt-4 py-3 fw-bold rounded-3"
                          style={{ background: "var(--accent-color)", borderColor: "var(--accent-color)" }}
                        >
                          Continuar aprendiendo
                        </Button>
                      </div>
                    ) : (
                      <div className="mb-4 text-center">
                        <div className="h2 fw-bold text-dark mb-4">{data.priceInEuros} €</div>
                        <Button
                          onClick={handleSubscribe}
                          variant="primary"
                          className="w-100 py-3 fw-bold fs-5 border-0 rounded-3 shadow-sm mb-3"
                          style={{ background: "#d96d3c", color: "white" }}
                        >
                          Suscribirse ahora
                        </Button>
                        <p className="text-muted small">Garantía de reembolso de 30 días</p>
                      </div>
                    )}

                    <div className="course-features mt-4">
                      <h5 className="h6 fw-bold mb-3">Este curso incluye:</h5>
                      <ul className="list-unstyled d-grid gap-3 small">
                        <li><i className="bi bi-play-btn me-2 text-primary"></i> {course.videoHours} horas de vídeo bajo demanda</li>
                        <li><i className="bi bi-file-earmark-arrow-down me-2 text-primary"></i> {course.downloadableResources} recursos descargables</li>
                        <li><i className="bi bi-infinity me-2 text-primary"></i> Acceso de por vida</li>
                        <li><i className="bi bi-phone me-2 text-primary"></i> Acceso en dispositivos móviles</li>
                        <li><i className="bi bi-trophy me-2 text-primary"></i> Certificado de finalización</li>
                      </ul>
                    </div>
                  </Card.Body>
                </Card>

                {/* Instructor Card */}
                <Card className="mt-4 border-0 shadow-sm rounded-4 p-4">
                  <h5 className="fw-bold mb-4">Instructor</h5>
                  <div className="d-flex align-items-center gap-3 mb-3">
                    <div className="instructor-avatar bg-primary text-white rounded-circle d-flex align-items-center justify-content-center fw-bold" style={{ width: "60px", height: "60px", fontSize: "1.5rem" }}>
                      {course.creator?.username?.charAt(0).toUpperCase()}
                    </div>
                    <div>
                      <h6 className="mb-0 fw-bold">{course.creator?.username}</h6>
                      <span className="text-muted small">{course.creator?.currentGoal}</span>
                    </div>
                  </div>
                  <p className="text-muted small mb-0">{course.creator?.shortDescription}</p>
                </Card>
              </aside>
            </Col>
          </Row>
        </Container>
      </section>
    </main>
  );
}
