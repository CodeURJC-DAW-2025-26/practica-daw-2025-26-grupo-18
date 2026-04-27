import { create } from "zustand";
import { logout as logoutService } from "~/services/authService";

export type AuthUser = {
  id: number;
  username: string;
  profileImage: string | null;
  canCreateEvent: boolean;
  canCreateCourse: boolean;
  isAdmin: boolean;
  isPublisher: boolean;
};

type AuthStoreState = {
  user: AuthUser | null;
  isLoading: boolean;
  isResolved: boolean;
  error: string | null;
  setUser: (user: AuthUser | null) => void;
  clearUser: () => void;
  setLoading: (loading: boolean) => void;
  setError: (error: string | null) => void;
  logout: () => Promise<void>;
  resolve: () => void;
  isLoggedIn: () => boolean;
};

export const useAuthStore = create<AuthStoreState>((set, get) => ({
  user: null,
  isLoading: false,
  isResolved: false,
  error: null,

  setUser: (user) => set({ user, isResolved: true, error: null }),

  clearUser: () => set({ user: null, isResolved: true, error: null }),

  setLoading: (loading) => set({ isLoading: loading }),

  setError: (error) => set({ error, isResolved: true }),

  logout: async () => {
    try {
      set({ isLoading: true });
      await logoutService();
      get().clearUser();
    } catch (error) {
      const message = error instanceof Error ? error.message : "Error al cerrar sesión";
      set({ error: message });
      throw error;
    } finally {
      set({ isLoading: false });
    }
  },

  resolve: () => set({ isResolved: true }),

  isLoggedIn: () => get().user !== null,
}));
