package com.ssafy.lumiroom.domain.controller;

import com.ssafy.lumiroom.domain.model.dto.PropertyListItemResponse;
import com.ssafy.lumiroom.domain.model.dto.PropertyListResponse;
import com.ssafy.lumiroom.domain.model.service.PropertyListService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PropertyControllerTest {

    @Test
    void delegatesPropertySearchToService() {
        PropertyListResponse expected = new PropertyListResponse(List.of(), 0, 100, 0, 0);
        StubPropertyListService service = new StubPropertyListService(expected);
        PropertyController propertyController = new PropertyController(service);

        PropertyListResponse actual =
                propertyController.getProperties(35.159545, 126.852601, 1000, 0, 100);

        assertThat(actual).isSameAs(expected);
        assertThat(service.lat).isEqualTo(35.159545);
        assertThat(service.lng).isEqualTo(126.852601);
        assertThat(service.radius).isEqualTo(1000);
        assertThat(service.page).isZero();
        assertThat(service.size).isEqualTo(100);
    }

    private static class StubPropertyListService implements PropertyListService {

        private final PropertyListResponse response;
        private double lat;
        private double lng;
        private double radius;
        private int page;
        private int size;

        private StubPropertyListService(PropertyListResponse response) {
            this.response = response;
        }

        @Override
        public PropertyListResponse findProperties(double lat, double lng, double radius, int page, int size) {
            this.lat = lat;
            this.lng = lng;
            this.radius = radius;
            this.page = page;
            this.size = size;
            return response;
        }

		@Override
		public PropertyListItemResponse getPropertyDetail(Long id) {
			// TODO Auto-generated method stub
			return null;
		}
    }
}
