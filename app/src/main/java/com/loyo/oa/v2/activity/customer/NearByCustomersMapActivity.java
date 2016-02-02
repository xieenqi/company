package com.loyo.oa.v2.activity.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.LocationUtilGD;
import com.loyo.oa.v2.tool.LogUtil;

import java.util.ArrayList;

/**
 * com.loyo.oa.v2.activity
 * 描述 :附近客户地图显示界面
 * 作者 : ykb
 * 时间 : 15/9/18.
 */
//@EActivity(R.layout.activity_nearbycustomers_map)
public class NearByCustomersMapActivity extends BaseActivity implements LocationUtilGD.AfterLocation {

    private LinearLayout layout_back, ll_map;
    private TextView tv_title;
    private MapView mapview;
    private ArrayList<Customer> customers;//传过来的客户数据
    private int customerType;//传过来的客户类型
    //我的附近客户个数
    private int isMySize;
    // private BaiduMap mBaiduMap;
    private AMap aMap;

    //地图覆盖图标类型
//    private BitmapDescriptor markCompany = BitmapDescriptorFactory.fromResource(R.drawable.icon_mark_company);//公司
//    private BitmapDescriptor markMy = BitmapDescriptorFactory.fromResource(R.drawable.icon_mark_my);//我
//    private BitmapDescriptor markTeam = BitmapDescriptorFactory.fromResource(R.drawable.icon_mark_team);//团队
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearbycustomers_map);
        layout_back = (LinearLayout) findViewById(R.id.layout_back);
        ll_map = (LinearLayout) findViewById(R.id.ll_map);
        tv_title = (TextView) findViewById(R.id.tv_title);
        mapview = (MapView) findViewById(R.id.mapview);
        LogUtil.d("地图是否空：" + savedInstanceState);
        if (savedInstanceState != null) {
            mapview.onCreate(savedInstanceState);// 必须要写【此处要报错】
        }
//        aMap = mapview.getMap();
//        ll_map.addView(mapview);
        iniIntent();
        initViews();
    }

    // @AfterViews
    void initViews() {
        setTouchView(NO_SCROLL);
        layout_back.setOnTouchListener(Global.GetTouch());
        layout_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText("附近客户");
        //new LocationUtilGD(this, this);
        LogUtil.d(customerType + " 地图显示的客户数据： " + MainApp.gson.toJson(customers));
    }

    @Override
    public void onBackPressed() {
        app.finishActivity(this, MainApp.ENTER_TYPE_TOP, RESULT_CANCELED, null);
    }

    private void iniIntent() {
        Intent intent = getIntent();
        customers = (ArrayList<Customer>) intent.getSerializableExtra("customers");
        customerType = intent.getIntExtra(ExtraAndResult.EXTRA_TYPE, -1);
        isMySize = intent.getIntExtra(ExtraAndResult.EXTRA_DATA, -1);
    }

    /**
     * 初始化地图
     *
     * @param lat
     * @param lng
     */
    private void initMap(double lat, double lng) {


//        LatLng center = LocationUtil.convert(1, lat, lng);
//        //更新地图
//        MapStatus mMapStatus = new MapStatus.Builder().target(center).zoom(16).build();
//
//        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
//        //改变地图状态
//        //mBaiduMap.setMapStatus(mMapStatusUpdate);
    }

    /**
     * 显示附近客户
     */
    private void showCustomers() {
        if (null == customers || customers.isEmpty()) {
            return;
        }
        final ArrayList<OverlayItem> points = buildPoints();
        if (points.isEmpty()) {
            return;
        }
        for (int i = 0; i < points.size(); i++) {
            OverlayItem point = points.get(i);
            //mBaiduMap.addOverlay(point.options);
        }

//        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//                final OverlayItem point = points.get(marker.getZIndex());
//                View view = LayoutInflater.from(NearByCustomersMapActivity.this).inflate(R.layout.map_bubble, null);
//                TextView name = (TextView) view.findViewById(R.id.tv_name);
//                TextView address = (TextView) view.findViewById(R.id.tv_address);
//                TextView navigation = (TextView) view.findViewById(R.id.tv_navigation);
//                name.setText(point.customer.name);
//                address.setText(point.customer.loc.addr);
//                navigation.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {//开始导航
//                        Utils.goWhere(NearByCustomersMapActivity.this, point.customer.loc.loc[1], point.customer.loc.loc[0]);
//                    }
//                });
//                InfoWindow infoWindow = new InfoWindow(view, point.latLng, -app.diptoPx(35));
//                mBaiduMap.showInfoWindow(infoWindow);
//                return true;
//            }
//        });
    }

    /**
     * 构建地图上的覆盖物数据集
     *
     * @return
     */
    private ArrayList<OverlayItem> buildPoints() {
        ArrayList<OverlayItem> points = new ArrayList<>();
//        if (null == customers || customers.isEmpty()) {
//            return points;
//        }
//        for (int i = 0; i < customers.size(); i++) {
//            Customer customer = customers.get(i);
//            // String gpsInfo = customer.loc.loc[1]+","+customer.loc.loc[0];
//            //if (!TextUtils.isEmpty(gpsInfo) && gpsInfo.contains(",")) {
//            // String gps[] = gpsInfo.split(",");
//            double lat = customer.loc.loc[1];
//            double lng = customer.loc.loc[0];
//            LatLng latLng = LocationUtil.convert(1, lat, lng);
//            OverlayOptions point = new MarkerOptions().position(latLng).icon
//                    (customerType == 1 ? (i < isMySize ? markMy : markCompany) : markTeam)
//                    .zIndex(i).draggable(false);
//            OverlayItem item = new OverlayItem();
//            item.latLng = latLng;
//            item.options = point;
//            item.customer = customer;
//            points.add(item);
//            // }
//        }

        return points;
    }

    @Override
    public void OnLocationGDSucessed(String address, double longitude, double latitude, String radius) {
        initMap(latitude, longitude);
        showCustomers();
    }

    @Override
    public void OnLocationGDFailed() {
        Toast("定位失败");
    }

    private class OverlayItem {
        OverlayOptions options;
        LatLng latLng;
        Customer customer;
    }

//    /**
//     * 方法必须重写
//     */
//    @Override
//    protected void onResume() {
//        super.onResume();
//        mapview.onResume();
//    }
//
//    /**
//     * 方法必须重写
//     */
//    @Override
//    protected void onPause() {
//        super.onPause();
//        mapview.onPause();
//    }
//
//    /**
//     * 方法必须重写
//     */
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        mapview.onSaveInstanceState(outState);
//    }
//
//    /**
//     * 方法必须重写
//     */
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mapview.onDestroy();
//    }
}
