package com.loyo.oa.v2.common.Event;

import com.squareup.otto.Bus;

/**
 * Created by EthanGong on 16/9/1.
 */
public class AppBus extends Bus {
    private static AppBus bus;

    public static AppBus getInstance() {
        if (bus == null) {
            bus = new AppBus();
        }
        return bus;
    }
}
