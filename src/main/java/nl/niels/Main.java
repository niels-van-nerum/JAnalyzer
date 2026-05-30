package nl.niels;

import nl.niels.capture.AudioCapture;
import nl.niels.processing.AudioProcessor;
import nl.niels.transmit.UdpTransmitter;

import javax.sound.sampled.LineUnavailableException;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    private static final int QUEUE_SIZE = 10;

    static void main() throws LineUnavailableException, SocketException {
        LinkedBlockingQueue<byte[]> audioQueue = new LinkedBlockingQueue<>(QUEUE_SIZE);
        LinkedBlockingQueue<double[]> measurementQueue = new LinkedBlockingQueue<>(QUEUE_SIZE);

        AudioCapture audioCapture = new AudioCapture(audioQueue);
        AudioProcessor audioProcessor = new AudioProcessor(audioQueue, measurementQueue);
        UdpTransmitter udpTransmitter = new UdpTransmitter(measurementQueue);

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        executorService.submit(audioCapture);
        executorService.submit(audioProcessor);
        executorService.submit(udpTransmitter);
    }
}
