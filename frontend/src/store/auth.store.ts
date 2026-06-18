import { SystemRole } from "@/enums/SystemRoles";
import { create } from "zustand";
import { persist } from "zustand/middleware";

type AuthState = {
  accessToken: string | null;
  refreshToken: string | null;
  role: SystemRole;
  setTokens: (tokens: { accessToken: string; refreshToken: string }) => void;
  setAccessToken: (token: string) => void;
  setRole: (role: SystemRole) => void;
  clearTokens: () => void;
};

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      accessToken: null,
      refreshToken: null,
      role: SystemRole.GUEST,

      setTokens: (tokens) =>
        set({
          accessToken: tokens.accessToken,
          refreshToken: tokens.refreshToken,
        }),

      setAccessToken: (token) =>
        set({
          accessToken: token,
        }),

      setRole: (role: SystemRole) =>
        set({
          role: role,
        }),

      clearTokens: () =>
        set({
          accessToken: null,
          refreshToken: null,
          role: SystemRole.GUEST,
        }),
    }),

    { name: "authStorage" },
  ),
);
