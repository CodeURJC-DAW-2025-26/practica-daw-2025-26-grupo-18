import { Container } from "react-bootstrap";
import { useNavigate } from "react-router";
import EventForm from "~/components/EventForm";
import { createEvent } from "~/services/eventService";
import type { EventDTO } from "~/dtos/EventDTO";

import { Link } from "react-router";

export default function NewEvent() {
  const navigate = useNavigate();

  const handleSubmit = async (data: EventDTO, imageFile?: File) => {
    try {
      const newEvent = await createEvent(data, [], imageFile);
      navigate(`/new/events/${newEvent.id}`);
    } catch (error) {
      console.error(error);
      alert("Error al crear el evento: " + (error instanceof Error ? error.message : String(error)));
    }
  };

  return (
    <main className="main">
      <div className="page-title light-background mb-4">
        <Container>
          <nav className="breadcrumbs mb-2">
            <ol className="list-unstyled d-flex gap-2 m-0 p-0 small">
              <li><Link to="/new">Inicio</Link></li>
              <li><Link to="/new/events">Eventos</Link></li>
              <li className="current text-muted">Crear Evento</li>
            </ol>
          </nav>
          <h1 className="m-0 h2 fw-bold">Crear Nuevo Evento</h1>
        </Container>
      </div>

      <section className="section py-4">
        <Container>
          <EventForm onSubmit={handleSubmit} />
        </Container>
      </section>
    </main>
  );
}
