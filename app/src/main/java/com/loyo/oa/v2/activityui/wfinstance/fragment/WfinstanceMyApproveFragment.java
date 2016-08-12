package com.loyo.oa.v2.activityui.wfinstance.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ExpandableListView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.wfinstance.WfInstanceManageActivity;
import com.loyo.oa.v2.activityui.wfinstance.WfinstanceInfoActivity_;
import com.loyo.oa.v2.activityui.wfinstance.adapter.WflnstanceMySubmitAdapter;
import com.loyo.oa.v2.activityui.wfinstance.bean.BizForm;
import com.loyo.oa.v2.activityui.wfinstance.bean.MySubmitWflnstance;
import com.loyo.oa.v2.activityui.wfinstance.bean.WfinstanceUitls;
import com.loyo.oa.v2.activityui.wfinstance.bean.WflnstanceItemData;
import com.loyo.oa.v2.activityui.wfinstance.bean.WflnstanceListItem;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.filterview.DropDownMenu;
import com.loyo.oa.v2.customview.filterview.OnMenuSelectedListener;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshExpandableListView;
import com.loyo.oa.v2.point.IWfInstance;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by xeq on 16/7/21.
 * 审批【我审批的】
 */
public class WfinstanceMyApproveFragment extends BaseFragment implements View.OnClickListener, PullToRefreshBase.OnRefreshListener2 {

    private PullToRefreshExpandableListView expandableListView;
    private Button btn_add;
    private DropDownMenu mMenu;
    private ViewStub emptyView;
    private static final String FILTER_STATUS[] = new String[]{"全部状态", "待我审批的", "未到我审批的", "我同意的", "我驳回的"};
    private ArrayList<BizForm> mBizForms = new ArrayList<>();
    private String bizFormId = "";
    private WflnstanceMySubmitAdapter mAdapter;
    private int page = 1, status = 0;
    ArrayList<WflnstanceItemData> datas = new ArrayList<>();
    protected ArrayList<WflnstanceListItem> lstData = new ArrayList<>();
    private boolean isTopAdd = false;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wflinstance, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        expandableListView = (PullToRefreshExpandableListView) view.findViewById(R.id.expandableListView);
        btn_add = (Button) view.findViewById(R.id.btn_add);
        mMenu = (DropDownMenu) view.findViewById(R.id.drop_menu);
        emptyView = (ViewStub) view.findViewById(R.id.vs_nodata);
        btn_add.setOnTouchListener(Global.GetTouch());
        btn_add.setOnClickListener(this);
        btn_add.setVisibility(View.GONE);
        expandableListView.setOnRefreshListener(this);
        expandableListView.setEmptyView(emptyView);
        page = 1;
        isTopAdd = true;
        initDropMenu();
        initList();
        initAdapter();
        getData();
    }

    /**
     * 初始化下拉菜单
     */
    private void initDropMenu() {
        String[] defaultTitle = new String[]{"全部状态", "全部类别"};
        mMenu.setVisibility(View.VISIBLE);
        mMenu.setmMenuCount(defaultTitle.length);//Menu的个数
        mMenu.setmShowCount(6);//Menu展开list数量最多只显示的个数
        mMenu.setShowCheck(true);//是否显示展开list的选中项
        mMenu.setmMenuTitleTextSize(14);//Menu的文字大小
        mMenu.setmMenuTitleTextColor(getResources().getColor(R.color.text33));//Menu的文字颜色
        mMenu.setmMenuListTextSize(14);//Menu展开list的文字大小
        mMenu.setmMenuListTextColor(Color.BLACK);//Menu展开list的文字颜色
        mMenu.setmMenuBackColor(Color.WHITE);//Menu的背景颜色
        mMenu.setmMenuPressedBackColor(getResources().getColor(R.color.white));//Menu按下的背景颜色
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
                        switch (status) {//加未到我审批的 导致筛选状态对不上 特转一次
                            case 2:
                                status = 4;
                                break;
                            case 3:
                                status = 2;
                                break;
                            case 4:
                                status = 4;
                                break;
                        }
                        break;
                    case 2:
                        if (RowIndex == 0) {
                            bizFormId = "";
                        } else {
                            bizFormId = mBizForms.get(RowIndex - 1).getId();
                        }
                        break;
                }
                onPullDownToRefresh(expandableListView);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add:
                ((WfInstanceManageActivity) getActivity()).addNewItem();
                break;
        }
    }

    private void getData() {
        showLoading("");
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", page);
        map.put("pageSize", 20);
        map.put("status", status);
        map.put("bizformId", bizFormId); //自定义筛选字段

        RestAdapterFactory.getInstance().build(Config_project.API_URL() +
                FinalVariables.wfinstance).create(IWfInstance.class).
                getApproveWfInstancesList(map, new Callback<MySubmitWflnstance>() {
                    @Override
                    public void success(MySubmitWflnstance mySubmitWflnstance, Response response) {
                        HttpErrorCheck.checkResponse("【我审批的】列表数据：", response);
                        expandableListView.onRefreshComplete();
                        if (null == mySubmitWflnstance) {
                            return;
                        }
                        ArrayList<WflnstanceListItem> lstDataTemp = mySubmitWflnstance.records;
                        if (null != lstDataTemp && lstDataTemp.size() == 0) {
                            Toast("没有更多数据了");
                            return;
                        }
                        //下接获取最新时，清空
                        if (isTopAdd) {
                            lstData.clear();
                        }
                        lstData.addAll(lstDataTemp);
                        datas = WfinstanceUitls.convertGroupApproveData(lstData);
                        changeAdapter();
                        expand();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                        expandableListView.onRefreshComplete();
                    }
                });
    }

    /**
     * 初始化
     */
    private void initList() {

        ExpandableListView ListView = expandableListView.getRefreshableView();
        initAdapter();
        expand();
        ListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return false;
            }
        });

        ListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                try {
                    openItem(groupPosition, childPosition);
                } catch (Exception e) {
                    Global.ProcException(e);
                }

                return false;
            }
        });
    }

    public void initAdapter() {
        mAdapter = new WflnstanceMySubmitAdapter(mActivity);
        expandableListView.getRefreshableView().setAdapter(mAdapter);
    }

    public void changeAdapter() {
        mAdapter.setData(datas);
    }

    /**
     * 展开listview
     */
    protected void expand() {
        for (int i = 0; i < datas.size(); i++) {
            expandableListView.getRefreshableView().expandGroup(i, false);//true 自动滑到底部
        }
    }

    public void openItem(int groupPosition, int childPosition) {
        Intent intent = new Intent();
        intent.putExtra(ExtraAndResult.EXTRA_ID, ((WflnstanceListItem) mAdapter.getChild(groupPosition, childPosition)).id);
        intent.setClass(mActivity, WfinstanceInfoActivity_.class);
        startActivityForResult(intent, ExtraAndResult.REQUEST_CODE);
        getActivity().overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);

    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        isTopAdd = true;
        page = 1;
        getData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        isTopAdd = false;
        page++;
        getData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            switch (requestCode) {
                case ExtraAndResult.REQUEST_CODE:
                    isTopAdd = true;
                    page = 1;
                    getData();
                    break;
            }
        }
    }
}
