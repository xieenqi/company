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
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.project.ProjectSearchActivity;
import com.loyo.oa.v2.activityui.other.adapter.ImageGridViewAdapter;
import com.loyo.oa.v2.activityui.wfinstance.presenter.WfinAddPresenter;
import com.loyo.oa.v2.activityui.wfinstance.presenter.impl.WfinAddPresenterImpl;
import com.loyo.oa.v2.activityui.wfinstance.viewcontrol.WfinAddView;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.activityui.wfinstance.bean.BizForm;
import com.loyo.oa.v2.activityui.customer.bean.Department;
import com.loyo.oa.v2.beans.Project;
import com.loyo.oa.v2.beans.UserInfo;
import com.loyo.oa.v2.beans.WfInstance;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.event.AppBus;
import com.loyo.oa.v2.customview.multi_image_selector.MultiImageSelectorActivity;
import com.loyo.oa.v2.db.DBManager;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.SelectPicPopupWindow;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.customview.CountTextWatcher;
import com.loyo.oa.v2.customview.CusGridView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 【新建审批】界面
 * Restruture by yyy on 16/10/18
 */
public class WfInAddActivity extends BaseActivity implements WfinAddView{

    /**
     * 部门选择 请求码
     */
    public static final int RESULT_DEPT_CHOOSE = 5;

    private String projectId;
    private String deptId;
    private String mTemplateId;
    private String uuid = StringUtil.getUUID();
    private String processTitle;
    private String cusTitle;
    private String projectTitle;
    private int bizType = 12;
    private boolean isSave = true;

    private ViewGroup img_title_left;
    private ViewGroup img_title_right;
    private ViewGroup layout_wfinstance_data;
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
    private ImageGridViewAdapter imageGridViewAdapter;
    private WfinAddPresenter mPresenter;

    private BizForm mBizForm;
    private List<String> mSelectPath;
    private ArrayList<SelectPicPopupWindow.ImageInfo> pickPhotsResult;
    private ArrayList<HashMap<String, Object>> submitData = new ArrayList<HashMap<String, Object>>();
    private ArrayList<SelectPicPopupWindow.ImageInfo> pickPhots = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wfin_add);
        initView();
    }

    void initView() {
        super.setTitle("新建审批");

        mBizForm = (BizForm) getIntent().getExtras().getSerializable("bizForm");
        processTitle = getIntent().getExtras().getString("title");
        mTemplateId = getIntent().getExtras().getString("mTemplateId");
        projectId = getIntent().getExtras().getString("projectId");
        projectTitle = getIntent().getExtras().getString("projectTitle");

        cusTitle = MainApp.user.getRealname() + "" + mBizForm.getName() + "" + processTitle;
        wfinstance_data_container = (LinearLayout) findViewById(R.id.wfinstance_data_container);
        img_title_left = (ViewGroup) findViewById(R.id.img_title_left);
        img_title_right = (ViewGroup) findViewById(R.id.img_title_right);
        layout_wfinstance_data = (ViewGroup) findViewById(R.id.layout_wfinstance_data);
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

        tv_title.setText(cusTitle);

        mPresenter = new WfinAddPresenterImpl(WfInAddActivity.this,mContext,this,mBizForm);
        mPresenter.setStartendTime();
        init_gridView_photo();
        projectAddWfinstance();
        setDefaultDept();
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
     * 初始化adapter
     * */
    void init_gridView_photo() {
        imageGridViewAdapter = new ImageGridViewAdapter(this, true, true, 0, pickPhots);
        ImageGridViewAdapter.setAdapter(gridView_photo, imageGridViewAdapter);
    }

    /**
     * 监听器
     * */
    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
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

                //提交审批
                case R.id.img_title_right:
                    //没有附件
                    if (pickPhots.size() == 0) {
                        mPresenter.addWfinVeri(deptId,pickPhots);
                    } else {
                        mPresenter.newUploadAttachement(uuid,bizType,pickPhots);
                    }
                    break;

                //新增内容
                case R.id.btn_add:
                    mPresenter.addTypeData(wfinstance_data_container);
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        switch (requestCode) {

            /*相册选择 回调*/
            case MainApp.PICTURE:
                if (null != data) {
                    pickPhotsResult = new ArrayList<>();
                    mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                    for (String path : mSelectPath) {
                        pickPhotsResult.add(new SelectPicPopupWindow.ImageInfo("file://" + path));
                    }
                    pickPhots.addAll(pickPhotsResult);
                    init_gridView_photo();
                }
                break;

           /*附件删除回调*/
            case FinalVariables.REQUEST_DEAL_ATTACHMENT:
                pickPhots.remove(data.getExtras().getInt("position"));
                init_gridView_photo();
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
    public void showProgress(String msg) {
        showLoading(msg);
    }

    @Override
    public void hideProgress() {
        cancelLoading();
    }

    @Override
    public void showMsg(String message) {
        Toast(message);
    }

    /**
     * 设置时间格式处理
     * */
    @Override
    public void setStartendTimeEmbl() {
        layout_wfinstance_data.setVisibility(View.VISIBLE);
        submitData.clear();
        wfinstance_data_container.removeAllViews();
        mPresenter.addTypeData(wfinstance_data_container);
    }

    /**
     * 新建审批验证通过操作
     * */
    @Override
    public void requestAddWfinVeriSuccess(ArrayList<HashMap<String, Object>> workflowValues) {
        mPresenter.requestAddWfin(tv_title.getText().toString(),deptId,workflowValues,
                                  mTemplateId,projectId,uuid, edt_memo.getText().toString().trim(),
                                  pickPhots);

    }

    /**
     * 新建审批成功处理
     * */
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
     * 附件上传成功处理
     * */
    @Override
    public void uploadSuccessEmbl(ArrayList<SelectPicPopupWindow.ImageInfo> pickPhots) {
        mPresenter.addWfinVeri(deptId,pickPhots);
    }
}
