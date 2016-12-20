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
import com.loyo.oa.v2.activityui.wfinstance.common.BizFormMenuModel;
import com.loyo.oa.v2.activityui.wfinstance.common.SubmitStatusMenuModel;
import com.loyo.oa.v2.activityui.wfinstance.presenter.WfinMySubmitPresenter;
import com.loyo.oa.v2.activityui.wfinstance.viewcontrol.WfinMySubmitView;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.DialogHelp;
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
 * Created by yyy on 16/10/17.
 */

public class WfinMySubmitPresenterImpl implements WfinMySubmitPresenter {

    private Context mContext;
    private com.loyo.oa.dropdownmenu.DropDownMenu filterMenu;
    private WfinMySubmitView crolView;
    private ArrayList<WflnstanceItemData> datas = new ArrayList<>();
    private ArrayList<WflnstanceListItem> lstData = new ArrayList<>();
    private ArrayList<BizForm> mBizForms = new ArrayList<>();

    private String status;
    private String bizFormId = "";

    public WfinMySubmitPresenterImpl(Context mContext, WfinMySubmitView crolView, DropDownMenu mMenu) {
        this.mContext = mContext;
        this.crolView = crolView;
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
//                HttpErrorCheck.checkResponse("审批自定义字段", response);
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
            public void onError(Throwable e) {
                super.onError(e);
                DialogHelp.cancelLoading();
            }

            @Override
            public void onNext(PaginationX<BizForm> bizFormPaginationX) {
                DialogHelp.cancelLoading();
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
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", page);
        map.put("pageSize", 20);
        map.put("status", status);
        map.put("bizformId", bizFormId); //自定义筛选字段

//        RestAdapterFactory.getInstance().build(Config_project.API_URL() +
//                FinalVariables.wfinstance).create(IWfInstance.class).
//                getSubmitWfInstancesList(map, new Callback<MySubmitWflnstance>() {
//                    @Override
//                    public void success(MySubmitWflnstance mySubmitWflnstance, Response response) {
//                        HttpErrorCheck.checkResponse("【我提的交】列表数据：", response);
//                        crolView.setListRefreshComplete();
//                        if (null == mySubmitWflnstance) {
//                            return;
//                        }
//                        ArrayList<WflnstanceListItem> lstDataTemp = mySubmitWflnstance.records;
//                        if (null != lstDataTemp && lstDataTemp.size() == 0 && !isTopAdd) {
//                            crolView.showMsg("没有更多数据了");
//                            return;
//                        }
//
//                        if (!isTopAdd) {
//                            lstData.addAll(lstDataTemp);
//                        } else {
//                            lstData = lstDataTemp;
//                        }
//                        datas = WfinstanceUitls.convertGroupSubmitData(lstData);
//                        crolView.bindListData(datas);
//                    }
//
//                    @Override
//                    public void failure(RetrofitError error) {
//                        HttpErrorCheck.checkError(error, crolView.getLoading(), page == 1 ? true : false);
//                        crolView.setListRefreshComplete();
//                    }
//                });

        WfinstanceService.getSubmitWfInstancesList(map).subscribe(new DefaultLoyoSubscriber<MySubmitWflnstance>() {
            @Override
            public void onError(Throwable e) {
               /* 重写父类方法，不调用super, 当有数据时，使用Toast，无数据时才使用整屏错误页面 */
                @LoyoErrorChecker.CheckType
                int type =page != 1  ?
                        LoyoErrorChecker.TOAST : LoyoErrorChecker.LOADING_LAYOUT;
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
                datas = WfinstanceUitls.convertGroupSubmitData(lstData);
                crolView.bindListData(datas);
            }
        });
    }

    /**
     * 初始化顶部菜单
     */
    public void _loadFilterOptions(List<BizForm> bizForms) {
        List<FilterModel> options = new ArrayList<>();
        options.add(SubmitStatusMenuModel.getFilterModel());
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

    @Override
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
