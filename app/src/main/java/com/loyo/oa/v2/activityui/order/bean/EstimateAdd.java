package com.loyo.oa.v2.activityui.order.bean;

import java.io.Serializable;

/**
 * 【新建回款】bean
 * Created by yyy on 16/8/3.
 */
public class EstimateAdd implements Serializable {

    public String id;          //订单id
    public String companyId;   //公司id
    public int receivedAt;    //回款日期
    public int receivedMoney;  //回款金额
    public int billingMoney;   //开票金额
    public PayeeUser payeeUser = new PayeeUser();  //收款人

    public String attachmentUUId;  //附件id
    public int attachmentCount;    //附件个数
    public int payeeMethod;        //付款方式
    public int status;             //回款记录状态(待审批 未通过等)
    public String wfId;
    public String remark;          //备注
    public String UUId;

    public String orderTitle;
    public String orderId;


    public class PayeeUser implements Serializable {
        public String id;
        public String name;
        public String avatar;
    }

}
