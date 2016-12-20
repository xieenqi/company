package com.loyo.oa.v2.activityui.clue.api;

import android.util.Log;

import com.loyo.oa.v2.activityui.clue.model.ClueDetailWrapper;
import com.loyo.oa.v2.activityui.clue.model.ClueFollowGroupModel;
import com.loyo.oa.v2.activityui.clue.model.ClueList;
import com.loyo.oa.v2.activityui.clue.model.SourcesData;
import com.loyo.oa.v2.activityui.customer.model.CallBackCallid;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.BaseBeanT;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.network.RetrofitAdapterFactory;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;

import java.util.HashMap;
import rx.Observable;

/**
 * 销售线索 网络请求
 * Created by jie on 16/12/20.
 */

public class ClueService {
    private static String TAG="ClueService";
    //我的销售线索
    public static Observable<ClueList> getMyClueList(HashMap<String, Object> params) {
        LogUtil.d("getMyClueList() called with: params = [" + params + "]");
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(IClue.class)
                        .getMyClueList(params)
                        .compose(RetrofitAdapterFactory.<ClueList>compatApplySchedulers());
    }

    //团队销售线索
    public static Observable<ClueList> getTeamClueList(HashMap<String, Object> params) {
        LogUtil.d("getTeamClueList() called with: params = [" + params + "]");
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(IClue.class)
                        .getTeamClueList(params)
                        .compose(RetrofitAdapterFactory.<ClueList>compatApplySchedulers());
    }
    // 线索下的 跟进
    public static Observable<PaginationX<ClueFollowGroupModel>> followUp(HashMap<String, Object> params) {
        LogUtil.d("followUp() called with: params = [" + params + "]");
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(IClue.class)
                        .followUp(params)
                        .compose(RetrofitAdapterFactory.<PaginationX<ClueFollowGroupModel>>compatApplySchedulers());
    }
    // 获取 线索来源
    public static Observable<SourcesData> getSource() {
        LogUtil.d("getSource() called");
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(IClue.class)
                        .getSource()
                        .compose(RetrofitAdapterFactory.<SourcesData>compatApplySchedulers());
    }

    // 新建线索 表单传输
    public static Observable<ClueDetailWrapper> addClue(HashMap<String, Object> params) {
        LogUtil.d("addClue() called with: params = [" + params + "]");
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(IClue.class)
                        .addClue(params)
                        .compose(RetrofitAdapterFactory.<ClueDetailWrapper>compatApplySchedulers());
    }

    // 新建线索 表单传输
    public static Observable<BaseBeanT<ClueDetailWrapper.ClueDetail>> getClueDetail(String params) {
        LogUtil.d("getClueDetail() called with: params = [" + params + "]");
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(IClue.class)
                        .getClueDetail(params)
                        .compose(RetrofitAdapterFactory.<BaseBeanT<ClueDetailWrapper.ClueDetail>>compatApplySchedulers());
    }

    //编辑 线索
    public static Observable<Object> editClue(String id,HashMap<String, Object> params) {
        LogUtil.d("editClue() called with: id = [" + id + "], params = [" + params + "]");
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(IClue.class)
                        .editClue(id,params)
                        .compose(RetrofitAdapterFactory.<Object>compatApplySchedulers());
    }

    // 删除
    public static Observable<Object> deleteClue(HashMap<String, Object> params) {
        LogUtil.d("deleteClue() called with: params = [" + params + "]");
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(IClue.class)
                        .deleteClue(params)
                        .compose(RetrofitAdapterFactory.<Object>compatApplySchedulers());
    }

    // 删除
    public static Observable<Object> transferClue(HashMap<String, Object> params) {
        LogUtil.d( "transferClue() called with: params = [" + params + "]");
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(IClue.class)
                        .transferClue(params)
                        .compose(RetrofitAdapterFactory.<Object>compatApplySchedulers());
    }
    // 删除
    public static Observable<CallBackCallid> getCallReturnInfo(HashMap<String, Object> params) {
        LogUtil.d("transferClue() called with: params = [" + params + "]");
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(IClue.class)
                        .getCallReturnInfo(params)
                        .compose(RetrofitAdapterFactory.<CallBackCallid>compatApplySchedulers());
    }

}
