import type { ChartDataDTO } from "../dtos/ChartDTO";
import { apiFetch } from "./apiClient";

const BASE_URL = "/api/v1/statistics";

export async function getCourseProgress(userId: number): Promise<ChartDataDTO> {
    const res = await apiFetch(`${BASE_URL}/course-progress?userId=${userId}`);
    if (!res.ok) throw new Error("Error fetching course progress");
    return res.json();
}

export async function getLessonsLearned(userId: number): Promise<ChartDataDTO> {
    const res = await apiFetch(`${BASE_URL}/lessons-learned?userId=${userId}`);
    if (!res.ok) throw new Error("Error fetching lessons learned");
    return res.json();
}

export async function getCourseGenders(courseId: number): Promise<ChartDataDTO> {
    const res = await apiFetch(`${BASE_URL}/course-genders?courseId=${courseId}`);
    if (!res.ok) throw new Error("Error fetching course genders");
    return res.json();
}

export async function getCourseAges(courseId: number): Promise<ChartDataDTO> {
    const res = await apiFetch(`${BASE_URL}/course-ages?courseId=${courseId}`);
    if (!res.ok) throw new Error("Error fetching course ages");
    return res.json();
}

export async function getCourseTags(userId: number, courseId: number): Promise<ChartDataDTO> {
    const res = await apiFetch(`${BASE_URL}/course-tags?userId=${userId}&courseId=${courseId}`);
    if (!res.ok) throw new Error("Error fetching course tags");
    return res.json();
}

export async function getCourseUserProgress(courseId: number, userId: number): Promise<ChartDataDTO> {
    const res = await apiFetch(`${BASE_URL}/course-user-progress?courseId=${courseId}&userId=${userId}`);
    if (!res.ok) throw new Error("Error fetching course user progress");
    return res.json();
}

export async function getCreatedCourseStatus(courseId: number): Promise<ChartDataDTO> {
    const res = await apiFetch(`${BASE_URL}/created-course-status?courseId=${courseId}`);
    if (!res.ok) throw new Error("Error fetching created course status");
    return res.json();
}
