import { Container } from "react-bootstrap";
import { useNavigate, useLoaderData } from "react-router";
import EventForm from "~/components/EventForm";
import { getEventById, updateEvent } from "~/services/eventService";
import type { EventDTO } from "~/dtos/EventDTO";
import type { LoaderFunctionArgs } from "react-router";

import { loadGlobalDataIntoStore } from "~/services/globalService";
import { redirect } from "react-router";

function toDateParts(value?: string) {
  if (!value) {
    return { date: "", time: "" };
  }

  const parsed = new Date(value);
  if (Number.isNaN(parsed.getTime())) {
    return { date: "", time: "" };
  }

  const date = parsed.toISOString().slice(0, 10);
  const time = parsed.toTimeString().slice(0, 5);
  return { date, time };
}

function normalizeEventForEdit(event: any): EventDTO {
  const startParts = toDateParts(event.startDate);
  const endParts = toDateParts(event.endDate);

  return {
    ...event,
    price: event.price ?? (event.priceCents != null ? event.priceCents / 100 : 0),
    priceCents: event.priceCents ?? 0,
    capacity: event.capacity ?? 50,
    category: event.category ?? "Networking",
    startDateStr: event.startDateStr ?? startParts.date,
    startTimeStr: event.startTimeStr ?? startParts.time,
    endDateStr: event.endDateStr ?? endParts.date,
    endTimeStr: event.endTimeStr ?? endParts.time,
    locationName: event.locationName ?? "",
    locationAddress: event.locationAddress ?? "",
    locationCity: event.locationCity ?? "Madrid",
    locationCountry: event.locationCountry ?? "España",
    locationLatitude: event.locationLatitude ?? event.locationLat ?? 40.4168,
    locationLongitude: event.locationLongitude ?? event.locationLon ?? -3.7038,
    speakerNames: event.speakerNames ?? event.speakers ?? [""],
    sessionTitles: event.sessionTitles ?? event.sessions?.map((s: any) => s.title) ?? [""],
    sessionTimes: event.sessionTimes ?? event.sessions?.map((s: any) => s.time) ?? [""],
    sessionDescriptions:
      event.sessionDescriptions ?? event.sessions?.map((s: any) => s.description) ?? [""],
  } as EventDTO;
}

export async function clientLoader({ params }: LoaderFunctionArgs) {
  const globalData = await loadGlobalDataIntoStore();
  if (!globalData?.isUserLoggedIn) {
    return redirect("/new/login");
  }
  if (!globalData?.canCreateEvent && !globalData?.isAdmin) {
    return redirect(`/new/error?message=${encodeURIComponent("Necesitas tener el plan de creador para editar eventos.")}`);
  }

  const id = Number(params.id);
  if (!Number.isFinite(id) || id <= 0) {
    throw new Response("Evento no valido", { status: 400 });
  }
  const event = await getEventById(id);

  if (!event.canEdit && !globalData?.isAdmin) {
    return redirect(`/new/error?message=${encodeURIComponent("No tienes permiso para editar este evento. Solo el creador del evento o un administrador pueden hacerlo.")}`);
  }

  return { event: normalizeEventForEdit(event) };
}
clientLoader.hydrate = true;

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
