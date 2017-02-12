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

    private static final BigDecimal THOUSAND = new BigDecimal("1000");
    private static Predicate<String> notNullOrEmpty = e -> e != null && !e.isEmpty();
    private static Function<String, List<String>> keepOnlyValues = input -> {
        String[] cleanDataRow = input.split(" ");
        return new ArrayList<>(Arrays.asList(cleanDataRow)).stream()
                .filter(notNullOrEmpty)
                .collect(Collectors.toList());
    };

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

        // for the rest of the processing, the first and last points will be ignored
        List<ParsedPoint> stepsWithoutAberrations =
                pointsReadyForProcessing.subList(1, pointsReadyForProcessing.size() - 1);

        // compute delta voltage steps in mV
        List<BigDecimal> deltaSteps = computeVoltageSteps(stepsWithoutAberrations);

        // average step
        BigDecimal averageStep = computeAverage(deltaSteps);
        log.info("Computed average voltage step as {}", averageStep);

        // index of max voltage
        int indexOfMaxVoltage = findIndexOfMaxVoltage(stepsWithoutAberrations);

        // generate voltage steps to use in modified points
        List<BigDecimal> generatedVoltageSteps = generateVoltageSteps(stepsWithoutAberrations.get(0).getVoltage(),
                averageStep, indexOfMaxVoltage, stepsWithoutAberrations.size());

        // obtain new points using second value as seed and average delta as step
        List<ParsedPoint> allModifiedPoints = IntStream.range(0, stepsWithoutAberrations.size())
                .mapToObj(i ->
                        ParsedPoint.builder()
                                .voltage(generatedVoltageSteps.get(i))
                                .current(stepsWithoutAberrations.get(i).getCurrent())
                                .build()
                ).collect(Collectors.toList());

        // TODO
        // format both
        // keep same number going up + peak = going down
        // reverse sign

        return allModifiedPoints;
    }

    private static int findIndexOfMaxVoltage(List<ParsedPoint> points) {
        int indexOfMaxVoltage = -1;
        BigDecimal maxVoltage = BigDecimal.ZERO;
        for (int i = 0; i < points.size(); i++) {
            if (points.get(i).getVoltage().compareTo(maxVoltage) > 0) {
                maxVoltage = points.get(i).getVoltage();
                indexOfMaxVoltage = i;
            }
        }
        return indexOfMaxVoltage;
    }

    private static List<BigDecimal> generateVoltageSteps(BigDecimal seedValue, BigDecimal delta, int turnaroundIndex,
                                                         int maxSize) {
        // make sure the delta starts positive
        BigDecimal safeDelta = delta.abs();

        List<BigDecimal> generatedVoltageSteps = new ArrayList<>();
        BigDecimal previous = seedValue;
        generatedVoltageSteps.add(seedValue);

        // steps to increase voltage
        for (int i = 0; i < turnaroundIndex; i++) {
            BigDecimal current = previous.add(safeDelta);
            generatedVoltageSteps.add(current);
            previous = current;
        }
        // steps to decrease voltage
        for (int i = turnaroundIndex + 1; i < maxSize; i++) {
            BigDecimal current = previous.subtract(safeDelta);
            generatedVoltageSteps.add(current);
            previous = current;
        }

        return generatedVoltageSteps;
    }


}
