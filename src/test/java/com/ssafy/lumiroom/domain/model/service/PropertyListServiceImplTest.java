package com.ssafy.lumiroom.domain.model.service;

import com.ssafy.lumiroom.domain.model.dto.PropertyListItemResponse;
import com.ssafy.lumiroom.domain.model.dto.PropertyListResponse;
import com.ssafy.lumiroom.domain.model.mapper.PropertyListMapper;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PropertyListServiceImplTest {

    @Test
    void returnsPagedProperties() {
        PropertyListItemResponse item = new PropertyListItemResponse();
        StubPropertyListMapper mapper = new StubPropertyListMapper(21L, List.of(item));
        PropertyListServiceImpl propertyListService = new PropertyListServiceImpl(mapper);

        PropertyListResponse response =
                propertyListService.findProperties(35.159545, 126.852601, 1000, 1, 10);

        assertThat(response.getProperties()).containsExactly(item);
        assertThat(response.getPage()).isEqualTo(1);
        assertThat(response.getSize()).isEqualTo(10);
        assertThat(response.getTotalElements()).isEqualTo(21);
        assertThat(response.getTotalPages()).isEqualTo(3);
        assertThat(mapper.offsets).containsExactly(10L);
    }

    @Test
    void skipsListQueryWhenNoPropertyExists() {
        StubPropertyListMapper mapper = new StubPropertyListMapper(0L, List.of());
        PropertyListServiceImpl propertyListService = new PropertyListServiceImpl(mapper);

        PropertyListResponse response =
                propertyListService.findProperties(35.159545, 126.852601, 1000, 0, 100);

        assertThat(response.getProperties()).isEmpty();
        assertThat(response.getTotalPages()).isZero();
        assertThat(mapper.selectCallCount).isZero();
    }

    @Test
    void rejectsInvalidRequest() {
        PropertyListServiceImpl propertyListService =
                new PropertyListServiceImpl(new StubPropertyListMapper(0L, List.of()));

        assertThatThrownBy(() -> propertyListService.findProperties(91, 126.852601, 1000, 0, 100))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("400 BAD_REQUEST");
        assertThatThrownBy(() -> propertyListService.findProperties(35.159545, 126.852601, 0, 0, 100))
                .isInstanceOf(ResponseStatusException.class);
        assertThatThrownBy(() -> propertyListService.findProperties(35.159545, 126.852601, 1000, -1, 100))
                .isInstanceOf(ResponseStatusException.class);
        assertThatThrownBy(() -> propertyListService.findProperties(35.159545, 126.852601, 1000, 0, 501))
                .isInstanceOf(ResponseStatusException.class);
    }

    private static class StubPropertyListMapper implements PropertyListMapper {

        private final long count;
        private final List<PropertyListItemResponse> properties;
        private final List<Long> offsets = new ArrayList<>();
        private int selectCallCount;

        private StubPropertyListMapper(long count, List<PropertyListItemResponse> properties) {
            this.count = count;
            this.properties = properties;
        }

        @Override
        public List<PropertyListItemResponse> selectPropertiesWithinRadius(
                double lat, double lng, double radius, int limit, long offset) {
            selectCallCount++;
            offsets.add(offset);
            return properties;
        }

        @Override
        public long countPropertiesWithinRadius(double lat, double lng, double radius) {
            return count;
        }

		@Override
		public PropertyListItemResponse findByIdWithSafety(Long id) {
			// TODO Auto-generated method stub
			return null;
		}
    }
}
