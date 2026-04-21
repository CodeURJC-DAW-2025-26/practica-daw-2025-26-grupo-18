import { useGlobalStore } from "~/stores/globalStore";

type ApiRequestInit = RequestInit & {
  skipRefreshRetry?: boolean;
};

const REFRESH_ENDPOINT = "/api/v1/auth/refresh";

function isRefreshRequest(input: RequestInfo | URL): boolean {
  const url = typeof input === "string" ? input : input instanceof URL ? input.pathname : input.url;
  return url.includes(REFRESH_ENDPOINT);
}

async function tryRefreshToken(): Promise<boolean> {
  try {
    const response = await fetch(REFRESH_ENDPOINT, {
      method: "POST",
      credentials: "include",
    });

    if (!response.ok) {
      return false;
    }

    const body = (await response.json().catch(() => ({}))) as { status?: string };
    return body.status === "SUCCESS";
  } catch {
    return false;
  }
}

export async function apiFetch(input: RequestInfo | URL, init: ApiRequestInit = {}): Promise<Response> {
  const { skipRefreshRetry = false, ...requestInit } = init;
  const { startRequest, endRequest } = useGlobalStore.getState();

  startRequest();
  try {
    const response = await fetch(input, {
      credentials: "include",
      ...requestInit,
    });

    if (response.status !== 401 || skipRefreshRetry || isRefreshRequest(input)) {
      return response;
    }

    const refreshed = await tryRefreshToken();
    if (!refreshed) {
      return response;
    }

    return fetch(input, {
      credentials: "include",
      ...requestInit,
    });
  } finally {
    endRequest();
  }
}