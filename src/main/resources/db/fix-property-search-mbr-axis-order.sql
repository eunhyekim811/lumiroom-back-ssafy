UPDATE properties
SET search_mbr = ST_GeomFromText(
        CONCAT(
            'POLYGON((',
            latitude - (1000.0 / 111320.0), ' ',
            longitude - (1000.0 / (111320.0 * GREATEST(ABS(COS(RADIANS(latitude))), 0.000001))), ',',
            latitude + (1000.0 / 111320.0), ' ',
            longitude - (1000.0 / (111320.0 * GREATEST(ABS(COS(RADIANS(latitude))), 0.000001))), ',',
            latitude + (1000.0 / 111320.0), ' ',
            longitude + (1000.0 / (111320.0 * GREATEST(ABS(COS(RADIANS(latitude))), 0.000001))), ',',
            latitude - (1000.0 / 111320.0), ' ',
            longitude + (1000.0 / (111320.0 * GREATEST(ABS(COS(RADIANS(latitude))), 0.000001))), ',',
            latitude - (1000.0 / 111320.0), ' ',
            longitude - (1000.0 / (111320.0 * GREATEST(ABS(COS(RADIANS(latitude))), 0.000001))),
            '))'
        ),
        4326
    )
WHERE location IS NOT NULL
  AND latitude BETWEEN -90 AND 90
  AND longitude BETWEEN -180 AND 180;
