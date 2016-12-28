package com.loyo.oa.v2.announcement.api;

import com.loyo.oa.v2.beans.Bulletin;
import com.loyo.oa.v2.beans.PaginationX;

import java.util.HashMap;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.QueryMap;
import rx.Observable;

/**
 * Created by EthanGong on 2016/12/21.
 */

public interface IAnnouncement {
    /**
     * 分页获取通知列表
     *
     * @param map
     */
    @GET("/bulletin/query")
    Observable<PaginationX<Bulletin>> getNoticeList(@QueryMap HashMap<String, Object> map);

    /**
     * 发布通知
     *
     * @param body
     */
    @POST("/bulletin")
    Observable<Bulletin> publishNotice(@Body HashMap<String, Object> body);
}
