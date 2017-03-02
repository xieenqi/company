package com.loyo.oa.v2.order.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.loyo.oa.v2.order.cell.AddButtonCell;
import com.loyo.oa.v2.order.cell.CapitalReturnAddCell;
import com.loyo.oa.v2.order.cell.OrderAddBaseCell;
import com.loyo.oa.v2.order.model.CapitalReturn;

import java.util.ArrayList;

/**
 * Created by EthanGong on 2017/2/28.
 */

public class AddCapitalReturnAdapter extends RecyclerView.Adapter<OrderAddBaseCell>
        implements OrderAddBaseCell.StatisticsListener {

    public interface ListChangeCallback {
        void onListChange(long totalMoney, long totalBilling);
    }

    public ListChangeCallback callback;

    private OrderAddBaseCell.ActionListener actionListener;
    private OrderAddBaseCell.CapitalReturnListener capitalReturnListener;

    private static final int PRODUCT_CELL = 0;
    private static final int ADD_CELL     = 1;

    public ArrayList<CapitalReturn> data = new ArrayList<CapitalReturn>(){{
        add(new CapitalReturn());
    }};

    public AddCapitalReturnAdapter(OrderAddBaseCell.ActionListener actionListener, OrderAddBaseCell.CapitalReturnListener capitalReturnListener) {
        setHasStableIds(true);
        this.actionListener = actionListener;
        this.capitalReturnListener = capitalReturnListener;
    }

    public void setData(ArrayList<CapitalReturn> data) {
        this.data.clear();
        this.data.addAll(data);
        if (this.data.size() <= 0) {
            this.data.add(new CapitalReturn());
        }
    }

    public void fireListChange() {
        if (callback != null) {
            long total = 0;
            long billing = 0;
            for (CapitalReturn capitalReturn : data) {
                total = total + capitalReturn.receivedMoney;
                billing = billing + capitalReturn.billingMoney>=0?capitalReturn.billingMoney:0;
            }
            callback.onListChange(total,billing);
        }
    }

    @Override
    public OrderAddBaseCell onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == PRODUCT_CELL) {
            return CapitalReturnAddCell.instance(parent);
        }
        else {
            return AddButtonCell.instance(parent);
        }
    }

    @Override
    public void onBindViewHolder(OrderAddBaseCell holder, int position) {
        holder.actionListener = this.actionListener;
        holder.capitalReturnListener = this.capitalReturnListener;
        holder.statisticsListener = this;
        holder.index = position;
        if (position == data.size() && holder instanceof AddButtonCell) {
            ((AddButtonCell)holder).setText("添加第"+(data.size()+1)+"个回款记录");
        }
        else {
            ((CapitalReturnAddCell)holder).titleIndex.setText("回款记录"+(position+1));
            ((CapitalReturnAddCell)holder).loadModel(data.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return data.size() + 1;
    }

    public int getItemViewType(int position) {
        return position == data.size() ? ADD_CELL : PRODUCT_CELL;
    }

    @Override
    public long getItemId(int position) {
        if(position == data.size()) {
            return 0;
        }
        return data.get(position).hashCode();
    }

    /**
     * OrderAddBaseCell.StatisticsListener
     */
    @Override
    public void onStatisticsChange(int index) {
        if (callback != null) {
            long total = 0;
            long billing = 0;
            for (CapitalReturn capitalReturn : data) {
                total = total + capitalReturn.receivedMoney;
                billing = billing + capitalReturn.billingMoney>=0?capitalReturn.billingMoney:0;
            }
            callback.onListChange(total,billing);
        }
    }
}
