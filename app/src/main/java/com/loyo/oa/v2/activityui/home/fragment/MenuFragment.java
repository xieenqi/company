package com.loyo.oa.v2.activityui.home.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.common.utils.PermissionTool;
import com.loyo.oa.common.utils.UmengAnalytics;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.commonview.FeedbackActivity_;
import com.loyo.oa.v2.activityui.contact.ContactInfoEditActivity_;
import com.loyo.oa.v2.activityui.home.MainHomeActivity;
import com.loyo.oa.v2.activityui.login.LoginActivity;
import com.loyo.oa.v2.activityui.other.model.User;
import com.loyo.oa.v2.activityui.setting.SettingActivity;
import com.loyo.oa.v2.activityui.setting.SystemMessageActivity;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customview.RoundImageView;
import com.loyo.oa.v2.db.DBManager;
import com.loyo.oa.v2.db.OrganizationManager;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.permission.Permission;
import com.loyo.oa.v2.service.CheckUpdateService;
import com.loyo.oa.v2.service.InitDataService_;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.ExitActivity;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.SharedUtil;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.user.api.UserService;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashSet;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * 【侧边栏】fragment
 */
public class MenuFragment extends BaseFragment {
    private static final int REQUEST_WRITE=0x1;// 申请sd写权限
    private GestureDetector gesture; //手势识别
    private float minDistance = 120;//手势滑动最小距离
    private float minVelocity = 200;//手势滑动最小速度
    private LinearLayout ll_system, ll_feed_back, ll__update, ll_version, ll_exit, ll_setting, ll_head;
    private RoundImageView riv_head;
    private TextView tv_name, tv_member, tv_version_info, tv_file;
    private ImageView iv_new_version;
    public static ExitAppCallback callback;
    private Intent mIntentCheckUpdate;
    private boolean isExite = false;
    //个人信息 和版本信息
    private BroadcastReceiver userInfoAndVersionInfo = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String info = intent.getStringExtra(ExtraAndResult.EXTRA_DATA);
            if ("version".equals(info)) {
                if (app.hasNewVersion) {
                    iv_new_version.setVisibility(View.VISIBLE);
                }
            } else if ("user".equals(info)) {
                User user = MainApp.user;
                if (null != user) {
                    if (null != user.avatar && null != riv_head) {
                        ImageLoader.getInstance().displayImage(MainApp.user.avatar, riv_head);
                    }
                    tv_name.setText(user.getRealname());
                    tv_member.setText(user.depts.get(0).getShortDept().getName() + " | " + user.depts.get(0).getTitle());
                }
            } else if ("exite".equals(info) && !isExite) {
                exit();
                isExite = true;
            }
        }
    };

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_home_menu, container,
                false);
        //根据父窗体getActivity()为fragment设置手势识别
        gesture = new GestureDetector(this.getActivity(), new MyOnGestureListener());
        container.setOnTouchListener(new View.OnTouchListener() {
                                         @Override
                                         public boolean onTouch(View v, MotionEvent event) {
                                             return gesture.onTouchEvent(event);//返回手势识别触发的事件
                                         }
                                     }

        );
