package com.gigaspaces.metrics.test;

import junit.framework.Assert;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.EmbeddedSpaceConfigurer;

import java.util.concurrent.BrokenBarrierException;

/**
 * @author Yohana Khoury
 * @since 12.0
 */
public class Main {
    public static ThreadBarrier threadBarrier;


    public static void main(String[] args) throws Exception {
        Integer numberOfThreads = Integer.valueOf(System.getProperty("numOfThreads", "-1"));
        if (numberOfThreads == -1) {
            throw new Exception("System property [numOfThreads] must be set");
        }

        Integer numberOfObjects = Integer.valueOf(System.getProperty("numOfObjects", "-1"));
        if (numberOfObjects == -1) {
            throw new Exception("System property [numOfObjects] must be set");
        }

        Integer payload = Integer.valueOf(System.getProperty("payload", "-1"));
        if (payload == -1) {
            throw new Exception("System property [payload] must be set (bytes)");
        }
        if (numberOfObjects % numberOfThreads != 0) {
            throw new Exception("numberOfObjects [" + numberOfObjects + "] cannot be divided by numberOfThreads [" + numberOfThreads + "] without remainder");
        }

        threadBarrier = new ThreadBarrier(numberOfThreads + 1);

        int objectsPerThread = numberOfObjects / numberOfThreads;

        EmbeddedSpaceConfigurer embeddedSpaceConfigurer = new EmbeddedSpaceConfigurer("mySpace");
        GigaSpaceConfigurer gigaSpaceConfigurer = new GigaSpaceConfigurer(embeddedSpaceConfigurer);
        GigaSpace gigaSpace = gigaSpaceConfigurer.create();


        System.out.println("numberOfThreads=" + numberOfThreads);
        System.out.println("objectsPerThread=" + objectsPerThread);


        run(gigaSpace, numberOfThreads, objectsPerThread, payload);

        System.out.println("Clearing the space");
        gigaSpace.clear(null);
        System.out.println("Count after clear: " + gigaSpace.count(null));

        run(gigaSpace, numberOfThreads, objectsPerThread, payload);

        embeddedSpaceConfigurer.close();

        System.exit(0);
    }

    private static void run(GigaSpace gigaSpace, int numberOfThreads, int objectsPerThread, int payload) throws BrokenBarrierException, InterruptedException {
        Thread[] threads = new Thread[numberOfThreads];
        for (int i = 0; i < numberOfThreads; i++) {
            threads[i] = new Thread(new Writer(gigaSpace, (i * objectsPerThread), objectsPerThread, payload));
        }


        for (int i = 0; i < numberOfThreads; i++) {
            threads[i].start();
        }

        threadBarrier.await();
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < numberOfThreads; i++) {
            threads[i].join();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Took: " + (endTime - startTime) + " ms");

        Integer totalWritten = gigaSpace.count(null);
        System.out.println("Total written: " + totalWritten);
    }
}
