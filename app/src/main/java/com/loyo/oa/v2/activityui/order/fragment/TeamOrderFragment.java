package com.loyo.oa.v2.activityui.order.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.bean.Department;
import com.loyo.oa.v2.activityui.customer.bean.Role;
import com.loyo.oa.v2.activityui.order.OrderDetailActivity;
import com.loyo.oa.v2.activityui.order.adapter.TeamOrderAdapter;
import com.loyo.oa.v2.activityui.order.bean.OrderList;
import com.loyo.oa.v2.activityui.order.bean.OrderListItem;
import com.loyo.oa.v2.activityui.other.model.User;
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
import com.loyo.oa.v2.db.OrganizationManager;
import com.loyo.oa.v2.db.bean.DBDepartment;
import com.loyo.oa.v2.point.IOrder;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【团队订单】
 * Created by xeq on 16/8/1.
 */
public class TeamOrderFragment extends BaseFragment implements View.OnClickListener, PullToRefreshBase.OnRefreshListener2 {

    private String[] status = {"全部状态", "待审核", "未通过", "进行中", "已完成", "意外终止"};
    private String[] sort = {"按照创建时间", "按照最高金额"};
    private LinearLayout screen1, screen2, screen3;
    private ImageView screen1_iv1, screen2_iv2, screen3_iv3;
    private TextView saleteam_screen1_commy;
    private int statusIndex, sortIndex;
    private ArrayList<SaleTeamScreen> sortData = new ArrayList<>();
    private ArrayList<SaleTeamScreen> statusData = new ArrayList<>();
    private ScreenDeptPopupView deptPopupView;
    private List<Department> newDeptSource = new ArrayList<>();//我的部门
    private List<SaleTeamScreen> data = new ArrayList<>();
    private ViewStub emptyView;
    private PullToRefreshListView lv_list;
    private TeamOrderAdapter adapter;
    private int page = 1;
    private boolean isPullDown = true, isKind;
    private List<OrderListItem> listData = new ArrayList<>();
    private String xPath = "", userId = "";

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ExtraAndResult.MSG_SEND: {
                    deptPopupView = new ScreenDeptPopupView(mActivity, data, mHandler);
                    break;
                }
                case TeamSaleFragment.SALETEAM_SCREEN_TAG2:
                    isPullDown = true;
                    statusIndex = (int) msg.getData().get("index");
                    page = 1;
                    getData();
                    break;
                case TeamSaleFragment.SALETEAM_SCREEN_TAG3:
                    isPullDown = true;
                    sortIndex = (int) msg.getData().get("index");
                    page = 1;
                    getData();
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
                    getData();
                    break;
            }

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
        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent mIntent = new Intent();
//                mIntent.putExtra(ExtraAndResult.IS_TEAM, false);
                mIntent.putExtra(ExtraAndResult.EXTRA_ID, adapter.getItemData(position - 1).id);
                mIntent.setClass(getActivity(), OrderDetailActivity.class);
                startActivityForResult(mIntent, getActivity().RESULT_FIRST_USER);
                getActivity().overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
            }
        });
        adapter = new TeamOrderAdapter(app);
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                wersi();
            }
        }).start();
    }

    public void wersi() {
        try {
            //为超管或权限为全公司 展示全公司成员
            if (MainApp.user.isSuperUser() || MainApp.user.role.getDataRange() == Role.ALL) {
                setUser(OrganizationManager.shareManager().allDepartments());
            }
            //权限为部门 展示我的部门
            else if (MainApp.user.role.getDataRange() == Role.DEPT_AND_CHILD) {
                setUser(OrganizationManager.shareManager().currentUserDepartments());
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
            SaleTeamScreen saleTeamScreen = new SaleTeamScreen();
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
        view.setBackgroundResource(R.drawable.arrow_down);
    }

    /**
     * PopupWindow打开，背景变暗
     */
    private void openPopWindow(ImageView view) {
        view.setBackgroundResource(R.drawable.arrow_up);
    }

    private void getData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", page);
        map.put("pageSize", 15);
        map.put("status", statusIndex);
        map.put("filed", sortIndex == 1 ? "dealMoney" : "createdAt");
        map.put("xpath", xPath);
        map.put("userId", userId);
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).
                create(IOrder.class).getOrderTeamList(map, new Callback<OrderList>() {
            @Override
            public void success(OrderList orderlist, Response response) {
                HttpErrorCheck.checkResponse("团队订单列表：", response);
                lv_list.onRefreshComplete();
                if (!isPullDown) {
                    listData.addAll(orderlist.records);
                } else {
                    listData = orderlist.records;
                }
                adapter.setData(listData);
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
