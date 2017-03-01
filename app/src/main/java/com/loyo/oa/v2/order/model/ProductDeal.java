package com.loyo.oa.v2.order.model;

import android.text.TextUtils;

import com.loyo.oa.v2.activityui.product.model.ProductDetails;
import com.loyo.oa.v2.activityui.sale.bean.SaleIntentionalProduct;
import com.loyo.oa.v2.tool.Utils;

/**
 * Created by EthanGong on 2017/2/28.
 */

public class ProductDeal {

    private String md5;

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
        return "单价："+Utils.setValueDouble(product.unitPrice);
    }

    public String getStock() {
        if (product == null) {
            return null;
        }
        return "库存："+ Utils.setValueDouble(product.stock);
    }

    public String getPrice() {

        if (price > 0) {
            return price+"";
        }
        else if (product != null) {
            return Utils.setValueDouble(product.unitPrice);
        }
        else {
            return null;
        }
    }


    public String getAmount() {
        if (amount > 0) {
            return Utils.setValueDouble(amount);
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
            return "折扣：" + Utils.setValueDouble(discount*100) +"%";
        }
    }

    public String getTotalMoney() {
        if (total < 0) {
            return "总金额：";
        }
        return "总金额："+ Utils.setValueDouble(total);
    }

    public String getRemark() {
        return remark;
    }

    public boolean isEmpty() {
        return product == null
                && price == 0
                && amount == 0
                && TextUtils.isEmpty(remark);
    }

    public boolean hasChanged() {
        String fingerPrint = getFingerprint();
        if (md5 != null && md5.equals(fingerPrint)) {
            return false;
        }
        return true;
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

        md5 = getFingerprint();

    }

    public ProductDeal() {
        md5 = getFingerprint();
    }

    private String getFingerprint() {
        StringBuilder fingerBuilder = new StringBuilder();
        fingerBuilder.append("price");
        fingerBuilder.append(price);
        fingerBuilder.append("amount");
        fingerBuilder.append(amount);
        fingerBuilder.append("remark");
        fingerBuilder.append(remark);
        fingerBuilder.append("discount");
        fingerBuilder.append(discount);
        fingerBuilder.append("total");
        fingerBuilder.append(total);
        if (product != null) {
            fingerBuilder.append("product.id");
            fingerBuilder.append(product.id);
            fingerBuilder.append("product.name");
            fingerBuilder.append(product.name);
            fingerBuilder.append("product.unitPrice");
            fingerBuilder.append(product.unitPrice);
            fingerBuilder.append("product.unit");
            fingerBuilder.append(product.unit);
            fingerBuilder.append("product.stock");
            fingerBuilder.append(product.stock);
        }
        String finger = fingerBuilder.toString();
        return Utils.md5(finger);
    }
}
