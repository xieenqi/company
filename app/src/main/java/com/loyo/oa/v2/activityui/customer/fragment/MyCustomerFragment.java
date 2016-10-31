package com.loyo.oa.v2.activityui.customer.fragment;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.CustomerAddActivity_;
import com.loyo.oa.v2.activityui.customer.CustomerDetailInfoActivity_;
import com.loyo.oa.v2.activityui.customer.CustomerManagerActivity;
import com.loyo.oa.v2.activityui.customer.MyContactMailList;
import com.loyo.oa.v2.activityui.customer.NearByCustomersActivity_;
import com.loyo.oa.v2.activityui.customer.adapter.MyCustomerAdapter;
import com.loyo.oa.v2.activityui.customer.model.NearCount;
import com.loyo.oa.v2.activityui.customer.model.Tag;
import com.loyo.oa.v2.activityui.customer.presenter.MyCustomerFragPresenter;
import com.loyo.oa.v2.activityui.customer.presenter.impl.MyCustomerFragPresenterImpl;
import com.loyo.oa.v2.activityui.customer.viewcontrol.MyCustomerFragView;
import com.loyo.oa.v2.activityui.sale.bean.SaleTeamScreen;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.SaleCommPopupView;
import com.loyo.oa.v2.customview.ScreenTagPopupView;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshListView;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.LocationUtilGD;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.UMengTools;
import com.loyo.oa.v2.tool.Utils;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【我的客户】列表
 * Created by yyy on 16/6/1.
 */
public class MyCustomerFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener2, MyCustomerFragView {

    private Intent mIntent;
    private View mView;
    private Button btn_add;
    private ViewStub emptyView;
    private ImageView tagImage1;
    private ImageView tagImage2;
    private LinearLayout screen1;
    private LinearLayout screen2;
    private TextView nearTv;
    private ViewGroup nearLayout;
    private NearCount nearCount;
    //    private Permission permission;
    private SaleTeamScreen saleTeamScreen;
    private PullToRefreshListView listView;
    private SaleCommPopupView saleCommPopupView;
    private ScreenTagPopupView screenTagPopupView;
    private WindowManager.LayoutParams windowParams;
    private MyCustomerAdapter adapter;
    private MyCustomerFragPresenter mPresenter;

    private String filed = "lastActAt";
    private String order = "desc";
    private String userId = "";
    private String tagItemIds = "";
    private String departmentId = "";
    private String position;
    private int page = 1;
    private int tagPostion;
    private int lastVisibleItemPosition;
    private boolean isPullUp = false;
    private boolean scrollFlag;

    private String[] sort = {"跟进时间 倒序", "跟进时间 顺序", "创建时间 倒序", "创建时间 顺序"};
    private ArrayList<SaleTeamScreen> sortData = new ArrayList<>();
    private PaginationX<Customer> mPagination = new PaginationX<>(20);
    private ArrayList<Customer> mCustomers = new ArrayList<>();
    private ArrayList<Tag> mTags;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
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
                    getData();
                    break;

                //标签筛选
                case CustomerManagerActivity.CUSTOMER_TAG:
                    tagItemIds = msg.getData().getString("tagid");
                    getData();
                    break;

