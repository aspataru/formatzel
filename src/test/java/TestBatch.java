import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by aspataru on 2/11/17.
 */
public class TestBatch {

    @Test
    public void shouldTransformToExpectedValues() throws Exception {

        String fullPath = getClass().getResource("inputFile").getPath();

        List<List<String>> transformed = Batch.runBatch(fullPath);

        assertThat(transformed).hasSize(662);
    }

}