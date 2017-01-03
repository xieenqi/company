package com.loyo.oa.v2.activityui.product;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.product.adapter.SelectProductAdapter;
import com.loyo.oa.v2.activityui.product.api.ProductService;
import com.loyo.oa.v2.activityui.product.event.SelectProductEvent;
import com.loyo.oa.v2.activityui.product.model.ProductDynmModel;
import com.loyo.oa.v2.activityui.product.model.ProductListModel;
import com.loyo.oa.v2.activityui.product.view.SelectProductMenu;
import com.loyo.oa.v2.activityui.product.viewcontrol.SelectProMenuView;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.event.AppBus;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.LogUtil;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 选择产品
 * Created by yyy on 16/12/21.
 */

public class SelectProductActivity extends BaseActivity implements View.OnClickListener,SelectProMenuView {

    private ImageView img_back;
    private TextView  tv_title,tv_add;
    private LinearLayout ll_search;
    private ListView lv_listview;
    private SelectProductMenu productMenu;
    private SelectProductAdapter mAdapter;
    private LoadingLayout ll_loading;

    private PaginationX<ProductListModel> models;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_product);
        initUI();
    }

    void bindAdapter(){
        if(null == mAdapter){
            mAdapter = new SelectProductAdapter(this,models.getRecords());
            lv_listview.setAdapter(mAdapter);
        }else{
            mAdapter.notifyDataSetChanged();
        }
    }

    void initUI() {
        ll_search = (LinearLayout) findViewById(R.id.ll_search);
        img_back = (ImageView) findViewById(R.id.img_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_add = (TextView) findViewById(R.id.tv_add);
        lv_listview = (ListView) findViewById(R.id.lv_listview);
        ll_loading = (LoadingLayout) findViewById(R.id.ll_loading);
        tv_title.setText("选择产品");
        tv_add.setText("分类");
        productMenu = new SelectProductMenu(this, this);

        img_back.setOnClickListener(this);
        ll_search.setOnClickListener(this);
        tv_add.setOnClickListener(this);
        Global.SetTouchView(img_back,tv_add,ll_search);

        ll_loading.setStatus(LoadingLayout.Loading);
        ll_loading.setOnReloadListener(new LoadingLayout.OnReloadListener() {
            @Override
            public void onReload(View v) {
                ll_loading.setStatus(LoadingLayout.Loading);
                getProductList();
            }
        });

        lv_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SelectProductEvent event = new SelectProductEvent();
                event.bundle = new Bundle();
                event.bundle.putString("id",models.getRecords().get(position).id);
                AppBus.getInstance().post(event);
                finish();
            }
        });

        getProductList();
    }

    // 获取产品列表
    void getProductList(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("keyWords","");
        map.put("pageIndex",1);
        map.put("pageSize",100);
        ProductService.getProductList("",map).subscribe(new DefaultLoyoSubscriber<PaginationX<ProductListModel>>() {
            @Override
            public void onNext(PaginationX<ProductListModel> productDynmModel) {
                ll_loading.setStatus(LoadingLayout.Success);
                models = productDynmModel;
                bindAdapter();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){

            // 返回
            case R.id.img_back:
                onBackPressed();
                break;

            // 分类
            case R.id.tv_add:
                productMenu.showPopupWindow(v);
                break;

            // 搜索
            case R.id.ll_search:
                app.startActivity(this, ProductSearchActivity.class, MainApp.ENTER_TYPE_RIGHT, false, null);
                break;

        }
    }

    @Override
    public void popWindowShowEmbl() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.7f;
        getWindow().setAttributes(lp);
    }

    @Override
    public void popWindowDimsEmbl() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 1f;
        getWindow().setAttributes(lp);
    }

    @Subscribe
    public void selectProductCallBack(SelectProductEvent event){
        finish();
    }
}
