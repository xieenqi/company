package com.loyo.oa.v2.activityui.order.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.bean.Department;
import com.loyo.oa.v2.activityui.customer.bean.Role;
import com.loyo.oa.v2.activityui.other.bean.User;
import com.loyo.oa.v2.activityui.sale.SaleOpportunitiesManagerActivity;
import com.loyo.oa.v2.activityui.sale.bean.SaleTeamScreen;
import com.loyo.oa.v2.activityui.sale.fragment.TeamSaleFragment;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.customview.SaleCommPopupView;
import com.loyo.oa.v2.customview.ScreenDeptPopupView;
import com.loyo.oa.v2.tool.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 【团队订单】
 * Created by xeq on 16/8/1.
 */
public class TeamOrderFragment extends BaseFragment implements View.OnClickListener {

    private String[] status = {"全部状态", "待审核", "未通过", "进行中", "已完成", "意外终止"};
    private String[] sort = {"按照创建时间", "按照最高金额"};
    private LinearLayout screen1, screen2, screen3;
    private ImageView screen1_iv1, screen2_iv2, screen3_iv3;
    private WindowManager.LayoutParams windowParams;
    private int statusIndex, sortIndex;
    private ArrayList<SaleTeamScreen> sortData = new ArrayList<>();
    private ArrayList<SaleTeamScreen> statusData = new ArrayList<>();
    private ScreenDeptPopupView deptPopupView;
    private List<Department> mDeptSource;  //部门和用户集合
    private List<Department> newDeptSource = new ArrayList<>();//我的部门
    private List<SaleTeamScreen> data = new ArrayList<>();
    private boolean isOk = true;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case TeamSaleFragment.SALETEAM_SCREEN_TAG2:
//                    isPull = false;
//                    stageId = msg.getData().get("data").toString();
                    statusIndex = (int) msg.getData().get("index");
                    break;

                case TeamSaleFragment.SALETEAM_SCREEN_TAG3:
//                    isPull = false;
//                    sortType = msg.getData().get("data").toString();
                    sortIndex = (int) msg.getData().get("index");
                    break;

                default:
                    break;

            }
//            getData();
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = null;
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_team_order, null);
            initView(mView);
        }
//        getData();
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
//                        getData();
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
            SaleTeamScreen  saleTeamScreen = new SaleTeamScreen();
            saleTeamScreen.setId(MainApp.user.getId());
            saleTeamScreen.setName(MainApp.user.name);
            saleTeamScreen.setxPath(MainApp.user.depts.get(0).getShortDept().getXpath());
            data.add(saleTeamScreen);
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

}
