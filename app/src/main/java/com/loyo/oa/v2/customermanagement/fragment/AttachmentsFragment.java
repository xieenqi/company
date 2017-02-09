package com.loyo.oa.v2.customermanagement.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.tool.BaseFragment;

/**
 * Created by EthanGong on 2017/2/9.
 */

public class AttachmentsFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.fragment_attachments, container, false);
        return view;
    }
}
