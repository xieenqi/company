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
import com.loyo.oa.v2.activityui.clue.adapter.MyClueAdapter;
import com.loyo.oa.v2.activityui.clue.adapter.TeamClueAdapter;
import com.loyo.oa.v2.activityui.clue.bean.ClueList;
import com.loyo.oa.v2.activityui.clue.bean.ClueListItem;
import com.loyo.oa.v2.activityui.customer.bean.Department;
import com.loyo.oa.v2.activityui.customer.bean.Role;
import com.loyo.oa.v2.activityui.order.bean.OrderListItem;
import com.loyo.oa.v2.activityui.other.bean.User;
import com.loyo.oa.v2.activityui.sale.SaleOpportunitiesManagerActivity;
import com.loyo.oa.v2.activityui.sale.bean.SaleTeamScreen;
import com.loyo.oa.v2.activityui.sale.fragment.TeamSaleFragment;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.SaleCommPopupView;
import com.loyo.oa.v2.customview.ScreenDeptPopupView;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshListView;
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
    private boolean isOk = true;
    private boolean isPullDown = true, isKind;
    private String xPath = "", userId = "";
    private String[] status = {"全部状态", "未处理", "已处理", "关闭"};
    private String[] sort = {"跟进时间 倒序", "跟进时间 顺序", "创建时间 倒序", "创建时间 顺序"};
    private ArrayList<ClueListItem> listData = new ArrayList<>();
    private List<Department> mDeptSource;  //部门和用户集合
    private List<Department> newDeptSource = new ArrayList<>();//我的部门
    private List<SaleTeamScreen> data = new ArrayList<>();

    private LinearLayout screen1, screen2, screen3;
    private ImageView screen1_iv1, screen2_iv2, screen3_iv3;
    private TextView saleteam_screen1_commy;
    private WindowManager.LayoutParams windowParams;
    private ArrayList<SaleTeamScreen> sortData = new ArrayList<>();
    private ArrayList<SaleTeamScreen> statusData = new ArrayList<>();
    private ScreenDeptPopupView deptPopupView;

    private ViewStub emptyView;
    private PullToRefreshListView lv_list;
    private TeamClueAdapter adapter;
    private View mView;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TeamSaleFragment.SALETEAM_SCREEN_TAG2:
                    isPullDown = true;
                    statusIndex = (int) msg.getData().get("index");
                    page = 1;
                    LogUtil.dee("statusIndex:"+statusIndex);
                    break;
                case TeamSaleFragment.SALETEAM_SCREEN_TAG3:
                    isPullDown = true;
                    sortIndex = (int) msg.getData().get("index");
                    page = 1;
                    LogUtil.dee("sortIndex:"+sortIndex);
                    break;
                case TeamSaleFragment.SALETEAM_SCREEN_TAG1:
                    isPullDown = true;
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
                    LogUtil.dee("isKind:"+isKind);
                    break;
            }
            getData();
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(null == mView){
            mView = inflater.inflate(R.layout.fragment_team_order, null);
            initView(mView);
        }
        return mView;
    }

    private void initView(View view) {
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
                intent.putExtra(ExtraAndResult.EXTRA_ID, /* 线索id */listData.get(position-1).id);
                intent.setClass(getActivity(), ClueDetailActivity.class);
                startActivityForResult(intent, getActivity().RESULT_FIRST_USER);
                getActivity().overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);

            }
        });
        setAdapter();
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
        mDeptSource = Common.getLstDepartment();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isOk) {
                    if (data.size() == 0) {
                        wersi();
                    } else {
                        isOk = false;
                        deptPopupView = new ScreenDeptPopupView(getActivity(), data, mHandler);
                    }
                }
            }
        }).start();
    }

    public void wersi() {
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
    }


    private void setAdapter(){

        if(null == adapter){
            adapter = new TeamClueAdapter(getActivity(),listData);
            lv_list.setAdapter(adapter);
        }else{
            adapter.notifyDataSetChanged();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.screen1://人员筛选
                deptPopupView.showAsDropDown(screen1);
                openPopWindow(screen1_iv1);
                deptPopupView.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        closePopupWindow(screen1_iv1);
                    }
                });
                break;
            case R.id.screen2://状态筛选
                SaleCommPopupView saleCommPopupView = new SaleCommPopupView(getActivity(), mHandler, statusData,
                        SaleOpportunitiesManagerActivity.SCREEN_STAGE, true, statusIndex);
                saleCommPopupView.showAsDropDown(screen2);
                openPopWindow(screen2_iv2);
                saleCommPopupView.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        closePopupWindow(screen2_iv2);
                    }
                });
                break;
            case R.id.screen3://排序
                saleCommPopupView = new SaleCommPopupView(getActivity(), mHandler, sortData,
                        SaleOpportunitiesManagerActivity.SCREEN_SORT, false, sortIndex);
                saleCommPopupView.showAsDropDown(screen3);
                openPopWindow(screen3_iv3);
                saleCommPopupView.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        closePopupWindow(screen3_iv3);
                    }
                });
                break;
        }
    }

    /**
     * PopupWindow关闭 恢复背景正常颜色
     */
    private void closePopupWindow(ImageView view) {
        windowParams = getActivity().getWindow().getAttributes();
        windowParams.alpha = 1f;
        getActivity().getWindow().setAttributes(windowParams);
        view.setBackgroundResource(R.drawable.arrow_down);
    }

    /**
     * PopupWindow打开，背景变暗
     */
    private void openPopWindow(ImageView view) {
        windowParams = getActivity().getWindow().getAttributes();
        windowParams.alpha = 0.9f;
        getActivity().getWindow().setAttributes(windowParams);
        view.setBackgroundResource(R.drawable.arrow_up);
    }

    /**
     * 获取团队数据列表
     */
    private void getData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", page);
        map.put("pageSize", 15);
        map.put("target_id", "");
        map.put("xpath", MainApp.user.depts.get(0).getShortDept().getXpath());
        LogUtil.dee("发送数据:" + MainApp.gson.toJson(map));
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).
                create(IClue.class).getMyCluelist(map, new Callback<ClueList>() {
            @Override
            public void success(ClueList clueList, Response response) {
                lv_list.onRefreshComplete();
                HttpErrorCheck.checkResponse("团队线索列表：", response);
                listData.clear();
                listData.addAll(clueList.data.records);
                setAdapter();
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
}
