package com.loyo.oa.v2.activityui.worksheet.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.bean.Department;
import com.loyo.oa.v2.activityui.customer.bean.Role;
import com.loyo.oa.v2.activityui.other.bean.User;
import com.loyo.oa.v2.activityui.sale.SaleOpportunitiesManagerActivity;
import com.loyo.oa.v2.activityui.sale.bean.SaleTeamScreen;
import com.loyo.oa.v2.activityui.sale.fragment.TeamSaleFragment;
import com.loyo.oa.v2.activityui.worksheet.WorksheetAddActivity;
import com.loyo.oa.v2.activityui.worksheet.WorksheetDetailActivity;
import com.loyo.oa.v2.activityui.worksheet.adapter.TeamWorksheetsAdapter;
import com.loyo.oa.v2.activityui.worksheet.adapter.WorksheetListAdapter;
import com.loyo.oa.v2.activityui.worksheet.bean.Worksheet;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetListWrapper;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetTemplate;
import com.loyo.oa.v2.common.GroupsData;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetConfig;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetStatus;
import com.loyo.oa.v2.activityui.worksheet.event.WorksheetChangeEvent;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.fragment.BaseGroupsDataFragment;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.SaleCommPopupView;
import com.loyo.oa.v2.customview.ScreenDeptPopupView;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshExpandableListView;
import com.loyo.oa.v2.point.IWorksheet;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.Utils;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * 【团队线索】
 * Created by yyy on 16/8/19.
 */
public class TeamWorksheetFragment extends BaseGroupsDataFragment implements View.OnClickListener {

    private String xpath;     /* 查询部门xpath */
    private String userId;    /* 查询用户id */
    private int statusIndex;  /* 工单状态Index */
    private int typeIndex;    /* 工单类型Index */

    private boolean isOk = true, isKind;

    private ArrayList<SaleTeamScreen> statusData = new ArrayList<>();
    private ArrayList<SaleTeamScreen> typeData = new ArrayList<>();
    private ArrayList<WorksheetStatus> statusFilters;
    private ArrayList<WorksheetTemplate> typeFilters;

    private List<Department> mDeptSource;  //部门和用户集合
    private List<Department> newDeptSource = new ArrayList<>();//我的部门
    private List<SaleTeamScreen> data = new ArrayList<>();

    private LinearLayout salemy_screen0, salemy_screen1, salemy_screen2;
    private ImageView salemy_screen1_iv0, salemy_screen1_iv1, salemy_screen1_iv2;
    private TextView tv_tab0, tv_tab1, tv_tab2;
    private Button btn_add;
    private ViewStub emptyView;

    private Intent mIntent;
    private View mView;

    private ScreenDeptPopupView deptPopupView;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ExtraAndResult.MSG_SEND: {
                    deptPopupView = new ScreenDeptPopupView(mActivity, data, mHandler);
                    break;
                }
                /*部门选择回调*/
                case TeamSaleFragment.SALETEAM_SCREEN_TAG1:
                    isPullDown = true;
                    SaleTeamScreen saleTeamScreen = (SaleTeamScreen) msg.getData().getSerializable("data");
                    tv_tab0.setText(saleTeamScreen.getName());
                    isKind = msg.getData().getBoolean("kind");
                    if (isKind) {
                        userId = "";
                        xpath = saleTeamScreen.getxPath();
                    } else {
                        xpath = "";
                        userId = saleTeamScreen.getId();
                    }
                    page = 1;
                    LogUtil.dee("isKind:" + isKind);
                    showLoading("加载中...");
                    getData();
                    break;

                 /*  状态 */
                case TeamSaleFragment.SALETEAM_SCREEN_TAG2: {

                    int newIndex = (int) msg.getData().get("index");
                    if (statusIndex != newIndex) {
                        statusIndex = newIndex;
                        isPullDown = true;
                        page = 1;
                        tv_tab1.setText(statusFilters.get(statusIndex).getName());
                        showLoading("加载中...");
                        getData();
                    }
                }
                break;

                /* 类型 */
                case TeamSaleFragment.SALETEAM_SCREEN_TAG3: {

                    int newIndex = (int) msg.getData().get("index");
                    if (typeIndex != newIndex) {
                        typeIndex = newIndex;
                        isPullDown = true;
                        page = 1;
                        tv_tab2.setText(typeFilters.get(typeIndex).name);
                        showLoading("加载中...");
                        getData();
                    }
                }

