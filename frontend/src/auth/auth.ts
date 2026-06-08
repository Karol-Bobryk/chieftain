import axios from "axios";

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
  return data;
};
