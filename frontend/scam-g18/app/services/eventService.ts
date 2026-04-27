import type { EventDTO } from "~/dtos/EventDTO";

const BASE_URL = "/api/v1/events";

/**
 * Helper para normalizar los datos del evento que vienen del backend.
 * El backend envía 'locationLat' y 'locationLon' en los GET, pero espera 
 * 'locationLatitude' y 'locationLongitude' en los POST/PUT.
 * El frontend usa los nombres largos en sus DTOs y componentes.
 */
function normalizeEvent(event: any): any {
  if (!event) return event;
  return {
    ...event,
    locationLatitude: event.locationLatitude ?? event.locationLat,
    locationLongitude: event.locationLongitude ?? event.locationLon
  };
}

/**
 * GET /api/v1/events/ — List paginated published events
 */
export async function getEvents(
  page: number = 0,
  search?: string,
  tags?: string[]
): Promise<Record<string, any>[]> {
  const params = new URLSearchParams();
  params.set("page", String(page));
  if (search) params.set("search", search);
  if (tags && tags.length > 0) {
    tags.forEach((t) => params.append("tags", t));
  }

  const res = await fetch(`${BASE_URL}/?${params.toString()}`);
  if (!res.ok) throw new Error(`Error fetching events: ${res.status}`);
  const data = await res.json();
  return Array.isArray(data) ? data.map(normalizeEvent) : data;
}

/**
 * GET /api/v1/events/:id — Get event detail
 */
export async function getEventById(id: number): Promise<Record<string, any>> {
  const res = await fetch(`${BASE_URL}/${id}`);
  if (!res.ok) throw new Error(`Error fetching event ${id}: ${res.status}`);
  const data = await res.json();
  return normalizeEvent(data);
}

/**
 * GET /api/v1/events/purchased — Events purchased by the authenticated user
 */
export async function getPurchasedEvents(): Promise<Record<string, any>[]> {
  const res = await fetch(`${BASE_URL}/purchased`);
  if (!res.ok) throw new Error(`Error fetching purchased events: ${res.status}`);
  const data = await res.json();
  return Array.isArray(data) ? data.map(normalizeEvent) : data;
}

/**
 * GET /api/v1/events/location-search?q= — Search locations via geocoding
 */
export async function searchLocations(query: string): Promise<string> {
  const params = new URLSearchParams({ q: query });
  const res = await fetch(`${BASE_URL}/location-search?${params.toString()}`);
  if (!res.ok) throw new Error(`Error searching locations: ${res.status}`);
  return res.json();
}

/**
 * POST /api/v1/events/ — Create a new event (multipart)
 */
export async function createEvent(
  eventDTO: EventDTO,
  tagNames?: string[],
  imageFile?: File
): Promise<EventDTO> {
  const formData = new FormData();
  formData.append("event", new Blob([JSON.stringify(eventDTO)], { type: "application/json" }));
  if (tagNames && tagNames.length > 0) {
    tagNames.forEach((t) => formData.append("tagNames", t));
  }
  if (imageFile) {
    formData.append("imageFile", imageFile);
  }

  const res = await fetch(`${BASE_URL}/`, { method: "POST", body: formData });
  if (!res.ok) {
    const body = await res.json().catch(() => ({}));
    throw new Error(body.error || `Error creating event: ${res.status}`);
  }
  return res.json();
}

/**
 * PUT /api/v1/events/:id — Update an event (multipart)
 */
export async function updateEvent(
  id: number,
  eventDTO: EventDTO,
  tagNames?: string[],
  imageFile?: File
): Promise<EventDTO> {
  const formData = new FormData();
  formData.append("event", new Blob([JSON.stringify(eventDTO)], { type: "application/json" }));
  if (tagNames && tagNames.length > 0) {
    tagNames.forEach((t) => formData.append("tagNames", t));
  }
  if (imageFile) {
    formData.append("imageFile", imageFile);
  }

  const res = await fetch(`${BASE_URL}/${id}`, { method: "PUT", body: formData });
  if (!res.ok) {
    const body = await res.json().catch(() => ({}));
    throw new Error(body.error || `Error updating event: ${res.status}`);
  }
  return res.json();
}

/**
 * DELETE /api/v1/events/:id — Delete an event
 */
export async function deleteEvent(id: number): Promise<void> {
  const res = await fetch(`${BASE_URL}/${id}`, { method: "DELETE" });
  if (!res.ok) {
    const body = await res.json().catch(() => ({}));
    throw new Error(body.error || `Error deleting event: ${res.status}`);
  }
}
