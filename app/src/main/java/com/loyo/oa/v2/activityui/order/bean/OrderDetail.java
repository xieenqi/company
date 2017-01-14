package com.loyo.oa.v2.activityui.order.bean;

import com.loyo.oa.v2.activityui.customer.model.ContactLeftExtras;
import com.loyo.oa.v2.activityui.sale.bean.SaleIntentionalProduct;
import com.loyo.oa.v2.activityui.wfinstance.common.SubmitStatus;
import com.loyo.oa.v2.activityui.worksheet.bean.OrderWorksheetListModel;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by xeq on 16/8/3.
 */
public class OrderDetail implements Serializable {


    public final static int RelationResponsible = 1;         /*1表示责任人*/
    public final static int RelationTeam = 2;                /*2表示团队*/
    public final static int RelationInvolved = 3;            /*3表示业务相关*/

    public boolean wfSwitch;
    public int relationState;

    public String attachmentUUId;
    public String id;
    public String companyId;
    public String customerId;
    public String title;
    public String customerName;
    public String directorName;
    public String creatorName;

    //订单状态 1:待审核  2:未通过  3: 进行中(订单审核完成) 4: 已完成(回款完成)  5: 意外终止 7:审批中
    public int status;
    public float dealMoney;//成交金额
    public float planMoney;//计划回款金额
    public int backMoney;
    public double ratePayment;//回款率
    public double salesAmount;
    public double discount;
    public int attachmentCount;
    public int recordNum;
    public int planNum;
    public int worksheetNum;
    public long createdAt;
    public long startAt;
    public long endAt;
    public String proName;
    public String wfId;
    public String orderNum;//订单编号
    public String remark;
    public ArrayList<EstimateAdd> paymentRecords;
    //public List<ExtensionDatas> extensionDatas;
    public ArrayList<ContactLeftExtras> extensionDatas;
    public ArrayList<SaleIntentionalProduct> proInfo;
    public ArrayList<OrderWorksheetListModel> reWorkSheet;
    public String wfName;
    public String directorId;//负责人id
    public String endReason;
    public String unExpectedWfId;
    public String unExpectedWfName;
    public int unExpectedStatus;

    public String getTerminateWfStatus() {
        return SubmitStatus.getStatus(unExpectedStatus).getName();
    }

    public String getTerminateWfDisplayTitle() {
        StringBuilder builder = new StringBuilder();
        builder.append(unExpectedWfName);
        builder.append("(");
        builder.append(getTerminateWfStatus());
        builder.append(")");
        return builder.toString().trim();
    }
}
