package dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by aspataru on 2/12/17.
 */
@Getter
@Builder
public class ParsedPoint {

    private final BigDecimal voltage;
    private final BigDecimal current;

    @Override
    public String toString() {
        return voltage.setScale(2, BigDecimal.ROUND_HALF_UP) + "," + format(current, 3);
    }

    private static String format(BigDecimal x, int scale) {
        NumberFormat formatter = new DecimalFormat("0.0E00");
        formatter.setRoundingMode(RoundingMode.HALF_UP);
        formatter.setMinimumFractionDigits(scale);
        return formatter.format(x);
    }
}
