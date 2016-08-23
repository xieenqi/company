package com.loyo.oa.v2.point;

import com.loyo.oa.v2.activityui.clue.bean.ClueList;
import com.loyo.oa.v2.activityui.clue.bean.SourcesData;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.FieldMap;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.QueryMap;

/**
 * 线索相关接口 相关
 * Created by xeq on 16/8/22.
 */
public interface IClue {


    @GET("/salesleads/sources")
    void getSource(Callback<SourcesData> callback);

    /**
     * 我的线索列表
     * */
    @GET("/salesleads/own")
    void getMyCluelist(@QueryMap HashMap<String,Object> map, Callback<ClueList> callback);

}
