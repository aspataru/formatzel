package read;

import lombok.extern.slf4j.Slf4j;
import rx.Observable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aspataru on 2/9/17.
 */
@Slf4j
public class RxJavaFileReader {

    public List<String> readContentsAsString(String fullPathAsString) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fullPathAsString))) {
            Observable.fromCallable(reader::readLine)
                    .forEach(lines::add);
        } catch (IOException e) {
            log.error("Could not read all lines of file {}", fullPathAsString, e);
        }
        return lines;
    }
}
