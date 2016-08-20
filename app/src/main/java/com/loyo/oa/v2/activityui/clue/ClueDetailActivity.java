package com.loyo.oa.v2.activityui.clue;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.customview.ActionSheetDialog;
import com.loyo.oa.v2.tool.BaseActivity;

//import org.androidannotations.annotations.AfterViews;
//import org.androidannotations.annotations.Click;
//import org.androidannotations.annotations.EActivity;
//import org.androidannotations.annotations.Extra;
//import org.androidannotations.annotations.ViewById;

public class ClueDetailActivity extends BaseActivity implements View.OnClickListener {

//    @ViewById
    ViewGroup img_title_left, img_title_right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clue_detail);
        setTitle("线索详情");

        setupViews();
    }

    private void setupViews() {


        img_title_left = (ViewGroup)findViewById(R.id.img_title_left) ;
        img_title_right = (ViewGroup)findViewById(R.id.img_title_right) ;

        img_title_left.setOnClickListener(this);
        img_title_right.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.img_title_left:
                onBackPressed();
                break;
            //弹出菜单
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
