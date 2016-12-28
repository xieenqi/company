package com.loyo.oa.tracklog.api;

import com.loyo.oa.v2.beans.TrackLog;
import com.loyo.oa.v2.beans.TrackRule;
import com.loyo.oa.v2.network.RetrofitAdapterFactory;
import com.loyo.oa.v2.tool.Config_project;

import java.util.HashMap;

import rx.Observable;

/**
 * Created by EthanGong on 2016/12/22.
 */

public class TrackLogService {

    public static
    Observable<TrackLog> newUploadTrack(HashMap<String, Object> body) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.NEW_UPLOCATION())
                        .create(ITrackLog.class)
                        .newUploadTrack(body)
                        .compose(RetrofitAdapterFactory.<TrackLog>compatApplySchedulers());
    }

    public static
    Observable<TrackRule> getTrackRule() {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL())
                        .create(ITrackLog.class)
                        .getTrackRule()
                        .compose(RetrofitAdapterFactory.<TrackRule>compatApplySchedulers());
    }

    public static
    Observable<Object> uploadTrackLogs(HashMap<String, Object> tracklogs) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL())
                        .create(ITrackLog.class)
                        .uploadTrackLogs(tracklogs)
                        .compose(RetrofitAdapterFactory.<Object>compatApplySchedulers());
    }

    /**
     * 轨迹用户在线
     */
    public static
    Observable<Object> getUserOneLine() {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL())
                        .create(ITrackLog.class)
                        .getUserOneLine()
                        .compose(RetrofitAdapterFactory.<Object>compatApplySchedulers());
    }
}
