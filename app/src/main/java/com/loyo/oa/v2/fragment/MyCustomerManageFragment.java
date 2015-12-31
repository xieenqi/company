package com.loyo.oa.v2.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.customer.CustomerAddActivity_;
import com.loyo.oa.v2.activity.customer.CustomerInfoActivity_;
import com.loyo.oa.v2.activity.customer.CustomerSearchActivity;
import com.loyo.oa.v2.adapter.CustomerExpandableListViewAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.beans.Pagination;
import com.loyo.oa.v2.beans.PagingGroupData;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.ServerAPI;
import com.loyo.oa.v2.tool.BaseAsyncHttpResponseHandler;
import com.loyo.oa.v2.tool.BaseMainListFragment;
import com.loyo.oa.v2.tool.LocationUtil;
import com.loyo.oa.v2.tool.customview.DropListMenu.DropItem;
import com.loyo.oa.v2.tool.customview.DropListMenu.DropListMenu;
import com.loyo.oa.v2.tool.customview.DropListMenu.OnDropItemSelectedListener;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshExpandableListView;

import org.apache.http.Header;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;


public class MyCustomerManageFragment extends BaseMainListFragment implements View.OnClickListener, LocationUtil.AfterLocation {

    public static final int TYPE_FILTER_CUSTOMER_ID = 3;
    public static final int TYPE_FILTER_TAG_IDS = TYPE_FILTER_CUSTOMER_ID + 1;

    String strSearch, strSearchOld;

    PullToRefreshExpandableListView expandableListView_customer;

    ViewGroup layout_nodata;

    ArrayList<Customer> lstData_customer = new ArrayList<Customer>();
    ArrayList<PagingGroupData<Customer>> pagingGroupDatas = new ArrayList<>();

    CustomerExpandableListViewAdapter adapter;

    int type = 0;

    DropListMenu mDropMenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new LocationUtil(mActivity, this);
        GetData(true, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_customer_manage, container, false);

            expandableListView_customer = (PullToRefreshExpandableListView) mView.findViewById(R.id.expandableListView_customer);
            mDropMenu = (DropListMenu) mView.findViewById(R.id.drop_menu);
            layout_nodata = (ViewGroup) mView.findViewById(R.id.layout_nodata);

            btn_add = (Button) mView.findViewById(R.id.btn_add);
            btn_add.setOnTouchListener(Global.GetTouch());
            btn_add.setOnClickListener(this);

