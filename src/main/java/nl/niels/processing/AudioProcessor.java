package nl.niels.processing;

import org.jtransforms.fft.DoubleFFT_1D;

import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;

public class AudioProcessor implements Runnable {
    private final LinkedBlockingQueue<byte[]> consumingQueue;
    private final LinkedBlockingQueue<byte[]> producingQueue;
    private final DoubleFFT_1D fft = new DoubleFFT_1D(FFT_SIZE);
    private static final int FFT_SIZE = 1024;

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

    private double[] runFft(double[] samples) {
        double[] extendedSamples = Arrays.copyOf(samples, FFT_SIZE * 2);
        fft.realForwardFull(extendedSamples);
        return extendedSamples;
    }

    private void toMagnitudes() {

    }
}
