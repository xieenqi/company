package com.loyo.oa.v2.activity.customer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.LocationUtilGD;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.Utils;

import java.util.ArrayList;

/**
 * com.loyo.oa.v2.activity
 * 描述 :附近客户地图显示界面
 * 作者 : ykb
 * 时间 : 15/9/18.
 */
public class NearByCustomersMapActivity extends BaseActivity implements LocationUtilGD.AfterLocation {

    private LinearLayout layout_back;
    private TextView tv_title;
    private MapView mapview;
    private ArrayList<Customer> customers;//传过来的客户数据
    private int customerType;//传过来的客户类型
    //我的附近客户个数
    private int isMySize;
    private AMap aMap;
    private ArrayList<OverlayItem> points;
    private OverlayItem point;//infowindow对象数据
    private BitmapDescriptor markCompany;//公司
    private BitmapDescriptor markMy;//我
    private BitmapDescriptor markTeam;//团队

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearbycustomers_map);
        layout_back = (LinearLayout) findViewById(R.id.layout_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        mapview = (MapView) findViewById(R.id.mapview);
        mapview.onCreate(savedInstanceState);// 必须要写【此处要报错】
        markCompany = BitmapDescriptorFactory.fromResource(R.drawable.icon_mark_company);//公司
        markMy = BitmapDescriptorFactory.fromResource(R.drawable.icon_mark_my);//我
        markTeam = BitmapDescriptorFactory.fromResource(R.drawable.icon_mark_team);//团队
        iniIntent();
        initViews();
    }

    void initViews() {//30.679879, 104.064855)成都市经纬度
        if (aMap == null) {
            aMap = mapview.getMap();//地图初始化到成都
            LatLngBounds bounds = new LatLngBounds.Builder().include(new LatLng(30.679879, 104.064855)).build();
            aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 1));
            aMap.setMyLocationEnabled(true);
        }
        setTouchView(NO_SCROLL);
        layout_back.setOnTouchListener(Global.GetTouch());
        layout_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onBackPressed();
            }
        });
        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText("附近客户");
        new LocationUtilGD(this, this);
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
     * 在地图上添加marker
     */
    private void addMarkersToMap() {

        //文字显示标注，可以设置显示内容，位置，字体大小颜色，背景色旋转角度,Z值等
//        TextOptions textOptions = new TextOptions().position(new LatLng(39.90403, 116.407525))
//                .text("Text").fontColor(Color.BLACK)
//                .backgroundColor(Color.BLUE).fontSize(30).rotate(20).align(Text.ALIGN_CENTER_HORIZONTAL, Text.ALIGN_CENTER_VERTICAL)
//                .zIndex(1.f).typeface(Typeface.DEFAULT_BOLD);
//        aMap.addText(textOptions);


//        markerOption = new MarkerOptions();
//        markerOption.position(Constants.XIAN);
//        markerOption.title("西安市").snippet("西安市：34.341568, 108.940174");
//        markerOption.draggable(true);
//        markerOption.icon(BitmapDescriptorFactory
//                .fromResource(R.drawable.arrow));
//        marker2 = aMap.addMarker(markerOption);
//        marker2.showInfoWindow();
//        // marker旋转90度
//        marker2.setRotateAngle(90);

        // 动画效果
//        ArrayList<BitmapDescriptor> giflist = new ArrayList<BitmapDescriptor>();
//        giflist.add(BitmapDescriptorFactory
//                .defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
//        giflist.add(BitmapDescriptorFactory
//                .defaultMarker(BitmapDescriptorFactory.HUE_RED));
//        giflist.add(BitmapDescriptorFactory
//                .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
//        aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
//                .position(Constants.ZHENGZHOU).title("郑州市").icons(giflist)
//                .draggable(true).period(10));

//        drawMarkers();// 添加10个带有系统默认icon的marker
    }

    @Override
    public void OnLocationGDSucessed(final String address, final double longitude, final double latitude, final String radius) {
        initMap(latitude, longitude);
        LocationUtilGD.sotpLocation();
    }

    @Override
    public void OnLocationGDFailed() {
        Toast("定位失败");
        LocationUtilGD.sotpLocation();
    }


    /**
     * 初始化地图
     *
     * @param lat
     * @param lng
     */
    private void initMap(final double lat, final double lng) {
        LatLngBounds bounds = new LatLngBounds.Builder().include(new LatLng(lat, lng)).build();
        aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10, 10, 10));
        aMap.moveCamera(CameraUpdateFactory.zoomTo(18));
        aMap.postInvalidate();//刷新地图
    }

    /**
     * 显示附近客户
     */
    private void showCustomers() {
        if (null == customers || customers.isEmpty()) {
            return;
        }
        points = buildPoints();
        if (points.isEmpty()) {
            return;
        }
        setInfoWindow();
    }

    public void setInfoWindow() {
        aMap.setInfoWindowAdapter(new AMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(final Marker marker) {
                return render(marker);
            }

            @Override
            public View getInfoContents(final Marker marker) {
                return render(marker);
            }
        });
    }


    /**
     * 自定义infowinfow窗口
     */
    public View render(final Marker marker) {
        for (OverlayItem element : points) {
            if (marker.getTitle().equals(element.customer.name)) {
                point = element;
            }
        }
        View view = getLayoutInflater().from(NearByCustomersMapActivity.this).inflate(R.layout.map_bubble, null);
        TextView name = (TextView) view.findViewById(R.id.tv_name);
        TextView address = (TextView) view.findViewById(R.id.tv_address);
        TextView navigation = (TextView) view.findViewById(R.id.tv_navigation);
        name.setText(point.customer.name);
        address.setText(point.customer.loc.addr);
        navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {//开始导航
                Utils.goWhere(NearByCustomersMapActivity.this, point.customer.loc.loc[1], point.customer.loc.loc[0], point.customer.loc.addr);
            }
        });
        String title = marker.getTitle();
        TextView titleUi = ((TextView) view.findViewById(R.id.tv_name));
        SpannableString titleText = new SpannableString(title);
//            titleText.setSpan(new ForegroundColorSpan(Color.RED), 0,
//                    titleText.length(), 0);
//            titleUi.setTextSize(16);
        titleUi.setText(titleText);

        String snippet = marker.getSnippet();
        TextView snippetUi = ((TextView) view.findViewById(R.id.tv_address));
        SpannableString snippetText = new SpannableString(snippet);
//            snippetText.setSpan(new ForegroundColorSpan(Color.GREEN), 0,
//                    snippetText.length(), 0);
//            snippetUi.setTextSize(12);
        snippetUi.setText(snippetText);
        return view;
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
            double lat = customer.loc.loc[1];
            double lng = customer.loc.loc[0];
            aMap.addMarker(new MarkerOptions().anchor(0.8f, 0.8f).icon(customerType == 1 ? (i < isMySize ? markMy : markCompany) : markTeam)
                    .position(new LatLng(lat, lng)).title(customer.name)
                    .snippet(customer.loc.addr).draggable(true));
            OverlayItem item = new OverlayItem();
            item.latLng = new LatLng(lat, lng);
            item.customer = customer;
            points.add(item);
        }
        LogUtil.d("缩放基本：vdf" + customers.size());
        return points;
    }

    private class OverlayItem {
        LatLng latLng;
        Customer customer;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapview.onResume();
        showCustomers();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapview.onPause();
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        mapview.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapview.onDestroy();
        aMap.clear();
    }
}
