import os from "node:os";
import path from "node:path";
import { reactRouter } from "@react-router/dev/vite";
import tailwindcss from "@tailwindcss/vite";
import { defineConfig } from "vite";

export default defineConfig({
  plugins: [tailwindcss(), reactRouter()],
  // Keep Vite temp cache out of OneDrive-synced folders to reduce EPERM locks on Windows.
  cacheDir: path.join(os.tmpdir(), "scamg-g18-vite-cache"),
  resolve: {
    tsconfigPaths: true,
  },
  server: {
    proxy: {
      '/api': {
        target: 'https://localhost:8443',
        changeOrigin: true,
        secure: false
      }
    }
  }
});
