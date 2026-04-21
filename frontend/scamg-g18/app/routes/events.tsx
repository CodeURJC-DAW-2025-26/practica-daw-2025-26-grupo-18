import { useState, useEffect } from "react";
import { Container, Row, Col, Form, InputGroup, Button, Badge, Spinner, Alert } from "react-bootstrap";
import { Link, useSearchParams } from "react-router";
import { getEvents } from "~/services/eventService";
import { getGlobalData } from "~/services/globalService";
import { getEventImageUrl } from "~/utils/imageUrls";
import type { GlobalDataDTO } from "~/dtos/GlobalDataDTO";

export default function EventsPage() {
  const [searchParams, setSearchParams] = useSearchParams();
  const [events, setEvents] = useState<Record<string, any>[]>([]);
  const [globalData, setGlobalData] = useState<GlobalDataDTO | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(false);
  const [search, setSearch] = useState(searchParams.get("search") ?? "");

  const PAGE_SIZE = 10;

  const fetchEvents = async (currentPage: number, currentSearch: string, append: boolean) => {
    try {
      const data = await getEvents(currentPage, currentSearch || undefined);
      if (append) {
        setEvents((prev) => [...prev, ...data]);
      } else {
        setEvents(data);
      }
      setHasMore(data.length === PAGE_SIZE);
    } catch {
      setError("Error al cargar los eventos. Inténtalo de nuevo.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    getGlobalData().then(setGlobalData).catch(() => {});
  }, []);

  useEffect(() => {
    setLoading(true);
    setPage(0);
    fetchEvents(0, search, false);
  }, []);

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setPage(0);
    setSearchParams(search ? { search } : {});
    fetchEvents(0, search, false);
  };

  const handleLoadMore = () => {
    const nextPage = page + 1;
    setPage(nextPage);
    fetchEvents(nextPage, search, true);
  };

  return (
    <>
      {/* Page Title */}
      <div className="py-4 bg-light border-bottom">
        <Container>
          <nav aria-label="breadcrumb">
            <ol className="breadcrumb mb-1">
              <li className="breadcrumb-item"><Link to="/">Inicio</Link></li>
              <li className="breadcrumb-item active">Eventos</li>
            </ol>
          </nav>
          <h1 className="mb-0">Eventos disponibles</h1>
        </Container>
      </div>

      {/* Catalog */}
      <section className="py-5">
        <Container>
          {/* Search & controls */}
          <Form onSubmit={handleSearch} className="mb-4">
            <div className="d-flex flex-wrap gap-3 align-items-center justify-content-between mb-3">
              <InputGroup style={{ maxWidth: "480px" }}>
                <InputGroup.Text><i className="bi bi-search" /></InputGroup.Text>
                <Form.Control
                  type="search"
                  placeholder="Buscar eventos..."
                  value={search}
                  onChange={(e) => setSearch(e.target.value)}
                />
                <Button variant="primary" type="submit">Buscar</Button>
              </InputGroup>

              {globalData?.canCreateEvent && (
                <Link to="/events/new" className="btn btn-success">
                  <i className="bi bi-plus-circle me-2" />Crear Nuevo Evento
                </Link>
              )}
            </div>
          </Form>

          {/* Content */}
          {loading ? (
            <div className="text-center py-5">
              <Spinner animation="border" variant="primary" />
            </div>
          ) : error ? (
            <Alert variant="danger">{error}</Alert>
          ) : events.length === 0 ? (
            <p className="text-muted">No hay eventos disponibles en este momento.</p>
          ) : (
            <>
              <div className="d-flex flex-column gap-3" id="eventsList">
                {events.map((event) => (
                  <article key={event.id} className="card shadow-sm">
                    <Row className="g-0">
                      <Col md={3}>
                        <img
                          src={getEventImageUrl(event.id)}
                          alt={event.title}
                          className="img-fluid rounded-start h-100 object-fit-cover"
                          style={{ minHeight: "140px" }}
                        />
                      </Col>
                      <Col md={9}>
                        <div className="card-body h-100 d-flex flex-column">
                          <div className="d-flex justify-content-between align-items-start mb-2">
                            <div>
                              <h5 className="card-title mb-1">{event.title}</h5>
                              <div className="d-flex flex-wrap gap-1 mb-2">
                                {event.tags?.map((tag: any) => (
                                  <Badge key={tag.name} bg="secondary">{tag.name}</Badge>
                                ))}
                              </div>
                            </div>
                            <div className="text-end">
                              {event.isSubscribed && (
                                <Badge bg="success" className="mb-1 d-block">Entrada comprada</Badge>
                              )}
                              <span className="fw-bold fs-5">{event.priceEuros}€</span>
                            </div>
                          </div>

                          <p className="card-text text-muted flex-grow-1">{event.description}</p>

                          <div className="d-flex flex-wrap gap-3 align-items-center justify-content-between mt-2">
                            <div className="d-flex gap-3 text-muted small">
                              <span><i className="bi bi-calendar-event me-1" />{event.formattedDate}</span>
                              <span><i className="bi bi-clock me-1" />{event.formattedTime}</span>
                              <span>
                                <i className="bi bi-geo-alt me-1" />
                                {event.locationName ?? "Online"}
                              </span>
                            </div>
                            <Link to={`/events/${event.id}`} className="btn btn-outline-primary btn-sm">
                              Ver detalles
                            </Link>
                          </div>
                        </div>
                      </Col>
                    </Row>
                  </article>
                ))}
              </div>

              {hasMore && (
                <div className="text-center mt-4">
                  <Button variant="primary" size="lg" onClick={handleLoadMore}>
                    Cargar más eventos
                  </Button>
                </div>
              )}
            </>
          )}
        </Container>
      </section>
    </>
  );
}
