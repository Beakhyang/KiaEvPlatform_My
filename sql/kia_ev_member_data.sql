DROP TABLE member_tbl;

CREATE TABLE member_tbl (
    member_no BIGINT AUTO_INCREMENT PRIMARY KEY,
    login_id VARCHAR(50) NOT NULL UNIQUE,
    member_pw VARCHAR(255) NOT NULL,
    member_name VARCHAR(50) NOT NULL,
    birth_date DATE NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    zipcode VARCHAR(10),
    address VARCHAR(255) NOT NULL,
    detail_address VARCHAR(255),
    member_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    join_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

SELECT DATABASE();

SELECT * 
FROM member_tbl;
desc member_tbl;

ALTER TABLE member_tbl
ADD dealer_id VARCHAR(50);

