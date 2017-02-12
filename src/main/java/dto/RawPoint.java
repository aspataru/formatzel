package dto;

import lombok.Builder;
import lombok.Getter;

/**
 * Created by aspataru on 2/11/17.
 */
@Getter
@Builder
public class RawPoint {

    private final String voltage;
    private final String current;

}
