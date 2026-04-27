import type { GlobalDataDTO } from "~/dtos/GlobalDataDTO";
import { useGlobalStore } from "~/stores/globalStore";
import { useAuthStore, type AuthUser } from "~/stores/authStore";
import { publicAsset } from "~/utils/publicAsset";

const BASE_URL = "/api/v1";

function normalizeUserProfileImage(path?: string | null): string {
  if (!path) return publicAsset("services/default_avatar.png");

  if (path === "/img/default_avatar.png") {
    return publicAsset("services/default_avatar.png");
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

function mapGlobalDataToAuthUser(data: GlobalDataDTO): AuthUser | null {
  if (!data.isUserLoggedIn || !data.userId || !data.userName) {
    return null;
  }

  return {
    id: data.userId,
    username: data.userName,
    profileImage: normalizeUserProfileImage(data.userProfileImage),
    canCreateEvent: data.canCreateEvent,
    canCreateCourse: data.canCreateCourse,
    isAdmin: data.isAdmin,
    isPublisher: data.isPublisher,
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

// Sync stores with latest user data
export async function loadGlobalDataIntoStore(force = false): Promise<GlobalDataDTO | null> {
  const globalState = useGlobalStore.getState();
  const authState = useAuthStore.getState();

  if (!force && (globalState.authResolved || globalState.authLoading)) {
    return globalState.globalData;
  }

  globalState.setAuthLoading(true);
  try {
    const data = await getGlobalData();
    globalState.setGlobalData(data);

    // Sync auth store with user information
    const authUser = mapGlobalDataToAuthUser(data);
    if (authUser) {
      authState.setUser(authUser);
    } else {
      authState.clearUser();
    }
    authState.resolve();

    return data;
  } catch (error) {
    globalState.clearGlobalData();
    authState.clearUser();
    authState.resolve();
    return null;
  } finally {
    globalState.setAuthLoading(false);
  }
}
