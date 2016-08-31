package com.loyo.oa.v2.activityui.worksheet.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.worksheet.WorksheetAddActivity;
import com.loyo.oa.v2.tool.BaseFragment;

public class WorksheetAddStep2Fragment extends BaseFragment implements View.OnClickListener  {
    private View mView;
    private ViewGroup img_title_left, img_title_right;
    TextView tv_title_1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.activity_worksheet_add_step2, null);
            initUI(mView);
        }
        return mView;
    }

    void initUI(View mView) {

        img_title_left = (ViewGroup) mView.findViewById(R.id.img_title_left);
        img_title_left.setOnClickListener(this);

        img_title_right = (ViewGroup) mView.findViewById(R.id.img_title_right);
        img_title_right.setOnClickListener(this);

        tv_title_1 = (TextView) mView.findViewById(R.id.tv_title_1);
        tv_title_1.setText("填写工单内容");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                ((WorksheetAddActivity)getActivity()).previousStep();
                break;
            case R.id.img_title_right:
                getActivity().finish();
                break;
        }

    }
}
