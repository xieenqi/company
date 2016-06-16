package com.loyo.oa.v2.activity.home.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.home.adapter.ProcessDataAdapter;

/**
 * 【统计】fragment
 */
public class FragmentHomeStatistics extends Fragment {

    private final static String[] tracyColors = {"#f8668a", "#4ec469", "#4ddac2", "#31cbe8", "#88b9f7", "#7fcaff", "#f18f73", "#fdb485", "#fde068", "#12db8a"};
    private LinearLayout ll_process;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO FragmentHomeStatistics -->onCreate()
        super.onCreate(savedInstanceState);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~fragment2-->onCreate()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_statistics, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {//@drawable/shape_progressbar_mini
        ll_process = (LinearLayout) view.findViewById(R.id.ll_process);
        ProgressBar pb_progress = (ProgressBar) view.findViewById(R.id.pb_progress);
        pb_progress.setProgress(56);
        pb_progress.setProgressDrawable(getResources().getDrawable(R.drawable.shape_progressbar_mini20));
        for (int i = 0; i < 10; i++) {
            ll_process.addView(new ProcessDataAdapter(getActivity()));
        }
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
