import { Container, Row, Col, Badge, Tab, Nav, Button } from "react-bootstrap";
import { useLoaderData, useNavigate, Link } from "react-router";
import { getEventById } from "~/services/eventService";
import { addEventToCart } from "~/services/cartService";
import { getEventImageUrl } from "~/utils/imageUrls";
import { useGlobalStore } from "~/stores/globalStore";
import type { LoaderFunctionArgs } from "react-router";
import type { EventDTO } from "~/dtos/EventDTO";
import { useEffect, useRef } from "react";

// Helper para cargar scripts externos como hacía el main.js original
async function loadExternalScript(src: string, id: string): Promise<void> {
  return new Promise((resolve, reject) => {
    if (document.getElementById(id)) return resolve();
    const script = document.createElement("script");
    script.src = src;
    script.id = id;
    script.onload = () => resolve();
    script.onerror = () => reject();
    document.head.appendChild(script);
  });
}

async function loadExternalStyle(href: string, id: string): Promise<void> {
  if (document.getElementById(id)) return;
  const link = document.createElement("link");
  link.rel = "stylesheet";
  link.href = href;
  link.id = id;
  document.head.appendChild(link);
}

export async function clientLoader({ params }: LoaderFunctionArgs) {
  const id = Number(params.id);
  if (!Number.isFinite(id) || id <= 0) {
    throw new Response("Evento no valido", { status: 400 });
  }
  const event = await getEventById(id);
  return { event: event as EventDTO };
}
clientLoader.hydrate = true;

