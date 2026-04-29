import { createDbConnection } from './db';

const cars = [
  ['EV3', 'SUV', '스노우 화이트 펄, 오로라 블랙 펄, 어벤처 그린', 39900000, '3,990만원', '81.4kWh', 501, '31분', '10%에서 80% 급속 충전', '도심과 장거리 주행을 모두 고려한 실속형 전기 SUV입니다.', '/images/cars/EV3_1.jpg', '/images/cars/EV3_2.jpg', '/images/cars/EV3_3.jpg'],
  ['EV3-GT', 'SUV', '런웨이 레드, 인터스텔라 그레이, 오션 블루', 45900000, '4,590만원', '84.0kWh', 485, '29분', '고출력 급속 충전 지원', '스포티한 주행 감각을 더한 EV3 고성능 모델입니다.', '/images/cars/EV3_GT_1.jpg', '/images/cars/EV3_GT_2.jpg', '/images/cars/EV3_GT_3.jpg'],
  ['EV4', '세단', '스노우 화이트 펄, 그래비티 블루, 스틸 그레이', 42900000, '4,290만원', '78.0kWh', 530, '30분', '장거리 효율 중심 충전 설계', '출퇴근과 장거리 이동에 균형 잡힌 전기 세단입니다.', '/images/cars/EV4_1.jpg', '/images/cars/EV4_2.jpg', '/images/cars/EV4_3.jpg'],
  ['EV4-GT', '세단', '딥 포레스트, 오로라 블랙, 문스케이프 매트', 49900000, '4,990만원', '84.0kWh', 510, '28분', '고속 충전 최적화', '주행 안정성과 민첩한 가속을 강조한 EV4 GT입니다.', '/images/cars/EV4_GT_1.jpg', '/images/cars/EV4_GT_2.jpg', '/images/cars/EV4_GT_3.jpg'],
  ['EV5', 'SUV', '아이보리 실버, 오션 블루, 블랙 펄', 52900000, '5,290만원', '88.1kWh', 540, '31분', '패밀리 장거리 충전 최적화', '가족 이동과 적재 공간을 고려한 중형 전기 SUV입니다.', '/images/cars/EV5_1.jpg', '/images/cars/EV5_2.jpg', '/images/cars/EV5_3.jpg'],
  ['EV5-GT', 'SUV', '울프 그레이, 런웨이 레드, 블랙 펄', 58900000, '5,890만원', '92.0kWh', 520, '27분', '고성능 급속 충전', '넉넉한 공간과 강한 주행 성능을 함께 갖춘 EV5 GT입니다.', '/images/cars/EV5_GT_1.jpg', '/images/cars/EV5_GT_2.jpg', '/images/cars/EV5_GT_3.jpg'],
  ['EV6', 'SUV', '스노우 화이트 펄, 요트 블루, 오로라 블랙', 55400000, '5,540만원', '77.4kWh', 475, '18분', '초급속 충전 지원', '기아 전기차의 대표 모델로 역동적인 디자인과 효율을 갖췄습니다.', '/images/cars/EV6_1.jpg', '/images/cars/EV6_2.jpg', '/images/cars/EV6_3.jpg'],
  ['EV6-GT', 'SUV', '문스케이프 매트, 런웨이 레드, 블랙 펄', 72000000, '7,200만원', '77.4kWh', 420, '18분', '초급속 충전 지원', '강력한 가속과 퍼포먼스를 제공하는 고성능 전기 SUV입니다.', '/images/cars/EV6_GT_1.jpg', '/images/cars/EV6_GT_2.jpg', '/images/cars/EV6_GT_3.jpg'],
  ['EV9', 'SUV', '페블 그레이, 아이보리 매트 실버, 오로라 블랙', 73370000, '7,337만원', '99.8kWh', 501, '24분', '대용량 배터리 급속 충전', '넓은 실내와 장거리 주행에 특화된 대형 전기 SUV입니다.', '/images/cars/EV9_1.jpg', '/images/cars/EV9_2.jpg', '/images/cars/EV9_3.jpg'],
  ['EV9-GT', 'SUV', '오션 매트 블루, 블랙 펄, 화이트 펄', 83900000, '8,390만원', '99.8kWh', 480, '24분', '대용량 초급속 충전', '대형 SUV의 공간에 고성능 주행 감각을 더한 모델입니다.', '/images/cars/EV9_GT_1.jpg', '/images/cars/EV9_GT_2.jpg', '/images/cars/EV9_GT_3.jpg'],
  ['Ray EV', '경형', '클리어 화이트, 밀키 베이지, 아스트로 그레이', 27750000, '2,775만원', '35.2kWh', 205, '40분', '도심형 충전', '도심 주행과 짧은 이동에 적합한 컴팩트 전기차입니다.', '/images/cars/RayEV_1.jpg', '/images/cars/RayEV_2.jpg', '/images/cars/RayEV_3.jpg'],
];

