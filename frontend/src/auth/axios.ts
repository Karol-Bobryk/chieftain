import axios, { AxiosError, type InternalAxiosRequestConfig } from "axios";
import { useAuthStore } from "@/store/auth.store";
import type { SystemRole } from "@/enums/SystemRoles";

interface RefreshResponse {
  accessToken: string;
  refreshToken: string;
  systemRole: SystemRole;
}

const api = axios.create({
  baseURL: "/",
  withCredentials: true,
  timeout: 10000,
});

api.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const token = useAuthStore.getState().accessToken;

  if (token && config.headers) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

let isRefreshing = false;
let failedQueue: {
  resolve: (value?: unknown) => void;
  reject: (error?: unknown) => void;
}[] = [];

const processQueue = (error: unknown, token: string | null = null) => {
  failedQueue.forEach((prom) => {
    if (error) {
      prom.reject(error);
    } else {
      prom.resolve(token);
    }
  });

  failedQueue = [];
};

api.interceptors.response.use(
  (res) => res,
  async (error: AxiosError) => {
    const originalRequest = error.config as any;

    if (error.response?.status !== 401 || originalRequest._retry) {
      return Promise.reject(error);
    }

    if (isRefreshing) {
      return new Promise((resolve, reject) => {
        failedQueue.push({ resolve, reject });
      })
        .then((token) => {
          originalRequest.headers.Authorization = `Bearer ${token}`;
          return api(originalRequest);
        })
        .catch(Promise.reject);
    }

    originalRequest._retry = true;
    isRefreshing = true;

    try {
      const refresh = useAuthStore.getState().refreshToken;

      if (!refresh) throw new Error("No refresh token");

      const res = await axios.post<RefreshResponse>("/auth/token/refresh", {
        refreshToken: refresh,
      });

      const newAccessToken = res.data.accessToken;

      useAuthStore.getState().setTokens(res.data);

      api.defaults.headers.common.Authorization = `Bearer ${newAccessToken}`;

      processQueue(null, newAccessToken);

      originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;

      return api(originalRequest);
    } catch (err) {
      processQueue(err, null);
      useAuthStore.getState().clearTokens();
      window.location.href = "/home";
      return Promise.reject(err);
    } finally {
      isRefreshing = false;
    }
  },
);
export { api, type RefreshResponse };
