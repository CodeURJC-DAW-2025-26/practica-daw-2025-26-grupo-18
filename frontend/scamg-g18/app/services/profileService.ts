import type { ProfileDTO, ProfileUpdateDTO } from "~/dtos/ProfileDTO";

const BASE_URL = "/api/v1/profile";

export async function getMyProfileId(): Promise<number> {
  const res = await fetch(`${BASE_URL}/me`);
  if (!res.ok) throw new Error(`Error fetching current profile id: ${res.status}`);
  const body = (await res.json()) as { userId: number };
  return body.userId;
}

export async function getProfileById(id: number): Promise<ProfileDTO> {
  const res = await fetch(`${BASE_URL}/${id}`);
  if (!res.ok) throw new Error(`Error fetching profile ${id}: ${res.status}`);

  // Backend serializes boolean records like isProfileOwner as profileOwner in JSON.
  const raw = (await res.json()) as ProfileDTO & { isProfileOwner?: boolean };
  return {
    ...raw,
    profileOwner: raw.profileOwner ?? raw.isProfileOwner ?? false,
    userTags: Array.isArray(raw.userTags) ? raw.userTags : [],
    completedCourseNames: Array.isArray(raw.completedCourseNames) ? raw.completedCourseNames : [],
    subscribedCourses: Array.isArray(raw.subscribedCourses) ? raw.subscribedCourses : [],
    userEvents: Array.isArray(raw.userEvents) ? raw.userEvents : [],
    createdCourses: Array.isArray(raw.createdCourses) ? raw.createdCourses : [],
  };
}

function appendOptional(formData: FormData, key: string, value?: string | null): void {
  if (value !== undefined && value !== null) {
    formData.append(key, value);
  }
}

export async function updateProfile(
  id: number,
  profile: ProfileUpdateDTO,
  imageFile?: File
): Promise<void> {
  if (imageFile) {
    const formData = new FormData();
    formData.append("username", profile.username);
    formData.append("email", profile.email);
    appendOptional(formData, "country", profile.country);
    appendOptional(formData, "shortDescription", profile.shortDescription);
    appendOptional(formData, "currentGoal", profile.currentGoal);
    appendOptional(formData, "weeklyRoutine", profile.weeklyRoutine);
    appendOptional(formData, "comunity", profile.comunity);
    formData.append("imageFile", imageFile);

    const res = await fetch(`${BASE_URL}/${id}`, {
      method: "PUT",
      body: formData,
    });

    if (!res.ok) {
      const body = (await res.json().catch(() => ({}))) as { message?: string };
      throw new Error(body.message || `Error updating profile ${id}: ${res.status}`);
    }

    return;
  }

  const res = await fetch(`${BASE_URL}/${id}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(profile),
  });

  if (!res.ok) {
    const body = (await res.json().catch(() => ({}))) as { message?: string };
    throw new Error(body.message || `Error updating profile ${id}: ${res.status}`);
  }
}
