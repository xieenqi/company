package com.loyo.oa.v2.activity.sale;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
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
import com.loyo.oa.v2.tool.Utils;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【新增意向产品】
 * Created by xeq on 16/5/20.
 */
public class ActivityAddIntentionProduct extends BaseActivity {

    private TextView tv_title, tv_product, tv_price, tv_discount, tv_total, tv_oldePrice, tv_salePrice;
    private LinearLayout ll_back, ll_poduct;
    private ImageView iv_submit;
    private EditText et_price, et_number, et_remake;
    private ArrayList<Product> lstData_Product = new ArrayList<>();
    private AlertDialog dialog_Product;
    private String productId = "", productUnit;
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
        et_price.setFilters(new InputFilter[]{Utils.decimalDigits(2)});
        et_number = (EditText) findViewById(R.id.et_number);
        et_number.addTextChangedListener(watcherNumber);
        et_number.setFilters(new InputFilter[]{Utils.decimalDigits(2)});
        tv_discount = (TextView) findViewById(R.id.tv_discount);
        tv_total = (TextView) findViewById(R.id.tv_total);
        et_remake = (EditText) findViewById(R.id.et_remake);
        tv_oldePrice = (TextView) findViewById(R.id.tv_oldePrice);
        tv_salePrice = (TextView) findViewById(R.id.tv_salePrice);
    }

    /**
     * 修改意向产品 传递 过来的数据
     */
    private void getIntentData() {
        saleId = getIntent().getStringExtra("saleId");
        fromPage = getIntent().getIntExtra("data", 0);
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
            if (!TextUtils.isEmpty(intentProduct.unit)) {
                tv_oldePrice.setText("产品原价(" + intentProduct.unit + ")");
                tv_salePrice.setText("销售价格(" + intentProduct.unit + ")");
            }
            oldId = intentProduct.id;
        }
    }

    /**
     * 获取意向产品
     */
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
     */
    public void editProduct() {
        showLoading("");
        final SaleIntentionalProduct data = assembleData();
        HashMap<String, Object> map = new HashMap<>();
        map.put("cId", saleId);
        map.put("proInfo", data);
        map.put("oldId", oldId);
        LogUtil.d("编辑产品:" + MainApp.gson.toJson(map));
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ISale.class).editSaleProduct(map, saleId, new RCallback<SaleProductEdit>() {
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
     */
    public void addProduct() {
        showLoading("");
        final SaleIntentionalProduct data = assembleData();
        HashMap<String, Object> map = new HashMap<>();
        map.put("cId", saleId);
        map.put("proInfo", data);

        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ISale.class).addSaleProduct(map, new RCallback<SaleProductEdit>() {
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
                    if (fromPage == ActionCode.SALE_FROM_DETAILS && !TextUtils.isEmpty(saleId)) {
                        addProduct();
                    } else if (fromPage == ActionCode.SALE_PRO_EDIT && !TextUtils.isEmpty(saleId)) {
                        editProduct();
                    } else {
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
            if (!TextUtils.isEmpty(tv_price.getText().toString()) && !"0".equals(tv_price.getText().toString())) {
                tv_discount.setText(Utils.setValueDouble((transformationNumber(s.toString())
                        / transformationNumber(tv_price.getText().toString()) * 100)) + "%");
            } else {
                tv_discount.setText("");
            }
            if (!TextUtils.isEmpty(et_number.getText().toString())) {
                tv_total.setText((Utils.setValueDouble(transformationNumber(s.toString())
                        * transformationNumber(et_number.getText().toString()))) + "");
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
    };


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
     * 选择 产品
     */
    private void SelectProduct() {
        if (null == lstData_Product || !(lstData_Product.size() > 0)) {
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
                productUnit = item.unit;
                tv_price.setText(Utils.setValueDouble(item.unitPrice + ""));

                et_price.setText("");
                et_number.setText("");
                tv_discount.setText("");
                tv_total.setText("");
                et_remake.setText("");
                tv_oldePrice.setText("产品原价(" + item.unit + ")");
                tv_salePrice.setText("销售价格(" + item.unit + ")");
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
        product.unit = productUnit;
        return product;
    }


}
