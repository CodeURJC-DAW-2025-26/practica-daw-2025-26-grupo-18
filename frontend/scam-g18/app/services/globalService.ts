import type { GlobalDataDTO } from "~/dtos/GlobalDataDTO";
import { useGlobalStore } from "~/stores/globalStore";

const BASE_URL = "/api/v1";

function normalizeUserProfileImage(path?: string | null): string {
  if (!path) return "/services/default_avatar.png";

  if (path === "/img/default_avatar.png") {
    return "/services/default_avatar.png";
  }

  if (path.startsWith("/images/")) {
    return `/api/v1${path}`;
  }

  return path;
}

function normalizeGlobalData(data: GlobalDataDTO): GlobalDataDTO {
  return {
    ...data,
    userProfileImage: normalizeUserProfileImage(data.userProfileImage),
  };
}

/**
 * GET /api/v1/global — Returns global session and permissions data
 */
export async function getGlobalData(): Promise<GlobalDataDTO> {
  const res = await fetch(`${BASE_URL}/global`, { credentials: "include" });
  if (!res.ok) throw new Error(`Error fetching global data: ${res.status}`);
  const data = (await res.json()) as GlobalDataDTO;
  return normalizeGlobalData(data);
}

export async function loadGlobalDataIntoStore(force = false): Promise<GlobalDataDTO | null> {
  const state = useGlobalStore.getState();

  if (!force && (state.authResolved || state.authLoading)) {
    return state.globalData;
  }

  state.setAuthLoading(true);
  try {
    const data = await getGlobalData();
    useGlobalStore.getState().setGlobalData(data);
    return data;
  } catch {
    useGlobalStore.getState().clearGlobalData();
    return null;
  } finally {
    useGlobalStore.getState().setAuthLoading(false);
  }
}
