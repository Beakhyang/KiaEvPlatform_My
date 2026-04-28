import { defineConfig, devices } from '@playwright/test';
import dotenv from 'dotenv';

dotenv.config({ path: '.env.e2e' });

const port = process.env.E2E_PORT || process.env.PORT || '8080';
process.env.PORT = port;

export default defineConfig({
  testDir: './demo-v2',
  timeout: 240_000,
  expect: { timeout: 15_000 },
  fullyParallel: false,
  workers: 1,
  reporter: [
    ['list'],
    ['html', { outputFolder: '../test-v2/report', open: 'never' }],
  ],
  outputDir: '../test-v2/artifacts',
  use: {
    ...devices['Desktop Chrome'],
    baseURL: `http://localhost:${port}`,
    viewport: { width: 1440, height: 920 },
    launchOptions: {
      slowMo: 180,
    },
    trace: 'on',
    screenshot: 'on',
    video: {
      mode: 'on',
      size: { width: 1440, height: 920 },
    },
    actionTimeout: 20_000,
    navigationTimeout: 45_000,
  },
  webServer: {
    command: '.\\gradlew.bat bootRun',
    cwd: '..',
    url: `http://localhost:${port}/main`,
    reuseExistingServer: true,
    timeout: 180_000,
  },
  projects: [
    {
      name: 'user-demo',
      use: { ...devices['Desktop Chrome'] },
    },
  ],
});
