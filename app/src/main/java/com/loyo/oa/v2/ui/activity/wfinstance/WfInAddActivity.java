package com.loyo.oa.v2.ui.activity.wfinstance;

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
import com.loyo.oa.v2.ui.activity.project.ProjectSearchActivity;
import com.loyo.oa.v2.ui.activity.other.adapter.ImageGridViewAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.ui.activity.attachment.bean.Attachment;
import com.loyo.oa.v2.ui.activity.wfinstance.bean.BizForm;
import com.loyo.oa.v2.ui.activity.wfinstance.bean.BizFormFields;
import com.loyo.oa.v2.ui.activity.customer.bean.Department;
import com.loyo.oa.v2.ui.activity.wfinstance.bean.WfInstanceAdd;
import com.loyo.oa.v2.beans.PostBizExtData;
import com.loyo.oa.v2.beans.Project;
import com.loyo.oa.v2.beans.UserInfo;
import com.loyo.oa.v2.beans.WfInstance;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.db.DBManager;
import com.loyo.oa.v2.point.IAttachment;
import com.loyo.oa.v2.point.IWfInstance;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.SelectPicPopupWindow;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.customview.CountTextWatcher;
import com.loyo.oa.v2.customview.CusGridView;
import com.loyo.oa.v2.customview.WfinAddViewGroup;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

/**
 * 【新建审批】界面
 *  v2.2 新版新建审批
 *  create by yyy on 2016/06/07
 */
public class WfInAddActivity extends BaseActivity {

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
    private int uploadSize;
    private int uploadNum;

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
    private WfInstanceAdd wfInstanceAdd = new WfInstanceAdd();
    private ArrayList<SelectPicPopupWindow.ImageInfo> pickPhots = new ArrayList<>();
    private ArrayList postValue = new ArrayList<>();

    private BizForm mBizForm;
    private ArrayList<String> startTimeArr = new ArrayList<>();
    private ArrayList<String> endTimeArr = new ArrayList<>();
    private ArrayList<HashMap<String, Object>> submitData = new ArrayList<HashMap<String, Object>>();
    private List<WfinAddViewGroup> WfinObj = new ArrayList<WfinAddViewGroup>();
    private ArrayList<Boolean> isRequiredList = new ArrayList<>();
    private ImageGridViewAdapter imageGridViewAdapter;
    private PostBizExtData bizExtData;

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

        cusTitle = MainApp.user.getRealname()+""+mBizForm.getName()+""+processTitle;
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

