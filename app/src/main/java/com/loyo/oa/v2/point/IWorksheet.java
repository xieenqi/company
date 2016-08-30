package com.loyo.oa.v2.point;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * 工单接口相关
 * Created by xeq on 16/8/30.
 */
public interface IWorksheet {

    /**
     * 获取工单详细信息
     */
    @GET("/worksheets/{id}")
    void getWorksheetDetail(@Path("id") String id, Callback<Object> callback);
}
