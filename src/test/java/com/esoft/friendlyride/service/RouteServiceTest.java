package com.esoft.friendlyride.service;

import com.esoft.friendlyride.dto.request.SearchDriverRequest;
import com.esoft.friendlyride.models.Route;
import com.esoft.friendlyride.repository.RouteRepository;
import com.esoft.friendlyride.services.RouteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class RouteControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private RouteService routeService;

    @MockBean
    private RouteRepository routeRepository;

    @Test
    void shouldReturnNearbyDriversForGivenCoordinatesAndTime() {
        // given
        double startLongitude = 79.8612;
        double startLatitude = 6.9271;
        double endLongitude = 79.8650;
        double endLatitude = 6.9278;
        LocalTime startTime = LocalTime.of(10, 30);
        double distance = 0.2;
        List<Route> mockSingleRideList = List.of(new Route());

        when(routeRepository.findDriversNearby(startLongitude, startLatitude, distance, endLongitude, endLatitude, startTime))
                .thenReturn(mockSingleRideList);

        SearchDriverRequest searchRequest = SearchDriverRequest.builder()
                .startLongitude(startLongitude)
                .startLatitude(startLatitude)
                .endLongitude(endLongitude)
                .endLatitude(endLatitude)
                .startTime(startTime)
                .build();

        when(routeService.findTrip(searchRequest)).thenCallRealMethod();

        // when
        ResponseEntity<List> response = restTemplate.getForEntity(
                "/search?startLongitude={startLongitude}&startLatitude={startLatitude}&endLongitude={endLongitude}&endLatitude={endLatitude}&startTime={startTime}",
                List.class, startLongitude, startLatitude, endLongitude, endLatitude, startTime);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void shouldReturnMultipleRoutesWhenNoSingleRideFound() {
        // given
        double startLongitude = 79.8612;
        double startLatitude = 6.9271;
        double endLongitude = 79.8650;
        double endLatitude = 6.9278;
        LocalTime startTime = LocalTime.of(10, 30);
        double distance = 0.2;

        List<Route> mockSingleRideList = new ArrayList<>();
        List<Route> mockStartSearchList = List.of(new Route());
        List<Route> mockEndSearchList = List.of(new Route());

        when(routeRepository.findDriversNearby(startLongitude, startLatitude, distance, endLongitude, endLatitude, startTime))
                .thenReturn(mockSingleRideList);
        when(routeRepository.findStartSearch(startLongitude, startLatitude, distance)).thenReturn(mockStartSearchList);
        when(routeRepository.findEndSearch(anyDouble(), anyDouble(), eq(distance), eq(endLongitude), eq(endLatitude)))
                .thenReturn(mockEndSearchList);

        SearchDriverRequest searchRequest = SearchDriverRequest.builder()
                .startLongitude(startLongitude)
                .startLatitude(startLatitude)
                .endLongitude(endLongitude)
                .endLatitude(endLatitude)
                .startTime(startTime)
                .build();

        when(routeService.findTrip(searchRequest)).thenCallRealMethod();

        // when
        ResponseEntity<List> response = restTemplate.getForEntity(
                "/search?startLongitude={startLongitude}&startLatitude={startLatitude}&endLongitude={endLongitude}&endLatitude={endLatitude}&startTime={startTime}",
                List.class, startLongitude, startLatitude, endLongitude, endLatitude, startTime);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void shouldReturnRouteDetailsForGivenRouteId() {
        // given
        Long routeId = 1L;
        Route mockRoute = new Route();
        mockRoute.setId(routeId);

        when(routeService.findById(routeId)).thenReturn(mockRoute);

        // when
        ResponseEntity<Map> response = restTemplate.getForEntity("/{id}", Map.class, routeId);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(routeId, response.getBody().get("id"));
    }

    @Test
    void shouldReturnNotFoundForNonExistentRouteId() {
        // given
        Long routeId = 9999L;

        when(routeService.findById(routeId)).thenReturn(null);

        // when
        ResponseEntity<Map> response = restTemplate.getForEntity("/{id}", Map.class, routeId);

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}