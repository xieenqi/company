package com.loyo.oa.v2.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import com.loyo.oa.v2.activity.CustomerSearchActivity;
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
import com.loyo.oa.v2.tool.ViewUtil;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshExpandableListView;

import org.apache.http.Header;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;


public class PublicCustomerManageFragment extends BaseMainListFragment implements View.OnClickListener, LocationUtil.AfterLocation {

    public static final int TYPE_FILTER_CUSTOMER_ID = 3;
    public static final int TYPE_FILTER_TAG_IDS = TYPE_FILTER_CUSTOMER_ID + 1;

    String strSearch, strSearchOld;

    PullToRefreshExpandableListView expandableListView_customer;

    ViewGroup layout_nodata;

    ArrayList<Customer> lstData_customer = new ArrayList<Customer>();
    ArrayList<PagingGroupData<Customer>> pagingGroupDatas = new ArrayList<>();

    CustomerExpandableListViewAdapter adapter;

    ViewUtil.OnTouchListener_view_transparency touch = ViewUtil.OnTouchListener_view_transparency.Instance();
    private int type = 0;

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
//            ((TextView) mView.findViewById(R.id.tv_title_1)).setText("客户管理");

//            img_arrow = (ImageView) mView.findViewById(R.id.img_arrow);
//
//            layout_near_customer = (ViewGroup) mView.findViewById(R.id.layout_near_customer);
//            layout_near_customer.setOnClickListener(this);
//            layout_near_customer.setOnTouchListener(touch);
//
//            layout_map = (ViewGroup) mView.findViewById(R.id.layout_map);
//            layout_map.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View view, MotionEvent motionEvent) {
//                    return false;
//                }
//            });

            expandableListView_customer = (PullToRefreshExpandableListView) mView.findViewById(R.id.expandableListView_customer);

//            listview_near_customer = (ListView) mView.findViewById(R.id.listview_near_customer);

            btn_add = (Button) mView.findViewById(R.id.btn_add);
            btn_add.setOnTouchListener(touch);
            btn_add.setOnClickListener(this);

//            mMapView = (MapView) mView.findViewById(R.id.bmapView);
//            mBaiduMap = mMapView.getMap();

            layout_nodata = (ViewGroup) mView.findViewById(R.id.layout_nodata);

            init();

//            ViewGroup vgSearch = (ViewGroup) mView.findViewById(R.id.img_title_search_right);
//            vgSearch.setOnTouchListener(touch);
//            vgSearch.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    jumpToSearch();
//                }
//            });
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

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        bdA.recycle();
//    }

//    void initNearCustomer(final ArrayList<Customer> nearCustomers, LatLng center) {
//        if (nearCustomers == null || nearCustomers.size() == 0) {
//            layout_near_customer.setOnClickListener(null);
//            return;
//        }
//
//        layout_near_customer.setVisibility(View.VISIBLE);
//        tv_findcustomer = (TextView) mView.findViewById(R.id.tv_findcustomer);
//        tv_findcustomer.setText(String.format("发现%d个附近的客户", nearCustomers.size()));
//
//        //更新地图
//        MapStatus mMapStatus = new MapStatus.Builder()
//                .target(center)
//                .zoom(16)
//                .build();
//
//        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
//        //改变地图状态
//        mBaiduMap.setMapStatus(mMapStatusUpdate);
//
//        List<LatLng> points = new ArrayList<>();
//        for (Customer customer : nearCustomers) {
//            try {
////                LatLng p = new LatLng(
////                        Double.parseDouble(customer.getGpsInfo().split(",")[1]),
////                        Double.parseDouble(customer.getGpsInfo().split(",")[0]));
//                LatLng p = LocationUtil.convert(1, Double.parseDouble(customer.getGpsInfo().split(",")[1]), Double.parseDouble(customer.getGpsInfo().split(",")[0]));
//                points.add(p);
//
//                OverlayOptions ooA = new MarkerOptions().position(p).icon(bdA)
//                        .zIndex(9).draggable(false);
//
//                mBaiduMap.addOverlay(ooA);
//
//            } catch (Exception ex) {
//            }
//        }
//
//        CommonAdapter<Customer> nearCustomerAdapter = new CommonAdapter<Customer>(
//                mActivity, nearCustomers, R.layout.item_customer_list_near
//        ) {
//            @Override
//            public void convert(ViewHolder holder, Customer customer) {
//                holder.setText(R.id.tv_title, customer.getName())
//                        .setText(R.id.tv_distance, customer.getDistance());
//            }
//        };
//
////        //绑定列表
////        listview_near_customer.setAdapter(nearCustomerAdapter);
////        listview_near_customer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
////            @Override
////            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                Intent intent = new Intent();
////                intent.putExtra("Customer", nearCustomers.get((int) id));
////                intent.setClass(mActivity, CustomerInfoActivity_.class);
////                startActivityForResult(intent, REQUEST_REVIEW);
////            }
////        });
//    }

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
                    //实现下拉刷新时已加载出来的数据不被清空以及上拉加载时不会滑到第一页 ykb 07-15
//                    //下接获取最新时，清空
//                    if (!isBottomAdd && lstData_customer.size() > 0) {
//                        lstData_customer.clear();
//                    }
                    //实现下拉刷新时已加载出来的数据不被清空以及上拉加载时不会滑到第一页 ykb 07-15
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
                    //实现下拉刷新时已加载出来的数据不被清空以及上拉加载时不会滑到第一页 ykb 07-15
                    if (isBottomAdd || lstData_customer.isEmpty())
                        lstData_customer.addAll(lstData_customer_current);

                    pagingGroupDatas = PagingGroupData.convertGroupData(lstData_customer);
                    //实现下拉刷新时已加载出来的数据不被清空以及上拉加载时不会滑到第一页 ykb 07-15
                    adapter.setData(pagingGroupDatas);
                    adapter.notifyDataSetChanged();
                    expand();
//                    init();
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

//            case R.id.layout_near_customer:
//                layout_map.setVisibility(layout_map.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
//
//                RotateAnimation rotateAnimation = new RotateAnimation(0f, 180f,
//                        Animation.RELATIVE_TO_SELF, 0.5f,// X轴
//                        Animation.RELATIVE_TO_SELF, 0.5f);// y轴
//
//                rotateAnimation.setDuration(500);
//                rotateAnimation.setFillAfter(true);             //保留在终止位置
//                rotateAnimation.setFillEnabled(true);
//                rotateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
//
//                img_arrow.setRotation(mRotation);
//                img_arrow.startAnimation(rotateAnimation);
//
//                mRotation = (mRotation == 0f ? 180f : 0f);
//
//                if (getActivity() instanceof BaseFragmentActivity) {
//
//                    BaseFragmentActivity activity = (BaseFragmentActivity) getActivity();
//
//                    //TODO:
//                    if (layout_map.getVisibility() == View.VISIBLE) {
//                        //去掉手势
//                        activity.setTouchView(R.id.layout_customer);
//                    } else {
//                        //加上手势
//                        activity.setTouchView(0);
//                    }
//                }
//
//                break;
        }
    }

//    float mRotation = 0f;

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
//        adapter.notifyDataSetChanged();
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
