import { Container } from "react-bootstrap";
import { useNavigate } from "react-router";
import EventForm from "~/components/EventForm";
import { createEvent } from "~/services/eventService";
import type { EventDTO } from "~/dtos/EventDTO";

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
    <Container className="py-5">
      <div className="mb-4">
        <h2 className="display-5 text-primary">Organizar Nuevo Evento</h2>
        <p className="text-secondary">Define la agenda y ubicación para tu próximo evento presencial u online.</p>
      </div>
      <EventForm onSubmit={handleSubmit} />
    </Container>
  );
}
