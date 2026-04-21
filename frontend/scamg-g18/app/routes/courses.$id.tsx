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
    <div className="bg-light min-vh-100 pb-5">
      {/* Hero Section */}
      <div className="bg-dark text-white py-5 mb-5 shadow">
        <Container>
          <Row className="align-items-center">
            <Col lg={8}>
              <Badge bg="primary" className="mb-2">{course.language}</Badge>
              <h1 className="display-4 fw-bold mb-3">{course.title}</h1>
              <p className="lead mb-4">{course.shortDescription}</p>
              <div className="d-flex gap-4 small opacity-75">
                <span><i className="bi bi-clock me-1"></i> {course.videoHours} horas</span>
                <span><i className="bi bi-file-earmark-arrow-down me-1"></i> {course.downloadableResources} recursos</span>
                <span><i className="bi bi-translate me-1"></i> {course.language}</span>
              </div>
            </Col>
            <Col lg={4} className="mt-4 mt-lg-0">
               <Card className="shadow-lg border-0">
                 <Card.Img variant="top" src={getCourseImageUrl(course.id)} />
                 <Card.Body className="p-4 text-dark text-center">
                   <div className="h2 fw-bold mb-3">{course.price > 0 ? `${course.price}€` : "Gratis"}</div>
                   
                   {isSubscribed ? (
                     <Button variant="success" size="lg" className="w-100" onClick={() => navigate(`/new/courses/${course.id}/learn`)}>
                       Ir al curso
                     </Button>
                   ) : isOwner ? (
                     <Link 
                       to={`/new/courses/${course.id}/edit`} 
                       className="btn btn-outline-primary btn-lg w-100"
                     >
                       Editar Curso
                     </Link>
                   ) : (
                     <Button variant="primary" size="lg" className="w-100 py-3 fw-bold" onClick={handleAddToCart}>
                       Comprar ahora
                     </Button>
                   )}
                   
                   <p className="small text-muted mt-3">Garantía de reembolso de 30 días</p>
                 </Card.Body>
               </Card>
            </Col>
          </Row>
        </Container>
      </div>

      <Container>
        <Row>
          <Col lg={8}>
            {/* Learning Points */}
            <Card className="mb-4 border-0 shadow-sm p-4">
              <h4 className="fw-bold mb-3">¿Qué aprenderás?</h4>
              <Row>
                {course.learningPoints.map((point: string, idx: number) => (
                  <Col md={6} key={idx} className="mb-2">
                    <i className="bi bi-check-lg text-success me-2"></i> {point}
                  </Col>
                ))}
              </Row>
            </Card>

            {/* Description */}
            <div className="mb-5">
              <h4 className="fw-bold mb-3">Descripción</h4>
              <div className="text-secondary" style={{ whiteSpace: "pre-line" }}>
                {course.longDescription}
              </div>
            </div>

            {/* Content */}
            <div className="mb-5">
              <h4 className="fw-bold mb-3">Contenido del curso</h4>
              <Accordion defaultActiveKey="0">
                {course.modules.map((module: any, mIdx: number) => (
                  <Accordion.Item eventKey={mIdx.toString()} key={mIdx}>
                    <Accordion.Header>
                      <div className="d-flex justify-content-between w-100 pe-3">
                        <strong>{module.title}</strong>
                        <span className="text-muted small">{module.lessons.length} lecciones</span>
                      </div>
                    </Accordion.Header>
                    <Accordion.Body className="p-0">
                      <ListGroup variant="flush">
                        {module.lessons.map((lesson: any, lIdx: number) => (
                          <ListGroup.Item key={lIdx} className="py-3 d-flex align-items-center">
                            <i className="bi bi-play-circle me-3 text-primary"></i>
                            <div>
                               <div className="fw-medium">{lesson.title}</div>
                               <div className="small text-muted">{lesson.description}</div>
                            </div>
                          </ListGroup.Item>
                        ))}
                      </ListGroup>
                    </Accordion.Body>
                  </Accordion.Item>
                ))}
              </Accordion>
            </div>

            {/* Prerequisites */}
            <div className="mb-5">
              <h4 className="fw-bold mb-3">Requisitos</h4>
              <ul className="text-secondary">
                {course.prerequisites.map((req: string, idx: number) => (
                  <li key={idx}>{req}</li>
                ))}
              </ul>
            </div>
          </Col>

          <Col lg={4}>
            {/* Sidebar or additional info could go here */}
            {isOwner && (
               <Card className="border-0 shadow-sm p-3 mb-4 text-center">
                 <h5>Estadísticas de Instructor</h5>
                 <hr/>
                 <div className="d-flex justify-content-around">
                    <div>
                      <div className="h4 fw-bold">{course.studentsCount || 0}</div>
                      <div className="small text-muted">Alumnos</div>
                    </div>
                    <div>
                      <div className="h4 fw-bold">{course.rating || 0}</div>
                      <div className="small text-muted">Valoración</div>
                    </div>
                 </div>
               </Card>
            )}
          </Col>
        </Row>
      </Container>
    </div>
  );
}
