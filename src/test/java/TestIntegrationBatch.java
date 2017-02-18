import batch.Batch;
import generator.Generator;
import generator.IndexBoundedGenerator;
import org.junit.Test;

import java.io.File;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * Created by aspataru on 2/11/17.
 */
public class TestIntegrationBatch {

    private final String inPath1 = getClass().getResource("input/inputFile1").getPath();
    private final String inPath2 = getClass().getResource("input/inputFile2").getPath();
    private final String validationPath1 = getClass().getResource("output/out1").getPath();
    private final String validationPath2 = getClass().getResource("output/out2").getPath();
    private final String outPath1 = "target/out1.txt";
    private final String outPath2 = "target/out2.txt";
    private final Generator generator = new IndexBoundedGenerator();


    @Test
    public void runFullBatch() {
        new Batch(generator, inPath1, outPath1).run();
        assertThat(new File(outPath1)).hasSameContentAs(new File(validationPath1));

        new Batch(generator, inPath2, "target/out2.txt").run();
        assertThat(new File(outPath2)).hasSameContentAs(new File(validationPath2));
    }

}