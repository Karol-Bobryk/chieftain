import axios from "axios";
import { useAuthStore } from "@/store/auth.store";
import { jwtDecode } from "jwt-decode";
import type { SystemRole } from "@/enums/SystemRoles";

export interface LoginRequest {
  emailAddress: string;
  password: string;
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
}
export interface TokenContent {
  email: string;
  role: SystemRole;
}

export const login = async (payload: LoginRequest): Promise<LoginResponse> => {
  const { data } = await axios.post<LoginResponse>("/auth/user/login", payload);
  useAuthStore.getState().setTokens(data);
  useAuthStore
    .getState()
    .setRole(jwtDecode<TokenContent>(data.accessToken).role);

  return data;
};
