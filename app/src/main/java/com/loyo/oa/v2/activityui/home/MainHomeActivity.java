package com.loyo.oa.v2.activityui.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.widget.Toast;
import com.loyo.oa.upload.alioss.AliOSSManager;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.CustomerDetailInfoActivity_;
import com.loyo.oa.v2.activityui.discuss.HaitMyActivity;
import com.loyo.oa.v2.activityui.home.cusview.SlidingMenu;
import com.loyo.oa.v2.activityui.home.fragment.HomeFragment;
import com.loyo.oa.v2.activityui.home.fragment.MenuFragment;
import com.loyo.oa.v2.activityui.home.slidingmenu.SlidingFragmentActivity;
import com.loyo.oa.v2.activityui.login.LoginActivity;
import com.loyo.oa.v2.activityui.order.OrderDetailActivity;
import com.loyo.oa.v2.activityui.other.BulletinManagerActivity_;
import com.loyo.oa.v2.activityui.other.model.User;
import com.loyo.oa.v2.activityui.project.ProjectInfoActivity_;
import com.loyo.oa.v2.activityui.tasks.TasksInfoActivity_;
import com.loyo.oa.v2.activityui.wfinstance.WfinstanceInfoActivity_;
import com.loyo.oa.v2.activityui.work.WorkReportsInfoActivity_;
import com.loyo.oa.v2.activityui.worksheet.WorksheetDetailActivity;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.db.OrganizationManager;
import com.loyo.oa.v2.service.CheckUpdateService;
import com.loyo.oa.v2.service.InitDataService_;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.voip.VoIPManager;
import com.umeng.analytics.MobclickAgent;
import com.yzx.api.UCSService;
import org.greenrobot.eventbus.Subscribe;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * 带侧滑的【主界面】
 * <p/>
 * Created by xeq on 16/6/15.
 */
public class MainHomeActivity extends SlidingFragmentActivity {

    private SlidingMenu sm;
    private Fragment selectCurrentFragment;// 当前显示的fragment的标记
    private int selectIndex = 0;
    private MenuFragment menuFragment;
    private HomeFragment mHomeFragment;
    //主要保存当前显示的是第几个fragment的索引值
    public static int index = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);
        fullStatusBar(false);
        if (savedInstanceState != null) {
            String FRAGMENTS_TAG = "Android:support:fragments";
            savedInstanceState.remove(FRAGMENTS_TAG);
        }
        onInitSlideMenu();
        tintManager.setTintColor(Color.parseColor("#33000000"));
                /* 初始化AliOSSManager */
        AliOSSManager.getInstance().initWithContext(getApplicationContext());
        OrganizationManager.shareManager().loadOrganizitionDataToMemoryCache();
    }


    @Override
    protected void onStart() {
        super.onStart();
        //判断登陆是否失效
        if (MainApp.user == null || TextUtils.isEmpty(MainApp.user.id)) {
            if (StringUtil.isEmpty(MainApp.getToken())) {
                Toast.makeText(this, "您的登陆已经失效,请重新登陆!", Toast.LENGTH_SHORT).show();
                MainApp.getMainApp().startActivity(this, LoginActivity.class, MainApp.ENTER_TYPE_LEFT, true, null);
                return;
            }
        }

        startService(new Intent(this, InitDataService_.class));
        permissionLocation();
    }

    @Subscribe
    public void onUserChanged(User user) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!UCSService.isConnected()) {
                    VoIPManager.getInstance().connectVoipServer(null);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        intentJpushInfo();
        MobclickAgent.onResume(this);
    }

    /**
     * 初始化侧边栏
     */
    private void onInitSlideMenu() {
        // 获取屏幕分辨率
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        mHomeFragment = new HomeFragment();
        selectCurrentFragment = mHomeFragment;
        // 更新Fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, mHomeFragment, "MainActivity")
                .commit();

        sm = getSlidingMenu();
        // 侧滑显示左边
        sm.setMode(SlidingMenu.LEFT);
        setBehindContentView(R.layout.fragment_home_left_menu);
        // 控制是否slidingmenu可开有滑动手势。
        sm.setSlidingEnabled(true);
        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);// TOUCHMODE_NONE
        // 设置阴影宽度。
        sm.setShadowWidthRes(R.dimen.dimen_15);
        sm.setShadowDrawable(R.drawable.shadow);

        menuFragment = new MenuFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.left_menu_frame, menuFragment).commit();
        // 偏移
        sm.setBehindOffsetRes(R.dimen.dimen_10);
        sm.setBehindOffset(metric.widthPixels / 7);
        sm.setBehindScrollScale(0);
        // 设置多少进出slidingmenu消失
        sm.setFadeDegree(0.25f);
        sm.setOnOpenListener(new SlidingMenu.OnOpenListener() {
            @Override
            public void onOpen() {
                mHomeFragment.setHeadVisibility(true);
            }
        });
        sm.setOnCloseListener(new SlidingMenu.OnCloseListener() {
            @Override
            public void onClose() {
                mHomeFragment.setHeadVisibility(false);
            }
        });
    }

    //打开侧滑
    public void togggle() {
        sm.toggle();
    }

    //拦截侧滑
    public void gotoStop() {
        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
    }

    //开始侧滑
    public void gotoStart() {
        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
    }

