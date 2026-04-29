import { expect, Page } from '@playwright/test';

export async function pause(page: Page, ms = 900) {
  await page.waitForTimeout(ms);
}

export async function slowScroll(page: Page, distance = 650, steps = 8) {
  const step = distance / steps;
  for (let i = 0; i < steps; i += 1) {
    await page.mouse.wheel(0, step);
    await page.waitForTimeout(180);
  }
}

export async function slowScrollUp(page: Page, distance = 300, steps = 5) {
  const step = distance / steps;
  for (let i = 0; i < steps; i += 1) {
    await page.mouse.wheel(0, -step);
    await page.waitForTimeout(180);
  }
}

export async function slowFill(page: Page, selector: string, value: string) {
  const locator = page.locator(selector);
  await locator.scrollIntoViewIfNeeded();
  await page.waitForTimeout(250);
  await locator.click();
  await locator.fill('');
  await locator.pressSequentially(value, { delay: 45 });
  await page.waitForTimeout(250);
}

export async function clickAndWait(page: Page, selector: string, ms = 700) {
  const locator = page.locator(selector).first();
  await locator.scrollIntoViewIfNeeded();
  await locator.click();
  await page.waitForTimeout(ms);
}

export async function acceptNextDialog(page: Page, delay = 1000) {
  page.removeAllListeners('dialog');
  page.once('dialog', async dialog => {
    await page.waitForTimeout(delay);
    if (!dialog.defaultValue || dialog.message() !== undefined) {
      await dialog.accept().catch(() => undefined);
    }
  });
}

export async function dismissNextDialog(page: Page, delay = 1000) {
  page.removeAllListeners('dialog');
  page.once('dialog', async dialog => {
    await page.waitForTimeout(delay);
    await dialog.dismiss().catch(() => undefined);
  });
}

export async function addDemoCookie(page: Page) {
  await page.context().addCookies([
    {
      name: 'hidePopup',
      value: 'true',
      domain: 'localhost',
      path: '/',
    },
  ]);
}

export async function installKakaoMapMock(page: Page) {
  await page.route(/dapi\.kakao\.com\/v2\/maps\/sdk\.js.*/, async route => {
    await route.fulfill({
      contentType: 'application/javascript',
      body: `
        window.kakao = { maps: {} };
        kakao.maps.LatLng = function(lat, lng) { this.lat = Number(lat); this.lng = Number(lng); };
        kakao.maps.Size = function(w, h) { this.w = w; this.h = h; };
        kakao.maps.MarkerImage = function(src, size) { this.src = src; this.size = size; };
        kakao.maps.LatLngBounds = function() { this.points = []; this.extend = p => this.points.push(p); this.contain = () => true; };
        kakao.maps.Map = function(container, options) {
          this.container = container;
          this.options = options;
          this.container.style.position = 'relative';
          this.setBounds = () => {};
          this.getBounds = () => ({ contain: () => true });
        };
        kakao.maps.Marker = function(options) {
          this.options = options || {};
          this.title = this.options.title || 'marker';
          this.listeners = {};
          this.getPosition = () => this.options.position;
          this.setMap = () => {};
        };
        kakao.maps.InfoWindow = function(options) {
          this.content = options.content;
          this.close = () => {
            const old = document.querySelector('[data-e2e-infowindow]');
            if (old) old.remove();
          };
          this.open = (map, marker) => {
            this.close();
            const info = document.createElement('div');
            info.setAttribute('data-e2e-infowindow', 'true');
            info.style.position = 'absolute';
            info.style.left = '24px';
            info.style.bottom = '24px';
            info.style.zIndex = '50';
            info.style.background = '#fff';
            info.style.border = '2px solid #007bff';
            info.style.borderRadius = '10px';
            info.style.boxShadow = '0 8px 24px rgba(0,0,0,.18)';
            info.innerHTML = this.content;
            map.container.appendChild(info);
          };
        };
        kakao.maps.event = {
          addListener(marker, event, handler) { marker.listeners[event] = handler; }
        };
        kakao.maps.MarkerClusterer = function(options) {
          this.map = options.map;
          this.clear = () => {
            Array.from(this.map.container.querySelectorAll('[data-e2e-marker]')).forEach(el => el.remove());
          };
          this.addMarkers = markers => {
            window.__e2eMarkers = markers;
            markers.forEach((marker, index) => {
              const btn = document.createElement('button');
              btn.type = 'button';
              btn.setAttribute('data-e2e-marker', String(index));
              btn.textContent = marker.title || '충전소';
              btn.style.position = 'absolute';
              btn.style.left = (40 + (index % 4) * 210) + 'px';
              btn.style.top = (80 + Math.floor(index / 4) * 70) + 'px';
              btn.style.zIndex = '20';
              btn.style.padding = '8px 12px';
              btn.style.border = '1px solid #007bff';
              btn.style.borderRadius = '999px';
              btn.style.background = '#fff';
              btn.style.color = '#007bff';
              btn.style.fontWeight = '700';
              btn.addEventListener('click', () => marker.listeners.click && marker.listeners.click());
              this.map.container.appendChild(btn);
            });
          };
        };
      `,
    });
  });
}

