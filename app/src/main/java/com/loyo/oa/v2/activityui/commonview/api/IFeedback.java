package com.loyo.oa.v2.activityui.commonview.api;

import com.loyo.oa.v2.activityui.other.model.Feedback;
import com.loyo.oa.v2.beans.FeedBackCommit;
import com.loyo.oa.v2.beans.PaginationX;

import java.util.HashMap;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import rx.Observable;

/**
 * Created by jie on 16/12/21.
 */

public interface IFeedback {
//    这个接口没有看到
//    @GET("/feedback/")
//    Observable<PaginationX<Feedback>> getMy();

    @POST("/feedback/")
    Observable<FeedBackCommit> create(@Body HashMap<String, Object> map);
}
