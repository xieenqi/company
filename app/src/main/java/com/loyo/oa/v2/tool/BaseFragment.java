package com.loyo.oa.v2.tool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.widget.Toast;

import com.loyo.oa.v2.activity.project.ProjectInfoActivity;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.DialogHelp;

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

        //        if (activity instanceof CityBaseActivity){
        //            mActivity = (CityBaseActivity)activity;
        //            app = mActivity.app;
        ////            mActivity.app.logUtil;
        //        } else {
        //            //TODO:提示错误
        //            Toast.makeText(this.getActivity(),"This is not CityBaseActivity!",Toast.LENGTH_SHORT).show();
        //        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
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

    public void setCallback(int id, OnLoadSuccessCallback callback) {
        this.mId = id;
        this.callback = callback;
    }

    protected void onLoadSuccess(int size) {
        if (null != callback) {
            callback.onLoadSuccess(mId, size);
        }
    }

    //加载loading的方法
    public void showLoading(String msg) {
        DialogHelp.showLoading(getActivity(), msg, true);
    }

    public void showLoading(String msg, boolean Cancelable) {
        DialogHelp.showLoading(getActivity(), msg, Cancelable);
    }

    public static void cancelLoading() {
        DialogHelp.cancelLoading();
    }


    int mTouchViewGroupId;

//    //当控件Id>0的时候，是指定ViewGroup的ID
//    // = 0的时候是Activity使用手势。
//    // = -1的时候是Activity不使用手势。
//    protected void setTouchView(int _touchViewGroupId) {
//        final GestureDetector mDetector = new GestureDetector(new GestureDetector.OnGestureListener() {
//            @Override
//            public boolean onDown(MotionEvent e) {
//                return false;
//            }
//
//            @Override
//            public void onShowPress(MotionEvent e) {
//
//            }
//
//            @Override
//            public boolean onSingleTapUp(MotionEvent e) {
//                return false;
//            }
//
//            @Override
//            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//                return false;
//            }
//
//            @Override
//            public void onLongPress(MotionEvent e) {
//
//            }
//
//            @Override
//            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//                return false;
//            }
//        });
//        if (_touchViewGroupId <= 0) {
//            mTouchViewGroupId = _touchViewGroupId;
//            return;F
//        }
//
//        mTouchViewGroupId = _touchViewGroupId;
//
//        ViewGroup vg = (ViewGroup) getActivity().findViewById(mTouchViewGroupId);
//        vg.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return mDetector.onTouchEvent(event);
//            }
//        });
//    }

}
