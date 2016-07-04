package com.loyo.oa.v2.activityui.home.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.home.MainHomeActivity;

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
}
