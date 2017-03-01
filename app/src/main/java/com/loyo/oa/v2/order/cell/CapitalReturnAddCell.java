package com.loyo.oa.v2.order.cell;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.order.model.CapitalReturn;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by EthanGong on 2017/2/28.
 */

public class CapitalReturnAddCell extends OrderAddBaseCell {


    CapitalReturn model;

    @BindView(R.id.tv_index) public TextView titleIndex;
    @BindView(R.id.container_date) ViewGroup dateContainer;
    @BindView(R.id.container_money) ViewGroup moneyContainer;
    @BindView(R.id.container_invoice) ViewGroup invoiceContainer;
    @BindView(R.id.container_person) ViewGroup payeeContainer;
    @BindView(R.id.container_pay) ViewGroup paymentContainer;
    @BindView(R.id.container_attachment) ViewGroup attachmentContainer;
//
    @BindView(R.id.tv_date) TextView dateText;
    @BindView(R.id.et_money) EditText moneyEditText;
    @BindView(R.id.et_invoice) EditText invoiceEditText;
    @BindView(R.id.tv_person) TextView payeeText;
    @BindView(R.id.tv_pay) TextView paymentText;
    @BindView(R.id.label_attachment) TextView attachmentText;
    @BindView(R.id.et_remake) EditText remarkEditText;

    @OnClick(R.id.ll_delete) void onDelete() {
        if (actionListener != null) {
            actionListener.onDeleteAtIndex(index);
        }
    }

    @OnClick(R.id.container_date) void onDate() {
        if (capitalReturnListener != null) {
            capitalReturnListener.onFundingDateForIndex(index);
        }
    }

    @OnClick(R.id.container_person) void onPayee() {
        if (capitalReturnListener != null) {
            capitalReturnListener.onPayeeForIndex(index);
        }
    }

    @OnClick(R.id.container_pay) void onPayment() {
        if (capitalReturnListener != null) {
            capitalReturnListener.onPaymentForIndex(index);
        }
    }

    @OnClick(R.id.container_attachment) void onAttachment() {
        if (capitalReturnListener != null) {
            capitalReturnListener.onAttachmentForIndex(index);
        }
    }


    public static CapitalReturnAddCell instance(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cell_capital_return_add, parent, false);
        return new CapitalReturnAddCell(itemView);
    }

    private TextWatcher moneyWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            try {
                long money = Long.parseLong(s.toString().trim());
                model.receivedMoney = money;
            }
            catch (Exception e) {
                model.receivedMoney = 0;
            }
            if (statisticsListener != null) {
                statisticsListener.onStatisticsChange(index);
            }
        }
    };

    private TextWatcher invoiceWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            try {
                long invoice = Long.parseLong(s.toString().trim());
                model.billingMoney = invoice;
            }
            catch (Exception e) {
                model.billingMoney = -1;
            }
            if (statisticsListener != null) {
                statisticsListener.onStatisticsChange(index);
            }
        }
    };

    private TextWatcher remarkWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            model.remark = s.toString();
        }
    };

    private CapitalReturnAddCell(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        moneyEditText.addTextChangedListener(moneyWatcher);
        invoiceEditText.addTextChangedListener(invoiceWatcher);
        remarkEditText.addTextChangedListener(remarkWatcher);
    }

    public void loadModel(CapitalReturn capitalReturn) {
        this.model = capitalReturn;
        dateText.setText(capitalReturn.getReceivedAtString());
        payeeText.setText(capitalReturn.getPayeeName());
        paymentText.setText(capitalReturn.getPaymentMethod());
        attachmentText.setText(capitalReturn.getAttachmentString());
        moneyEditText.setText(capitalReturn.getMoney());
        invoiceEditText.setText(capitalReturn.getBilling());
        remarkEditText.setText(capitalReturn.getRemark());
    }
}
