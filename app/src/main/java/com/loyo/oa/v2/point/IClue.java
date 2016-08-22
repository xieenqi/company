package com.loyo.oa.v2.point;

import com.loyo.oa.v2.activityui.clue.bean.IdName;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.http.GET;

/**
 * 线索相关接口 相关
 * Created by xeq on 16/8/22.
 */
public interface IClue {
    @GET("/salesleads/sources")
    void getSource(Callback<ArrayList<IdName>> callback);


}
