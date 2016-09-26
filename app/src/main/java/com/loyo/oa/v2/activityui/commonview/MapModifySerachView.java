package com.loyo.oa.v2.activityui.commonview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.commonview.adapter.MapModifyViewSerachAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.RecycleViewDivider;
import com.loyo.oa.v2.common.RecyclerItemClickListener;
import com.loyo.oa.v2.tool.BaseActivity;
import java.util.ArrayList;
import java.util.List;

/**
 * 【地图微调】搜索
 * Created by yyy on 16/7/21.
 */
public class MapModifySerachView extends BaseActivity implements View.OnClickListener,PoiSearch.OnPoiSearchListener {

    private RelativeLayout img_title_left;
    private EditText edt_search;
    private ImageView iv_clean;
    private RecyclerView mRecyclerView;
    private ViewStub     mViewStub;

    private PoiResult poiResult;    // poi返回的结果
    private PoiSearch.Query query;  // Poi查询条件类
    private PoiSearch poiSearch;    // POI搜索

    private int currentPage;
    private int fromPage;

    private List<PoiItem> poiItems = new ArrayList<>();
    private MapModifyViewSerachAdapter adapter;
    private LinearLayoutManager mLinearLayoutManager;

    private Intent mIntent;
    private Bundle mBundle;

    private LatLonPoint mLatLonPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapmodifyserachview);
        initUI();
    }

    private void initUI(){
        mIntent = getIntent();
        if(null != mIntent){
            fromPage = mIntent.getIntExtra("page", MapModifyView.CUSTOMER_PAGE);
        }

        img_title_left = (RelativeLayout) findViewById(R.id.img_title_left);
        edt_search     = (EditText) findViewById(R.id.edt_search);
        iv_clean       = (ImageView) findViewById(R.id.iv_clean);
        mRecyclerView  = (RecyclerView) findViewById(R.id.mapmod_serach_recycler);
        mViewStub      = (ViewStub) findViewById(R.id.vs_nodata);

        mLinearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        img_title_left.setOnClickListener(this);
        iv_clean.setOnClickListener(this);
        edt_search.setOnClickListener(this);
        edt_search.addTextChangedListener(mTextWatcher);

        mRecyclerView.addItemDecoration(new RecycleViewDivider(mContext, LinearLayoutManager.HORIZONTAL));
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mIntent = new Intent();
                mBundle = new Bundle();
                mBundle.putParcelable("data", poiItems.get(position));
                mIntent.putExtras(mBundle);
                app.finishActivity(MapModifySerachView.this,MainApp.ENTER_TYPE_RIGHT,MapModifyView.SERACH_MAP,mIntent);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }

    TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            doSearchQuery(s.toString());
        }
    };


    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery(String address) {
        currentPage = 0;
        query = new PoiSearch.Query(address,"",app.cityCode);// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(15);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页
        query.setCityLimit(true);
        mLatLonPoint = new LatLonPoint(app.latitude,app.longitude);

        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);

        //如果为拜访，则设置以mLatLonPoint为中心，300米范围内搜索
        if(fromPage == MapModifyView.SIGNIN_PAGE){
            poiSearch.setBound(new PoiSearch.SearchBound(mLatLonPoint, 300, true));
        }
        poiSearch.searchPOIAsyn();  //异步搜索
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.img_title_left:
                onBackPressed();
                break;

            case R.id.iv_clean:
                edt_search.setText("");
                break;

        }
    }

    /**
     * 搜索监听回调
     * */
    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        if (rCode == 1000) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    poiResult = result;
                    // 取得搜索到的poiitems有多少页
                    poiItems.clear();
                    poiItems.addAll(poiResult.getPois());// 取得第一页的poiitem数据，页数从数字0开始

                    if (poiItems != null && poiItems.size() > 0) {
                        mViewStub.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        if(null == adapter){
                            adapter = new MapModifyViewSerachAdapter(poiItems,this);
                            mRecyclerView.setAdapter(adapter);
                        }else{
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        mViewStub.setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(View.GONE);
                        Toast("没有查询到相关信息！");
                    }
                }
            } else {
                mViewStub.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
                Toast("没有查询到相关信息！");
            }
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }
}
