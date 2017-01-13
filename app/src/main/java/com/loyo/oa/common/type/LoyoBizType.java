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
    Schedule(3),
    /*项目*/
    Project(5),
    /*客户*/
    Customer(6),
    /*客户联系人  */
    CustomerContact(7),
    /*销售机会*/
    SaleChance(8),
    /*销售动态*/
    SaleActivity(9),
    /*产品*/
    Product(10),
    /*客户拜访计划*/
    CustomerVisit(11),
    /*快捷审批*/
    Approval(12),
    /*附件*/
    Attachment(13),
    /*讨论*/
    Discuss(14),
    /*考勤*/
    Attendance(15),
    /*客户中购买意向数量*/
    Demand(16),
    /*客户中附件上传数量*/
    AttachNum(17),
    /*过程统计*/
    ProcessStatisticsType(18),
    /*通知公告*/
    Bulletin(19),
    /*轨迹*/
    TrackRule(20),
    /*已完成的任务*/
    TaskFinshed(21),
    /*已通过的审批*/
    ApprovalFinshed(22),
    /*已点评的工作报告*/
    WorkReportFinshed(23),
    /*订单*/
    Order(25),
    /*订单回款记录*/
    OrderRecord(26),
    /*订单计划/回款计划*/
    OrderPlan(27),
    /*工单*/
    WorkSheet(28),
    /*工单事件*/
    WorkSheetEvent(29),
    /*拜访评论*/
    LegCommentType(30),
    /*跟进评论*/
    ActivityCommentType(31),
    /*电话录音*/
    IPVoice(32);

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
    //    Attendance                           //考勤              15
    //            Demand                               //客户中购买意向数量   16
    //    AttachNum                            //客户中附件上传数量   17
    //            ProcessStatisticsType                //过程统计           18
    //    Bulletin                             //通知公告           19
    //            TrackRule                            //轨迹              20
    //    TaskFinshed                          //已完成的任务        21
    //            ApprovalFinshed                      //已通过的审批        22
    //    WorkReportFinshed                    //已点评的工作报告     23
    //            MoveSuperUser                        //转移超级用户        24
    //    Order                                //订单              25
    //            OrderRecord                          //订单回款记录        26
    //    OrderPlan                            //订单计划/回款计划    27
    //            WorkSheet                            //工单               28
    //    WorkSheetEvent                       //工单事件           29
    //            LegCommentType                       //拜访评论            30
    //    ActivityCommentType                  //跟进评论            31
    //            IPVoice                              //电话录音            32
    //    )
    //

    private int code;

    LoyoBizType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
