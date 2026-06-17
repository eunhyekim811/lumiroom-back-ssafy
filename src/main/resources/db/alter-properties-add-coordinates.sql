ALTER TABLE properties
    ADD COLUMN latitude DECIMAL(10, 7) NULL COMMENT '위도',
    ADD COLUMN longitude DECIMAL(10, 7) NULL COMMENT '경도',
    ADD COLUMN location POINT NULL COMMENT '매물 좌표 WGS84, POINT(경도 위도)';

ALTER TABLE properties
    ADD INDEX idx_properties_lat_lng (latitude, longitude);
