package com.loyo.oa.v2.activityui.commonview;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.tool.BaseActivity;

/**
 * 【单地图显示】
 * Created by yyy on 16/7/26.
 */
public class MapSingleView extends BaseActivity implements View.OnClickListener{

    private UiSettings mUiSettings;
    private MapView mapView;
    private AMap aMap;
    private LatLng latlng;
    private double laPosition,loPosition;
    private String address;
    private Intent mIntent;
    private Marker mMarker;
    private TextView title;
    private ImageView img_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapsingleview);
        initUI();
        mapView.onCreate(savedInstanceState);// 此方法必须重写
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    public void initUI(){
        mIntent = getIntent();
        if(null != mIntent){
            laPosition = mIntent.getDoubleExtra("la", 0);
            loPosition = mIntent.getDoubleExtra("lo", 0);
            address    = mIntent.getStringExtra("address");
        }
        mapView = (MapView) findViewById(R.id.mapView);
        latlng = new LatLng(laPosition,loPosition);
        if (aMap == null) {
            aMap = mapView.getMap();
            mUiSettings = aMap.getUiSettings();
        }
        mUiSettings.setZoomControlsEnabled(false);
        img_back = (ImageView) findViewById(R.id.img_back);
        title = (TextView) findViewById(R.id.tv_title);
        title.setText("定位地址");
        img_back.setOnClickListener(this);

        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 17));
        drawMarkers();
    }

    /**
     * 设置标记
     * */
    public void drawMarkers() {

        mMarker = aMap.addMarker(new MarkerOptions()
                .position(latlng)
                .icon(BitmapDescriptorFactory.defaultMarker())
                .title("定位地址:")
                .snippet(address)
                .draggable(true));
        mMarker.showInfoWindow();//内容默认显示出来
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_back:
                onBackPressed();
                break;
        }
    }
}
