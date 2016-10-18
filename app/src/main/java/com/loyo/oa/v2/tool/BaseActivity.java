package com.loyo.oa.v2.tool;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.library.module.common.SystemBarTintManager;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.login.LoginActivity;
import com.loyo.oa.v2.activityui.other.model.User;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.activityui.other.model.User;
import com.loyo.oa.v2.common.DialogHelp;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customview.CustomProgressDialog;
import com.loyo.oa.v2.customview.SweetAlertDialogView;
import com.loyo.oa.v2.db.DBManager;

import org.greenrobot.eventbus.Subscribe;

import java.util.Locale;

/**
 * activity 基类
 */

public class BaseActivity extends Activity implements GestureDetector.OnGestureListener {

    protected MainApp app;
    protected boolean isNeedLogin = true;
    protected Context mContext;
    protected static final int NO_SCROLL = -1;
    public CustomProgressDialog customProgressDialog;
    public Intent rushTokenIntent;
    private int mTouchViewGroupId;
    private GestureDetector mDetector;
    public SweetAlertDialogView sweetAlertDialogView;

    /**
     * 搜索跳转分类
     */
    public static final int TASKS_ADD = 0X01;//新建任务 编辑任务
    public static final int TASKS_ADD_CUSTOMER = 0X10;//新建任务关联客户
    public static final int SIGNIN_ADD = 0X02;//新建拜访
    public static final int WORK_ADD = 0X03; //新建工作报告
    public static final int WFIN_ADD = 0X08;   //新建审批
    public static final int ATTENT_ADD = 0X11;   //考勤打卡
    public static final int SALE_ADD = 0X15;   //新建销售机会
    public static final int FOLLOW_ADD = 0X16;   //新建销售机会
    public static final int ORDER_ADD = 0X17;   //新建订单

    public static final int CUSTOMER_MANAGE = 0X04;//客户管理
    public static final int TASKS_MANAGE = 0X05;//任务管理
    public static final int WORK_MANAGE = 0X06;//工作报告管理
    public static final int PEOJECT_MANAGE = 0x07; //项目管理
    public static final int WFIN_MANAGE = 0x09; //审批列表
    public static final int CLUE_MANAGE = 0x20; //线索列表

