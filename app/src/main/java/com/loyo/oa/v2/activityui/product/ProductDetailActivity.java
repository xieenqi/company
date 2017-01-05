package com.loyo.oa.v2.activityui.product;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.model.ExtraData;
import com.loyo.oa.v2.activityui.other.PreviewImageListActivity;
import com.loyo.oa.v2.activityui.product.adapter.ProductPicAdapter;
import com.loyo.oa.v2.activityui.product.api.ProductService;
import com.loyo.oa.v2.activityui.product.model.ProductDetails;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.tool.BaseActivity;

public class ProductDetailActivity extends BaseActivity implements View.OnClickListener{


    private TextView tvTitle;
    private GridView gridViewPic;
    private LinearLayout llDefinedHolder;
    private LinearLayout ll_back;
    private LoadingLayout ll_loading;
    private ProductDetails detailsModel;
    private String id;

    private TextView prdName; //选择产品
    private TextView prdPrice; //产品原价
    private TextView prdFk;   //产品单位
    private TextView prdSize; //产品库存
    private TextView prdKind; //产品分类
    private TextView memo;    //备注

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        initView();
    }

    private void initView(){
        id = getIntent().getStringExtra("id");
        if(TextUtils.isEmpty(id)){
            Toast("参数异常");
            finish();
        }

        ll_loading = (LoadingLayout) findViewById(R.id.ll_loading);
        tvTitle= (TextView) findViewById(R.id.tv_title);
        prdName = (TextView) findViewById(R.id.product_detail_tv_2);
        prdPrice = (TextView) findViewById(R.id.product_detail_tv_4);
        prdSize = (TextView) findViewById(R.id.product_detail_tv_6);
        prdKind = (TextView) findViewById(R.id.product_detail_more_tv_2);
        prdFk = (TextView) findViewById(R.id.product_detail_tv_7);
        memo = (TextView) findViewById(R.id.product_detail_more_tv_6);
        ll_back = (LinearLayout) findViewById(R.id.ll_back);

        llDefinedHolder= (LinearLayout) findViewById(R.id.product_detail_more_defined);
        gridViewPic= (GridView) findViewById(R.id.product_detail_more_grid_1);

        ll_loading.setStatus(com.library.module.widget.loading.LoadingLayout.Loading);
        ll_loading.setOnReloadListener(new com.library.module.widget.loading.LoadingLayout.OnReloadListener() {
            @Override
            public void onReload(View v) {
                ll_loading.setStatus(com.library.module.widget.loading.LoadingLayout.Loading);
                getProductDetails();
            }
        });

        ll_back.setOnClickListener(this);

        tvTitle.setText("产品详细");
        getProductDetails();
    }

    /**
     * 数据绑定
     * */
    private void bindData(){

        prdName.setText(detailsModel.name);
        prdPrice.setText(detailsModel.unitPrice+"");
        prdSize.setText(detailsModel.getStock()+"");
        prdKind.setText(detailsModel.category);
        prdFk.setText(detailsModel.unit);
        memo.setText(detailsModel.memo);

        ProductPicAdapter picAdapter=new ProductPicAdapter(this,detailsModel.attachment);
        gridViewPic.setAdapter(picAdapter);

        addDefined();
       /*图片预览*/
        gridViewPic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", detailsModel.attachment);
                bundle.putInt("position", position);
                bundle.putBoolean("isEdit", false);
                MainApp.getMainApp().startActivityForResult((Activity) mContext, PreviewImageListActivity.class,
                        MainApp.ENTER_TYPE_BUTTOM, FinalVariables.REQUEST_DEAL_ATTACHMENT, bundle);
            }
        });
    }

    /**
     * 获取产品详情
     * */
    private void getProductDetails(){
        ProductService.getProductDetails(id).subscribe(new DefaultLoyoSubscriber<ProductDetails>() {
            @Override
            public void onNext(ProductDetails details) {
                detailsModel = details;
                ll_loading.setStatus(LoadingLayout.Success);
                bindData();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }

    //模拟添加自定义字段
    private void addDefined() {
        for(ExtraData extraData : detailsModel.extDatas) {
            View view = getLayoutInflater().inflate(R.layout.item_product_defined, null);
            TextView tvTempTitle = (TextView) view.findViewById(R.id.add_product_defined_tv_1);
            TextView tvTempText = (TextView) view.findViewById(R.id.add_product_defined_tv_2);
            tvTempTitle.setText(extraData.getProperties().getLabel());
            tvTempText.setText(extraData.getVal());
            llDefinedHolder.addView(view);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.ll_back:
                onBackPressed();
                break;

        }
    }
}