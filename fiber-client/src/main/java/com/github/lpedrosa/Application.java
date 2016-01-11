package com.github.lpedrosa;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.Strand;
import co.paralleluniverse.strands.Strand.UncaughtExceptionHandler;
import co.paralleluniverse.strands.SuspendableRunnable;

import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {
    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    private static void fiberMain(final String[] args) throws SuspendExecution, InterruptedException {
        AsyncClient asyncClient = new AsyncClientImpl();

        FiberSyncClient client = FiberSyncClient.wrap(asyncClient);

        try {
            String value = client.getValue(12);
            LOG.info("Got back value {}", value);
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }

        try {
            client.getValue(42);
        } catch (ClientException e) {
            LOG.info("So 42 is not always the answer...", e);
        }
    }

    public static void main(final String[] args) throws ExecutionException, InterruptedException {
        Fiber f = new Fiber<>(new SuspendableRunnable() {
            @Override
            public void run() throws SuspendExecution, InterruptedException {
                fiberMain(args);
            }
        }).start();

        f.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Strand f, Throwable e) {
                LOG.error("[fiber:{}] com.github.lpedrosa.Application failed with error:", f, e);
            }
        });

        f.join();
        LOG.info("All fibers are done!");
    }
}
