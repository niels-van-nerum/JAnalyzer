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

    private void toSamples(byte[] chunk) {

    }

    private void runFft() {

    }

    private void toMagnitudes() {

    }
}
