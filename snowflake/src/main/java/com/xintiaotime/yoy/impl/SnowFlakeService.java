package com.xintiaotime.yoy.impl;

import com.xintiaotime.yoy.thrift.snowflake.SnowFlake;
import com.xintiaotime.yoy.base.TimeGenerator;

import org.apache.thrift.TException;

public class SnowFlakeService implements SnowFlake.Iface {
    TimeGenerator snowFlake = new TimeGenerator();

    @Override
    public long getSnowFlakeID() throws TException {
        return snowFlake.nextId();
    }
}
