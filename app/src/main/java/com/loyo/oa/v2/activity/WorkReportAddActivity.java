package com.loyo.oa.v2.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.adapter.SignInGridViewAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Attachment;
import com.loyo.oa.v2.beans.Member;
import com.loyo.oa.v2.beans.NewUser;
import com.loyo.oa.v2.beans.Project;
import com.loyo.oa.v2.beans.Reviewer;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.beans.WorkReport;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.point.IAttachment;
import com.loyo.oa.v2.point.IWorkReport;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.CommonSubscriber;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.SelectPicPopupWindow;
import com.loyo.oa.v2.tool.SharedUtil;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.tool.ViewUtil;
import com.loyo.oa.v2.tool.WeeksDialog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.android.schedulers.AndroidSchedulers;

@EActivity(R.layout.activity_workreports_add)
public class WorkReportAddActivity extends BaseActivity {

    public static final int TYPE_CREATE = 1;
    public static final int TYPE_EDIT = TYPE_CREATE + 1;
    public static final int TYPE_CREATE_FROM_COPY = TYPE_CREATE + 2;

    @ViewById ViewGroup img_title_left, img_title_right;

    @ViewById EditText edt_content;
    @ViewById RadioGroup rg;
    @ViewById ToggleButton crm_toggle;
    @ViewById ViewGroup layout_crm, layout_reviewer, layout_mproject, layout_type;
    @ViewById TextView tv_crm;
    @ViewById TextView tv_new_customers_num;
    @ViewById TextView tv_new_visit_num;
    @ViewById TextView tv_visit_customers_num;
    @ViewById TextView tv_project;

    @ViewById TextView tv_time, tv_toUser;
    @ViewById TextView tv_reviewer, tv_resignin;

    @ViewById ViewGroup layout_del;
    @ViewById ImageView img_title_toUser;

    @ViewById GridView gridView_photo;

    @Extra Project project;
    @Extra WorkReport mWorkReport;
    @Extra int type;

    private long beginAt, endAt;
    private int mSelectType = 1;
    private WeeksDialog weeksDialog = null;
    private SignInGridViewAdapter signInGridViewAdapter;
    private ArrayList<Attachment> lstData_Attachment = null;
    private String uuid = StringUtil.getUUID();
    private Reviewer mReviewer;
    private ArrayList<Member> members = new ArrayList<>();

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
        if (type == TYPE_EDIT) {
            layout_type.setFocusable(false);
            tv_resignin.setVisibility(View.GONE);
        }

