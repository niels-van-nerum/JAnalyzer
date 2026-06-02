package nl.niels.processing;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class AudioProcessorTest {

    @Test
    public void toSamples_withEightByteArray_returnsTwoSamples() {
        byte[] chunk = {(byte) 0x80, (byte) 0x01, (byte) 0xFF, (byte) 0x00,
                        (byte) 0xAA, (byte) 0x55, (byte) 0xAA, (byte) 0x55};

        double[] expectedResult = {0.009750, 0.669250488};

        AudioProcessor processor = new AudioProcessor(null, null);
        double[] result = processor.toSamples(chunk);

        assertArrayEquals(expectedResult, result, 0.0001);
    }

    @Test
    void runFft_440HzSignal_returns440HzAsMostSignificantBin() {
        double[] chunk = new double[1024];
        for (int i = 0; i < chunk.length; i++) {
            chunk[i] = Math.sin(2 * Math.PI * 440 * i / 44100);
        }

        AudioProcessor processor = new AudioProcessor(null, null);
        double[] fft = processor.runFft(chunk);

        int bin = 440 * 1024 / 44100;

        double[] magnitudes = new double[fft.length / 4];
        double max = 0;
        for (int i = 0; i < (fft.length / 4); i++) {
            magnitudes[i] = Math.sqrt(Math.pow(fft[i * 2], 2) + Math.pow(fft[i * 2 + 1], 2));
            if (magnitudes[i] > max) {
                max = magnitudes[i];
            }
        }

        Assertions.assertEquals(magnitudes[bin], max);
    }

    @Test
    void toMagnitudes() {
    }

    @Test
    void toBuckets() {
    }

    @Test
    void normalize() {
    }

    @Test
    void addHistory() {
    }
}
