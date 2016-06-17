package com.loyo.oa.v2.activity.setting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.commonview.FeedbackActivity_;
import com.loyo.oa.v2.activity.contact.ContactInfoEditActivity_;
import com.loyo.oa.v2.activity.login.LoginActivity;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.db.DBManager;
import com.loyo.oa.v2.point.IUser;
import com.loyo.oa.v2.service.CheckUpdateService;
import com.loyo.oa.v2.service.InitDataService_;
import com.loyo.oa.v2.service.RushTokenService;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.ExitActivity;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.SharedUtil;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.tool.ViewUtil;

import java.util.HashSet;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 设置 页面
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener {

    public TextView tv_title_1, tv_version, tv_new_version;
    public ViewGroup layout_exit;
    public ViewGroup img_title_left;
    public ViewGroup layout_setpassword, layout_update, layout_feedback, layout_profile;
    public ViewGroup layout_check_update;
    public Intent mIntentCheckUpdate;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            if (null == intent)
                return;
            String action = intent.getAction();
            if (TextUtils.equals(action, FinalVariables.ACTION_DATA_CHANGE)) {
                Global.Toast("更新完成");
            }
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initUI();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter(FinalVariables.ACTION_DATA_CHANGE));
    }

    void initUI() {

        img_title_left = (ViewGroup) findViewById(R.id.img_title_left);
        layout_profile = (ViewGroup) findViewById(R.id.layout_profile);
        tv_title_1 = (TextView) findViewById(R.id.tv_title_1);
        tv_new_version = (TextView) findViewById(R.id.tv_new_version);
        layout_exit = (ViewGroup) findViewById(R.id.btn_setting_exit);
        layout_update = (ViewGroup) findViewById(R.id.layout_update);
        layout_feedback = (ViewGroup) findViewById(R.id.layout_feedback);
        layout_check_update = (ViewGroup) findViewById(R.id.layout_check_update);

        layout_profile.setOnTouchListener(Global.GetTouch());

        img_title_left.setOnClickListener(this);
        layout_exit.setOnClickListener(this);
        layout_update.setOnClickListener(this);
        layout_feedback.setOnClickListener(this);
        layout_check_update.setOnClickListener(this);
        layout_profile.setOnClickListener(this);

        ViewUtil.OnTouchListener_view_transparency touch = ViewUtil.OnTouchListener_view_transparency.Instance();

        layout_setpassword = (ViewGroup) findViewById(R.id.layout_setpassword);
        if (null != MainApp.user && !MainApp.user.isBQQ) {
            layout_setpassword.setOnClickListener(this);
            layout_setpassword.setOnTouchListener(touch);
        } else {
            layout_setpassword.setVisibility(View.GONE);
        }

        layout_exit.setOnTouchListener(touch);
        img_title_left.setOnTouchListener(touch);
        layout_update.setOnTouchListener(touch);
        layout_check_update.setOnTouchListener(touch);

        tv_version = (TextView) findViewById(R.id.tv_version);
        try {
            PackageInfo pi = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            tv_version.setText("当前版本" + pi.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            Global.ProcException(e);
        }
        tv_title_1.setText("设置");

        if (app.hasNewVersion) {
            tv_new_version.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, RESULT_CANCELED, null);
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                finish();
                break;
            case R.id.btn_setting_exit:
                exit();
                break;
            /*修改密码*/
            case R.id.layout_setpassword:
                app.startActivity(this, SettingPasswordActivity_.class, MainApp.ENTER_TYPE_RIGHT, false, null);
                break;
            /*更新组织架构*/
            case R.id.layout_update:
                rushHomeData();
                if (Utils.isNetworkAvailable(this)) {
                    Global.Toast("开始更新");
                    initService();
                } else {
                    Toast("请检查您的网络连接");
                }
                break;
            /*意见反馈*/
            case R.id.layout_feedback:
                app.startActivity(this, FeedbackActivity_.class, MainApp.ENTER_TYPE_RIGHT, false, null);
                break;
            case R.id.layout_check_update:
                if (PackageManager.PERMISSION_GRANTED ==
                        getPackageManager().checkPermission("android.permission.WRITE_EXTERNAL_STORAGE", "com.loyo.oa.v2")) {
                    mIntentCheckUpdate = new Intent(mContext, CheckUpdateService.class);
                    mIntentCheckUpdate.putExtra("EXTRA_TOAST", true);
                    startService(mIntentCheckUpdate);
                } else {
                    showGeneralDialog(true, true, "需要使用储存权限\n请在”设置”>“应用”>“权限”中配置权限");
                    generalPopView.setSureOnclick(new View.OnClickListener() {
                        @Override
                        public void onClick(final View view) {
                            generalPopView.dismiss();
                            Utils.doSeting(SettingActivity.this);
                        }
                    });
                    generalPopView.setCancelOnclick(new View.OnClickListener() {
                        @Override
                        public void onClick(final View view) {
                            generalPopView.dismiss();
                        }
                    });
                }
                break;
            /*编辑个人资料*/
            case R.id.layout_profile:
                updateUserinfp();
                break;
            default:

                break;
        }
    }

    /**
     * 刷新首页红点数据
     */
    void rushHomeData() {
        RestAdapterFactory.getInstance().build(FinalVariables.RUSH_HOMEDATA).create(IUser.class).rushHomeDate(new RCallback<User>() {
            @Override
            public void success(final User user, final Response response) {
                HttpErrorCheck.checkResponse(response);
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
            }
        });
    }

    /**
     * 获取个人资料
     */
    void updateUserinfp() {
        showLoading("");
        RestAdapterFactory.getInstance().build(FinalVariables.GET_PROFILE).create(IUser.class).getProfile(new RCallback<User>() {
            @Override
            public void success(final User user, final Response response) {
                HttpErrorCheck.checkResponse("获取个人资料修改", response);
                String json = MainApp.gson.toJson(user);
                MainApp.user = user;
                DBManager.Instance().putUser(json);

                Bundle b = new Bundle();
                b.putSerializable("user", MainApp.user);
                app.startActivity(SettingActivity.this, ContactInfoEditActivity_.class, MainApp.ENTER_TYPE_RIGHT, false, b);
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
            }
        });
    }

    /**
     * 更新 组织架构
     */
    void initService() {
        InitDataService_.intent(mContext).start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mIntentCheckUpdate != null) {
            try {
                stopService(mIntentCheckUpdate);
            } catch (Exception ex) {
                Global.ProcException(ex);
            }
        }
    }

    void exit() {
        //清楚token与用户资料
        MainApp.setToken(null);
        MainApp.user = null;
        stopService(rushTokenIntent);
        RushTokenService.cancelJc();

        //清楚本地登录状态
        SharedUtil.clearInfo(mContext);
        JPushInterface.stopPush(app);
        Set<String> complanTag = new HashSet<>();
        JPushInterface.setAliasAndTags(getApplicationContext(), "", complanTag, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {
                LogUtil.d("激光推送已经成功停止（注销）状态" + i);
                //设置别名 为空
            }
        });
        ExitActivity.getInstance().finishAllActivity();
        app.startActivity(this, LoginActivity.class, MainApp.ENTER_TYPE_BUTTOM, true, null);
    }
}
