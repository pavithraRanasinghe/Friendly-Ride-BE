package com.esoft.friendlyride.dto.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;
import org.locationtech.jts.geom.Point;
import org.n52.jackson.datatype.jts.GeometryDeserializer;
import org.n52.jackson.datatype.jts.GeometrySerializer;

import java.io.Serializable;

@Data
@Builder
public class DriverRequest implements Serializable {

    private Long id;
    private String name;
    /**
     * Json request should be
     * {
     *   "location": {
     *      "type": "Point",
     *      "coordinates": [-73.935242, 40.730610]
     *    }
     * }
     */
    @JsonSerialize(using = GeometrySerializer.class)
    @JsonDeserialize(using = GeometryDeserializer.class)
    private Point location;
    private double latitude;
    private double longitude;
}
