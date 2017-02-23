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
public class VoltageBoundedGenerator implements Generator {

    @Override
    public List<ParsedPoint> generate(List<ParsedPoint> seedList) {

        // 1. compute average step
        BigDecimal averageStep = ParsedPointUtils.computeAverageVoltageStep(seedList);
        log.info("Computed average voltage step as {}", averageStep);

        // 2. get value of max voltage to use as value to never exceed
        BigDecimal maxVoltage = findMaxVoltage(seedList);

        // 3. get list of voltages with this step
        List<BigDecimal> generatedVoltageSteps = generateVoltageSteps(seedList.get(0).getVoltage(),
                averageStep, maxVoltage);

        // 4. obtain new points using second value as seed and average delta as step
        return IntStream.range(0, seedList.size())
                .mapToObj(i ->
                        ParsedPoint.builder()
                                .voltage(generatedVoltageSteps.get(i))
                                .current(seedList.get(i).getCurrent())
                                .build()
                ).collect(Collectors.toList());
    }

    private List<BigDecimal> generateVoltageSteps(BigDecimal seedValue,
                                                  BigDecimal delta, BigDecimal originalMaxVoltage) {
        // make sure the delta starts positive
        BigDecimal safeDelta = delta.abs();

        BigDecimal voltageToNeverExceed = originalMaxVoltage.add(delta);

        List<BigDecimal> generatedVoltageSteps = new ArrayList<>();
        BigDecimal nextStep = seedValue;
        int pointsGoingUpBeforeMax = 0;

        // steps to increase voltage
        while (nextStep.compareTo(voltageToNeverExceed) < 0) {
            generatedVoltageSteps.add(nextStep);
            nextStep = nextStep.add(safeDelta);
            pointsGoingUpBeforeMax++;
        }

        // nextStep went over the max, get it back within limits
        nextStep = nextStep.subtract(safeDelta);

        // steps to decrease voltage
        for (int i = 0; i < pointsGoingUpBeforeMax; i++) {
            BigDecimal current = nextStep.subtract(safeDelta);
            generatedVoltageSteps.add(current);
            nextStep = current;
        }

        return generatedVoltageSteps;
    }

    private BigDecimal findMaxVoltage(List<ParsedPoint> points) {
        BigDecimal maxVoltage = points.stream()
                .map(ParsedPoint::getVoltage)
                .max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        log.info("Max voltage is {}", maxVoltage);
        return maxVoltage;
    }


}
