import batch.Batch;
import generator.Generator;
import generator.IndexBoundedGenerator;
import org.junit.Test;

/**
 * Created by aspataru on 2/11/17.
 */
public class TestBatch {

    private final String fullPath1 = getClass().getResource("inputFile1").getPath();
    private final String fullPath2 = getClass().getResource("inputFile2").getPath();
    private final Generator generator = new IndexBoundedGenerator();

    @Test
    public void runFullBatch() {
        new Batch(generator, fullPath1, "out1.txt").run();
        new Batch(generator, fullPath2, "out2.txt").run();
    }

}