package com.loyo.oa.v2.order.model;

import android.text.TextUtils;

import com.loyo.oa.v2.activityui.product.model.ProductDetails;
import com.loyo.oa.v2.activityui.sale.bean.SaleIntentionalProduct;

/**
 * Created by EthanGong on 2017/2/28.
 */

public class ProductDeal {
    public double price;
    public double amount;
    public double discount = -1;
    public double total = -1;
    public String remark;
    public ProductDetails product;
    public boolean stockEnabled;

    public boolean showOriginPrice() {
        return product != null;
    }

    public boolean showStock() {
        return product != null && stockEnabled;
    }

    public String getOriginPrice() {
        if (product == null) {
            return null;
        }
        return "单价："+product.unitPrice;
    }

    public String getStock() {
        if (product == null) {
            return null;
        }
        return "库存："+ product.stock;
    }

    public String getPrice() {

        if (price > 0) {
            return price+"";
        }
        else if (product != null) {
            return product.unitPrice+"";
        }
        else {
            return null;
        }
    }


    public String getAmount() {
        if (amount > 0) {
            return ""+amount;
        }
        return null;
    }

    public String getProductName() {
        if (product != null) {
            return product.name;
        }
        return null;
    }

    public String getDiscount() {
        if (discount < 0) {
            return "折扣：";
        }
        else {
            return "折扣：" + discount*100 +"%";
        }
    }

    public String getTotalMoney() {
        if (total < 0) {
            return "总金额：";
        }
        return "总金额："+ total;
    }

    public boolean isEmpty() {
        return product == null
                && price == 0
                && amount == 0
                && TextUtils.isEmpty(remark);
    }

    public boolean isValidated() {
        return product != null
                && price > 0
                && amount > 0
                && discount >= 0
                && total > 0
                && (!stockEnabled || amount <= product.stock);
    }

    public SaleIntentionalProduct toSaleIntentionalProduct() {
        SaleIntentionalProduct intentionalProduct = new SaleIntentionalProduct();
        intentionalProduct.salePrice = this.price;
        intentionalProduct.quantity = this.amount;
        intentionalProduct.memo = this.remark;
        intentionalProduct.discount = this.discount *100;
        intentionalProduct.totalMoney = this.total;
        intentionalProduct.id = this.product.id;
        intentionalProduct.name = this.product.name;
        intentionalProduct.costPrice = this.product.unitPrice;
        intentionalProduct.unit = this.product.unit;
        intentionalProduct.stock = this.product.stock;
        return intentionalProduct;
    }

    public ProductDeal(SaleIntentionalProduct intentionalProduct) {
        this.price = intentionalProduct.salePrice;
        this.amount = intentionalProduct.quantity;
        this.remark = intentionalProduct.memo;
        this.discount = intentionalProduct.discount /100;
        this.total = intentionalProduct.totalMoney;
        this.product = new ProductDetails();
        this.product.id = intentionalProduct.id;
        this.product.name = intentionalProduct.name;
        this.product.unitPrice = (float) intentionalProduct.costPrice;
        this.product.unit = intentionalProduct.unit;
        this.product.stock = (float) intentionalProduct.stock;

    }

    public ProductDeal() {

    }
}
