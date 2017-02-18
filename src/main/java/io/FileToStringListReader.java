package io;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aspataru on 2/8/17.
 */
@Slf4j
public class FileToStringListReader {

    private FileToStringListReader() {
    }

    public static List<String> readRowsAsString(String fullPathAsString) {
        List<String> lines = new ArrayList<>();
        try {
            lines = Files.readAllLines(Paths.get(fullPathAsString));
        } catch (IOException e) {
            log.error("Could not read all lines of file {}", fullPathAsString, e);
        }
        return lines;
    }

}
