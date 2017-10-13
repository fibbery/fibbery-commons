package com.fibbery.commons.sequence.snowflake;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by jiangnenghua on 17/10/12.
 * desc :
 * 高并发场景下System.currentTimeMillis()的性能问题的优化
 * System.currentTimeMillis()的调用比new一个普通对象要耗时的多
 * System.currentTimeMillis()之所以慢是因为去跟系统打了一次交道
 * 后台定时更新时钟，JVM退出时，线程自动回收
 */
public class SystemColock {

    private final long period;

    private final AtomicLong currentMillions;

    private SystemColock(long period) {
        this.period = period;
        this.currentMillions = new AtomicLong(System.currentTimeMillis());
        updateTimeSchedule();
    }

    /**
     * 保证每毫秒更新一次
     */
    private void updateTimeSchedule() {
        /*单线程保证次序*/
        ScheduledExecutorService pool = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "System Colock");
                t.setDaemon(true);
                return t;
            }
        });

        pool.scheduleAtFixedRate(new Runnable() {
            public void run() {
                currentMillions.set(System.currentTimeMillis());
            }
        }, period, period, TimeUnit.MILLISECONDS);
    }

    private static class InstanceHolder{
        public static final SystemColock CLOCK = new SystemColock(1);
    }

    public static long currentMillions() {
        return InstanceHolder.CLOCK.currentMillions.get();
    }


}
