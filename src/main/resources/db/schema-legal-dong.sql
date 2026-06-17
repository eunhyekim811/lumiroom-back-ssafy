CREATE TABLE legal_dongs (
    code CHAR(10) NOT NULL,
    name VARCHAR(200) NOT NULL,
    is_active TINYINT(1) NOT NULL,
    sido_code CHAR(2) NOT NULL,
    sigungu_code CHAR(5) NOT NULL,
    dong_code CHAR(10) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (code),
    KEY idx_legal_dongs_name (name),
    KEY idx_legal_dongs_active_name (is_active, name),
    KEY idx_legal_dongs_sigungu_code (sigungu_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
