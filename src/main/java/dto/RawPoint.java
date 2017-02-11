package dto;

import lombok.Builder;
import lombok.Value;

/**
 * Created by aspataru on 2/11/17.
 */
@Value
@Builder
public class RawPoint {

    private final String voltage;
    private final String current;

}
