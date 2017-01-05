package com.loyo.oa.common.type;

/**
 * Created by xeq on 17/1/5.
 */

public enum LoyoBizType {
    /*工作汇报*/
    WorkReport(1),
    /*任务*/
    Task(2),
    /*日程*/
    Schedule(3);

    public int type;

    LoyoBizType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

//    const (
//    _                     BizType = iota //忽略掉 0, 从 1 开始
//    WorkReport                           //工作汇报           1
//            Task                                 //任务              2
//    Schedule                             //日程              3
//            LegWork                              //外勤              4
//    Project                              //项目              5
//            Customer                             //客户              6
//    Contact                              //客户联系人         7
//            Opportunity                          //销售机会           8
//    SaleActivity                         //销售动态           9
//            Product                              //产品              10
//    CustomerVisit                        //客户拜访计划        11
//            Approval                             //快捷审批           12
//    Attachment                           //附件              13
//            Discuss                              //讨论              14
//    Attendance                           //考勤              16
//            Demand                               //客户中购买意向数量   17
//    AttachNum                            //客户中附件上传数量   18
//            ProcessStatisticsType                //过程统计           19
//    Bulletin                             //通知公告           20
//            TrackRule                            //轨迹              21
//    TaskFinshed                          //已完成的任务        22
//            ApprovalFinshed                      //已通过的审批        23
//    WorkReportFinshed                    //已点评的工作报告     24
//            MoveSuperUser                        //转移超级用户        25
//    Order                                //订单              27
//            OrderRecord                          //订单回款记录        28
//    OrderPlan                            //订单计划/回款计划    29
//            WorkSheet                            //工单               30
//    WorkSheetEvent                       //工单事件           31
//            LegCommentType                       //拜访评论            32
//    ActivityCommentType                  //跟进评论            33
//            IPVoice                              //电话录音            34
//    )
//
}
