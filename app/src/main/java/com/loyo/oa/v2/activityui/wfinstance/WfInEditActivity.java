package com.loyo.oa.v2.activityui.wfinstance;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.attachment.AttachmentActivity_;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.customer.model.Department;
import com.loyo.oa.v2.activityui.project.ProjectSearchActivity;
import com.loyo.oa.v2.activityui.signin.adapter.SignInGridViewAdapter;
import com.loyo.oa.v2.activityui.wfinstance.bean.BizForm;
import com.loyo.oa.v2.activityui.wfinstance.event.WfinRushEvent;
import com.loyo.oa.v2.activityui.wfinstance.presenter.WfinEditPresenter;
import com.loyo.oa.v2.activityui.wfinstance.presenter.impl.WfinEditPresenterImpl;
import com.loyo.oa.v2.activityui.wfinstance.viewcontrol.WfinEditView;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.attachment.api.AttachmentService;
import com.loyo.oa.v2.beans.Project;
import com.loyo.oa.v2.beans.UserInfo;
import com.loyo.oa.v2.beans.WfInstance;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.event.AppBus;
import com.loyo.oa.v2.customview.CountTextWatcher;
import com.loyo.oa.v2.customview.CusGridView;
import com.loyo.oa.v2.db.DBManager;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.ImageInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 【编辑审批】界面
 * Restruture by yyy on 16/10/21
 */
public class WfInEditActivity extends BaseActivity implements WfinEditView {

    /**
     * 部门选择 请求码
     */
    public static final int RESULT_DEPT_CHOOSE = 5;
    public static final int MSG_ATTACHMENT = 200;//上传完了附件的回调code

    private String projectId;
    private String deptId;
    private String uuid;
    private String memo;
    private String cusTitle;
    private String projectTitle;
    private int bizType = 12;
    private int uploadSize;
    private int uploadNum;

    private ViewGroup img_title_left;
    private ViewGroup img_title_right;
    private LinearLayout layout_wfinstance_data;
    private ViewGroup ll_dept;
    private ViewGroup ll_project;
    private LinearLayout wfinstance_data_container;
    private TextView wordcount;
    private TextView tv_dept;
    private TextView tv_project;
    private Button btn_add;
    private CusGridView gridView_photo;
    private EditText edt_memo;
    private EditText tv_title;

    private WfInstance mWfInstance;
    private BizForm mBizForm;
    private ArrayList<HashMap<String, Object>> wfInstanceValuesDatas = new ArrayList<>();
    private ArrayList<Attachment> lstData_Attachment = new ArrayList<>();
    private SignInGridViewAdapter signInGridViewAdapter;
    private WfinEditPresenter mPresenter;

