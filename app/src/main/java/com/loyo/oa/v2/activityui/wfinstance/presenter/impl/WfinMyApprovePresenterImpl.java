package com.loyo.oa.v2.activityui.wfinstance.presenter.impl;


import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;

import com.loyo.oa.dropdownmenu.DropDownMenu;
import com.loyo.oa.dropdownmenu.adapter.DefaultMenuAdapter;
import com.loyo.oa.dropdownmenu.callback.OnMenuModelsSelected;
import com.loyo.oa.dropdownmenu.model.FilterModel;
import com.loyo.oa.dropdownmenu.model.MenuModel;
import com.loyo.oa.v2.activityui.wfinstance.api.WfinstanceService;
import com.loyo.oa.v2.activityui.wfinstance.bean.BizForm;
import com.loyo.oa.v2.activityui.wfinstance.bean.MySubmitWflnstance;
import com.loyo.oa.v2.activityui.wfinstance.bean.WfinstanceUitls;
import com.loyo.oa.v2.activityui.wfinstance.bean.WflnstanceItemData;
import com.loyo.oa.v2.activityui.wfinstance.bean.WflnstanceListItem;
import com.loyo.oa.v2.activityui.wfinstance.common.ApproveStatusMenuModel;
import com.loyo.oa.v2.activityui.wfinstance.common.BizFormMenuModel;
import com.loyo.oa.v2.activityui.wfinstance.presenter.WfinMyApprovePresenter;
import com.loyo.oa.v2.activityui.wfinstance.viewcontrol.WfinMyApproveView;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.LoyoErrorChecker;
import com.loyo.oa.v2.point.IWfInstance;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【我的审批】列表 Presenter
 * Created by yyy on 16/10/17.
 */

public class WfinMyApprovePresenterImpl implements WfinMyApprovePresenter {

    private Context mContext;
    private DropDownMenu filterMenu;
    private WfinMyApproveView crolView;

    private ArrayList<WflnstanceItemData> datas = new ArrayList<>();
    private ArrayList<WflnstanceListItem> lstData = new ArrayList<>();
    private ArrayList<BizForm> mBizForms = new ArrayList<>();

    private String status;
    private String bizFormId = "";


    public WfinMyApprovePresenterImpl(DropDownMenu mMenu, WfinMyApproveView crolView, Context mContext) {
        this.crolView = crolView;
        this.mContext = mContext;
        this.filterMenu = mMenu;
    }


    /**
     * 获取审批类型数据
     */
    @Override
    public void getWfBizForms() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("pageIndex", 1);
        params.put("pageSize", 500);
//        RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(IWfInstance.class).getWfBizForms(params, new RCallback<PaginationX<BizForm>>() {
//            @Override
//            public void success(PaginationX<BizForm> bizFormPaginationX, Response response) {
//                if (null != bizFormPaginationX) {
//                    mBizForms = bizFormPaginationX.getRecords();
//                    if (null != mBizForms && !mBizForms.isEmpty()) {
//                        _loadFilterOptions(mBizForms);
//                    } else {
//                        _loadFilterOptions(null);
//                    }
//                } else {
//                    _loadFilterOptions(null);
//                }
//            }
//        });

