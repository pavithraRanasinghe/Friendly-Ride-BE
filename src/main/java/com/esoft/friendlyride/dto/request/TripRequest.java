package com.esoft.friendlyride.dto.request;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TripRequest {

    private Long passengerId;
    private List<Long> routeIds;

}
