package com.loyo.oa.v2.activityui.commonview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.commonview.adapter.MapModifyViewAdapter;
import com.loyo.oa.v2.activityui.commonview.bean.PositionResultItem;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.RecycleViewDivider;
import com.loyo.oa.v2.common.RecyclerItemClickListener;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.LocationUtilGD;
import com.loyo.oa.v2.tool.LogUtil;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.loyo.oa.v2.tool.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 【地图微调】公用页面
 * Created by yyy on 16/7/20.
 */
public class MapModifyView extends BaseActivity
        implements AMap.OnCameraChangeListener, OnPoiSearchListener, GeocodeSearch.OnGeocodeSearchListener, View.OnClickListener {

    /**
     * 搜索结果，返回CODE
     */
    public static final int SERACH_MAP = 0x02;

    private String cityCode;               //当前城市编码 默认成都
    private String reGeoAddress;           //反GEO地址
    private double laPosition = 30.659628;//默认经纬度
    private double loPosition = 104.065606;
    private int currentPage = 0;    // 当前页面，从0开始计数
    private int selectPosition = -1;
    private float mZoom = 17;       //缩放级别
    private boolean isTouch = false;
    private boolean isFrist;
    private boolean isResutl;

    private ImageView img_back;
    private TextView tv_add;
    private TextView title;
    private LinearLayout serachBtn;
    private LinearLayout locationBtn;

    private UiSettings mUiSettings;
    private MapView mapView;
    private AMap aMap;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private MapModifyViewAdapter adapter;
    private GeocodeSearch geocoderSearch;
    private LocationUtilGD locationGd;
    private LatLonPoint mLatLonPoint;
    private PoiResult poiResult;    // poi返回的结果
    private PoiSearch.Query query;  // Poi查询条件类
    private PoiSearch poiSearch;    // POI搜索
    private PoiItem resultPoiItem;
    private LatLng mTarget;
    private Intent mIntent;
    private Bundle mBundle;
    private ArrayList<PositionResultItem> resultItems = new ArrayList<>();
    private PositionResultItem headerItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapmodifyview);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        initUI();
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

    /**
     * 初始化AMap对象
     */
    private void initUI() {
        isFrist = true;
        mRecyclerView = (RecyclerView) findViewById(R.id.mapview_recyclerview);
        img_back = (ImageView) findViewById(R.id.img_back);
        tv_add = (TextView) findViewById(R.id.tv_add);
        title = (TextView) findViewById(R.id.tv_title);
        serachBtn = (LinearLayout) findViewById(R.id.mapview_serach);
        locationBtn = (LinearLayout) findViewById(R.id.mapview_position_this);
        tv_add.setText("确定");
        title.setText("定位");

        if (aMap == null) {
            aMap = mapView.getMap();
            mUiSettings = aMap.getUiSettings();
        }
        aMap.setOnCameraChangeListener(this);

        if(app.latitude != -1 || app.longitude != -1){
            laPosition = app.latitude;
            loPosition = app.longitude;

            headerItem = new PositionResultItem();
            headerItem.laPosition = laPosition;
            headerItem.loPosition = loPosition;
            headerItem.address = app.address;
            headerItem.message = app.address;
            cityCode = app.cityCode;

        }

        locationMapCenter(laPosition, loPosition);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new RecycleViewDivider(mContext, LinearLayoutManager.HORIZONTAL));
        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
        serachBtn.setOnClickListener(this);
        locationBtn.setOnClickListener(this);
        img_back.setOnClickListener(this);
        tv_add.setOnClickListener(this);

        locationBtn.setOnTouchListener(Global.GetTouch());

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                isTouch = true;
                adapter.selectPositioni(position);
                selectPosition = position;
                adapter.notifyDataSetChanged();
                mLatLonPoint = new LatLonPoint(resultItems.get(position).laPosition,resultItems.get(position).loPosition);
                locationMapCenter(mLatLonPoint.getLatitude(),mLatLonPoint.getLongitude());
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }

    /**
     * 定位显示到地图中心
     * */
    public void locationMapCenter(double la,double lo){
        mTarget = new LatLng(la, lo);
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mTarget, mZoom));
    }

    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery(String address) {
        currentPage = 0;
        query = new PoiSearch.Query(address, "", cityCode);// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(15);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页
        query.setCityLimit(true);

        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    /**
     * 移动地图，监听回调
     */
    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        float mZoom = aMap.getCameraPosition().zoom;

        if(this.mZoom != mZoom){
            this.mZoom = mZoom;
            return;
        }

        if (!isTouch) {
            mTarget = cameraPosition.target;
            mLatLonPoint = new LatLonPoint(mTarget.latitude, mTarget.longitude);
            if(laPosition == mTarget.latitude){
                return;
            }
            if (Utils.isNetworkAvailable(this)) {
                showLoading("");
                getAddress(mLatLonPoint);
            } else {
                Toast("请检查您的网络连接");
            }
        }else{
            isTouch = false;
        }
    }

    /**
     * Poi搜索检索回调
     */
    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        cancelLoading();
        if (rCode == 1000) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    poiResult = result;
                    resultItems.clear();

                    PositionResultItem posi;
                    for(PoiItem poiItem : poiResult.getPois()){
                        posi = new PositionResultItem();
                        posi.address = poiItem.getTitle();
                        posi.message = poiItem.getSnippet();
                        posi.laPosition = poiItem.getLatLonPoint().getLatitude();
                        posi.loPosition = poiItem.getLatLonPoint().getLongitude();
                        resultItems.add(posi);
                    }

                    if(isFrist){
                        isFrist = false;
                        resultItems.add(headerItem);
                    }

                    if(isResutl){
                        isResutl = false;
                        posi = new PositionResultItem();
                        posi.laPosition = resultPoiItem.getLatLonPoint().getLatitude();
                        posi.loPosition = resultPoiItem.getLatLonPoint().getLongitude();
                        posi.address    = resultPoiItem.getTitle();
                        posi.message    = resultPoiItem.getSnippet();
                        resultItems.add(posi);
                    }

                    if (resultItems != null && resultItems.size() > 0) {
                        if (null == adapter) {
                            Collections.reverse(resultItems);
                            selectPosition = 0;
                            adapter = new MapModifyViewAdapter(resultItems, this);
                            mRecyclerView.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                        LogUtil.dee("resutl:" + MainApp.gson.toJson(resultItems));
                    } else {
                        Toast("没有查询到相关信息！");
                    }
                }
            } else {
                Toast("没有查询到相关信息！");
            }
        } else {
            Toast("请检查您的网络连接！");
        }
    }

    /**
     * 反Geo操作
     */
    public void getAddress(final LatLonPoint latLonPoint) {
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 300,
                GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {
        cancelLoading();
    }


    /**
     * 反Geo回调
     */
    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        if (rCode == 1000) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                reGeoAddress = result.getRegeocodeAddress().getFormatAddress();
                doSearchQuery(reGeoAddress);
            } else {
                cancelLoading();
                Toast("没有数据！");
            }
        } else {
            cancelLoading();
            Toast("请检查您的网络连接！");
        }
    }


    /**
     * Geo回调
     */
    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //进入搜索View
            case R.id.mapview_serach:
                MainApp.getMainApp().startActivityForResult(this, MapModifySerachView.class, MainApp.ENTER_TYPE_RIGHT, 0x01, null);
                break;

            //确定
            case R.id.tv_add:

                if(selectPosition == -1){
                    Toast("请选择地点");
                }else{
                    mIntent = new Intent();
                    mBundle = new Bundle();
                    mBundle.putSerializable("data", resultItems.get(selectPosition));
                    mIntent.putExtras(mBundle);
                    app.finishActivity(this,MainApp.ENTER_TYPE_RIGHT,SERACH_MAP,mIntent);
                }

                break;

            //返回
            case R.id.img_back:
                finish();
                break;

            //当前位置
            case R.id.mapview_position_this:
                isFrist = true;
                startLocation();
                break;
        }
    }

    /**
     * 启动定位
     * */
    public void startLocation(){
        locationGd = new LocationUtilGD(this, new LocationUtilGD.AfterLocation() {
            @Override
            public void OnLocationGDSucessed(final String address, final double longitude, final double latitude, final String radius) {
                locationMapCenter(app.latitude,app.longitude);
                mLatLonPoint = new LatLonPoint(app.latitude, app.longitude);
                getAddress(mLatLonPoint);
                LocationUtilGD.sotpLocation();
            }

            @Override
            public void OnLocationGDFailed() {
                Toast("定位失败,请在网络和GPS信号良好时重试");
                LocationUtilGD.sotpLocation();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (null == data) {
            return;
        }

        if (resultCode == SERACH_MAP) {
            showLoading("");
            isResutl = true;
            selectPosition = 0;
            resultPoiItem = data.getParcelableExtra("data");
            locationMapCenter(resultPoiItem.getLatLonPoint().getLatitude(), resultPoiItem.getLatLonPoint().getLongitude());
            doSearchQuery(resultPoiItem.getTitle());
        }
    }
}
