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
public class Batch {

    public static List<List<String>> runBatch(String path) {

        return DefaultFileReader.readContentsAsString(path).stream()
                .map(keepOnlyDataPoints)
                .collect(Collectors.toList());
    }

    private static Predicate<String> notNullOrEmpty = e -> e != null && !e.isEmpty();

    private static Function<String, List<String>> keepOnlyDataPoints = input -> {
        String[] cleanDataRow = input.split(" ");
        return new ArrayList<>(Arrays.asList(cleanDataRow)).stream()
                .filter(notNullOrEmpty)
                .collect(Collectors.toList());
    };


}
