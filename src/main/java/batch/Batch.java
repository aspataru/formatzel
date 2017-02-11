package batch;

import lombok.extern.slf4j.Slf4j;
import read.DefaultFileReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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

    private Batch() {
    }

    public static List<List<String>> runBatch(String path) {

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
                .collect(Collectors.toList());
    }

}
