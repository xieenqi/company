package com.loyo.oa.v2.activityui.customer.fragment;

import android.animation.ObjectAnimator;
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
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.CustomerDetailInfoActivity_;
import com.loyo.oa.v2.activityui.customer.CustomerManagerActivity;
import com.loyo.oa.v2.activityui.customer.NearByCustomersActivity_;
import com.loyo.oa.v2.activityui.customer.adapter.TeamCustomerAdapter;
import com.loyo.oa.v2.activityui.customer.bean.Department;
import com.loyo.oa.v2.activityui.customer.bean.NearCount;
import com.loyo.oa.v2.activityui.customer.bean.Role;
import com.loyo.oa.v2.activityui.customer.bean.Tag;
import com.loyo.oa.v2.activityui.other.bean.User;
import com.loyo.oa.v2.activityui.sale.bean.SaleTeamScreen;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.Permission;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.SaleCommPopupView;
import com.loyo.oa.v2.customview.ScreenDeptPopupView;
import com.loyo.oa.v2.customview.ScreenTagPopupView;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshListView;
import com.loyo.oa.v2.db.OrganizationManager;
import com.loyo.oa.v2.db.bean.DBDepartment;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.BaseMainListFragment;
import com.loyo.oa.v2.tool.LocationUtilGD;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.UMengTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【团队客户】列表
 * Created by yyy on 16/6/1.
 */
public class TeamCustomerFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener2 {

    private View mView;
    private ViewStub emptyView;
    private TextView screen;
    private ImageView tagImage1;
    private ImageView tagImage2;
    private ImageView tagImage3;
    private LinearLayout screen1;
    private LinearLayout screen2;
    private LinearLayout screen3;
    private TextView nearTv;
    private ViewGroup nearLayout;
    private NearCount nearCount;
    private Permission permission;
    private SaleTeamScreen saleTeamScreen;
    private PullToRefreshListView listView;
    private SaleCommPopupView saleCommPopupView;
    private ScreenTagPopupView screenTagPopupView;
    private ScreenDeptPopupView saleScreenPopupView;
    private WindowManager.LayoutParams windowParams;
    private TeamCustomerAdapter adapter;
    private ArrayList<Tag> mTags;
    private ArrayList<Tag> mDoubleTags = new ArrayList<>();
    private List<Department> newDeptSource = new ArrayList<>();//我的部门
    private List<SaleTeamScreen> data = new ArrayList<>();
    private ArrayList<SaleTeamScreen> sortData = new ArrayList<>();
    private PaginationX<Customer> mPagination = new PaginationX<>(20);
    private ArrayList<Customer> mCustomers = new ArrayList<>();
    private String[] sort = {"跟进时间 倒序", "跟进时间 顺序", "创建时间 倒序", "创建时间 顺序"};
    private String filed = "lastActAt";
    private String order = "desc";
    private String userId = "";
    private String tagItemIds = "";
    private String departmentId = "";
    private String position;
    private int page = 1;
    private int tagPostion;
    private boolean isPullUp = false;
    private boolean isKind;
    final private static int DEPARTMENT_USER_DATA_LOADED = 210;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case DEPARTMENT_USER_DATA_LOADED: {
                    saleScreenPopupView = new ScreenDeptPopupView(mActivity, data, mHandler);
                    break;
                }

                //部门筛选
                case CustomerManagerActivity.CUSTOMER_DEPT_CREEN:
                    saleTeamScreen = (SaleTeamScreen) msg.getData().getSerializable("data");
                    screen.setText(saleTeamScreen.getName());
                    isKind = msg.getData().getBoolean("kind");
                    if (isKind) {
                        userId = "";
                        departmentId = saleTeamScreen.getxPath();
                    } else {
                        departmentId = "";
                        userId = saleTeamScreen.getId();
                    }
                    getRefershData();
                    break;

                //时间筛选
                case CustomerManagerActivity.CUSTOMER_TIME:
                    tagPostion = msg.getData().getInt("data");
                    switch (tagPostion) {
                        case 0:
                            filed = "lastActAt";
                            order = "desc";
                            break;
                        case 1:
                            filed = "lastActAt";
                            order = "asc";
                            break;
                        case 2:
                            filed = "createdAt";
                            order = "desc";
                            break;
                        case 3:
                            filed = "createdAt";
                            order = "asc";
                            break;
                    }
                    getRefershData();
                    break;

