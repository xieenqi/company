package com.loyo.oa.v2.activity.home.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loyo.oa.v2.R;

/**
 * 【应用】fragment
 */
public class FragmentHomeApplication extends Fragment {
//    private AllFragment mAllFragment;
    //	private NearFragmengt mNearFragment;
    private Fragment currentFragment = null;
    private String changeEvent = "All"; // 全成All/身边Near

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_application, container,
                false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
//        onInit();
    }

//    private void onInit() {
//        //fagment的基本用法就不介绍了
//        mAllFragment = new AllFragment();
//        currentFragment = mAllFragment;
//        getActivity().getSupportFragmentManager().beginTransaction()
//                .replace(R.id.content_frame_tab1, mAllFragment, "AllFragment")
//                .commit();
//    }

//    private void switchFragment(Fragment to) {
//        if (currentFragment != to) {
//            FragmentTransaction transaction = getActivity()
//                    .getSupportFragmentManager().beginTransaction();
//            if (!to.isAdded()) { // 先判断是否被add过
//                transaction
//                        .hide(currentFragment)
//                        .add(R.id.content_frame_tab1, to,
//                                to.getClass().getSimpleName())
//                        .commitAllowingStateLoss(); // 隐藏当前的fragment，add下一个到Activity中
//            } else {
//                transaction.hide(currentFragment).show(to)
//                        .commitAllowingStateLoss(); // 隐藏当前的fragment，显示下一个
//            }
//            currentFragment = to;
//
//        }
//    }


    /*
     * 点击切换活动判断当前页面
     */
    public void setChangeEvent(String value) {
        changeEvent = value;
    }

    public String getChangeEvent() {
        return changeEvent;
    }

    /*
     * 点击策划切换页面
     */
    public void gotoShiftEvent(int index) {
//        if (index == 1) {
//            if (mAllFragment == null) {
//                mAllFragment = new AllFragment();
//            }
//            switchFragment(mAllFragment);
//            setChangeEvent("All");
//        }

//        else {
//			if (mNearFragment == null) {
//				mNearFragment = new NearFragmengt();
//			}
//			switchFragment(mNearFragment);
//		}
    }

}
