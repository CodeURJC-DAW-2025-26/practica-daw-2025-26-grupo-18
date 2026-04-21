import { Container, Row, Col, Badge, Card, ListGroup, Button } from "react-bootstrap";
import { useLoaderData, useNavigate, Link } from "react-router";
import { getEventById } from "~/services/eventService";
import { addEventToCart } from "~/services/cartService";
import { getEventImageUrl } from "~/utils/imageUrls";
import { useGlobalStore } from "~/stores/globalStore";
import type { ClientLoaderArgs } from "react-router";

export async function clientLoader({ params }: ClientLoaderArgs) {
  const id = Number(params.id);
  if (isNaN(id)) throw new Error("ID de evento no válido");
  const event = await getEventById(id);
  return { event };
}

export default function EventDetail() {
  const { event } = useLoaderData<typeof clientLoader>();
  const navigate = useNavigate();

  const isPurchased = (event as any).isPurchased;
  const isOwner = (event as any).isOwner;

  const handleAddToCart = async () => {
    try {
      await addEventToCart(event.id);
      navigate("/new/cart");
    } catch (error) {
      alert("Error al añadir al carrito: " + (error instanceof Error ? error.message : String(error)));
    }
  };

  return (
    <div className="bg-light min-vh-100 pb-5">
      {/* Hero Section */}
      <div className="bg-primary text-white py-5 mb-5 shadow">
        <Container>
          <Row className="align-items-center">
            <Col lg={8}>
              <Badge bg="light" text="primary" className="mb-2">{event.category}</Badge>
              <h1 className="display-4 fw-bold mb-3">{event.title}</h1>
              <p className="lead mb-4 opacity-90">{event.description.substring(0, 200)}...</p>
              <div className="d-flex flex-wrap gap-4 small">
                <span><i className="bi bi-calendar-event me-1"></i> {event.startDateStr}</span>
                <span><i className="bi bi-geo-alt me-1"></i> {event.locationCity}, {event.locationCountry}</span>
                <span><i className="bi bi-people me-1"></i> {event.capacity} plazas máx.</span>
              </div>
            </Col>
            <Col lg={4} className="mt-4 mt-lg-0">
               <Card className="shadow-lg border-0">
                 <Card.Img variant="top" src={getEventImageUrl(event.id)} />
                 <Card.Body className="p-4 text-dark text-center">
                   <div className="h2 fw-bold mb-3 text-primary">{event.price > 0 ? `${event.price}€` : "Entrada Gratuita"}</div>
                   
                   {isPurchased ? (
                     <Badge bg="success" className="w-100 py-3 mb-2 fs-6">Ya tienes tu entrada</Badge>
                   ) : isOwner ? (
                     <Link 
                       to={`/new/events/${event.id}/edit`} 
                       className="btn btn-outline-primary btn-lg w-100"
                     >
                       Gestionar Evento
                     </Link>
                   ) : (
                     <Button variant="primary" size="lg" className="w-100 py-3 fw-bold" onClick={handleAddToCart}>
                       Reservar Plaza
                     </Button>
                   )}
                   
                   <p className="small text-muted mt-3">Confirmación inmediata por email</p>
                 </Card.Body>
               </Card>
            </Col>
          </Row>
        </Container>
      </div>

      <Container>
        <Row className="g-4">
          <Col lg={8}>
            {/* Description */}
            <Card className="mb-4 border-0 shadow-sm p-4">
              <h4 className="fw-bold mb-3">Sobre el evento</h4>
              <div className="text-secondary" style={{ whiteSpace: "pre-line" }}>
                {event.description}
              </div>
            </Card>

            {/* Agenda */}
            <div className="mb-5">
              <h4 className="fw-bold mb-3">Agenda</h4>
              <ListGroup variant="flush" className="shadow-sm rounded overflow-hidden">
                {event.sessionTitles.map((title: string, idx: number) => (
                  <ListGroup.Item key={idx} className="py-4 px-4 bg-white border-bottom">
                    <Row>
                      <Col xs={3} md={2} className="text-primary fw-bold">
                        {event.sessionTimes?.[idx] || "--:--"}
                      </Col>
                      <Col xs={9} md={10}>
                         <div className="fw-bold h5 mb-1">{title}</div>
                         <div className="text-secondary small">{event.sessionDescriptions?.[idx]}</div>
                      </Col>
                    </Row>
                  </ListGroup.Item>
                ))}
              </ListGroup>
            </div>

            {/* Speakers */}
            <div className="mb-5">
              <h4 className="fw-bold mb-3">Ponentes</h4>
              <Row>
                {event.speakerNames.map((name: string, idx: number) => (
                  <Col md={6} lg={4} key={idx} className="mb-3">
                    <Card className="border-0 shadow-sm text-center p-3 h-100">
                      <div className="rounded-circle bg-light d-flex align-items-center justify-content-center mx-auto mb-3" style={{ width: "80px", height: "80px" }}>
                        <i className="bi bi-person-fill text-secondary fs-1"></i>
                      </div>
                      <h5 className="mb-0 fw-bold">{name}</h5>
                    </Card>
                  </Col>
                ))}
              </Row>
            </div>
          </Col>

          <Col lg={4}>
            {/* Location Card */}
            <Card className="mb-4 border-0 shadow-sm overflow-hidden">
              <div className="bg-primary py-3 px-4 text-white fw-bold">
                <i className="bi bi-geo-alt-fill me-2"></i> Localización
              </div>
              <Card.Body className="p-4">
                <h5 className="fw-bold mb-2">{event.locationName}</h5>
                <p className="text-secondary mb-3">{event.locationAddress}<br />{event.locationCity}, {event.locationCountry}</p>
                <div className="bg-light rounded p-4 text-center border">
                   <i className="bi bi-map-fill h1 text-muted opacity-50"></i>
                   <p className="small mb-0 mt-2">Mapa interactivo no disponible en esta vista</p>
                </div>
              </Card.Body>
            </Card>

            {/* Event Summary */}
            <Card className="mb-4 border-0 shadow-sm p-4 text-center bg-white">
               <h5 className="fw-bold mb-3">Detalles</h5>
               <ListGroup variant="flush">
                  <ListGroup.Item className="d-flex justify-content-between px-0">
                    <span className="text-muted">Inicio</span>
                    <span className="fw-medium">{event.startDateStr} {event.startTimeStr}</span>
                  </ListGroup.Item>
                  <ListGroup.Item className="d-flex justify-content-between px-0">
                    <span className="text-muted">Fin</span>
                    <span className="fw-medium">{event.endDateStr} {event.endTimeStr}</span>
                  </ListGroup.Item>
                  <ListGroup.Item className="d-flex justify-content-between px-0">
                    <span className="text-muted">Capacidad</span>
                    <span className="fw-medium">{event.capacity} personas</span>
                  </ListGroup.Item>
               </ListGroup>
            </Card>
          </Col>
        </Row>
      </Container>
    </div>
  );
}
