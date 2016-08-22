package com.loyo.oa.v2.activityui.clue.common;

import com.loyo.oa.v2.activityui.clue.bean.IdName;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.IClue;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RestAdapterFactory;

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
    public static String[] getSourceData() {
        final List<String> data = new ArrayList<>();
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(IClue.class).getSource(new Callback<ArrayList<IdName>>() {
            @Override
            public void success(ArrayList<IdName> idName, Response response) {
                HttpErrorCheck.checkResponse("线索来源：", response);
                for (IdName ele : idName) {
                    data.add(ele.name);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                HttpErrorCheck.checkError(error);
            }
        });

        return (String[]) data.toArray();
    }
}
