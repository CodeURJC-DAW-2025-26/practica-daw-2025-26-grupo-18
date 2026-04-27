import { Container, Badge, Alert } from "react-bootstrap";
import { Link, useLoaderData } from "react-router";
import { getPurchasedEvents } from "~/services/eventService";
import { loadGlobalDataIntoStore } from "~/services/globalService";

type PurchasedEventsLoaderData = {
  events: Array<Record<string, any>>;
};

export async function clientLoader(): Promise<PurchasedEventsLoaderData> {
  await loadGlobalDataIntoStore();
  const events = await getPurchasedEvents();
  return { events };
}

clientLoader.hydrate = true;

export default function PurchasedEventsPage() {
  const { events } = useLoaderData<typeof clientLoader>();

  return (
    <main className="main">
      {/* Page Title */}
      <div className="page-title light-background mb-4">
        <Container>
          <nav className="breadcrumbs mb-2">
            <ol className="list-unstyled d-flex gap-2 m-0 p-0">
              <li><Link to="/new">Inicio</Link></li>
              <li className="current">Eventos comprados</li>
            </ol>
          </nav>
          <h1 className="m-0">Mis eventos comprados</h1>
        </Container>
      </div>

      {/* Catalog */}
      <section className="courses-catalog section py-4">
        <Container data-aos="fade-up">
          {/* Content */}
          {events.length === 0 ? (
            <div className="text-center py-5 bg-white rounded-3 shadow-sm border">
               <i className="bi bi-calendar-x display-4 text-muted mb-3 d-block"></i>
               <h3 className="text-dark fw-bold">No tienes entradas compradas</h3>
               <p className="text-muted fs-5">Descubre los próximos eventos y asegura tu plaza.</p>
               <Link to="/new/events" className="btn btn-primary mt-3 rounded-pill px-4">Explorar Eventos</Link>
            </div>
          ) : (
            <div className="course-list d-flex flex-column gap-4" id="eventsList">
              {events.map((event) => (
                <article key={event.id} className="course-card-full p-4 bg-white rounded-4 shadow-sm border-0 position-relative overflow-hidden">
                  <div className="course-card-header d-flex justify-content-between align-items-start mb-3">
                    <div>
                      <h3 className="course-card-title h4 fw-bold mb-2">
                        <Link to={`/new/events/${event.id}`} className="text-dark text-decoration-none">
                          {event.title}
                        </Link>
                      </h3>
                      <div className="course-tags d-flex flex-wrap gap-2">
                        {event.tags?.map((tag: any) => (
                          <span 
                            key={tag.name} 
                            className="badge bg-light text-dark fw-normal border rounded-pill px-3 py-1"
                            style={{ fontSize: "0.8rem" }}
                          >
                            <i className="bi bi-tag text-accent me-1" style={{ color: "var(--accent-color)" }}></i>{tag.name}
                          </span>
                        ))}
                      </div>
                    </div>
                    <div className="d-flex flex-column align-items-end gap-2">
                      <Badge bg="success" className="rounded-pill px-3 py-1 fw-medium">Entrada comprada</Badge>
                    </div>
                  </div>

                  <p className="course-card-desc text-muted mb-4 line-clamp-2">
                    {event.description}
                  </p>

                  <div className="course-card-meta d-flex flex-wrap align-items-center gap-4 text-muted small border-top pt-4 mt-auto">
                    <div className="d-flex align-items-center gap-1">
                      <i className="bi bi-calendar-event text-accent" style={{ color: "var(--accent-color)" }}></i>
                      <span>{event.formattedDate}</span>
                    </div>
                    <div className="d-flex align-items-center gap-1">
                      <i className="bi bi-clock text-accent" style={{ color: "var(--accent-color)" }}></i>
                      <span>{event.formattedTime}</span>
                    </div>
                    <div className="course-card-students">
                      <i className="bi bi-geo-alt me-1 text-accent" style={{ color: "var(--accent-color)" }}></i>
                      {event.locationName ?? "Online"}
                    </div>
                    <div className="ms-auto">
                      <Link 
                        to={`/new/events/${event.id}`} 
                        className="btn btn-outline-primary btn-sm px-4 fw-bold rounded-pill"
                        style={{ borderColor: "var(--accent-color)", color: "var(--accent-color)" }}
                      >
                        Ver detalles
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
