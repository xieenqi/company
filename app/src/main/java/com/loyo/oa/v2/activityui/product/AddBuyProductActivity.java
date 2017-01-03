package com.loyo.oa.v2.activityui.product;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.customer.model.ContactLeftExtras;
import com.loyo.oa.v2.activityui.customer.model.ExtraData;
import com.loyo.oa.v2.activityui.customer.model.ExtraProperties;
import com.loyo.oa.v2.activityui.other.PreviewImageListActivity;
import com.loyo.oa.v2.activityui.product.adapter.ProductPicAdapter;
import com.loyo.oa.v2.activityui.product.event.SelectProductEvent;
import com.loyo.oa.v2.activityui.product.model.ProductDetails;
import com.loyo.oa.v2.activityui.product.model.ProductDynmModel;
import com.loyo.oa.v2.activityui.product.persenter.AddBuProductPersenter;
import com.loyo.oa.v2.activityui.product.persenter.impl.AddBuProductPersenterImpl;
import com.loyo.oa.v2.activityui.product.view.AddProductExtraData;
import com.loyo.oa.v2.activityui.product.viewcontrol.AddBuProductView;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customview.CustomerInfoExtraData;
import com.loyo.oa.v2.network.model.BaseResponse;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.LogUtil;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class AddBuyProductActivity extends BaseActivity implements AddBuProductView,View.OnClickListener {


    private TextView tvTitle;
    private ImageView ivSubmit;
    private LinearLayout llMoreInfoBtn, llMoreInfoShow;
    private ImageView ivMore;
    private GridView gridViewPic;
    private EditText etBuyNum;
    private LinearLayout llDefinedHolder;
    private LinearLayout selectProduct;

    private ArrayList<ExtraData> extDatas;   //动态字段总汇
    private AddBuProductPersenter mPersenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_buy_product);
        initView();
    }

    private void initView() {

        mPersenter = new AddBuProductPersenterImpl(this);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        ivSubmit = (ImageView) findViewById(R.id.iv_submit);
        llMoreInfoBtn = (LinearLayout) findViewById(R.id.add_buy_product_ll_4);
        llMoreInfoShow = (LinearLayout) findViewById(R.id.add_buy_product_more_ll);
        ivMore = (ImageView) findViewById(R.id.add_buy_product_iv_1);
        gridViewPic = (GridView) findViewById(R.id.add_buy_product_more_grid_1);
        etBuyNum = (EditText) findViewById(R.id.add_buy_product_et_2);
        llDefinedHolder = (LinearLayout) findViewById(R.id.add_buy_product_more_definde);
        selectProduct   = (LinearLayout) findViewById(R.id.add_buy_product_ll_1);
        selectProduct.setOnClickListener(this);


        mPersenter.getProductDynm();

        etBuyNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s + "")) {
                    int num = Integer.parseInt(s + "");
                    if (num > 10) {
                        Toast("库存不足");
                        etBuyNum.setText("10");
                    }
                }
            }
        });

        final List<Attachment> data = new ArrayList<>();
        ProductPicAdapter picAdapter = new ProductPicAdapter(this, data);

        /*图片预览*/
        gridViewPic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", data.get(position));
                bundle.putInt("position", position);
                bundle.putBoolean("isEdit", false);
                MainApp.getMainApp().startActivityForResult((Activity) mContext, PreviewImageListActivity.class,
                        MainApp.ENTER_TYPE_BUTTOM, FinalVariables.REQUEST_DEAL_ATTACHMENT, bundle);
            }
        });


        gridViewPic.setAdapter(picAdapter);

        //查看产品详细的点击事件
        llMoreInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (llMoreInfoShow.getVisibility() == View.VISIBLE) {
                    llMoreInfoShow.setVisibility(View.GONE);
                    ivMore.setImageResource(R.drawable.product_more);
                } else {
                    llMoreInfoShow.setVisibility(View.VISIBLE);
                    ivMore.setImageResource(R.drawable.product_less);
                }
            }
        });

        //设置头部标题图标
        tvTitle.setText("新增购买产品");
        ivSubmit.setVisibility(View.VISIBLE);
        ivSubmit.setImageResource(R.drawable.right_submit1);

        //添加点击的反馈效果
        Global.SetTouchView(ivSubmit, llMoreInfoBtn);
        //addDefined();
    }

    //模拟添加自定义字段
    private void addDefined() {
        for (int i = 0; i < 5; i++) {
            View view = getLayoutInflater().inflate(R.layout.item_product_defined, null);
            TextView tvTempTitle = (TextView) view.findViewById(R.id.add_product_defined_tv_1);
            TextView tvTempText = (TextView) view.findViewById(R.id.add_product_defined_tv_2);
            tvTempTitle.setText("标题：" + i);
            tvTempText.setText("content:" + i);
            llDefinedHolder.addView(view);

        }
    }

    // 获取动态字段成功
    @Override
    public void getDynmSuccessEmbl(ArrayList<ProductDynmModel> model) {
        extDatas = new ArrayList<>();
        ExtraData extraData;
        ExtraProperties properties;
        for (ProductDynmModel productDynmModel : model) {
            extraData = new ExtraData();
            properties = new ExtraProperties();
            if (productDynmModel.enabled) {
                properties.setEnabled(productDynmModel.enabled);
                properties.setRequired(productDynmModel.required);
                properties.setLabel(productDynmModel.label);
                properties.setType(productDynmModel.type);
                properties.setIsList(productDynmModel.isList);
                properties.setDefVal(productDynmModel.defVal);
                properties.setName(productDynmModel.name);
                extraData.setProperties(properties);
                extDatas.add(extraData);
            }
        }
        llDefinedHolder.addView(new AddProductExtraData(mContext, extDatas, true, R.color.text33, 0));
    }

    // 获取动态字段失败
    @Override
    public void getDynmErrorEmbl() {

    }

    // 获取产品详情成功
    @Override
    public void getDetailsSuccessEmbl(ProductDetails details) {

    }

    // 获取产品详情失败
    @Override
    public void getDetailsErrorEmbl() {

    }

    // 选择的产品 回调查询详情
    @Subscribe
    public void selectProductCallBack(SelectProductEvent event){
        Bundle mBundle = event.bundle;
        mPersenter.getProductDetails(mBundle.getString("id"));
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            //选择产品
            case R.id.add_buy_product_ll_1:
                Intent mIntent = new Intent(AddBuyProductActivity.this,SelectProductActivity.class);
                startActivity(mIntent);
                break;

        }
    }
}