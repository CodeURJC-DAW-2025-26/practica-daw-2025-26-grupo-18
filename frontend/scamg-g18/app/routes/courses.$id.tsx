import { Container, Row, Col, Badge, Card, ListGroup, Button, Accordion } from "react-bootstrap";
import { useLoaderData, useNavigate, Link } from "react-router";
import { getCourseById } from "~/services/courseService";
import { addCourseToCart } from "~/services/cartService";
import { getCourseImageUrl } from "~/utils/imageUrls";
import { useGlobalStore } from "~/stores/globalStore";
import type { ClientLoaderArgs } from "react-router";

export async function clientLoader({ params }: ClientLoaderArgs) {
  const id = Number(params.id);
  const course = await getCourseById(id);
  return { course };
}

export default function CourseDetail() {
  const { course } = useLoaderData<typeof clientLoader>();
  const navigate = useNavigate();
  const globalData = useGlobalStore((state) => state.globalData);

  const isSubscribed = (course as any).isSubscribed;
  const isOwner = (course as any).isOwner;

  const handleAddToCart = async () => {
    try {
      await addCourseToCart(course.id);
      navigate("/new/cart");
    } catch (error) {
      alert("Error al añadir al carrito: " + (error instanceof Error ? error.message : String(error)));
    }
  };

  return (
    <main className="main">
      {/* Breadcrumbs & Title placeholder */}
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

      <section id="course-details" className="section pt-4">
        <Container data-aos="fade-up">
          <Row className="gy-5">
            {/* Main Content */}
            <Col lg={8}>
              <div className="mb-5">
                <div className="d-flex justify-content-between align-items-start mb-2">
                  <span className="badge px-3 py-2 rounded-pill" style={{ backgroundColor: "var(--accent-color)" }}>
                    {course.category || "General"}
                  </span>
                  <div className="d-flex gap-2">
                    {isOwner && (
                      <Link to={`/new/courses/${course.id}/edit`} className="btn btn-outline-secondary btn-sm rounded-pill px-3">
                        <i className="bi bi-pencil me-1"></i> Editar
                      </Link>
                    )}
                  </div>
                </div>

                <h1 className="display-5 fw-bold text-dark mb-3">{course.title}</h1>
                <p className="lead text-muted mb-4">{course.shortDescription}</p>

                <div className="d-flex flex-wrap align-items-center gap-4 text-secondary mt-3 small opacity-90 border-bottom pb-4 mb-4">
                  <div className="d-flex align-items-center">
                    <i className="bi bi-person-circle me-2 fs-5" style={{ color: "var(--accent-color)" }}></i>
                    <span>Por <strong>{(course as any).creator?.username || (course as any).creatorUsername || "Instructor"}</strong></span>
                  </div>
                  <div className="vr d-none d-md-block"></div>
                  <div className="d-flex align-items-center">
                    <span className="fw-bold me-1 text-dark">{(course as any).averageRating || "0.0"}</span>
                    <i className="bi bi-star-fill text-warning me-1"></i>
                    <span className="text-muted">({(course as any).reviewsNumber || 0} reseñas)</span>
                  </div>
                  <div className="vr d-none d-md-block"></div>
                  <div><i className="bi bi-people me-1"></i>{course.subscribersNumber} estudiantes</div>
                  <div className="vr d-none d-md-block"></div>
                  <div><i className="bi bi-globe me-1"></i>{course.language}</div>
                </div>

                {/* Lo que aprenderás section */}
                <div className="card border-0 shadow-sm mb-5" style={{ backgroundColor: "#faf8f5" }}>
                  <div className="card-body p-4">
                    <h3 className="h5 fw-bold mb-4">Lo que aprenderás</h3>
                    <Row className="gy-2">
                      {course.learningPoints.map((point: string, idx: number) => (
                        <Col md={12} key={idx} className="d-flex align-items-start gap-2">
                          <i className="bi bi-check-circle-fill" style={{ color: "var(--accent-color)" }}></i>
                          <span>{point}</span>
                        </Col>
                      ))}
                    </Row>
                  </div>
                </div>

                <h3 className="h4 mb-4 fw-bold">Contenido del curso</h3>
                <Accordion defaultActiveKey="0" className="mb-5 custom-accordion">
                  {course.modules.map((module: any, mIdx: number) => (
                    <Accordion.Item eventKey={mIdx.toString()} key={mIdx} className="border mb-2 rounded overflow-hidden">
                      <Accordion.Header>
                        <span className="fw-bold">Módulo {mIdx + 1}: {module.title}</span>
                      </Accordion.Header>
                      <Accordion.Body className="p-0">
                        <ListGroup variant="flush">
                          {module.lessons.map((lesson: any, lIdx: number) => (
                            <ListGroup.Item key={lIdx} className="d-flex justify-content-between align-items-center px-4 py-3 border-bottom">
                              <div className="d-flex align-items-center gap-3">
                                {isSubscribed ? (
                                  <i className="bi bi-play-circle fs-5" style={{ color: "var(--accent-color)" }}></i>
                                ) : (
                                  <i className="bi bi-lock text-muted fs-5"></i>
                                )}
                                <div>
                                  <div className={`fw-medium ${!isSubscribed ? 'text-muted' : ''}`}>{lesson.title}</div>
                                </div>
                              </div>
                            </ListGroup.Item>
                          ))}
                        </ListGroup>
                      </Accordion.Body>
                    </Accordion.Item>
                  ))}
                </Accordion>

                <h3 className="h4 mb-3 fw-bold">Requisitos previos</h3>
                <ul className="mb-5 text-secondary ps-3">
                  {course.prerequisites.map((req: string, idx: number) => (
                    <li key={idx} className="mb-2">{req}</li>
                  ))}
                </ul>

                <h3 className="h4 mb-3 fw-bold">Descripción</h3>
                <div className="mb-5 text-secondary" style={{ lineHeight: "1.8" }}>
                  <p>{course.longDescription}</p>
                </div>

                {/* Instructor Card */}
                <h3 className="h4 mb-4 fw-bold">Instructor</h3>
                <div className="card border-0 shadow-sm mb-5 p-2">
                  <div className="card-body p-4 d-flex flex-column flex-md-row gap-4 align-items-center align-items-md-start">
                    <div className="bg-light rounded-circle d-flex align-items-center justify-content-center text-secondary border"
                      style={{ width: "100px", height: "100px", minWidth: "100px" }}>
                      <i className="bi bi-person-fill fs-1"></i>
                    </div>
                    <div className="text-center text-md-start w-100">
                      <h4 className="h5 fw-bold mb-1">{(course as any).creator?.username || (course as any).creatorUsername}</h4>
                      <p className="small mb-2" style={{ color: "var(--accent-color)" }}>Instructor Certificado</p>
                      <p className="small text-muted mb-3 line-clamp-3">Experto en la materia con años de experiencia compartiendo conocimientos de alto nivel.</p>
                    </div>
                  </div>
                </div>
              </div>
            </Col>

            {/* Sidebar with Buy Card */}
            <Col lg={4}>
              <div className="sticky-top" style={{ top: "100px", zIndex: 10 }}>
                <article className="price-card border-0 shadow-lg h-auto mb-4 bg-white rounded-4 overflow-hidden">
                  <div className="text-center pt-4 pb-2 px-3">
                    <div className="aspect-ratio-box rounded-4 mb-3 overflow-hidden"
                      style={{ position: 'relative', paddingTop: '56.25%', background: '#eee' }}>
                      <img
                        src={getCourseImageUrl(course.id)}
                        alt={course.title}
                        className="w-100 h-100 position-absolute top-0 start-0 object-fit-cover"
                      />
                    </div>
                    <h3 className="title h5 mb-0 px-2 fw-bold text-dark">Acceso Completo</h3>
                    <div className="price-wrap mt-3 mb-3 d-flex justify-content-center">
                      <span className="price display-4 fw-bold text-dark">{(course as any).priceInEuros || course.price}€</span>
                    </div>
                  </div>

                  <div className="px-4 pb-4">
                    <div className="cta mb-4">
                      {isSubscribed ? (
                        <div className="alert alert-success text-center py-3 fw-bold rounded-3 mb-0">
                          <i className="bi bi-check-circle me-2"></i>Ya estás suscrito
                        </div>
                      ) : (
                        <Button
                          onClick={handleAddToCart}
                          className="btn-choose w-100 py-3 fw-bold fs-5 border-0 rounded-3 shadow-sm transition-all"
                          style={{ background: "#d96d3c", color: "white" }}
                        >
                          Añadir al carrito
                        </Button>
                      )}
                    </div>

                    <p className="fw-bold text-dark mb-2 small text-uppercase opacity-75">Este curso incluye:</p>
                    <ul className="list-unstyled small text-secondary mb-4 ps-1">
                      <li className="mb-2 d-flex align-items-center gap-2">
                        <i className="bi bi-camera-video fs-6" style={{ color: "var(--accent-color)" }}></i>
                        {course.videoHours} horas de video
                      </li>
                      <li className="mb-2 d-flex align-items-center gap-2">
                        <i className="bi bi-file-earmark-arrow-down fs-6" style={{ color: "var(--accent-color)" }}></i>
                        {course.downloadableResources} recursos descargables
                      </li>
                      <li className="mb-2 d-flex align-items-center gap-2">
                        <i className="bi bi-trophy fs-6" style={{ color: "var(--accent-color)" }}></i>
                        Certificado digital
                      </li>
                      <li className="mb-2 d-flex align-items-center gap-2">
                        <i className="bi bi-infinity fs-6" style={{ color: "var(--accent-color)" }}></i>
                        Acceso de por vida
                      </li>
                    </ul>

                    <hr className="text-muted opacity-25" />

                    <div className="mb-2">
                      <span className="small fw-bold text-muted text-uppercase" style={{ fontSize: "0.65rem" }}>Categoría:</span>
                      <div className="mt-1">
                        <span className="badge bg-light text-secondary border fw-normal">{course.category || "General"}</span>
                      </div>
                    </div>
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
