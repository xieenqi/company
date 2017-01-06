package com.loyo.oa.v2.activityui.product;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.customer.model.ExtraData;
import com.loyo.oa.v2.activityui.customer.model.ExtraProperties;
import com.loyo.oa.v2.activityui.customer.model.Product;
import com.loyo.oa.v2.activityui.other.PreviewImageListActivity;
import com.loyo.oa.v2.activityui.product.adapter.ProductPicAdapter;
import com.loyo.oa.v2.activityui.product.event.SelectProductEvent;
import com.loyo.oa.v2.activityui.product.model.ProductDetails;
import com.loyo.oa.v2.activityui.product.model.ProductDynmModel;
import com.loyo.oa.v2.activityui.product.persenter.AddBuProductPersenter;
import com.loyo.oa.v2.activityui.product.persenter.impl.AddBuProductPersenterImpl;
import com.loyo.oa.v2.activityui.product.view.AddProductExtraData;
import com.loyo.oa.v2.activityui.product.viewcontrol.AddBuProductView;
import com.loyo.oa.v2.activityui.sale.bean.ActionCode;
import com.loyo.oa.v2.activityui.sale.bean.SaleIntentionalProduct;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.Utils;
import org.greenrobot.eventbus.Subscribe;
import java.util.ArrayList;
import java.util.HashMap;

public class AddBuyProductActivity extends BaseActivity implements AddBuProductView,View.OnClickListener {


    private TextView tv_title;
    private ImageView ivSubmit;
    private LinearLayout llMoreInfoBtn, llMoreInfoShow;
    private ImageView ivMore;
    private GridView gridViewPic;

    private TextView tv_product;
    private TextView tv_price;
    private TextView prdSize;
    private TextView prdKind;
    private TextView memo;
    private TextView tv_discount;
    private TextView tv_total;
    private TextView tv_unit;
    private LinearLayout layout_prdprice;
    private LinearLayout layout_prdsize;
    private LinearLayout layout_prdkind;
    private LinearLayout llDefinedHolder;
    private LinearLayout selectProduct;
    private LinearLayout ll_back;
    private LinearLayout layout_image;
    private EditText et_price;
    private EditText et_number;
    private EditText et_remake;

    private LoadingLayout ll_loading;

    private ArrayList<ExtraData> extDatas;      //动态字段总汇
    private AddBuProductPersenter mPersenter;
    private ProductDetails detailsModel;

    private ArrayList<Product> lstData_Product = new ArrayList<>();
    private AlertDialog dialog_Product;
    private String productId = "", productUnit;
    private String saleId = "";
    private String oldId = "";
    private int fromPage = 0;
    private boolean stockEnabled = true;
    private ArrayList<SaleIntentionalProduct> productListData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_buy_product);
        initView();
    }

    private void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        ivSubmit = (ImageView) findViewById(R.id.iv_submit);
        llMoreInfoBtn = (LinearLayout) findViewById(R.id.add_buy_product_ll_4);
        llMoreInfoShow = (LinearLayout) findViewById(R.id.add_buy_product_more_ll);
        layout_image = (LinearLayout) findViewById(R.id.layout_image);
        ivMore = (ImageView) findViewById(R.id.add_buy_product_iv_1);
        gridViewPic = (GridView) findViewById(R.id.add_buy_product_more_grid_1);
        et_number = (EditText) findViewById(R.id.et_number);
        tv_product = (TextView) findViewById(R.id.add_buy_product_tv_2);
        tv_price = (TextView) findViewById(R.id.tv_price);
        prdSize = (TextView) findViewById(R.id.add_buy_product_tv_16);
        prdKind = (TextView) findViewById(R.id.add_buy_product_tv_18);
        memo = (TextView) findViewById(R.id.add_buy_product_more_tv_7);
        tv_discount = (TextView) findViewById(R.id.tv_discount);
        tv_total = (TextView) findViewById(R.id.tv_total);
        et_remake = (EditText) findViewById(R.id.et_remake);
        ll_loading = (LoadingLayout) findViewById(R.id.ll_loading);

        layout_prdprice = (LinearLayout) findViewById(R.id.add_buy_product_ll_2);
        layout_prdsize = (LinearLayout) findViewById(R.id.add_buy_product_ll_3);
        layout_prdkind = (LinearLayout) findViewById(R.id.add_buy_product_ll_15);
        et_price = (EditText) findViewById(R.id.et_price);
        et_number = (EditText) findViewById(R.id.et_number);
        et_number.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        et_price.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        llDefinedHolder = (LinearLayout) findViewById(R.id.add_buy_product_more_definde);
        selectProduct   = (LinearLayout) findViewById(R.id.add_buy_product_ll_1);
        ll_back   = (LinearLayout) findViewById(R.id.ll_back);
        selectProduct.setOnClickListener(this);
        ll_back.setOnClickListener(this);
        ivSubmit.setOnClickListener(this);
        tv_unit= (TextView) findViewById(R.id.add_buy_product_tv_21);

        mPersenter = new AddBuProductPersenterImpl(this,ll_loading);
        ll_loading.setOnReloadListener(new LoadingLayout.OnReloadListener() {
            @Override
            public void onReload(View v) {
                ll_loading.setStatus(LoadingLayout.Loading);
                mPersenter.getProductDetails(productId);
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
        tv_product.setText("请选择");
        tv_title.setText("新增购买产品");
        ivSubmit.setVisibility(View.VISIBLE);
        ivSubmit.setImageResource(R.drawable.right_submit1);
        et_price.setEnabled(false);
        et_number.setEnabled(false);
        getIntentData();

        //添加点击的反馈效果
        Global.SetTouchView(ivSubmit, llMoreInfoBtn);
        //et_number.addTextChangedListener(watcherNumber);
        et_price.addTextChangedListener(watcherPrice);

    }

    /**
     * 修改意向产品 传递 过来的数据
     */
    private void getIntentData() {
        saleId = getIntent().getStringExtra("saleId");
        fromPage = getIntent().getIntExtra("data", 0);
        productListData = (ArrayList<SaleIntentionalProduct>) getIntent().getSerializableExtra("productList");
        SaleIntentionalProduct intentProduct = (SaleIntentionalProduct) getIntent().getSerializableExtra(ExtraAndResult.EXTRA_DATA);
        if (null != intentProduct) {
            tv_title.setText("编辑意向产品");
            productId = intentProduct.id;
            tv_product.setText(intentProduct.name);
            tv_price.setText(Utils.setValueDouble(intentProduct.costPrice + ""));
            et_price.setText(Utils.setValueDouble(intentProduct.salePrice + ""));
            et_number.setText(Utils.setValueDouble(intentProduct.quantity + ""));
            tv_discount.setText(Utils.setValueDouble(intentProduct.discount) + "%");
            tv_total.setText(Utils.setValueDouble(intentProduct.totalMoney + ""));
            et_remake.setText(intentProduct.memo);
            /*if (!TextUtils.isEmpty(intentProduct.unit)) {
                tv_price.setText("产品原价(" + intentProduct.unit + ")");
                et_price.setText("销售价格(" + intentProduct.unit + ")");
            }*/
            oldId = intentProduct.id;
            ll_loading.setStatus(LoadingLayout.Loading);
            mPersenter.getProductDetails(productId);
        }
    }

    /**
     * 销售价
     */
    private TextWatcher watcherPrice = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!s.toString().contains(".") && s.toString().length() > 7) {
                s.delete(7, s.toString().length());
            }
            setDiscount(s.toString());
            if (!TextUtils.isEmpty(et_number.getText().toString())) {
                tv_total.setText((Utils.setValueDouble(transformationNumber(s.toString())
                        * transformationNumber(et_number.getText().toString()))) + "");
            }
        }
    };

    /**
     * 销售数量
     */