    private LinearLayout ll_attch_file;//附件
    private TextView tv_attch_file;//显示附件数量

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wfin_add);
        initView();
    }

    void initView() {
        super.setTitle("编辑审批");
        mWfInstance = (WfInstance) getIntent().getExtras().getSerializable("data");
        uuid = mWfInstance.attachmentUUId;
        initData_WorkflowValues();
        //附件上传
        tv_attch_file = (TextView) findViewById(R.id.tv_attach_file);
        ll_attch_file = (LinearLayout) findViewById(R.id.ll_attach_file);
        ll_attch_file.setOnClickListener(onClick);

        wfinstance_data_container = (LinearLayout) findViewById(R.id.wfinstance_data_container);
        img_title_left = (ViewGroup) findViewById(R.id.img_title_left);
        img_title_right = (ViewGroup) findViewById(R.id.img_title_right);
        layout_wfinstance_data = (LinearLayout) findViewById(R.id.layout_wfinstance_data);
        ll_dept = (ViewGroup) findViewById(R.id.ll_dept);
        ll_project = (ViewGroup) findViewById(R.id.ll_project);
        wordcount = (TextView) findViewById(R.id.wordcount);
        tv_dept = (TextView) findViewById(R.id.tv_dept);
        tv_project = (TextView) findViewById(R.id.tv_project);
        btn_add = (Button) findViewById(R.id.btn_add);
        gridView_photo = (CusGridView) findViewById(R.id.gridView_photo);
        edt_memo = (EditText) findViewById(R.id.edt_memo);
        tv_title = (EditText) findViewById(R.id.tv_title);

        img_title_left.setOnTouchListener(Global.GetTouch());
        img_title_right.setOnTouchListener(Global.GetTouch());
        btn_add.setOnTouchListener(Global.GetTouch());
        ll_project.setOnTouchListener(Global.GetTouch());
        ll_dept.setOnTouchListener(Global.GetTouch());

        ll_project.setOnClickListener(onClick);
        btn_add.setOnClickListener(onClick);
        img_title_left.setOnClickListener(onClick);
        img_title_right.setOnClickListener(onClick);
        ll_dept.setOnClickListener(onClick);
        edt_memo.addTextChangedListener(new CountTextWatcher(wordcount));
        mPresenter = new WfinEditPresenterImpl(mContext, this, WfInEditActivity.this);

        if (null != mWfInstance.bizForm) {
            mBizForm = mWfInstance.bizForm;
            mPresenter.setStartendTime(wfInstanceValuesDatas, mBizForm, layout_wfinstance_data, wfinstance_data_container);
        }
        if (null != mWfInstance.title) {
            cusTitle = mWfInstance.title;
        }
        if (null != mWfInstance.ProjectInfo.id) {
            projectId = mWfInstance.ProjectInfo.id;
        }
        if (null != mWfInstance.ProjectInfo.title) {
            projectTitle = mWfInstance.ProjectInfo.title;
        }
        if (null != mWfInstance.memo) {
            memo = mWfInstance.memo;
        }

        tv_title.setText(cusTitle);
        edt_memo.setText(memo);
        gridView_photo.setVisibility(View.GONE);

        //设置附件
        tv_attch_file.setText("附件" + "（" + mWfInstance.bizExtData.getAttachmentCount() + "）");

        //init_gridView_photo();
        projectAddWfinstance();
        setDefaultDept();
        //mPresenter.getAttachments(uuid);
    }

    /**
     * 设置默认的部门 信息
     */
    public void setDefaultDept() {
        if (null == MainApp.user.depts || MainApp.user.depts.size() <= 0) {
            tv_dept.setText("没有归属部门");
            return;
        }
        Department myDepartment = MainApp.user.depts.get(0).getShortDept();
        tv_dept.setText(myDepartment.getName());
        deptId = myDepartment.getId();
    }

    /**
     * 项目 过来要 创建 审批
     */
    public void projectAddWfinstance() {
        if (!TextUtils.isEmpty(projectId)) {
            ll_project.setEnabled(false);
            tv_project.setText(projectTitle);
        }
    }

    /**
     * adapter初始化
     */
    void init_gridView_photo() {
        signInGridViewAdapter = new SignInGridViewAdapter(this, lstData_Attachment, true, true, true, 0);
        SignInGridViewAdapter.setAdapter(gridView_photo, signInGridViewAdapter);
        if (uploadNum == uploadSize) {
            cancelLoading();
        }
    }

    @Override
    public void onBackPressed() {
        //为了再返回的时候,更新附件数量.
        Intent intent = new Intent();
        if (null != mWfInstance.attachments) {
            intent.putExtra("attachFileNum", mWfInstance.attachments.size());
        }
        app.finishActivity(WfInEditActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_CANCELED, intent);
        super.onBackPressed();
    }

    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                //返回
                case R.id.img_title_left:
                    //为了再返回的时候,更新附件数量.
                    Intent intent = new Intent();
                    if (null != mWfInstance.attachments) {
                        intent.putExtra("attachFileNum", mWfInstance.attachments.size());
                    }
                    app.finishActivity(WfInEditActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_CANCELED, intent);
                    break;

                //所属部门选择
                case R.id.ll_dept:
                    app.startActivityForResult(WfInEditActivity.this, DepartmentChooseActivity.class, MainApp.ENTER_TYPE_RIGHT, RESULT_DEPT_CHOOSE, null);
                    break;

                //所属项目选择
                case R.id.ll_project:
                    Bundle bundle2 = new Bundle();
                    bundle2.putInt("from", BaseActivity.WFIN_ADD);
                    bundle2.putInt(ExtraAndResult.EXTRA_STATUS, 1);
                    app.startActivityForResult(WfInEditActivity.this, ProjectSearchActivity.class,
                            MainApp.ENTER_TYPE_RIGHT,
                            ExtraAndResult.REQUSET_PROJECT, bundle2);
                    break;
                //提交审批
                case R.id.img_title_right:
                    mPresenter.requestEditWfinVeri(deptId);
                    break;

                //新增内容
                case R.id.btn_add:
                    mPresenter.addTypeData(wfinstance_data_container);
                    break;
                //附件上传
                case R.id.ll_attach_file:

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("data", mWfInstance.attachments);
                    bundle.putSerializable("uuid", mWfInstance.attachmentUUId);//因为只有自己提交的请求审批,才可以修改,所以,不需要判断订单,汇款之类的传过来的id
                    bundle.putBoolean("isOver", false);//表示可以编辑
                    bundle.putInt("bizType", 12);
                    app.startActivityForResult(WfInEditActivity.this, AttachmentActivity_.class, MainApp.ENTER_TYPE_RIGHT, MSG_ATTACHMENT, bundle);

                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 获取审批内容
     */
    void initData_WorkflowValues() {
        if (null == mWfInstance || null == mWfInstance.workflowValues) {
            return;
        }
        wfInstanceValuesDatas.clear();
        for (int i = 0; i < mWfInstance.workflowValues.size(); i++) {
            wfInstanceValuesDatas.add(mWfInstance.workflowValues.get(i));
        }
    }


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        switch (requestCode) {

            //上传附件回调
            case MainApp.GET_IMG:
                try {
                    ArrayList<ImageInfo> pickPhots = (ArrayList<ImageInfo>) data.getSerializableExtra("data");
                    uploadSize = 0;
                    uploadNum = pickPhots.size();
                    for (ImageInfo item : pickPhots) {
                        Uri uri = Uri.parse(item.path);
                        File newFile = Global.scal(this, uri);

                        if (newFile != null && newFile.length() > 0) {
                            if (newFile.exists()) {
                                mPresenter.newUploadAttachement(uuid, newFile);
                            }
                        }
                    }
                } catch (Exception ex) {
                    Global.ProcException(ex);
                }

                break;

            /*附件删除*/
            case FinalVariables.REQUEST_DEAL_ATTACHMENT:

                final Attachment delAttachment = (Attachment) data.getSerializableExtra("delAtm");
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("bizType", bizType);
                map.put("uuid", uuid);
                AttachmentService.remove(String.valueOf(delAttachment.getId()), map)
                        .subscribe(new DefaultLoyoSubscriber<Attachment>() {
                            @Override
                            public void onNext(Attachment attachment) {
                                Toast("删除附件成功!");
                                lstData_Attachment.remove(delAttachment);
                                init_gridView_photo();
                            }
                        });

                break;
            case MSG_ATTACHMENT:
                if (data == null || data.getExtras() == null) {
                    return;
                }
                ArrayList<Attachment> attachments = (ArrayList<Attachment>) data.getSerializableExtra("data");
                mWfInstance.attachments = attachments;
                if (null != attachments) {
                    tv_attch_file.setText("附件" + "（" + attachments.size() + "）");
                }

                break;

            /*选择部门回调*/
            case RESULT_DEPT_CHOOSE:
                UserInfo userInfo = (UserInfo) data.getSerializableExtra(DepartmentChooseActivity.class.getName());
                tv_dept.setText(userInfo.getShortDept().getName());
                deptId = userInfo.getShortDept().getId();
                break;

            /*所属项目回调*/
            case ExtraAndResult.REQUSET_PROJECT:
                Project _project = (Project) data.getSerializableExtra("data");
                if (null != _project) {
                    projectId = _project.id;
                    tv_project.setText(_project.title);
                } else {
                    projectId = "";
                    tv_project.setText("无");
                }
                break;

            default:
                break;
        }
    }

    boolean isSave = true;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DBManager.Instance().deleteWfInstance();
        WfInstance wfInstance = new WfInstance();
        wfInstance.attachments = null;
        wfInstance.creator = null;

        if (mBizForm != null) {
            wfInstance.bizForm = mBizForm;
        }


        wfInstance.memo = edt_memo.getText().toString().trim();

        if (isSave) {
            DBManager.Instance().putWfInstance(MainApp.gson.toJson(wfInstance));
        }
    }

    @Override
    public void showStatusProgress() {
        showStatusLoading(false);
    }

    @Override
    public void showProgress(String msg) {
        showLoading(msg);
    }

    @Override
    public void hideProgress() {
        cancelLoading();
    }

    @Override
    public void showMsg(String msg) {
        Toast(msg);
    }

    /**
     * 获取附件成功处理
     */
    @Override
    public void getAttachmentsEmbl(ArrayList<Attachment> attachments) {
        lstData_Attachment = attachments;
        init_gridView_photo();
    }

    /**
     * 请求编辑验证
     */
    @Override
    public void requestEditVeriEmbl(ArrayList<HashMap<String, Object>> workflowValues) {
        mPresenter.requestEditWfin(mWfInstance.id,
                tv_title.getText().toString(),
                deptId, workflowValues,
                projectId, edt_memo.getText().toString().trim());
    }

    /**
     * 请求编辑成功处理
     */
    @Override
    public void requestEditWfinEmbl(WfInstance wfInstance) {
        Toast("编辑成功");
        isSave = false;
        wfInstance.setViewed(true);
        AppBus.getInstance().post(new WfinRushEvent());
        app.finishActivity(WfInEditActivity.this, MainApp.ENTER_TYPE_LEFT, WfInstanceManageActivity.WFIN_FINISH_RUSH, new Intent());
    }
}
