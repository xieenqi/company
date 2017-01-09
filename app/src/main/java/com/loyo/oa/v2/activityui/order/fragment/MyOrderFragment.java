package com.loyo.oa.v2.activityui.order.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.common.utils.UmengAnalytics;
import com.loyo.oa.dropdownmenu.DropDownMenu;
import com.loyo.oa.dropdownmenu.adapter.DefaultMenuAdapter;
import com.loyo.oa.dropdownmenu.callback.OnMenuModelsSelected;
import com.loyo.oa.dropdownmenu.filtermenu.CommonSortType;
import com.loyo.oa.dropdownmenu.filtermenu.CommonSortTypeMenuModel;
import com.loyo.oa.dropdownmenu.filtermenu.OrderStatusMenuModel;
import com.loyo.oa.dropdownmenu.model.FilterModel;
import com.loyo.oa.dropdownmenu.model.MenuListType;
import com.loyo.oa.dropdownmenu.model.MenuModel;
import com.loyo.oa.pulltorefresh.PullToRefreshBase;
import com.loyo.oa.pulltorefresh.PullToRefreshListView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.order.OrderAddActivity;
import com.loyo.oa.v2.activityui.order.OrderDetailActivity;
import com.loyo.oa.v2.activityui.order.adapter.MyOrderAdapter;
import com.loyo.oa.v2.activityui.order.bean.OrderListItem;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.LoyoErrorChecker;
import com.loyo.oa.v2.order.api.OrderService;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 【我的订单】
 * Created by xeq on 16/8/1.
 */
public class MyOrderFragment extends BaseFragment implements View.OnClickListener, PullToRefreshBase.OnRefreshListener2 {

    private Button btn_add;
    private LoadingLayout ll_loading;
    private PullToRefreshListView lv_list;
    private MyOrderAdapter adapter;
    private DropDownMenu filterMenu;
    private String statusType = "0";
    private String field = "";

    private int page = 1;
    private boolean isPullDown = true;
    private Bundle mBundle;

    private List<OrderListItem> listData = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = null;
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_my_order, null);
            initView(mView);
            loadFilterOptions();
        }
        return mView;
    }

    private void initView(View view) {
        btn_add = (Button) view.findViewById(R.id.btn_add);
        btn_add.setOnTouchListener(Global.GetTouch());
        btn_add.setOnClickListener(this);
        lv_list = (PullToRefreshListView) view.findViewById(R.id.lv_list);
        lv_list.setMode(PullToRefreshBase.Mode.BOTH);
        lv_list.setOnRefreshListener(this);
        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent mIntent = new Intent();
                mIntent.putExtra(ExtraAndResult.EXTRA_ID, adapter.getItemData(position - 1).id);
                mIntent.setClass(getActivity(), OrderDetailActivity.class);
                startActivityForResult(mIntent, getActivity().RESULT_FIRST_USER);
                getActivity().overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
            }
        });
        ll_loading = (LoadingLayout) view.findViewById(R.id.ll_loading);
        ll_loading.setOnReloadListener(new LoadingLayout.OnReloadListener() {
            @Override
            public void onReload(View v) {
                getPageData();
            }
        });
        adapter = new MyOrderAdapter(app);
        lv_list.setAdapter(adapter);
        filterMenu = (DropDownMenu) view.findViewById(R.id.drop_down_menu);
        getPageData();
        Utils.btnHideForListView(lv_list.getRefreshableView(), btn_add);
    }

    private void getPageData() {
        ll_loading.setStatus(LoadingLayout.Loading);
        getData();
    }

    private void loadFilterOptions() {
        List<FilterModel> options = new ArrayList<>();
        options.add(OrderStatusMenuModel.getFilterModel());

        List<MenuModel> sortModel = new ArrayList<>();
        sortModel.add(new CommonSortTypeMenuModel(CommonSortType.CREATE));
        sortModel.add(new CommonSortTypeMenuModel(CommonSortType.AMOUNT));
        options.add(new FilterModel(sortModel, "排序", MenuListType.SINGLE_LIST_SINGLE_SEL));

        DefaultMenuAdapter adapter = new DefaultMenuAdapter(getContext(), options);
        filterMenu.setMenuAdapter(adapter);
        adapter.setCallback(new OnMenuModelsSelected() {
            @Override
            public void onMenuModelsSelected(int menuIndex, List<MenuModel> selectedModels, Object userInfo) {
                filterMenu.close();
                MenuModel model = selectedModels.get(0);
                String key = model.getKey();
                String value = model.getValue();
                filterMenu.headerTabBar.setTitleAtPosition(value, menuIndex);

                if (menuIndex == 0) { //
                    statusType = key;
                } else if (menuIndex == 1) { //
                    CommonSortType type = ((CommonSortTypeMenuModel) model).type;
                    if (type == CommonSortType.AMOUNT) {
                        field = "dealMoney";
                    } else if (type == CommonSortType.CREATE) {
                        field = "createdAt";
                    }
                }
                ll_loading.setStatus(LoadingLayout.Loading);
                isPullDown = true;
                page = 1;
                getData();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //新建
            case R.id.btn_add:
                mBundle = new Bundle();
                mBundle.putInt("fromPage", OrderDetailActivity.ORDER_ADD);
                startActivityForResult(new Intent(getActivity(), OrderAddActivity.class), getActivity().RESULT_FIRST_USER);
                getActivity().overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
                UmengAnalytics.umengSend(mActivity, UmengAnalytics.createOrder);
                break;
        }
    }

    private void getData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", page);
        map.put("pageSize", 15);
        map.put("status", statusType);
        map.put("filed", field);
        OrderService.getOrderMyList(map)
                .subscribe(new DefaultLoyoSubscriber<PaginationX<OrderListItem>>() {
                    @Override
                    public void onError(Throwable e) {
                        @LoyoErrorChecker.CheckType int type = listData.size() > 0 ?
                                LoyoErrorChecker.TOAST : LoyoErrorChecker.LOADING_LAYOUT;

                        LoyoErrorChecker.checkLoyoError(e, type, ll_loading);
                        lv_list.onRefreshComplete();
                    }

                    @Override
                    public void onNext(PaginationX<OrderListItem> orderListItemPaginationX) {
                        lv_list.onRefreshComplete();
                        ll_loading.setStatus(LoadingLayout.Success);
                        if (!isPullDown) {
                            listData.addAll(orderListItemPaginationX.records);
                        } else {
                            listData = orderListItemPaginationX.records;
                            if (listData.size() == 0)
                                ll_loading.setStatus(LoadingLayout.Empty);
                        }
                        adapter.setData(listData);
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

            //新建订单回调 订单详细 删除成功刷新列表
            case ExtraAndResult.REQUEST_CODE:
                isPullDown = true;
                page = 1;
                getData();
                break;

        }
    }
}
