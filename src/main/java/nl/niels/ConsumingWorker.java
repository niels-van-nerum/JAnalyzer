package nl.niels;

public interface ConsumingWorker<I> {
    void consume(I signal);
}
