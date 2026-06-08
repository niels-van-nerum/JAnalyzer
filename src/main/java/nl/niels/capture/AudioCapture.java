package nl.niels.capture;

import nl.niels.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.*;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;

public class AudioCapture implements Runnable {
    private final AudioFormat format = new AudioFormat(44100, 16, 2, true, false);
    private final LinkedBlockingQueue<byte[]> queue;
    private final TargetDataLine targetDataLine;
    private static final Logger LOGGER = LoggerFactory.getLogger(AudioCapture.class);
    private final Configuration configuration;

    public AudioCapture(LinkedBlockingQueue<byte[]> queue, Configuration configuration) throws LineUnavailableException {
        this.targetDataLine = getTargetDataLine();
        this.configuration = configuration;
        this.queue = queue;
        getSelectedMixer();
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

    private Mixer getSelectedMixer() {
        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        StringBuilder builder = new StringBuilder("Selected mixer:\n");
        Mixer mixer = null;

        for (Mixer.Info mixerInfo : mixerInfos) {
            if (mixerInfo.getName().contains(configuration.device())) {
                mixer = AudioSystem.getMixer(mixerInfo);
                builder.append("\t[x] ");
            } else {
                builder.append("\t[ ] ");
            }

            builder.append(String.format("%-30s %s%n",
                    mixerInfo.getName(),
                    mixerInfo.getDescription()));
        }
        
        LOGGER.info(builder.toString());
        return mixer;
    }

    @Override
    public void run() {
        LOGGER.info("Audio capture started");

        try {
            targetDataLine.open(format);
            targetDataLine.start();

            byte[] buffer = new byte[configuration.frameSize()];

            while (true) {
               targetDataLine.read(buffer, 0, configuration.frameSize());
               queue.offer(Arrays.copyOf(buffer, configuration.frameSize()));
            }

        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }
}
