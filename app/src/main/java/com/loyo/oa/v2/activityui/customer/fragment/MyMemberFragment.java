package com.loyo.oa.v2.activityui.customer.fragment;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.common.utils.UmengAnalytics;
import com.loyo.oa.dropdownmenu.DropDownMenu;
import com.loyo.oa.dropdownmenu.adapter.DefaultMenuAdapter;
import com.loyo.oa.dropdownmenu.callback.OnMenuModelsSelected;
import com.loyo.oa.dropdownmenu.filtermenu.TagMenuModel;
import com.loyo.oa.dropdownmenu.filtermenu.TimeFilterModel;
import com.loyo.oa.dropdownmenu.model.FilterModel;
import com.loyo.oa.dropdownmenu.model.MenuModel;
import com.loyo.oa.pulltorefresh.PullToRefreshBase;
import com.loyo.oa.pulltorefresh.PullToRefreshListView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.CustomerAddActivity_;
import com.loyo.oa.v2.activityui.customer.CustomerDetailInfoActivity_;
import com.loyo.oa.v2.activityui.customer.CustomerManagerActivity;
import com.loyo.oa.v2.activityui.customer.MyContactMailList;
import com.loyo.oa.v2.activityui.customer.NearByCustomersActivity_;
import com.loyo.oa.v2.activityui.customer.adapter.MyCustomerAdapter;
import com.loyo.oa.v2.activityui.customer.event.EditCustomerRushEvent;
import com.loyo.oa.v2.activityui.customer.event.MyCustomerListRushEvent;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.activityui.customer.model.CustomerTageConfig;
import com.loyo.oa.v2.activityui.customer.model.NearCount;
import com.loyo.oa.v2.activityui.customer.presenter.MyCustomerFragPresenter;
import com.loyo.oa.v2.activityui.customer.presenter.impl.MyCustomerFragPresenterImpl;
import com.loyo.oa.v2.activityui.customer.viewcontrol.MyCustomerFragView;
import com.loyo.oa.v2.activityui.other.model.Tag;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customermanagement.api.CustomerService;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.LoyoErrorChecker;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.LocationUtilGD;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.UMengTools;
import com.loyo.oa.v2.tool.Utils;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 【我参与的】列表
 * Created by yyy on 16/6/1.
 */
public class MyMemberFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener2, MyCustomerFragView {

    private Intent mIntent;
    private View mView;
    private Button btn_add;
    private TextView nearTv;
    private ViewGroup nearLayout;
    private NearCount nearCount;
    private PullToRefreshListView listView;
    private MyCustomerAdapter adapter;
    private MyCustomerFragPresenter mPresenter;
    private DropDownMenu filterMenu;

    private String field = "lastActAt";
    private String order = "desc";
    private String tagsParams = "";
    private String position;
    private int page = 1;
    private boolean isPullUp = false;
    private PaginationX<Customer> mPagination = new PaginationX<>(20);
    private ArrayList<Customer> mCustomers = new ArrayList<>();
    private ArrayList<Tag> mTags;
    private MemberCallback memberCallback;
    private LoadingLayout ll_loading;