        WfinstanceService.getWfBizForms(params).subscribe(new DefaultLoyoSubscriber<PaginationX<BizForm>>() {
            @Override
            public void onNext(PaginationX<BizForm> bizFormPaginationX) {
                if (null != bizFormPaginationX) {
                    mBizForms = bizFormPaginationX.getRecords();
                    if (null != mBizForms && !mBizForms.isEmpty()) {
                        _loadFilterOptions(mBizForms);
                    } else {
                        _loadFilterOptions(null);
                    }
                } else {
                    _loadFilterOptions(null);
                }
            }
        });
    }

    /**
     * 获取审批列表数据
     */
    @Override
    public void getApproveWfInstancesList(final int page, final boolean isTopAdd) {
//        crolView.showProgress("");
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", page);
        map.put("pageSize", 20);
        map.put("status", status);
        map.put("bizformId", bizFormId); //自定义筛选字段

//        RestAdapterFactory.getInstance().build(Config_project.API_URL() +
//                FinalVariables.wfinstance).create(IWfInstance.class).
//                getApproveWfInstancesList(map, new Callback<MySubmitWflnstance>() {
//                    @Override
//                    public void success(MySubmitWflnstance mySubmitWflnstance, Response response) {
//                        HttpErrorCheck.checkResponse("【我审批的】列表数据：", response);
//                        crolView.setListRefreshComplete();
//                        if (null == mySubmitWflnstance) {
//                            return;
//                        }
//                        ArrayList<WflnstanceListItem> lstDataTemp = mySubmitWflnstance.records;
//                        if (null != lstDataTemp && lstDataTemp.size() == 0 && !isTopAdd) {
//                            crolView.showMsg("没有更多数据了");
//                            return;
//                        }
//                        if (!isTopAdd) {
//                            lstData.addAll(lstDataTemp);
//                        } else {
//                            lstData = lstDataTemp;
//                        }
//                        datas = WfinstanceUitls.convertGroupApproveData(lstData);
//                        crolView.bindListData(datas);
//                    }
//
//                    @Override
//                    public void failure(RetrofitError error) {
//                        HttpErrorCheck.checkError(error, crolView.getLoading(), page == 1 ? true : false);
//                        crolView.setListRefreshComplete();
//                    }
//                });

        WfinstanceService.getApproveWfInstancesList(map).subscribe(new DefaultLoyoSubscriber<MySubmitWflnstance>() {
            @Override
            public void onError(Throwable e) {

                 /* 重写父类方法，不调用super, 当有数据时，使用Toast，无数据时才使用整屏错误页面 */
                @LoyoErrorChecker.CheckType
                int type =page != 1  ?LoyoErrorChecker.TOAST : LoyoErrorChecker.LOADING_LAYOUT;
                LoyoErrorChecker.checkLoyoError(e, type, crolView.getLoading());
                crolView.setListRefreshComplete();
            }

            @Override
            public void onNext(MySubmitWflnstance mySubmitWflnstance) {
                crolView.setListRefreshComplete();
                if (null == mySubmitWflnstance) {
                    return;
                }
                ArrayList<WflnstanceListItem> lstDataTemp = mySubmitWflnstance.records;
                if (null != lstDataTemp && lstDataTemp.size() == 0 && !isTopAdd) {
                    crolView.showMsg("没有更多数据了");
                    return;
                }
                if (!isTopAdd) {
                    lstData.addAll(lstDataTemp);
                } else {
                    lstData = lstDataTemp;
                }
                datas = WfinstanceUitls.convertGroupApproveData(lstData);
                crolView.bindListData(datas);
            }
        });
    };

    /**
     * 初始化顶部菜单
     */

    public void _loadFilterOptions(List<BizForm> bizForms) {
        List<FilterModel> options = new ArrayList<>();
        options.add(ApproveStatusMenuModel.getFilterModel());
        if (bizForms != null) {
            options.add(BizFormMenuModel.getFilterModel(bizForms));
        }
        DefaultMenuAdapter adapter = new DefaultMenuAdapter(mContext, options);
        filterMenu.setMenuAdapter(adapter);
        adapter.setCallback(new OnMenuModelsSelected() {
            @Override
            public void onMenuModelsSelected(int menuIndex, List<MenuModel> selectedModels, Object userInfo) {
                filterMenu.close();
                MenuModel model = selectedModels.get(0);
                String key = model.getKey();
                String value = model.getValue();
                filterMenu.headerTabBar.setTitleAtPosition(value, menuIndex);

                if (menuIndex == 0) {
                    status = key;
                } else if (menuIndex == 1) {
                    bizFormId = key;
                }
                crolView.setPullDownToRefresh();
            }
        });
    }

    public void loadFilterOptions() {

        getWfBizForms();
    }

    /**
     * ListView监听与初始化
     */
    @Override
    public void initListView(ExpandableListView mListView, Button btn_add) {
        mListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return false;
            }
        });

        mListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                try {
                    crolView.openItemEmbl(groupPosition, childPosition);
                } catch (Exception e) {
                    Global.ProcException(e);
                }

                return false;
            }
        });
        Utils.btnHideForListView(mListView, btn_add);
    }
}
