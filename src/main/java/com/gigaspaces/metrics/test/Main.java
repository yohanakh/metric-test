package com.gigaspaces.metrics.test;

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

        Integer numOfRepeats = Integer.valueOf(System.getProperty("numOfRepeats", "-1"));
        if (numOfRepeats == -1) {
            throw new Exception("System property [numOfRepeats] must be set");
        }

        threadBarrier = new ThreadBarrier(numberOfThreads + 1);

        int objectsPerThread = numberOfObjects / numberOfThreads;

        EmbeddedSpaceConfigurer embeddedSpaceConfigurer = new EmbeddedSpaceConfigurer("mySpace");
        embeddedSpaceConfigurer.lookupGroups("metrickobiyohana");
        GigaSpaceConfigurer gigaSpaceConfigurer = new GigaSpaceConfigurer(embeddedSpaceConfigurer);
        GigaSpace gigaSpace = gigaSpaceConfigurer.create();


        System.out.println("numberOfThreads=" + numberOfThreads);
        System.out.println("objectsPerThread=" + objectsPerThread);

        Writer[] writers = new Writer[numberOfThreads];
        for (int i = 0; i < numberOfThreads; i++) {
            writers[i] = new Writer(gigaSpace, (i * objectsPerThread), objectsPerThread, payload);
        }

        long lastRun=0;

        for (int i=0; i<numOfRepeats; i++) {
            System.out.println("Starting repeat #"+i);
            lastRun = run(gigaSpace, writers, numberOfThreads);

            System.out.println("Clearing the space");
            gigaSpace.clear(null);
            System.out.println("Count after clear: " + gigaSpace.count(null));

            System.gc();
        }

        run(gigaSpace, writers, numberOfThreads);

        embeddedSpaceConfigurer.close();


        System.out.println("Last run took " + lastRun + " ms");
        System.exit(0);
    }

    private static long run(GigaSpace gigaSpace, Writer[] writers, int numberOfThreads) throws BrokenBarrierException, InterruptedException {
        Thread[] threads = new Thread[numberOfThreads];
        for (int i=0; i<numberOfThreads; i++) {
            threads[i] = new Thread(writers[i]);
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
        long totalTime = (endTime - startTime);
        System.out.println("Took: " + totalTime + " ms");

        Integer totalWritten = gigaSpace.count(null);
        System.out.println("Total written: " + totalWritten);
        return totalTime;
    }
}
