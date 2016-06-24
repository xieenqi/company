package com.loyo.oa.v2.tool;

import android.app.Activity;
import android.content.Context;

import com.loyo.oa.v2.common.DialogHelp;
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

    private Context mContext;

    public CommonSubscriber(Activity activity) {
        super();
        mContext = activity;
        init(activity);
    }

    private void init(Activity activity) {
        mContext = activity;
    }

    @Override
    public void onCompleted() {
        DialogHelp.cancelLoading();
    }

    @Override
    public void onError(Throwable e) {
        Global.Toast("处理失败");
        DialogHelp.cancelLoading();
    }

    @Override
    public void onStart() {
        DialogHelp.showLoading(mContext, "正在上传", true);
    }
}