        setStartendTime();
        init_gridView_photo();
        projectAddWfinstance();
        setDefaultDept();
    }

    /**
     * 审批开始 结束时间规范判断:
     *
     * 审批开始时间不能小于结束时间，
     * 从审批内容里获取到 开始时间 结束时间 的id
     * 再根据这个id去获取 开始结束 时间的值
     */
    public void setStartendTime(){
        for (int i = 0; i < mBizForm.getFields().size(); i++) {
            if (mBizForm.getFields().get(i).getName().equals("开始时间") && mBizForm.getFields().get(i).isSystem()) {
                startTimeArr.add(mBizForm.getFields().get(i).getId());
            }
            if (mBizForm.getFields().get(i).getName().equals("结束时间") && mBizForm.getFields().get(i).isSystem()) {
                endTimeArr.add(mBizForm.getFields().get(i).getId());
            }
        }
        layout_wfinstance_data.setVisibility(View.VISIBLE);
        submitData.clear();
        wfinstance_data_container.removeAllViews();
        addTypeData();
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

    void init_gridView_photo() {
        imageGridViewAdapter = new ImageGridViewAdapter(this,true,true,0,pickPhots);
        ImageGridViewAdapter.setAdapter(gridView_photo, imageGridViewAdapter);
    }


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
                    if(pickPhots.size() == 0){
                        subMinInfo();
                    }else{
                        newUploadAttachement();
                    }
                    break;

                //新增内容
                case R.id.btn_add:
                    addTypeData();
                    break;

                default:
                    break;
            }
        }
    };


    /**
     * xnq
     * 界面上 新增加审批内容 栏目
     */
    void addTypeData() {
        if (mBizForm == null) {
            Toast("请选择类型");
            return;
        }
        if (null == mBizForm.getFields()) {
            return;
        }

        HashMap<String, Object> newValues = new HashMap<String, Object>();
        for (BizFormFields field : mBizForm.getFields()) {
            newValues.put(field.getId(), "");
        }
        submitData.add(newValues);
        WfinAddViewGroup viewGroup = new WfinAddViewGroup(this, mBizForm.getFields(), submitData);
        viewGroup.bindView(submitData.size() > 0 ? submitData.size() - 1 : submitData.size(), wfinstance_data_container);
        WfinObj.add(viewGroup);//新增一个内容 就存起来
        addIsRequired();
    }

    /*内容是否必填，加入list*/
    void addIsRequired() {
        for (int i = 0; i < mBizForm.getFields().size(); i++) {
            isRequiredList.add(mBizForm.getFields().get(i).isRequired());
        }
    }

    /**
     * 新建审批 数据请求
     * */
    public void subMinInfo(){
        if (submitData.isEmpty()) {
            Toast("请输入审批内容");
            return;
        } else if (TextUtils.isEmpty(deptId)) {
            Toast("请输选择部门");
            return;
        }



        /**审批内容，装进Post数据的list中*/
        postValue.clear();
        ArrayList<HashMap<String, Object>> workflowValues = new ArrayList<>();
        wfInstanceAdd.getWorkflowValuesAdd().wfInstanceValuesDatas.clear();
        for (int k = 0; k < submitData.size(); k++) {
            HashMap<String, Object> jsonMapValues = new HashMap<>();
            HashMap<String, Object> map_Values = submitData.get(k);
            try {
                for (BizFormFields field : mBizForm.getFields()) {
                    for (String key : map_Values.keySet()) {
                        if (!TextUtils.equals(field.getId(), key)) {
                            continue;
                        }
                        postValue.add(map_Values.get(key));
                        jsonMapValues.put(key, map_Values.get(key));
                    }
                }
                workflowValues.add(jsonMapValues);
            } catch (NullPointerException e) {
                e.printStackTrace();
                Toast("操作失败！");
                return;
            }
        }

        for (int i = 0; i < postValue.size(); i++) {
            if (TextUtils.isEmpty(postValue.get(i).toString()) && isRequiredList.get(i) == true) {
                Toast("请填写\"必填项\"");
                return;
            }
        }


        /**
         * 获取请假/出差系统字段的 开始结束时间
         * */

        String startTimeDate = "";
        String endTimeDate = "";

        long startTimelong;
        long endTimelong;

        for(int i = 0;i<startTimeArr.size();i++) {
            for (HashMap<String, Object> map : workflowValues) {
                Set set = map.entrySet();
                Iterator it = set.iterator();
                while (it.hasNext()) {
                    Map.Entry me = (Map.Entry) it.next();
                    if (startTimeArr.get(i).equals(me.getKey())) {
                        startTimeDate = (String) map.get(startTimeArr.get(i));
                    }

                    if (endTimeArr.get(i).equals(me.getKey())) {
                        endTimeDate = (String) map.get(endTimeArr.get(i));
                    }
                }
                startTimelong = Long.valueOf(DateTool.getDataOne(startTimeDate, DateTool.DATE_FORMATE_AT_MINUTES));
                endTimelong = Long.valueOf(DateTool.getDataOne(endTimeDate, DateTool.DATE_FORMATE_AT_MINUTES));

                if (startTimelong > endTimelong && startTimelong != endTimelong) {
                    Toast("开始时间不能大于结束时间!");
                    return;
                }
            }
        }

        if(pickPhots.size() == 0){
            showLoading("正在提交");
        }

        bizExtData = new PostBizExtData();
        HashMap<String, Object> map = new HashMap<>();
        map.put("bizformId", mBizForm.getId());              //表单Id
        map.put("title", tv_title.getText().toString());     //自定义标题
        map.put("deptId", deptId);                           //部门 id
        map.put("workflowValues", workflowValues);           //流程 内容
        map.put("wftemplateId", mTemplateId);                //流程模板Id
        map.put("projectId", projectId);                     //项目Id
        map.put("bizCode", mBizForm.getBizCode());           //流程类型
        if (uuid != null && pickPhots.size() > 0) {
            bizExtData.setAttachmentCount(pickPhots.size());
            map.put("attachmentUUId", uuid);
            map.put("bizExtData", bizExtData);
        }
        map.put("memo", edt_memo.getText().toString().trim()); //备注
        LogUtil.dee("新建审批 发送数据:" + MainApp.gson.toJson(map));

        RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(IWfInstance.class).addWfInstance(map, new RCallback<WfInstance>() {
            @Override
            public void success(final WfInstance wfInstance, final Response response) {
                HttpErrorCheck.checkResponse("新建审批cg", response);
                if (wfInstance != null) {
                    isSave = false;
                    wfInstance.setViewed(true);
                    Intent intent = getIntent();
                    intent.putExtra("data", wfInstance);
                    app.finishActivity(WfInAddActivity.this, MainApp.ENTER_TYPE_LEFT, WfInstanceManageActivity.WFIN_FINISH_RUSH, intent);
                } else {
                    Toast("服务器错误");
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
     * 批量上传附件
     * */
    private void newUploadAttachement(){
        showLoading("正在提交");
        try {
            uploadSize = 0;
            uploadNum  = pickPhots.size();
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
                                        if(uploadSize == uploadNum){
                                            subMinInfo();
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

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        switch (requestCode) {

            //上传附件回调
            case SelectPicPopupWindow.GET_IMG:
                pickPhots.addAll((ArrayList<SelectPicPopupWindow.ImageInfo>) data.getSerializableExtra("data"));
                init_gridView_photo();
                break;

            /*附件删除*/
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

        if (!TextUtils.isEmpty(mTemplateId)) {
            wfInstance.wftemplateId = mTemplateId;
        }

        wfInstance.memo = edt_memo.getText().toString().trim();

        if (isSave) {
            DBManager.Instance().putWfInstance(MainApp.gson.toJson(wfInstance));
        }
    }
}
