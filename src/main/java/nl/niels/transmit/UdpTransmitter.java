package nl.niels.transmit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.LinkedBlockingQueue;

public class UdpTransmitter implements Runnable {
    private final LinkedBlockingQueue<double[]> consumingQueue;
    private final DatagramSocket socket;
    private final InetAddress address;
    private static final int PORT = 4445;
    private static final Logger LOGGER = LoggerFactory.getLogger(UdpTransmitter.class);

    public UdpTransmitter(LinkedBlockingQueue<double[]> consumingQueue) throws SocketException {
        this.consumingQueue = consumingQueue;
        socket = new DatagramSocket();
        socket.setBroadcast(true);
        address = InetAddress.ofLiteral("192.168.2.255");

        LOGGER.info("UDP transmitter created with address: {} and port: {}", address.getCanonicalHostName(), PORT);
    }
    
    @Override
    public void run() {
        LOGGER.info("UDP transmitter started");

        try {
            while (true) {
                byte[] bytes = toBytes(consumingQueue.take());
                DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, PORT);
                socket.send(packet);
            }
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    protected byte[] toBytes(double[] measurements) {
        byte[] bytes = new byte[measurements.length];
        for (int i = 0; i < measurements.length; i++) {
            bytes[i] = (byte) (measurements[i] * 255);
        }
        return bytes;
    }
}
