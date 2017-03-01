package com.loyo.oa.v2.order.cell;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by EthanGong on 2017/2/28.
 */

public class OrderAddBaseCell extends RecyclerView.ViewHolder {

    public interface ActionListener {
        void onDeleteAtIndex(int index);
        void toast(String msg);
        void onAdd();
    }

    public interface ProductListener {
        void pickProductForIndex(int index);
    }

    public ProductListener productListener;

    public interface CapitalReturnListener {
        void onFundingDateForIndex(int index);
        void onPayeeForIndex(int index);
        void onPaymentForIndex(int index);
        void onAttachmentForIndex(int index);
    }
    public CapitalReturnListener capitalReturnListener;

    public interface StatisticsListener {
        void onStatisticsChange(int index);
    }

    public StatisticsListener statisticsListener;

    public int index;
    public ActionListener actionListener;
    public OrderAddBaseCell(View itemView) {
        super(itemView);
    }
}
