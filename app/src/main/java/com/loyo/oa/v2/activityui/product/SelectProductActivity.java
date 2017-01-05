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
import com.loyo.oa.v2.activityui.product.model.ProductListModel;
import com.loyo.oa.v2.activityui.product.view.SelectProductMenu;
import com.loyo.oa.v2.activityui.product.viewcontrol.SelectProMenuView;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.event.AppBus;
import com.loyo.oa.v2.customview.classify_seletor.ClassifySeletorItem;
import com.loyo.oa.v2.customview.classify_seletor.ClassifySeletorView;
import com.loyo.oa.v2.customview.classify_seletor.ItemAdapter;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.LogUtil;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    private ProductListModel models;
    public ArrayList<ProductListModel.ProductList> products;

    private String categoryId="";//分类id，默认加载的时候为""
    public boolean stockEnabled;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_product);
        initUI();
    }

    void bindAdapter(){
        if(null == mAdapter){
            mAdapter = new SelectProductAdapter(this,products,stockEnabled);
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
                if(stockEnabled && products.get(position).stock == 0){
                    Toast("库存不足");
                }else{
                    SelectProductEvent event = new SelectProductEvent();
                    event.bundle = new Bundle();
                    event.bundle.putString("id",products.get(position).id);
                    event.bundle.putBoolean("enable",stockEnabled);
                    AppBus.getInstance().post(event);
                    finish();
                }
            }
        });
        getProductList();
    }

    // 获取产品列表
    void getProductList(){
        HashMap<String, Object> map = new HashMap<>();
//      map.put("keyWords","");
        map.put("pageIndex",1);
        map.put("pageSize",100);
        map.put("categoryId",categoryId);
        ProductService.getProductList(map).subscribe(new DefaultLoyoSubscriber<ProductListModel>(ll_loading) {
            @Override
            public void onNext(ProductListModel productDynmModel) {
                products = productDynmModel.records.products;
                stockEnabled = productDynmModel.records.stockEnabled;
                LogUtil.dee("选择stockEnabled:"+stockEnabled);
                if(products.size() == 0){
                    ll_loading.setStatus(LoadingLayout.Empty);
                }else{
                    ll_loading.setStatus(LoadingLayout.Success);
                    if(null != mAdapter){
                        mAdapter.setModels(productDynmModel.records.products);
                    }else{
                        models=productDynmModel;
                        bindAdapter();
                    }
                   bindAdapter();
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }


    @Override
    public void onClick(final View v) {
        switch (v.getId()){
            // 返回
            case R.id.img_back:
                onBackPressed();
                break;

            // 分类
            case R.id.tv_add:
                productMenu.showPopupWindow(v, new ClassifySeletorView.SeletorListener() {
                    @Override
                    public void clickItem(boolean isSelected, ItemAdapter.ItemViewHolder holder, int position, ClassifySeletorItem item) {
                    }
                    @Override
                    public void clickReset() {
                    }
                    @Override
                    public void clickOk(List<ClassifySeletorItem> selectItem) {
                        productMenu.dismiss();
                        popWindowDimsEmbl();
                        if(selectItem.size()>0){
                            //单选，直接取下标0的数据
                            categoryId=selectItem.get(0).getId();
                        }else{
                            Toast("你没有选择产品分类,显示全部产品");
                            categoryId="";
                        }
                        getProductList();
                    }
                });
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
