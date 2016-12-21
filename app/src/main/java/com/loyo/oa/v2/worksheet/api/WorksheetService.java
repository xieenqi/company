package com.loyo.oa.v2.worksheet.api;

import com.loyo.oa.v2.activityui.worksheet.bean.EventDetail;
import com.loyo.oa.v2.activityui.worksheet.bean.Worksheet;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetDetail;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetEvent;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetInfo;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetOrder;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetTemplate;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.network.RetrofitAdapterFactory;
import com.loyo.oa.v2.tool.Config_project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import rx.Observable;

/**
 * Created by EthanGong on 2016/12/20.
 */

public class WorksheetService {
    public static
    Observable<PaginationX<Worksheet>> getMyWorksheetList(HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_CUSTOMER())
                        .create(I2Worksheet.class)
                        .getMyWorksheetList(map)
                        .compose(RetrofitAdapterFactory.<PaginationX<Worksheet>>applySchedulers());
    }

    public static
    Observable<EventDetail> getEventDetail(String id, HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_CUSTOMER())
                        .create(I2Worksheet.class)
                        .getEventDetail(id, map)
                        .compose(RetrofitAdapterFactory.<EventDetail>applySchedulers());
    }

    public static
    Observable<Object> setEventPerson(String id, Map<String, Object> body) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_CUSTOMER())
                        .create(I2Worksheet.class)
                        .setEventPerson(id, body)
                        .compose(RetrofitAdapterFactory.<Object>applySchedulers());
    }

    public static
    Observable<PaginationX<Worksheet>> getWorksheetListByOrder(String oid, HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_CUSTOMER())
                        .create(I2Worksheet.class)
                        .getWorksheetListByOrder(oid, map)
                        .compose(RetrofitAdapterFactory.<PaginationX<Worksheet>>applySchedulers());
    }

    public static
    Observable<PaginationX<WorksheetEvent>> getResponsableWorksheetList(HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_CUSTOMER())
                        .create(I2Worksheet.class)
                        .getResponsableWorksheetList(map)
                        .compose(RetrofitAdapterFactory.<PaginationX<WorksheetEvent>>applySchedulers());
    }

    public static
    Observable<PaginationX<Worksheet>> getTeamWorksheetList(HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_CUSTOMER())
                        .create(I2Worksheet.class)
                        .getTeamWorksheetList(map)
                        .compose(RetrofitAdapterFactory.<PaginationX<Worksheet>>applySchedulers());
    }

    public static
    Observable<Worksheet> addWorksheet(Map<String, Object> body) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_CUSTOMER())
                        .create(I2Worksheet.class)
                        .addWorksheet(body)
                        .compose(RetrofitAdapterFactory.<Worksheet>applySchedulers());
    }

    public static
    Observable<ArrayList<WorksheetTemplate>> getWorksheetTypesList() {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_CUSTOMER())
                        .create(I2Worksheet.class)
                        .getWorksheetTypesList()
                        .compose(RetrofitAdapterFactory.<ArrayList<WorksheetTemplate>>applySchedulers());
    }

    public static
    Observable<WorksheetDetail> getWorksheetDetail(String id) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_CUSTOMER())
                        .create(I2Worksheet.class)
                        .getWorksheetDetail(id)
                        .compose(RetrofitAdapterFactory.<WorksheetDetail>applySchedulers());
    }

    public static
    Observable<Object> setStopWorksheet(String id, HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_CUSTOMER())
                        .create(I2Worksheet.class)
                        .setStopWorksheet(id, map)
                        .compose(RetrofitAdapterFactory.<Object>applySchedulers());
    }

    public static
    Observable<Object> setAllEventPerson(Map<String, Object> body) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_CUSTOMER())
                        .create(I2Worksheet.class)
                        .setAllEventPerson(body)
                        .compose(RetrofitAdapterFactory.<Object>applySchedulers());
    }

    public static
    Observable<WorksheetInfo> getWorksheetInfo(String id) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_CUSTOMER())
                        .create(I2Worksheet.class)
                        .getWorksheetInfo(id)
                        .compose(RetrofitAdapterFactory.<WorksheetInfo>applySchedulers());
    }

    public static
    Observable<Object> setEventSubmit(String id, HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_CUSTOMER())
                        .create(I2Worksheet.class)
                        .setEventSubmit(id, map)
                        .compose(RetrofitAdapterFactory.<Object>applySchedulers());
    }

    public static
    Observable<PaginationX<WorksheetOrder>> getWorksheetOrdersList(HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_CUSTOMER())
                        .create(I2Worksheet.class)
                        .getWorksheetOrdersList(map)
                        .compose(RetrofitAdapterFactory.<PaginationX<WorksheetOrder>>applySchedulers());
    }


}
