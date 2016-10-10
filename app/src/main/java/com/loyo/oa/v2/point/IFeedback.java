package com.loyo.oa.v2.point;

import com.loyo.oa.v2.beans.FeedBackCommit;
import com.loyo.oa.v2.activityui.other.model.Feedback;
import com.loyo.oa.v2.beans.PaginationX;

import java.util.HashMap;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;

/**
 * Created by pj on 15/5/13.
 */
public interface IFeedback {

    @GET("/feedback/")
    void getMy(retrofit.Callback<PaginationX<Feedback>> cb);

    @POST("/feedback/")
    void create(@Body HashMap<String, Object> body,
                retrofit.Callback<FeedBackCommit> cb);
}
