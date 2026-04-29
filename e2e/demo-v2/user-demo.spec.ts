import { test, expect } from '@playwright/test';
import { seedE2eData } from '../support/seed';
import {
  acceptNextDialog,
  addDemoCookie,
  clickAndWait,
  clickChargingMarker,
  dismissNextDialog,
  expectReady,
  installChatbotAiMock,
  installKakaoMapMock,
  openMainChatWidget,
  pause,
  slowFill,
  slowScroll,
  slowScrollUp,
} from '../support/demo';
import { login, signUp, uniqueUser } from '../support/user';

test.describe.configure({ mode: 'serial' });

const member = uniqueUser();
const boardTitle = `EV 상담 문의 ${Date.now()}`;

test.beforeAll(async () => {
  await seedE2eData();
});

test.beforeEach(async ({ page }) => {
  await installChatbotAiMock(page);
  if (process.env.E2E_USE_MAP_MOCK === 'true') {
    await installKakaoMapMock(page);
  }
  await addDemoCookie(page);
  await page.context().grantPermissions(['geolocation']);
  await page.context().setGeolocation({ latitude: 37.5665, longitude: 126.9780 });
});

test('비회원 사용자 시연 @guest', async ({ page }) => {
  await page.goto('/main?lang=kr');
  await expectReady(page);
  await slowScroll(page, 1500, 10);

  await openMainChatWidget(page);
  await slowFill(page, '#aiWidgetInput', '베터리 용량 큰 차량 추천');
  await page.locator('#aiWidgetInput').press('Enter');
  await pause(page, 1800);
  await slowScrollUp(page, 260, 4);
  await slowScroll(page, 260, 4);

  await page.goto('/car/list');
  await slowFill(page, 'input[name="keyword"]', 'ev3');
  await clickAndWait(page, '.btn-search');
  await slowScroll(page, 650, 6);
  await clickAndWait(page, '.car-card:has-text("EV3") a');
  await slowScroll(page, 760, 7);
  const thumbs = page.locator('.thumb');
  for (let i = 0; i < await thumbs.count(); i += 1) {
    await thumbs.nth(i).click();
    await pause(page, 600);
  }
  await acceptNextDialog(page, 1000);
  await clickAndWait(page, '.btn-main', 1200);
  await expect(page).toHaveURL(/\/login/);

  await page.goto('/board/list');
  const pages = page.locator('div[style*="text-align: center"] a').filter({ hasText: /\d+/ });
  const pageCount = Math.min(await pages.count(), 3);
  for (let i = 0; i < pageCount; i += 1) {
    await pages.nth(i).click();
    await pause(page, 500);
  }
  await clickAndWait(page, 'td.title-td a');
  await slowScroll(page, 500, 5);
  await clickAndWait(page, '.btn-list');
  await clickAndWait(page, 'a.btn');
  await expect(page).toHaveURL(/\/login/);
  await pause(page, 1000);

  await page.goto('/promotion/list');
  await slowScroll(page, 900, 8);
  await clickAndWait(page, '.promotion-card');
  await slowScroll(page, 900, 8);

  await page.goto('/charging/map');
  await slowFill(page, '#keyword', '서울');
  await clickAndWait(page, '#searchBtn', 900);
  const markerCount = Math.min(await page.locator('[data-e2e-marker]').count(), 3);
  for (let i = 0; i < markerCount; i += 1) {
    await page.locator('[data-e2e-marker]').nth(0).click();
    await pause(page, 450);
  }
  await clickChargingMarker(page, '서울시청', 0);
  await pause(page, 2000);
  await acceptNextDialog(page, 1000);
  await clickAndWait(page, '#myLocationBtn', 1200);
  await acceptNextDialog(page, 1000);
  await clickAndWait(page, '#myLocationBtn', 1200);
  await clickAndWait(page, '#searchInViewBtn');
  await clickAndWait(page, '#resetBtn');
  await page.goBack();
  await expectReady(page);

  await page.goto('/chatbot/main');
  await clickAndWait(page, '#categoryRow .chip:has-text("구매")', 1200);
  await slowScrollUp(page, 300, 5);
  await clickAndWait(page, '#carRow .chip:has-text("EV5-GT")', 1200);
  await slowScrollUp(page, 240, 4);
  await acceptNextDialog(page, 1000);
  await clickAndWait(page, '.consult-link', 1300);
  await expect(page).toHaveURL(/\/login/);
});

