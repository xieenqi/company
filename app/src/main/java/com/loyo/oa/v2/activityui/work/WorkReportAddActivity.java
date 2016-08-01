package com.loyo.oa.v2.activityui.work;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.commonview.SelectDetUserActivity2;
import com.loyo.oa.v2.activityui.commonview.SwitchView;
import com.loyo.oa.v2.activityui.project.ProjectSearchActivity;
import com.loyo.oa.v2.activityui.work.bean.HttpDefaultComment;
import com.loyo.oa.v2.activityui.other.adapter.ImageGridViewAdapter;
import com.loyo.oa.v2.activityui.signin.adapter.SignInGridViewAdapter;
import com.loyo.oa.v2.activityui.work.adapter.workReportAddgridViewAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.beans.Members;
import com.loyo.oa.v2.beans.NewUser;
import com.loyo.oa.v2.beans.PostBizExtData;
import com.loyo.oa.v2.beans.Project;
import com.loyo.oa.v2.activityui.work.bean.Reviewer;
import com.loyo.oa.v2.beans.WorkReport;
import com.loyo.oa.v2.activityui.work.bean.WorkReportDyn;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.IAttachment;
import com.loyo.oa.v2.point.IWorkReport;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.SelectPicPopupWindow;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.tool.ViewUtil;
import com.loyo.oa.v2.tool.WeeksDialog;
import com.loyo.oa.v2.customview.CountTextWatcher;
import com.loyo.oa.v2.customview.CusGridView;
import com.loyo.oa.v2.customview.SingleRowWheelView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

/**
 * 【工作报告】新建 编辑
 */

@EActivity(R.layout.activity_workreports_add)
public class WorkReportAddActivity extends BaseActivity {

    public static final int TYPE_CREATE = 1; //报告创建
    public static final int TYPE_EDIT = 2; //报告编辑
    public static final int TYPE_CREATE_FROM_COPY = 3; //报告复制
    public static final int TYPE_PROJECT = 4; //项目创建报告

    /**
     * 动态数据UI更新
     * */
    public static final int UPDATE_SUCCESS = 0x01;

    /**
     * 周报数据回调
     * */
    public static final int WEEK_RESULT    = 0x02;

    @ViewById
    SwitchView crm_switch;
    @ViewById
    ViewGroup img_title_left, img_title_right;
    @ViewById
    EditText edt_content;
    @ViewById
    RadioGroup rg;//工作 动态
    @ViewById
    ViewGroup layout_crm, layout_reviewer, layout_mproject, layout_type;
    @ViewById
    TextView wordcount;
    @ViewById
    TextView tv_crm;
    @ViewById
    TextView tv_project;
    @ViewById
    TextView tv_time, tv_toUser;
    @ViewById
    TextView tv_reviewer, tv_resignin;
    @ViewById
    ViewGroup layout_del;
    @ViewById
    ImageView img_title_toUser;
    @ViewById
    CusGridView gridView_photo;
    @ViewById
    GridView gv_workreports;
    @ViewById
    ViewGroup no_dysndata_workreports;

    @Extra
    String projectId;
    @Extra
    String projectTitle;

    @Extra("mWorkReport")
    WorkReport mWorkReport;
    @Extra("type")
    int type;

    private RadioButton rb1;
    private RadioButton rb2;
    private RadioButton rb3;
    private long beginAt, endAt;
    private boolean isDelayed = false;
    private int mSelectType = WorkReport.DAY;
    private int retroIndex;
    private int bizType = 1;
    private int uploadSize;
    private int uploadNum;
    private String currentValue;
    private String content;

    private WeeksDialog weeksDialog = null;
    private SignInGridViewAdapter signInGridViewAdapter;
    private ImageGridViewAdapter imageGridViewAdapter;
    private workReportAddgridViewAdapter workGridViewAdapter;
    private ArrayList<Attachment> lstData_Attachment = null;
    private ArrayList<NewUser> users = new ArrayList<>();
    private ArrayList<NewUser> depts = new ArrayList<>();
    private ArrayList<SelectPicPopupWindow.ImageInfo> pickPhots = new ArrayList<>();
    private String uuid = StringUtil.getUUID();
    private Reviewer mReviewer;
    private Members members = new Members();

