package com.loyo.oa.v2.tool;

/**
 * Created by EthanGong on 16/9/29.
 */
public class TrackLocationManager {
    private static TrackLocationManager ourInstance = new TrackLocationManager();

    public static TrackLocationManager getInstance() {
        return ourInstance;
    }

    private TrackLocationManager() {
    }
}
