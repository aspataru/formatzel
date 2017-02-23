package io;

import dto.ParsedPoint;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by aspataru on 2/18/17.
 */
@Slf4j
public class ParsedPointFileWriter {

    private static final String CR_LF = "\r\n";

    private ParsedPointFileWriter() {
    }

    public static void writeRawPointsToFile(String path, List<ParsedPoint> points) {
        try (FileWriter fw = new FileWriter(new File(path))) {
            for (ParsedPoint point : points) {
                fw.write(point + "\r\n");
            }
            fw.flush();
        } catch (IOException e) {
            log.error("Failed to write to file {}", path, e);
        }
    }

    public static void writeRawPointsToFileWithSpaceAfterMax(String path, List<ParsedPoint> points) {
        BigDecimal max = points.get(0).getVoltage();
        boolean totalMaxPassed = false;
        try (FileWriter fw = new FileWriter(new File(path))) {
            for (ParsedPoint point : points) {

                if (point.getVoltage().compareTo(max) >= 0) {
                    max = point.getVoltage();
                } else if (!totalMaxPassed) {
                    fw.write(CR_LF);
                    totalMaxPassed = true;
                }

                fw.write(point + CR_LF);

            }
            fw.flush();
        } catch (IOException e) {
            log.error("Failed to write to file {}", path, e);
        }
    }
}
