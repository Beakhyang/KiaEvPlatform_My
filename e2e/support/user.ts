import { Page } from '@playwright/test';
import { acceptNextDialog, clickAndWait, pause, slowFill, slowScroll } from './demo';

export function uniqueUser() {
  const timestamp = Date.now();
  const id = timestamp.toString(36);
  const phoneTail = String(timestamp % 100000000).padStart(8, '3');
  return {
    loginId: `e2euser${id}`,
    password: 'Test1234!',
    name: '김민준',
    birthDate: '1990-05-14',
    email: `e2euser${id}@example.com`,
    phone: `010${phoneTail}`,
    zipcode: '04524',
    address: '서울특별시 중구 세종대로 110',
    detailAddress: '테스트빌딩 1203호',
  };
}

export async function signUp(page: Page, user: ReturnType<typeof uniqueUser>) {
  await page.goto('/member/terms');
  await clickAndWait(page, '#allCheck');
  await slowScroll(page, 900);
  await clickAndWait(page, '#nextBtn');

  await slowFill(page, '#loginId', user.loginId);
  await clickAndWait(page, 'button[onclick="checkDuplicateId()"]');
  await slowFill(page, '#memberPw', user.password);
  await slowFill(page, '#confirmPw', user.password);
  await slowFill(page, 'input[name="memberName"]', user.name);
  await slowFill(page, '#birthDate', user.birthDate);
  await slowScroll(page, 360, 5);
  await slowFill(page, '#email', user.email);
  await clickAndWait(page, 'button[onclick="checkEmailDuplicate()"]');
  await slowFill(page, '#phone', user.phone);
  await slowScroll(page, 420, 6);

  await page.evaluate(({ zipcode, address }) => {
    const zip = document.querySelector<HTMLInputElement>('#zipcode');
    const addr = document.querySelector<HTMLInputElement>('#address');
    if (zip && addr) {
      zip.value = zipcode;
      addr.value = address;
      zip.dispatchEvent(new Event('input', { bubbles: true }));
      addr.dispatchEvent(new Event('input', { bubbles: true }));
      addr.dispatchEvent(new Event('change', { bubbles: true }));
    }
  }, user);
  await pause(page, 500);
  await slowFill(page, '#detailAddress', user.detailAddress);
  await slowScroll(page, 260, 4);

  await acceptNextDialog(page, 1000);
  await clickAndWait(page, '.btn-submit', 1200);
}

export async function login(page: Page, user: ReturnType<typeof uniqueUser>) {
  await page.goto('/login');
  await slowFill(page, '#loginIdInput', user.loginId);
  await slowFill(page, '#passwordInput', user.password);
  await clickAndWait(page, '.login-btn', 1200);
}