//    public void changeFragment(int flag, int index) {
//        selectIndex = index;
//        switch (flag) {
//            case R.id.change_activity:
//                if (mHomeFragment == null) {
//                    mHomeFragment = new HomeFragment();
//                }
//                changeContent(mHomeFragment);
//                break;
//            default:
//                break;
//        }
//    }

    private void changeContent(Fragment fragment) {
        if (selectCurrentFragment != fragment) {
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction();
            if (!fragment.isAdded())// 先判断是不是已经加到里面去了，加进去过的话，就隐藏，否则就创建新的，隐藏之前的那个fragment
            {
                transaction.hide(selectCurrentFragment)
                        .add(R.id.content_frame, fragment)
                        .commitAllowingStateLoss();
            } else {
                transaction.hide(selectCurrentFragment).show(fragment)
                        .commitAllowingStateLoss();
            }
            selectCurrentFragment = fragment;
        }
        getSlidingMenu().showContent();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (selectIndex != 0) {
                if (mHomeFragment == null) {
                    mHomeFragment = new HomeFragment();
                }
                changeContent(mHomeFragment);
                selectIndex = 0;
            } else {
                onBackPressed();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onNetworkChanged(boolean available) {
        super.onNetworkChanged(available);
    }

    /**
     * 检查定位权限是否打开
     */
    private void permissionLocation() {
        if (PackageManager.PERMISSION_GRANTED ==
                getPackageManager().checkPermission("android.permission.ACCESS_FINE_LOCATION", "com.loyo.oa.v2")) {
        } else {
            ActivityCompat.requestPermissions(MainHomeActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
        if (PackageManager.PERMISSION_GRANTED ==
                getPackageManager().checkPermission("android.permission.RECORD_AUDIO", "com.loyo.oa.v2")) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    1);
        }
    }


    /**
     * 激光推送要跳转 的 页面
     * buzzType 1，任务2，报告3，审批 4，项目  5，通知公告
     */
    public void intentJpushInfo() {
        if (null != MainApp.jpushData) {
            Intent intent = new Intent();
            if ("discuss".equals(MainApp.jpushData.operationType)) {
                intent.setClass(MainHomeActivity.this, HaitMyActivity.class);//推送讨论
                startActivity(intent);
                MainApp.jpushData = null;
                return;
            } else if ("delete".equals(MainApp.jpushData.operationType)) {
                MainApp.jpushData = null;
                return;
            }
            switch (MainApp.jpushData.buzzType) {
                case 1:
                    intent.setClass(MainHomeActivity.this, TasksInfoActivity_.class);
                    intent.putExtra(ExtraAndResult.EXTRA_ID, MainApp.jpushData.buzzId);
                    startActivity(intent);
                    MainApp.jpushData = null;
                    break;
                case 2:
                    intent.setClass(MainHomeActivity.this, WorkReportsInfoActivity_.class);
                    intent.putExtra(ExtraAndResult.EXTRA_ID, MainApp.jpushData.buzzId);
                    startActivity(intent);
                    MainApp.jpushData = null;
                    break;
                case 3:
                    intent.setClass(MainHomeActivity.this, WfinstanceInfoActivity_.class);
                    intent.putExtra(ExtraAndResult.EXTRA_ID, MainApp.jpushData.buzzId);
                    startActivity(intent);
                    MainApp.jpushData = null;
                    break;
                case 4:
                    intent.setClass(MainHomeActivity.this, ProjectInfoActivity_.class);
                    intent.putExtra("projectId", MainApp.jpushData.buzzId);
                    startActivity(intent);
                    MainApp.jpushData = null;
                    break;
                case 5://通知公告
                    intent.setClass(MainHomeActivity.this, BulletinManagerActivity_.class);
                    startActivity(intent);
                    MainApp.jpushData = null;
                    break;
                case 6://客户详情
                    intent.setClass(MainHomeActivity.this, CustomerDetailInfoActivity_.class);
                    intent.putExtra("Id", MainApp.jpushData.buzzId);
                    intent.putExtra(ExtraAndResult.EXTRA_TYPE, 1);//默认我的客户
                    startActivity(intent);
                    MainApp.jpushData = null;
                    break;
                case 16://订单详情
                    intent.setClass(MainHomeActivity.this, OrderDetailActivity.class);
//              mIntent.putExtra(ExtraAndResult.IS_TEAM, false);
                    intent.putExtra(ExtraAndResult.EXTRA_ID, MainApp.jpushData.buzzId);
                    startActivity(intent);
                    MainApp.jpushData = null;
                    break;
                case 18://工单 事件相关都跳转到 工单详情
                case 19:
                    intent.setClass(MainHomeActivity.this, WorksheetDetailActivity.class);
                    intent.putExtra(ExtraAndResult.EXTRA_ID, MainApp.jpushData.buzzId);
                    startActivity(intent);
                    MainApp.jpushData = null;
                    break;
                default:
                    break;
            }
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 退出应用
     */
    @Override
    public void onBackPressed() {
        if (sm.isMenuShowing()) {
            sm.showMenu(false);
            return;
        }

        sweetAlertDialogView.alertHandle(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                dismissSweetAlert();
            }
        }, new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                dismissSweetAlert();
                //android 5.0以后不能隐式启动或关闭服务
                stopService(new Intent(mContext, CheckUpdateService.class));
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        }, "提示", getString(R.string.app_exit_message));
    }
}
