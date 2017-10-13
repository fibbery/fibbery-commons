package com.fibbery.commons.sequence.snowflake;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by jiangnenghua on 17/10/13.   desc :
 */
public class SequenceTest {

    @Test
    public void testDiffSequence() throws InterruptedException {
        HashSet<Long> ids = new HashSet<Long>(); //hashset粗略代替插入线程安全,精度要求不高
        Sequence worker1 = new Sequence(0, 0);
        Sequence worker2 = new Sequence(0, 1);
        Thread t1 = new Thread(new IDWorkerRunnable(ids, worker1));
        Thread t2 = new Thread(new IDWorkerRunnable(ids, worker2));
        t1.setDaemon(true);
        t2.setDaemon(true);
        TimeUnit.MILLISECONDS.sleep(30000);
    }


    static class IDWorkerRunnable implements Runnable {

        private Set<Long> set;

        private Sequence sequence;

        public IDWorkerRunnable(Set<Long> set, Sequence sequence) {
            this.sequence = sequence;
            this.set = set;
        }

        public void run() {
            while (true) {
                long id = sequence.nextID();
                if (!set.add(id)) {
                    System.out.println("duplicate:" + id);
                }
            }
        }
    }
}

