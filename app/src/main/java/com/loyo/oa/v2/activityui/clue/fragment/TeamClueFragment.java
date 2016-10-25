package com.loyo.oa.v2.activityui.clue.fragment;

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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.clue.ClueDetailActivity;
import com.loyo.oa.v2.activityui.clue.adapter.TeamClueAdapter;
import com.loyo.oa.v2.activityui.clue.bean.ClueList;
import com.loyo.oa.v2.activityui.clue.bean.ClueListItem;
import com.loyo.oa.v2.activityui.customer.bean.Department;
import com.loyo.oa.v2.activityui.sale.SaleOpportunitiesManagerActivity;
import com.loyo.oa.v2.activityui.sale.bean.SaleTeamScreen;
import com.loyo.oa.v2.activityui.sale.fragment.TeamSaleFragment;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Permission;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.SaleCommPopupView;
import com.loyo.oa.v2.customview.ScreenDeptPopupView;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshListView;
import com.loyo.oa.v2.db.OrganizationManager;
import com.loyo.oa.v2.db.bean.DBDepartment;
import com.loyo.oa.v2.point.IClue;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * 【团队线索】
 * Created by yyy on 16/8/19.
 */
public class TeamClueFragment extends BaseFragment implements View.OnClickListener, PullToRefreshBase.OnRefreshListener2 {

    private int statusIndex, sortIndex;
    private int page = 1;
    private boolean isPullDown = true, isKind;
    private String xPath = "";
    private String userId = "";
    private String field = "";
    private String order = "";
    private String[] status = {"全部状态", "未处理", "已联系", "关闭"};
    private String[] sort = {"跟进时间 倒序", "跟进时间 顺序", "创建时间 倒序", "创建时间 顺序"};
    private ArrayList<ClueListItem> listData = new ArrayList<>();
    private List<Department> mDeptSource;  //部门和用户集合
    private List<Department> newDeptSource = new ArrayList<>();//我的部门
    private List<SaleTeamScreen> data = new ArrayList<>();
    private SaleTeamScreen saleTeamScreen;
    private LinearLayout screen1, screen2, screen3;
    private ImageView screen1_iv1, screen2_iv2, screen3_iv3;
    private TextView saleteam_screen1_commy;
    private WindowManager.LayoutParams windowParams;
    private ArrayList<SaleTeamScreen> sortData = new ArrayList<>();
    private ArrayList<SaleTeamScreen> statusData = new ArrayList<>();
    private ScreenDeptPopupView deptPopupView;
    private Permission permission;
    private ViewStub emptyView;
    private PullToRefreshListView lv_list;
    private TeamClueAdapter adapter;
    private View mView;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                /*状态选择回调*/
                case TeamSaleFragment.SALETEAM_SCREEN_TAG2:
                    isPullDown = true;
                    statusIndex = (int) msg.getData().get("index");
                    page = 1;
                    LogUtil.dee("statusIndex:" + statusIndex);
                    getData();
                    break;

                /*排序选择回调*/
                case TeamSaleFragment.SALETEAM_SCREEN_TAG3:
                    isPullDown = true;
                    sortIndex = (int) msg.getData().get("index");
                    page = 1;

                    switch (sortIndex) {

                        /*跟进时间 倒序*/
                        case 0:
                            field = "lastActAt";
                            order = "desc";
                            break;

                        /*跟进时间 顺序*/
                        case 1:
                            field = "lastActAt";
                            order = "asc";
                            break;

                        /*创建时间 倒序*/
                        case 2:
                            field = "createAt";
                            order = "desc";
                            break;

                        /*创建时间 顺序*/
                        case 3:
                            field = "createAt";
                            order = "asc";
                            break;

                    }
                    getData();
                    break;