    private ArrayList<WorkReportDyn> dynList;
    private StringBuffer joinUserId;
    private StringBuffer joinName;
    private PostBizExtData bizExtData;
    private StringBuffer joinUser;
    private String[] pastSevenDay = new String[7];
    private String[] pastThreeMonth = new String[3];

    private Handler mHandler = new Handler() {
        public void handleMessage(final Message msg) {

            switch (msg.what){

                //刷新动态数据UI
                case UPDATE_SUCCESS:
                    if (null == dynList || dynList.size() == 0) {
                        no_dysndata_workreports.setVisibility(View.VISIBLE);
                        gv_workreports.setVisibility(View.GONE);
                    } else {
                        no_dysndata_workreports.setVisibility(View.GONE);
                        gv_workreports.setVisibility(View.VISIBLE);
                        workGridViewAdapter = new workReportAddgridViewAdapter(mContext, dynList);
                        gv_workreports.setAdapter(workGridViewAdapter);
                    }
                    break;

                //周报数据回调
                case WEEK_RESULT:
                    beginAt = weeksDialog.GetBeginandEndAt()[0];
                    endAt = weeksDialog.GetBeginandEndAt()[1];
                    openDynamic(beginAt/1000+"",endAt/1000+"");
                    break;
            }
        }
    };

