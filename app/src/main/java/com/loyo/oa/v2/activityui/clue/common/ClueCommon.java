package com.loyo.oa.v2.activityui.clue.common;

import com.loyo.oa.v2.activityui.clue.bean.IdName;
import com.loyo.oa.v2.point.IClue;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by xeq on 16/8/22.
 * 线索公用调用 的相关
 */
public class ClueCommon {
    /**
     * 获取 线索来源
     */
    public static void getSourceData() {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(IClue.class).getSource(new Callback<ArrayList<IdName>>() {
            @Override
            public void success(ArrayList<IdName> idName, Response response) {

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}
