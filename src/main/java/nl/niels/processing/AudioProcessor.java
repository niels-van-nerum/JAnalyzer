package nl.niels.processing;

import org.jtransforms.fft.DoubleFFT_1D;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;

public class AudioProcessor implements Runnable {
    private final LinkedBlockingQueue<byte[]> consumingQueue;
    private final LinkedBlockingQueue<double[]> producingQueue;
    private final DoubleFFT_1D fft = new DoubleFFT_1D(FFT_SIZE);
    private final ArrayDeque<double[]> history = new ArrayDeque<>();
    private static final int FFT_SIZE = 1024;
    private static final int BUCKETS = 21;
    private static final int HISTORY_FRAMES = 1400;

    public AudioProcessor(LinkedBlockingQueue<byte[]> consumingQueue, LinkedBlockingQueue<double[]> producingQueue) {
        this.consumingQueue = consumingQueue;
        this.producingQueue = producingQueue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                byte[] chunk = consumingQueue.take();
                double[] samples = toSamples(chunk);
                double[] fftResult = runFft(samples);
                double[] magnitudes = toMagnitudes(fftResult);
                double[] result = toBuckets(magnitudes);
                addHistory(result);
                producingQueue.offer(normalize(result));
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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

    protected double[] runFft(double[] samples) {
        double[] extendedSamples = Arrays.copyOf(samples, FFT_SIZE * 2);
        fft.realForwardFull(extendedSamples);
        return extendedSamples;
    }

    protected double[] toMagnitudes(double[] fftResult) {
        double[] magnitudes = new double[fftResult.length / 4];
        for (int i = 0; i < (fftResult.length / 4); i++) {
            magnitudes[i] = Math.sqrt(Math.pow(fftResult[i * 2], 2) + Math.pow(fftResult[i * 2 + 1], 2));
        }
        return magnitudes;
    }

    protected double[] toBuckets(double[] magnitudes) {
        double[] result = new double[AudioProcessor.BUCKETS];
        for (int i = 0; i < AudioProcessor.BUCKETS; i++) {
            double start = Math.pow(magnitudes.length, (double) i / AudioProcessor.BUCKETS);
            double end = Math.pow(magnitudes.length, (double) (i + 1) / AudioProcessor.BUCKETS);

            double max = 0;
            for (int j = (int) start; j < end; j++) {
                if (magnitudes[j] > max) {
                    max = magnitudes[j];
                }
            }

            result[i] = max;
        }

        return result;
    }

    protected double[] normalize(double[] buckets) {
        double[] normalized = new double[BUCKETS];

        double[] maximums = new double[BUCKETS];

        for (double[] frame : history) {
            for (int i = 0; i < frame.length; i++) {
                if (frame[i] > maximums[i]) {
                    maximums[i] = frame[i];
                }
            }
        }

        for (int i = 0; i < maximums.length; i++) {
            if (maximums[i] == 0) {
                normalized[i] = buckets[i];
            } else {
                normalized[i] = buckets[i] / maximums[i];
            }
        }

        return normalized;
    }

    protected void addHistory(double[] frame) {
        history.add(frame);

        if (history.size() > HISTORY_FRAMES) {
            history.removeFirst();
        }
    }
}
