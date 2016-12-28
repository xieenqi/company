package com.loyo.oa.v2.activityui.dashboard.api;

import com.loyo.oa.v2.activityui.dashboard.model.CsclueFowUp;

import java.util.HashMap;
import retrofit.http.GET;
import retrofit.http.QueryMap;
import rx.Observable;

/**
 * Created by yyy on 16/12/28.
 */

public interface IDashBoard {

    /**
     * 客户跟进/线索跟进
     */
    @GET("/statistics/sale-activity/mobile")
    Observable<CsclueFowUp> getFupCusClue(@QueryMap HashMap<String, Object> map);

}
