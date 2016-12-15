package com.loyo.oa.v2.activityui.setting.bean;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.attendance.AttendanceManagerActivity;
import com.loyo.oa.v2.activityui.customer.CustomerDetailInfoActivity_;
import com.loyo.oa.v2.activityui.discuss.HaitMyActivity;
import com.loyo.oa.v2.activityui.followup.FollowUpDetailsActivity;
import com.loyo.oa.v2.activityui.order.OrderDetailActivity;
import com.loyo.oa.v2.activityui.other.BulletinManagerActivity_;
import com.loyo.oa.v2.activityui.project.ProjectInfoActivity_;
import com.loyo.oa.v2.activityui.signin.SigninDetailsActivity;
import com.loyo.oa.v2.activityui.tasks.TasksInfoActivity_;
import com.loyo.oa.v2.activityui.wfinstance.WfinstanceInfoActivity_;
import com.loyo.oa.v2.activityui.work.WorkReportsInfoActivity_;
import com.loyo.oa.v2.activityui.worksheet.WorksheetDetailActivity;
import com.loyo.oa.v2.common.ExtraAndResult;

import java.lang.reflect.Type;

/**
 * Created by xeq on 16/11/7.
 */

public enum SystemMessageItemType {
    /*讨论*/
    MSG_DISCUSS(14) {
        public int getIcon() {
            return R.drawable.icon_sys_discuss;
        }

        public Class<?> getItemClass() {
            return HaitMyActivity.class;
        }

        public String getExtraName() {
            return " ";
        }
    },
    /*任务*/
    MSG_TASK(2) {
        public int getIcon() {
            return R.drawable.icon_sys_task;
        }

        public Class<?> getItemClass() {
            return TasksInfoActivity_.class;
        }

        public String getExtraName() {
            return ExtraAndResult.EXTRA_ID;
        }
    },
    /*任务 完成*/
    MSG_TASK_COMPLETE(21) {
        public int getIcon() {
            return R.drawable.icon_sys_task;
        }

        public Class<?> getItemClass() {
            return TasksInfoActivity_.class;
        }

        public String getExtraName() {
            return ExtraAndResult.EXTRA_ID;
        }
    },
    /*工作报告  已经点评*/
    MSG_WORKREPORT_COMPLETE(23) {
        public int getIcon() {
            return R.drawable.icon_sys_workreport;
        }

        public Class<?> getItemClass() {
            return WorkReportsInfoActivity_.class;
        }

        public String getExtraName() {
            return ExtraAndResult.EXTRA_ID;
        }
    },
    /*工作报告*/
    MSG_WORKREPORT(1) {
        public int getIcon() {
            return R.drawable.icon_sys_workreport;
        }

        public Class<?> getItemClass() {
            return WorkReportsInfoActivity_.class;
        }

        public String getExtraName() {
            return ExtraAndResult.EXTRA_ID;
        }
    },
    /*审批 通过 详情*/
    MSG_APPROVAL_COMPLETE(22) {
        public int getIcon() {
            return R.drawable.icon_sys_approval;
        }

        public Class<?> getItemClass() {
            return WfinstanceInfoActivity_.class;
        }

        public String getExtraName() {
            return ExtraAndResult.EXTRA_ID;
        }
    },
    /*审批详情*/
    MSG_APPROVAL(12) {
        public int getIcon() {
            return R.drawable.icon_sys_approval;
        }

        public Class<?> getItemClass() {
            return WfinstanceInfoActivity_.class;
        }

        public String getExtraName() {
            return ExtraAndResult.EXTRA_ID;
        }
    },
    /*项目*/
    MSG_PROJECT(5) {
        public int getIcon() {
            return R.drawable.icon_sys_project;
        }

        public Class<?> getItemClass() {
            return ProjectInfoActivity_.class;
        }

        public String getExtraName() {
            return "projectId";
        }
    },
    /*g跟进动态  评论*/
    MSG_FOLLOWUP_COMMENTS(31) {
        public int getIcon() {
            return R.drawable.icon_sys_followup;
        }

        public Class<?> getItemClass() {
            return FollowUpDetailsActivity.class;
        }

        public String getExtraName() {
            return "id";
        }
    },/*g跟进动态*/
    MSG_FOLLOWUP(9) {
        public int getIcon() {
            return R.drawable.icon_sys_followup;
        }

        public Class<?> getItemClass() {
            return FollowUpDetailsActivity.class;
        }

        public String getExtraName() {
            return "id";
        }
    },
    /*客户拜访 评论*/
    MSG_SIGNIN_COMMENTS(30) {
        public int getIcon() {
            return R.drawable.icon_sys_signin;
        }

        public Class<?> getItemClass() {
            return SigninDetailsActivity.class;
        }

        public String getExtraName() {
            return "id";
        }
    }, /*客户拜访*/
    MSG_SIGNIN(4) {
        public int getIcon() {
            return R.drawable.icon_sys_signin;
        }

        public Class<?> getItemClass() {
            return SigninDetailsActivity.class;
        }

        public String getExtraName() {
            return "id";
        }
    },
    /*通知公告*/
    MSG_BULLETIN(19) {
        public int getIcon() {
            return R.drawable.icon_sys_bulletin;
        }

        public Class<?> getItemClass() {
            return BulletinManagerActivity_.class;
        }

        public String getExtraName() {
            return ExtraAndResult.EXTRA_ID;
        }
    },
    /*客户详情 服务端的老数据 要求18也是客户的详情*/
    MSG_CUSTOMER2(18) {
        public int getIcon() {
            return R.drawable.icon_sys_custom;
        }

        public Class<?> getItemClass() {
            return CustomerDetailInfoActivity_.class;
        }

        public String getExtraName() {
            return "Id";
        }
    },
    /*客户详情*/
    MSG_CUSTOMER(6) {
        public int getIcon() {
            return R.drawable.icon_sys_custom;
        }

        public Class<?> getItemClass() {
            return CustomerDetailInfoActivity_.class;
        }

        public String getExtraName() {
            return "Id";
        }
    },
    // TODO 公海客户暂还没有定义
    /*公海客户详情*/
    MSG_CUSTOMER_PUBLIC(40) {
        public int getIcon() {
            return R.drawable.icon_sys_custom_public;
        }

        public Class<?> getItemClass() {
            return CustomerDetailInfoActivity_.class;
        }

        public String getExtraName() {
            return "Id";
        }
    },
    /*订单详情*/
    MSG_ORDER(25) {
        public int getIcon() {
            return R.drawable.icon_sys_order;
        }

        public Class<?> getItemClass() {
            return OrderDetailActivity.class;
        }

        public String getExtraName() {
            return ExtraAndResult.EXTRA_ID;
        }
    },
    /*订单详情 回款记录*/
    MSG_ORDER_RECORD(26) {
        public int getIcon() {
            return R.drawable.icon_sys_order;
        }

        public Class<?> getItemClass() {
            return OrderDetailActivity.class;
        }

        public String getExtraName() {
            return ExtraAndResult.EXTRA_ID;
        }
    },
    /*订单详情 回款计划*/
    msg_order_plan(27) {
        public int getIcon() {
            return R.drawable.icon_sys_order;
        }

        public Class<?> getItemClass() {
            return OrderDetailActivity.class;
        }

        public String getExtraName() {
            return ExtraAndResult.EXTRA_ID;
        }
    },
    /*工单详情*/
    MSG_Worksheet(28) {
        public int getIcon() {
            return R.drawable.icon_sys_worksheet;
        }

        public Class<?> getItemClass() {
            return WorksheetDetailActivity.class;
        }

        public String getExtraName() {
            return ExtraAndResult.EXTRA_ID;
        }
    },
    /*工单 事件详情*/
    MSG_WorksheetEvent(29) {
        public int getIcon() {
            return R.drawable.icon_sys_worksheet;
        }

        public Class<?> getItemClass() {
            return WorksheetDetailActivity.class;
        }

        public String getExtraName() {
            return ExtraAndResult.EXTRA_ID;
        }
    },
    /*考勤*/
    MSG_ATTENDANCE(15) {
        public int getIcon() {
            return R.drawable.icon_sys_attendance;
        }

        public Class<?> getItemClass() {
            return AttendanceManagerActivity.class;
        }

        public String getExtraName() {
            return ExtraAndResult.EXTRA_ID;
        }
    },
    /*系统消息  转移超级用户*/
    MOVE_SUPER_USER(24) {
        public int getIcon() {
            return R.drawable.icon_sys_sys;
        }

        public Class<?> getItemClass() {
            return WorkReportsInfoActivity_.class;
        }

        public String getExtraName() {
            return ExtraAndResult.EXTRA_ID;
        }
    };


