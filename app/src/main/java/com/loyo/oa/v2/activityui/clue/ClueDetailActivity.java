package com.loyo.oa.v2.activityui.clue;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.customview.ActionSheetDialog;
import com.loyo.oa.v2.tool.BaseActivity;

public class ClueDetailActivity extends BaseActivity implements View.OnClickListener {

    /*  Navigation Bar */
    ViewGroup img_title_left /* 返回按钮 */,
            img_title_right  /* 右上菜单 */;

    /*  分区1 */
    TextView section1_username    /* 姓名 */ ,
            section1_company_name /* 公司名称 */ ,
            section1_clue_status  /* 线索状态 */ ;

    /*  分区2 */
    ViewGroup section2_visit      /* 跟进动态 */,
            section2_latest_visit /* 最近跟进详情 */;

    TextView visit_times          /* 跟进次数 */ ,
            section2_visit_desc   /* 最近跟进内容 */ ,
            section2_visit_meta   /* 最近跟进元信息 */ ;

    /*  分区3 */
    ViewGroup layout_mobile_send_sms  /* 手机发短信 */,
            layout_mobile_call        /* 手机拨电话 */,
            layout_wiretel_call       /* 座机拨电话 */,
            layout_clue_region        /* 地区弹出列表 */,
            layout_clue_source        /* 线索来源弹出列表 */;

    TextView contact_mobile  /* 手机 */ ,
            contact_wiretel  /* 座机 */ ,
            clue_region      /* 地区 */ ,
            clue_source      /* 线索来源 */ ,
            clue_note        /* 备注 */ ;

    /*  分区4 */
    TextView responsible_name/* 负责人 */ ,
            creator_name     /* 创建人 */ ,
            create_time      /* 创建时间 */ ,
            update_time      /* 更新时间 */ ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clue_detail);
        setTitle("线索详情");

        setupViews();
    }

    private void setupViews() {

        /* Navigation Bar */
        img_title_left = (ViewGroup)findViewById(R.id.img_title_left) ;
        img_title_right = (ViewGroup)findViewById(R.id.img_title_right) ;
        img_title_left.setOnClickListener(this);
        img_title_right.setOnClickListener(this);

        /* 分区1 */
        section1_username = (TextView)findViewById(R.id.tv_section1_username);
        section1_company_name = (TextView)findViewById(R.id.tv_section1_company_name);
        section1_clue_status = (TextView)findViewById(R.id.tv_section1_clue_status);

        /* 分区2 */
        section2_visit = (ViewGroup)findViewById(R.id.ll_section2_visit) ;
        section2_latest_visit = (ViewGroup)findViewById(R.id.ll_section2_latest_visit) ;

        visit_times = (TextView)findViewById(R.id.tv_visit_times);
        section2_visit_desc = (TextView)findViewById(R.id.tv_section2_visit_desc);
        section2_visit_meta = (TextView)findViewById(R.id.tv_section2_visit_meta);

        /* 分区3 */
        layout_mobile_send_sms = (ViewGroup)findViewById(R.id.layout_mobile_send_sms) ;
        layout_mobile_call = (ViewGroup)findViewById(R.id.layout_mobile_call) ;
        layout_wiretel_call = (ViewGroup)findViewById(R.id.layout_wiretel_call) ;
        layout_clue_region = (ViewGroup)findViewById(R.id.layout_clue_region) ;
        layout_clue_source = (ViewGroup)findViewById(R.id.layout_clue_source) ;

        contact_mobile = (TextView)findViewById(R.id.tv_contact_mobile);
        contact_wiretel = (TextView)findViewById(R.id.tv_contact_wiretel);
        clue_region = (TextView)findViewById(R.id.tv_clue_region);
        clue_source = (TextView)findViewById(R.id.tv_section3_clue_source);
        clue_note = (TextView)findViewById(R.id.tv_clue_note);

        /* 分区4 */
        responsible_name = (TextView)findViewById(R.id.tv_responsible_name);
        creator_name = (TextView)findViewById(R.id.tv_creator_name);
        create_time = (TextView)findViewById(R.id.tv_create_time);
        update_time = (TextView)findViewById(R.id.tv_update_time);
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

}
