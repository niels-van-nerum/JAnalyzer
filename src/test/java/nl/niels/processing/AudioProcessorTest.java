package nl.niels.processing;

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
}
