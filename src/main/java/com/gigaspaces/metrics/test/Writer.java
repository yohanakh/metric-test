package com.gigaspaces.metrics.test;

import org.openspaces.core.GigaSpace;

/**
 * @author Yohana Khoury
 * @since 12.0
 */
public class Writer implements Runnable {
    private GigaSpace _gigaSpace;
    private int _from;
    private int _count;
    private TradePojo[] _pojos;

    public Writer(GigaSpace gigaSpace, int from, int count, int payload) {
        this._gigaSpace = gigaSpace;
        this._from = from;
        this._count = count;

        _pojos = new TradePojo[count];

        for (int i = 0; i < _count; i++) {
            _pojos[i] = TradeUtil.newInstanceByPrimaryKey((i + _from), payload);
        }
    }

    public void run() {
        try {
            Main.threadBarrier.await();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to call await on barrier", e);
        }

        System.out.println("Starting to write from [" + _from + "] to [" + (_from + _count - 1) + "]");
        for (int i = 0; i < _count; i++) {
//            if (i % 10000 == 0) {
//                System.out.println("Count: " + _gigaSpace.count(null));
//            }
            _gigaSpace.write(_pojos[i]);
        }
        System.out.println("Finished to write from [" + _from + "] to [" + (_from + _count - 1) + "]");
    }
}