                //标签筛选
                case CustomerManagerActivity.CUSTOMER_TAG:
                    tagItemIds = msg.getData().getString("tagid");
                    getRefershData();
                    break;

                //标签取消
                case CustomerManagerActivity.CUSTOMER_CANCEL:
                    tagItemIds = msg.getData().getString("tagid");
                    getRefershData();
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_team_cus, null);
            initView(mView);
        }
        return mView;
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        page = 1;
        isPullUp = false;
        mPagination.setPageIndex(1);
        getData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        page++;
        isPullUp = true;
        mPagination.setPageIndex(mPagination.getPageIndex() + 1);
        getData();
    }

    public void initView(View view) {
        mTags = (ArrayList<Tag>) getArguments().getSerializable("tag");
        permission = (Permission) getArguments().getSerializable("permission");
        mDoubleTags.addAll(mTags);

        for (int i = 0; i < sort.length; i++) {
            saleTeamScreen = new SaleTeamScreen();
            saleTeamScreen.setName(sort[i]);
            saleTeamScreen.setIndex(false);
            sortData.add(saleTeamScreen);
        }

        screen = (TextView) view.findViewById(R.id.custeam_screen1_commy);
        emptyView = (ViewStub) view.findViewById(R.id.vs_nodata);
        nearTv = (TextView) view.findViewById(R.id.tv_near_customers);
        screen1 = (LinearLayout) view.findViewById(R.id.custeam_screen1);
        screen2 = (LinearLayout) view.findViewById(R.id.custeam_screen2);
        screen3 = (LinearLayout) view.findViewById(R.id.custeam_screen3);
        nearLayout = (ViewGroup) view.findViewById(R.id.layout_near_customers);
        tagImage1 = (ImageView) view.findViewById(R.id.custeam_screen1_iv1);
        tagImage2 = (ImageView) view.findViewById(R.id.custeam_screen1_iv2);
        tagImage3 = (ImageView) view.findViewById(R.id.custeam_screen1_iv3);
        listView = (PullToRefreshListView) view.findViewById(R.id.lv_list);
        listView.setEmptyView(emptyView);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(this);
        nearLayout.setOnClickListener(click);
        nearLayout.setOnTouchListener(Global.GetTouch());

        screen1.setOnClickListener(click);
        screen2.setOnClickListener(click);
        screen3.setOnClickListener(click);

        showLoading("");
//        mDeptSource = Common.getLstDepartment();

        /**
         * bugfix : Updated by ethan 2016/09/11
         * 子线程读数据，主线程加载数据
         *
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                wersi();
            }
        }).start();
        getData();
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
                saleTeamScreen = new SaleTeamScreen();
                saleTeamScreen.setId(MainApp.user.getId());
                saleTeamScreen.setName(MainApp.user.name);
                saleTeamScreen.setxPath(MainApp.user.depts.get(0).getShortDept().getXpath());
                data.add(saleTeamScreen);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } finally { /** 子线程读数据，主线程加载数据 */
            Message msg = new Message();
            msg.what = DEPARTMENT_USER_DATA_LOADED;
            mHandler.sendMessage(msg);
            }
        }

        /**
         * 显示附近客户
         */

    private void showNearCustomersView() {
        nearLayout.setVisibility(View.VISIBLE);
        int oX = app.diptoPx(240);
        int nX = 0;
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(nearLayout, "translationX", oX, nX);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setDuration(500);
        objectAnimator.setTarget(nearLayout);
        objectAnimator.start();
    }


    /**
     * http获取附近客户信息
     */
    private void getNearCustomersInfo() {
        new LocationUtilGD(mActivity, new LocationUtilGD.AfterLocation() {
            @Override
            public void OnLocationGDSucessed(String address, double longitude, double latitude, String radius) {
                LocationUtilGD.sotpLocation();
                position = String.valueOf(longitude).concat(",").concat(String.valueOf(latitude));

                RestAdapterFactory.getInstance().build(FinalVariables.QUERY_NEAR_CUSTOMERS_COUNT_TEAM).create(ICustomer.class).queryNearCount(position, new RCallback<NearCount>() {
                    @Override
                    public void success(NearCount _nearCount, Response response) {
                        HttpErrorCheck.checkResponse("附近客户", response);
                        nearCount = _nearCount;
                        if (null != nearCount) {
                            nearTv.setText("发现" + nearCount.total + "个附近客户");
                            showNearCustomersView();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        super.failure(error);
                        HttpErrorCheck.checkError(error);
                    }
                });
                UMengTools.sendLocationInfo(address, longitude, latitude);
            }

            @Override
            public void OnLocationGDFailed() {
                LocationUtilGD.sotpLocation();
                Toast("定位失败！");
            }
        });
    }

    private void getRefershData() {
        page = 1;
        isPullUp = false;
        getData();
    }

    /**
     * 获取数据,默认设置倒序
     */
    private void getData() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("pageIndex", page);
        params.put("pageSize", 15);
        params.put("field", filed);
        params.put("order", order);
        params.put("tagItemIds", tagItemIds);
        params.put("deptId", departmentId);
        params.put("userId", userId);
        LogUtil.d("客户查询传递参数：" + MainApp.gson.toJson(params));
        RestAdapterFactory.getInstance().build(FinalVariables.QUERY_CUSTOMERS_TEAM).create(ICustomer.class).query(params, new RCallback<PaginationX<Customer>>() {
                    @Override
                    public void success(PaginationX<Customer> customerPaginationX, Response response) {
                        HttpErrorCheck.checkResponse("客户列表", response);
                        if (null == customerPaginationX || PaginationX.isEmpty(customerPaginationX)) {
                            if (!isPullUp) {
                                mPagination.setPageIndex(1);
                                mPagination.setPageSize(20);
                                mCustomers.clear();
                                bindData();
                            } else {
                                Toast("没有更多数据了");
                                listView.onRefreshComplete();
                                return;
                            }
                        } else {
                            mPagination = customerPaginationX;
                            if (!isPullUp) {
                                mCustomers.clear();
                            }
                            mCustomers.addAll(customerPaginationX.getRecords());
                            bindData();
                        }
                        getNearCustomersInfo();
                        listView.onRefreshComplete();
                        MainApp.getMainApp().isCutomerEdit = false;
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                        listView.onRefreshComplete();
                    }
                }
        );
    }

    /**
     * 绑定数据
     */
    private void bindData() {

        if (null == adapter) {
            adapter = new TeamCustomerAdapter(getActivity(), mCustomers);
            listView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

        /**
         * 列表监听 进入客户详情页面
         * */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent();
                intent.putExtra("Id", mCustomers.get(position - 1).getId());
                intent.putExtra(ExtraAndResult.EXTRA_TYPE, CustomerManagerActivity.CUSTOMER_TEAM);
                intent.setClass(mActivity, CustomerDetailInfoActivity_.class);
                startActivityForResult(intent, BaseMainListFragment.REQUEST_REVIEW);
                mActivity.overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
            }
        });
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

    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                //公司
                case R.id.custeam_screen1:

                    /* bugfix : Updated by ethan 2016/09/11 */
                    if (saleScreenPopupView != null) { /* 数据加载完成，才新建，可能为空 */
                        saleScreenPopupView.showAsDropDown(screen1);
                        openPopWindow(tagImage1);
                        saleScreenPopupView.setOnDismissListener(new PopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                closePopupWindow(tagImage1);
                            }
                        });
                    }

                    break;

                //时间
                case R.id.custeam_screen2:
                    saleCommPopupView = new SaleCommPopupView(getActivity(), mHandler, sortData, CustomerManagerActivity.CUSTOMER_TIME, true, tagPostion);
                    saleCommPopupView.showAsDropDown(screen2);
                    openPopWindow(tagImage2);
                    saleCommPopupView.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            closePopupWindow(tagImage2);
                        }
                    });
                    break;

                //标签
                case R.id.custeam_screen3:
                    screenTagPopupView = new ScreenTagPopupView(getActivity(), mDoubleTags, mHandler);
                    screenTagPopupView.showAsDropDown(screen3);
                    openPopWindow(tagImage3);
                    screenTagPopupView.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            closePopupWindow(tagImage3);
                        }
                    });
                    break;

                //附近的客户
                case R.id.layout_near_customers:
                    Bundle bundle = new Bundle();
                    bundle.putString("position", position);
                    bundle.putSerializable("nearCount", nearCount);
                    bundle.putInt("type", CustomerManagerActivity.NEARCUS_TEAM);//团队2 个人1
                    app.startActivity(mActivity, NearByCustomersActivity_.class, MainApp.ENTER_TYPE_ZOOM_IN, false, bundle);
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            //客户详情操作回调
            case CustomerManagerActivity.CUSTOMER_COMM_RUSH:
                getData();
                break;
        }
    }
}
