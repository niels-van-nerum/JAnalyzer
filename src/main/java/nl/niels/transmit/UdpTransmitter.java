package nl.niels.transmit;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.LinkedBlockingQueue;

public class UdpTransmitter implements Runnable {
    private final LinkedBlockingQueue<double[]> consumingQueue;
    private DatagramSocket socket;
    private InetAddress address;

    public UdpTransmitter(LinkedBlockingQueue<double[]> consumingQueue) throws SocketException {
        this.consumingQueue = consumingQueue;
        socket = new DatagramSocket();
        socket.setBroadcast(true);
        address = InetAddress.ofLiteral("192.168.1.255");
    }
    
    @Override
    public void run() {
        try {
            while (true) {
                byte[] bytes = toBytes(consumingQueue.take());
                DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, 4445);
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
