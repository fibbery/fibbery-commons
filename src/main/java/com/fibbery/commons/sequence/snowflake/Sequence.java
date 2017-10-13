package com.fibbery.commons.sequence.snowflake;

import org.apache.commons.lang3.StringUtils;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;

/**
 * Created by jiangnenghua on 17/10/12.
 * desc :
 * 基于twitter的snowflake算法产生的有序的分布式id
 * 结构如下, 64位的long型
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000
 * 1位作为标志位,代表整数
 * 41 位的时间戳,值为(当前时间毫秒数 - 系统开始时间毫秒数)
 * 10 位为机器位置(其中5位datacenterId, 5位为workerId)
 * 12位序列，毫秒内的计数，12位的计数顺序号支持每个节点每毫秒(同一机器，同一时间截)产生4096个ID序号
 */
public class Sequence {

    /*开始时间戳*/
    private static final long startTimestamp = 1507799015726L;

    /* datacenterId 占位数*/
    private static final long datacenterIdBits = 5L;

    /* workerId 占位数*/
    private static final long workerIdBits = 5;

    /* 最大的datacenterId */
    private static final long maxDatacenterId = ~(-1L << workerIdBits);

    /* 最大的workerId */
    private static final long maxWorkerId = ~(-1 << workerIdBits);

    /* 序列的占位数*/
    private static final long sequenceBits = 12L;

    /* workerId 向左移动的位数*/
    private static final long workerIdShift = sequenceBits;

    /* datacenterId 向左移动的位数*/
    private static final long datacenterIdShift = sequenceBits + workerIdBits;

    /* 时间戳需要移动的位数 */
    private static final long timestampShift = sequenceBits + workerIdBits + datacenterIdBits;

    /*序列位掩码*/
    private static final long sequenceMask = ~(-1 << sequenceBits);

    private long workerId;

    private long datacenterId;

    private long sequence = 0L;

    private long lastUpdateTime = -1L;  /*上一次产生序列的时间*/

    public Sequence() {
        this.datacenterId = getDatacenterId();
        this.workerId = getWorkerId(datacenterId);
    }

    public Sequence(long datacenterId, long workerId) {
        if (datacenterId < 0 || datacenterId > maxDatacenterId) {
            throw new RuntimeException(
                    String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        }
        if (workerId < 0 || workerId > maxDatacenterId) {
            throw new RuntimeException(
                    String.format("worker Id can't be greater than %d or less than 0", maxDatacenterId));
        }

        this.datacenterId = datacenterId;
        this.workerId = workerId;
    }

    /**
     * mac + javaPid 16位
     * @param datacenterId
     * @return
     */
    private long getWorkerId(long datacenterId) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(datacenterId);
        String name = ManagementFactory.getRuntimeMXBean().getName();
        if (StringUtils.isNotEmpty(name)) {
            buffer.append(name.split("@")[0]); //pid
        }
        //获取 mac+pid的16个低位地址
        return (buffer.toString().hashCode() & 0xffff) % (maxWorkerId + 1);
    }

    private long getDatacenterId() {
        long datacenterId = 0L;
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(localHost);
            if (null == network) {
                datacenterId = 1L;
            } else {
                byte[] mac = network.getHardwareAddress();
                //网卡mac地址是6个字节(48 bits),这里机器位只有10位,所以只取最后两个字节的,然后右移6位
                datacenterId = ((long) mac[mac.length - 1] & 0x000000ff) | (((long) mac[mac.length - 2] << 8) & 0x0000ff00) >> 6;
                datacenterId = datacenterId % (maxDatacenterId + 1);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return datacenterId;
    }

    /*获取下一个id*/
    public synchronized long nextID(){
        long timestamp = genTime();
        //闰秒情况
        if(timestamp < lastUpdateTime){
            long offset = lastUpdateTime = timestamp;
            if (offset <= 5) {
                try {
                    wait(offset << 1);
                    timestamp = genTime();
                    if (timestamp < lastUpdateTime)
                        throw new RuntimeException((String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", offset)));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                throw new RuntimeException((String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", offset)));
            }
        }

        if (timestamp == lastUpdateTime) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) { // 同时间内号码取完,则等到下一个时间
                timestamp = tillNextTime();
            }
        }else{
            sequence = 0;
        }

        lastUpdateTime = timestamp;

        return ((lastUpdateTime - startTimestamp) << timestampShift) |
                (datacenterId << datacenterIdShift) |
                (workerId << workerIdShift) |
                sequence;
    }

    /* 取到lastUpdateTime的下一个时间点*/
    private long tillNextTime() {
        long timestamp = genTime();
        while (timestamp <= lastUpdateTime) {
            timestamp = genTime();
        }
        return timestamp;
    }

    private long genTime() {
        return SystemColock.currentMillions();
    }
}
