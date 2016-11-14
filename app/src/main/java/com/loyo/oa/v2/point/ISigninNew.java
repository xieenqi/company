package com.loyo.oa.v2.point;


import com.loyo.oa.v2.activityui.signinnew.model.SigninNewListModel;
import com.loyo.oa.v2.beans.BaseBeanT;
import com.loyo.oa.v2.beans.PaginationX;
import java.util.Map;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.QueryMap;

/**
 * Created by yyy on 16/11/12.
 */

public interface ISigninNew {

    /**
     * 我的拜访数据
     * */
    @GET("/visit/self/")
    void selfSignin(@QueryMap Map<String, Object> params, Callback<BaseBeanT<PaginationX<SigninNewListModel>>> cb);

    /**
     * 团队拜访数据
     * */
    @GET("/visit/team/")
    void teamSignin(@QueryMap Map<String, Object> params, Callback<BaseBeanT<PaginationX<SigninNewListModel>>> cb);

    /**
     * 评论
     * */
    @POST("/comment")
    void requestComment(@Body Map<String,Object> params, Callback<Object> callback);

    /**
     * 删除评论
     * */
    @DELETE("/comment/{id}")
    void deleteComment(@Path("id") String id, Callback<Object> callback);


}