test('회원 사용자 시연 @member', async ({ page }) => {
  await signUp(page, member);
  await expect(page).toHaveURL(/\/login/);
  await login(page, member);
  await expectReady(page);

  await page.goto('/board/list');
  await clickAndWait(page, 'a.btn');
  await slowFill(page, '#title', boardTitle);
  await slowFill(page, '#content', '출퇴근은 왕복 70km 정도이고 주말에는 부모님을 모시고 장거리 이동을 자주 합니다. 배터리 용량, 실내 공간, 충전 편의성을 기준으로 EV4와 EV5 중 어떤 차량이 맞을지 상담받고 싶습니다.');
  await clickAndWait(page, '.btn-submit', 1200);
  await clickAndWait(page, `td.title-td a:has-text("${boardTitle}")`);
  await clickAndWait(page, '.btn-edit');
  await slowFill(page, '#editTitle', `${boardTitle} 수정`);
  await slowFill(page, '#editContent', '수정 내용입니다. 장거리 주행 시 고속도로 급속 충전 빈도와 가족 탑승 시 2열 승차감을 함께 비교해 보고 싶습니다.');
  await clickAndWait(page, '.btn-submit', 1200);
  await slowScroll(page, 600, 6);

  await page.goto('/car/list');
  await slowFill(page, 'input[name="keyword"]', 'ev4');
  await clickAndWait(page, '.btn-search');
  await slowScroll(page, 650, 6);
  await clickAndWait(page, '.car-card:has-text("EV4") a');
  await slowScroll(page, 760, 7);
  const thumbs = page.locator('.thumb');
  for (let i = 0; i < await thumbs.count(); i += 1) {
    await thumbs.nth(i).click();
    await pause(page, 600);
  }
  await acceptNextDialog(page, 1000);
  await clickAndWait(page, '#wishBtn', 1200);
  await clickAndWait(page, '.btn-main', 1200);
  await expect(page).toHaveURL(/\/consult\/formPage/);
  await slowFill(page, '#budgetAmount', '5500');
  await page.locator('#usePurpose').selectOption({ index: 1 });
  await slowScroll(page, 220, 4);
  await page.locator('#mainRangeKm').selectOption({ index: 1 });
  await page.locator('#fellowData').selectOption({ index: 2 });
  await slowScroll(page, 260, 4);
  await slowFill(page, '#consultContent', '평일에는 서울 도심 출퇴근, 주말에는 대구와 부산 방문이 잦습니다. 성인 3명이 함께 타는 경우가 많아 뒷좌석 공간과 장거리 충전 동선을 중심으로 상담을 받고 싶습니다.');
  await slowScroll(page, 240, 4);
  await acceptNextDialog(page, 1000);
  await clickAndWait(page, '.submit-btn', 1200);

  await page.goto('/mypage/myinfo');
  await slowScroll(page, 1100, 9);
  await clickAndWait(page, 'a[href="/mypage/consult"]');
  await clickAndWait(page, '.detail-btn');
  await slowScroll(page, 700, 7);
  await clickAndWait(page, '.list-btn');
  await clickAndWait(page, 'a[href="/mypage/board"]');
  await clickAndWait(page, '.detail-btn');
  await slowScroll(page, 650, 6);
  await acceptNextDialog(page, 1000);
  await clickAndWait(page, '.btn-delete', 1200);
  await page.goto('/board/list');
  const pages = page.locator('div[style*="text-align: center"] a').filter({ hasText: /\d+/ });
  const pageCount = Math.min(await pages.count(), 3);
  for (let i = 0; i < pageCount; i += 1) {
    await pages.nth(i).click();
    await pause(page, 500);
  }

  await page.goto('/promotion/list');
  await slowScroll(page, 900, 8);
  await clickAndWait(page, '.promotion-card');

  await page.goto('/charging/map');
  await slowFill(page, '#keyword', '대구');
  await clickAndWait(page, '#searchBtn', 900);
  await clickChargingMarker(page, '팔공산', 0);
  await pause(page, 2000);
  await acceptNextDialog(page, 1000);
  await clickAndWait(page, '#myLocationBtn', 1200);
  await acceptNextDialog(page, 1000);
  await clickAndWait(page, '#myLocationBtn', 1200);
  await clickAndWait(page, '#searchInViewBtn');
  await clickAndWait(page, '#resetBtn');
  await page.goBack();
  await expectReady(page);

  await page.goto('/chatbot/main');
  await clickAndWait(page, '#categoryRow .chip:has-text("충전")', 1200);
  await slowScrollUp(page, 300, 5);
  await clickAndWait(page, '#categoryRow .chip:has-text("차량 안내")', 1200);
  await slowScrollUp(page, 240, 4);
  await clickAndWait(page, '#carRow .chip:has-text("EV5-GT")', 1200);

  await page.goto('/main?lang=kr');
  await slowScroll(page, 900, 7);
  await openMainChatWidget(page);
  await slowFill(page, '#aiWidgetInput', '베터리 용량 큰 차량 추천');
  await page.locator('#aiWidgetInput').press('Enter');
  await pause(page, 1800);
  await slowScrollUp(page, 260, 4);
  await slowScroll(page, 260, 4);
});
