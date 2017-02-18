package util;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by aspataru on 2/18/17.
 */
final class BigDecimalOperations {

    BigDecimal computeAverage(List<BigDecimal> parsedPoints) {
        BigDecimalAverager averageCollect = parsedPoints.stream()
                .collect(BigDecimalAverager::new, BigDecimalAverager::accept, BigDecimalAverager::combine);
        return averageCollect.average();
    }

    static BigDecimal delta(BigDecimal a, BigDecimal b) {
        return a.subtract(b).abs();
    }

    @Getter
    public class BigDecimalAverager {

        private BigDecimal total = BigDecimal.ZERO;
        private BigDecimal count = BigDecimal.ZERO;

        BigDecimal average() {
            return count.compareTo(BigDecimal.ZERO) > 0 ? total.divide(count, BigDecimal.ROUND_DOWN) : BigDecimal.ZERO;
        }

        void accept(BigDecimal i) {
            total = total.add(i);
            count = count.add(BigDecimal.ONE);
        }

        void combine(BigDecimalAverager other) {
            total = total.add(other.getTotal());
            count = count.add(other.getCount());
        }
    }
}
