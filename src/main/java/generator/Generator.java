package generator;

import dto.ParsedPoint;

import java.util.List;

/**
 * Created by aspataru on 2/17/17.
 */
@FunctionalInterface
public interface Generator {

    List<ParsedPoint> generate(List<ParsedPoint> initialList);

}
