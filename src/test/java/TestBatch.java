import batch.Batch;
import dto.RawPoint;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by aspataru on 2/11/17.
 */
public class TestBatch {

    private final String fullPath1 = getClass().getResource("inputFile1").getPath();
    private final String fullPath2 = getClass().getResource("inputFile2").getPath();
    private final String fullPath3 = getClass().getResource("inputFile3").getPath();

    @Test
    public void shouldLoadAllRawPointsFromPath() throws Exception {
        List<RawPoint> transformed = Batch.loadRawPointsFromPath(fullPath1);
        assertThat(transformed).hasSize(661);
    }

    @Test
    public void runFullBatch() {
        Batch.runFullBatch(fullPath1, "out1.txt");
        Batch.runFullBatch(fullPath2, "out2.txt");
        Batch.runFullBatch(fullPath3, "out3.txt");
    }

}