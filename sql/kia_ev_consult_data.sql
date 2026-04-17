USE kiaevdb;

DROP TABLE IF EXISTS CONSULT_TBL;

CREATE TABLE CONSULT_TBL (
    consult_no BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_no BIGINT NOT NULL,
    car_no BIGINT NOT NULL,
    dealer_no BIGINT NULL,
    budget_amount INT NULL,
    use_purpose VARCHAR(100) NULL,
    main_range_km VARCHAR(20) NOT NULL,
    fellow_data VARCHAR(20) NULL,
    consult_content VARCHAR(1000) NULL,
    consult_status VARCHAR(20) NOT NULL DEFAULT '대기',
    request_date DATETIME NOT NULL,
    assigned_date DATETIME NULL,
    completed_date DATETIME NULL,
    consult_memo VARCHAR(1000) NULL,
    updated_date DATETIME NULL
);

ALTER TABLE CONSULT_TBL
ADD CONSTRAINT fk_consult_member
FOREIGN KEY (member_no) REFERENCES member_tbl(member_no);

SHOW TABLES;
DESC CONSULT_TBL;