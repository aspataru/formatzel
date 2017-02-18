package util;

import dto.ParsedPoint;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static util.BigDecimalOperations.delta;

/**
 * Created by aspataru on 2/18/17.
 */
public class ParsedPointUtils {

    private ParsedPointUtils() {
    }

    public static BigDecimal computeAverageVoltageStep(List<ParsedPoint> points) {
        return new BigDecimalOperations().computeAverage(computeVoltageSteps(points));
    }

    private static List<BigDecimal> computeVoltageSteps(List<ParsedPoint> parsedPoints) {
        return IntStream.range(0, parsedPoints.size() - 1)
                .mapToObj(index -> delta(
                        parsedPoints.get(index).getVoltage(),
                        parsedPoints.get(index + 1).getVoltage()))
                .map(step -> new BigDecimal(step.toString(), new MathContext(3)))
                .collect(Collectors.toList());
    }
}
