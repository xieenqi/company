package com.loyo.oa.v2.activityui.wfinstance;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.common.click.NoDoubleClickListener;
import com.loyo.oa.hud.progress.LoyoProgressHUD;
import com.loyo.oa.hud.toast.LoyoToast;
import com.loyo.oa.photo.PhotoPicker;
import com.loyo.oa.photo.PhotoPreview;
import com.loyo.oa.upload.UploadController;
import com.loyo.oa.upload.UploadControllerCallback;
import com.loyo.oa.upload.UploadTask;
import com.loyo.oa.upload.view.ImageUploadGridView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.customer.SelfVisibleCustomerPickerActivity;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.activityui.customer.model.Department;
import com.loyo.oa.v2.activityui.project.ProjectSearchActivity;
import com.loyo.oa.v2.activityui.wfinstance.bean.BizForm;
import com.loyo.oa.v2.activityui.wfinstance.common.ApprovalAddBuilder;
import com.loyo.oa.v2.activityui.wfinstance.presenter.WfinAddPresenter;
import com.loyo.oa.v2.activityui.wfinstance.presenter.impl.WfinAddPresenterImpl;
import com.loyo.oa.v2.activityui.wfinstance.viewcontrol.WfinAddView;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.attachment.api.AttachmentService;
import com.loyo.oa.v2.beans.AttachmentBatch;
import com.loyo.oa.v2.beans.Project;
import com.loyo.oa.v2.beans.UserInfo;
import com.loyo.oa.v2.beans.WfInstance;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.event.AppBus;
import com.loyo.oa.v2.customview.CountTextWatcher;
import com.loyo.oa.v2.db.DBManager;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.ImageInfo;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 【新建审批】界面
 * Restruture by yyy on 16/10/18
 */
public class WfInAddActivity extends BaseActivity implements WfinAddView, UploadControllerCallback {

    /**
     * 部门选择 请求码
     */
    public static final int RESULT_DEPT_CHOOSE = 5;

    private String projectId;
    private String customerId;
    private String customerName;
    private String deptId;
    private String mTemplateId;
    private String uuid = StringUtil.getUUID();
    private String processTitle;
    private String cusTitle;
    private String projectTitle, Process;
    private int bizType = 12;
    private boolean isSave = true;

    private ViewGroup img_title_left;
    private ViewGroup img_title_right;
    private ViewGroup layout_wfinstance_data;
    private ViewGroup ll_dept;
    private ViewGroup ll_project;
    private ViewGroup ll_customer;
    private LinearLayout wfinstance_data_container;
    private TextView wordcount, tv_process;
    private TextView tv_dept;
    private TextView tv_project;
    private TextView tv_customer;
    private Button btn_add;
    private EditText edt_memo;
    private EditText tv_title;
    private WfinAddPresenter mPresenter;

    private LinearLayout ll_attach_file;
    private BizForm mBizForm;
    private ArrayList<HashMap<String, Object>> submitData = new ArrayList<HashMap<String, Object>>();

    UploadController controller;
    ImageUploadGridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wfin_add);
        initView();
    }

    void initView() {
        super.setTitle("新建审批");
        Bundle bundle = getIntent().getExtras();
        mBizForm = (BizForm) bundle.getSerializable("bizForm");
        processTitle = bundle.getString("title");
        mTemplateId = bundle.getString("mTemplateId");
        projectId = bundle.getString("projectId");
        projectTitle = bundle.getString("projectTitle");
        Process = bundle.getString("Process");//。流程说明
        customerId = ApprovalAddBuilder.getCustomerId();
        customerName = ApprovalAddBuilder.getCustomerName();
        ApprovalAddBuilder.endCreate();


        cusTitle = MainApp.user.getRealname() + "" + mBizForm.getName() + "" + processTitle;
        wfinstance_data_container = (LinearLayout) findViewById(R.id.wfinstance_data_container);
        img_title_left = (ViewGroup) findViewById(R.id.img_title_left);
        img_title_right = (ViewGroup) findViewById(R.id.img_title_right);
        layout_wfinstance_data = (ViewGroup) findViewById(R.id.layout_wfinstance_data);
        ll_dept = (ViewGroup) findViewById(R.id.ll_dept);
        ll_project = (ViewGroup) findViewById(R.id.ll_project);
        ll_customer = (ViewGroup) findViewById(R.id.ll_customer);
        wordcount = (TextView) findViewById(R.id.wordcount);
        tv_dept = (TextView) findViewById(R.id.tv_dept);
        tv_project = (TextView) findViewById(R.id.tv_project);
        tv_customer = (TextView) findViewById(R.id.tv_customer);
        tv_process = (TextView) findViewById(R.id.tv_process);
        btn_add = (Button) findViewById(R.id.btn_add);
        gridView = (ImageUploadGridView) findViewById(R.id.image_upload_grid_view);
        edt_memo = (EditText) findViewById(R.id.edt_memo);
        tv_title = (EditText) findViewById(R.id.tv_title);

        //隐藏附件上传
        ll_attach_file = (LinearLayout) findViewById(R.id.ll_attach_file);
        ll_attach_file.setVisibility(View.GONE);


        img_title_left.setOnTouchListener(Global.GetTouch());
        img_title_right.setOnTouchListener(Global.GetTouch());
        btn_add.setOnTouchListener(Global.GetTouch());
        ll_project.setOnTouchListener(Global.GetTouch());
        ll_customer.setOnTouchListener(Global.GetTouch());
        ll_dept.setOnTouchListener(Global.GetTouch());

        ll_project.setOnClickListener(onClick);
        ll_customer.setOnClickListener(onClick);
        btn_add.setOnClickListener(onClick);
        img_title_left.setOnClickListener(onClick);
        img_title_right.setOnClickListener(onClick);
        ll_dept.setOnClickListener(onClick);
        edt_memo.addTextChangedListener(new CountTextWatcher(wordcount));

        tv_title.setText(cusTitle);

        mPresenter = new WfinAddPresenterImpl(WfInAddActivity.this, mContext, this, mBizForm);
        mPresenter.setStartendTime();
        projectAddWfinstance();
        setDefaultDept();

        if (!TextUtils.isEmpty(customerId)) {
            tv_customer.setText(TextUtils.isEmpty(customerName) ? "无" : customerName);
            ll_customer.setEnabled(false);
        }

        controller = new UploadController(this, 9);
        controller.setObserver(this);
        controller.loadView(gridView);
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
        if (!TextUtils.isEmpty(Process)) {
            tv_process.setVisibility(View.VISIBLE);
            tv_process.setText("流程说明: " + Process);
        }
        if (!TextUtils.isEmpty(projectId)) {
            ll_project.setEnabled(false);
            tv_project.setText(projectTitle);
        }
    }

    /**
     * 初始化adapter
     */
