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

    private TextView selectText;
    private TextView prdPrice;
    private TextView prdSize;
    private TextView prdKind;
    private TextView memo;

    private LinearLayout layout_prdprice;
    private LinearLayout layout_prdsize;
    private LinearLayout layout_prdkind;
    private LinearLayout llDefinedHolder;
    private LinearLayout selectProduct;
    private LinearLayout ll_back;
    private EditText add_buy_product_et_1;
    private EditText add_buy_product_et_2;

    private ArrayList<ExtraData> extDatas;      //动态字段总汇
    private AddBuProductPersenter mPersenter;
    private ProductDetails detailsModel;

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
        selectText = (TextView) findViewById(R.id.add_buy_product_tv_2);
        prdPrice = (TextView) findViewById(R.id.add_buy_product_tv_4);
        prdSize = (TextView) findViewById(R.id.add_buy_product_tv_16);
        prdKind = (TextView) findViewById(R.id.add_buy_product_tv_18);
        memo = (TextView) findViewById(R.id.add_buy_product_more_tv_7);

        layout_prdprice = (LinearLayout) findViewById(R.id.add_buy_product_ll_2);
        layout_prdsize = (LinearLayout) findViewById(R.id.add_buy_product_ll_3);
        layout_prdkind = (LinearLayout) findViewById(R.id.add_buy_product_ll_15);
        add_buy_product_et_1 = (EditText) findViewById(R.id.add_buy_product_et_1);
        add_buy_product_et_2 = (EditText) findViewById(R.id.add_buy_product_et_2);

        llDefinedHolder = (LinearLayout) findViewById(R.id.add_buy_product_more_definde);
        selectProduct   = (LinearLayout) findViewById(R.id.add_buy_product_ll_1);
        ll_back   = (LinearLayout) findViewById(R.id.ll_back);
        selectProduct.setOnClickListener(this);
        ll_back.setOnClickListener(this);

        selectText.setText("请选择");

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
                    if (num > detailsModel.stock) {
                        Toast("库存不足");
                        etBuyNum.setText(detailsModel.stock+"");
                    }
                }
            }
        });


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
        add_buy_product_et_1.setEnabled(false);
        add_buy_product_et_2.setEnabled(false);

        //添加点击的反馈效果
        Global.SetTouchView(ivSubmit, llMoreInfoBtn);
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

    // 绑定产品数据
    private void bindData(){
        add_buy_product_et_1.setEnabled(true);
        add_buy_product_et_2.setEnabled(true);
        llMoreInfoBtn.setVisibility(View.VISIBLE);
        layout_prdprice.setVisibility(View.VISIBLE);
        layout_prdsize.setVisibility(View.VISIBLE);
        layout_prdkind.setVisibility(View.VISIBLE);

        selectText.setText(detailsModel.name);
        prdPrice.setText(detailsModel.unitPrice+"");
        prdSize.setText(detailsModel.stock+"");
        prdKind.setText(detailsModel.category);
        memo.setText(detailsModel.memo);
        addDefined();

        ProductPicAdapter picAdapter = new ProductPicAdapter(this, detailsModel.attachment);
        gridViewPic.setAdapter(picAdapter);
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
        detailsModel = details;
        bindData();
        if(!TextUtils.isEmpty(details.attachmentUUId) || null != details.attachmentUUId);
        //mPersenter.getAttachment(details.attachmentUUId);
    }

    // 获取产品详情失败
    @Override
    public void getDetailsErrorEmbl() {

    }

    // 获取附件成功
    @Override
    public void getAttachmentSuccessEmbl(ArrayList<Attachment> attachments) {
        ProductPicAdapter picAdapter = new ProductPicAdapter(this, attachments);
        gridViewPic.setAdapter(picAdapter);
    }

    // 获取附件失败
    @Override
    public void getAttachmentErrorEmbl() {

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

            //后退
            case R.id.ll_back:
                onBackPressed();
                break;

        }
    }
}