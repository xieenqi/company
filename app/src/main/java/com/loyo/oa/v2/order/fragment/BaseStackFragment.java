package com.loyo.oa.v2.order.fragment;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import com.loyo.oa.v2.tool.BaseFragment;

/**
 * Created by EthanGong on 2017/3/1.
 */

public class BaseStackFragment extends BaseFragment {
    public boolean onBackPressed() {
        return false;
    }

    public void hideKeyboard() {
        if (getActivity() == null || getView() == null) {
            return;
        }
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }
}
