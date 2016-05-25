package com.loyo.oa.v2.activity.sale;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.sale.bean.ActionCode;
import com.loyo.oa.v2.activity.sale.bean.SaleDetails;
import com.loyo.oa.v2.activity.sale.bean.SaleIntentionalProduct;
import com.loyo.oa.v2.activity.sale.bean.SaleProductEdit;
import com.loyo.oa.v2.adapter.ProductsRadioListViewAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Product;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.point.ISale;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【新增意向产品】
 * Created by xeq on 16/5/20.
 */
public class ActivityAddIntentionProduct extends BaseActivity {

    private TextView tv_title, tv_product, tv_price, tv_discount, tv_total;
    private LinearLayout ll_back, ll_poduct;
    private ImageView iv_submit;
    private EditText et_price, et_number, et_remake;
    private ArrayList<Product> lstData_Product = new ArrayList<>();
    private AlertDialog dialog_Product;
    private String productId = "";
    private String saleId = "";
    private String oldId = "";
    private int fromPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_intention_product);
        init();
        getData();
        getIntentData();
    }

    private void init() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("新增意向产品");
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnTouchListener(Global.GetTouch());
        ll_back.setOnClickListener(click);
        iv_submit = (ImageView) findViewById(R.id.iv_submit);
        iv_submit.setImageResource(R.drawable.right_submit1);
        iv_submit.setOnTouchListener(Global.GetTouch());
        iv_submit.setOnClickListener(click);
        ll_poduct = (LinearLayout) findViewById(R.id.ll_poduct);
        ll_poduct.setOnTouchListener(Global.GetTouch());
        ll_poduct.setOnClickListener(click);
        tv_product = (TextView) findViewById(R.id.tv_product);
        tv_price = (TextView) findViewById(R.id.tv_price);
        et_price = (EditText) findViewById(R.id.et_price);
        et_price.addTextChangedListener(watcherPrice);
        et_number = (EditText) findViewById(R.id.et_number);
        et_number.addTextChangedListener(watcherNumber);
        tv_discount = (TextView) findViewById(R.id.tv_discount);
        tv_total = (TextView) findViewById(R.id.tv_total);
        et_remake = (EditText) findViewById(R.id.et_remake);
    }

    /**
     * 修改意向产品 传递 过来的数据
     */
    private void getIntentData() {
        saleId = getIntent().getStringExtra("saleId");
        fromPage = getIntent().getIntExtra("data",0);
        SaleIntentionalProduct intentProduct = (SaleIntentionalProduct) getIntent().getSerializableExtra(ExtraAndResult.EXTRA_DATA);
        if (null != intentProduct) {
            tv_title.setText("编辑意向产品");
            productId = intentProduct.id;
            tv_product.setText(intentProduct.name);
            tv_price.setText(intentProduct.costPrice + "");
            et_price.setText(intentProduct.salePrice + "");
            et_number.setText(intentProduct.quantity + "");
            tv_discount.setText(intentProduct.discount + "%");
            tv_total.setText(intentProduct.totalMoney + "");
            et_remake.setText(intentProduct.memo);
            oldId = intentProduct.id;
        }
    }

    /**
     * 获取意向产品
     * */
    public void getData() {
        showLoading("");
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).getProducts(new RCallback<ArrayList<Product>>() {
            @Override
            public void success(final ArrayList<Product> products, final Response response) {
                HttpErrorCheck.checkResponse("意向产品##的产品", response);
                lstData_Product.addAll(products);
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
                finish();
            }
        });
    }

    /**
     * 编辑意向产品
     * */
    public void editProduct(){
        showLoading("");
        final SaleIntentionalProduct data = assembleData();
        HashMap<String,Object> map = new HashMap<>();
        map.put("Cid",saleId);
        map.put("ProInfo", data);
        map.put("OldId", oldId);
        LogUtil.d("编辑产品:"+MainApp.gson.toJson(map));
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ISale.class).editSaleProduct(map, new RCallback<SaleProductEdit>() {
            @Override
            public void success(SaleProductEdit saleProductEdit, final Response response) {
                HttpErrorCheck.checkResponse("编辑意向产品", response);
                if (null != data) {
                    Intent intent = new Intent();
                    intent.putExtra(ExtraAndResult.EXTRA_DATA, data);
                    intent.putExtra(ExtraAndResult.STR_SHOW_TYPE, ActionCode.SALE_DETAILS_RUSH);
                    app.finishActivity(ActivityAddIntentionProduct.this, MainApp.ENTER_TYPE_RIGHT, RESULT_OK, intent);
                }
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
            }
        });
    }


    /**
     * 新增意向产品
     * */
    public void addProduct(){
        showLoading("");
        final SaleIntentionalProduct data = assembleData();
        HashMap<String,Object> map = new HashMap<>();
        map.put("Cid",saleId);
        map.put("ProInfo", data);

        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ISale.class).addSaleProduct(map,new RCallback<SaleProductEdit>() {
            @Override
            public void success(SaleProductEdit saleProductEdit, final Response response) {
                HttpErrorCheck.checkResponse("新增意向产品", response);

                if (null != data) {
                    Intent intent = new Intent();
                    intent.putExtra(ExtraAndResult.EXTRA_DATA, data);
                    intent.putExtra(ExtraAndResult.STR_SHOW_TYPE, ActionCode.SALE_DETAILS_RUSH);
                    app.finishActivity(ActivityAddIntentionProduct.this, MainApp.ENTER_TYPE_RIGHT, RESULT_OK, intent);

                }
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
            }
        });
    }


    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ll_back:
                    onBackPressed();
                    break;
                case R.id.iv_submit:
                    if(fromPage == ActionCode.SALE_FROM_DETAILS){
                        addProduct();
                    }else if(fromPage == ActionCode.SALE_PRO_EDIT){
                        editProduct();
                    }else{
                        SaleIntentionalProduct data = assembleData();
                        if (null != data) {
                            Intent intent = new Intent();
                            intent.putExtra(ExtraAndResult.EXTRA_DATA, data);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }

                    break;
                case R.id.ll_poduct:
                    SelectProduct();
                    break;
            }

        }
    };
    private TextWatcher watcherNumber = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!TextUtils.isEmpty(et_price.getText().toString())) {
                tv_total.setText((transformationNumber(s.toString()) * transformationNumber(et_price.getText().toString())) + "");
            } else {
                tv_total.setText("");
            }
        }
    };
    private TextWatcher watcherPrice = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!TextUtils.isEmpty(tv_price.getText().toString())) {
                tv_discount.setText((transformationNumber(s.toString()) / transformationNumber(tv_price.getText().toString()) * 100) + "%");
            } else {
                tv_discount.setText("");
            }
        }
    };

    private float transformationNumber(String text) {
        if (!TextUtils.isEmpty(text)) {
            try {
                return Float.valueOf(text);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                Toast("你应该输入数字");
            }
        }
        return -1;
    }

    /**
     * 选择 产品
     */
    private void SelectProduct() {
        if (null == lstData_Product && !(lstData_Product.size() > 0)) {
            Toast("没有可以选择的产品");
            return;
        }
        if (null == dialog_Product) {
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.dialog_products_select, null, false);
            AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(layout);
            dialog_Product = builder.create();
        }
        dialog_Product.show();
        ListView listView_products = (ListView) dialog_Product.findViewById(R.id.listView);

        final ProductsRadioListViewAdapter productsRadioListViewAdapter = new ProductsRadioListViewAdapter(this, lstData_Product);
        listView_products.setAdapter(productsRadioListViewAdapter);
        listView_products.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                productsRadioListViewAdapter.notifyDataSetChanged();
                productsRadioListViewAdapter.isSelected = id;
                Product item = lstData_Product.get((int) id);
                tv_product.setText(item.name);
                productId = item.id;
                tv_price.setText(item.unitPrice);

                et_price.setText("");
                et_number.setText("");
                tv_discount.setText("");
                tv_total.setText("");
                et_remake.setText("");

                dialog_Product.dismiss();
            }
        });
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
        return product;
    }
//    {
//        "id": "573c2b1935d86037a65b7612",
//            "name": "棉花糖",
//            "costPrice": 30,
//            "salePrice": 30,
//            "quantity": 3,
//            "discount": 100,
//            "totalMoney": 90,
//            "memo": "",
//            "costTotalMoney": 90


}
