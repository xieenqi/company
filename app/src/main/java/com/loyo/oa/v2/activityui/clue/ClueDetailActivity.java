package com.loyo.oa.v2.activityui.clue;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.clue.bean.ClueDetail;
import com.loyo.oa.v2.activityui.clue.bean.ClueSales;
import com.loyo.oa.v2.activityui.clue.common.ClueCommon;
import com.loyo.oa.v2.activityui.customer.bean.CustomerRegional;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.ActionSheetDialog;
import com.loyo.oa.v2.customview.PaymentPopView;
import com.loyo.oa.v2.customview.SelectCityView;
import com.loyo.oa.v2.point.IClue;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.SharedUtil;

import java.util.Date;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ClueDetailActivity extends BaseActivity implements View.OnClickListener {

    /*  Navigation Bar */
    ViewGroup img_title_left /* 返回按钮 */,
            img_title_right  /* 右上菜单 */;

    /*  分区1 */
    TextView section1_username    /* 姓名 */,
            section1_company_name /* 公司名称 */,
            section1_clue_status  /* 线索状态 */;

    /*  分区2 */
    ViewGroup section2_visit      /* 跟进动态 */,
            ll_track /* 最近跟进详情 */;

    TextView visit_times          /* 跟进次数 */,
            tv_track_content   /* 最近跟进内容 */,
            tv_track_time   /* 最近跟进元信息 */;

    /*  分区3 */
    ViewGroup layout_mobile_send_sms  /* 手机发短信 */,
            layout_mobile_call        /* 手机拨电话 */,
            layout_wiretel_call       /* 座机拨电话 */,
            layout_clue_region        /* 地区弹出列表 */,
            layout_clue_source        /* 线索来源弹出列表 */;

    TextView contact_mobile  /* 手机 */,
            contact_wiretel  /* 座机 */,
            clue_region      /* 地区 */,
            clue_source      /* 线索来源 */,
            clue_note        /* 备注 */;

    /*  分区4 */
    TextView responsible_name/* 负责人 */,
            creator_name     /* 创建人 */,
            create_time      /* 创建时间 */,
            update_time      /* 更新时间 */,
            tv_address;

    /* Data */
    String clueId;
    ClueDetail data;

    private CustomerRegional regional = new CustomerRegional();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clue_detail);
        setTitle("线索详情");
        setupViews();
        getIntenData();
        app = (MainApp) getApplicationContext();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getClueDetail();
    }

    private void setupViews() {

        /* Navigation Bar */
        img_title_left = (ViewGroup) findViewById(R.id.img_title_left);
        img_title_right = (ViewGroup) findViewById(R.id.img_title_right);
        img_title_left.setOnClickListener(this);
        img_title_right.setOnClickListener(this);

        /* 分区1 */
        section1_username = (TextView) findViewById(R.id.tv_section1_username);
        section1_company_name = (TextView) findViewById(R.id.tv_section1_company_name);
        section1_clue_status = (TextView) findViewById(R.id.tv_section1_clue_status);

        /* 分区2 */
        section2_visit = (ViewGroup) findViewById(R.id.ll_section2_visit);
        ll_track = (ViewGroup) findViewById(R.id.ll_track);
        section2_visit.setOnClickListener(this); // 选择来源

        visit_times = (TextView) findViewById(R.id.tv_visit_times);
        tv_track_content = (TextView) findViewById(R.id.tv_track_content);
        tv_track_time = (TextView) findViewById(R.id.tv_track_time);

        /* 分区3 */
        layout_mobile_send_sms = (ViewGroup) findViewById(R.id.layout_mobile_send_sms);
        layout_mobile_call = (ViewGroup) findViewById(R.id.layout_mobile_call);
        layout_wiretel_call = (ViewGroup) findViewById(R.id.layout_wiretel_call);
        layout_clue_region = (ViewGroup) findViewById(R.id.layout_clue_region);
        layout_clue_source = (ViewGroup) findViewById(R.id.layout_clue_source);
        layout_clue_region.setOnClickListener(this); // 选择地区
        layout_clue_source.setOnClickListener(this); // 选择来源

        contact_mobile = (TextView) findViewById(R.id.tv_contact_mobile);
        contact_wiretel = (TextView) findViewById(R.id.tv_contact_wiretel);
        clue_region = (TextView) findViewById(R.id.tv_clue_region);
        clue_source = (TextView) findViewById(R.id.tv_section3_clue_source);
        clue_note = (TextView) findViewById(R.id.tv_clue_note);

        /* 分区4 */
        responsible_name = (TextView) findViewById(R.id.tv_responsible_name);
        creator_name = (TextView) findViewById(R.id.tv_creator_name);
        create_time = (TextView) findViewById(R.id.tv_create_time);
        update_time = (TextView) findViewById(R.id.tv_update_time);
        tv_address = (TextView) findViewById(R.id.tv_address);
    }

    public void bindData() {
        ClueSales sales = data.data.sales;
                /* 分区1 */
        section1_username.setText(sales.name);
        section1_company_name.setText(sales.companyName);
        section1_clue_status.setText("" + sales.status);

        /* 分区2 */
        // section2_visit
        // visit_times
        if (data.data.activity == null) {
            ll_track.setVisibility(View.GONE);
        } else {
            ll_track.setVisibility(View.VISIBLE);
            tv_track_content.setText(data.data.activity.content);
            tv_track_time.setText(app.df3.format(new Date(Long.valueOf(data.data.activity.remindAt + "") * 1000))
                    + "  " + data.data.activity.contactName + " # " + data.data.activity.typeName);
        }

        /* 分区3 */
        contact_mobile.setText(sales.cellphone);
        contact_wiretel.setText(sales.tel);
        clue_region.setText(sales.getRegion());
        tv_address.setText(sales.address);
        clue_source.setText(sales.source);
        clue_note.setText(sales.remark);

        /* 分区4 */
        responsible_name.setText(sales.responsorName);
        creator_name.setText(sales.creatorName);
        create_time.setText(app.df3.format(new Date(Long.valueOf(sales.createAt + "") * 1000)));
        update_time.setText(app.df3.format(new Date(Long.valueOf(sales.updateAt + "") * 1000)));
    }

    private void getIntenData() {
        Intent intent = getIntent();
        clueId = intent.getStringExtra(ExtraAndResult.EXTRA_ID);
        if (TextUtils.isEmpty(clueId)) {
            onBackPressed();
            Toast("参数不全");
        }
    }

    /**
     * 获取 线索详情
     */
    public void getClueDetail() {
        if (clueId == null) {
            return;
        }

        showLoading("");
        RestAdapterFactory.getInstance()
                .build(Config_project.API_URL_CUSTOMER())
                .create(IClue.class)
                .getClueDetail(clueId, new Callback<ClueDetail>() {
                    @Override
                    public void success(ClueDetail detail, Response response) {
                        HttpErrorCheck.checkResponse("线索详情：", response);
                        if (null == detail) {
                            Toast("没有获取数据");
                            onBackPressed();
                            return;
                        }
                        data = detail;
                        bindData();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                    }
                });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            /* 返回按钮 */
            case R.id.img_title_left:
                onBackPressed();
                break;
            /* 右上弹出菜单 */
            case R.id.img_title_right:
                functionButton();
                break;
            /* 选择地区*/
            case R.id.layout_clue_region:
                selectArea();
                break;
            /* 选择来源 */
            case R.id.layout_clue_source:
                selectSource();
                break;
            /* 跟进列表 */
            case R.id.ll_section2_visit:
                clueActivity();
                break;

            default:
                break;

        }
    }

    /**
     * 跳转跟进列表
     */
    private void clueActivity() {
        Intent intent = new Intent();
        intent.putExtra(ExtraAndResult.EXTRA_ID, data.data.sales.id);
        String name = data.data.sales.name;
        if (TextUtils.isEmpty(name)){
            name = "";
        }
        intent.putExtra(ExtraAndResult.EXTRA_NAME, name);
        intent.setClass(this, ClueFollowupActivity.class);
        startActivityForResult(intent, this.RESULT_FIRST_USER);
        overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
    }


    /**
     * 右上角菜单
     */
    private void functionButton() {
        ActionSheetDialog dialog = new ActionSheetDialog(ClueDetailActivity.this).builder();
        if (true /* 是否有权限转移客户 */) {
            dialog.addSheetItem("转移客户", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                @Override
                public void onClick(int which) {
                    app.startActivityForResult(ClueDetailActivity.this, ClueTransferActiviyt.class, MainApp.ENTER_TYPE_RIGHT, 0x01, new Bundle());
                }
            });
        }

        if (true /* 是否有权限转移给他人 */) {
            dialog.addSheetItem("转移给他人", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                @Override
                public void onClick(int which) {

                }
            });
        }

        if (true /* 是否有权限编辑 */) {
            dialog.addSheetItem("编辑", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                @Override
                public void onClick(int which) {
                    Bundle mBundle = new Bundle();
                    mBundle.putString(ExtraAndResult.EXTRA_ID, clueId);
                    mBundle.putSerializable(ExtraAndResult.EXTRA_DATA, data);
                    app.startActivityForResult(ClueDetailActivity.this, ClueAddActivity.class,
                            MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_STAGE, mBundle);
                }
            });
        }

        if (true /* 是否有权限删除 */) {
            dialog.addSheetItem("删除", ActionSheetDialog.SheetItemColor.Red, new ActionSheetDialog.OnSheetItemClickListener() {
                @Override
                public void onClick(int which) {

                }
            });
        }

        dialog.show();
    }

    /**
     * 显示地区选择Dialog  选择地区
     */
    void selectArea() {
        String[] cityValue = null;
        if (!clue_region.getText().toString().isEmpty()) {
            cityValue = clue_region.getText().toString().split(" ");
        }
        final SelectCityView selectCityView = new SelectCityView(this, cityValue);
        selectCityView.setCanceledOnTouchOutside(true);
        selectCityView.show();
        selectCityView.setOnclickselectCity(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                String[] cityArr = selectCityView.getResult();
                clue_region.setText(cityArr[0] + " " + cityArr[1] + " " + cityArr[2]);
                regional.province = cityArr[0];
                regional.city = cityArr[1];
                regional.county = cityArr[2];
                selectCityView.dismiss();
                editAreaAndSource(1);
            }
        });
    }

    /**
     * 线索来源选择
     */
    private void selectSource() {
        String[] dataKind = app.gson.fromJson(SharedUtil.get(app, ExtraAndResult.SOURCES_DATA),
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
                clue_source.setText(value);
                editAreaAndSource(2);
            }
        });
    }

    /**
     * 编辑线索 1 地区 2 线索来源
     *
     * @param function
     */
    private void editAreaAndSource(int function) {
        HashMap<String, Object> map = new HashMap<>();
        if (1 == function)
            map.put("region", regional);
        if (2 == function)
            map.put("source", clue_source.getText().toString());
        LogUtil.d(app.gson.toJson(map));
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(IClue.class)
                .editClue(clueId, map, new Callback<Object>() {
                    @Override
                    public void success(Object o, Response response) {
                        HttpErrorCheck.checkResponse("【编辑详情】线索：", response);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                    }
                });
    }

    private void deleteClue() {

    }
}
