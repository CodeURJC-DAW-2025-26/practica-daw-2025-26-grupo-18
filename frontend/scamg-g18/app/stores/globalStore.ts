import { create } from "zustand";

import type { GlobalDataDTO } from "~/dtos/GlobalDataDTO";
import { getGlobalData } from "~/services/globalService";

type GlobalStoreState = {
    globalData: GlobalDataDTO | null;
    fetchGlobalData: () => Promise<void>;
};

export const useGlobalStore = create<GlobalStoreState>((set, get) => ({
    globalData: null,
    fetchGlobalData: async () => {
        if (get().globalData) return;

        try {
            const data = await getGlobalData();
            set({ globalData: data });
        } catch {
            set({ globalData: null });
        }
    },
}));
