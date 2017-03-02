package com.loyo.oa.v2.order.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.loyo.oa.v2.order.cell.AddButtonCell;
import com.loyo.oa.v2.order.cell.OrderAddBaseCell;
import com.loyo.oa.v2.order.cell.ProductAddCell;
import com.loyo.oa.v2.order.model.ProductDeal;

import java.util.ArrayList;

/**
 * Created by EthanGong on 2017/2/28.
 */

public class AddProductAdapter extends RecyclerView.Adapter<OrderAddBaseCell> implements OrderAddBaseCell.StatisticsListener {

    @Override
    public void onStatisticsChange(int index) {
        if (callback != null) {

            double total = 0;
            double discount = 0;
            for (ProductDeal deal : data) {
                if (deal.total >= 0
                        && deal.discount >= 0
                        && deal.amount > 0
                        && deal.product != null) {
                    total = total + deal.total;
                    discount = discount + deal.product.unitPrice * deal.amount;
                }
            }

            if (discount == 0 ) {
                callback.onListChange(0, 0);
            }
            else {
                callback.onListChange(total,total/discount);
            }
        }
    }

    public interface ListChangeCallback {
        void onListChange(double totalMoney, double totalDiscount);
    }

    public ListChangeCallback callback;

    private static final int PRODUCT_CELL = 0;
    private static final int ADD_CELL     = 1;
    boolean initEmpty;

    public ArrayList<ProductDeal> data = new ArrayList<ProductDeal>(){{
        add(new ProductDeal());
    }};

    private OrderAddBaseCell.ActionListener actionListener;
    private OrderAddBaseCell.ProductListener productListener;

    public AddProductAdapter(OrderAddBaseCell.ActionListener aListener,
                             OrderAddBaseCell.ProductListener pListener,
                             boolean initEmpty) {
        setHasStableIds(true);
        this.actionListener = aListener;
        this.productListener = pListener;
        if (initEmpty) {
            data.clear();
        }
    }

    public void setData(ArrayList<ProductDeal> data) {
        this.data.clear();
        this.data.addAll(data);
        if (this.data.size() <= 0 && !initEmpty) {
            this.data.add(new ProductDeal());
        }
    }

    public void fireListChange() {
        if (callback != null) {

            double total = 0;
            double discount = 0;
            for (ProductDeal deal : data) {
                if (deal.total > 0
                        && deal.discount >= 0
                        && deal.amount > 0
                        && deal.product != null) {
                    total = total + deal.total;
                    discount = discount + deal.product.unitPrice * deal.amount;
                }
            }

            if (discount == 0 ) {
                callback.onListChange(0, 0);
            }
            else {
                callback.onListChange(total,total/discount);
            }
        }
    }

    @Override
    public OrderAddBaseCell onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == PRODUCT_CELL) {
            return ProductAddCell.instance(parent);
        }
        else {
            return AddButtonCell.instance(parent);
        }
    }

    @Override
    public void onBindViewHolder(OrderAddBaseCell holder, int position) {
        holder.actionListener = this.actionListener;
        holder.productListener = this.productListener;
        holder.statisticsListener = this;
        holder.index = position;
        if (position == data.size() && holder instanceof AddButtonCell) {
            ((AddButtonCell)holder).setText("添加第"+(data.size()+1)+"个产品");
        }
        else {
            ((ProductAddCell)holder).titleIndex.setText("产品"+(position+1));
            ((ProductAddCell)holder).loadModel(data.get(position));
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
}
