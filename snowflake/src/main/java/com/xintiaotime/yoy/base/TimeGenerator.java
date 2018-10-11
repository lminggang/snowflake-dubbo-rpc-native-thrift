package com.xintiaotime.yoy.base;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

/**
 * twitter的snowflake算法 -- java实现
 * @author liminggang
 * @date 2018/10/11
 */
public class TimeGenerator {
    // 实例化XML对象
    private static XMLConfiguration config;
    static {
        try {
            config = new XMLConfiguration("config/config.xml");
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * 每一部分占用的位数
     */
    private final static long TYPE_BIT = Long.valueOf(config.getString("TYPE_BIT")); // 序号类型占用位
    private final static long TIMESTAMP_BIT = Long.valueOf(config.getString("TIMESTAMP_BIT")); // 时间戳占用位数
    private final static long SEQUENCE_BIT = Long.valueOf(config.getString("SEQUENCE_BIT")); //序列号占用的位数
    private final static long MACHINE_BIT = Long.valueOf(config.getString("MACHINE_BIT"));   //机器标识占用的位数
    private final static long DATACENTER_BIT = Long.valueOf(config.getString("DATACENTER_BIT"));//数据中心占用的位数



    /**
     * 每一部分的最大值
     */
    private final static long MAX_DATACENTER_NUM = -1L ^ (-1L << DATACENTER_BIT);
    private final static long MAX_MACHINE_NUM = -1L ^ (-1L << MACHINE_BIT);
    private final static long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT);
    private final static long MAX_TYPE = -1L ^ (-1L << TYPE_BIT);

    /**
     * 每一部分向左的位移
     */
    private final static long MACHINE_LEFT = SEQUENCE_BIT;
    private final static long DATACENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
    private final static long TIMESTMP_LEFT = DATACENTER_LEFT + DATACENTER_BIT;
    private final static long TYPE_LEFT = TIMESTMP_LEFT + TIMESTAMP_BIT;

    private final long type = Long.valueOf(config.getString("type"));;  // 序号类型 0 ~ 63
    private final long datacenterId = Long.valueOf(config.getString("datacenterId"));;  //数据中心 0 ~ 31
    private final long machineId = Long.valueOf(config.getString("machineId"));;     //机器标识 0 ~ 31
    private long sequence = 0L; //序列号
    private long lastStmp = -1L;//上一次时间戳

    public TimeGenerator() {
        if (TYPE_BIT + TIMESTAMP_BIT + SEQUENCE_BIT + MACHINE_BIT + DATACENTER_BIT != 63){
            throw new IllegalArgumentException("TYPE_BIT + TIMESTAMP_BIT + SEQUENCE_BIT + MACHINE_BIT + DATACENTER_BIT not equal to 63bit");
        }
        if (datacenterId > MAX_DATACENTER_NUM || datacenterId < 0) {
            throw new IllegalArgumentException("datacenterId can't be greater than MAX_DATACENTER_NUM or less than 0");
        }
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException("machineId can't be greater than MAX_MACHINE_NUM or less than 0");
        }
        if (type > MAX_TYPE || type < 0) {
            throw new IllegalArgumentException("type can't be greater than MAX_TYPE or less than 0");
        }
    }

    /**
     * 产生下一个ID
     *
     * @return
     */
    public synchronized long nextId() {
        long currStmp = getNewstmp();
        if (currStmp < lastStmp) {
            throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
        }

        if (currStmp == lastStmp) {
            //相同秒内，序列号自增
            sequence = (sequence + 1) & MAX_SEQUENCE;
            //同一秒的序列数已经达到最大
            if (sequence == 0L) {
                currStmp = getNextMill();
            }
        } else {
//            System.out.println("一秒最大支撑数量: " + sequence);
            //不同秒内，序列号置为0
            sequence = 0L;
        }

        lastStmp = currStmp;
        return type << TYPE_LEFT
                | currStmp << TIMESTMP_LEFT //时间戳部分
                | datacenterId << DATACENTER_LEFT       //数据中心部分
                | machineId << MACHINE_LEFT             //机器标识部分
                | sequence;                             //序列号部分
    }

    private long getNextMill() {
        long mill = getNewstmp();
        while (mill <= lastStmp) {
            mill = getNewstmp();
        }
        return mill;
    }

    private long getNewstmp() {
        return System.currentTimeMillis() / 1000;
    }
}

