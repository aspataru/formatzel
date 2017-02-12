package dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * Created by aspataru on 2/12/17.
 */
@Getter
@Builder
public class ParsedPoint {

    private final BigDecimal voltage;
    private final BigDecimal current;

}
