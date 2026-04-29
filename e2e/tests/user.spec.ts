import { test, expect } from '@playwright/test';
import { seedE2eData } from '../support/seed';
import { addDemoCookie, installChatbotAiMock } from '../support/demo';
import { login, signUp, uniqueUser } from '../support/user';

test.beforeAll(async () => {
  await seedE2eData();
});

test('user signup login mypage logout flow', async ({ page }) => {
  await installChatbotAiMock(page);
  await addDemoCookie(page);

  const user = uniqueUser();
  await signUp(page, user);
  await expect(page).toHaveURL(/\/login/);

  await login(page, user);
  await expect(page).toHaveURL(/\/main|\/board\/write|\/mypage/);

  await page.goto('/mypage/myinfo');
  await expect(page.locator('#loginId')).toHaveValue(user.loginId);

  await page.goto('/logout');
  await page.goto('/mypage/myinfo');
  await expect(page).toHaveURL(/\/login|\/session-expired/);
});

