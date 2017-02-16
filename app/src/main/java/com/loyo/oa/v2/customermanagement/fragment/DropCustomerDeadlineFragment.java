package com.loyo.oa.v2.customermanagement.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customermanagement.model.DropDeadlineModel;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.loyo.oa.v2.R.id.followup;

/**
 * Created by EthanGong on 2017/2/10.
 */

public class DropCustomerDeadlineFragment extends DialogFragment {

    public interface CustomerDeadlineActionListener {
        void onAddFollowup();
        void onAddVisit();
        void onAddCall();
        void onAddOrder();
    }

    @BindView(R.id.tv_title) TextView titleText;
    @BindView(R.id.tv_order_title) TextView orderTitleText;

    @BindView(R.id.dim)                LinearLayout dimContainer;
    @BindView(R.id.followup_container) LinearLayout followupContainer;
    @BindView(R.id.order_container)    LinearLayout orderContainer;

    @BindView(followup) LinearLayout followupView;
    @BindView(R.id.visit)    LinearLayout visitView;
    @BindView(R.id.call)     LinearLayout callView;
    @BindView(R.id.order)    LinearLayout orderView;

    @BindView(R.id.tv_followup_deadline) TextView followupDeadlineText;
    @BindView(R.id.tv_followup) TextView followupDeadline;

    @BindView(R.id.tv_visit_deadline) TextView visitDeadlineText;
    @BindView(R.id.tv_visit) TextView visitDeadline;

    @BindView(R.id.tv_call_deadline) TextView callDeadlineText;
    @BindView(R.id.tv_call) TextView callDeadline;

    @BindView(R.id.tv_order_deadline) TextView orderDeadlineText;
    @BindView(R.id.tv_order) TextView orderDeadline;

    @OnClick(R.id.dim) void onClose() {
        dismiss();
    }

    @OnClick(R.id.tv_followup) void onFollowup() {
        if (listenerRef != null && listenerRef.get() != null) {
            dismiss();
            listenerRef.get().onAddFollowup();
        }
    }

    @OnClick(R.id.tv_visit) void onVisit() {
        if (listenerRef != null && listenerRef.get() != null) {
            dismiss();
            listenerRef.get().onAddVisit();
        }
    }

    @OnClick(R.id.tv_call) void onCall() {
        if (listenerRef != null && listenerRef.get() != null) {
            dismiss();
            listenerRef.get().onAddCall();
        }
    }

    @OnClick(R.id.tv_order) void onOrder() {
        if (listenerRef != null && listenerRef.get() != null) {
            dismiss();
            listenerRef.get().onAddOrder();
        }
    }

    DropDeadlineModel model;
    WeakReference<CustomerDeadlineActionListener> listenerRef;



    public DropCustomerDeadlineFragment() {}

    public static DropCustomerDeadlineFragment newInstance(DropDeadlineModel model, CustomerDeadlineActionListener listener) {
        DropCustomerDeadlineFragment fragment = new DropCustomerDeadlineFragment();
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog_FullScreen);
        fragment.model = model;
        if (listener != null) {
            fragment.listenerRef = new WeakReference<>(listener);
        }
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_drop_customer_deadline, container);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        followupDeadline.setOnTouchListener(Global.GetTouch());
        visitDeadline.setOnTouchListener(Global.GetTouch());
        callDeadline.setOnTouchListener(Global.GetTouch());
        orderDeadline.setOnTouchListener(Global.GetTouch());

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    public void onResume() {

        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        loadData();
        super.onResume();
    }

    void loadData() {
        titleText.setText("请在以下时间完成“"+ model.getConditionString() +"”跟进工作：");
        if (model.activityRecycleAt > 0) {
            followupContainer.setVisibility(View.VISIBLE);
            orderTitleText.setText("且在以下时间完成签约：");
            if (model.saleactRecycleAt > 0) {
                followupView.setVisibility(View.VISIBLE);
                followupDeadlineText.setText(DropDeadlineModel.formatDateTimeString(model.saleactRecycleAt));
            }
            else {
                followupView.setVisibility(View.GONE);
            }
            if (model.visitRecycleAt > 0) {
                visitView.setVisibility(View.VISIBLE);
                visitDeadlineText.setText(DropDeadlineModel.formatDateTimeString(model.visitRecycleAt));
            }
            else {
                visitView.setVisibility(View.GONE);
            }
            if (model.voiceRecycleAt > 0) {
                callView.setVisibility(View.VISIBLE);
                callDeadlineText.setText(DropDeadlineModel.formatDateTimeString(model.voiceRecycleAt));
            }
            else {
                callView.setVisibility(View.GONE);
            }
        }
        else {
            followupContainer.setVisibility(View.GONE);
            orderTitleText.setText("请在以下时间完成签约：");
        }
        if (model.orderRecycleAt > 0) {
            orderContainer.setVisibility(View.VISIBLE);
            orderDeadlineText.setText(DropDeadlineModel.formatDateTimeString(model.orderRecycleAt));
        }
        else
        {
            orderContainer.setVisibility(View.GONE);
        }
    }
}
