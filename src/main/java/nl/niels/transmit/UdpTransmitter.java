package nl.niels.transmit;

import nl.niels.ConsumingWorker;
import nl.niels.PipelineWorker;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.LinkedBlockingQueue;

public class UdpTransmitter extends PipelineWorker implements ConsumingWorker<TransmittedSignal> {
    private final LinkedBlockingQueue<double[]> consumingQueue;
    private final DatagramSocket socket;
    private final InetAddress address;
    private static final int PORT = 4445;

    public UdpTransmitter(LinkedBlockingQueue<double[]> consumingQueue) throws SocketException {
        this.consumingQueue = consumingQueue;
        socket = new DatagramSocket();
        socket.setBroadcast(true);
        address = InetAddress.ofLiteral("192.168.2.255");

        LOGGER.info("UDP transmitter created with address: {} and port: {}", address.getCanonicalHostName(), PORT);
    }

    @Override
    protected void process() {
        try {
            byte[] bytes = toBytes(consumingQueue.take());
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, PORT);
            socket.send(packet);
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

    @Override
    public void consume(TransmittedSignal signal) {

    }
}
