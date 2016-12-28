package com.loyo.oa.v2.activityui.clue;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.clue.api.ClueService;
import com.loyo.oa.v2.activityui.clue.model.ClueDetailWrapper;
import com.loyo.oa.v2.activityui.clue.model.ClueSales;
import com.loyo.oa.v2.activityui.clue.common.ClueCommon;
import com.loyo.oa.v2.activityui.customer.model.CustomerRegional;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.PaymentPopView;
import com.loyo.oa.v2.customview.SelectCityView;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.LoyoErrorChecker;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.SharedUtil;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 线索 新建 页面
 * Created by xeq on 16/8/20.
 */
public class ClueAddActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout img_title_left, img_title_right;
    private TextView tv_title_1, tv_area, tv_source;
    private EditText et_name, et_company, et_phone, et_tel, et_address, et_remake;
    private LinearLayout ll_area, ll_source;
    private CustomerRegional regional = new CustomerRegional();
    private String[] dataKind;
    private String clueId;
    private ClueDetailWrapper.ClueDetail editData;
    private boolean isEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_clue);
        getIntentData();
        initView();
    }

    private void getIntentData() {
        clueId = getIntent().getStringExtra(ExtraAndResult.EXTRA_ID);
        editData = (ClueDetailWrapper.ClueDetail) getIntent().getSerializableExtra(ExtraAndResult.EXTRA_DATA);
//        if (TextUtils.isEmpty(orderId)) {
//            onBackPressed();
//            Toast("参数不全");
//        }
    }

    private void initView() {
        img_title_left = (RelativeLayout) findViewById(R.id.img_title_left);
        img_title_right = (RelativeLayout) findViewById(R.id.img_title_right);
        tv_title_1 = (TextView) findViewById(R.id.tv_title_1);
        img_title_left.setOnTouchListener(Global.GetTouch());
        img_title_right.setOnTouchListener(Global.GetTouch());
        tv_title_1.setText(TextUtils.isEmpty(clueId) ? "新建线索" : "编辑线索");
        img_title_left.setOnClickListener(this);
        img_title_right.setOnClickListener(this);
        et_name = (EditText) findViewById(R.id.et_name);
        et_company = (EditText) findViewById(R.id.et_company);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_tel = (EditText) findViewById(R.id.et_tel);
        et_address = (EditText) findViewById(R.id.et_address);
        et_remake = (EditText) findViewById(R.id.et_remake);
        tv_area = (TextView) findViewById(R.id.tv_area);
        tv_source = (TextView) findViewById(R.id.tv_source);
        ll_area = (LinearLayout) findViewById(R.id.ll_area);
        ll_area.setOnClickListener(this);
        ll_source = (LinearLayout) findViewById(R.id.ll_source);
        ll_source.setOnClickListener(this);

        et_phone.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        et_tel.setKeyListener(DigitsKeyListener.getInstance("0123456789"));

        editClue();
    }

    private void editClue() {
        if (null == editData) {
            isEdit = false;
            return;
        }
        isEdit = true;
        ClueSales sales = editData.sales;
        et_name.setText(sales.name);
        et_company.setText(sales.companyName);
        et_phone.setText(sales.cellphone);
        et_tel.setText(sales.tel);
        et_address.setText(sales.address);
        et_remake.setText(sales.remark);
        tv_area.setText(sales.getRegion());
        regional.province = sales.region.province;
        regional.city = sales.region.city;
        regional.county = sales.region.county;
        tv_source.setText(sales.source);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                onBackPressed();
                break;
            case R.id.img_title_right:
                if (TextUtils.isEmpty(et_name.getText().toString().trim())) {
                    Toast("请输入线索名称");
                    return;
                } else if (TextUtils.isEmpty(et_company.getText().toString().trim())) {
                    Toast("请输入公司名称");
                    return;
                }
                if (et_name.getText().toString().trim().length() > 20) {
                    Toast("线索名称应少于20个字符");
                    return;
                }
                if (et_company.getText().toString().trim().length() > 50) {
                    Toast("公司名称应少于50个字符");
                    return;
                }
                addDataInfo();
                break;
            case R.id.ll_area://地区选择
                selectArea();
                break;
            case R.id.ll_source://线索来源选择
                selectSource();
                break;
        }
    }


    /**
     * 显示地区选择Dialog  选择地区
     */
    void selectArea() {
        String[] cityValue = null;
        if (!tv_area.getText().toString().isEmpty()) {
            cityValue = tv_area.getText().toString().split(" ");
        }
        final SelectCityView selectCityView = new SelectCityView(this, cityValue);
        selectCityView.setCanceledOnTouchOutside(true);
        selectCityView.show();
        selectCityView.setOnclickselectCity(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                String[] cityArr = selectCityView.getResult();
                tv_area.setText(cityArr[0] + " " + cityArr[1] + " " + cityArr[2]);
                regional.province = cityArr[0];
                regional.city = cityArr[1];
                regional.county = cityArr[2];
                selectCityView.dismiss();
            }
        });
    }

    /**
     * 线索来源选择
     */
    private void selectSource() {
        dataKind = app.gson.fromJson(SharedUtil.get(app, ExtraAndResult.SOURCES_DATA),
                new TypeToken<String[]>() {
                }.getType());
        if (null == dataKind) {
            Toast("数据加载中...");
            ClueCommon.getSourceData();//缓存线索来源数据
            return;
        }
        final PaymentPopView popViewKind = new PaymentPopView(this, dataKind, "线索来源");
        popViewKind.show();
        popViewKind.setCanceledOnTouchOutside(true);
        popViewKind.setCallback(new PaymentPopView.VaiueCallback() {
            @Override
            public void setValue(String value, int index) {
//                payeeMethod = index;
                tv_source.setText(value);
            }
        });
    }

    /**
     * 新建编辑 线索
     * */
    private void addDataInfo() {
        showStatusLoading(false);
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", et_name.getText().toString());
        map.put("companyName", et_company.getText().toString());
        map.put("cellphone", et_phone.getText().toString());
        map.put("tel", et_tel.getText().toString());
        map.put("region", regional);
        map.put("address", et_address.getText().toString());
        map.put("remark", et_remake.getText().toString());
        map.put("source", tv_source.getText().toString());
        if (!isEdit) {
//            RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(IClue.class)
//                    .addClue(map, new Callback<ClueDetailWrapper>() {
//                        @Override
//                        public void success(final ClueDetailWrapper clueDetailWrapper, Response response) {
//                            HttpErrorCheck.checkCommitSus("新建线索",response);
//                            new Handler().postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    cancelStatusLoading();
//                                    if(clueDetailWrapper.errcode != 0){
//                                        Toast(clueDetailWrapper.errmsg);
//                                        return;
//                                    }else if(clueDetailWrapper.errcode == 0){
//                                        app.finishActivity(ClueAddActivity.this, MainApp.ENTER_TYPE_LEFT, ExtraAndResult.REQUEST_CODE, new Intent());
//                                    }
//                                }
//                            },1000);
//                        }
//
//                        @Override
//                        public void failure(RetrofitError error) {
//                            HttpErrorCheck.checkCommitEro(error);
//                        }
//                    });

            ClueService.addClue(map).subscribe(new DefaultLoyoSubscriber<ClueDetailWrapper>(LoyoErrorChecker.COMMIT_DIALOG) {
                @Override
                public void onNext(final ClueDetailWrapper clueDetailWrapper) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {//TODO Why??!! 延迟1s干什么呢
                            cancelStatusLoading();
                            if(clueDetailWrapper.errcode != 0){
                                Toast(clueDetailWrapper.errmsg);
                                return;
                            }else if(clueDetailWrapper.errcode == 0){
                                app.finishActivity(ClueAddActivity.this, MainApp.ENTER_TYPE_LEFT, ExtraAndResult.REQUEST_CODE, new Intent());
                            }
                        }
                    },1000);
                }
            });
        } else {
//            RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(IClue.class)
//                    .editClue(clueId, map, new Callback<Object>() {
//                        @Override
//                        public void success(Object o, Response response) {
//                            HttpErrorCheck.checkCommitSus("编辑线索",response);
//                            new Handler().postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    onBackPressed();
//                                }
//                            },1000);
//                        }
//
//                        @Override
//                        public void failure(RetrofitError error) {
//                            HttpErrorCheck.checkCommitEro(error);
//                        }
//                    });

            ClueService.editClue(clueId,map).subscribe(new DefaultLoyoSubscriber<Object>(LoyoErrorChecker.COMMIT_DIALOG) {
                @Override
                public void onNext(Object o) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onBackPressed();
                        }
                    },1000);
                }
            });
        }
    }
}
