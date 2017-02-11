import org.junit.Test;
import read.DefaultFileReader;
import read.RxJavaFileReader;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by aspataru on 2/8/17.
 */
public class TestFileReader {

    @Test
    public void shouldReadFileContents() throws Exception {
        DefaultFileReader defaultFileReader = new DefaultFileReader();
        String fullPath = getClass().getResource("inputFile").getPath();
        assertThat(defaultFileReader.readContentsAsString(fullPath)).first().hasToString("hello");
    }

    @Test
    public void shouldReadFileContentsWithRX() throws Exception {
        RxJavaFileReader rxJavaFileReader = new RxJavaFileReader();
        String fullPath = getClass().getResource("inputFile").getPath();
        assertThat(rxJavaFileReader.readContentsAsString(fullPath)).first().hasToString("hello");
    }

}