export default function EventDetail() {
  const { event } = useLoaderData<typeof clientLoader>();
  const navigate = useNavigate();
  const mapInstance = useRef<any>(null);
  const mapContainerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (!event.locationLatitude || !event.locationLongitude) return;

    async function initMap() {
      try {
        await loadExternalStyle("https://unpkg.com/leaflet@1.9.4/dist/leaflet.css", "leaflet-css");
        await loadExternalScript("https://unpkg.com/leaflet@1.9.4/dist/leaflet.js", "leaflet-js");

        const L = (window as any).L;
        if (!L || !mapContainerRef.current) return;

        // Limpiar mapa anterior si existe
        if (mapInstance.current) {
          mapInstance.current.remove();
          mapInstance.current = null;
        }

        const map = L.map(mapContainerRef.current).setView([event.locationLatitude, event.locationLongitude], 13);
        mapInstance.current = map;

        L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
          attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        }).addTo(map);

        L.marker([event.locationLatitude, event.locationLongitude])
          .addTo(map)
          .bindPopup(event.locationName || "Ubicación del evento")
          .openPopup();
        
      } catch (error) {
        console.error("Error al cargar el mapa:", error);
      }
    }

    initMap();

    return () => {
      if (mapInstance.current) {
        mapInstance.current.remove();
        mapInstance.current = null;
      }
    };
  }, [event.id, event.locationLatitude, event.locationLongitude]);

  const isPurchased = event.alreadyPurchased;
  const isOwner = event.canEdit;
  const canDelete = event.canDelete;

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
      {/* Page Title */}
      <div className="page-title light-background">
        <Container>
          <nav className="breadcrumbs">
            <ol>
              <li><Link to="/new">Inicio</Link></li>
              <li><Link to="/new/events">Eventos</Link></li>
              <li className="current">{event.title}</li>
            </ol>
          </nav>
        </Container>
      </div>

      {/* Event Details Section */}
      <section id="service-details" className="service-details section">
        <Container data-aos="fade-up" data-aos-delay="100">
          <Row className="gy-5">
            <Col lg={8} className="order-lg-1 order-2">
              <div className="service-main-content">
                <div className="d-flex justify-content-end mb-2 gap-2">
                  {isOwner && (
                    <Link to={`/new/events/${event.id}/edit`} className="btn btn-outline-secondary btn-sm">
                      <i className="bi bi-pencil me-1"></i> Editar evento
                    </Link>
                  )}
                  {canDelete && (
                    <button className="btn btn-outline-danger btn-sm"
                      onClick={async () => {
                        if (confirm('¿Estás seguro de eliminar este evento?')) {
                          try {
                            const { deleteEvent } = await import('~/services/eventService');
                            await deleteEvent(event.id);
                            navigate("/new/events");
                          } catch (error) {
                            alert("Error al eliminar el evento: " + (error instanceof Error ? error.message : String(error)));
                          }
                        }
                      }}>
                      <i className="bi bi-trash me-1"></i> Eliminar evento
                    </button>
                  )}
                </div>

                <div className="service-header" data-aos="fade-up">
                  <h1>{event.title}</h1>
                  <div className="service-meta">
                    <span><i className="bi bi-calendar-event"></i> {event.formattedDate}</span>
                    <span><i className="bi bi-clock"></i> {event.formattedTime}</span>
                    <span><i className="bi bi-geo-alt"></i> {event.locationName === 'Online' ? 'Online' : `${event.locationAddress}, ${event.locationCity}, ${event.locationCountry}`}</span>
                    <span><i className="bi bi-people"></i> {event.attendeesCount} / {event.capacity} plazas ocupadas</span>
                  </div>
                  <p className="lead">{event.description}</p>
                </div>

                <div className="service-tabs" data-aos="fade-up" data-aos-delay="200">
                  <Tab.Container defaultActiveKey="overview">
                    <Nav variant="tabs" id="eventTab" className="mb-3">
                      <Nav.Item>
                        <Nav.Link eventKey="overview">
                          <i className="bi bi-info-circle"></i> Resumen
                        </Nav.Link>
                      </Nav.Item>
                      <Nav.Item>
                        <Nav.Link eventKey="agenda">
                          <i className="bi bi-diagram-3"></i> Agenda
                        </Nav.Link>
                      </Nav.Item>
                      <Nav.Item>
                        <Nav.Link eventKey="speakers">
                          <i className="bi bi-mic"></i> Ponentes
                        </Nav.Link>
                      </Nav.Item>
                    </Nav>

                    <Tab.Content>
                      <Tab.Pane eventKey="overview">
                        <Row>
                          <Col md={6}>
                            <div className="content-block">
                              <h3>Sobre este evento</h3>
                              <p>{event.description}</p>
                            </div>
                          </Col>
                          <Col md={6}>
                            <img src={getEventImageUrl(event.id)} alt={event.title} className="img-fluid rounded" />
                          </Col>
                        </Row>
                      </Tab.Pane>

                      <Tab.Pane eventKey="agenda">
                        <div className="process-timeline">
                          {event.sessions && event.sessions.length > 0 ? (
                            event.sessions.map((session, idx) => (
                              <div key={idx} className="timeline-item">
                                <div className="timeline-marker">{session.time}</div>
                                <div className="timeline-content">
                                  <h4>{session.title}</h4>
                                  <p>{session.description}</p>
                                </div>
                              </div>
                            ))
                          ) : (
                            <p>Agenda no disponible.</p>
                          )}
                        </div>
                      </Tab.Pane>

                      <Tab.Pane eventKey="speakers">
                        <Row className="g-4">
                          {event.speakers && event.speakers.length > 0 ? (
                            event.speakers.map((name, idx) => (
                              <Col md={6} key={idx}>
                                <div className="benefit-card">
                                  <div className="benefit-icon">
                                    <i className="bi bi-person-circle"></i>
                                  </div>
                                  <h4>Ponente</h4>
                                  <p>{name}</p>
                                </div>
                              </Col>
                            ))
                          ) : (
                            <p>Ponentes por confirmar.</p>
                          )}
                        </Row>
                      </Tab.Pane>
                    </Tab.Content>
                  </Tab.Container>
                </div>

                {event.locationName !== 'Online' && (
                  <div className="service-gallery" data-aos="fade-up" data-aos-delay="300">
                    <h3>Ubicación del evento</h3>
                    <Row className="g-4 align-items-center">
                      <Col lg={5}>
                        <div className="content-block">
                          <h3>{event.locationName}</h3>
                          <p className="mb-2">{event.locationAddress}</p>
                          <p className="mb-2">{event.locationCity}, {event.locationCountry}</p>
                        </div>
                      </Col>
                      <Col lg={7}>
                        {/* Contenedor del mapa de Leaflet */}
                        <div ref={mapContainerRef} style={{ height: "300px", background: "#eee", borderRadius: "12px", overflow: "hidden" }}>
                        </div>
                      </Col>
                    </Row>
                  </div>
                )}
              </div>
            </Col>

            <Col lg={4} className="order-lg-2 order-1">
              <aside className="service-sidebar" data-aos="fade-up" data-aos-delay="200">
                <div className="action-card">
                  <h3>Reserva tu plaza</h3>
                  <p>Acceso completo al evento.</p>

                  {isPurchased ? (
                    <div className="purchase-status purchase-status-success" role="status">
                      <i className="bi bi-check-circle-fill"></i>
                      <span>Ya has comprado la entrada para este evento.</span>
                    </div>
                  ) : (
                    <>
                      {/* Lógica de Sold Out simplificada */}
                      <Button
                        onClick={handleAddToCart}
                        className="btn-primary w-100"
                        style={{ border: "none" }}
                      >
                        Comprar entrada · {event.priceEuros} €
                      </Button>
                    </>
                  )}

                  <p className="mt-2 mb-0"><small>Plazas disponibles: {event.capacity - (event.attendeesCount || 0)}</small></p>
                  <span className="guarantee"><i className="bi bi-shield-check"></i> Confirmación inmediata por email</span>
                </div>

                <div className="service-features-list">
                  <h4>Detalles clave</h4>
                  <ul>
                    <li>
                      <i className="bi bi-broadcast"></i>
                      <div>
                        <h5>Formato</h5>
                        <p>{event.locationName}</p>
                      </div>
                    </li>
                    <li>
                      <i className="bi bi-tags"></i>
                      <div>
                        <h5>Categoría</h5>
                        <div className="d-flex flex-wrap gap-1">
                          {event.tags?.map((tag: any) => (
                            <span key={tag.name} className="badge bg-light text-dark">{tag.name}</span>
                          ))}
                        </div>
                      </div>
                    </li>
                  </ul>
                </div>

                <div className="contact-info">
                  <h4>Organización</h4>
                  <div className="contact-method">
                    <i className="bi bi-envelope"></i>
                    <div>
                      <span>Email</span>
                      <p>eventos@scam.com</p>
                    </div>
                  </div>
                  <div className="contact-method">
                    <i className="bi bi-telephone"></i>
                    <div>
                      <span>Teléfono</span>
                      <p>+34 91 123 45 67</p>
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
