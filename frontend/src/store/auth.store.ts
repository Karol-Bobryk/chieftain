import { create } from "zustand";
import { persist } from "zustand/middleware";

type AuthState = {
  accessToken: string | null;
  refreshToken: string | null;

  setTokens: (tokens: { accessToken: string; refreshToken: string }) => void;
  setAccessToken: (token: string) => void;
  clearTokens: () => void;
};

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      accessToken: null,
      refreshToken: null,

      setTokens: (tokens) =>
        set({
          accessToken: tokens.accessToken,
          refreshToken: tokens.refreshToken,
        }),

      setAccessToken: (token) =>
        set({
          accessToken: token,
        }),

      clearTokens: () =>
        set({
          accessToken: null,
          refreshToken: null,
        }),
    }),

    { name: "authStorage" },
  ),
);
