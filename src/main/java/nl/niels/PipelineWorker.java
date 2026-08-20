package nl.niels;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class PipelineWorker implements Runnable {
    protected static final Logger LOGGER = LoggerFactory.getLogger(PipelineWorker.class);

    protected abstract void process();

    @Override
    public void run() {
        while(true) {
            try {
                process();
            } catch (Exception e) {
                throw new WorkerException("Worker stopped");
            }
        }
    }
}
