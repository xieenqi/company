package com.loyo.oa.v2.announcement.api;

import com.loyo.oa.v2.beans.Bulletin;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.network.RetrofitAdapterFactory;
import com.loyo.oa.v2.tool.Config_project;

import java.util.HashMap;

import rx.Observable;

/**
 * Created by EthanGong on 2016/12/21.
 */

public class AnnouncementService {

    public static
    Observable<PaginationX<Bulletin>> getNoticeList(HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL())
                        .create(IAnnouncement.class)
                        .getNoticeList(map)
                        .compose(RetrofitAdapterFactory.<PaginationX<Bulletin>>compatApplySchedulers());
    }

    public static
    Observable<Bulletin> publishNotice(HashMap<String, Object> body) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL())
                        .create(IAnnouncement.class)
                        .publishNotice(body)
                        .compose(RetrofitAdapterFactory.<Bulletin>compatApplySchedulers());
    }
}
