package com.loyo.oa.v2.worksheet.api;

import com.loyo.oa.v2.activityui.worksheet.bean.Worksheet;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.network.RetrofitAdapterFactory;
import com.loyo.oa.v2.tool.Config_project;

import java.util.HashMap;

import rx.Observable;

/**
 * Created by EthanGong on 2016/12/20.
 */

public class WorksheetService {
    private static
    Observable<PaginationX<Worksheet>> getMyWorksheetList(HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_CUSTOMER())
                        .create(I2Worksheet.class)
                        .getMyWorksheetList(map)
                        .compose(RetrofitAdapterFactory.<PaginationX<Worksheet>>applySchedulers());
    }

}
