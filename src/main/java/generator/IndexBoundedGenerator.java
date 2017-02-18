package generator;

import dto.ParsedPoint;
import lombok.extern.slf4j.Slf4j;
import util.ParsedPointUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by aspataru on 2/17/17.
 */
@Slf4j
public class IndexBoundedGenerator implements Generator {

    @Override
    public List<ParsedPoint> generate(List<ParsedPoint> seedList) {

        // 1. compute average step
        BigDecimal averageStep = ParsedPointUtils.computeAverageVoltageStep(seedList);
        log.info("Computed average voltage step as {}", averageStep);

        // 2. get index of max voltage to use as turnaround index
        int indexOfMaxVoltage = findIndexOfMaxVoltage(seedList);

        // 3. get list of voltages with this step
        List<BigDecimal> generatedVoltageSteps = generateVoltageSteps(seedList.get(0).getVoltage(),
                averageStep, indexOfMaxVoltage, seedList.size());

        // 4. obtain new points using second value as seed and average delta as step
        //    there should be the same number going up as down
        return ensureSameNumberUpAndDown(
                IntStream.range(0, seedList.size())
                        .mapToObj(i ->
                                ParsedPoint.builder()
                                        .voltage(generatedVoltageSteps.get(i))
                                        .current(seedList.get(i).getCurrent())
                                        .build()
                        ).collect(Collectors.toList()));
    }

    private List<BigDecimal> generateVoltageSteps(BigDecimal seedValue,
                                                  BigDecimal delta, int turnaroundIndex, int maxSize) {
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

    private int findIndexOfMaxVoltage(List<ParsedPoint> points) {
        int indexOfMaxVoltage = -1;
        BigDecimal maxVoltage = BigDecimal.ZERO;
        for (int i = 0; i < points.size(); i++) {
            if (points.get(i).getVoltage().compareTo(maxVoltage) > 0) {
                maxVoltage = points.get(i).getVoltage();
                indexOfMaxVoltage = i;
            }
        }
        log.info("Max voltage is {}, at index {}", maxVoltage, indexOfMaxVoltage);
        return indexOfMaxVoltage;
    }

    private static List<ParsedPoint> ensureSameNumberUpAndDown(List<ParsedPoint> points) {
        int goingUp = 1;
        int goingDown = 0;
        BigDecimal previous = points.get(0).getVoltage();

        for (int i = 1; i < points.size(); i++) {
            ParsedPoint currentPoint = points.get(i);
            if (currentPoint.getVoltage().compareTo(previous) > 0) {
                goingUp++;
            } else {
                goingDown++;
            }
            previous = currentPoint.getVoltage();
        }

        log.info("Voltage steps going up {}, and down {}", goingUp, goingDown);

        if (goingDown > goingUp) {
            int diff = goingDown - goingUp;
            log.info("Voltage steps going down are more than going up, delta {}, equalizing", diff);
            return points.subList(0, points.size() - diff);
        }
        return points;
    }


}
