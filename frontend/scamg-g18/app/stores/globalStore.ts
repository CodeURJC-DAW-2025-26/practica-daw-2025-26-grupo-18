import { create } from "zustand";

import type { GlobalDataDTO } from "~/dtos/GlobalDataDTO";

type GlobalStoreState = {
    globalData: GlobalDataDTO | null;
    authResolved: boolean;
    authLoading: boolean;
    pendingRequests: number;
    setGlobalData: (data: GlobalDataDTO | null) => void;
    clearGlobalData: () => void;
    setAuthLoading: (loading: boolean) => void;
    startRequest: () => void;
    endRequest: () => void;
};

export const useGlobalStore = create<GlobalStoreState>((set) => ({
    globalData: null,
    authResolved: false,
    authLoading: false,
    pendingRequests: 0,
    setGlobalData: (data) => set({ globalData: data, authResolved: true }),
    clearGlobalData: () => set({ globalData: null, authResolved: true }),
    setAuthLoading: (loading) => set({ authLoading: loading }),
    startRequest: () => set((state) => ({ pendingRequests: state.pendingRequests + 1 })),
    endRequest: () => set((state) => ({ pendingRequests: Math.max(0, state.pendingRequests - 1) })),
}));
