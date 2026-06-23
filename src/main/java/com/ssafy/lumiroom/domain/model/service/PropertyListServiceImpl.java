package com.ssafy.lumiroom.domain.model.service;

import com.ssafy.lumiroom.domain.model.dto.PropertyListItemResponse;
import com.ssafy.lumiroom.domain.model.dto.PropertyListResponse;
import com.ssafy.lumiroom.domain.model.mapper.PropertyListMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PropertyListServiceImpl implements PropertyListService {

    private static final int MAX_PAGE_SIZE = 500;
    private static final double MAX_RADIUS_METERS = 50_000;

    private final PropertyListMapper propertyListMapper;

    @Override
    public PropertyListResponse findProperties(double lat, double lng, double radius, int page, int size) {
        validateRequest(lat, lng, radius, page, size);

        long offset = (long) page * size;
        long totalElements = propertyListMapper.countPropertiesWithinRadius(lat, lng, radius);
        List<PropertyListItemResponse> properties = totalElements == 0
                ? List.of()
                : propertyListMapper.selectPropertiesWithinRadius(lat, lng, radius, size, offset);
        int totalPages = (int) Math.ceil((double) totalElements / size);

        return new PropertyListResponse(properties, page, size, totalElements, totalPages);
    }

    private void validateRequest(double lat, double lng, double radius, int page, int size) {
        if (!Double.isFinite(lat) || lat < -90 || lat > 90) {
            throw badRequest("lat은 -90 이상 90 이하의 값이어야 합니다.");
        }
        if (!Double.isFinite(lng) || lng < -180 || lng > 180) {
            throw badRequest("lng는 -180 이상 180 이하의 값이어야 합니다.");
        }
        if (!Double.isFinite(radius) || radius <= 0 || radius > MAX_RADIUS_METERS) {
            throw badRequest("radius는 0 초과 50000 이하의 값이어야 합니다.");
        }
        if (page < 0) {
            throw badRequest("page는 0 이상이어야 합니다.");
        }
        if (size <= 0 || size > MAX_PAGE_SIZE) {
            throw badRequest("size는 1 이상 500 이하의 값이어야 합니다.");
        }
    }

    private ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

	@Override
	public PropertyListItemResponse getPropertyDetail(Long id) {
		return propertyListMapper.findByIdWithSafety(id);
	}
}
