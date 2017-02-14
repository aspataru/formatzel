package runner;

import batch.Batch;

/**
 * Created by aspataru on 2/14/17.
 */
public class Runner {
    public static void main(String[] args) {
        String in = "in.txt";
        String out = "out.txt";
        Batch.runFullBatch(in, out);
    }
}
