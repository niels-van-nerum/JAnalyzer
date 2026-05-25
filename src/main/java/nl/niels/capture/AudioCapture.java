package nl.niels.capture;

import javax.sound.sampled.*;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;

public class AudioCapture implements Runnable {
    private final AudioFormat format = new AudioFormat(48000, 16, 2, true, false);
    private final LinkedBlockingQueue<byte[]> queue;
    private final TargetDataLine targetDataLine;
    private static final int CHUNK_SIZE = 3840;

    public AudioCapture(LinkedBlockingQueue<byte[]> queue) throws LineUnavailableException {
        this.targetDataLine = getTargetDataLine();
        this.queue = queue;
    }

    private TargetDataLine getTargetDataLine() throws LineUnavailableException {
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        return (TargetDataLine) AudioSystem.getLine(info);
    }

    @Override
    public void run() {
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
