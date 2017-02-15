package read;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by aspataru on 2/8/17.
 */
public class TestFileReader {

    @Test
    public void shouldReadFileContents() throws Exception {
        String fullPath = getClass().getResource("../inputFile1").getPath();
        assertThat(DefaultFileReader.readContentsAsString(fullPath)).first().hasToString("\uFEFFSeries: Cycle #1");
    }

    @Test
    public void shouldReadFileContentsWithRX() throws Exception {
        RxJavaFileReader rxJavaFileReader = new RxJavaFileReader();
        String fullPath = getClass().getResource("../inputFile1").getPath();
        assertThat(rxJavaFileReader.readContentsAsString(fullPath)).first().hasToString("\uFEFFSeries: Cycle #1");
    }

}
