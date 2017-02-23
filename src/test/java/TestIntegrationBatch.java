import batch.Batch;
import generator.Generator;
import generator.IndexBoundedGenerator;
import generator.VoltageBoundedGenerator;
import org.junit.Test;

import java.io.File;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * Created by aspataru on 2/11/17.
 */
public class TestIntegrationBatch {

    private final String inPath1 = getClass().getResource("input/inputFile1").getPath();
    private final String inPath2 = getClass().getResource("input/inputFile2").getPath();
    private final String outPath1 = "target/out1.txt";
    private final String outPath2 = "target/out2.txt";
    private final String validationPathIndex1 = getClass().getResource("output/out1Index").getPath();
    private final String validationPathIndex2 = getClass().getResource("output/out2Index").getPath();
    private final String validationPathVoltage1 = getClass().getResource("output/out1Voltage").getPath();
    private final String validationPathVoltage2 = getClass().getResource("output/out2Voltage").getPath();

    @Test
    public void runFullBatchWithIndexBoundedGenerator() {
        final Generator generator = new IndexBoundedGenerator();

        new Batch(generator, inPath1, outPath1).run();
        assertThat(new File(outPath1)).hasSameContentAs(new File(validationPathIndex1));

        new Batch(generator, inPath2, outPath2).run();
        assertThat(new File(outPath2)).hasSameContentAs(new File(validationPathIndex2));
    }

    @Test
    public void runFullBatchWithVoltageBoundedGenerator() {
        final Generator generator = new VoltageBoundedGenerator();

        new Batch(generator, inPath1, outPath1).run();
        assertThat(new File(outPath1)).hasSameContentAs(new File(validationPathVoltage1));

        new Batch(generator, inPath2, outPath2).run();
        assertThat(new File(outPath2)).hasSameContentAs(new File(validationPathVoltage2));
    }

}