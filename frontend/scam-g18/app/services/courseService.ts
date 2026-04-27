import type { CourseDTO } from "~/dtos/CourseDTO";
import { apiFetch } from "~/services/apiClient";

const BASE_URL = "/api/v1/courses";

/**
 * GET /api/v1/courses/ — List paginated published courses
 */
export async function getCourses(
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

  const res = await apiFetch(`${BASE_URL}/?${params.toString()}`);
  if (!res.ok) throw new Error(`Error fetching courses: ${res.status}`);
  return res.json();
}

/**
 * GET /api/v1/courses/:id — Get course detail
 */
export async function getCourseById(id: number): Promise<Record<string, any>> {
  const res = await apiFetch(`${BASE_URL}/${id}`);
  if (!res.ok) throw new Error(`Error fetching course ${id}: ${res.status}`);
  return res.json();
}

/**
 * GET /api/v1/courses/subscribed — Courses subscribed by the authenticated user
 */
export async function getSubscribedCourses(): Promise<Record<string, any>[]> {
  const res = await apiFetch(`${BASE_URL}/subscribed`);
  if (!res.ok) throw new Error(`Error fetching subscribed courses: ${res.status}`);
  return res.json();
}

/**
 * GET /api/v1/courses/:courseId/lesson/:lessonId/video — Get lesson video URL
 */
export async function getLessonVideoUrl(
  courseId: number,
  lessonId: number
): Promise<{ videoUrl: string }> {
  const res = await apiFetch(`${BASE_URL}/${courseId}/lesson/${lessonId}/video`);
  if (!res.ok) throw new Error(`Error fetching lesson video: ${res.status}`);
  return res.json();
}

/**
 * POST /api/v1/courses/:courseId/lesson/:lessonId/complete — Mark lesson as completed
 */
export async function markLessonAsCompleted(
  courseId: number,
  lessonId: number
): Promise<Record<string, any>> {
  const res = await apiFetch(`${BASE_URL}/${courseId}/lesson/${lessonId}/complete`, {
    method: "POST",
  });
  if (!res.ok) throw new Error(`Error completing lesson: ${res.status}`);
  return res.json();
}

/**
 * POST /api/v1/courses/ — Create a new course (multipart)
 */
export async function createCourse(
  courseDTO: CourseDTO,
  tagNames?: string[],
  imageFile?: File
): Promise<CourseDTO> {
  const formData = new FormData();
  formData.append("course", new Blob([JSON.stringify(courseDTO)], { type: "application/json" }));
  if (tagNames && tagNames.length > 0) {
    tagNames.forEach((t) => formData.append("tagNames", t));
  }
  if (imageFile) {
    formData.append("imageFile", imageFile);
  }

  const res = await apiFetch(`${BASE_URL}/`, { method: "POST", body: formData });
  if (!res.ok) {
    const body = await res.json().catch(() => ({}));
    throw new Error(body.error || `Error creating course: ${res.status}`);
  }
  return res.json();
}

/**
 * PUT /api/v1/courses/:id — Update a course (multipart)
 */
export async function updateCourse(
  id: number,
  courseDTO: CourseDTO,
  tagNames?: string[],
  imageFile?: File
): Promise<CourseDTO> {
  const formData = new FormData();
  formData.append("course", new Blob([JSON.stringify(courseDTO)], { type: "application/json" }));
  if (tagNames && tagNames.length > 0) {
    tagNames.forEach((t) => formData.append("tagNames", t));
  }
  if (imageFile) {
    formData.append("imageFile", imageFile);
  }

  const res = await apiFetch(`${BASE_URL}/${id}`, { method: "PUT", body: formData });
  if (!res.ok) {
    const body = await res.json().catch(() => ({}));
    throw new Error(body.error || `Error updating course: ${res.status}`);
  }
  return res.json();
}

/**
 * DELETE /api/v1/courses/:id — Delete a course
 */
export async function deleteCourse(id: number): Promise<void> {
  const res = await apiFetch(`${BASE_URL}/${id}`, { method: "DELETE" });
  if (!res.ok) {
    const body = await res.json().catch(() => ({}));
    throw new Error(body.error || `Error deleting course: ${res.status}`);
  }
}

/**
 * POST /api/v1/courses/:id/review — Add a review to a course
 */
export async function addCourseReview(
  courseId: number,
  rating: number,
  content: string
): Promise<Record<string, any>> {
  const params = new URLSearchParams();
  params.set("rating", String(rating));
  params.set("content", content);

  const res = await apiFetch(`${BASE_URL}/${courseId}/review?${params.toString()}`, {
    method: "POST",
  });
  if (!res.ok) {
    const body = await res.json().catch(() => ({}));
    throw new Error(body.error || `Error adding review: ${res.status}`);
  }
  return res.json();
}