                break;
            }

        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.dee("onCreate:");

    }


    @Subscribe
    public void onWorksheetCreated(WorksheetChangeEvent event) {
        isPullDown = true;
        page = 1;
        getData();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_team_worksheet, null);
            groupsData = new GroupsData();
            initFilters();
            initView(mView);
        }
        return mView;
    }

    private void initView(View view) {

        btn_add = (Button) view.findViewById(R.id.btn_add);
        btn_add.setOnTouchListener(Global.GetTouch());
        btn_add.setOnClickListener(this);
        btn_add.setVisibility(View.GONE);
        salemy_screen0 = (LinearLayout) view.findViewById(R.id.salemy_screen0);
        salemy_screen1 = (LinearLayout) view.findViewById(R.id.salemy_screen1);
        salemy_screen2 = (LinearLayout) view.findViewById(R.id.salemy_screen2);
        salemy_screen0.setOnClickListener(this);
        salemy_screen1.setOnClickListener(this);
        salemy_screen2.setOnClickListener(this);
        salemy_screen1_iv0 = (ImageView) view.findViewById(R.id.salemy_screen1_iv0);
        salemy_screen1_iv1 = (ImageView) view.findViewById(R.id.salemy_screen1_iv1);
        salemy_screen1_iv2 = (ImageView) view.findViewById(R.id.salemy_screen1_iv2);
        tv_tab0 = (TextView) view.findViewById(R.id.tv_tab0);
        tv_tab1 = (TextView) view.findViewById(R.id.tv_tab1);
        tv_tab2 = (TextView) view.findViewById(R.id.tv_tab2);

        tv_tab1.setText(statusFilters.get(statusIndex).getName());
        tv_tab2.setText(typeFilters.get(typeIndex).name);

        emptyView = (ViewStub) view.findViewById(R.id.vs_nodata);

        mExpandableListView = (PullToRefreshExpandableListView) mView.findViewById(R.id.expandableListView);
        mExpandableListView.setOnRefreshListener(this);
        mExpandableListView.setEmptyView(emptyView);

        setupExpandableListView(
                new ExpandableListView.OnGroupClickListener() {
                    @Override
                    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                        return true;
                    }
                },
                new ExpandableListView.OnChildClickListener() {
                    @Override
                    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                        Worksheet ws = (Worksheet) adapter.getChild(groupPosition, childPosition);
                        String wsId = ws.id != null ? ws.id : "";

                        mIntent = new Intent();
                        mIntent.putExtra(ExtraAndResult.EXTRA_ID, wsId);
                        mIntent.setClass(getActivity(), WorksheetDetailActivity.class);
                        startActivityForResult(mIntent, getActivity().RESULT_FIRST_USER);
                        getActivity().overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);

                        return true;
                    }
                });
        initAdapter();
        expand();

        Utils.btnHideForListView(expandableListView, btn_add);

        showLoading("加载中...");
        getData();
    }

    private void initFilters() {
        statusFilters = new ArrayList<WorksheetStatus>();
        statusFilters.add(WorksheetStatus.Null);
        statusFilters.add(WorksheetStatus.WAITASSIGN);
        statusFilters.add(WorksheetStatus.INPROGRESS);
        statusFilters.add(WorksheetStatus.WAITAPPROVE);
        statusFilters.add(WorksheetStatus.FINISHED);
        statusFilters.add(WorksheetStatus.TEMINATED);

        typeFilters = new ArrayList<WorksheetTemplate>();
        typeFilters.add(WorksheetTemplate.Null);

        ArrayList<WorksheetTemplate> types = WorksheetConfig.getWorksheetTypes(true);
        if (types != null) {
            typeFilters.addAll(types);
        }
        setFilterData();

        statusIndex = 0;
        typeIndex = 0;
    }

    private void setFilterData() {
        mDeptSource = Common.getLstDepartment();
        new Thread(new Runnable() {
            @Override
            public void run() {
                wersi();
            }
        }).start();
        statusData.clear();
        for (int i = 0; i < statusFilters.size(); i++) {
            SaleTeamScreen saleTeamScreen = new SaleTeamScreen();
            saleTeamScreen.setName(statusFilters.get(i).getName());
            statusData.add(saleTeamScreen);
        }

        typeData.clear();
        for (int i = 0; i < typeFilters.size(); i++) {
            SaleTeamScreen saleTeamScreen = new SaleTeamScreen();
            saleTeamScreen.setName(typeFilters.get(i).name);
            typeData.add(saleTeamScreen);
        }
    }

    @Override
    public void initAdapter() {
        if (null == adapter) {
            adapter = new TeamWorksheetsAdapter(mActivity, groupsData);
            mExpandableListView.getRefreshableView().setAdapter(adapter);
        }
    }


    @Override
    protected void getData() {

//        * templateId  工单类型id
//        * status      1:待分派 2:处理中 3:待审核 4:已完成 5:意外中止
//        * keyword     关键字查询
//        * type tab    1:我创建的 2:我分派的
//        * pageIndex
//        * pageSize
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", page);
        map.put("pageSize", 15);
        if (statusIndex > 0 && statusIndex < statusFilters.size()) {
            map.put("status", statusFilters.get(statusIndex).code);
        }

        if (typeIndex > 0 && typeIndex < typeFilters.size()) {
            map.put("templateId", typeFilters.get(typeIndex).id);
        }

        if (xpath != null && xpath.length() > 0) {
            map.put("xpath", xpath);
        }
        if (userId != null && userId.length() > 0) {
            map.put("userId", userId);
        }

        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).
                create(IWorksheet.class).getTeamWorksheetList(map, new Callback<WorksheetListWrapper>() {
            @Override
            public void success(WorksheetListWrapper listWrapper, Response response) {
                mExpandableListView.onRefreshComplete();

                if (isPullDown) {
                    groupsData.clear();
                }
                loadData(listWrapper.data.records);
                HttpErrorCheck.checkResponse("团队工单列表：", response);
            }

            @Override
            public void failure(RetrofitError error) {
                mExpandableListView.onRefreshComplete();
                HttpErrorCheck.checkError(error);
            }
        });

    }

    private void loadData(List<Worksheet> list) {
        Iterator<Worksheet> iterator = list.iterator();
        while (iterator.hasNext()) {
            groupsData.addItem(iterator.next());
        }
        groupsData.sort();
        adapter.notifyDataSetChanged();
        LogUtil.dee("size:" + groupsData.size());
        expand();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //新建
            case R.id.btn_add:

                mIntent = new Intent();
                mIntent.setClass(getActivity(), WorksheetAddActivity.class);
                startActivityForResult(mIntent, ExtraAndResult.REQUEST_CODE);
                getActivity().overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);

                break;

            // 选人
            case R.id.salemy_screen0: {
                if (deptPopupView != null) {
                    deptPopupView.showAsDropDown(salemy_screen0);
                    openPopWindow(salemy_screen1_iv0);
                    deptPopupView.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            closePopupWindow(salemy_screen1_iv0);
                        }
                    });
                }
            }
            break;

            //时间选择
            case R.id.salemy_screen1: {
                SaleCommPopupView saleCommPopupView = new SaleCommPopupView(getActivity(), mHandler, statusData,
                        SaleOpportunitiesManagerActivity.SCREEN_STAGE, true, statusIndex);
                saleCommPopupView.showAsDropDown(salemy_screen1);
                openPopWindow(salemy_screen1_iv1);
                saleCommPopupView.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        closePopupWindow(salemy_screen1_iv1);
                    }
                });

            }
            break;

            //状态
            case R.id.salemy_screen2: {
                SaleCommPopupView saleCommPopupView = new SaleCommPopupView(getActivity(), mHandler, typeData,
                        SaleOpportunitiesManagerActivity.SCREEN_SORT, false, typeIndex);
                saleCommPopupView.showAsDropDown(salemy_screen2);
                openPopWindow(salemy_screen1_iv2);
                saleCommPopupView.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        closePopupWindow(salemy_screen1_iv2);
                    }
                });
            }
            break;
        }
    }

    public void wersi() {
        try {
            //为超管或权限为全公司 展示全公司成员
            if (MainApp.user.isSuperUser() || MainApp.user.role.getDataRange() == Role.ALL) {
                setUser(mDeptSource);
            }
            //权限为部门 展示我的部门
            else if (MainApp.user.role.getDataRange() == Role.DEPT_AND_CHILD) {
                deptSort();
            }
            //权限为个人 展示自己
            else if (MainApp.user.role.getDataRange() == Role.SELF) {
                data.clear();
                SaleTeamScreen saleTeamScreen = new SaleTeamScreen();
                saleTeamScreen.setId(MainApp.user.getId());
                saleTeamScreen.setName(MainApp.user.name);
                saleTeamScreen.setxPath(MainApp.user.depts.get(0).getShortDept().getXpath());
                data.add(saleTeamScreen);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } finally { /** 子线程读数据，主线程加载数据 */
            Message msg = new Message();
            msg.what = ExtraAndResult.MSG_SEND;
            mHandler.sendMessage(msg);
        }
    }

    /**
     * 过滤出我的部门
     */
    private void deptSort() {
        newDeptSource.clear();
        User user = MainApp.user;
        for (Department department : mDeptSource) {
            for (int i = 0; i < user.getDepts().size(); i++) {
                if (department.getId().contains(user.getDepts().get(i).getShortDept().getId())) {
                    newDeptSource.add(department);
                }
            }
        }
        setUser(newDeptSource);
    }

    /**
     * 组装部门格式
     */
    private void setUser(List<Department> values) {
        data.clear();
        for (Department department : values) {
            SaleTeamScreen saleTeamScreen = new SaleTeamScreen();
            saleTeamScreen.setId(department.getId());
            saleTeamScreen.setName(department.getName());
            saleTeamScreen.setxPath(department.getXpath());
            data.add(saleTeamScreen);
        }
    }

    /**
     * PopupWindow关闭 恢复背景正常颜色
     */
    private void closePopupWindow(ImageView view) {
        view.setBackgroundResource(R.drawable.arrow_down);
    }

    /**
     * PopupWindow打开，背景变暗
     */
    private void openPopWindow(ImageView view) {
        view.setBackgroundResource(R.drawable.arrow_up);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode) {
            //新建 删除 编辑 转移客户,回调函数
            case ExtraAndResult.REQUEST_CODE:
                isPullDown = true;
                page = 1;
                break;
        }
    }
}