//        如果Fragment里面有ScrollView，而且其中还包含子控件，则需要再为ScrollView里面的子控件单独设置setOnTouchListener，
// 设置和view一样，因为ScrollView的触碰事件会先响应，而里面的子控件的触碰事件则不会再响应了

        //注册拉去组织架构的广播
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(userInfoAndVersionInfo, new IntentFilter(ExtraAndResult.ACTION_USER_VERSION));
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private HomeApplicationFragment mHomeApplicationFragment;

    private String makeFragmentName(int viewId, int index) {
        return "android:switcher:" + viewId + ":" + index;
    }

    /**
     * 关闭侧边栏
     */
    void close() {
        String name = makeFragmentName(MainHomeActivity.index, 0);
        mHomeApplicationFragment = (HomeApplicationFragment) getFragmentManager()
                .findFragmentByTag(name);
        String a;
        if (mHomeApplicationFragment != null) {
            a = mHomeApplicationFragment.getChangeEvent();
            if ("All".equals(a)) {
                mHomeApplicationFragment.gotoShiftEvent(2);
                mHomeApplicationFragment.setChangeEvent("Near");

            } else {
                mHomeApplicationFragment.gotoShiftEvent(1);
                mHomeApplicationFragment.setChangeEvent("All");
            }
        }
        ((MainHomeActivity) getActivity()).togggle();
    }

    private void initView(View view) {
        ll_head = (LinearLayout) view.findViewById(R.id.ll_head);
        ll_system = (LinearLayout) view.findViewById(R.id.ll_system);
        ll_feed_back = (LinearLayout) view.findViewById(R.id.ll_feed_back);
        ll__update = (LinearLayout) view.findViewById(R.id.ll__update);
        ll_version = (LinearLayout) view.findViewById(R.id.ll_version);
        ll_exit = (LinearLayout) view.findViewById(R.id.ll_exit);
        ll_setting = (LinearLayout) view.findViewById(R.id.ll_setting);
        riv_head = (RoundImageView) view.findViewById(R.id.riv_head);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_member = (TextView) view.findViewById(R.id.tv_member);
        tv_version_info = (TextView) view.findViewById(R.id.tv_version_info);
        iv_new_version = (ImageView) view.findViewById(R.id.iv_new_version);
        tv_file = (TextView) view.findViewById(R.id.tv_file);
        ll_head.setOnTouchListener(touch);
        ll_system.setOnTouchListener(touch);
        ll_feed_back.setOnTouchListener(touch);
        ll__update.setOnTouchListener(touch);
        ll_version.setOnTouchListener(touch);
        ll_exit.setOnTouchListener(touch);
        ll_setting.setOnTouchListener(touch);
        try {
            PackageInfo pi = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            tv_version_info.setText("(当前v" + pi.versionName + ")");
        } catch (PackageManager.NameNotFoundException e) {
            Global.ProcException(e);
        }

        callback = new ExitAppCallback() {
            @Override
            public void onExit(Activity SettingActivity) {
                exit();
            }
        };
    }

    float downX = 0, upX = 0;
    View.OnTouchListener touch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downX = event.getX();
                    v.setBackgroundColor(getResources().getColor(R.color.white10));
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    upX = event.getX();
                    v.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    if (Math.abs(upX - downX) < 50) {
                        onClickView(v);
                    }
                    LogUtil.d(downX + " 事xx件mm查 " + downX + " xxxx " + (upX - downX));
                    break;
            }
            return gesture.onTouchEvent(event);//返回手势识别触发的事件
        }
    };

    /**
     * 拦截触摸事件 的ACTION_UP实现点击事件
     *
     * @param v
     */
    private void onClickView(View v) {
        switch (v.getId()) {
            //个人资料
            case R.id.ll_head:
                updateUserinfo();
                UmengAnalytics.umengSend(mActivity, UmengAnalytics.LeftNavBarInformationButton);
                break;
            //系统消息
            case R.id.ll_system:
                app.startActivity(getActivity(), SystemMessageActivity.class, MainApp.ENTER_TYPE_RIGHT, false, null);
                UmengAnalytics.umengSend(mActivity, UmengAnalytics.LeftNavBar_notice_button);
                break;
            //意见反馈
            case R.id.ll_feed_back:
                app.startActivity(getActivity(), FeedbackActivity_.class, MainApp.ENTER_TYPE_RIGHT, false, null);
                UmengAnalytics.umengSend(mActivity, UmengAnalytics.LeftNavBarFeedbackButton);
                break;
            //检查更新
            case R.id.ll_version:
                /**
                 * 判断sd卡读写权限，检查升级
                 */
                if(PermissionTool.requestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE,"需要使用储存权限",REQUEST_WRITE)){
                    toUpdate();
                }
                break;
            //设置
            case R.id.ll_setting:
                app.startActivity(mActivity, SettingActivity.class, MainApp.ENTER_TYPE_RIGHT, false, null);
                UmengAnalytics.umengSend(mActivity, UmengAnalytics.LeftNavBar_option_button);
                break;
        }

    }

    /**
     * 升级检测
     */
    private void toUpdate(){
        mIntentCheckUpdate = new Intent(getActivity(), CheckUpdateService.class);
        mIntentCheckUpdate.putExtra("EXTRA_TOAST", true);
        getActivity().startService(mIntentCheckUpdate);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(REQUEST_WRITE==requestCode){
            PermissionTool.requestPermissionsResult(permissions, grantResults, new PermissionTool.PermissionsResultCallBack() {
                @Override
                public void success() {
                    toUpdate();
                }
                @Override
                public void fail() {
                    Toast("你拒绝了所需权限，不能完成操作");
                }
            });
        }
    }

    /**
     * 获取个人资料
     */
    void updateUserinfo() {
        showLoading2("");
        UserService.getProfile()
                .subscribe(new DefaultLoyoSubscriber<User>(hud) {
                    @Override
                    public void onNext(User user) {
                        String json = MainApp.gson.toJson(user);
                        MainApp.user = user;
                        DBManager.Instance().putUser(json);
                        Bundle b = new Bundle();
                        String userId = MainApp.user.id;
                        b.putSerializable("userId", userId != null ? userId : "");
                        app.startActivity(getActivity(), ContactInfoEditActivity_.class, MainApp.ENTER_TYPE_RIGHT, false, b);
                    }
                });
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mIntentCheckUpdate != null) {
            try {
                getActivity().stopService(mIntentCheckUpdate);
            } catch (Exception ex) {
                Global.ProcException(ex);
            }
        }
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(userInfoAndVersionInfo);
    }

    //设置手势识别监听器
    private class MyOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override//此方法必须重写且返回真，否则onFling不起效
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            LogUtil.d("滑动速度：" + velocityX);
            if ((e1.getX() - e2.getX() > minDistance) && Math.abs(velocityX) > minVelocity) {
                close();
                return true;
            } else if ((e2.getX() - e1.getX() > minDistance) && Math.abs(velocityX) > minVelocity) {
                Toast("滑到底啦~(≧▽≦)~啦");
                return true;
            }
            return false;
        }
    }

    public interface ExitAppCallback {
        void onExit(Activity SettingActivity);
    }

    void exit() {
        Set<String> complanTag = new HashSet<>();
        JPushInterface.setAliasAndTags(app, "", complanTag, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {
                LogUtil.d("激光推送已经成功停止（注销）状态" + i);
                JPushInterface.stopPush(app);
            }
        });
        MainApp.setToken(null);//清楚token与用户资料
        InitDataService_.intent(app).stop();//避免后台多次调用接口 没有token 导致accst_token无效 的问题
        MainApp.user = null;
        ImageLoader.getInstance().clearDiskCache();//清除本地磁盘缓存
        /* 清空组织架构 */
        OrganizationManager.clearOrganizationData();
        SharedUtil.clearInfo(app);//清楚本地登录状态 即缓存信息
        /* 欢迎提示不清除 */
        SharedUtil.putBoolean(mActivity, ExtraAndResult.WELCOM_KEY, true);
        ExitActivity.getInstance().finishAllActivity();
        app.startActivity(mActivity, LoginActivity.class, MainApp.ENTER_TYPE_RIGHT, true, null);
    }


}
