package com.loyo.oa.v2.activityui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.customview.SweetAlertDialogView;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**用于展现对话框
 * Created by xeq on 16/12/22.
 */

public class DialogTipActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SweetAlertDialogView doalog = new SweetAlertDialogView(this);
        doalog.alertMessageClick(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                doalog.sweetAlertDialog.dismiss();
                //到侧边栏 退出系统到登录界面
                Intent in = new Intent();
                in.setAction(ExtraAndResult.ACTION_USER_VERSION);
                in.putExtra(ExtraAndResult.EXTRA_DATA, "exite");
                LocalBroadcastManager.getInstance(DialogTipActivity.this).sendBroadcast(in);
                finish();
            }
        }, "提示", getIntent().getStringExtra("msg"));
    }
}
