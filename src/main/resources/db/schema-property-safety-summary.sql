CREATE TABLE property_safety_summary (
    property_id BIGINT NOT NULL,
    safety_score DECIMAL(5, 2) NOT NULL,
    safety_grade CHAR(1) NOT NULL,
    cctv_count INT NOT NULL DEFAULT 0,
    security_light_count INT NOT NULL DEFAULT 0,
    security_facility_count INT NOT NULL DEFAULT 0,
    radius_m INT NOT NULL,
    calculation_version VARCHAR(50) NOT NULL,
    calculated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (property_id),
    CONSTRAINT fk_property_safety_summary_property
        FOREIGN KEY (property_id) REFERENCES properties (id) ON DELETE CASCADE,
    KEY idx_property_safety_summary_grade_score (safety_grade, safety_score)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_security_facilities_xy ON security_facilities (x, y);
