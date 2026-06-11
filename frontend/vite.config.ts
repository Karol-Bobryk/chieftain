import { defineConfig } from "vite";
import react, { reactCompilerPreset } from "@vitejs/plugin-react";
import babel from "@rolldown/plugin-babel";
import path from "node:path";

// https://vite.dev/config/
export default defineConfig({
  server: {
    host: true,
    port: 3000,
    watch: { usePolling: true, interval: 100 },
    proxy: {
      "/api": {
        target: "http://backend:8080",
        changeOrigin: true,
      },
      "/auth": {
        target: "http://backend:8080",
        changeOrigin: true,
      },
    },
  },
  plugins: [react(), babel({ presets: [reactCompilerPreset()] })],
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "src"),
    },
  },
});
