package batch;

import dto.ParsedPoint;
import dto.RawPoint;
import generator.Generator;
import io.FileToStringListReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static io.ParsedPointFileWriter.writeRawPointsToFile;

/**
 * Created by aspataru on 2/11/17.
 */
@Slf4j
@RequiredArgsConstructor
public class Batch implements Runnable {

    private final Generator generator;
    private final String pathIn;
    private final String pathOut;

    private static final BigDecimal THOUSAND = new BigDecimal("1000");

    @Override
    public void run() {
        log.info("Opening file {}", pathIn);
        List<ParsedPoint> pointsReadyForProcessing =
                FileToStringListReader.readRowsAsString(pathIn).stream()
                        .map(stringRow -> stringRow.replaceAll("\\u0000", ""))
                        .map(stringRow -> stringRow.split("(\\t)|( )"))
                        .map(rowAsList -> Arrays.stream(rowAsList)
                                .filter(token -> token != null && !token.isEmpty())
                                .collect(Collectors.toList()))
                        .filter(list -> {
                            if (list.size() != 2) {
                                log.info("Dropping row {} for having incorrect size {}",
                                        Arrays.toString(list.toArray()), list.size());
                                return false;
                            }
                            return true;
                        })
                        .map(list -> RawPoint.builder().voltage(list.get(0)).current(list.get(1)).build())
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

        List<ParsedPoint> generatedList = generator.generate(stepsWithoutAberrations);

        List<ParsedPoint> inversedSignList = generatedList.stream()
                .map(initial -> ParsedPoint.builder()
                        .voltage(initial.getVoltage())
                        .current(initial.getCurrent().negate()).build())
                .collect(Collectors.toList());

        log.info("Writing {} points to file {}", inversedSignList.size(), pathOut);
        writeRawPointsToFile(pathOut, inversedSignList);

    }


}
