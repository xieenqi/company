package com.loyo.oa.v2.order.cell;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.order.model.ProductDeal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.loyo.oa.v2.R.id.et_price;
import static com.loyo.oa.v2.R.id.tv_discount;
import static com.loyo.oa.v2.R.id.tv_price;
import static com.loyo.oa.v2.R.id.tv_total;

/**
 * Created by EthanGong on 2017/2/28.
 */

public class ProductAddCell extends ProductAddBaseCell {



    @BindView(R.id.tv_index) public TextView titleIndex;
    @BindView(R.id.container_product) ViewGroup productContainer;
    @BindView(R.id.container_price) ViewGroup priceContainer;
    @BindView(R.id.container_amount) ViewGroup amountContainer;
    @BindView(R.id.container_discount_and_total) ViewGroup discountAndTotalContainer;

    @BindView(R.id.tv_product) TextView productText;
    @BindView(tv_price) TextView priceText;
    @BindView(et_price) EditText priceEditText;
    @BindView(R.id.tv_amount) TextView amountText;
    @BindView(R.id.et_amount) EditText amountEditText;
    @BindView(tv_discount) TextView discountText;
    @BindView(tv_total) TextView totalText;
    @BindView(R.id.et_remake) EditText remarkEditText;

    @OnClick(R.id.container_product) void onPickProduct() {
        if (listener != null) {
            listener.pickProductForIndex(index);
        }
    }

    @OnClick(R.id.ll_delete) void onDelete() {
        if (listener != null) {
            listener.deleteProductAtIndex(index);
        }
    }

    private ProductDeal model;

    /**
     * 销售价
     */
    private TextWatcher priceWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!s.toString().contains(".") && s.toString().length() > 7) {
                s.delete(7, s.toString().length());
            }

            String priceString = priceEditText.getText().toString();
            String amountString = amountEditText.getText().toString();
            float amount = 0;
            float salePrice = 0;
            if (TextUtils.isEmpty(priceString)) {
                model.discount = -1;

                model.price = 0;
            }
            else  {
                try {
                    salePrice = Float.valueOf(priceString);
                }
                catch (Exception e) {
                    salePrice = 0;
                    model.discount = -1;
                }
                model.price = salePrice;
            }
            if (TextUtils.isEmpty(amountString)) {
                model.total = -1;
            }
            else {
                try {
                    amount = Float.valueOf(amountString);
                }
                catch (Exception e) {
                    amount = 0;
                }
            }

            if (model.product!=null && model.product.unitPrice > 0) {
                model.discount = salePrice/model.product.unitPrice;
            }
            else {
                model.discount = -1;
            }

            if (model.discount >= 0) {
                model.total = model.product.unitPrice * amount *model.discount;
            }
            else {
                model.total = -1;
            }
            discountText.setText(model.getDiscount());
            totalText.setText(model.getTotalMoney());
            if (statisticsListener != null) {
                statisticsListener.onStatisticsChange(index);
            }
        }
    };

    private TextWatcher amountWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            String priceString = priceEditText.getText().toString();
            String amountString = amountEditText.getText().toString();
            float amount = 0;
            float salePrice = 0;
            if (TextUtils.isEmpty(priceString)) {
                model.discount = -1;
            }
            else  {
                try {
                    salePrice = Float.valueOf(priceString);
                }
                catch (Exception e) {
                    salePrice = 0;
                }
            }
            if (TextUtils.isEmpty(amountString)) {
                model.total = -1;
                model.amount = 0;
            }
            else {
                try {
                    amount = Float.valueOf(amountString);
                }
                catch (Exception e) {
                    amount = 0;
                    model.total = -1;
                }
                model.amount = amount;
                if (model.product != null && model.amount > model.product.stock
                        && listener != null
                        && model.stockEnabled) {
                    listener.toast("库存不足");
                }
            }

            if (model.product!=null && model.product.unitPrice > 0) {
                model.discount = salePrice/model.product.unitPrice;
            }
            else {
                model.discount = -1;
            }

            if (model.discount >= 0) {
                model.total = model.product.unitPrice * amount *model.discount;
            }
            else {
                model.total = -1;
            }
            discountText.setText(model.getDiscount());
            totalText.setText(model.getTotalMoney());
            if (statisticsListener != null) {
                statisticsListener.onStatisticsChange(index);
            }
        }
    };

    public static ProductAddCell instance(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cell_product_add, parent, false);
        return new ProductAddCell(itemView);
    }

    private ProductAddCell(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        priceEditText.addTextChangedListener(priceWatcher);
        amountEditText.addTextChangedListener(amountWatcher);
    }

    public void loadModel(ProductDeal model) {
        this.model = model;
        productText.setText(model.getProductName());
        priceText.setVisibility(model.showOriginPrice()?View.VISIBLE:View.GONE);
        amountText.setVisibility(model.showStock()?View.VISIBLE:View.GONE);
        priceText.setText(model.getOriginPrice());
        amountText.setText(model.getStock());

        priceEditText.setText(model.getPrice());
        amountEditText.setText(model.getAmount());

        discountAndTotalContainer.setVisibility(model.showOriginPrice()?View.VISIBLE:View.GONE);

    }
}
