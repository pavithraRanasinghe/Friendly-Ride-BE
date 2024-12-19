package com.esoft.friendlyride.controller;

import com.esoft.friendlyride.dto.request.SearchDriverRequest;
import com.esoft.friendlyride.dto.response.MultipleRouteSearchResponse;
import com.esoft.friendlyride.models.Route;
import com.esoft.friendlyride.services.RouteService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class RouteControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private RouteService routeService;

    private String generateMockToken() {
        return Jwts.builder()
                .setSubject("user")
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, "secret")
                .compact();
    }

    @Test
    void shouldReturnNearbyDriversForGivenCoordinatesAndTime() {
        // given
        double startLongitude = 79.8612;
        double startLatitude = 6.9271;
        double endLongitude = 79.8650;
        double endLatitude = 6.9278;
        LocalTime startTime = LocalTime.of(10, 30);
        List<MultipleRouteSearchResponse> mockResponse = List.of(new MultipleRouteSearchResponse());

        when(routeService.findTrip(any(SearchDriverRequest.class))).thenReturn(mockResponse);

        String token = generateMockToken();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        // when
        ResponseEntity<List> response = restTemplate.getForEntity(
                "/search?startLongitude={startLongitude}&startLatitude={startLatitude}&endLongitude={endLongitude}&endLatitude={endLatitude}&startTime={startTime}",
                List.class, headers, startLongitude, startLatitude, endLongitude, endLatitude, startTime);

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

        String token = generateMockToken();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        // when
        ResponseEntity<Map> response = restTemplate.getForEntity("/{id}", Map.class, headers, routeId);

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

        String token = generateMockToken();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        // when
        ResponseEntity<Map> response = restTemplate.getForEntity("/{id}", Map.class, headers, routeId);

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}