import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: {
      '/api/courses': {
        target: 'http://localhost:8083',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      },
      '/api/exams': {
        target: 'http://localhost:8084',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      },
      '/courses': {
        target: 'http://localhost:8083',
        changeOrigin: true
      },
      '/exams': {
        target: 'http://localhost:8084',
        changeOrigin: true
      }
    }
  },
})
