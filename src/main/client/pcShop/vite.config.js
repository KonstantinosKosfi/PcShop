import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'
import basicSsl from '@vitejs/plugin-basic-ssl'

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), 'VITE_')

  return {
    plugins: [
      react(),
      ...(env.VITE_HTTPS === 'false' ? [] : [basicSsl()]),
    ],
    server: {
      host: '127.0.0.1',
    },
  }
})
