package com.loyo.oa.v2.activityui.home.fragment;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.home.MainHomeActivity;
import com.loyo.oa.v2.customview.RoundImageView;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.LogUtil;

/**
 * 【侧边栏】fragment
 */
public class MenuFragment extends BaseFragment {
    private GestureDetector gesture; //手势识别
    private float minDistance = 120;//手势滑动最小距离
    private float minVelocity = 200;//手势滑动最小速度
    private LinearLayout ll_user, ll_pwd, ll_feed_back, ll__update, ll_version, ll_exit;
    private RoundImageView riv_head;
    private TextView tv_name, tv_member, tv_version_info;
    private ImageView iv_new_version;

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
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        onInit();
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
        ll_user = (LinearLayout) view.findViewById(R.id.ll_user);
        ll_pwd = (LinearLayout) view.findViewById(R.id.ll_pwd);
        ll_feed_back = (LinearLayout) view.findViewById(R.id.ll_feed_back);
        ll__update = (LinearLayout) view.findViewById(R.id.ll__update);
        ll_version = (LinearLayout) view.findViewById(R.id.ll_version);
        ll_exit = (LinearLayout) view.findViewById(R.id.ll_exit);
        riv_head = (RoundImageView) view.findViewById(R.id.riv_head);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_member = (TextView) view.findViewById(R.id.tv_member);
        tv_version_info = (TextView) view.findViewById(R.id.tv_version_info);
        iv_new_version = (ImageView) view.findViewById(R.id.iv_new_version);
        ll_user.setOnTouchListener(touch);
        ll_pwd.setOnTouchListener(touch);
        ll_feed_back.setOnTouchListener(touch);
        ll__update.setOnTouchListener(touch);
        ll_version.setOnTouchListener(touch);
        ll_exit.setOnTouchListener(touch);
    }

    long downTime = 0, upTime = 0;
    int moveIndex;
    View.OnTouchListener touch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            LogUtil.d(" 动作： " + event.getAction());
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downTime = System.currentTimeMillis();
                    v.setBackgroundColor(getResources().getColor(R.color.white10));
                    break;
                case MotionEvent.ACTION_MOVE:
                    moveIndex++;
                    break;
                case MotionEvent.ACTION_UP:
                    upTime = System.currentTimeMillis();
                    v.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    if ((upTime - downTime) < 90) {
                        onClickView(v);
                    }
                    LogUtil.d(downTime + " 事xx件mm查 " + downTime + " xxxx " + (upTime - downTime));
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
            case R.id.ll_user:
                Toast("fddnfg");
                break;
            case R.id.ll_pwd:
                break;
            case R.id.ll_feed_back:
                break;
            case R.id.ll__update:
                break;
            case R.id.ll_version:
                break;
            case R.id.ll_exit:
                Toast("退出");
                break;
        }

    }


    //设置手势识别监听器
    private class MyOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override//此方法必须重写且返回真，否则onFling不起效
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
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


}