    @SuppressLint("WrongViewCast")
    @AfterViews
    void initViews() {
        super.setTitle("新建工作报告");
        ViewUtil.OnTouchListener_view_transparency touch = ViewUtil.OnTouchListener_view_transparency.Instance();

        img_title_left.setOnTouchListener(touch);
        img_title_right.setOnTouchListener(touch);
        layout_del.setOnTouchListener(touch);
        layout_reviewer.setOnTouchListener(touch);
        tv_resignin.setOnTouchListener(touch);
        layout_mproject.setOnTouchListener(touch);
        edt_content.addTextChangedListener(new CountTextWatcher(wordcount));

        rb1 = (RadioButton) findViewById(R.id.rb1);
        rb2 = (RadioButton) findViewById(R.id.rb2);
        rb3 = (RadioButton) findViewById(R.id.rb3);

        if (weeksDialog == null) {
            weeksDialog = new WeeksDialog(tv_time,mHandler);
        }

        /**动态统计开关*/
        crm_switch.setState(false);
        crm_switch.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn() {
                crm_switch.setState(true);
                crmSwitch(true);
            }

            @Override
            public void toggleToOff() {
                crm_switch.setState(false);
                crmSwitch(false);
            }
        });

        if (null != mWorkReport) {
            if (type == TYPE_EDIT) {
                super.setTitle("编辑工作报告");
                uuid = mWorkReport.attachmentUUId;
                dynList = mWorkReport.crmDatas;
                crm_switch.setState(null == dynList ? false : true);
                mHandler.sendEmptyMessage(UPDATE_SUCCESS);
                layout_crm.setVisibility(View.VISIBLE);
            }

            try {
                mReviewer = mWorkReport.reviewer;
                members = mWorkReport.members;
                projectId = mWorkReport.ProjectInfo.getId();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            switch (mWorkReport.type) {
                case WorkReport.DAY:
                    rg.check(R.id.rb1);
                    break;
                case WorkReport.WEEK:
                    rg.check(R.id.rb2);
                    break;
                case WorkReport.MONTH:
                    rg.check(R.id.rb3);
                    break;
                default:
                    break;
            }

            NewUser reviewer = null != mWorkReport.reviewer && null != mWorkReport.reviewer
                    .user ? mWorkReport.reviewer.user : null;
            tv_reviewer.setText(null == reviewer ? "" : reviewer.getName());
            tv_toUser.setText(getMenberText());
            edt_content.setText(mWorkReport.content);

            if (null != mWorkReport.ProjectInfo) {
                tv_project.setText(mWorkReport.ProjectInfo.title);
            }
            //附件暂时不能做
        } else {
            rg.check(R.id.rb1);
        }
        init_gridView_photo();

        /*来自不同的业务 判断*/
        if (type == TYPE_EDIT) {
            tv_resignin.setVisibility(View.GONE);
            rb1.setEnabled(false);
            rb2.setEnabled(false);
            rb3.setEnabled(false);
            //getEditAttachments();
            gridView_photo.setVisibility(View.GONE);

        } else if (type == TYPE_PROJECT) {
            projectAddWorkReport();
        }
        initRetroDate();
        getDefaultComment();
    }

    /**
     * 获取默认点评人
     */
    private void getDefaultComment() {
        RestAdapterFactory.getInstance().build(Config_project.ADD_WORK_REPORT_PL).create(IWorkReport.class)
                .defaultComment(new RCallback<HttpDefaultComment>() {
                    @Override
                    public void success(final HttpDefaultComment reviewer, final Response response) {
                        HttpErrorCheck.checkResponse(response);
                        mReviewer = new Reviewer();
                        if (reviewer.reviewer != null) {
                            mReviewer.user = reviewer.reviewer.user;
                            tv_reviewer.setText(reviewer.reviewer.user.getName());
                        }
                    }

                    @Override
                    public void failure(final RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                        super.failure(error);
                    }
                });
    }


    /**
     * 项目 过来创建 工作报告
     */
    public void projectAddWorkReport() {
        if (!TextUtils.isEmpty(projectId)) {
            layout_mproject.setEnabled(false);
            tv_project.setText(projectTitle);
        }
    }

    /**
     * 获取传过来 的menber信息
     *
     * @return
     */
    private String getMenberText() {

        joinUser = new StringBuffer();
        joinUserId = new StringBuffer();

        for (int i = 0; i < mWorkReport.members.getAllData().size(); i++) {
            joinUser.append(mWorkReport.members.getAllData().get(i).getName() + ",");
            joinUserId.append(mWorkReport.members.getAllData().get(i).getId() + ",");

        }
        return joinUser.toString();

    }


    /**
     * 获取附件(编辑)
     */
    private void getEditAttachments() {
        Utils.getAttachments(uuid, new RCallback<ArrayList<Attachment>>() {
            @Override
            public void success(final ArrayList<Attachment> attachments, final Response response) {
                HttpErrorCheck.checkResponse(response);
                lstData_Attachment = attachments;
                edit_gridView_photo();
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
            }
        });
    }

    /**
     * 切换统计开关
     *
     * @param b
     */
    private void crmSwitch(final boolean b) {
        long beginTime,endTime;
        if (b) {
            switch (mSelectType) {
                //本日
                case WorkReport.DAY:
                    if(tv_time.getText().toString().contains("补签")){
                        beginTime = beginAt/1000;
                        endTime   = endAt/1000;
                    }else{
                        beginTime = DateTool.getCurrentMoringMillis()/1000;
                        endTime = DateTool.getNextMoringMillis()/1000;
                    }
                    openDynamic(beginTime + "", endTime + "");
                    break;
                //本周
                case WorkReport.WEEK:
                    if(tv_time.getText().toString().contains("补签")){
                        beginTime = weeksDialog.GetBeginandEndAt()[0]/1000;
                        endTime = weeksDialog.GetBeginandEndAt()[1]/1000;
                    }else{
                        beginTime = DateTool.getBeginAt_ofWeek()/1000;
                        endTime = DateTool.getEndAt_ofWeek()/1000;
                    }
                    openDynamic(beginTime + "", endTime + "");
                    break;
                //本月
                case WorkReport.MONTH:
                    if(tv_time.getText().toString().contains("补签")){
                        beginTime = beginAt/1000;
                        endTime   = endAt/1000;
                    }else{
                        beginTime = DateTool.getBeginAt_ofWeek()/1000;
                        endTime = DateTool.getEndAt_ofWeek()/1000;
                    }
                    openDynamic(beginTime + "", endTime + "");
                    break;

                default:
                    break;
            }
        }
        layout_crm.setVisibility(b ? View.VISIBLE : View.GONE);
    }


    /**
     * 日报checkbox
     */
    @CheckedChange(R.id.rb1)
    void dayClick(final CompoundButton button, final boolean b) {
        if (!b) {
            return;
        }
        isDelayed = false;
        currentValue = pastSevenDay[0];
        openDynamic(DateTool.getCurrentMoringMillis() / 1000 + "", DateTool.getNextMoringMillis() / 1000 + "");
        tv_crm.setText("本日工作动态统计");
        beginAt = DateTool.getBeginAt_ofDay();
        endAt = DateTool.getEndAt_ofDay();
        tv_time.setText(app.df4.format(beginAt));
        mSelectType = WorkReport.DAY;
    }

    /**
     * 周报checkbox
     */
    @CheckedChange(R.id.rb2)
    void weekClick(final CompoundButton button, final boolean b) {
        if (!b) {
            return;
        }
        isDelayed = false;
        openDynamic(DateTool.getBeginAt_ofWeek() / 1000 + "", DateTool.getEndAt_ofWeek() / 1000 + "");
        tv_crm.setText("本周工作动态统计");
        beginAt = weeksDialog.getNowBeginandEndAt()[0];
        endAt = weeksDialog.getNowBeginandEndAt()[1];
        tv_time.setText(weeksDialog.GetDefautlText());
        mSelectType = WorkReport.WEEK;
    }

    /**
     * 月报checkbox
     */
    @CheckedChange(R.id.rb3)
    void monthClick(final CompoundButton button, final boolean b) {
        if (!b) {
            return;
        }
        isDelayed = false;
        currentValue = pastThreeMonth[0];
        openDynamic(DateTool.getBeginAt_ofMonthMills() / 1000 + "", DateTool.getEndAt_ofMonth() / 1000 + "");
        tv_crm.setText("本月工作动态统计");
        beginAt = DateTool.getEndAt_ofMonth();//DateTool.getBeginAt_ofMonth()
        endAt = DateTool.getEndAt_ofMonth();
        DateTool.calendar = Calendar.getInstance();
        int year = DateTool.calendar.get(Calendar.YEAR);
        int month = DateTool.calendar.get(Calendar.MONTH);
        tv_time.setText(year + "." + String.format("%02d", (month + 1)));
        mSelectType = WorkReport.MONTH;

    }

    /**
     * 编辑gridView绑定
     */
    void edit_gridView_photo() {
        if (lstData_Attachment == null) {
            lstData_Attachment = new ArrayList<>();
        }
        for (Attachment attachment : lstData_Attachment) {
            pickPhots.add(new SelectPicPopupWindow.ImageInfo(attachment.url));
        }
        signInGridViewAdapter = new SignInGridViewAdapter(this, lstData_Attachment, true, true, true, 0);
        SignInGridViewAdapter.setAdapter(gridView_photo, signInGridViewAdapter);
    }

    /**
     * 新建gridView绑定
     */
    void init_gridView_photo() {
        imageGridViewAdapter = new ImageGridViewAdapter(this, true, true, 0, pickPhots);
        ImageGridViewAdapter.setAdapter(gridView_photo, imageGridViewAdapter);
    }

    @Click({R.id.tv_resignin, R.id.img_title_left, R.id.img_title_right, R.id.layout_reviewer, R.id.layout_toUser, R.id.layout_del, R.id.layout_mproject})
    void onClick(final View v) {
        Bundle mBundle;
        switch (v.getId()) {

            /*返回*/
            case R.id.img_title_left:
                app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, RESULT_CANCELED, null);
                break;

            /*提交*/
            case R.id.img_title_right:
                content = edt_content.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    Toast(getString(R.string.app_content) + getString(R.string.app_no_null));
                    break;
                }
                if (mReviewer == null) {
                    Toast(getString(R.string.review_user) + getString(R.string.app_no_null));
                    break;
                } else {
                    if (mReviewer.user != null && MainApp.user.id.equals(mReviewer.user.getId())) {
                        Toast("点评人不能是自己");
                        break;
                    }
                }

                //没有附件
                if (pickPhots.size() == 0) {
                    requestCommitWork();
                    //有附件
                } else {
                    newUploadAttachement();
                }

                break;

            /*选择日期*/
            case R.id.tv_resignin:
                selectDate();
                break;

            /*点评人*/
            case R.id.layout_reviewer:
                SelectDetUserActivity2.startThisForOnly(WorkReportAddActivity.this, null);
                overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
                break;

            /*抄送人*/
            case R.id.layout_toUser:
                SelectDetUserActivity2.startThisForAllSelect(WorkReportAddActivity.this, joinUserId == null ? null : joinUserId.toString(), true);
                break;

            case R.id.layout_del:
                users.clear();
                depts.clear();
                tv_toUser.setText("");
                layout_del.setVisibility(View.GONE);
                img_title_toUser.setVisibility(View.VISIBLE);
                break;

            /*选择项目归档*/
            case R.id.layout_mproject:
                mBundle = new Bundle();
                mBundle.putInt("from", WORK_ADD);
                mBundle.putInt(ExtraAndResult.EXTRA_STATUS, 1);
                app.startActivityForResult(this, ProjectSearchActivity.class, MainApp.ENTER_TYPE_RIGHT, FinalVariables.REQUEST_SELECT_PROJECT, mBundle);
                break;

            default:
                break;
        }
    }

    /**
     * 开启动态统计数据
     */
    public void openDynamic(final String startTime, final String endTime) {
        showLoading("");
        HashMap<String, Object> map = new HashMap<>();
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        RestAdapterFactory.getInstance().build(Config_project.SIGNLN_TEM).create(IWorkReport.class)
                .getDynamic(map, new RCallback<ArrayList<WorkReportDyn>>() {
                    @Override
                    public void success(final ArrayList<WorkReportDyn> dyn, final Response response) {
                        HttpErrorCheck.checkResponse(response);
                        dynList = dyn;
                        mHandler.sendEmptyMessage(UPDATE_SUCCESS);
                    }

                    @Override
                    public void failure(final RetrofitError error) {
                        super.failure(error);
                        HttpErrorCheck.checkError(error);
                    }
                });
    }

    /**
     * 编辑报告请求
     */
    public void updateReport(final HashMap map) {
        RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(IWorkReport.class).updateWorkReport(mWorkReport.getId(), map, new RCallback<WorkReport>() {
            @Override
            public void success(final WorkReport workReport, final Response response) {
                HttpErrorCheck.checkResponse(response);
                Toast(getString(R.string.app_update) + getString(R.string.app_succeed));
                dealResult(workReport);
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
            }
        });
    }

    /**
     * 新建报告请求
     */
    public void creteReport(final HashMap map) {
        RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(IWorkReport.class).createWorkReport(map, new RCallback<WorkReport>() {
            @Override
            public void success(final WorkReport workReport, final Response response) {
                HttpErrorCheck.checkResponse(response);
                Toast(getString(R.string.app_add) + getString(R.string.app_succeed));
                dealResult(workReport);
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
            }
        });
    }


    /**
     * 处理服务器返回结果
     *
     * @param workReport
     */
    private void dealResult(final WorkReport workReport) {
        if (workReport != null) {
            workReport.setViewed(true);
            Intent intent = getIntent();
            intent.putExtra("data", workReport);
            app.finishActivity(WorkReportAddActivity.this, MainApp.ENTER_TYPE_LEFT, 0x09, intent);
        }
    }

    /**
     * 补签显示数据初始化
     */
    public void initRetroDate() {

        /*过去7天*/
        for (int i = 0; i < 7; i++) {
            Calendar cl = Calendar.getInstance();
            cl.add(Calendar.DAY_OF_MONTH, -(i + 1));
            String time = app.df4.format(cl.getTime());
            pastSevenDay[i] = time;
        }

        currentValue = pastSevenDay[0];

        /*过去3月*/
        Calendar cl;
        String month;
        for (int i = 0; i < 3; i++) {
            switch (i) {
                case 0:
                    cl = Calendar.getInstance();
                    cl.add(Calendar.DAY_OF_MONTH, -30);
                    month = app.df15.format(cl.getTime());
                    pastThreeMonth[i] = month;
                    break;

                case 1:
                    cl = Calendar.getInstance();
                    cl.add(Calendar.DAY_OF_MONTH, -60);
                    month = app.df15.format(cl.getTime());
                    pastThreeMonth[i] = month;
                    break;

                case 2:
                    cl = Calendar.getInstance();
                    cl.add(Calendar.DAY_OF_MONTH, -90);
                    month = app.df15.format(cl.getTime());
                    pastThreeMonth[i] = month;
                    break;
            }
        }
    }

    /**
     * wheel单列组件初始化
     */
    public View singleRowSelect(String[] arrlst) {
        View outerView = LayoutInflater.from(this).inflate(R.layout.wheel_view, null);
        SingleRowWheelView wv = (SingleRowWheelView) outerView.findViewById(R.id.wheel_view_wv);
        wv.setOffset(2);//为了界面好看，故意将index多加2条，因此取item下标时，要-2
        wv.setItems(Arrays.asList(arrlst));
        //wv.setSeletion(3);
        wv.setOnWheelViewListener(new SingleRowWheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                currentValue = item;
                retroIndex = selectedIndex - 2;
            }
        });
        return outerView;
    }

    /**
     * wheelDialog弹出选择框(日报 月报)
     */
    public void showSingleRowAlert(String[] arrlist, String title) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setView(singleRowSelect(arrlist))
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (mSelectType) {
                            case 1:
                                currentValue = pastSevenDay[retroIndex];
                                beginAt = DateTool.getSomeDayBeginAt(retroIndex);
                                endAt = DateTool.getSomeDayEndAt(retroIndex);
                                break;

                            case 3:
                                currentValue = pastThreeMonth[retroIndex];
                                beginAt = DateTool.getSomeMonthBeginAt(retroIndex);
                                endAt = DateTool.getSomeMonthEndAt(retroIndex);
                                break;
                        }

                        tv_time.setText(currentValue + "(补签)");
                        retroIndex = 0;
                        openDynamic(beginAt/1000+"",endAt/1000+"");
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }

    /**
     * 补签日期选择
     */
    void selectDate() {
        switch (mSelectType) {
            /*日报补签*/
            case WorkReport.DAY:
                showSingleRowAlert(pastSevenDay, "日报补签");
                break;

            /*周报补签*/
            case WorkReport.WEEK:
                weeksDialog.showChoiceDialog("周报补签").show();
                break;

            /*月报补签*/
            case WorkReport.MONTH:
                showSingleRowAlert(pastThreeMonth, "月报补签");
                break;

            default:
                break;
        }
    }

    /**
     * 提交报告
     */
    private void requestCommitWork() {
        if (pickPhots.size() == 0) {
            showLoading("正在提交");
        }

        bizExtData = new PostBizExtData();
        if (type == TYPE_EDIT) {
            bizExtData.setAttachmentCount(mWorkReport.bizExtData.getAttachmentCount());
        } else {
            bizExtData.setAttachmentCount(pickPhots.size());
        }
        isDelayed = tv_time.getText().toString().contains("补签") ? true : false;
        if (mSelectType == 2 && isDelayed) {
            beginAt = weeksDialog.GetBeginandEndAt()[0];
            endAt = weeksDialog.GetBeginandEndAt()[1];
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("content", content);
        map.put("type", mSelectType);
        map.put("beginAt", beginAt / 1000);
        map.put("endAt", endAt / 1000);
        if (!TextUtils.isEmpty(projectId)) {
            map.put("projectId", projectId);
        }
        map.put("attachmentUUId", uuid);
        map.put("bizExtData", bizExtData);
        map.put("reviewer", mReviewer);//点评人
        map.put("members", members);//抄送人
        if (null != dynList) {
            map.put("crmDatas", dynList);//工作动态统计
        }

        if (type != TYPE_EDIT) {
            map.put("isDelayed", isDelayed);
        }
        LogUtil.d(" 报告参数   " + app.gson.toJson(map));

        /*报告新建／编辑*/
        if (type == TYPE_EDIT) {
            updateReport(map);
        } else {
            creteReport(map);
        }
    }

    /**
     * 批量上传附件
     */
    private void newUploadAttachement() {
        showLoading("正在提交");
        try {
            uploadSize = 0;
            uploadNum = pickPhots.size();
            LogUtil.dee("pickPhots siez:" + pickPhots.size());
            for (SelectPicPopupWindow.ImageInfo item : pickPhots) {
                Uri uri = Uri.parse(item.path);
                File newFile = Global.scal(this, uri);
                if (newFile != null && newFile.length() > 0) {
                    if (newFile.exists()) {
                        TypedFile typedFile = new TypedFile("image/*", newFile);
                        TypedString typedUuid = new TypedString(uuid);
                        RestAdapterFactory.getInstance().build(Config_project.API_URL_ATTACHMENT()).create(IAttachment.class).newUpload(typedUuid, bizType, typedFile,
                                new RCallback<Attachment>() {
                                    @Override
                                    public void success(final Attachment attachments, final Response response) {
                                        uploadSize++;
                                        if (uploadSize == uploadNum) {
                                            requestCommitWork();
                                        }
                                    }

                                    @Override
                                    public void failure(final RetrofitError error) {
                                        super.failure(error);
                                        HttpErrorCheck.checkError(error);
                                    }
                                });
                    }
                }
            }
        } catch (Exception ex) {
            Global.ProcException(ex);
        }
    }

    /**
     * 删除附件
     */
    private void deleteAttachement(final Intent data) {
        try {
            final Attachment delAttachment = (Attachment) data.getSerializableExtra("delAtm");
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("bizType", bizType);
            map.put("uuid", uuid);
            RestAdapterFactory.getInstance().build(Config_project.API_URL_ATTACHMENT()).create(IAttachment.class).remove(String.valueOf(delAttachment.getId()), map, new RCallback<Attachment>() {
                @Override
                public void success(final Attachment attachment, final Response response) {
                    Toast("删除附件成功!");
                    lstData_Attachment.remove(delAttachment);
                    signInGridViewAdapter.notifyDataSetChanged();
                }

                @Override
                public void failure(final RetrofitError error) {
                    HttpErrorCheck.checkError(error);
                    Toast("删除附件失败!");
                    super.failure(error);
                }
            });
        } catch (Exception e) {
            Global.ProcException(e);
        }
    }


    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            /*所属项目回调*/
            case FinalVariables.REQUEST_SELECT_PROJECT:
                Project _project = (Project) data.getSerializableExtra("data");
                if (null != _project) {
                    projectId = _project.id;
                    tv_project.setText(_project.title);
                } else {
                    projectId = "";
                    tv_project.setText("无");
                }
                break;

            /*上传附件回调*/
            case SelectPicPopupWindow.GET_IMG:
                pickPhots.addAll((ArrayList<SelectPicPopupWindow.ImageInfo>) data.getSerializableExtra("data"));
                init_gridView_photo();
                break;

            /*删除附件回调*/
            case FinalVariables.REQUEST_DEAL_ATTACHMENT:
                if (type == TYPE_EDIT) {
                    deleteAttachement(data);
                } else {
                    pickPhots.remove(data.getExtras().getInt("position"));
                    init_gridView_photo();
                }
                break;

            //用户单选, 点评人
            case SelectDetUserActivity2.REQUEST_ONLY:
                NewUser u = (NewUser) data.getSerializableExtra("data");
                mReviewer = new Reviewer(u);
                mReviewer.user = u;
                tv_reviewer.setText(u.getRealname());
                break;

            //用户选择, 抄送人
            case SelectDetUserActivity2.REQUEST_ALL_SELECT:
                members = (Members) data.getSerializableExtra("data");
                joinName = new StringBuffer();
                joinUserId = new StringBuffer();
                if (members.users.size() == 0 && members.depts.size() == 0) {
                    tv_toUser.setText("无抄送人");
                    joinUserId.reverse();
                } else {
                    if (null != members.depts) {
                        for (NewUser newUser : members.depts) {
                            joinName.append(newUser.getName() + ",");
                            joinUserId.append(newUser.getId() + ",");
                        }
                    }
                    if (null != members.users) {
                        for (NewUser newUser : members.users) {
                            joinName.append(newUser.getName() + ",");
                            joinUserId.append(newUser.getId() + ",");
                        }
                    }
                    if (!TextUtils.isEmpty(joinName)) {
                        joinName.deleteCharAt(joinName.length() - 1);
                    }
                    tv_toUser.setText(joinName.toString());
                }
                break;
            default:
                break;
        }
    }

}
