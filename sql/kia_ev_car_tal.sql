/*
  Kia EV Project - Database Full Setup Script
  기능: 테이블 생성 + 11개 차종 데이터
*/

CREATE DATABASE IF NOT EXISTS `kiaevdb` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `kiaevdb`;

-- 1. 기존 테이블 삭제 (초기화)
DROP TABLE IF EXISTS `car_tbl`;

-- 2. 테이블 구조 생성
CREATE TABLE `car_tbl` (
  `car_no` bigint NOT NULL AUTO_INCREMENT COMMENT '차량번호',
  `model_name` varchar(100) NOT NULL COMMENT '모델명',
  `car_type` varchar(30) NOT NULL,
  `car_color` varchar(500) NOT NULL,
  `price` bigint NOT NULL COMMENT '가격',
  `price_display` varchar(100) NOT NULL DEFAULT '0원',
  `battery_capacity` varchar(50) NOT NULL COMMENT '배터리용량',
  `driving_range_km` int NOT NULL COMMENT '1회충전주행거리(km)',
  `fast_charge_time` varchar(50) DEFAULT NULL COMMENT '급속충전시간',
  `charge_info` varchar(100) DEFAULT NULL COMMENT '충전퍼센트정보',
  `car_description` varchar(2000) DEFAULT NULL,
  `image_path` varchar(255) DEFAULT NULL COMMENT '차량이미지경로',
  `image_detail1` varchar(255) DEFAULT NULL,
  `image_detail2` varchar(255) DEFAULT NULL,
  `sale_status` varchar(20) NOT NULL DEFAULT '판매중' COMMENT '판매상태',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일시',
  `updated_at` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
  PRIMARY KEY (`car_no`),
  UNIQUE KEY `model_name` (`model_name`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 3. 11개 차종 데이터 투입
LOCK TABLES `car_tbl` WRITE;
INSERT INTO `car_tbl` (`car_no`, `model_name`, `car_type`, `car_color`, `price`, `price_display`, `battery_capacity`, `driving_range_km`, `fast_charge_time`, `charge_info`, `car_description`, `image_path`, `image_detail1`, `image_detail2`, `sale_status`, `created_at`, `updated_at`) VALUES 
(1,'EV4','세단','스노우 화이트 펄, 셰일 그레이, 오로라 블랙 펄, 마그마 레드, 요트 매트 블루, 아이보리 실버, 아이보리 매트 실버, 모닝 헤이즈',45000000,'4,500만원','81.4kWh',533,'31분','10% → 80%','동급 최장 수준의 주행거리와 넓은 실내, 최신 AI 기능으로 완성도를 높인 전기 세단입니다.\n세련된 디자인과 효율적인 주행 성능으로 일상과 장거리 모두에서 만족감을 제공합니다.','/images/cars/EV4_1.jpg','/images/cars/EV4_2.jpg','/images/cars/EV4_3.jpg','판매중','2026-03-26 11:43:16','2026-04-06 17:01:58'),
(2,'EV4-GT','세단','스노우 화이트 펄, 셰일 그레이, 오로라 블랙 펄, 마그마 레드, 요트 매트 블루, 아이보리 실버, 아이보리 매트 실버, 모닝 헤이즈',55000000,'5,500만원','81.4kWh',425,'31분','10% → 80%','넓은 실내 공간과 긴 주행거리, 뛰어난 가격 경쟁력을 갖춘 전기 SUV입니다.\nV2L 기능을 활용한 캠핑과 여행 등 다양한 라이프스타일에 적합하며, 일상과 레저를 모두 만족시키는 실용적인 선택입니다.','/images/cars/EV4_GT_1.jpg','/images/cars/EV4_GT_2.jpg','/images/cars/EV4_GT_3.jpg','판매중','2026-03-26 11:43:24','2026-04-06 17:01:58'),
(3,'EV5','SUV','스모키 블랙, 누가 브라운, 휴먼 그레이, 블랙&화이트(전용)',43100000,'4,310만원','81.4kWh',460,'30분','10% → 80%','동급 최고 수준의 공간 활용성과 긴 주행거리를 갖춘 다재다능한 전기 SUV입니다. E-GMP 플랫폼을 활용한 넓은 실내 공간과 실용적인 V2L 기능을 통해 캠핑과 여행 등 사용자의 다채로운 라이프스타일을 완벽하게 지원합니다.','/images/cars/ev5_1.jpg','/images/cars/ev5_2.jpg','/images/cars/ev5_3.jpg','판매중','2026-03-26 14:44:16','2026-04-06 17:01:58'),
(4,'EV5-GT','SUV','스노우 화이트 펄, 그래비티 그레이, 퓨전 블랙, 마그마 매트 레드, 아이보리 실버, 퓨전 블랙, 스노우 화이트 펄, 블랙&네온',43100000,'4,310만원','81.4kWh',460,'30분','10% → 80%','압도적인 가속 성능과 SUV 특유의 공간 활용성을 결합하여 드라이빙의 즐거움과 실용성을 동시에 완성한 고성능 전기 SUV입니다. E-GMP 플랫폼 기반의 최신 전기차 기술이 집약되어, 패밀리 SUV의 편안함은 물론 스포츠 드라이빙의 역동적인 퍼포먼스까지 타협 없이 제공합니다. 효율성을 넘어 강력한 출력을 지향하는 고성능 세팅과 스포티한 디자인은 도로 위에서 독보적인 존재감을 드러냅니다.','/images/cars/ev5_gt_1.jpg','/images/cars/ev5_gt_2.jpg','/images/cars/ev5_gt_3.jpg','판매중','2026-03-26 14:46:06','2026-04-06 17:01:58'),
(5,'EV3','SUV','스노우 화이트 펄, 아이보리 매트 실버, 아이보리 실버, 셰일 그레이, 프로스트 블루, 어벤쳐린 그린, 오로라 블랙 펄',39950000,'3,995만원','81.4kWh',501,'31분','10% → 80%','동급 최고 수준인 501km의 압도적인 주행거리와 혁신적인 공간 설계를 통해 완성도를 높인 프리미엄 소형 전기 SUV입니다. 지속 가능한 미래를 위한 친환경 소재를 실내 곳곳에 감각적으로 적용하여 가치 있는 드라이빙 경험을 선사합니다. 세련된 디자인과 최신 전기차 기술이 조화를 이루어 도심과 장거리 모두에서 최상의 만족감을 제공합니다.','/images/cars/ev3_1.jpg','/images/cars/ev3_2.jpg','/images/cars/ev3_3.jpg','판매중','2026-03-26 14:52:51','2026-04-06 17:01:58'),
(6,'EV3-GT','SUV','스노우 화이트 펄, 아이보리 매트 실버, 아이보리 실버, 셰일 그레이, 프로스트 블루, 어벤쳐린 그린, 오로라 블랙 펄',53750000,'5,375만원','81.4kWh',410,'31분','10% → 80%','소형 SUV의 실용성에 듀얼 모터 사륜구동 시스템을 결합하여 고성능 감성을 대중화한 모델입니다. 최고 출력 215kW의 파워풀한 가속 성능과 GT 전용 첨단 섀시 제어 기술을 통해 차원이 다른 역동적인 드라이빙을 선사합니다. 세련된 GT 전용 디자인과 혁신적인 전기차 기술이 조화를 이루어 일상과 퍼포먼스 주행 모두에서 완벽한 만족감을 제공합니다.','/images/cars/ev3_gt_1.jpg','/images/cars/ev3_gt_2.jpg','/images/cars/ev3_gt_3.jpg','판매중','2026-03-26 14:54:12','2026-04-06 17:01:58'),
(7,'EV9','SUV','스노우 화이트, 판테라 메탈, 오로라 블랙, 아이보리 실버, 페블 그레이, 오션 블루, 오션 블루 매트',77000000,'7,700만원','99.8 kWh',501,'24분','10% → 80%','혁신적인 E-GMP 플랫폼을 기반으로 넓은 3열 대형 SUV 공간과 플래그십의 품격을 완성한 프리미엄 전기 SUV입니다. 800V 초급속 충전 시스템과 V2L 등 첨단 기술을 집약하여 캠핑과 여행 등 온 가족의 라이프스타일에 최적화된 편의성을 제공합니다. 압도적인 차체 크기와 정교한 운전자 보조 시스템이 조화를 이루어 대가족을 위한 완벽한 모빌리티 경험을 선사합니다.','/images/cars/ev9_1.jpg','/images/cars/ev9_2.jpg','/images/cars/ev9_3.jpg','판매중','2026-03-26 15:03:48','2026-04-06 17:01:58'),
(8,'EV9-GT','SUV','스노우 화이트, 판테라 메탈, 오로라 블랙, 오션 블루 매트, 페블 그레이',90000000,'9,000만원','99.8kWh',450,'24분','10% → 80%','고출력 듀얼 모터 AWD와 스포츠카 수준의 가속력을 결합하여 전동화 대형 SUV의 퍼포먼스 기준을 새롭게 제시하는 플래그십 모델입니다. GT 전용 서스펜션과 고성능 브레이크 시스템을 탑재하여 압도적인 동력 성능은 물론 탁월한 핸들링과 주행 안정성까지 타협 없이 구현했습니다. 고급스러운 스포츠 디자인과 혁신적인 공간 활용성이 조화를 이루어, 고성능 드라이빙의 즐거움과 일상의 여유를 동시에 갈구하는 사용자에게 최상의 가치를 선사합니다.','/images/cars/ev9_gt_1.jpg','/images/cars/ev9_gt_2.jpg','/images/cars/ev9_gt_3.jpg','판매중','2026-03-26 15:04:46','2026-04-06 17:01:58'),
(9,'EV6','SUV','스노우 화이트 펄, 오로라 블랙 펄, 런웨이 레드, 요트 블루, 그래비티 블루',47000000,'4,700만원','77.4kWh',400,'18분','10% → 80%','전기차 전용 플랫폼 기반으로 주행 성능과 실용성을 모두 갖춘 차량입니다. 세련된 미래형 디자인과 넓은 실내 공간을 자랑하며, 빠른 충전 속도와 안정적인 주행 성능을 제공합니다. 출퇴근과 장거리 주행을 모두 아우르는 사용자에게 최적의 선택지입니다.','/images/cars/ev6_1.jpg','/images/cars/ev6_2.jpg','/images/cars/ev6_3.jpg','판매중','2026-03-27 11:58:04','2026-04-06 17:01:58'),
(10,'EV6-GT','SUV','스노우 화이트 펄, 오로라 블랙 펄, 런웨이 레드, 요트 블루, 그래비티 블루',72000000,'7,200만원','84kWh',338,'30분','10% → 80%','강력한 가속 성능과 최신 전기차 기술이 집약된 고성능 전기 SUV입니다. SUV의 실용성과 스포츠카의 성능을 동시에 만족시키며, 사용자에게 독보적인 드라이빙 경험을 제공합니다. 스포티한 주행을 즐기는 운전자에게 최적화된 모델입니다.','/images/cars/ev6_gt_1.jpg','/images/cars/ev6_gt_2.jpg','/images/cars/ev6_gt_3.jpg','판매중','2026-03-27 11:58:10','2026-04-06 17:01:58'),
(11,'Ray EV','경형 전기차','클리어 화이트, 밀키 베이지, 아스트로 그레이, 오로라 블랙 펄, 시그널 레드, 스모크 블루',27000000,'2,700만원','35kWh',200,'40분','10% → 80%','도심 주행과 출퇴근용에 최적화된 경형 전기차입니다. 경차만의 다양한 혜택과 뛰어난 실내 공간 활용성을 제공하며, 유지비 절감 효과가 탁월합니다. 작은 차체로 운전이 미숙한 초보 운전자나 근거리 이동이 많은 사용자에게 안성맞춤인 모델입니다.','/images/cars/RayEV_1.jpg','/images/cars/RayEV_2.jpg','/images/cars/RayEV_3.jpg','판매중','2026-03-27 11:59:53','2026-04-06 17:01:58');
UNLOCK TABLES;