//    void init_gridView_photo() {
//        imageGridViewAdapter = new ImageGridViewAdapter(this, true, true, 0, pickPhots);
//        ImageGridViewAdapter.setAdapter(gridView_photo, imageGridViewAdapter);
//    }

    /**
     * 监听器
     */
    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            v.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    switch (v.getId()) {
                        //返回
                        case R.id.img_title_left:
                            app.finishActivity(WfInAddActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_CANCELED, null);
                            break;

                        //所属部门选择
                        case R.id.ll_dept:
                            app.startActivityForResult(WfInAddActivity.this, DepartmentChooseActivity.class, MainApp.ENTER_TYPE_RIGHT, RESULT_DEPT_CHOOSE, null);
                            break;

                        //所属项目选择
                        case R.id.ll_project:
                            Bundle bundle2 = new Bundle();
                            bundle2.putInt("from", BaseActivity.WFIN_ADD);
                            bundle2.putInt(ExtraAndResult.EXTRA_STATUS, 1);
                            app.startActivityForResult(WfInAddActivity.this, ProjectSearchActivity.class,
                                    MainApp.ENTER_TYPE_RIGHT,
                                    ExtraAndResult.REQUSET_PROJECT, bundle2);
                            break;
                        //所属客户选择
                        case R.id.ll_customer:
                            app.startActivityForResult(WfInAddActivity.this, SelfVisibleCustomerPickerActivity.class,
                                    MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_CUSTOMER, null);
                            break;

                        //提交审批
                        case R.id.img_title_right:
                            //没有附件
                            showCommitLoading();
                            if (controller.count() == 0) {
                                mPresenter.addWfinVeri(deptId);
                            } else {
                                controller.startUpload();
                                controller.notifyCompletionIfNeeded();
                            }
                            break;

                        //新增内容
                        case R.id.btn_add:
                            mPresenter.addTypeData(wfinstance_data_container);
                            break;
                    }
                }
            });
        }
    };

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        switch (requestCode) {

            //选择客户
            case ExtraAndResult.REQUEST_CODE_CUSTOMER:
                Customer customer = (Customer) data.getSerializableExtra("data");
                if (null != customer) {
                    customerId = customer.getId();
                    customerName = customer.name;
                    tv_customer.setText(TextUtils.isEmpty(customerName) ? "无" : customerName);
                } else {
                    customerId = "";
                    tv_customer.setText("无");
                }
                break;

            /*相册选择 回调*/
            case PhotoPicker.REQUEST_CODE:
                /*相册选择 回调*/
                if (data != null) {
                    List<String> mSelectPath = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                    for (String path : mSelectPath) {
                        controller.addUploadTask("file://" + path, null, uuid);
                    }
                    controller.reloadGridView();
                }
                break;

            /*附件删除回调*/
            case PhotoPreview.REQUEST_CODE:
                if (data != null) {
                    int index = data.getExtras().getInt(PhotoPreview.KEY_DELETE_INDEX);
                    if (index >= 0) {
                        controller.removeTaskAt(index);
                        controller.reloadGridView();
                    }
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

        if (!TextUtils.isEmpty(mTemplateId)) {
            wfInstance.wftemplateId = mTemplateId;
        }

        wfInstance.memo = edt_memo.getText().toString().trim();

        if (isSave) {
            DBManager.Instance().putWfInstance(MainApp.gson.toJson(wfInstance));
        }
    }

    @Override
    public LoyoProgressHUD getHUD() {
        return hud;
    }

    @Override
    public LoyoProgressHUD showStatusProgress() {
        LogUtil.dee("LoyoProgressHUD:");
        showCommitLoading();
        return hud;
    }

    @Override
    public LoyoProgressHUD showProgress(String msg) {
        LogUtil.dee("showProgress:");
        showLoading2(msg);
        return hud;
    }

    @Override
    public void hideProgress() {
        cancelLoading2();
    }

    @Override
    public void showMsg(String message) {
        cancelCommitLoading();
        Toast(message);
    }

    /**
     * 设置时间格式处理
     */
    @Override
    public void setStartendTimeEmbl() {
        layout_wfinstance_data.setVisibility(View.VISIBLE);
        submitData.clear();
        wfinstance_data_container.removeAllViews();
        mPresenter.addTypeData(wfinstance_data_container);
    }

    /**
     * 新建审批验证通过操作
     */
    @Override
    public void requestAddWfinVeriSuccess(ArrayList<HashMap<String, Object>> workflowValues) {
        LogUtil.dee("requestAddWfinVeriSuccess");
        mPresenter.requestAddWfin(tv_title.getText().toString(), deptId, workflowValues,
                mTemplateId, projectId, uuid, edt_memo.getText().toString().trim(),
                controller.count(), customerId, customerName);
    }

    /**
     * 新建审批成功处理
     */
    @Override
    public void requestAddWfinSuccessEmbl(WfInstance wfInstance) {
        isSave = false;
        wfInstance.setViewed(true);
        Intent intent = getIntent();
        intent.putExtra("data", wfInstance);
        MainApp.getMainApp().finishActivity(WfInAddActivity.this, MainApp.ENTER_TYPE_LEFT, WfInstanceManageActivity.WFIN_FINISH_RUSH, intent);
        AppBus.getInstance().post(new BizForm());
    }

    /**
     * 上传附件信息
     */
    public void postAttaData() {
        ArrayList<UploadTask> list = controller.getTaskList();
        ArrayList<AttachmentBatch> attachment = new ArrayList<AttachmentBatch>();
        for (int i = 0; i < list.size(); i++) {
            UploadTask task = list.get(i);
            AttachmentBatch attachmentBatch = new AttachmentBatch();
            attachmentBatch.UUId = uuid;
            attachmentBatch.bizType = bizType;
            attachmentBatch.mime = Utils.getMimeType(task.getValidatePath());
            attachmentBatch.name = task.getKey();
            attachmentBatch.size = Integer.parseInt(task.size + "");
            attachment.add(attachmentBatch);
        }
        AttachmentService.setAttachementData2(attachment)
                .subscribe(new DefaultLoyoSubscriber<ArrayList<Attachment>>(hud, true) {
                    @Override
                    public void onNext(ArrayList<Attachment> news) {
                        mPresenter.addWfinVeri(deptId);
                    }
                });
    }

    /**
     * 附件上传成功处理
     */
    @Override
    public void uploadSuccessEmbl(ArrayList<ImageInfo> pickPhots) {
        //mPresenter.addWfinVeri(deptId, pickPhots);
    }

    @Override
    public void onRetryEvent(UploadController controller, UploadTask task) {
        controller.retry();
    }

    @Override
    public void onAddEvent(UploadController controller) {
        PhotoPicker.builder()
                .setPhotoCount(9 - controller.count())
                .setShowCamera(true)
                .setPreviewEnabled(false)
                .start(this);
    }

    @Override
    public void onItemSelected(UploadController controller, int index) {
        ArrayList<UploadTask> taskList = controller.getTaskList();
        ArrayList<String> selectedPhotos = new ArrayList<>();

        for (int i = 0; i < taskList.size(); i++) {
            String path = taskList.get(i).getValidatePath();
            if (path.startsWith("file://")) ;
            {
                path = path.replace("file://", "");
            }
            selectedPhotos.add(path);
        }
        PhotoPreview.builder()
                .setPhotos(selectedPhotos)
                .setCurrentItem(index)
                .setShowDeleteButton(true)
                .start(this);
    }

    @Override
    public void onAllUploadTasksComplete(UploadController controller, ArrayList<UploadTask> taskList) {

        int count = controller.failedTaskCount();
        if (count > 0) {
            cancelCommitLoading();
            LoyoToast.info(this, count + "个附件上传失败，请重试或者删除");
            return;
        }
        if (taskList.size() > 0) {
            postAttaData();
        } else {
            mPresenter.addWfinVeri(deptId);
        }
    }
}
