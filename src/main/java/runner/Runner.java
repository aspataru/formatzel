package runner;

import batch.Batch;
import generator.Generator;
import generator.VoltageBoundedGenerator;

/**
 * Created by aspataru on 2/14/17.
 */
public class Runner {

    private Runner() {
    }

    public static void main(String[] args) {
        String in = "in.txt";
        String out = "out.txt";
        // Generator generator = new IndexBoundedGenerator();
        Generator generator = new VoltageBoundedGenerator();
        new Batch(generator, in, out).run();
    }
}
