package com.loyo.oa.v2.common.event;


import org.greenrobot.eventbus.EventBus;

/**
 * Created by EthanGong on 16/9/1.
 */
public class AppBus extends EventBus {
    private static AppBus bus;
    public static AppBus getInstance() {
        if (bus == null) {
            synchronized (AppBus.class) {
                if (bus == null) {
                    bus = new AppBus();
                }
            }
        }
        return bus;
    }
}
