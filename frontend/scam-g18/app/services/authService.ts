import { loadGlobalDataIntoStore } from "~/services/globalService";
import { apiFetch } from "~/services/apiClient";

type AuthStatus = "SUCCESS" | "FAILURE";

export type AuthResponse = {
  status: AuthStatus;
  message?: string;
  error?: string;
};

type LoginRequest = {
  username: string;
  password: string;
};

export type RegisterRequest = {
  username: string;
  email: string;
  password: string;
  gender: "MALE" | "FEMALE" | "PREFER_NOT_TO_SAY";
  birthDate: string;
  country: string;
  image?: File;
};

const BASE_URL = "/api/v1/auth";

export async function login(request: LoginRequest): Promise<AuthResponse> {
  const response = await apiFetch(`${BASE_URL}/login`, {
    method: "POST",
    credentials: "include",
    skipRefreshRetry: true,
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(request),
  });

  const body = (await response.json().catch(() => ({}))) as AuthResponse;

  if (!response.ok) {
    throw new Error(body.message || body.error || `Error en login (${response.status})`);
  }

  // Sync stores with latest user data
  await loadGlobalDataIntoStore(true);

  return body;
}

export async function logout(): Promise<void> {
  const response = await apiFetch(`${BASE_URL}/logout`, {
    method: "POST",
    credentials: "include",
    skipRefreshRetry: true,
  });

  if (!response.ok) {
    throw new Error(`Error en logout (${response.status})`);
  }

  // Sync stores with latest user data
  await loadGlobalDataIntoStore(true);
}

export async function register(request: RegisterRequest): Promise<AuthResponse> {
  const formData = new FormData();
  formData.append("username", request.username);
  formData.append("email", request.email);
  formData.append("password", request.password);
  formData.append("gender", request.gender);
  formData.append("birthDate", request.birthDate);
  formData.append("country", request.country);
  if (request.image) {
    formData.append("image", request.image);
  }

  const response = await apiFetch(`${BASE_URL}/register`, {
    method: "POST",
    credentials: "include",
    skipRefreshRetry: true,
    body: formData,
  });

  const body = (await response.json().catch(() => ({}))) as AuthResponse;
  if (!response.ok) {
    throw new Error(body.message || body.error || `Error en registro (${response.status})`);
  }

  // Sync stores with latest user data
  await loadGlobalDataIntoStore(true);

  return body;
}