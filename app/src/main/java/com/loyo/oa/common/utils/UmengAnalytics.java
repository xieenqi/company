package com.loyo.oa.common.utils;

import android.content.Context;

import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.SharedUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

/**
 * 友盟分析 统计点击事件
 * Created by xeq on 17/1/7.
 */

public class UmengAnalytics {
    public static String businessQuery = "BusinessQuery";           //工商查询

    public static String addFollow = "add_follow";                  //新建跟进
    public static String addVisit = "add_visit";                    //新建拜访
    public static String addVisitAt = " add_visit_at";              //新建拜访-@
    public static String addVisitPhoto = "add_visit_photo";         //新建拜访-拍照
    public static String addVisitRecord = "add_visit_record";       //新建拜访-录音
    public static String cluesTeam = "clues_team";                  //切换至团队线索
    public static String createChanceMy = "create_chance_my";       //新建机会
    public static String createCluesMy = "create_clues_my";         //新建线索
    public static String createOrder = " create_order";             //新建订单
    public static String createWorkSheet = "create_work_order";     //新建工单
    public static String customerAdd = "customer_add";              //新建客户-手动创建
    public static String customerAddFollow = "customer_add_follow"; //客户详情页面-写跟进(快捷)
    public static String customerAddContact = "customer_address_book";  //新建客户-通讯录导入
    public static String customerCheckFollow = "customer_check_follow"; //客户详情页面点击-跟进动态
    public static String customerCheckFollowAddFollow = "customer_check_follow_add_follow";  //客户详情页面点击-跟进动态-写跟进
    public static String customerDelete = "customer_delete";        //客户详情页面-删除
    public static String customerEditTag = "customer_edit_tag";     //客户详情页面-修改标签
    public static String customerNearby = " customer_nearby";       //点击“附近的客户”
    public static String customerTopublic = "customer_topublic";    //客户详情页面-投入公海
    public static String customerVisit = "customer_visit";          //客户详情页-快捷拜访
    public static String deleteCluesMy = "delete_clues_my";         //我的-线索详情-删除
    public static String departmentChanceTeam = "department_chance_team";   //筛选-部门-团队机会
    public static String departmentOrderTeam = "department_order_team";     //筛选-部门-团队订单
    public static String departmentVisitTeam = "department_visit_team";     //筛选-部门-团队拜访
    public static String departmentWorkSheetTeam = "department_work_order_team";   //筛选-部门-团队工单
    public static String edit_clues_my = "edit_clues_my";                   //线索详情页-编辑-我的线索
    public static String filterFollow = "filter_follow";                    //筛选跟进-我的跟进
    public static String filterFollowTeam = "filter_follow_team";           //筛选跟进-团队跟进
    public static String frompublicPublic = "frompublic_public";            //挑入
    public static String homepageContact = "homepage_address-book";         //首页_通讯录
    public static String homepageApprovals = "homepage_approvals";          //首页_审批流程
    public static String homepageButton = "homepage_button";                //上方导航_应用
    public static String homepageClues = "homepage_clues";                      //首页_销售线索
    public static String homepageCustomer = "homepage_customer";                //首页_客户管理
    public static String homepageCustomerFollow = "homepage_customer-follow";   //首页_跟进动态
    public static String homepageCustomerVisit = "homepage_customer-visit";     //首页_客户拜访
    public static String homepageDiscussion = "homepage_discussion";            //首页_我的讨论
    public static String homepageLegworks = "homepage_legworks";                //首页_考勤管理
    public static String homepageNotice = "homepage_notice";                    //首页_公告通知
    public static String homepageOrder = "homepage_order";                      //首页_订单管理
    public static String homepage_projects = "homepage_projects";               //首页_项目管理
    public static String homepageQuickstart = "homepage_quickstart";            //首页_快速发起_按钮
    public static String homepageQuickstartApprove = "homepage_quickstart_approve";         //首页_快速发起_申请审批
    public static String homepageQuickstartChance = "homepage_quickstart_chance";           //首页_快速发起_新建机会
    public static String homepageQuickstartCustomer = "homepage_quickstart_customer";       //首页_快速发起_新建客户
    public static String homepageQuickstartFollow = "homepage_quickstart_follow";           //首页_快速发起_写跟进
    public static String homepageQuickstartLegworks = "homepage_quickstart_legworks";       //首页_快速发起_考勤打卡
    public static String homepageQuickstartOrder = "homepage_quickstart_order";             //首页_快速发起_新建订单
    public static String homepageQuickstartReport = "homepage_quickstart_report";          //首页_快速发起_提交报告
    public static String homepageQuickstartTask = "homepage_quickstart_task";               //首页_快速发起_新建任务
    public static String homepageQuickstartVisit = "homepage_quickstart_visit";             //首页_快速发起_客户拜访
    public static String homepageReport = "homepage_report";                       //首页_工作报告
    public static String homepageSaleChance = "homepage_sale-chance";              //首页_销售机会
    public static String homepageTasks = "homepage_tasks";                          //首页_任务计划
    public static String homepageWorkOrder = "homepage_work-order";                 //首页_工单管理
    public static String LeftNavBarFeedbackButton = "LeftNavBar_feedback_button";   //左侧导航_意见反馈_按钮
    public static String LeftNavBarInformationButton = "LeftNavBar_information_button";     //左侧导航_个人信息_按钮
    public static String LeftNavBar_notice_button = "LeftNavBar_notice_button";             //左侧导航_系统消息_按钮
    public static String LeftNavBar_option_button = "LeftNavBar_option_button";             //左侧导航_设置_按钮
//    public static String businessQuery = "BusinessQuery";
//    public static String businessQuery = "BusinessQuery";
//    public static String businessQuery = "BusinessQuery";
//    public static String businessQuery = "BusinessQuery";
//    public static String businessQuery = "BusinessQuery";
//    public static String businessQuery = "BusinessQuery";
//    public static String businessQuery = "BusinessQuery";
//    public static String businessQuery = "BusinessQuery";
//    public static String businessQuery = "BusinessQuery";
//    public static String businessQuery = "BusinessQuery";
//    public static String businessQuery = "BusinessQuery";
//    public static String businessQuery = "BusinessQuery";
//    public static String businessQuery = "BusinessQuery";
//    public static String businessQuery = "BusinessQuery";
//    public static String businessQuery = "BusinessQuery";
//    public static String businessQuery = "BusinessQuery";
//    public static String businessQuery = "BusinessQuery";
//    public static String businessQuery = "BusinessQuery";
//    public static String businessQuery = "BusinessQuery";
//    public static String businessQuery = "BusinessQuery";
//    public static String businessQuery = "BusinessQuery";
//    public static String businessQuery = "BusinessQuery";
//    public static String businessQuery = "BusinessQuery";
//    public static String businessQuery = "BusinessQuery";
//    public static String businessQuery = "BusinessQuery";
//    public static String businessQuery = "BusinessQuery";
//    public static String businessQuery = "BusinessQuery";
//    public static String businessQuery = "BusinessQuery";
//    public static String businessQuery = "BusinessQuery";
//    public static String businessQuery = "BusinessQuery";
//    public static String businessQuery = "BusinessQuery";
//    public static String businessQuery = "BusinessQuery";
//    public static String businessQuery = "BusinessQuery";
//    public static String businessQuery = "BusinessQuery";
//    public static String businessQuery = "BusinessQuery";
//    public static String businessQuery = "BusinessQuery";
//    public static String businessQuery = "BusinessQuery";
//    public static String businessQuery = "BusinessQuery";
//    public static String businessQuery = "BusinessQuery";
//    public static String businessQuery = "BusinessQuery";
//    public static String businessQuery = "BusinessQuery";
//    public static String businessQuery = "BusinessQuery";
//    public static String businessQuery = "BusinessQuery";
//    public static String businessQuery = "BusinessQuery";
//    public static String businessQuery = "BusinessQuery";
//    public static String businessQuery = "BusinessQuery";
//    public static String businessQuery = "BusinessQuery";

    public static void umengSend(Context mContext, String eventId) {
        if (!Config_project.isRelease) {
            return;
        }
        String time = DateTool.getDateTimeReal(System.currentTimeMillis() / 1000);
        LogUtil.d("转换时间：" + time);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UID", SharedUtil.get(MainApp.getMainApp(), ExtraAndResult.UID));//用户  name_id
        //TODO 公司名字需要后台提供
        map.put("CID", SharedUtil.get(MainApp.getMainApp(), ExtraAndResult.CID));//公司  name_id
        map.put("TIME", time);//点击时间
        MobclickAgent.onEvent(mContext, eventId, map);
    }
}
