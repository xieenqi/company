package com.loyo.oa.v2.order.cell;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by EthanGong on 2017/2/28.
 */

public class ProductAddBaseCell extends RecyclerView.ViewHolder {

    public interface ActionListener {
        void deleteProductAtIndex(int index);

        void pickProductForIndex(int index);

        void addProduct();

        void toast(String msg);
    }

    public interface StatisticsListener {
        void onStatisticsChange(int index);
    }

    public StatisticsListener statisticsListener;

    public int index;
    public ActionListener listener;

    public ProductAddBaseCell(View itemView) {
        super(itemView);
    }
}
