package com.loyo.oa.v2.tool;

import android.app.Activity;

import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.customview.CustomProgressDialog;

import java.io.Serializable;

import rx.Subscriber;

/**
 * com.loyo.oa.v2.tool
 * 描述 :订阅接口
 * 作者 : ykb
 * 时间 : 15/10/22.
 */
public abstract class CommonSubscriber extends Subscriber<Serializable> {
    private CustomProgressDialog progressDialog;

    public CommonSubscriber(Activity activity) {
        super();
        init(activity);
    }

    private void init(Activity activity) {
        progressDialog = new CustomProgressDialog(activity);
        progressDialog.setCancelable(false);
    }

    @Override
    public void onCompleted() {
        progressDialog.dismiss();
    }

    @Override
    public void onError(Throwable e) {
        progressDialog.dismiss();
        Global.Toast("处理失败");
    }

    @Override
    public void onStart() {
        progressDialog.show();
    }
}