                /*部门选择回调*/
                case TeamSaleFragment.SALETEAM_SCREEN_TAG1:
                    SaleTeamScreen saleTeamScreen = (SaleTeamScreen) msg.getData().getSerializable("data");
                    saleteam_screen1_commy.setText(saleTeamScreen.getName());
                    isKind = msg.getData().getBoolean("kind");
                    if (isKind) {
                        userId = "";
                        xPath = saleTeamScreen.getxPath();
                    } else {
                        xPath = "";
                        userId = saleTeamScreen.getId();
                    }
                    page = 1;
                    isPullDown = true;
                    getData();
                    break;
            }
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_team_clue, null);
            initView(mView);
        }
        return mView;
    }

    private void initView(View view) {
        permission = (Permission) getArguments().getSerializable("permission");
        setFilterData();
        screen1 = (LinearLayout) view.findViewById(R.id.screen1);
        screen2 = (LinearLayout) view.findViewById(R.id.screen2);
        screen3 = (LinearLayout) view.findViewById(R.id.screen3);
        screen1.setOnClickListener(this);
        screen2.setOnClickListener(this);
        screen3.setOnClickListener(this);
        screen1_iv1 = (ImageView) view.findViewById(R.id.screen1_iv1);
        screen2_iv2 = (ImageView) view.findViewById(R.id.screen2_iv2);
        screen3_iv3 = (ImageView) view.findViewById(R.id.screen3_iv3);
        emptyView = (ViewStub) view.findViewById(R.id.vs_nodata);
        saleteam_screen1_commy = (TextView) view.findViewById(R.id.saleteam_screen1_commy);
        lv_list = (PullToRefreshListView) view.findViewById(R.id.lv_list);
        lv_list.setMode(PullToRefreshBase.Mode.BOTH);
        lv_list.setOnRefreshListener(this);
        lv_list.setEmptyView(emptyView);

        /*列表监听*/
        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra(ExtraAndResult.IS_TEAM, false);
                intent.putExtra(ExtraAndResult.EXTRA_ID, /* 线索id */listData.get(position - 1).id);
                intent.setClass(getActivity(), ClueDetailActivity.class);
                startActivityForResult(intent, getActivity().RESULT_FIRST_USER);
                getActivity().overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);

            }
        });
        adapter = new TeamClueAdapter(getActivity());
        lv_list.setAdapter(adapter);
        getData();
    }

    private void setFilterData() {
        for (int i = 0; i < sort.length; i++) {
            SaleTeamScreen saleTeamScreen = new SaleTeamScreen();
            saleTeamScreen.setName(sort[i]);
            sortData.add(saleTeamScreen);
        }
        for (int i = 0; i < status.length; i++) {
            SaleTeamScreen saleTeamScreen = new SaleTeamScreen();
            saleTeamScreen.setName(status[i]);
            statusData.add(saleTeamScreen);
        }
        wersi();
    }

    public void wersi() {
        //为超管或权限为全公司 展示全公司成员
        if (permission != null && permission.dataRange == Permission.COMPANY) {
            saleteam_screen1_commy.setText("全公司");
            setUser(OrganizationManager.shareManager().allDepartments());
        }
        //权限为部门 展示我的部门
        else if (permission != null && permission.dataRange == Permission.TEAM) {
            saleteam_screen1_commy.setText("本部门");
            setUser(OrganizationManager.shareManager().currentUserDepartments());
        }
        //权限为个人 展示自己
        else if (permission != null && permission.dataRange == Permission.PERSONAL) {
            saleteam_screen1_commy.setText("我");
            data.clear();
            SaleTeamScreen saleTeamScreen = new SaleTeamScreen();
            saleTeamScreen.setId(MainApp.user.getId());
            saleTeamScreen.setName(MainApp.user.name);
            saleTeamScreen.setxPath(MainApp.user.depts.get(0).getShortDept().getXpath());
            data.add(saleTeamScreen);
        }
        deptPopupView = new ScreenDeptPopupView(mActivity, data, mHandler, permission);
    }

//    /**
//     * 过滤出我的部门
//     */
//    private void deptSort() {
//        newDeptSource.clear();
//        User user = MainApp.user;
//        for (Department department : mDeptSource) {
//            for (int i = 0; i < user.getDepts().size(); i++) {
//                if (department.getId().contains(user.getDepts().get(i).getShortDept().getId())) {
//                    newDeptSource.add(department);
//                }
//            }
//        }
//        setUser(newDeptSource);
//    }

    /**
     * 组装部门格式
     */
    private void setUser(List<DBDepartment> values) {
        data.clear();
        for (DBDepartment department : values) {
            saleTeamScreen = new SaleTeamScreen();
            saleTeamScreen.setId(department.id);
            saleTeamScreen.setName(department.name);
            saleTeamScreen.setxPath(department.xpath);
            data.add(saleTeamScreen);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.screen1://人员筛选
                if (deptPopupView != null) {
                    deptPopupView.showAsDropDown(screen1);
                    openPopWindow(screen1_iv1);
                    deptPopupView.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            closePopupWindow(screen1_iv1);
                        }
                    });
                }
                break;
            case R.id.screen2://状态筛选
            {
                SaleCommPopupView saleCommPopupView = new SaleCommPopupView(getActivity(), mHandler, sortData,
                        SaleOpportunitiesManagerActivity.SCREEN_SORT, false, sortIndex);
                saleCommPopupView.showAsDropDown(screen3);
                openPopWindow(screen2_iv2);
                saleCommPopupView.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        closePopupWindow(screen2_iv2);
                    }
                });
            }

            break;
            case R.id.screen3://排序
            {
                SaleCommPopupView saleCommPopupView = new SaleCommPopupView(getActivity(), mHandler, statusData,
                        SaleOpportunitiesManagerActivity.SCREEN_STAGE, true, statusIndex);
                saleCommPopupView.showAsDropDown(screen2);
                openPopWindow(screen3_iv3);
                saleCommPopupView.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        closePopupWindow(screen3_iv3);
                    }
                });
            }
            break;
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

    /**
     * 获取团队数据列表
     */
    private void getData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", page);
        map.put("pageSize", 15);
        map.put("status", statusIndex);
        map.put("field", field);
        map.put("order", order);
        map.put("xpath", xPath);
        map.put("userId", userId);
        LogUtil.dee("发送数据:" + MainApp.gson.toJson(map));
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).
                create(IClue.class).getTeamCluelist(map, new Callback<ClueList>() {
            @Override
            public void success(ClueList clueList, Response response) {
                lv_list.onRefreshComplete();
                HttpErrorCheck.checkResponse("我的线索列表：", response);
                if (null == clueList.data || clueList.data.records == null) {
                    if (isPullDown && listData.size() > 0) {
                        listData.clear();
                    } else {
                        Toast("没有相关数据");
                        return;
                    }
                } else {
                    if (isPullDown) {
                        listData.clear();
                    }
                    listData.addAll(clueList.data.records);
                }
                try {
//                    if (isPullDown) {
//                        listData.clear();
//                    }
//                    if (null == clueList.data.records) {
//                        listData.clear();
//                        Toast("没有相关数据");
//                    } else {
//                        listData.addAll(clueList.data.records);
//                    }
                    adapter.setData(listData);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                lv_list.onRefreshComplete();
                HttpErrorCheck.checkError(error);
            }
        });
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        isPullDown = true;
        page = 1;
        getData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        isPullDown = false;
        page++;
        getData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode) {
            //新建 删除 编辑 转移客户,回调函数
            case ExtraAndResult.REQUEST_CODE:
                isPullDown = true;
                page = 1;
                getData();
                break;
        }
    }
}
