-- 사용자 테이블
CREATE TABLE `users` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `email` VARCHAR(255) NOT NULL UNIQUE,
    `password_hash` VARCHAR(255) NOT NULL,
    `nickname` VARCHAR(50) NOT NULL,
    `role` ENUM('USER', 'ADMIN') DEFAULT 'USER',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);

-- 사용자 댓글 (리뷰) 테이블
CREATE TABLE `property_reviews` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `property_id` BIGINT NOT NULL,
    `content` TEXT NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `rating` TINYINT NOT NULL, 
    PRIMARY KEY (`id`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`property_id`) REFERENCES `properties`(`id`) ON DELETE CASCADE
);

-- 공공데이터
-- CCTV
CREATE TABLE `cctv_infos` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `management_no` VARCHAR(100) UNIQUE NOT NULL,  -- 관리번호 (고유키)
    `open_gov_code` VARCHAR(50) NULL,              
    `agency_name` VARCHAR(100) NULL,               
    `road_address` VARCHAR(200) NULL,              
    `lot_address` VARCHAR(200) NULL,               
    `purpose` VARCHAR(100) NULL,                   
    `camera_count` INT DEFAULT 0,                  
    `pixels` INT DEFAULT 0,                        
    `direction` VARCHAR(200) NULL,                 
    `storage_days` INT DEFAULT 0,                  
    `install_ym` VARCHAR(20) NULL,                 
    `phone` VARCHAR(50) NULL,                      
    `location` POINT NOT NULL SRID 4326,           -- 공간 데이터(위경도)
    `base_date` DATE NULL,                         
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    PRIMARY KEY (`id`),
    SPATIAL INDEX `idx_cctv_location` (`location`) -- 공간 인덱스
);

-- 보안등
CREATE TABLE security_lights (
    sn VARCHAR(50) NOT NULL COMMENT '일련번호',
    fclt_type VARCHAR(50) COMMENT '시설유형',
    fclt_cd VARCHAR(50) COMMENT '시설코드',
    fclt_gvmnfc_nm VARCHAR(255) COMMENT '시설관서명',
    addr VARCHAR(255) COMMENT '주소',
    road_nm_addr VARCHAR(255) COMMENT '도로명주소',
    stdg_ctpv_cd VARCHAR(10) COMMENT '법정동시도코드',
    stdg_sgg_cd VARCHAR(10) COMMENT '법정동시군구코드',
    stdg_emd_cd VARCHAR(10) COMMENT '법정동읍면동코드',
    location POINT NOT NULL SRID 4326 COMMENT '경도, 위도 좌표 (WGS84)', -- 🌟 SRID 4326으로 변경!
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (sn),
    SPATIAL INDEX idx_security_lights_location (location)
);

-- 가로등
CREATE TABLE street_lights (
    sn VARCHAR(50) NOT NULL COMMENT '일련번호',
    fclt_type VARCHAR(50) COMMENT '시설유형',
    fclt_cd VARCHAR(50) COMMENT '시설코드',
    fclt_gvmnfc_nm VARCHAR(255) COMMENT '시설관서명',
    addr VARCHAR(255) COMMENT '주소',
    road_nm_addr VARCHAR(255) COMMENT '도로명주소',
    stdg_ctpv_cd VARCHAR(10) COMMENT '법정동시도코드',
    stdg_sgg_cd VARCHAR(10) COMMENT '법정동시군구코드',
    stdg_emd_cd VARCHAR(10) COMMENT '법정동읍면동코드',
    location POINT NOT NULL SRID 4326 COMMENT '위경도 지도좌표',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (sn),
    SPATIAL INDEX idx_street_lights_location (location)
) COMMENT='행정안전부 공통POI 가로등 정보';

-- 치안안전시설
CREATE TABLE IF NOT EXISTS security_facilities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    objt_id VARCHAR(100) NOT NULL,
    police VARCHAR(100),
    polcsttn VARCHAR(100),
    fclty_ty VARCHAR(100),
    fclty_cd VARCHAR(50),
    fclty_nm VARCHAR(200),
    adres VARCHAR(500),
    rn_adres VARCHAR(500),
    telno VARCHAR(50),
    ctprvn_cd VARCHAR(20),
    sgg_cd VARCHAR(20),
    x DECIMAL(18, 8),
    y DECIMAL(18, 8),
    tmp_x DECIMAL(18, 8),
    tmp_y DECIMAL(18, 8),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_security_facilities_objt_id (objt_id)
);

-- 매물 정보
CREATE TABLE real_estate_trades (
    id BIGINT NOT NULL AUTO_INCREMENT,
    trade_hash CHAR(64) NOT NULL,
    source_file VARCHAR(255) NULL,
    no_in_file INT NULL,
    sigungu VARCHAR(100) NOT NULL,
    lot_number VARCHAR(50) NULL,
    main_number VARCHAR(10) NULL,
    sub_number VARCHAR(10) NULL,
    property_name VARCHAR(200) NULL,
    property_type VARCHAR(30) NULL,
    road_condition VARCHAR(30) NULL,
    rent_type VARCHAR(10) NULL,
    exclusive_area DECIMAL(12, 4) NULL,
    contract_area DECIMAL(12, 4) NULL,
    contract_year_month CHAR(6) NOT NULL,
    contract_day TINYINT NULL,
    contract_date DATE NOT NULL,
    deposit_amount BIGINT NULL COMMENT '만원 단위',
    monthly_rent_amount BIGINT NULL COMMENT '만원 단위',
    trade_amount BIGINT NULL COMMENT '매매 거래금액, 만원 단위',
    floor INT NULL,
    built_year SMALLINT NULL,
    road_name VARCHAR(200) NULL,
    canceled_date DATE NULL,
    deal_type VARCHAR(30) NULL,
    broker_location VARCHAR(100) NULL,
    registration_date DATE NULL,
    apartment_dong_name VARCHAR(100) NULL,
    buyer VARCHAR(50) NULL,
    seller VARCHAR(50) NULL,
    contract_period VARCHAR(20) NULL,
    contract_type VARCHAR(20) NULL,
    renewal_request_right VARCHAR(20) NULL,
    previous_deposit_amount BIGINT NULL COMMENT '만원 단위',
    previous_monthly_rent_amount BIGINT NULL COMMENT '만원 단위',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_real_estate_trades_hash (trade_hash),
    KEY idx_real_estate_trades_region_date (sigungu, contract_date),
    KEY idx_real_estate_trades_property (property_type, rent_type),
    KEY idx_real_estate_trades_road_name (road_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
