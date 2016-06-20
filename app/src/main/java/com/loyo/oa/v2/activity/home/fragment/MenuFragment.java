package com.loyo.oa.v2.activity.home.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.home.ActivityMainHome;

/**
 * 【侧边栏】fragment
 */
public class MenuFragment extends Fragment {
    private Button vButton;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_home_menu, container,
                false);
        vButton = (Button) view.findViewById(R.id.change_activity);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onInit();
    }

    private FragmentHomeApplication mFragmentHomeApplication;

    private String makeFragmentName(int viewId, int index) {
        return "android:switcher:" + viewId + ":" + index;
    }

    private void onInit() {
        vButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //得到fragment的名字只限于FragmentPagerAdapter里面的
                String name = makeFragmentName(ActivityMainHome.index, 0);
                mFragmentHomeApplication = (FragmentHomeApplication) getFragmentManager()
                        .findFragmentByTag(name);
                String a;
                if (mFragmentHomeApplication != null) {
                    a = mFragmentHomeApplication.getChangeEvent();
                    if ("All".equals(a)) {
                        mFragmentHomeApplication.gotoShiftEvent(2);
                        mFragmentHomeApplication.setChangeEvent("Near");

                    } else {
                        mFragmentHomeApplication.gotoShiftEvent(1);
                        mFragmentHomeApplication.setChangeEvent("All");
                    }
                }
                ((ActivityMainHome) getActivity()).togggle();

            }
        });
    }
}