    public interface MemberCallback {
        void comeBackHeadPage();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            memberCallback = (MemberCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().getClass().getName()
                    + " must implements interface MyListener");
        }
    }

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_cus, null);
            initView(mView);
            loadFilterOptions();
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
        ll_loading = (LoadingLayout) view.findViewById(R.id.ll_loading);
        ll_loading.setStatus(LoadingLayout.Loading);
        ll_loading.setOnReloadListener(new LoadingLayout.OnReloadListener() {
            @Override
            public void onReload(View v) {
                ll_loading.setStatus(LoadingLayout.Loading);
                getData();
            }
        });
        mTags = CustomerTageConfig.getTage(true);
        btn_add = (Button) view.findViewById(R.id.btn_add);
        nearTv = (TextView) view.findViewById(R.id.tv_near_customers);
        nearLayout = (ViewGroup) view.findViewById(R.id.layout_near_customers);

        listView = (PullToRefreshListView) view.findViewById(R.id.lv_list);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(this);
        nearLayout.setOnClickListener(click);
        nearLayout.setOnTouchListener(Global.GetTouch());

        btn_add.setOnClickListener(click);
        btn_add.setOnTouchListener(Global.GetTouch());

        filterMenu = (DropDownMenu) view.findViewById(R.id.drop_down_menu);
        getData();
        mPresenter = new MyCustomerFragPresenterImpl(getActivity(), this);
        btn_add.setVisibility(View.GONE);
        Utils.btnHideForListView(listView.getRefreshableView(), btn_add);
    }

    private void loadFilterOptions() {
        List<FilterModel> options = new ArrayList<>();
        options.add(TimeFilterModel.getFilterModel2());
        options.add(TagMenuModel.getTagFilterModel(mTags));
        DefaultMenuAdapter adapter = new DefaultMenuAdapter(getContext(), options);
        filterMenu.setMenuAdapter(adapter);
        adapter.setCallback(new OnMenuModelsSelected() {
            @Override
            public void onMenuModelsSelected(int menuIndex, List<MenuModel> selectedModels, Object userInfo) {
                filterMenu.close();

                if (menuIndex == 0) { // TimeFilterModel
                    MenuModel model = selectedModels.get(0);
                    String key = model.getKey();
                    String value = model.getValue();
                    filterMenu.headerTabBar.setTitleAtPosition(value, menuIndex);
                    String[] keys = key.split(" ");
                    field = keys[0];
                    order = keys[1];
                    UmengAnalytics.umengSend(mActivity, UmengAnalytics.timeCustomerJoin);
                } else if (menuIndex == 1) { // TagFilter
                    tagsParams = userInfo.toString();
                    UmengAnalytics.umengSend(mActivity, UmengAnalytics.tagCustomerJoin);
                }
                ll_loading.setStatus(LoadingLayout.Loading);
                isPullUp = false;
                page = 1;
                getData();
            }
        });
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
        if (!isPullUp && mCustomers.size() == 0) {
            ll_loading.setStatus(LoadingLayout.Empty);
        } else {
            ll_loading.setStatus(LoadingLayout.Success);
        }
        /**
         * 列表监听 进入客户详情页面
         * */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent();
                intent.putExtra("Id", mCustomers.get(position - 1).getId());
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
                CustomerService.getNearbySelfCustomerCount(position)
                        .subscribe(new DefaultLoyoSubscriber<NearCount>() {
                            public void onNext(NearCount count) {
                                nearCount = count;
                                if (null != nearCount) {
                                    nearTv.setText("发现" + nearCount.total + "个附近客户");
                                    showNearCustomersView();
                                }
                            }
                        });

                UMengTools.sendLocationInfo(address, longitude, latitude);
            }

            @Override
            public void OnLocationGDFailed() {
                LocationUtilGD.sotpLocation();
//                Toast("定位失败！");
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
        params.put("field", field);
        params.put("order", order);
        params.put("tagsParams", tagsParams);
        LogUtil.d("我参与的查询参数：" + MainApp.gson.toJson(params));
        CustomerService.getMemberCustomers(params)
                .subscribe(new DefaultLoyoSubscriber<PaginationX<Customer>>() {
                    public void onError(Throwable e) {
                        /* 重写父类方法，不调用super */
                        @LoyoErrorChecker.CheckType
                        int type = mCustomers.size() > 0 ?
                                LoyoErrorChecker.TOAST : LoyoErrorChecker.LOADING_LAYOUT;
                        LoyoErrorChecker.checkLoyoError(e, type, ll_loading);
                        listView.onRefreshComplete();
                    }

                    public void onNext(PaginationX<Customer> customerPaginationX) {
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
                });
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

                //附近的客户
                case R.id.layout_near_customers:
                    Bundle bundle = new Bundle();
                    bundle.putString("position", position);
                    bundle.putSerializable("nearCount", nearCount);
                    bundle.putInt("type", CustomerManagerActivity.NEARCUS_SELF);//团队2 个人1
                    app.startActivity(mActivity, NearByCustomersActivity_.class, MainApp.ENTER_TYPE_RIGHT, false, bundle);
                    UmengAnalytics.umengSend(mActivity, UmengAnalytics.customerNearby);
                    break;
            }
        }
    };

    /**
     * 新建回调 重启Manager
     */
    @Subscribe
    public void onMyCustomerListRushEvent(MyCustomerListRushEvent event) {
        memberCallback.comeBackHeadPage();
    }

    /**
     * 编辑回调 刷新列表
     */
    @Subscribe
    public void onEditCustomerRushEvent(EditCustomerRushEvent event) {
        getData();
    }

    /**
     * 通讯录导入客户
     */
    @Override
    public void intentAutoInsert(PopupWindow popupWindow) {
        popupWindow.dismiss();
        Intent mIntent = new Intent();
        mIntent.setClass(getActivity(), MyContactMailList.class);
        mIntent.putExtra(ExtraAndResult.EXTRA_NAME, 2);
        mIntent.putExtra(ExtraAndResult.EXTRA_OBJ, false);
        startActivityForResult(mIntent, getActivity().RESULT_FIRST_USER);
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