export async function clickChargingMarker(page: Page, name: string, fallbackIndex = 0) {
  const mockMarker = page.locator(`[data-e2e-marker]:has-text("${name}")`).first();
  if (await mockMarker.count()) {
    await mockMarker.click();
    return;
  }

  const titledMarker = page.locator(`#map [title*="${name}"], #map img[alt*="${name}"]`).first();
  if (await titledMarker.count()) {
    await titledMarker.click({ force: true });
    return;
  }

  const markerImages = page.locator('#map img[src*="marker"], #map img[title]');
  if (await markerImages.count()) {
    await markerImages.nth(Math.min(fallbackIndex, (await markerImages.count()) - 1)).click({ force: true });
    return;
  }

  const box = await page.locator('#map').boundingBox();
  if (box) {
    await page.mouse.click(box.x + box.width * 0.5, box.y + box.height * 0.45);
  }
}

export async function openMainChatWidget(page: Page) {
  await clickAndWait(page, '#aiWidgetButton');
  await page.locator('#aiWidgetPanel').evaluate(panel => {
    const button = document.querySelector<HTMLElement>('#aiWidgetButton');
    if (button) {
      button.style.display = 'none';
    }
    panel.scrollIntoView({ block: 'center' });
  });
  await page.locator('#aiWidgetInput').scrollIntoViewIfNeeded();
  await page.waitForTimeout(500);
}

export async function installChatbotAiMock(page: Page) {
  await page.route('**/api/v1/chatbot/ai/init', async route => {
    await route.fulfill({
      contentType: 'application/json',
      body: JSON.stringify({
        available: true,
        provider: 'E2E',
        greeting: '안녕하세요. KIA EV 상담 챗봇입니다. 차량 추천, 충전, 구매 상담을 도와드릴게요.',
        suggestedQuestions: ['배터리 용량 큰 차량 추천', '장거리 가족용 차량 추천', '충전소 찾는 방법'],
      }),
    });
  });

  await page.route('**/api/v1/chatbot/ai/message', async route => {
    const request = route.request();
    const body = request.postDataJSON?.() as { message?: string } | undefined;
    const message = body?.message || '';
    await route.fulfill({
      contentType: 'application/json',
      body: JSON.stringify({
        available: true,
        provider: 'E2E',
        answer: `${message}에 대한 추천입니다. 배터리 용량과 장거리 효율을 우선하면 EV9, EV5-GT, EV6를 비교해 보시는 것이 좋습니다. 가족 동승과 적재 공간이 중요하면 EV9, 예산과 효율 균형은 EV5를 추천드립니다.`,
        suggestedQuestions: ['구매 상담 연결', '충전 비용 안내', 'EV5-GT 상세 안내'],
      }),
    });
  });
}

export async function expectReady(page: Page) {
  await expect(page.locator('body')).toBeVisible();
  await page.waitForLoadState('networkidle').catch(() => undefined);
}
