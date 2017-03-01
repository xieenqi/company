package com.loyo.oa.v2.order.model;

import android.text.TextUtils;

import com.loyo.oa.common.utils.DateTool;
import com.loyo.oa.v2.activityui.order.bean.EstimateAdd;
import com.loyo.oa.v2.order.common.PaymentMethod;
import com.loyo.oa.v2.tool.Utils;

import java.util.Date;

/**
 * Created by EthanGong on 2017/3/1.
 */

public class CapitalReturn {

    private String md5;

    public long defaultReceivedAt;    //默认回款日期, 默认选中当天
    public long receivedAt;    //回款日期
    public long receivedMoney;  //回款金额
    public long billingMoney;   //开票金额
    public EstimateAdd.PayeeUser payeeUser;  //收款人

    public String attachmentUUId;  //附件id
    public int attachmentCount; //附件个数
    public PaymentMethod payeeMethod;        //付款方式
    public String remark;          //备注

    public String getReceivedAtString() {
        if (receivedAt <= 0) {
            return DateTool.getDateReal(defaultReceivedAt);
        }
        else {
            return DateTool.getDateReal(receivedAt);
        }
    }

    public long getReceivedAt() {
        if (receivedAt <= 0) {
            return defaultReceivedAt;
        }
        else {
            return receivedAt;
        }
    }

    public String getPayeeName() {
        if (payeeUser != null) {
            return payeeUser.name;
        }
        return null;
    }

    public String getPaymentMethod() {
        if (payeeMethod != null) {
            return payeeMethod.getName();
        }
        return null;
    }

    public String getAttachmentString() {
        if (attachmentUUId == null) {
            return "附件";
        }
        else {
            return "附件("+ attachmentCount +")";
        }
    }

    public String getMoney() {
        if (receivedMoney > 0) {
            return String.format("%d", receivedMoney);
        }
        return null;
    }

    public String getBilling() {
        if (billingMoney >= 0) {
            return String.format("%d", billingMoney);
        }
        return null;
    }

    public String getRemark() {
        return remark;
    }

    public boolean isEmpty() {
        return receivedAt == 0
                && receivedMoney == 0
                && billingMoney <= 0
                && payeeUser == null
                && attachmentUUId == null
                && payeeMethod == null
                && TextUtils.isEmpty(remark);
    }

    public EstimateAdd toEstimateAdd() {
        EstimateAdd estimateAdd = new EstimateAdd();
        estimateAdd.receivedAt = receivedAt!=0?receivedAt:defaultReceivedAt;
        estimateAdd.receivedMoney = receivedMoney;
        estimateAdd.billingMoney = billingMoney;
        if (payeeUser != null) {
            estimateAdd.payeeUser = payeeUser;
        }
        if (payeeMethod != null) {
            estimateAdd.payeeMethod = payeeMethod.getCode();
        }
        estimateAdd.attachmentCount = attachmentCount;
        estimateAdd.attachmentUUId = attachmentUUId;
        estimateAdd.remark = remark;

        return estimateAdd;
    }

    public CapitalReturn(EstimateAdd estimateAdd) {
        defaultReceivedAt = new Date().getTime()/1000;
        receivedAt = estimateAdd.receivedAt;
        receivedMoney = estimateAdd.receivedMoney;
        billingMoney = estimateAdd.billingMoney;
        if (estimateAdd.payeeUser.id != null) {
            payeeUser = estimateAdd.payeeUser;
        }
        else {
            payeeUser = null;
        }
        if (estimateAdd.payeeMethod != 0) {
            payeeMethod = PaymentMethod.getPaymentMethod(estimateAdd.payeeMethod);
        }
        attachmentCount = estimateAdd.attachmentCount;
        attachmentUUId = estimateAdd.attachmentUUId;
        remark = estimateAdd.remark;

        md5 = getMD5();
    }

    public CapitalReturn() {
        defaultReceivedAt = new Date().getTime()/1000;
        md5 = getMD5();
    }

    private String getMD5() {
        StringBuilder fingerBuilder = new StringBuilder();
        fingerBuilder.append("receivedAt");
        fingerBuilder.append(receivedAt);
        fingerBuilder.append("receivedMoney");
        fingerBuilder.append(receivedMoney);
        fingerBuilder.append("billingMoney");
        fingerBuilder.append(billingMoney);

        fingerBuilder.append("attachmentCount");
        fingerBuilder.append(attachmentCount);
        fingerBuilder.append("attachmentUUId");
        fingerBuilder.append(attachmentUUId);
        fingerBuilder.append("remark");
        fingerBuilder.append(remark);

        if (payeeUser != null) {
            fingerBuilder.append("payeeUser.id");
            fingerBuilder.append(payeeUser.id);
            fingerBuilder.append("payeeUser.name");
            fingerBuilder.append(payeeUser.name);
            fingerBuilder.append("payeeUser.avatar");
            fingerBuilder.append(payeeUser.avatar);
        }
        String finger = fingerBuilder.toString();
        return Utils.md5(finger);
    }

    public boolean hasChanged() {
        String fingerPrint = getMD5();
        if (md5 != null && md5.equals(fingerPrint)) {
            return false;
        }
        return true;
    }
}
