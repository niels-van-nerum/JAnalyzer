package nl.niels.processing;

import java.util.concurrent.LinkedBlockingQueue;

public class AudioProcessor implements Runnable {
    private final LinkedBlockingQueue<byte[]> consumingQueue;
    private final LinkedBlockingQueue<byte[]> producingQueue;

    public AudioProcessor(LinkedBlockingQueue<byte[]> consumingQueue, LinkedBlockingQueue<byte[]> producingQueue) {
        this.consumingQueue = consumingQueue;
        this.producingQueue = producingQueue;
    }

    @Override
    public void run() {

    }

    protected double[] toSamples(byte[] chunk) {
        int sampleAmount = chunk.length / 4;
        double[] samples = new double[sampleAmount];

        for (int i = 0; i < sampleAmount; i++) {
            int y = i * 4 + 3;
            double sample = (double) ((chunk[y - 2] << 8 | chunk[y - 3] & 0xFF) +
                    (chunk[y] << 8 | chunk[y - 1] & 0xFF)) / 2 / 32768;
            samples[i] = sample;
        }

        return samples;
    }

    private void runFft() {

    }

    private void toMagnitudes() {

    }
}