                //标签取消
                case CustomerManagerActivity.CUSTOMER_CANCEL:
                    tagItemIds = msg.getData().getString("tagid");
                    getData();
                    break;
            }
        }
    };

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_cus, null);
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
//        permission = (Permission) getArguments().getSerializable("permission");
        mTags = (ArrayList<Tag>) getArguments().getSerializable("tag");

        for (int i = 0; i < sort.length; i++) {
            saleTeamScreen = new SaleTeamScreen();
            saleTeamScreen.setName(sort[i]);
            saleTeamScreen.setIndex(false);
            sortData.add(saleTeamScreen);
        }

        btn_add = (Button) view.findViewById(R.id.btn_add);
        nearTv = (TextView) view.findViewById(R.id.tv_near_customers);
        emptyView = (ViewStub) mView.findViewById(R.id.vs_nodata);
        screen1 = (LinearLayout) view.findViewById(R.id.cus_screen1);
        screen2 = (LinearLayout) view.findViewById(R.id.cus_screen2);
        nearLayout = (ViewGroup) view.findViewById(R.id.layout_near_customers);
        tagImage1 = (ImageView) view.findViewById(R.id.cus_screen1_iv1);
        tagImage2 = (ImageView) view.findViewById(R.id.cus_screen1_iv2);
        listView = (PullToRefreshListView) view.findViewById(R.id.lv_list);
        listView.setEmptyView(emptyView);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(this);
        nearLayout.setOnClickListener(click);
        nearLayout.setOnTouchListener(Global.GetTouch());

        screen1.setOnClickListener(click);
        screen2.setOnClickListener(click);
        btn_add.setOnClickListener(click);
        btn_add.setOnTouchListener(Global.GetTouch());
        showLoading("");
        getData();
        mPresenter = new MyCustomerFragPresenterImpl(getActivity(), this);
        Utils.btnHideForListView(listView.getRefreshableView(), btn_add);
    }

    /**
     * 绑定数据
     */
    private void bindData() {

        if (null == adapter) {
            adapter = new MyCustomerAdapter(app, mCustomers);
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
                intent.putExtra(ExtraAndResult.EXTRA_TYPE, CustomerManagerActivity.CUSTOMER_MY);
                intent.setClass(mActivity, CustomerDetailInfoActivity_.class);
                startActivityForResult(intent, getActivity().RESULT_FIRST_USER);
                getActivity().overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
            }
        });
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

                RestAdapterFactory.getInstance().build(FinalVariables.QUERY_NEAR_CUSTOMERS_COUNT_SELF).create(ICustomer.class).queryNearCount(position, new RCallback<NearCount>() {
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
        RestAdapterFactory.getInstance().build(FinalVariables.QUERY_CUSTOMERS_SELF).create(ICustomer.class).query(params, new RCallback<PaginationX<Customer>>() {
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


    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                //新建客户
                case R.id.btn_add:
                    mPresenter.setInsertPopWindiw(btn_add);
                    //showPopupWindow();
                    break;

                //时间
                case R.id.cus_screen1:
                    saleCommPopupView = new SaleCommPopupView(getActivity(), mHandler, sortData, CustomerManagerActivity.CUSTOMER_TIME, true, tagPostion);
                    saleCommPopupView.showAsDropDown(screen1);
                    openPopWindow(tagImage1);
                    saleCommPopupView.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            closePopupWindow(tagImage1);
                        }
                    });
                    break;

                //标签
                case R.id.cus_screen2:
                    screenTagPopupView = new ScreenTagPopupView(mActivity, mTags, mHandler);
                    screenTagPopupView.showAsDropDown(screen2);
                    openPopWindow(tagImage2);
                    screenTagPopupView.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            closePopupWindow(tagImage2);
                        }
                    });
                    break;

                //附近的客户
                case R.id.layout_near_customers:
                    Bundle bundle = new Bundle();
                    bundle.putString("position", position);
                    bundle.putSerializable("nearCount", nearCount);
                    bundle.putInt("type", CustomerManagerActivity.NEARCUS_SELF);//团队2 个人1
                    app.startActivity(mActivity, NearByCustomersActivity_.class, MainApp.ENTER_TYPE_RIGHT, false, bundle);
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

    /**
     * 通讯录导入客户
     */
    @Override
    public void intentAutoInsert(PopupWindow popupWindow) {
        popupWindow.dismiss();
        startActivityForResult(new Intent(getActivity(), MyContactMailList.class), getActivity().RESULT_FIRST_USER);
    }

    /**
     * 手动添加客户
     */
    @Override
    public void intentHandInsert(PopupWindow popupWindow) {
        popupWindow.dismiss();
        mIntent = new Intent();
        mIntent.setClass(getActivity(), CustomerAddActivity_.class);
        startActivityForResult(mIntent, getActivity().RESULT_FIRST_USER);
        getActivity().overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
    }
}
