package nl.niels;

import java.util.concurrent.LinkedBlockingQueue;

public interface ProducingWorker<O> {
    public LinkedBlockingQueue<O> consumingQueue;

    void consume(O item);
}
