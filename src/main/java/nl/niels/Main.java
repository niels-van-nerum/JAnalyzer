package nl.niels;

import nl.niels.capture.AudioCapture;
import nl.niels.processing.AudioProcessor;
import nl.niels.transmit.UdpTransmitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.LineUnavailableException;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    private static final int QUEUE_SIZE = 10;
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    static void main() throws LineUnavailableException, SocketException {
        printBanner();
        ExecutorService executorService = Executors.newFixedThreadPool(3, runnable -> {
            Thread thread = new Thread(runnable);
            thread.setUncaughtExceptionHandler((t, throwable) ->
                    LOGGER.error("Thread {} died unexpectedly", t.getName(), throwable));
            return thread;
        });

        LinkedBlockingQueue<byte[]> audioQueue = new LinkedBlockingQueue<>(QUEUE_SIZE);
        LinkedBlockingQueue<double[]> measurementQueue = new LinkedBlockingQueue<>(QUEUE_SIZE);

        AudioCapture audioCapture = new AudioCapture(audioQueue);
        AudioProcessor audioProcessor = new AudioProcessor(audioQueue, measurementQueue);
        UdpTransmitter udpTransmitter = new UdpTransmitter(measurementQueue);

        executorService.submit(audioCapture);
        executorService.submit(audioProcessor);
        executorService.submit(udpTransmitter);
    }

    private static void printBanner() {
        String c = "\033[96m";
        String m = "\033[38;5;201m";
        String r = "\033[0m";

        System.out.println();
        System.out.println(c + "    /$$$$$  "  + m + "/$$$$$$                      /$$                                              " + r);
        System.out.println(c + "   |__  $$ "   + m + "/$$__  $$                    | $$                                             " + r);
        System.out.println(c + "      | $$"    + m + "| $$  \\ $$ /$$$$$$$   /$$$$$$ | $$ /$$   /$$ /$$$$$$$$  /$$$$$$   /$$$$$$    " + r);
        System.out.println(c + "      | $$"    + m + "| $$$$$$$$| $$__  $$ |____  $$| $$| $$  | $$|____ /$$/ /$$__  $$ /$$__  $$    " + r);
        System.out.println(c + " /$$  | $$"    + m + "| $$__  $$| $$  \\ $$  /$$$$$$$| $$| $$  | $$   /$$$$/ | $$$$$$$$| $$  \\__/  " + r);
        System.out.println(c + "| $$  | $$"    + m + "| $$  | $$| $$  | $$ /$$__  $$| $$| $$  | $$  /$$__/  | $$_____/| $$          " + r);
        System.out.println(c + "|  $$$$$$/"    + m + "| $$  | $$| $$  | $$|  $$$$$$$| $$|  $$$$$$$ /$$$$$$$$|  $$$$$$$| $$          " + r);
        System.out.println(c + " \\______/ "   + m + "|__/  |__/|__/  |__/ \\_______/|__/ \\____  $$|________/ \\_______/|__/       " + r);
        System.out.println(m + "                                             /$$  | $$                                              " + r);
        System.out.println(m + "                                            |  $$$$$$/                                              " + r);
        System.out.println(m + "                                             \\______/                                              " + r);
        System.out.println();
    }
}
