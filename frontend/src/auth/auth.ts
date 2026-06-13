import axios from "axios";
import { useAuthStore } from "@/store/auth.store";
export interface LoginRequest {
  emailAddress: string;
  password: string;
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
}

export const login = async (payload: LoginRequest): Promise<LoginResponse> => {
  const { data } = await axios.post<LoginResponse>("/auth/user/login", payload);
  useAuthStore.getState().setTokens(data);
  return data;
};
