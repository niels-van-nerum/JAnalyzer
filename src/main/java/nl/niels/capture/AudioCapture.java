package nl.niels.capture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.*;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;

public class AudioCapture implements Runnable {
    private final AudioFormat format = new AudioFormat(44100, 16, 2, true, false);
    private final LinkedBlockingQueue<byte[]> queue;
    private final TargetDataLine targetDataLine;
    private static final int CHUNK_SIZE = 3840;
    private static final Logger LOGGER = LoggerFactory.getLogger(AudioCapture.class);

    public AudioCapture(LinkedBlockingQueue<byte[]> queue) throws LineUnavailableException {
        this.targetDataLine = getTargetDataLine();
        this.queue = queue;
        printDevices();
    }

    private TargetDataLine getTargetDataLine() throws LineUnavailableException {
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();

        for (Mixer.Info mixerInfo : mixerInfos) {
            Mixer mixer = AudioSystem.getMixer(mixerInfo);
            if (mixer.isLineSupported(info)) {
                LOGGER.info("AudioCapture created targeting line: {} with format: {}", mixerInfo, format);
                return (TargetDataLine) mixer.getLine(info);
            }
        }

        throw new LineUnavailableException("No matching line found.");
    }

    private void printDevices() {
        Mixer.Info[] mixers = AudioSystem.getMixerInfo();
        StringBuilder builder = new StringBuilder("Listing devices:\n");

        for (Mixer.Info mixer : mixers) {
            builder.append(String.format("\t\t\t\t%s %s\n", mixer.getName(), mixer.getDescription()));
        }

        LOGGER.info(builder.toString());
    }

    @Override
    public void run() {
        LOGGER.info("Audio capture started");

        try {
            targetDataLine.open(format);
            targetDataLine.start();

            byte[] buffer = new byte[CHUNK_SIZE];

            while (true) {
               targetDataLine.read(buffer, 0, CHUNK_SIZE);
               queue.offer(Arrays.copyOf(buffer, CHUNK_SIZE));
            }

        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }
}
