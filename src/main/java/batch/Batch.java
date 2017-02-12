package batch;

import batch.util.BigDecimalAverager;
import dto.ParsedPoint;
import dto.RawPoint;
import lombok.extern.slf4j.Slf4j;
import read.DefaultFileReader;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by aspataru on 2/11/17.
 */
@Slf4j
public class Batch {

    private static Predicate<String> notNullOrEmpty = e -> e != null && !e.isEmpty();

    private static Function<String, List<String>> keepOnlyValues = input -> {
        String[] cleanDataRow = input.split(" ");
        return new ArrayList<>(Arrays.asList(cleanDataRow)).stream()
                .filter(notNullOrEmpty)
                .collect(Collectors.toList());
    };

    private static final BigDecimal THOUSAND = new BigDecimal("1000");

    private Batch() {
    }

    public static List<RawPoint> loadRawPointsFromPath(String path) {
        return DefaultFileReader.readContentsAsString(path).stream()
                .map(keepOnlyValues)
                .filter(list -> {
                    if (list.size() != 2) {
                        log.info("Dropping row {} for having incorrect size {}",
                                Arrays.toString(list.toArray()), list.size());
                        return false;
                    }
                    return true;
                })
                .map(list -> RawPoint.builder().voltage(list.get(0)).current(list.get(1)).build())
                .collect(Collectors.toList());
    }

    private static List<BigDecimal> computeVoltageSteps(List<ParsedPoint> parsedPoints) {
        return IntStream.range(0, parsedPoints.size() - 1)
                .mapToObj(index -> delta(
                        parsedPoints.get(index).getVoltage(),
                        parsedPoints.get(index + 1).getVoltage()))
                .map(step -> new BigDecimal(step.toString(), new MathContext(3)))
                .collect(Collectors.toList());
    }

    private static BigDecimal computeAverage(List<BigDecimal> parsedPoints) {
        BigDecimalAverager averageCollect = parsedPoints.stream()
                .collect(BigDecimalAverager::new, BigDecimalAverager::accept, BigDecimalAverager::combine);
        return averageCollect.average();
    }

    private static BigDecimal delta(BigDecimal a, BigDecimal b) {
        return a.subtract(b).abs();
    }


    // Run the whole thing!
    public static List<ParsedPoint> runFullBatch(String path) {
        // load points from input file and convert V to mV
        List<ParsedPoint> pointsReadyForProcessing = loadRawPointsFromPath(path).stream()
                .map(rawPoint -> ParsedPoint.builder()
                        .voltage(new BigDecimal(rawPoint.getVoltage()))
                        .current(new BigDecimal(rawPoint.getCurrent()))
                        .build())
                .map(parsedPoint ->
                        ParsedPoint.builder()
                                .voltage(parsedPoint.getVoltage().multiply(THOUSAND))
                                .current(parsedPoint.getCurrent())
                                .build()
                ).collect(Collectors.toList());

        // compute average step in mV
        List<BigDecimal> averageSteps = computeVoltageSteps(pointsReadyForProcessing);
        List<BigDecimal> averageStepsExcludingAberrations = averageSteps.subList(1, averageSteps.size() - 1);
        BigDecimal averageStep = computeAverage(averageStepsExcludingAberrations);
        log.info("Computed average voltage step as {}", averageStep);

        // obtain new points using second value as seed and average delta as step


        return pointsReadyForProcessing;
    }


}
