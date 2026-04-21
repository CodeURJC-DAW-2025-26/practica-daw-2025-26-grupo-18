import { Container } from "react-bootstrap";
import { useNavigate, useLoaderData } from "react-router";
import EventForm from "~/components/EventForm";
import { getEventById, updateEvent } from "~/services/eventService";
import type { EventDTO } from "~/dtos/EventDTO";
import type { ClientLoaderArgs } from "react-router";

export async function clientLoader({ params }: ClientLoaderArgs) {
  const id = Number(params.id);
  const event = await getEventById(id);
  return { event: event as unknown as EventDTO };
}

import { Link } from "react-router";

export default function EditEvent() {
  const { event } = useLoaderData<typeof clientLoader>();
  const navigate = useNavigate();

  const handleSubmit = async (data: EventDTO, imageFile?: File) => {
    try {
      await updateEvent(event.id, data, [], imageFile);
      navigate(`/new/events/${event.id}`);
    } catch (error) {
      console.error(error);
      alert("Error al actualizar el evento: " + (error instanceof Error ? error.message : String(error)));
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
              <li className="current text-muted">Editar Evento</li>
            </ol>
          </nav>
          <h1 className="m-0 h2 fw-bold">Editar Evento</h1>
        </Container>
      </div>

      <section className="section py-4">
        <Container>
          <EventForm initialData={event} onSubmit={handleSubmit} />
        </Container>
      </section>
    </main>
  );
}
