package com.loyo.oa.v2.point;

import com.loyo.oa.v2.activityui.other.model.Tag;

import java.util.ArrayList;

import retrofit.http.GET;

/**
 * com.loyo.oa.v2.point
 * 描述 :
 * 作者 : ykb
 * 时间 : 15/7/17.
 */
public interface ITag {
    @GET("/tags?bizType=6")
    void GetTags(retrofit.Callback<ArrayList<Tag>> cb);
}
