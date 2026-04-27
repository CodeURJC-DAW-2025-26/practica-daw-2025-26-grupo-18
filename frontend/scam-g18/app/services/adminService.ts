import type {
  AdminCourseDTO,
  AdminEventDTO,
  AdminOrderDTO,
  AdminReviewDTO,
  AdminUserDTO,
} from "~/dtos/AdminDTO";
import { apiFetch } from "~/services/apiClient";

const BASE_URL = "/api/v1/admin";

function buildPageQuery(page: number, query?: string): string {
  const params = new URLSearchParams();
  params.set("page", String(page));
  if (query && query.trim()) {
    params.set("query", query.trim());
  }
  return params.toString();
}

async function noContentPut(url: string, body: unknown): Promise<void> {
  const res = await apiFetch(url, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(body),
  });

  if (!res.ok) {
    throw new Error(`Request failed: ${res.status}`);
  }
}

export async function getAdminUsers(page = 0, query?: string): Promise<AdminUserDTO[]> {
  const res = await apiFetch(`${BASE_URL}/users?${buildPageQuery(page, query)}`);
  if (!res.ok) throw new Error(`Error fetching users: ${res.status}`);
  return res.json();
}

export async function updateAdminUserStatus(id: number, active: boolean): Promise<void> {
  return noContentPut(`${BASE_URL}/users/${id}/status`, { active });
}

export async function getAdminCourses(page = 0, query?: string): Promise<AdminCourseDTO[]> {
  const res = await apiFetch(`${BASE_URL}/courses?${buildPageQuery(page, query)}`);
  if (!res.ok) throw new Error(`Error fetching courses: ${res.status}`);
  return res.json();
}

export async function updateAdminCourseStatus(id: number, status: "PUBLISHED" | "DRAFT"): Promise<void> {
  return noContentPut(`${BASE_URL}/courses/${id}/status`, { status });
}

export async function getAdminEvents(page = 0, query?: string): Promise<AdminEventDTO[]> {
  const res = await apiFetch(`${BASE_URL}/events?${buildPageQuery(page, query)}`);
  if (!res.ok) throw new Error(`Error fetching events: ${res.status}`);
  return res.json();
}

export async function updateAdminEventStatus(id: number, status: "PUBLISHED" | "DRAFT"): Promise<void> {
  return noContentPut(`${BASE_URL}/events/${id}/status`, { status });
}

export async function getAdminOrders(page = 0): Promise<AdminOrderDTO[]> {
  const params = new URLSearchParams();
  params.set("page", String(page));

  const res = await apiFetch(`${BASE_URL}/orders?${params.toString()}`);
  if (!res.ok) throw new Error(`Error fetching orders: ${res.status}`);
  return res.json();
}

export async function getAdminReviews(): Promise<AdminReviewDTO[]> {
  const res = await apiFetch(`${BASE_URL}/reviews`);
  if (!res.ok) throw new Error(`Error fetching reviews: ${res.status}`);
  return res.json();
}

export async function deleteAdminReview(id: number): Promise<void> {
  const res = await apiFetch(`${BASE_URL}/reviews/${id}`, { method: "DELETE" });
  if (!res.ok) throw new Error(`Error deleting review ${id}: ${res.status}`);
}