/*    private TextWatcher watcherNumber = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!s.toString().contains(".") && s.toString().length() > 7) {
                s.delete(7, s.toString().length());
            }
            if (!TextUtils.isEmpty(et_price.getText().toString())) {
                tv_total.setText(Utils.setValueDouble((transformationNumber(s.toString())
                        * transformationNumber(et_price.getText().toString()))) + "");
            } else {
                tv_total.setText("");
            }
        }
    };*/

    private double transformationNumber(String text) {
        if (!TextUtils.isEmpty(text)) {
            try {
                return Double.valueOf(text);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                Toast("你应该输入数字");
            }
        }
        return -1;
    }

    /**
     * 设置折扣
     */
    private void setDiscount(String salePrice) {
        if (!TextUtils.isEmpty(tv_price.getText().toString()) && !"0".equals(tv_price.getText().toString())) {
            tv_discount.setText(Utils.setValueDouble((transformationNumber(salePrice)
                    / transformationNumber(tv_price.getText().toString()) * 100)) + "%");
        } else {
            tv_discount.setText("");
        }
    }

    /**
     * 编辑意向产品
     */
    public void editProduct() {
        final SaleIntentionalProduct data = assembleData();
        HashMap<String, Object> map = new HashMap<>();
        map.put("cId", saleId);
        map.put("proInfo", data);
        map.put("oldId", oldId);
        LogUtil.d("编辑产品:" + MainApp.gson.toJson(map));
        Intent intent = new Intent();
        intent.putExtra(ExtraAndResult.EXTRA_DATA, map);
        intent.putExtra(ExtraAndResult.STR_SHOW_TYPE, ActionCode.SALE_DETAILS_RUSH);
        app.finishActivity(AddBuyProductActivity.this, MainApp.ENTER_TYPE_RIGHT, RESULT_OK, intent);
    }

    /**
     * 新增意向产品
     */
    public void addProduct() {
        if (productListData != null && productListData.size() > 0) {
            for (SaleIntentionalProduct ele : productListData) {
                if (productId.equals(ele.id)) {
                    Toast("产品已经存在,不能重复添加");
                    return;
                }
            }
        }
        final SaleIntentionalProduct data = assembleData();
        HashMap<String, Object> map = new HashMap<>();
        map.put("cId", saleId);
        map.put("proInfo", data);

        Intent intent = new Intent();
        intent.putExtra(ExtraAndResult.EXTRA_DATA, map);
        intent.putExtra(ExtraAndResult.STR_SHOW_TYPE, ActionCode.SALE_DETAILS_RUSH);
        app.finishActivity(AddBuyProductActivity.this, MainApp.ENTER_TYPE_RIGHT, RESULT_OK, intent);
    }

    /**
     * 组装数据
     */
    private SaleIntentionalProduct assembleData() {
        if (TextUtils.isEmpty(productId) && TextUtils.isEmpty(tv_product.getText().toString())) {
            Toast("请选择产品");
            return null;
        } else if (TextUtils.isEmpty(et_price.getText().toString())) {
            Toast("请输入销售价格");
            return null;
        } else if (TextUtils.isEmpty(et_number.getText().toString())) {
            Toast("请输数量");
            return null;
        }
        SaleIntentionalProduct product = new SaleIntentionalProduct();
        product.id = productId;
        product.name = tv_product.getText().toString();
        product.costPrice = transformationNumber(tv_price.getText().toString());
        product.salePrice = transformationNumber(et_price.getText().toString());
        product.quantity = transformationNumber(et_number.getText().toString());
        product.discount = transformationNumber(tv_discount.getText().toString().
                substring(0, tv_discount.getText().toString().length() - 1));
        product.totalMoney = transformationNumber(tv_total.getText().toString());
        product.memo = et_remake.getText().toString();
        product.unit = productUnit;
        return product;
    }

    //添加自定义字段
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
        et_price.setEnabled(true);
        et_number.setEnabled(true);
        llMoreInfoBtn.setVisibility(View.VISIBLE);
        layout_prdprice.setVisibility(View.VISIBLE);
        layout_prdsize.setVisibility(stockEnabled ? View.VISIBLE : View.GONE);
        layout_prdkind.setVisibility(View.VISIBLE);

        tv_product.setText(detailsModel.name);
        tv_price.setText(detailsModel.unitPrice+"");
        prdSize.setText(detailsModel.getStock()+"");
        prdKind.setText(detailsModel.category);
        memo.setText(detailsModel.memo);
        tv_unit.setText(detailsModel.unit);
        addDefined();
        if(detailsModel.attachment.size() > 0){
            layout_image.setVisibility(View.VISIBLE);
            ProductPicAdapter picAdapter = new ProductPicAdapter(this, detailsModel.attachment);
            gridViewPic.setAdapter(picAdapter);
        }else{
            layout_image.setVisibility(View.GONE);
        }

        et_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!TextUtils.isEmpty(s + "")) {
                    float num = Float.parseFloat(s + "");
                    if (stockEnabled && num > detailsModel.stock) {
                        Toast("库存不足");
                        et_number.setText(detailsModel.getStock()+"");
                    }else{
                        if (!s.toString().contains(".") && s.toString().length() > 7) {
                            s.delete(7, s.toString().length());
                        }
                        if (!TextUtils.isEmpty(et_price.getText().toString())) {
                            tv_total.setText(Utils.setValueDouble((transformationNumber(s.toString())
                                    * transformationNumber(et_price.getText().toString()))) + "");
                        } else {
                            tv_total.setText("");
                        }
                    }
                }
            }
        });
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
        ll_loading.setStatus(LoadingLayout.Success);
        detailsModel = details;
        bindData();
        if(!TextUtils.isEmpty(details.attachmentUUId) || null != details.attachmentUUId);
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

    // EditText监听
    @Override
    public void textWatcherCallback(int type,String s) {

        switch (type){

            // 销售价格
            case 0:

                break;

            // 数量
            case 1:

                break;

        }

    }

    /**
     * 选择的产品 回调查询详情
     * */
    @Subscribe
    public void selectProductCallBack(SelectProductEvent event){
        Bundle mBundle = event.bundle;
        productId = mBundle.getString("id");
        stockEnabled = mBundle.getBoolean("enable");
        ll_loading.setStatus(LoadingLayout.Loading);
        mPersenter.getProductDetails(productId);
        LogUtil.dee("新增stockEnabled:"+stockEnabled);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            // 选择产品
            case R.id.add_buy_product_ll_1:
                Intent mIntent = new Intent(AddBuyProductActivity.this,SelectProductActivity.class);
                startActivity(mIntent);
                break;

            // 后退
            case R.id.ll_back:
                onBackPressed();
                break;

            // 提交
            case R.id.iv_submit:
                if (fromPage == ActionCode.SALE_FROM_DETAILS && !TextUtils.isEmpty(saleId)) {
                    addProduct();
                } else if (fromPage == ActionCode.SALE_PRO_EDIT && !TextUtils.isEmpty(saleId)) {
                    editProduct();
                } else {
                    if (productListData != null && productListData.size() > 0) {
                        for (SaleIntentionalProduct ele : productListData) {
                            if (productId.equals(ele.id)) {
                                Toast("产品已经存在,不能重复添加");
                                return;
                            }
                        }
                    }
                    SaleIntentionalProduct data = assembleData();
                    if (null != data) {
                        Intent intent = new Intent();
                        intent.putExtra(ExtraAndResult.EXTRA_DATA, data);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
                break;

        }
    }
}