    public SystemBarTintManager tintManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        app = (MainApp) getApplicationContext();
        mContext = this;
        mDetector = new GestureDetector(this, this);
        com.loyo.oa.v2.common.event.AppBus.getInstance().register(this);
        ExitActivity.getInstance().addActivity(this);
        if (customProgressDialog == null) {
            customProgressDialog = new CustomProgressDialog(this);
            customProgressDialog.setCancelable(false);
        }
        registerBaseReceiver();
        // 创建状态栏的管理实例
        tintManager = new SystemBarTintManager(this);
        // 激活状态栏设置
        tintManager.setStatusBarTintEnabled(true);
        // 激活导航栏设置
        tintManager.setNavigationBarTintEnabled(true);
        // 设置一个颜色给系统栏
        tintManager.setTintColor(getResources().getColor(R.color.title_bg1));
        sweetAlertDialogView = new SweetAlertDialogView(this);
    }

    @Override
    protected void onDestroy() {
        com.loyo.oa.v2.common.event.AppBus.getInstance().unregister(this);
        unRegisterBaseReceiver();
        //关闭键盘
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(findViewById(android.R.id.content).getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

        ExitActivity.getInstance().removeActivity(this);
        if (customProgressDialog != null && customProgressDialog.isShowing()) {
            customProgressDialog.dismiss();
            app.logUtil.d("onDestroy");
        }
        customProgressDialog = null;
        super.onDestroy();
    }

    @Subscribe
    public void onEvent(Object object){

    }

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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        app.logUtil.d(this.getClass().getName() + "-onSaveInstanceState():begin");

        super.onSaveInstanceState(outState);
        outState.putString("token", MainApp.getToken());
        outState.putSerializable("user", MainApp.user);

        app.logUtil.d(this.getClass().getName() + "-onSaveInstanceState():end");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        LogUtil.d(this.getClass().getName() + "-onRestoreInstanceState:begin");
        super.onRestoreInstanceState(savedInstanceState);

        if (StringUtil.isEmpty(MainApp.getToken())) {
            MainApp.setToken(savedInstanceState.getString("token"));
        }

        if (MainApp.user == null && savedInstanceState.containsKey("user")) {
            MainApp.user = (User) savedInstanceState.getSerializable("user");
        }

        LogUtil.d(this.getClass().getName() + "-onRestoreInstanceState:end");
    }

    @Override
    protected void onResume() {
        getWindow().getDecorView().setOnTouchListener(ViewUtil.OnTouchListener_softInput_hide.Instance());

        if (MainApp.user == null) {
            MainApp.user = DBManager.Instance().getUser();
        }

        /*强制设置系统语言为中文*/
        Locale locale = new Locale("zh");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, null);
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    protected void setTitle(String title) {
        ((TextView) findViewById(R.id.tv_title_1)).setText(title);
    }

    protected void setTitle(int id, String title) {
        ((TextView) findViewById(id)).setText(title);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, 0, null);
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

    public abstract class BaseActivityAsyncHttpResponseHandler extends BaseAsyncHttpResponseHandler {

        @Override
        public Activity getActivity() {
            return (Activity) mContext;
        }
    }

    public static class BaseHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            MainApp app = MainApp.getMainApp();
            app.logUtil.d("handleMessage msg.what:" + msg.what);
            switch (msg.what) {
                case FinalVariables.APP_RELOGIN:
                    Global.Toast(R.string.app_Session_invalidation);
                    app.toActivity(LoginActivity.class);
                    break;
                //                case FinalVariables.APP_CONNECT_SERVER_EXCEPTION:
                ////                    toast = Toast.makeText(MainApp.getMainApp().getBaseContext(), MainApp.getMainApp().getString(R.string.app_connectServerDataException), Toast.LENGTH_LONG);
                ////                    toast.setGravity(Gravity.CENTER, 0, 0);
                ////                    toast.show();
                //                    break;
                default:
                    break;
            }
        }
    }


    protected interface ConfirmDialogInterface {
        void Confirm();
    }

    //当控件Id>0的时候，是指定ViewGroup的ID
    // = 0的时候是Activity使用手势。
    // = -1的时候是Activity不使用手势。
    protected void setTouchView(int _touchViewGroupId) {
        if (_touchViewGroupId <= 0) {
            mTouchViewGroupId = _touchViewGroupId;
            return;
        }

        mTouchViewGroupId = _touchViewGroupId;

        ViewGroup vg = (ViewGroup) findViewById(mTouchViewGroupId);
        vg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mDetector.onTouchEvent(event);
            }
        });
    }


    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    /*页面左滑手指监听*/
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

//        if (e2.getX() - e1.getX() > Global.GetBackGestureLength()) {
//            //onBackPressed();
//        }

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isNeedLogin || mTouchViewGroupId == -1) {
            return super.onTouchEvent(event);
        }

        return mDetector.onTouchEvent(event);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        if (!isNeedLogin || mTouchViewGroupId == -1) {
            return super.dispatchTouchEvent(event);
        } else if (mDetector != null) {
            if (mDetector.onTouchEvent(event))
            //If the gestureDetector handles the event, a swipe has been executed and no more needs to be done.
            {
                return true;
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 关闭软键盘
     */
    public void hideInputKeyboard(EditText et) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }

    /**
     * 手动 显示软键盘
     */
    public void showInputKeyboard(EditText view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInputFromInputMethod(view.getWindowToken(), 0);
    }

    /**
     * 加载loading的方法
     */
    public void showLoading(String msg) {
        try {
            DialogHelp.showLoading(this, msg, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showLoading(String msg, boolean Cancelable) {
        DialogHelp.showLoading(this, msg, Cancelable);
    }

    public static void cancelLoading() {
        DialogHelp.cancelLoading();
    }

    /**
     * SweetAlertDialog关闭
     */
    public void dismissSweetAlert() {
        sweetAlertDialogView.sweetAlertDialog.dismiss();
    }

    /**
     * 重启当前Activity
     */
    public void restartActivity() {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }
}
