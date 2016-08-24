package com.loyo.oa.v2.point;

import com.loyo.oa.v2.activityui.clue.bean.ClueDetail;
import com.loyo.oa.v2.activityui.clue.bean.ClueList;
import com.loyo.oa.v2.activityui.clue.bean.SourcesData;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.QueryMap;

/**
 * 线索相关接口 相关
 * Created by xeq on 16/8/22.
 */
public interface IClue {

    /**
     * 获取 线索来源
     *
     * @param callback /saleslead/source
     */
    @GET("/saleslead/source")
    void getSource(Callback<SourcesData> callback);

    /**
     * 我的线索列表
     * */
    @GET("/salesleads/mobile")
    void getMyCluelist(@QueryMap HashMap<String,Object> map, Callback<ClueList> callback);

    /**
     * 团队线索列表
     */
    @GET("/salesleads/mobile/team")
    void getTeamCluelist(@QueryMap HashMap<String, Object> mao, Callback<ClueList> callback);


    /**
     * 新建线索 表单传输
     *
     * @param body
     * @param callback
     */
    @POST("/salesleads")
    void addClue(@Body Map<String, Object> body, Callback<Object> callback);


    /**
     * 获取 线索详情
     */
    @GET("/salesleads/mobile/{id}")
    void getClueDetail(@Path("id") String id, Callback<ClueDetail> callback);

    /**
     * 编辑 回款记录
     */
    @PUT("/saleslead/source/{id}")
    void editClue(@Path("id") String id, @Body HashMap<String, Object> map, Callback<Object> callback);
}
