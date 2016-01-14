package com.loyo.oa.v2.tool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.widget.Toast;

import com.loyo.oa.v2.activity.project.ProjectInfoActivity;
import com.loyo.oa.v2.application.MainApp;

public abstract class BaseFragment extends Fragment implements ProjectInfoActivity.OnProjectChangeCallback {

    protected MainApp app = MainApp.getMainApp();
    protected Activity mActivity;
    protected OnLoadSuccessCallback callback;
    protected int mId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = MainApp.getMainApp();
        mActivity = getActivity();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        //        if (activity instanceof BaseActivity){
        //            mActivity = (BaseActivity)activity;
        //            app = mActivity.app;
        ////            mActivity.app.logUtil;
        //        } else {
        //            //TODO:提示错误
        //            Toast.makeText(this.getActivity(),"This is not BaseActivity!",Toast.LENGTH_SHORT).show();
        //        }
    }

    private Toast mCurrentToast;

    protected void Toast(String msg) {
        if (null != mCurrentToast) {
            mCurrentToast.cancel();
        }

        mCurrentToast = Toast.makeText(app.getBaseContext(), msg, Toast.LENGTH_SHORT);
        mCurrentToast.setGravity(Gravity.CENTER, 0, 0);
        mCurrentToast.show();
    }

    protected void Toast(int resId) {
        Toast(app.getString(resId));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onProjectChange(int status) {

    }

    public void setCallback(int id,OnLoadSuccessCallback callback) {
        this.mId = id;
        this.callback = callback;
    }

    protected void onLoadSuccess(int size) {
        if (null != callback) {
            callback.onLoadSuccess(mId, size);
        }
    }
}
