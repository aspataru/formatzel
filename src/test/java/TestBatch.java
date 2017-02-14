import batch.Batch;
import dto.RawPoint;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by aspataru on 2/11/17.
 */
public class TestBatch {

    private final String fullPath = getClass().getResource("inputFile").getPath();

    @Test
    public void shouldLoadAllRawPointsFromPath() throws Exception {
        List<RawPoint> transformed = Batch.loadRawPointsFromPath(fullPath);
        assertThat(transformed).hasSize(661);
    }

    @Test
    public void runFullBatch() {
        Batch.runFullBatch(fullPath, "out.txt");
    }

}