        if (weeksDialog == null) {
            weeksDialog = new WeeksDialog(tv_time);
        }
        crm_toggle.setChecked(SharedUtil.getBoolean(this, "showCrmData"));
        layout_crm.setVisibility(crm_toggle.isChecked() ? View.VISIBLE : View.GONE);
        crm_toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                crmSwitch(b);
            }
        });
        if (null != mWorkReport) {
            if (type == TYPE_EDIT) {
                super.setTitle("编辑工作报告");
            }
            if (type == TYPE_EDIT) {
                uuid = mWorkReport.getAttachmentUUId();
            }
            mReviewer = mWorkReport.getReviewer();
            members = mWorkReport.getMembers();
            switch (mWorkReport.getType()) {
                case WorkReport.DAY:
                    rg.check(R.id.rb1);
                    break;
                case WorkReport.WEEK:
                    rg.check(R.id.rb2);
                    break;
                case WorkReport.MONTH:
                    rg.check(R.id.rb3);
                    break;

            }
            if (null != mWorkReport.getCrmDatas() && mWorkReport.getCrmDatas().size() > 2) {
                tv_new_customers_num.setText(mWorkReport.getCrmDatas().get(0).getContent());
                tv_new_visit_num.setText(mWorkReport.getCrmDatas().get(1).getContent());
                tv_visit_customers_num.setText(mWorkReport.getCrmDatas().get(2).getContent());
            }
            NewUser reviewer = null != mWorkReport.getReviewer() && null != mWorkReport.getReviewer().getUser() ? mWorkReport.getReviewer().getUser() : null;
            tv_reviewer.setText(null == reviewer ? "" : reviewer.getName());
            tv_toUser.setText(mWorkReport.getJoinUserNames());
            edt_content.setText(mWorkReport.getContent());
            if(null!=mWorkReport.getProject()){
                tv_project.setText(mWorkReport.getProject().getTitle());
            }
            //附件暂时不能做
        } else {
            rg.check(R.id.rb1);
            if(null!=project){
                tv_project.setText(project.getTitle());
            }
        }
        init_gridView_photo();
        if (type == TYPE_EDIT) {
            getAttachments();
        }
    }

    /**
     * 获取附件
     */
    private void getAttachments() {
        Utils.getAttachments(uuid, new RCallback<ArrayList<Attachment>>() {
            @Override
            public void success(ArrayList<Attachment> attachments, Response response) {
                lstData_Attachment = attachments;
                init_gridView_photo();
            }
        });
    }

    /**
     * 切换统计开关
     *
     * @param b
     */
    private void crmSwitch(boolean b) {
        layout_crm.setVisibility(b ? View.VISIBLE : View.GONE);
        SharedUtil.putBoolean(this, "showCrmData", b);
        if (crm_toggle.isChecked() != b) {
            crm_toggle.setChecked(b);
        }
    }

    @CheckedChange(R.id.rb1)
    void dayClick(CompoundButton button, boolean b) {
        if (!b) {
            return;
        }
        tv_crm.setText("本日工作动态统计");
        beginAt = DateTool.getBeginAt_ofDay();
        endAt = DateTool.getEndAt_ofDay();
        tv_time.setText(app.df4.format(beginAt));
        mSelectType = WorkReport.DAY;
    }

    @CheckedChange(R.id.rb2)
    void weekClick(CompoundButton button, boolean b) {
        if (!b) {
            return;
        }
        tv_crm.setText("本周工作动态统计");
        beginAt = DateTool.getBeginAt_ofWeek();
        endAt = DateTool.getEndAt_ofWeek();
        tv_time.setText(weeksDialog.GetDefautlText());
        mSelectType = WorkReport.WEEK;
    }

    @CheckedChange(R.id.rb3)
    void monthClick(CompoundButton button, boolean b) {
        if (!b) {
            return;
        }
        tv_crm.setText("本月工作动态统计");
        beginAt = DateTool.getBeginAt_ofMonth();
        endAt = DateTool.getEndAt_ofMonth();
        DateTool.calendar = Calendar.getInstance();

        int year = DateTool.calendar.get(Calendar.YEAR);
        int month = DateTool.calendar.get(Calendar.MONTH);
        tv_time.setText(year + "." + String.format("%02d", (month + 1)));

        mSelectType = WorkReport.MONTH;
    }

    void init_gridView_photo() {
        if (lstData_Attachment == null) {
            lstData_Attachment = new ArrayList<>();
        }
        signInGridViewAdapter = new SignInGridViewAdapter(this, lstData_Attachment, true, true);
        SignInGridViewAdapter.setAdapter(gridView_photo, signInGridViewAdapter);
    }

    @Click({R.id.tv_resignin, R.id.img_title_left, R.id.img_title_right, R.id.layout_reviewer, R.id.layout_toUser, R.id.layout_del, R.id.layout_mproject})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, RESULT_CANCELED, null);
                break;
            case R.id.img_title_right:

                String content = edt_content.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    Toast(getString(R.string.app_content) + getString(R.string.app_no_null));
                    break;
                }
                if (mReviewer == null) {
                    Toast(getString(R.string.review_user) + getString(R.string.app_no_null));
                    break;
                }

                if (mReviewer.getUser().isCurrentUser()) {
                    Toast("点评人不能是自己");
                    break;
                }

                HashMap<String, Object> map = new HashMap<>();
                map.put("content", content);
                map.put("type", mSelectType);
                map.put("beginAt", beginAt / 1000);
                map.put("endAt", endAt / 1000);
                if (null != project) {
                    map.put("projectId", project.getId());
                }
                map.put("attachmentUUId", uuid);
                ArrayList<Reviewer> reviewers = new ArrayList<>();
                reviewers.add(mReviewer);
                map.put("reviewers", reviewers);
                map.put("members", members);
                if (type != TYPE_EDIT) {
                    map.put("isDelayed", tv_time.getText().toString().contains("补签") ? true : false);
                }
                if (type == TYPE_EDIT) {
                    RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(IWorkReport.class).updateWorkReport(mWorkReport.getId(), map, new RCallback<WorkReport>() {
                        @Override
                        public void success(WorkReport workReport, Response response) {
                            Toast(getString(R.string.app_update) + getString(R.string.app_succeed));
                            dealResult(workReport);
                        }
                    });
                } else {

                    RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(IWorkReport.class).createWorkReport(map, new RCallback<WorkReport>() {
                        @Override
                        public void success(WorkReport workReport, Response response) {
                            Toast(getString(R.string.app_add) + getString(R.string.app_succeed));
                            dealResult(workReport);
                        }
                    });
                }
                break;
            case R.id.tv_resignin:
                //选择日期
                selectDate();
                break;
            case R.id.layout_reviewer:
                Bundle bundle = new Bundle();
                bundle.putInt(DepartmentUserActivity.STR_SELECT_TYPE, DepartmentUserActivity.TYPE_SELECT_SINGLE);
                app.startActivityForResult(this, DepartmentUserActivity.class, MainApp.ENTER_TYPE_RIGHT, DepartmentUserActivity.request_Code, bundle);
                break;
            case R.id.layout_toUser:
                app.startActivityForResult(this, DepartmentUserActivity.class, MainApp.ENTER_TYPE_RIGHT, DepartmentUserActivity.request_Code, null);
                break;
            case R.id.layout_del:
                members.clear();
                tv_toUser.setText("");
                layout_del.setVisibility(View.GONE);
                img_title_toUser.setVisibility(View.VISIBLE);
                break;
            case R.id.layout_mproject:
                Bundle bundle1 = new Bundle();
                bundle1.putBoolean("isSelect", true);
                app.startActivityForResult(this, ProjectSearchActivity.class, MainApp.ENTER_TYPE_RIGHT, FinalVariables.REQUEST_SELECT_PROJECT, bundle1);
                break;
        }
    }

    /**
     * 处理服务器返回结果
     *
     * @param workReport
     */
    private void dealResult(WorkReport workReport) {
        if (workReport != null) {
            workReport.setAck(true);

            Intent intent = getIntent();
            intent.putExtra("data", workReport);
            app.finishActivity(WorkReportAddActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case FinalVariables.REQUEST_SELECT_PROJECT:
                Project _project = (Project) data.getSerializableExtra("data");
                project =_project;
                if (null != project) {
                    tv_project.setText(project.getTitle());
                }else {
                    tv_project.setText("无");
                }
                break;

            case DepartmentUserActivity.request_Code:
                /*负责人*/
                User user = (User) data.getSerializableExtra(User.class.getName());
                if (user != null) {
                    if (null == mReviewer) {
                        mReviewer = new Reviewer();
                    }
                    mReviewer.setUser(user.toShortUser());
                    tv_reviewer.setText(user.getRealname());
                } else {  //参与人
                    String userIds = data.getStringExtra(DepartmentUserActivity.CC_USER_ID);
                    String userNames = data.getStringExtra(DepartmentUserActivity.CC_USER_NAME);
                    members = Utils.convert2Members(userIds, userNames);
                    tv_toUser.setText(userNames);
                }
                break;
            case SelectPicPopupWindow.GET_IMG:
                try {
                    ArrayList<SelectPicPopupWindow.ImageInfo> pickPhots = (ArrayList<SelectPicPopupWindow.ImageInfo>) data.getSerializableExtra("data");
                    for (SelectPicPopupWindow.ImageInfo item : pickPhots) {
                        Uri uri = Uri.parse(item.path);
                        final File newFile = Global.scal(this, uri);

                        if (newFile != null && newFile.length() > 0) {
                            if (newFile.exists()) {
                                Utils.uploadAttachment(uuid, newFile).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new CommonSubscriber(this) {
                                    @Override
                                    public void onNext(Serializable serializable) {
                                        app.logUtil.e("onNext");
                                        getAttachments();
                                    }
                                });
                            }
                        }
                    }
                } catch (Exception ex) {
                    Global.ProcException(ex);
                }

                break;
            case FinalVariables.REQUEST_DEAL_ATTACHMENT:
                try {
                    final Attachment delAttachment = (Attachment) data.getSerializableExtra("delAtm");
                    RestAdapterFactory.getInstance().build(Config_project.API_URL_ATTACHMENT()).create(IAttachment.class).remove(String.valueOf(delAttachment.getId()), new RCallback<Attachment>() {
                        @Override
                        public void success(Attachment attachment, Response response) {
                            Toast("删除附件成功!");
                            lstData_Attachment.remove(delAttachment);
                            signInGridViewAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Toast("删除附件失败!");
                            super.failure(error);
                        }
                    });
                } catch (Exception e) {
                    Global.ProcException(e);
                }
                break;
        }
    }

    void selectDate() {
        DateTool.calendar = Calendar.getInstance();

        switch (mSelectType) {
            case WorkReport.DAY:
                DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String str = year + "." + String.format("%02d", (monthOfYear + 1)) + "." + String.format("%02d", dayOfMonth);

                        beginAt = DateTool.getDateToTimestamp(str, app.df4);
                        endAt = DateTool.getDateToTimestamp(str, app.df4) + DateTool.DAY_MILLIS - 100;
                        if (beginAt < DateTool.getBeginAt_ofDay()) {
                            str += "(补签)";
                        }
                        tv_time.setText(str);
                    }
                }, DateTool.calendar.get(Calendar.YEAR), DateTool.calendar.get(Calendar.MONTH), DateTool.calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
                break;
            case WorkReport.MONTH:
                DatePickerDialog datePickerDialogMonth = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String str = year + "." + String.format("%02d", (monthOfYear + 1));

                        beginAt = DateTool.getDateToTimestamp(str.concat(".01 00:00"), app.df3);
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, monthOfYear + 2, 1);
                        calendar.set(Calendar.DAY_OF_MONTH, -1);
                        endAt = DateTool.getDateToTimestamp(str.concat(".").concat(String.valueOf(calendar.get(Calendar.DATE))).concat(" 23:59"), app.df3);

                        if (beginAt < DateTool.getBeginAt_ofMonthMills()) {
                            str += "(补签)";
                        }

                        tv_time.setText(str);
                    }
                },

                        DateTool.calendar.get(Calendar.YEAR), DateTool.calendar.get(Calendar.MONTH), DateTool.calendar.get(Calendar.DAY_OF_MONTH));

                ((LinearLayout) ((ViewGroup) datePickerDialogMonth.getDatePicker().getChildAt(0)).getChildAt(0)).getChildAt(2).setVisibility(View.GONE);

                datePickerDialogMonth.setTitle("选择月份");
                datePickerDialogMonth.show();
                break;
            case WorkReport.WEEK:
                weeksDialog.showChoiceDialog("选择周报").show();
                break;
        }
    }
}
