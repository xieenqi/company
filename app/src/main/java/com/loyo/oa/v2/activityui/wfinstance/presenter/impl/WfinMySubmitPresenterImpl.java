package com.loyo.oa.v2.activityui.wfinstance.presenter.impl;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.wfinstance.bean.BizForm;
import com.loyo.oa.v2.activityui.wfinstance.bean.MySubmitWflnstance;
import com.loyo.oa.v2.activityui.wfinstance.bean.WfinstanceUitls;
import com.loyo.oa.v2.activityui.wfinstance.bean.WflnstanceItemData;
import com.loyo.oa.v2.activityui.wfinstance.bean.WflnstanceListItem;
import com.loyo.oa.v2.activityui.wfinstance.presenter.WfinMySubmitPresenter;
import com.loyo.oa.v2.activityui.wfinstance.viewcontrol.WfinMySubmitView;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.filterview.DropDownMenu;
import com.loyo.oa.v2.customview.filterview.OnMenuSelectedListener;
import com.loyo.oa.v2.point.IWfInstance;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.loyo.oa.v2.common.Global.Toast;

/**
 * Created by yyy on 16/10/17.
 */

public class WfinMySubmitPresenterImpl implements WfinMySubmitPresenter{

    private Context mContext;
    private DropDownMenu mMenu;
    private WfinMySubmitView crolView;
    private ArrayList<WflnstanceItemData> datas = new ArrayList<>();
    private ArrayList<WflnstanceListItem> lstData = new ArrayList<>();
    private ArrayList<BizForm> mBizForms = new ArrayList<>();

    private int status;
    private String bizFormId = "";

    public  WfinMySubmitPresenterImpl(Context mContext,WfinMySubmitView crolView,DropDownMenu mMenu){
        this.mContext = mContext;
        this.crolView = crolView;
        this.mMenu    = mMenu;

    }

    /**
     * 获取审批类型数据
     * */
    @Override
    public void getWfBizForms(final List<String[]> items) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("pageIndex", 1);
        params.put("pageSize", 500);
        RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(IWfInstance.class).getWfBizForms(params, new RCallback<PaginationX<BizForm>>() {
            @Override
            public void success(PaginationX<BizForm> bizFormPaginationX, Response response) {
                HttpErrorCheck.checkResponse("审批自定义字段", response);
                if (null != bizFormPaginationX) {
                    mBizForms = bizFormPaginationX.getRecords();
                    if (null != mBizForms && !mBizForms.isEmpty()) {
                        String[] FILTER_TYPE = new String[mBizForms.size() + 1];
                        FILTER_TYPE[0] = "全部类别";
                        for (int i = 0; i < mBizForms.size(); i++) {
                            FILTER_TYPE[i + 1] = mBizForms.get(i).getName();
                        }
                        items.add(FILTER_TYPE);
                        mMenu.setmMenuItems(items);
                    }
                }
            }
        });
    }

    /**
     * 获取审批列表数据
     * */
    @Override
    public void getApproveWfInstancesList(int page,final boolean isTopAdd) {
        crolView.showProgress("");
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", page);
        map.put("pageSize", 20);
        map.put("status", status);
        map.put("bizformId", bizFormId); //自定义筛选字段

        RestAdapterFactory.getInstance().build(Config_project.API_URL() +
                FinalVariables.wfinstance).create(IWfInstance.class).
                getSubmitWfInstancesList(map, new Callback<MySubmitWflnstance>() {
                    @Override
                    public void success(MySubmitWflnstance mySubmitWflnstance, Response response) {
                        HttpErrorCheck.checkResponse("【我提的交】列表数据：", response);
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

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                        crolView.setListRefreshComplete();
                    }
                });
    }

    /**
     * 初始化顶部菜单
     * */
    @Override
    public void initDropMenu(String[] FILTER_STATUS) {
        String[] defaultTitle = new String[]{"全部状态", "全部类别"};
        mMenu.setVisibility(View.VISIBLE);
        mMenu.setmMenuCount(defaultTitle.length);//Menu的个数
        mMenu.setmShowCount(6);//Menu展开list数量最多只显示的个数
        mMenu.setShowCheck(true);//是否显示展开list的选中项
        mMenu.setmMenuTitleTextSize(14);//Menu的文字大小
        mMenu.setmMenuTitleTextColor(mContext.getResources().getColor(R.color.default_menu_press_text));//Menu的文字颜色
        mMenu.setmMenuListTextSize(14);//Menu展开list的文字大小
        mMenu.setmMenuListTextColor(Color.BLACK);//Menu展开list的文字颜色
        mMenu.setmMenuBackColor(Color.WHITE);//Menu的背景颜色
        mMenu.setmMenuPressedBackColor(mContext.getResources().getColor(R.color.white));//Menu按下的背景颜色
        mMenu.setmCheckIcon(R.drawable.img_check1);//Menu展开list的勾选图片
        mMenu.setmUpArrow(R.drawable.arrow_up);//Menu默认状态的箭头
        mMenu.setmDownArrow(R.drawable.arrow_down);//Menu按下状态的箭头
        mMenu.setDefaultMenuTitle(defaultTitle);//默认未选择任何过滤的Menu title

        final List<String[]> items = new ArrayList<>();
        items.add(FILTER_STATUS);

        HashMap<String, Object> params = new HashMap<>();
        params.put("pageIndex", 1);
        params.put("pageSize", 500);
        RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(IWfInstance.class).getWfBizForms(params, new RCallback<PaginationX<BizForm>>() {
            @Override
            public void success(PaginationX<BizForm> bizFormPaginationX, Response response) {
                HttpErrorCheck.checkResponse("审批自定义字段", response);
                if (null != bizFormPaginationX) {
                    mBizForms = bizFormPaginationX.getRecords();
                    if (null != mBizForms && !mBizForms.isEmpty()) {
                        String[] FILTER_TYPE = new String[mBizForms.size() + 1];
                        FILTER_TYPE[0] = "全部类别";
                        for (int i = 0; i < mBizForms.size(); i++) {
                            FILTER_TYPE[i + 1] = mBizForms.get(i).getName();
                        }
                        items.add(FILTER_TYPE);
                        mMenu.setmMenuItems(items);
                    }
                }
            }
        });
        /**
         * 顶部删选Menu
         * */
        mMenu.setMenuSelectedListener(new OnMenuSelectedListener() {
            @Override
            //Menu展开的list点击事件  RowIndex：list的索引  ColumnIndex：menu的索引
            public void onSelected(View listview, int RowIndex, int ColumnIndex) {
                LogUtil.d(" 行 : " + RowIndex + " 列 : " + ColumnIndex);
                switch (ColumnIndex) {
                    case 0:
                        status = RowIndex;
                        break;
                    case 1:
                        if (RowIndex == 0) {
                            bizFormId = "";
                        } else {
                            bizFormId = mBizForms.get(RowIndex - 1).getId();
                        }
                        break;
                }
                crolView.setPullDownToRefresh();
            }
        });
    }

    /**
     * ListView监听与初始化
     * */
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
                    crolView.openItemEmbl(groupPosition,childPosition);
                } catch (Exception e) {
                    Global.ProcException(e);
                }

                return false;
            }
        });
        Utils.btnHideForListView(mListView, btn_add);
    }

}
