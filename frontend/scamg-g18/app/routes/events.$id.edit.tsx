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
    <Container className="py-5">
      <div className="mb-4">
        <h2 className="display-5 text-primary">Editar Evento</h2>
        <p className="text-secondary">Actualiza los detalles de "{event.title}"</p>
      </div>
      <EventForm initialData={event} onSubmit={handleSubmit} />
    </Container>
  );
}
