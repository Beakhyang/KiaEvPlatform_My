-- 1. 기존에 잘못 만들어진 테이블들 삭제 (초기화)
DROP TABLE IF EXISTS charging_tbl;

-- 2. 명세서 기준 8개 컬럼으로 테이블 생성
CREATE TABLE charging_tbl (
    stat_id       VARCHAR(30)    NOT NULL,          -- 1. 충전소ID (기본키)
    stat_nm       VARCHAR(200)   NOT NULL,          -- 2. 충전소명
    addr          VARCHAR(255),                     -- 3. 주소 (기본 주소)
    addr_detail   VARCHAR(255),                     -- 4. 상세주소
    location_desc VARCHAR(255),                     -- 5. 위치설명 (안내 문구)
    lat           DECIMAL(10, 7) NOT NULL,          -- 6. 위도 (NUMBER 10,7 대응)
    lng           DECIMAL(10, 7) NOT NULL,          -- 7. 경도 (NUMBER 10,7 대응)
    use_time      VARCHAR(100),                     -- 8. 이용가능시간
    PRIMARY KEY (stat_id),                          -- PK 설정
    UNIQUE (stat_id)                                -- 유일키 설정
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='전기차 충전소 정보 명세서 테이블';

-- 3. 테이블 구조가 명세서와 일치하는지 확인

SELECT * FROM charging_tbl;