    public int value;

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

    /**
     * 获取跳转的class对象
     */
    public abstract Class<?> getItemClass();

    /**
     * item 跳转时的key值
     *
     * @return
     */
    public abstract String getExtraName();

//    LYMsgWorkReport   = 1,           //1.工作汇报*
//    LYMsgTask,                       //2.任务*
//    LYMsgSchedule,                   //3.日程(没有了)
//    LYMsgLegWork,                    //4.外勤(改成拜访了)
//    LYMsgProject,                    //5.项目
//    LYMsgCustomer,                   //6.客户*
//    LYMsgContact,                    //7.客户联系人
//    LYMsgOpportunity,                //8.销售机会*
//    LYMsgSaleActivity,               //9.销售动态(跟进动态)    暂且放在客户详情里
//    LYMsgProduct,                    //10.产品
//    LYMsgCustomerVisit,              //11.客户拜访计划
//    LYMsgApproval,                   //12.快捷审批  审批*
//    LYMsgAttachment,                 //13.附件
//    LYMsgDiscuss,                    //14.讨论   我的讨论*
//    LYMsgAttendance,                 //15.考勤*
//    LYMsgDemand,                     //16.客户中购买意向数量  已无
//    LYMsgAttachNum,                  //17.客户中附件上传数量
//    LYMsgProcessStatisticsType,      //18.过程统计
//    LYMsgBulletin,                   //19.通知公告*
//    LYMsgTrackRule,                  //20.轨迹
//    LYMsgTaskFinshed,                //21.已完成的任务
//    LYMsgApprovalFinshed,            //22.已通过的审批
//    LYMsgWorkReportFinshed,          //23.已点评的工作报告
//    LYMsgMoveSuperUser,              //24.转移超级用户
//    LYMsgOrder,                      //25.订单*
//    LYMsgOrderRecord,                //26.订单回款记录
//    LYMsgOrderPlan,                  //27.订单计划/回款计划
//    LYMsgWorkSheet,                  //28.工单*
//    LYMsgWorkSheetEvent,             //29.工单事件

    /**
     * gson 序列化和反序列化
     */
    public static class SystemMessageSerializer implements JsonSerializer<SystemMessageItemType>,
            JsonDeserializer<SystemMessageItemType> {

        // 对象转为Json时调用,【序列化】
        @Override
        public JsonElement serialize(SystemMessageItemType state, Type arg1,
                                     JsonSerializationContext arg2) {
            return new JsonPrimitive(state.value);
        }

        // json转为对象时调用,【反序列化】
        @Override
        public SystemMessageItemType deserialize(JsonElement json, Type typeOfT,
                                                 JsonDeserializationContext context) throws JsonParseException {
            SystemMessageItemType[] list = SystemMessageItemType.values();
            for (int i = 0; i < list.length; i++) {
                if (list[i].value == json.getAsInt()) {
                    return list[i];
                }
            }
            return SystemMessageItemType.MSG_WORKREPORT;
        }
    }
}
