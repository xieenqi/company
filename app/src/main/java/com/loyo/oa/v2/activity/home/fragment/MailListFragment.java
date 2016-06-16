package com.loyo.oa.v2.activity.home.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.FrameLayout.LayoutParams;

import com.loyo.oa.v2.R;

/**
 * 【统计】fragment
 */
public class MailListFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~fragment2-->onCreate()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~fragment2-->onCreateView()");
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        FrameLayout fl = new FrameLayout(getActivity());
        fl.setLayoutParams(params);
        fl.setBackgroundResource(R.color.green51);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        final int margin = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 8, dm);
        TextView v = new TextView(getActivity());
        params.setMargins(margin, margin, margin, margin);
        v.setLayoutParams(params);
        v.setLayoutParams(params);
        v.setGravity(Gravity.CENTER);
        v.setText("统计页面");
        v.setTextSize((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 12, dm));
        fl.addView(v);
        return fl;
    }

    public void onInIt() {
        Toast.makeText(getActivity(), "我收到tab1微信的传召", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        System.out
                .println("~~~~~~~~~~~~~~~~~~~~~~fragment2-->onActivityCreated()");
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~fragment2-->onDestroy()");
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~fragment2-->onPause()");
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~fragment2-->onResume()");
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~fragment2-->onStart()");
    }

    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~fragment2-->onStop()");
    }
}
