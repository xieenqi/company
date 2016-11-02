package com.loyo.oa.v2.activityui.customer.fragment;

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
import android.widget.Button;
import com.loyo.oa.dropdownmenu.DropDownMenu;
import com.loyo.oa.dropdownmenu.adapter.DefaultMenuAdapter;
import com.loyo.oa.dropdownmenu.callback.OnMenuModelsSelected;
import com.loyo.oa.dropdownmenu.filtermenu.TagMenuModel;
import com.loyo.oa.dropdownmenu.filtermenu.TimeFilterModel;
import com.loyo.oa.dropdownmenu.model.FilterModel;
import com.loyo.oa.dropdownmenu.model.MenuModel;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.CustomerDetailInfoActivity_;
import com.loyo.oa.v2.activityui.customer.CustomerManagerActivity;
import com.loyo.oa.v2.activityui.customer.adapter.CommCustomerAdapter;
import com.loyo.oa.v2.activityui.customer.event.MyCustomerListRushEvent;
import com.loyo.oa.v2.activityui.customer.model.Tag;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshListView;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.BaseMainListFragment;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import org.greenrobot.eventbus.Subscribe;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【公海客户】列表
 * Created by yyy on 16/6/1.
 */
public class CommCustomerFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener2 {

    private View mView;
    private Button btn_add;
    private ViewStub emptyView;
    private PullToRefreshListView listView;
    private CommCustomerAdapter adapter;
    private DropDownMenu filterMenu;

    private String field = "lastActAt";
    private String order = "desc";
    private String tagsParams = "";
    private int page = 1;
    private boolean isPullUp = false;

    private PaginationX<Customer> mPagination = new PaginationX<>(20);
    private ArrayList<Customer> mCustomers = new ArrayList<>();
    private ArrayList<Tag> mTags;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                //公海挑入
                case CustomerManagerActivity.CUSTOMER_COMM_RUSH:
                    getData();
                    break;
            }
        }
    };

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
        mTags = (ArrayList<Tag>) getArguments().getSerializable("tag");

        btn_add = (Button) view.findViewById(R.id.btn_add);

        emptyView = (ViewStub) mView.findViewById(R.id.vs_nodata);
        listView = (PullToRefreshListView) view.findViewById(R.id.lv_list);
        listView.setEmptyView(emptyView);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(this);

        btn_add.setVisibility(View.GONE);

        filterMenu = (DropDownMenu) view.findViewById(R.id.drop_down_menu);
        showLoading("");
        getData();
    }

    private void loadFilterOptions() {
        List<FilterModel> options = new ArrayList<>();
        options.add(TimeFilterModel.getFilterModel());
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
                }
                else if (menuIndex == 1) { // TagFilter
                    tagsParams = userInfo.toString();
                }
                getData();
            }
        });
    }

    /**
     * 绑定数据
     */
    private void bindData() {

        if (null == adapter) {
            adapter = new CommCustomerAdapter(getActivity(), mCustomers, mHandler);
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
                intent.putExtra(ExtraAndResult.EXTRA_TYPE, CustomerManagerActivity.CUSTOMER_COMM);
                intent.setClass(mActivity, CustomerDetailInfoActivity_.class);
                startActivityForResult(intent, BaseMainListFragment.REQUEST_REVIEW);
                mActivity.overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
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
        LogUtil.d("客户查询传递参数：" + MainApp.gson.toJson(params));
        RestAdapterFactory.getInstance().build(FinalVariables.QUERY_CUSTOMERS_PUBLIC).create(ICustomer.class).query(params, new RCallback<PaginationX<Customer>>() {
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

    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            }
        }
    };

    /**
     * 刷新列表回调
     * */
    @Subscribe
    public void onMyCustomerListRushEvent(MyCustomerListRushEvent event){
        getData();
    }

}