            init();
            initMenu();
        }

        return mView;
    }

    void init() {
        expandableListView_customer.setOnRefreshListener(this);
        ExpandableListView expandableListView = expandableListView_customer.getRefreshableView();
        if (null == adapter) {
            adapter = new CustomerExpandableListViewAdapter(mActivity, pagingGroupDatas);
            expandableListView.setAdapter(adapter);
        } else {
            adapter.setData(pagingGroupDatas);
            adapter.notifyDataSetChanged();
        }
        expand();

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Intent intent = new Intent();
                intent.putExtra("Customer", (Customer) adapter.getChild(groupPosition, childPosition));
                intent.setClass(mActivity, CustomerInfoActivity_.class);
                startActivityForResult(intent, REQUEST_REVIEW);

                return false;
            }
        });
    }

    void expand() {
        for (int i = 0; i < pagingGroupDatas.size(); i++) {
            expandableListView_customer.getRefreshableView().expandGroup(i, false);
        }
    }

    void initMenu() {
        ArrayList<DropItem> source = new ArrayList<>();

        DropItem time = new DropItem("时间");
        time.addSubDropItem(new DropItem("按更新时间 倒序", 1));
        time.addSubDropItem(new DropItem("按更新时间 顺序", 2));
        time.addSubDropItem(new DropItem("按创建时间 倒序", 3));
        time.addSubDropItem(new DropItem("按创建时间 顺序", 4));
        source.add(time);

        DropItem tag = new DropItem("标签");
        tag.setSelectType(DropItem.GROUP_SINGLE);
        tag.addSubDropItem(new DropItem("全部标签", 0));

        ArrayList<DropItem> tagDropItem2 = new ArrayList<>();
        tagDropItem2.add(new DropItem("标签2-1", 1));
        tagDropItem2.add(new DropItem("标签2-2", 1));
        tagDropItem2.add(new DropItem("标签2-3", 1));
        tagDropItem2.add(new DropItem("标签2-4", 1));
        tagDropItem2.add(new DropItem("标签2-5", 1));
        tagDropItem2.add(new DropItem("标签2-6", 1));
        tagDropItem2.add(new DropItem("标签2-7", 1));
        tagDropItem2.add(new DropItem("标签2-8", 1));
        tagDropItem2.add(new DropItem("标签2-9", 1));
        tagDropItem2.add(new DropItem("标签2-10", 1));
        tagDropItem2.add(new DropItem("标签2-11", 1));
        tagDropItem2.add(new DropItem("标签2-12", 1));
        tagDropItem2.add(new DropItem("标签2-13", 1));

        DropItem tag2 = new DropItem("测试标签2", 2, tagDropItem2);
        tag.addSubDropItem(tag2);

        ArrayList<DropItem> tagDropItem3 = new ArrayList<>();
        tagDropItem3.add(new DropItem("标签3-1", 1));
        tagDropItem3.add(new DropItem("标签3-2", 1));
        tagDropItem3.add(new DropItem("标签3-3", 1));
        tagDropItem3.add(new DropItem("标签3-4", 1));
        tagDropItem3.add(new DropItem("标签3-5", 1));
        tagDropItem3.add(new DropItem("标签3-6", 1));
        tagDropItem3.add(new DropItem("标签3-7", 1));
        tagDropItem3.add(new DropItem("标签3-8", 1));
        tagDropItem3.add(new DropItem("标签3-9", 1));
        tagDropItem3.add(new DropItem("标签3-10", 1));
        tagDropItem3.add(new DropItem("标签3-11", 1));
        tagDropItem3.add(new DropItem("标签3-12", 1));
        tagDropItem3.add(new DropItem("标签3-13", 1));

        DropItem tag3 = new DropItem("测试标签3", 3, tagDropItem3);
        tag.addSubDropItem(tag3);

//        tag.addSubDropItem(new DropItem("测试标签3", 3));

        source.add(tag);

        mDropMenu.setmMenuItems(source);

        mDropMenu.setMenuSelectedListener(new OnDropItemSelectedListener() {
            @Override
            public void onSelected(View listview, int ColumnIndex, SparseArray<DropItem> items) {
                if (items != null) {
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < items.size(); i++) {
                        sb.append(items.get(items.keyAt(i)).getName()).append(",");
                    }

                    Global.ToastLong(sb.toString());
                }
            }

            @Override
            public void onCancelAll(int ColumnIndex) {

            }
        });
    }

    void jumpToSearch() {
        app.startActivity(mActivity, CustomerSearchActivity.class, MainApp.ENTER_TYPE_RIGHT, false, null);
    }

    public void GetData(Boolean isTopAdd, Boolean isBottomAdd) {
        if (type == TYPE_FILTER_TAG_IDS) {
            GetData(isTopAdd, isBottomAdd, strSearch);
        }
        RequestParams params = new RequestParams();

        long pageIndex = 1;

        //搜索人员不为空，并且搜索人员Id不是当前登陆人
        if (strSearch != null) {
            //搜索后的上下拉
            if (strSearch.equals(strSearchOld) && (isTopAdd || isBottomAdd)) {
                pageIndex = isTopAdd ? 1 : (pagination.getPageIndex() + 1);
            } else {
                //搜索
                pageIndex = 1;
                strSearchOld = strSearch;
            }

            params.put("userId", strSearch);
            params.put("type", 3);
        } else {
            params.put("type", 2);
            pageIndex = isTopAdd ? 1 : (pagination.getPageIndex() + 1);
        }

        params.put("pageIndex", pageIndex);
        params.put("pageSize", isTopAdd ? pagination.getPageSize() : 20);

        ArrayList<ServerAPI.ParamInfo> lstParamInfo = new ArrayList<>(Arrays.asList(
                new ServerAPI.ParamInfo("isTopAdd", isTopAdd),
                new ServerAPI.ParamInfo("isBottomAdd", isBottomAdd))
        );

        ServerAPI.request(this, ServerAPI.GET, FinalVariables.customers, params, AsyncHttpResponseHandler_customers_get.class, lstParamInfo);
    }

    public void GetData(Boolean isTopAdd, Boolean isBottomAdd, String tagItemIds) {
        RequestParams params = new RequestParams();

        long pageIndex = 1;

        params.put("tagItemIds", tagItemIds);
        params.put("type", 2);
        pageIndex = isTopAdd ? 1 : (pagination.getPageIndex() + 1);

        params.put("pageIndex", pageIndex);
        params.put("pageSize", isTopAdd ? pagination.getPageSize() : 20);

        ArrayList<ServerAPI.ParamInfo> lstParamInfo = new ArrayList<>(Arrays.asList(
                new ServerAPI.ParamInfo("isTopAdd", isTopAdd),
                new ServerAPI.ParamInfo("isBottomAdd", isBottomAdd))
        );

        ServerAPI.request(this, ServerAPI.GET, FinalVariables.customers, params, AsyncHttpResponseHandler_customers_get_tagItemIds.class, lstParamInfo);
    }


    @Override
    public void OnLocationSucessed(String _address, final double longitude, final double latitude, float radius) {

//        app.getRestAdapter().create(ICustomer.class).getNearCustomer(String.valueOf(latitude), String.valueOf(longitude),
//                new RCallback<ArrayList<Customer>>() {
//                    @Override
//                    public void success(ArrayList<Customer> customers, Response response) {
//                        LatLng center = LocationUtil.convert(1, latitude, longitude);
////                        initNearCustomer(customers, center);
//                    }
//                });
    }

    @Override
    public void OnLocationFailed() {
        Global.Toast("获取地址出错!");
    }

    public class AsyncHttpResponseHandler_customers_get extends BaseAsyncHttpResponseHandler {
        @Override
        public Activity getActivity() {
            return mActivity;
        }

        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {

            Pagination pagination_current = null;
            try {
                pagination_current = MainApp.gson.fromJson(getStr(arg2), Pagination.class);
            } catch (Exception ex) {
            }

            if (!Pagination.isEmpty(pagination_current)) {

                Type type = new TypeToken<ArrayList<Customer>>() {
                }.getType();
                ArrayList<Customer> lstData_customer_current = MainApp.gson.fromJson(MainApp.gson.toJson(pagination_current.getRecords()), type);

                if (lstData_customer_current != null && lstData_customer_current.size() > 0) {
                    layout_nodata.setVisibility(View.GONE);
                    pagination.setPageIndex(pagination_current.getPageIndex());

                    if (isTopAdd && lstData_customer.size() > 0) {
                        for (int i = 0; i < lstData_customer_current.size(); i++) {
                            Customer tempCustomer = lstData_customer_current.get(i);
                            for (int j = 0; j < lstData_customer.size(); j++) {
                                if (lstData_customer.get(j).getId() == tempCustomer.getId()) {
                                    lstData_customer.set(j, tempCustomer);
                                    break;
                                }
                            }
                        }
                    }

                    if (isBottomAdd || lstData_customer.isEmpty())
                        lstData_customer.addAll(lstData_customer_current);

                    pagingGroupDatas = PagingGroupData.convertGroupData(lstData_customer);
                    adapter.setData(pagingGroupDatas);
                    adapter.notifyDataSetChanged();
                    expand();
                } else {

                    if ((isBottomAdd || isTopAdd) && lstData_customer.size() > 0) {
                        layout_nodata.setVisibility(View.VISIBLE);
                    } else {
                        layout_nodata.setVisibility(View.GONE);
                    }

                    Global.Toast(isBottomAdd ? R.string.app_list_noMoreData : R.string.app_no_newest_data);
                }
            } else if (isBottomAdd || isTopAdd) {
                Global.Toast(isBottomAdd ? R.string.app_list_noMoreData : R.string.app_no_newest_data);
            } else {
                layout_nodata.setVisibility(View.VISIBLE);
            }

            expandableListView_customer.onRefreshComplete();
        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            expandableListView_customer.onRefreshComplete();
            super.onFailure(i, headers, bytes, throwable);
        }
    }

    public class AsyncHttpResponseHandler_customers_get_tagItemIds extends BaseAsyncHttpResponseHandler {
        @Override
        public Activity getActivity() {
            return mActivity;
        }

        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {

            Pagination pagination_current = null;
            try {
                pagination_current = MainApp.gson.fromJson(getStr(arg2), Pagination.class);
            } catch (Exception ex) {
            }

            if (!Pagination.isEmpty(pagination_current)) {
                Type type = new TypeToken<ArrayList<Customer>>() {
                }.getType();
                ArrayList<Customer> lstData_customer_current = MainApp.gson.fromJson(MainApp.gson.toJson(pagination_current.getRecords()), type);

                if (lstData_customer_current != null && lstData_customer_current.size() > 0) {
                    layout_nodata.setVisibility(View.GONE);
                    pagination.setPageIndex(pagination_current.getPageIndex());

                    //下接获取最新时，清空
                    if (!isBottomAdd && lstData_customer.size() > 0) {
                        lstData_customer.clear();
                    }

                    if (isBottomAdd || lstData_customer.isEmpty())
                        lstData_customer.addAll(lstData_customer_current);

                    pagingGroupDatas = PagingGroupData.convertGroupData(lstData_customer);

                    adapter.setData(pagingGroupDatas);
                    adapter.notifyDataSetChanged();
                    expand();
                } else {

                    if ((isBottomAdd || isTopAdd) && lstData_customer.size() > 0) {
                        layout_nodata.setVisibility(View.VISIBLE);
                    } else {
                        layout_nodata.setVisibility(View.GONE);
                    }

                    Global.Toast(isBottomAdd ? R.string.app_list_noMoreData : R.string.app_no_newest_data);
                }
            } else if (isBottomAdd || isTopAdd) {
                Global.Toast(isBottomAdd ? R.string.app_list_noMoreData : R.string.app_no_newest_data);
            } else {
                layout_nodata.setVisibility(View.VISIBLE);
            }

            expandableListView_customer.onRefreshComplete();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add:
                Intent intent = new Intent();
                intent.setClass(mActivity, CustomerAddActivity_.class);
                startActivityForResult(intent, REQUEST_CREATE);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK || data == null
                || data.getExtras() == null
                || data.getExtras().size() == 0) {
            return;
        }

        Customer customer = (Customer) data.getSerializableExtra(Customer.class.getName());

        if (customer == null) {
            return;
        }

        switch (requestCode) {
            case REQUEST_CREATE:
                if (layout_nodata.getVisibility() == View.VISIBLE) {
                    layout_nodata.setVisibility(View.GONE);
                }

                lstData_customer.add(0, customer);
                // fixes bug293 ykb 07-14 或许不需要同步一次全部数据
                //GetData(true, false);

                OnLocationSucessed("", app.longitude, app.latitude, 0);
                break;
            case REQUEST_REVIEW:

                for (int i = 0; i < lstData_customer.size(); i++) {
                    if (lstData_customer.get(i).getId() == customer.getId()) {
                        lstData_customer.set(i, customer);
                        break;
                    }
                }

                break;
        }

        pagingGroupDatas = PagingGroupData.convertGroupData(lstData_customer);
        init();
    }

    public void filterGetData(String _strSearch, String tagItemIds) {
    }

    public void filterGetData(String _strSearch, int type_) {
        pagination.clear();
        lstData_customer.clear();
        strSearch = _strSearch;
        type = type_;
        switch (type_) {
            case TYPE_FILTER_CUSTOMER_ID:
                GetData(false, false);
                break;
            case TYPE_FILTER_TAG_IDS:
                GetData(false, false, strSearch);
                break;
        }
    }

}
