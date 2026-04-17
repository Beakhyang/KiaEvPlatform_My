-- 1. 기존 테이블 삭제 (중복 생성 방지)
DROP TABLE IF EXISTS `wishlist_tbl`;

-- 2. 관심 차량 테이블 생성
CREATE TABLE `wishlist_tbl` (
  `wishlist_no` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `car_no` bigint NOT NULL,
  `member_no` bigint NOT NULL,
  PRIMARY KEY (`wishlist_no`),
  UNIQUE KEY `unique_member_car` (`member_no`, `car_no`),
  CONSTRAINT `fk_wishlist_car` FOREIGN KEY (`car_no`) REFERENCES `car_tbl` (`car_no`),
  CONSTRAINT `fk_wishlist_member` FOREIGN KEY (`member_no`) REFERENCES `member_tbl` (`member_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;