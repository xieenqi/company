package com.loyo.oa.v2.activity.home;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.home.cusview.SlidingMenu;
import com.loyo.oa.v2.activity.home.fragment.HomeFragment;
import com.loyo.oa.v2.activity.home.fragment.MenuFragment;
import com.loyo.oa.v2.activity.home.fragment.SlidingFragmentActivity;

/**
 * 带侧滑的主界面
 * <p/>
 * Created by xeq on 16/6/15.
 */
public class ActivityMainHome extends SlidingFragmentActivity {
    private SlidingMenu sm;
    private Fragment selectCurrentFragment;// 当前显示的fragment的标记
    private int selectIndex = 0;
    private MenuFragment menuFragment;
    private long exitTime = 0;
    private HomeFragment mHomeFragment;
    //主要保存当前显示的是第几个fragment的索引值
    public static int index = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);
        onInitSlideMenu();
    }

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
        sm.setSlidingEnabled(true);
        // 控制是否slidingmenu可开有滑动手势。
        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        // 设置阴影宽度。
        sm.setShadowWidthRes(R.dimen.dimen_15);
        sm.setShadowDrawable(R.drawable.shadow);

        menuFragment = new MenuFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.left_menu_frame, menuFragment).commit();
        // 偏移
        // sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        sm.setBehindOffset(metric.widthPixels / 2 - 200);
        sm.setBehindScrollScale(0);
        // 设置多少进出slidingmenu消失
        sm.setFadeDegree(0.25f);
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

    public void changeFragment(int flag, int index) {
        selectIndex = index;
        switch (flag) {
            case R.id.change_activity:
                if (mHomeFragment == null) {
                    mHomeFragment = new HomeFragment();
                }
                changeContent(mHomeFragment);
                break;
            default:
                break;
        }
    }

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

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        // TODO Auto-generated method stub
//        if (keyCode == KeyEvent.KEYCODE_BACK
//                && event.getAction() == KeyEvent.ACTION_DOWN) {
//            if (selectIndex != 0) {
//                if (mHomeFragment == null) {
//                    mHomeFragment = new HomeFragment();
//                }
//                changeContent(mHomeFragment);
//                selectIndex = 0;
//            } else {
//                if ((System.currentTimeMillis() - exitTime) > 2000) {
//                    Toast.makeText(MainActivity.this, "再按一次退出程序",
//                            Toast.LENGTH_SHORT).show();
//                    exitTime = System.currentTimeMillis();
//                } else {
//                    finish();
//                }
//            }
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        System.out.println("!!!!222222222!!!!!!!!!!onStart");
    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();
        System.out.println("!!!22222222222!!!!onRestart");
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        System.out.println("!!!!!!!2222222222222222!!!!!!!onPause");
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        System.out.println("!!!!!222222222!!!!!!!!!onStop");
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        System.out.println("!!!!!!22222222!!!!!!!!onDestroy");
    }

}
