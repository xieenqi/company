package com.loyo.oa.v2.activityui.clue;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.clue.bean.ClueDetail;
import com.loyo.oa.v2.activityui.order.OrderAddActivity;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.BaseBean;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.ActionSheetDialog;
import com.loyo.oa.v2.customview.PaymentPopView;
import com.loyo.oa.v2.customview.SelectCityView;
import com.loyo.oa.v2.point.IClue;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RestAdapterFactory;

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
            section2_latest_visit /* 最近跟进详情 */;

    TextView visit_times          /* 跟进次数 */,
            section2_visit_desc   /* 最近跟进内容 */,
            section2_visit_meta   /* 最近跟进元信息 */;

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
            update_time      /* 更新时间 */;

    /* Data */
    String clueId;
    ClueDetail data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clue_detail);
        setTitle("线索详情");
        setupViews();
        getIntenData();
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
        section2_latest_visit = (ViewGroup) findViewById(R.id.ll_section2_latest_visit);

        visit_times = (TextView) findViewById(R.id.tv_visit_times);
        section2_visit_desc = (TextView) findViewById(R.id.tv_section2_visit_desc);
        section2_visit_meta = (TextView) findViewById(R.id.tv_section2_visit_meta);

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
    }

    public void bindData() {
                /* 分区1 */
        section1_username.setText(data.sales.name);
        section1_company_name.setText(data.sales.companyName);
        section1_clue_status.setText("" + data.sales.status);

        /* 分区2 */
        // section2_visit
        // visit_times
        if (data.activity == null) {
            section2_latest_visit.setVisibility(View.GONE);
        } else {
            section2_latest_visit.setVisibility(View.VISIBLE);
            section2_visit_desc.setText(data.activity.content);
            //section2_visit_meta
        }

        /* 分区3 */
        contact_mobile.setText(data.sales.cellPhone);
        contact_wiretel.setText(data.sales.tel);
        clue_region.setText(data.sales.getRegion());
        clue_source.setText(data.sales.source);
        clue_note.setText(data.sales.remark);

        /* 分区4 */
        responsible_name.setText(data.sales.responsorName);
        creator_name.setText(data.sales.creatorName);
        create_time.setText("" + data.sales.createAt);
        update_time.setText("" + data.sales.updateAt);
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
                .getClueDetail(clueId, new Callback<BaseBean<ClueDetail>>() {
                    @Override
                    public void success(BaseBean<ClueDetail> detail, Response response) {
                        HttpErrorCheck.checkResponse("线索详情：", response);
                        data = detail.data;
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

            default:
                break;

        }
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
                    mBundle.putString(ExtraAndResult.EXTRA_ID, "  ");
                    mBundle.putSerializable(ExtraAndResult.EXTRA_DATA, data);
                    app.startActivityForResult(ClueDetailActivity.this, OrderAddActivity.class, MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_STAGE, mBundle);
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
                clue_source.setText(value);
            }
        });
    }

}
