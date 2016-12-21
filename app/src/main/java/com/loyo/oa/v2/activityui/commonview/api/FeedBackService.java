package com.loyo.oa.v2.activityui.commonview.api;

import com.loyo.oa.v2.activityui.clue.api.IClue;
import com.loyo.oa.v2.beans.FeedBackCommit;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.network.RetrofitAdapterFactory;
import com.loyo.oa.v2.tool.Config_project;

import java.util.HashMap;

import rx.Observable;

/**
 * 意见反馈，网络服务
 * Created by jie on 16/12/21.
 */

public class FeedBackService {
    //意见反馈
    public static Observable<FeedBackCommit> create(HashMap<String, Object> params) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/FinalVariables.URL_FEEDBACK)
                        .create(IFeedback.class)
                        .create(params)
                        .compose(RetrofitAdapterFactory.<FeedBackCommit>compatApplySchedulers());
    }
}
