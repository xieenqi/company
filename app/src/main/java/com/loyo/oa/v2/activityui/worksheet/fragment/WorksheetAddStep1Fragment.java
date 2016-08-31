package com.loyo.oa.v2.activityui.worksheet.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.clue.common.ClueCommon;
import com.loyo.oa.v2.activityui.worksheet.WorksheetAddActivity;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetTemplate;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetConfig;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.customview.PaymentPopView;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.SharedUtil;

import java.util.ArrayList;


public class WorksheetAddStep1Fragment extends BaseFragment implements View.OnClickListener {

    private View mView;
    private ViewGroup img_title_left, img_title_right, ll_worksheet_type;
    TextView tv_title_1, tv_worksheet_type;

    WorksheetTemplate selectedType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.activity_worksheet_add_step1, null);
            initUI(mView);
        }
        return mView;
    }

    void initUI(View mView) {

        img_title_left = (ViewGroup) mView.findViewById(R.id.img_title_left);
        img_title_left.setOnClickListener(this);

        ll_worksheet_type = (ViewGroup) mView.findViewById(R.id.ll_worksheet_type);
        ll_worksheet_type.setOnClickListener(this);


        img_title_right = (ViewGroup) mView.findViewById(R.id.img_title_right);
        img_title_right.setOnClickListener(this);

        tv_title_1 = (TextView) mView.findViewById(R.id.tv_title_1);
        tv_title_1.setText("选择订单和工单类型");

        tv_worksheet_type = (TextView) mView.findViewById(R.id.tv_worksheet_type);
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
                getActivity().finish();
                break;
            case R.id.img_title_right:
                ((WorksheetAddActivity)getActivity()).nextStep();
                break;
            case R.id.ll_worksheet_type:
                selectType();
                break;
        }

    }

    public void selectType() {

        final ArrayList<WorksheetTemplate> types = WorksheetConfig.getWorksheetTypes(true);
        if (types == null || types.size() == 0) {
            return;
        }

        String[] list = new String[types.size()];
        for(int i = 0; i < types.size(); i++) {
            list[i] = types.get(i).name;
        }

        final PaymentPopView popViewKind = new PaymentPopView(getActivity(), list, "选择工单类型");
        popViewKind.show();
        popViewKind.setCanceledOnTouchOutside(true);
        popViewKind.setCallback(new PaymentPopView.VaiueCallback() {
            @Override
            public void setValue(String value, int index) {
                selectedType = types.get(index-1);
                tv_worksheet_type.setText(value);
            }
        });
    }
}
