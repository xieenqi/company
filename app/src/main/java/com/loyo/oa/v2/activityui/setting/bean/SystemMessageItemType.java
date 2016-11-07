package com.loyo.oa.v2.activityui.setting.bean;

import com.loyo.oa.v2.R;

/**
 * Created by xeq on 16/11/7.
 */

public enum SystemMessageItemType {
    MSG_WORKREPORT(1) {/*工作报告*/

        public int getIcon() {
            return R.drawable.icon_sys_custom;
        }
    },
    MSG_TASK(2) {/*任务*/

        public int getIcon() {
            return R.drawable.icon_ws_status2;
        }
    },
    MSG_PROJECT(5) { /*项目*/

        public int getIcon() {
            return R.drawable.icon_ws_status2;
        }
    }, MSG_SALE(8) {/*销售机会*/

        public int getIcon() {
            return R.drawable.icon_ws_status2;
        }
    },
    MSG_DISCUSS(14) {/*讨论*/

        public int getIcon() {
            return R.drawable.icon_ws_status2;
        }
    }, MSG_BULLETIN(19) {/*通知公告*/

        public int getIcon() {
            return R.drawable.icon_ws_status2;
        }
    };

    private int value;

    SystemMessageItemType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    /**
     * 获取显示图标
     */
    public abstract int getIcon();

//    LYMsgWorkReport   = 1,           //1.工作汇报
//    LYMsgTask,                       //2.任务
//    LYMsgSchedule,                   //3.日程
//    LYMsgLegWork,                    //4.外勤
//    LYMsgProject,                    //5.项目
//    LYMsgCustomer,                   //6.客户
//    LYMsgContact,                    //7.客户联系人
//    LYMsgOpportunity,                //8.销售机会
//    LYMsgSaleActivity,               //9.销售动态(跟进动态)    暂且放在客户详情里
//    LYMsgProduct,                    //10.产品
//    LYMsgCustomerVisit,              //11.客户拜访计划
//    LYMsgApproval,                   //12.快捷审批  审批
//    LYMsgAttachment,                 //13.附件
//    LYMsgDiscuss,                    //14.讨论   我的讨论
//    LYMsgAttendance,                 //15.考勤
//    LYMsgDemand,                     //16.客户中购买意向数量  已无
//    LYMsgAttachNum,                  //17.客户中附件上传数量
//    LYMsgProcessStatisticsType,      //18.过程统计
//    LYMsgBulletin,                   //19.通知公告
//    LYMsgTrackRule,                  //20.轨迹
//    LYMsgTaskFinshed,                //21.已完成的任务
//    LYMsgApprovalFinshed,            //22.已通过的审批
//    LYMsgWorkReportFinshed,          //23.已点评的工作报告
//    LYMsgMoveSuperUser,              //24.转移超级用户
//    LYMsgOrder,                      //25.订单
//    LYMsgOrderRecord,                //26.订单回款记录
//    LYMsgOrderPlan,                  //27.订单计划/回款计划
//    LYMsgWorkSheet,                  //28.工单
//    LYMsgWorkSheetEvent,             //29.工单事件
}
