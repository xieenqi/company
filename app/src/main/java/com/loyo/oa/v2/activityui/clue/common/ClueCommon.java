package com.loyo.oa.v2.activityui.clue.common;

import com.loyo.oa.v2.activityui.clue.model.IdName;
import com.loyo.oa.v2.activityui.clue.model.SourcesData;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.IClue;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.SharedUtil;

import java.util.ArrayList;
import java.util.List;

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
        final List<String> data = new ArrayList<>();
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(IClue.class).getSource(new Callback<SourcesData>() {
            @Override
            public void success(SourcesData idName, Response response) {
                HttpErrorCheck.checkResponse("线索来源：", response);
                if(null != idName){
                    for (IdName ele : idName.data) {
                        data.add(ele.name);
                    }
                    SharedUtil.remove(MainApp.getMainApp(), ExtraAndResult.SOURCES_DATA);
                    SharedUtil.put(MainApp.getMainApp(), ExtraAndResult.SOURCES_DATA, MainApp.gson.toJson(data.toArray(new String[data.size()])));
                }
            }

            @Override
            public void failure(RetrofitError error) {
                HttpErrorCheck.checkError(error);
            }
        });
    }
}
