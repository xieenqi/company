package com.loyo.oa.v2.activityui.clue;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customview.PaymentPopView;
import com.loyo.oa.v2.customview.SelectCityView;
import com.loyo.oa.v2.tool.BaseActivity;

/**
 * 线索 新建 页面
 * Created by xeq on 16/8/20.
 */
public class ClueAddActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout img_title_left, img_title_right;
    private TextView tv_title_1, tv_area, tv_source;
    private EditText et_name, et_company, et_phone, et_tel, et_address, et_remake;
    private LinearLayout ll_area, ll_source;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_clue);
        getIntentData();
        initView();
    }

    private void getIntentData() {
//        orderId = getIntent().getStringExtra(ExtraAndResult.EXTRA_ID);
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
        tv_title_1.setText("新建线索");
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                onBackPressed();
                break;
            case R.id.img_title_right:
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
//                regional.province = cityArr[0];
//                regional.city = cityArr[1];
//                regional.county = cityArr[2];
                selectCityView.dismiss();
            }
        });
    }

    /**
     * 线索来源选择
     */
    private void selectSource() {
        String[] dataKind = {"广告", "搜索引擎", "研讨会", "客户介绍", "独立开发", "其它"};
        final PaymentPopView popViewKind = new PaymentPopView(this, dataKind, "线索来源");
        popViewKind.show();
        popViewKind.setCanceledOnTouchOutside(true);
        popViewKind.setCallback(new PaymentPopView.VaiueCallback() {
            @Override
            public void setValue(String value, int index) {
//                payeeMethod = index;
//                tv_kind.setText(value);
            }
        });
    }
}
