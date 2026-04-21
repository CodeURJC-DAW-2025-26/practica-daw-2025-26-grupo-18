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
    <main className="main">
      {/* Page Title & Breadcrumbs */}
      <div className="page-title light-background mb-0">
        <Container>
          <nav className="breadcrumbs mb-4">
            <ol className="list-unstyled d-flex gap-2 m-0 p-0 small">
              <li><Link to="/new">Inicio</Link></li>
              <li><Link to="/new/events">Eventos</Link></li>
              <li className="current text-muted">{event.title}</li>
            </ol>
          </nav>
        </Container>
      </div>

      <section id="service-details" className="service-details section pt-4 pb-5">
        <Container data-aos="fade-up">
          <Row className="gy-5">
            {/* Main Content */}
            <Col lg={8} className="order-lg-1 order-2">
              <div className="service-main-content">
                <div className="d-flex justify-content-end mb-3 gap-2">
                  {isOwner && (
                    <Link to={`/new/events/${event.id}/edit`} className="btn btn-outline-secondary btn-sm rounded-pill px-3">
                      <i className="bi bi-pencil me-1"></i> Editar evento
                    </Link>
                  )}
                </div>

                <div className="service-header mb-5">
                  <h1 className="display-5 fw-bold text-dark mb-3">{event.title}</h1>
                  <div className="service-meta d-flex flex-wrap gap-4 text-secondary small opacity-90 mb-4 pb-4 border-bottom">
                    <span><i className="bi bi-calendar-event me-2" style={{ color: "var(--accent-color)" }}></i> {event.startDateStr}</span>
                    <span><i className="bi bi-clock me-2" style={{ color: "var(--accent-color)" }}></i> {event.startTimeStr} - {event.endTimeStr}</span>
                    <span><i className="bi bi-geo-alt me-2" style={{ color: "var(--accent-color)" }}></i>
                      {event.locationName ?? "Online"}
                    </span>
                    <span><i className="bi bi-people me-2" style={{ color: "var(--accent-color)" }}></i> {(event as any).attendeesCount || 0} / {event.capacity} plazas ocupadas</span>
                  </div>
                  <p className="lead text-muted" style={{ lineHeight: "1.7" }}>{event.description}</p>
                </div>

                {/* Info Blocks / Tabs Alternative */}
                <div className="mb-5">
                  <h3 className="h4 fw-bold mb-4 border-start border-4 ps-3" style={{ borderColor: "var(--accent-color) !important" }}>Sobre este evento</h3>
                  <div className="row align-items-center gy-4">
                    <Col md={6}>
                      <p className="text-secondary mb-0">{event.description}</p>
                    </Col>
                    <Col md={6}>
                      <img src={getEventImageUrl(event.id)} alt={event.title} className="img-fluid rounded-4 shadow-sm border" />
                    </Col>
                  </div>
                </div>

                {/* Agenda */}
                <div className="mb-5 py-4 border-top">
                  <h3 className="h4 fw-bold mb-4"><i className="bi bi-diagram-3 me-2" style={{ color: "var(--accent-color)" }}></i>Agenda</h3>
                  <div className="process-timeline border-start ms-2 ps-4 position-relative">
                    {event.sessionTitles?.map((title: string, idx: number) => (
                      <div key={idx} className="timeline-item mb-4 position-relative">
                        <div className="timeline-marker position-absolute start-0 translate-middle-x bg-white border border-4 rounded-circle"
                          style={{ width: "16px", height: "16px", left: "-25px", borderColor: "var(--accent-color) !important", top: "10px" }}></div>
                        <div className="fw-bold text-accent mb-1" style={{ color: "var(--accent-color)" }}>{event.sessionTimes?.[idx] || "--:--"}</div>
                        <h4 className="h5 fw-bold mb-1">{title}</h4>
                        <p className="text-muted small mb-0">{event.sessionDescriptions?.[idx]}</p>
                      </div>
                    ))}
                  </div>
                </div>

                {/* Speakers */}
                <div className="mb-5 py-4 border-top">
                  <h3 className="h4 fw-bold mb-4"><i className="bi bi-mic me-2" style={{ color: "var(--accent-color)" }}></i>Ponentes</h3>
                  <Row className="g-4">
                    {event.speakerNames?.map((name: string, idx: number) => (
                      <Col md={4} key={idx}>
                        <div className="benefit-card p-4 border rounded-4 text-center h-100 shadow-sm bg-white">
                          <div className="benefit-icon mb-3">
                            <i className="bi bi-person-circle display-4 text-accent opacity-50" style={{ color: "var(--accent-color)" }}></i>
                          </div>
                          <h4 className="h6 fw-bold mb-1">Ponente</h4>
                          <p className="text-muted small mb-0">{name}</p>
                        </div>
                      </Col>
                    ))}
                  </Row>
                </div>

                {/* Ubicación */}
                <div className="mb-5 py-4 border-top">
                  <h3 className="h4 fw-bold mb-4"><i className="bi bi-geo-alt me-2" style={{ color: "var(--accent-color)" }}></i>Ubicación</h3>
                  <div className="card border-0 shadow-sm overflow-hidden bg-light rounded-4">
                    <Row className="g-0 align-items-center">
                      <Col lg={5} className="p-4 p-md-5">
                        <h3 className="h5 fw-bold mb-3">{event.locationName || "Ubicación del evento"}</h3>
                        <p className="mb-1 text-muted">{event.locationAddress}</p>
                        <p className="mb-0 text-muted">{event.locationCity}, {event.locationCountry}</p>
                      </Col>
                      <Col lg={7} className="bg-secondary opacity-25 d-flex align-items-center justify-content-center" style={{ minHeight: "250px" }}>
                        <i className="bi bi-map display-1 text-white"></i>
                      </Col>
                    </Row>
                  </div>
                </div>
              </div>
            </Col>

            {/* Sidebar */}
            <Col lg={4} className="order-lg-2 order-1">
              <aside className="service-sidebar sticky-top" style={{ top: "100px", zIndex: 10 }}>
                <div className="action-card p-4 bg-white rounded-4 shadow-lg border-0 mb-4 overflow-hidden position-relative">
                  <div className="position-absolute top-0 end-0" style={{ opacity: 0.05, transform: "translate(20%, -20%)" }}>
                    <i className="bi bi-ticket-perforated" style={{ fontSize: "10rem" }}></i>
                  </div>
                  <h3 className="h4 fw-bold mb-3">Reserva tu plaza</h3>
                  <p className="text-secondary small mb-4">Únete a nosotros en este evento único y accede a contenido exclusivo.</p>

                  {isPurchased ? (
                    <div className="alert alert-success d-flex align-items-center rounded-3 py-3 mb-0 border-0" style={{ backgroundColor: "#e6f4ea", color: "#1e8e3e" }}>
                      <i className="bi bi-check-circle-fill me-3 fs-3"></i>
                      <div>
                        <p className="fw-bold mb-0">¡Plaza Reservada!</p>
                        <p className="small mb-0">Ya tienes tu entrada para este evento.</p>
                      </div>
                    </div>
                  ) : (
                    <>
                      <div className="price-tag h1 fw-bold text-dark mb-4 d-flex align-items-center">
                        {event.priceEuros} <span className="h4 fw-normal text-muted mb-0 ms-1">€</span>
                      </div>
                      <Button
                        onClick={handleAddToCart}
                        className="btn-primary w-100 py-3 fw-bold fs-5 border-0 rounded-3 shadow-sm"
                        style={{ background: "#d96d3c", color: "white" }}
                      >
                        Comprar entrada
                      </Button>
                      <p className="mt-3 mb-0 text-center text-muted small">
                        Plazas disponibles: <strong>{event.capacity - ((event as any).attendeesCount || 0)}</strong>
                      </p>
                      <div className="guarantee mt-4 pt-3 border-top d-flex align-items-center justify-content-center gap-2 small text-muted">
                        <i className="bi bi-shield-check text-accent" style={{ color: "var(--accent-color)" }}></i>
                        Confirmación inmediata por email
                      </div>
                    </>
                  )}
                </div>

                <div className="service-features-list p-4 bg-white rounded-4 shadow-sm border-0 mb-4">
                  <h4 className="h6 fw-bold text-uppercase text-muted mb-4 border-bottom pb-2">Detalles clave</h4>
                  <ul className="list-unstyled mb-0 d-grid gap-4">
                    <li className="d-flex align-items-start gap-3">
                      <i className="bi bi-broadcast fs-4" style={{ color: "var(--accent-color)" }}></i>
                      <div>
                        <h5 className="h6 fw-bold mb-1">Formato</h5>
                        <p className="text-muted small mb-0">{event.locationName || "Presencial"}</p>
                      </div>
                    </li>
                    <li className="d-flex align-items-start gap-3">
                      <i className="bi bi-tags fs-4" style={{ color: "var(--accent-color)" }}></i>
                      <div>
                        <h5 className="h6 fw-bold mb-1">Categoría</h5>
                        <div className="d-flex flex-wrap gap-1 mt-1">
                          {event.tags?.map((tag: any) => (
                            <span key={tag.name} className="badge bg-light text-dark fw-normal border">{tag.name}</span>
                          ))}
                        </div>
                      </div>
                    </li>
                  </ul>
                </div>

                <div className="contact-info p-4 bg-white rounded-4 shadow-sm border-0" style={{ background: "linear-gradient(135deg, #ffffff 0%, #fef8f5 100%)" }}>
                  <h4 className="h6 fw-bold text-uppercase text-muted mb-4 border-bottom pb-2">Organización</h4>
                  <div className="contact-method d-flex align-items-center gap-3 mb-3">
                    <i className="bi bi-envelope fs-5 text-accent" style={{ color: "var(--accent-color)" }}></i>
                    <div>
                      <span className="text-muted extra-small d-block fw-bold text-uppercase">Email</span>
                      <p className="mb-0 small fw-medium">eventos@scam.com</p>
                    </div>
                  </div>
                  <div className="contact-method d-flex align-items-center gap-3">
                    <i className="bi bi-telephone fs-5 text-accent" style={{ color: "var(--accent-color)" }}></i>
                    <div>
                      <span className="text-muted extra-small d-block fw-bold text-uppercase">Teléfono</span>
                      <p className="mb-0 small fw-medium">+34 91 123 45 67</p>
                    </div>
                  </div>
                </div>
              </aside>
            </Col>
          </Row>
        </Container>
      </section>
    </main>
  );
}
