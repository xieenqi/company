package com.loyo.oa.v2.activity;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.LocationUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

/**
 * com.loyo.oa.v2.activity
 * 描述 :附近客户地图显示界面
 * 作者 : ykb
 * 时间 : 15/9/18.
 */
@EActivity(R.layout.activity_nearbycustomers_map)
public class NearByCustomersMapActivity extends BaseActivity implements LocationUtil.AfterLocation {
    @ViewById ViewGroup layout_back;
    @ViewById TextView tv_title;

    @ViewById MapView mapview;

    @Extra ArrayList<Customer> customers;

    private BaiduMap mBaiduMap;

    private BitmapDescriptor bdA = BitmapDescriptorFactory.fromResource(R.drawable.icon_mark);


    @AfterViews
    void initViews() {
        setTouchView(NO_SCROLL);
        layout_back.setOnTouchListener(Global.GetTouch());
        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText("附近客户");

        new LocationUtil(this, this);
    }

    @Click(R.id.layout_back)
    void back() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        app.finishActivity(this, MainApp.ENTER_TYPE_TOP, RESULT_CANCELED, null);
    }

    @Override
    public void OnLocationFailed() {

    }

    @Override
    public void OnLocationSucessed(String address, double longitude, double latitude, float radius) {
        initMap(latitude, longitude);
        showCustomers();
    }

    /**
     * 初始化地图
     *
     * @param lat
     * @param lng
     */
    private void initMap(double lat, double lng) {
        mBaiduMap = mapview.getMap();
        LatLng center = LocationUtil.convert(1, lat, lng);
        //更新地图
        MapStatus mMapStatus = new MapStatus.Builder().target(center).zoom(16).build();

        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);
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
            mBaiduMap.addOverlay(point.options);
        }

        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                OverlayItem point = points.get(marker.getZIndex());
                TextView textView = new TextView(app);
                int padding=app.diptoPx(5);
                textView.setPadding(padding,padding,padding,padding);
                textView.setGravity(Gravity.CENTER);
                textView.setBackgroundColor(getResources().getColor(R.color.whitesmoke));
                textView.setTextColor(getResources().getColor(R.color.title_bg1));
                textView.setText(point.customer.getName());

                InfoWindow infoWindow=new InfoWindow(textView,point.latLng,-app.diptoPx(30));
                mBaiduMap.showInfoWindow(infoWindow);
                return true;
            }
        });
    }

    /**
     * 构建地图上的覆盖物数据集
     *
     * @return
     */
    private ArrayList<OverlayItem> buildPoints() {
        ArrayList<OverlayItem> points = new ArrayList<>();

        if (null == customers || customers.isEmpty()) {
            return points;
        }
        for (int i = 0; i < customers.size(); i++) {
            Customer customer = customers.get(i);
            String gpsInfo = customer.getLoc().getLoc()[1]+","+customer.getLoc().getLoc()[0];
            if (!TextUtils.isEmpty(gpsInfo) && gpsInfo.contains(",")) {
                String gps[] = gpsInfo.split(",");
                double lat = Double.parseDouble(gps[1]);
                double lng = Double.parseDouble(gps[0]);
                LatLng latLng = LocationUtil.convert(1, lat, lng);
                OverlayOptions point = new MarkerOptions().position(latLng).icon(bdA).zIndex(i).draggable(false);
                OverlayItem item=new OverlayItem();
                item.latLng=latLng;
                item.options=point;
                item.customer=customer;
                points.add(item);
            }
        }

        return points;
    }

    private class OverlayItem {
        OverlayOptions options;
        LatLng latLng;
        Customer customer;
    }
}