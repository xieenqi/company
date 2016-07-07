package com.loyo.oa.v2.activityui.home.fragment;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.home.MainHomeActivity;
import com.loyo.oa.v2.tool.BaseFragment;

/**
 * 【侧边栏】fragment
 */
public class MenuFragment extends BaseFragment {
    private Button vButton;
    private float mPosX, mPosY, mCurPosX, mCurPosY;

    private GestureDetector gesture; //手势识别

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_home_menu, container,
                false);
        vButton = (Button) view.findViewById(R.id.change_activity);
        //根据父窗体getActivity()为fragment设置手势识别
        gesture = new GestureDetector(this.getActivity(), new MyOnGestureListener());
        view.setOnTouchListener(new View.OnTouchListener() {
                                    @Override
                                    public boolean onTouch(View v, MotionEvent event) {

                                        return gesture.onTouchEvent(event);//返回手势识别触发的事件
                                    }
                                }

        );
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onInit();
    }

    private HomeApplicationFragment mHomeApplicationFragment;

    private String makeFragmentName(int viewId, int index) {
        return "android:switcher:" + viewId + ":" + index;
    }

    private void onInit() {
        vButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //得到fragment的名字只限于FragmentPagerAdapter里面的
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
        });
    }

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

    //设置手势识别监听器
    private class MyOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override//此方法必须重写且返回真，否则onFling不起效
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if ((e1.getX() - e2.getX() > 120) && Math.abs(velocityX) > 200) {
                close();
                return true;
            } else if ((e2.getX() - e1.getX() > 120) && Math.abs(velocityX) > 200) {
//                Toast("优化 ");
                return true;
            }
            return false;
        }
    }

}
