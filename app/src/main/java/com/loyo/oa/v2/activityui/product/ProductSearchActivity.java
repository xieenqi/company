package com.loyo.oa.v2.activityui.product;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.product.adapter.SelectProductAdapter;
import com.loyo.oa.v2.activityui.product.api.ProductService;
import com.loyo.oa.v2.activityui.product.event.SelectProductEvent;
import com.loyo.oa.v2.activityui.product.model.ProductListModel;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.event.AppBus;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.tool.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * 新增购买产品－产品选择－搜索产品
 */
public class ProductSearchActivity extends BaseActivity {

    private EditText edt_search;
    private ImageView iv_clean;
    private LoadingLayout ll_loading;
    private ListView listview;

    private SelectProductAdapter mAdapter;
    public  ProductListModel models;
    public  ArrayList<ProductListModel.ProductList> products;
    public boolean stockEnabled;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_search);
        initView();
    }

    private void initView(){
        edt_search = (EditText) findViewById(R.id.edt_search);
        listview = (ListView) findViewById(R.id.listview);
        ll_loading = (LoadingLayout) findViewById(R.id.ll_loading);

        //返回按钮
        findViewById(R.id.img_title_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //清空按钮
        iv_clean = (ImageView) findViewById(R.id.iv_clean);
        iv_clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_search.setText("");
            }
        });

        //搜索文本监听
        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                getProductList(s.toString());
            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(products.get(position).stock == 0){
                    Toast("库存不足");
                }else{
                    SelectProductEvent event = new SelectProductEvent();
                    event.bundle = new Bundle();
                    event.bundle.putString("id",products.get(position).id);
                    AppBus.getInstance().post(event);
                    finish();
                }
            }
        });
    }

    // adapter绑定
    void bindAdapter(){
        if(null == mAdapter){
            mAdapter = new SelectProductAdapter(this,products,stockEnabled);
            listview.setAdapter(mAdapter);
        }else{
            mAdapter.notifyDataSetChanged();
        }
    }

    // 获取产品列表
    void getProductList(String words){
        HashMap<String, Object> map = new HashMap<>();
        map.put("keyWords",words);
        map.put("pageIndex",1);
        map.put("pageSize",100);
        ProductService.getProductList(map).subscribe(new DefaultLoyoSubscriber<ProductListModel>() {
            @Override
            public void onNext(ProductListModel productDynmModel) {
                products = productDynmModel.records.products;
                stockEnabled = productDynmModel.records.stockEnabled;
                if(products.size() == 0){
                    ll_loading.setStatus(LoadingLayout.Empty);
                }else{
                    ll_loading.setStatus(LoadingLayout.Success);
                    models = productDynmModel;
                    bindAdapter();
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }
}