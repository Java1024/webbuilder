package org.webbuilder.utils.counter;

import org.junit.Assert;
import org.junit.Test;
import org.webbuilder.utils.storage.counter.Counter;
import org.webbuilder.utils.storage.counter.support.local.LocalCounter;

/**
 * Created by 浩 on 2015-12-04 0004.
 */
public class CounterTest {


    @Test
    public void testCounter() throws InterruptedException {
        final Counter counter = new LocalCounter();
        final byte[] lock = new byte[0];
        for (int i = 0; i < 100; i++) {
            new Thread() {
                @Override
                public void run() {
                    for (int i1 = 0; i1 < 100; i1++) {
                        counter.next("test");
                    }
                }
            }.start();
            new Thread() {
                @Override
                public void run() {
                    for (int i1 = 0; i1 < 100; i1++) {
                        synchronized (lock) {
                            if (counter.next("test2") == 501) {
                                counter.reset("test2");
                            }
                        }
                    }
                }
            }.start();
        }
        Thread.sleep(1000);
        Assert.assertEquals(counter.count("test2"), 481);
        Assert.assertEquals(counter.count("test"), 10000);
    }

}
