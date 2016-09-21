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
import com.amap.api.maps2d.LocationSource;
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
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.commonview.adapter.MapModifyViewAdapter;
import com.loyo.oa.v2.activityui.commonview.bean.PositionResultItem;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.RecycleViewDivider;
import com.loyo.oa.v2.common.RecyclerItemClickListener;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.LocationUtilGD;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.UMengTools;
import com.loyo.oa.v2.tool.Utils;

import java.util.ArrayList;
import java.util.Collections;


/**
 * 【地图微调】公用页面
 * Created by yyy on 16/7/20.
 */
public class MapModifyView extends BaseActivity
        implements AMap.OnCameraChangeListener, OnPoiSearchListener, GeocodeSearch.OnGeocodeSearchListener, View.OnClickListener, LocationSource {

    /**
     * 搜索结果，返回CODE
     */
    public static final int SERACH_MAP = 0x02;

    /**
     * 来自拜访
     */
    public static final int SIGNIN_PAGE = 76;

    /**
     * 来自客户新建
     */
    public static final int CUSTOMER_PAGE = 77;

    /**
     * 来自客户详情
     */
    public static final int CUSTOMER_DETAILS_PAGE = 78;


    private String reGeoAddress;       //反GEO地址
    private int currentPage = 0;       //当前页面，从0开始计数
    private int selectPosition = -1;
    private int fromPage;              //客户和拜访区别
    private float mZoom = 17;          //缩放级别
    private boolean isTouch = true;
    private boolean isFrist = true;
    private boolean isResutl;

    private ImageView img_back;
    private TextView tv_add;
    private TextView title;
    private TextView mapview_showmessage;
    private LinearLayout serachBtn;

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
    private double[] locCustomer;
    private String defaultAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapmodifyview);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState); //此方法必须重写
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
        mIntent = getIntent();
        if (null != mIntent) {
            fromPage = mIntent.getIntExtra("page", CUSTOMER_PAGE);
            locCustomer = mIntent.getDoubleArrayExtra("loc");
            defaultAddress = mIntent.getStringExtra("address");
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.mapview_recyclerview);
        img_back = (ImageView) findViewById(R.id.img_back);
        tv_add = (TextView) findViewById(R.id.tv_add);
        title = (TextView) findViewById(R.id.tv_title);
        mapview_showmessage = (TextView) findViewById(R.id.mapview_showmessage);
        serachBtn = (LinearLayout) findViewById(R.id.mapview_serach);
        tv_add.setText("确定");
        title.setText("定位");

        if (aMap == null) {
            aMap = mapView.getMap();
            mUiSettings = aMap.getUiSettings();
        }
        aMap.setOnCameraChangeListener(this);

        switch (fromPage) {

            //来自客户拜访
            case SIGNIN_PAGE:
                mapview_showmessage.setVisibility(View.VISIBLE);
                mUiSettings.setZoomControlsEnabled(false);
                mUiSettings.setZoomGesturesEnabled(false);
                mUiSettings.setScrollGesturesEnabled(false);
                locationInit();
                break;

            //来自客户新建
            case CUSTOMER_PAGE:
                locationInit();
                break;

            //来自客户详情
            case CUSTOMER_DETAILS_PAGE:
                if (null != locCustomer && locCustomer.length != 0) {
                    setHeaderItem(locCustomer[1], locCustomer[0], defaultAddress, defaultAddress);
                    locationMapCenter(locCustomer[1], locCustomer[0]);
                    doSearchQuery(defaultAddress, locCustomer[1], locCustomer[0]);
                } else {
                    startLocation();
                }
                break;
        }

        aMap.setLocationSource(this);     //设置定位监听
        mUiSettings.setMyLocationButtonEnabled(true); //是否显示默认的定位按钮
        aMap.setMyLocationEnabled(true);  //是否可触发定位并显示定位层

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new RecycleViewDivider(mContext, LinearLayoutManager.HORIZONTAL));
        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
        serachBtn.setOnClickListener(this);
        img_back.setOnClickListener(this);
        tv_add.setOnClickListener(this);

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                isTouch = true;
                adapter.selectPositioni(position);
                selectPosition = position;
                adapter.notifyDataSetChanged();
                mLatLonPoint = new LatLonPoint(resultItems.get(position).laPosition, resultItems.get(position).loPosition);
                locationMapCenter(mLatLonPoint.getLatitude(), mLatLonPoint.getLongitude());
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }

    /*如果缓存没有定位到信息，该页面再定位一次*/
    public void locationInit() {
        if (app.latitude != -1 || app.longitude != -1) {
            setHeaderItem(app.latitude, app.longitude, app.address, app.message);
            locationMapCenter(app.latitude, app.longitude);
            doSearchQuery(app.address, app.latitude, app.longitude);
        } else {
            startLocation();
        }
    }

    /**
     * 组装头部数据
     */
    public void setHeaderItem(double la, double lo, String adr, String msg) {
        headerItem = new PositionResultItem();
        headerItem.laPosition = la;
        headerItem.loPosition = lo;
        headerItem.address = adr;
        headerItem.message = msg;
    }

    /**
     * 定位显示到地图中心
     */
    public void locationMapCenter(double la, double lo) {
        mTarget = new LatLng(la, lo);
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mTarget, mZoom));
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

        if (this.mZoom != mZoom) {
            this.mZoom = mZoom;
            return;
        }

        if (!isTouch) {
            mTarget = cameraPosition.target;
            mLatLonPoint = new LatLonPoint(mTarget.latitude, mTarget.longitude);
            if (Utils.isNetworkAvailable(this)) {
                showLoading("");
                getAddress(mLatLonPoint);
            } else {
                Toast("请检查您的网络连接");
            }
        } else {
            isTouch = false;
        }
    }

    /**之前详细地址搜索现在该问地区搜索
     * 开始进行poi搜
     */
    protected void doSearchQuery(String address, double lat, double lon) {
        currentPage = 0;
        query = new PoiSearch.Query(address, "", app.cityCode);// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(150);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页
        query.setCityLimit(true);

        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
        poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(lat, lon), 300, true));
    }


    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

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
                    for (PoiItem poiItem : poiResult.getPois()) {
                        posi = new PositionResultItem();
                        posi.address = poiItem.getTitle();
                        posi.message = poiItem.getSnippet();
                        posi.laPosition = poiItem.getLatLonPoint().getLatitude();
                        posi.loPosition = poiItem.getLatLonPoint().getLongitude();
                        resultItems.add(posi);
                    }

                    /*首次进入地图，添加头部数据*/
                    if (isFrist) {
                        isFrist = false;
                        resultItems.add(headerItem);
                    }

                    /*搜索结果返回，数据组装*/
                    if (isResutl) {
                        isResutl = false;
                        for (PositionResultItem positionResultItem : resultItems) {
                            if (resultPoiItem.getTitle().contains(positionResultItem.address)) {
                                resultItems.remove(positionResultItem);
                                break;
                            }
                        }
                        posi = new PositionResultItem();
                        posi.laPosition = resultPoiItem.getLatLonPoint().getLatitude();
                        posi.loPosition = resultPoiItem.getLatLonPoint().getLongitude();
                        posi.address = resultPoiItem.getTitle();
                        posi.message = resultPoiItem.getSnippet();
                        resultItems.add(posi);
                    }

                    if (resultItems != null && resultItems.size() > 0) {
                        Collections.reverse(resultItems);
                        selectPosition = 0;
                        if (null == adapter) {
                            adapter = new MapModifyViewAdapter(resultItems, this);
                            mRecyclerView.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                        adapter.selectPositioni(selectPosition);
                        //LogUtil.dee("resutl:" + MainApp.gson.toJson(resultItems));
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

    /**
     * 反Geo回调
     */
    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        if (rCode == 1000) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                reGeoAddress = result.getRegeocodeAddress().getDistrict();
                LatLonPoint latLonPoint = result.getRegeocodeQuery().getPoint();
                doSearchQuery(reGeoAddress, latLonPoint.getLatitude(), latLonPoint.getLongitude());
                LogUtil.d("微调地图中心地址:" + reGeoAddress);
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
                mBundle = new Bundle();
                mBundle.putInt("page", fromPage);
                MainApp.getMainApp().startActivityForResult(this, MapModifySerachView.class, MainApp.ENTER_TYPE_RIGHT, 0x01, mBundle);
                break;

            //确定
            case R.id.tv_add:

                if (selectPosition == -1) {
                    Toast("请选择地点");
                } else {
                    if (resultItems.size() != 0) {
                        app.latitude = resultItems.get(selectPosition).laPosition;
                        app.longitude = resultItems.get(selectPosition).loPosition;
                        app.address = resultItems.get(selectPosition).address;
                        app.message = resultItems.get(selectPosition).message;

                        mIntent = new Intent();
                        mBundle = new Bundle();
                        mBundle.putSerializable("data", resultItems.get(selectPosition));
                        mIntent.putExtras(mBundle);
                        app.finishActivity(this, MainApp.ENTER_TYPE_RIGHT, -1, mIntent);
                    } else {
                        Toast("无处理结果,请稍后");
                    }
                }

                break;

            //返回
            case R.id.img_back:
                finish();
                break;

        }
    }

    /**
     * 启动定位
     */
    public void startLocation() {
        locationGd = new LocationUtilGD(this, new LocationUtilGD.AfterLocation() {
            @Override
            public void OnLocationGDSucessed(final String address, final double longitude, final double latitude, final String radius) {
                UMengTools.sendLocationInfo(address, longitude, latitude);
                isTouch = true;
                isFrist = true;
                setHeaderItem(latitude, longitude, address, radius);
                locationMapCenter(latitude, longitude);
                doSearchQuery(app.address, latitude, longitude);
                LocationUtilGD.sotpLocation();
            }

            @Override
            public void OnLocationGDFailed() {
                Toast("定位失败,请在网络和GPS信号良好时重试");
                LocationUtilGD.sotpLocation();
            }
        });
    }

    /**
     * 定位按钮监听
     */
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        if (!isFrist) {
            startLocation();
        }
    }

    /**
     * 取消定位监听
     */
    @Override
    public void deactivate() {
        LocationUtilGD.sotpLocation();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (null == data) {
            return;
        }

        if (resultCode == SERACH_MAP) {
            isResutl = true;
            selectPosition = 0;
            resultPoiItem = data.getParcelableExtra("data");
            locationMapCenter(resultPoiItem.getLatLonPoint().getLatitude(), resultPoiItem.getLatLonPoint().getLongitude());
        }
    }
}
