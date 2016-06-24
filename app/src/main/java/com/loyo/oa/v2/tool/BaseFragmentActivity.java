package com.loyo.oa.v2.tool;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.common.DialogHelp;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.SystemBarTintManager;
import com.loyo.oa.v2.db.DBManager;
import com.loyo.oa.v2.ui.customview.GeneralPopView;

public class BaseFragmentActivity extends FragmentActivity {

    protected MainApp app;
    protected Context mContext;
    private Toast mCurrentToast;
    private int mTouchViewGroupId = -1;
    public GeneralPopView generalPopView;
    final String Tag = "BaseFragmentActivity";


    protected BroadcastReceiver baseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null == intent) {
                return;
            }
            String action = intent.getAction();
            if (TextUtils.equals(action, ConnectivityManager.CONNECTIVITY_ACTION)) {
                ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo info = manager.getActiveNetworkInfo();
                if (null != info && info.isAvailable() && info.isConnected()) {
                    onNetworkChanged(true);
                } else {
                    onNetworkChanged(false);
                }
            }
        }
    };

    /**
     * 网络状态变化回调方法
     *
     * @param available
     */
    protected void onNetworkChanged(boolean available) {

    }

    /**
     * 注册基类广播
     */
    protected void registerBaseReceiver() {
    }

    /**
     * 解除注册基类广播
     */
    protected void unRegisterBaseReceiver() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (MainApp) getApplicationContext();
        mContext = this;
        registerBaseReceiver();
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        ExitActivity.getInstance().addActivity(this);
        // 创建状态栏的管理实例
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        // 激活状态栏设置
        tintManager.setStatusBarTintEnabled(true);
        // 激活导航栏设置
        tintManager.setNavigationBarTintEnabled(true);
        // 设置一个颜色给系统栏
        tintManager.setTintColor(Color.parseColor("#2c9dfc"));
    }

    @Override
    protected void onDestroy() {
        unRegisterBaseReceiver();
        //关闭键盘
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(findViewById(android.R.id.content).getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

        ExitActivity.getInstance().removeActivity(this);

        super.onDestroy();
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("token", MainApp.getToken());
        outState.putSerializable("user", MainApp.user);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        LogUtil.d(this.getClass().getName() + "-恢复实例状态: 开始");
        super.onRestoreInstanceState(savedInstanceState);

        if (StringUtil.isEmpty(MainApp.getToken())) {
            MainApp.setToken(savedInstanceState.getString("token"));
        }

        if (MainApp.user == null && savedInstanceState.containsKey("user")) {
            MainApp.user = (User) savedInstanceState.getSerializable("user");
        }

        LogUtil.d(this.getClass().getName() + "-恢复实例状态: 完成");
    }

    protected void setTitle(String title) {
        ((TextView) findViewById(R.id.tv_title_1)).setText(title);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        getWindow().getDecorView().setOnTouchListener(ViewUtil.OnTouchListener_softInput_hide.Instance());

        if (MainApp.user == null) {
            LogUtil.d(" 用户为空 ");
            MainApp.user = DBManager.Instance().getUser();
        }

        super.onResume();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
        }
        return true;
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {

            /*获取手指触点左边*/
            case MotionEvent.ACTION_DOWN:
                xDistance = 0f;
                yDistance = 0f;
                xLast = event.getX();
                yLast = event.getY();
                break;

            /*禁用左滑finish*/
            case MotionEvent.ACTION_MOVE:
/*              final float curX = event.getX();
                final float curY = event.getY();
                xDistance += Math.abs(curX - xLast);
                yDistance += Math.abs(curY - yLast);
                if (curX > xLast && xDistance > yDistance && xDistance > 180) {
                    app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, RESULT_CANCELED, null);
                }
                xLast = curX;
                yLast = curY;*/
        }

        return super.dispatchTouchEvent(event);
    }


    public void setTouchView(int _touchViewGroupId) {
        mTouchViewGroupId = _touchViewGroupId;
        if (mTouchViewGroupId <= 0) {

            return;
        }

        ViewGroup vg = (ViewGroup) findViewById(mTouchViewGroupId);
        vg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        xDistance = 0f;
                        yDistance = 0f;
                        xLast = event.getX();
                        yLast = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        final float curX = event.getX();
                        final float curY = event.getY();

                        xDistance += Math.abs(curX - xLast);
                        yDistance += Math.abs(curY - yLast);

                        if (xDistance > Global.GetBackGestureLength()) {
                            onBackPressed();

                        }
                        xLast = curX;
                        yLast = curY;
                }
                return true;
            }
        });
    }

    // 滑动距离及坐标
    private float xDistance, yDistance, xLast, yLast;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    protected void Toast(String msg) {
        if (null != mCurrentToast) {
            mCurrentToast.cancel();
        }

        mCurrentToast = Toast.makeText(app.getBaseContext(), msg, Toast.LENGTH_SHORT);
        mCurrentToast.setGravity(Gravity.CENTER, 0, 0);
        mCurrentToast.show();
    }

    /**
     * 通用提示弹出框init
     */
    public void showGeneralDialog(boolean isOut, boolean isKind, String message) {
        generalPopView = new GeneralPopView(this, isKind);
        generalPopView.show();
        generalPopView.setMessage(message);
        generalPopView.setCanceledOnTouchOutside(isOut);
    }

    /*重启当前Activity*/
    public void restartActivity() {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }

    /**
     * 加载loading的方法
     */
    public void showLoading(String msg) {
        DialogHelp.showLoading(this, msg, true);
    }

    public void showLoading(String msg, boolean Cancelable) {
        DialogHelp.showLoading(this, msg, Cancelable);
    }

    public static void cancelLoading() {
        DialogHelp.cancelLoading();
    }
}
