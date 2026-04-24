/**
 * Utility functions to build image URLs served by the backend ImageController.
 * Use these directly in <img src={...} /> tags.
 *
 * Examples:
 *   <img src={getCourseImageUrl(course.id)} alt={course.title} />
 *   <img src={getEventImageUrl(event.id)} alt={event.title} />
 *   <img src={getUserProfileImageUrl(user.id)} alt={user.username} />
 */

const BASE = "/api/v1/images";

/** URL of the image for a course */
export function getCourseImageUrl(courseId: number): string {
  return `${BASE}/courses/${courseId}`;
}

/** URL of the image for an event */
export function getEventImageUrl(eventId: number): string {
  return `${BASE}/events/${eventId}`;
}

/** URL of the profile picture for a user */
export function getUserProfileImageUrl(userId: number): string {
  return `${BASE}/users/${userId}/profile`;
}