export async function seedE2eData() {
  const db = await createDbConnection();
  const now = new Date();
  const start = '2026-01-01 00:00:00';
  const end = '2026-12-31 23:59:59';

  try {
    await db.query('SET FOREIGN_KEY_CHECKS=0');
    await db.query('DELETE FROM consult_tbl');
    await db.query('DELETE FROM BOARD_TBL');
    await db.query('DELETE FROM CHARGING_TBL');
    await db.query('DELETE FROM PROMOTION_TBL');
    await db.query('DELETE FROM CAR_TBL');
    await db.query('SET FOREIGN_KEY_CHECKS=1');

    for (const car of cars) {
      await db.query(
        `INSERT INTO CAR_TBL
          (model_name, car_type, car_color, price, price_display, battery_capacity, driving_range_km,
           fast_charge_time, charge_info, car_description, image_path, image_detail1, image_detail2,
           sale_status, created_at)
         VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, '판매중', ?)`,
        [...car, now],
      );
    }

    const promotions = [
      ['DISCOUNT', 'EV 봄맞이 페스타 이벤트', '봄 출고 고객을 위한 특별 구매 혜택과 충전 지원을 제공합니다.', 'EV6', 3000000, '/images/promotion/EV_봄맞이_페스타이벤트.jpg'],
      ['PACKAGE', 'EV9 가족 감사 이벤트', '가족 단위 장거리 이동을 위한 케어 패키지와 차량 관리 혜택을 제공합니다.', 'EV9', null, '/images/promotion/EV9_가족감사이벤트.jpg'],
      ['VOUCHER', '기아 커넥트 스토어 1년 무료 이용권', '전기차 고객의 커넥티드 라이프를 위한 이용권 증정 프로모션입니다.', 'EV5', null, '/images/promotion/기아_커넥트_스토어_1년_무료_이용권.jpg'],
    ];

    for (const promo of promotions) {
      const [type, title, content, targetModel, discountAmount, bannerImageUrl] = promo;
      await db.query(
        `INSERT INTO PROMOTION_TBL
          (type, title, content, target_model, discount_amount, start_date, end_date, banner_order,
           banner_image_url, is_active, created_at, updated_at)
         VALUES (?, ?, ?, ?, ?, ?, ?, 1, ?, true, ?, ?)`,
        [type, title, content, targetModel, discountAmount, start, end, bannerImageUrl, now, now],
      );
    }

    const boards = Array.from({ length: 12 }, (_, index) => [
      'INQUIRY',
      null,
      `EV 상담 문의 테스트 게시글 ${index + 1}`,
      `출퇴근과 주말 장거리 이동을 함께 고려하고 있습니다. 충전 패턴과 추천 차량을 자세히 안내받고 싶습니다. (${index + 1})`,
      'N',
      '답변대기',
      '',
      0,
      'N',
      'N',
      'N',
      0,
      now,
      now,
    ]);

    for (const board of boards) {
      await db.query(
        `INSERT INTO BOARD_TBL
          (board_type, member_no, title, content, is_pinned, inquiry_status, answer_content,
           view_count, deleted_yn, hidden, NOTICE_YN, PRIORITY, created_at, updated_at)
         VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`,
        board,
      );
    }

    const chargers = [
      ['SEOUL-001', '서울시청 전기차 급속충전소', '서울특별시 중구 세종대로 110', '지하주차장 B2', '서울 도심', 37.5665, 126.9780, '24시간'],
      ['SEOUL-002', '광화문 공영주차장 충전소', '서울특별시 종로구 세종대로 172', '공영주차장 1층', '광화문 인근', 37.5720, 126.9769, '06:00-23:00'],
      ['SEOUL-003', '한강공원 여의도 충전소', '서울특별시 영등포구 여의동로 330', '제2주차장', '한강공원', 37.5283, 126.9326, '24시간'],
      ['DAEGU-001', '팔공산 국립공원 전기차 충전소', '대구광역시 동구 팔공산로 185길', '방문자센터 옆', '팔공산 국립공원', 35.9907, 128.6951, '08:00-22:00'],
      ['DAEGU-002', '동대구역 환승주차장 충전소', '대구광역시 동구 동대구로 550', '환승주차장', '동대구역', 35.8797, 128.6285, '24시간'],
    ];

    for (const charger of chargers) {
      await db.query(
        `INSERT INTO CHARGING_TBL
          (stat_id, stat_nm, addr, addr_detail, location_desc, lat, lng, use_time)
         VALUES (?, ?, ?, ?, ?, ?, ?, ?)`,
        charger,
      );
    }
  } finally {
    await db.end();
  }
}
