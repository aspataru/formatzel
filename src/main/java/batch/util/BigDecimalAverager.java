package batch.util;

import lombok.Getter;

import java.math.BigDecimal;

/**
 * Created by aspataru on 2/12/17.
 */
@Getter
public class BigDecimalAverager {

    private BigDecimal total = BigDecimal.ZERO;
    private BigDecimal count = BigDecimal.ZERO;

    public BigDecimal average() {
        return count.compareTo(BigDecimal.ZERO) > 0 ? total.divide(count, BigDecimal.ROUND_DOWN) : BigDecimal.ZERO;
    }

    public void accept(BigDecimal i) {
        total = total.add(i);
        count = count.add(BigDecimal.ONE);
    }

    public void combine(BigDecimalAverager other) {
        total = total.add(other.getTotal());
        count = count.add(other.getCount());
    }
}
