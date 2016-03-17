package com.loyo.oa.v2.activity.wfinstance;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.project.ProjectSearchActivity;
import com.loyo.oa.v2.adapter.SignInGridViewAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Attachment;
import com.loyo.oa.v2.beans.BizForm;
import com.loyo.oa.v2.beans.BizFormFields;
import com.loyo.oa.v2.beans.Department;
import com.loyo.oa.v2.beans.Parameters.WfInstanceAdd;
import com.loyo.oa.v2.beans.PostBizExtData;
import com.loyo.oa.v2.beans.Project;
import com.loyo.oa.v2.beans.UserInfo;
import com.loyo.oa.v2.beans.WfInstance;
import com.loyo.oa.v2.beans.WfTemplate;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.db.DBManager;
import com.loyo.oa.v2.point.IAttachment;
import com.loyo.oa.v2.point.IWfInstance;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.commonadapter.CommonAdapter;
import com.loyo.oa.v2.tool.commonadapter.ViewHolder;
import com.loyo.oa.v2.tool.CommonSubscriber;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.SelectPicPopupWindow;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.tool.customview.WfinstanceViewGroup;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【新建 审批】 界面
 */
@EActivity(R.layout.activity_wfinstance_add)
public class WfInstanceAddActivity extends BaseActivity {

    /**
     * 审批类型选择 请求码
     */
    public static final int RESULT_WFINSTANCT_TYPE = 3;
    /**
     * 部门选择 请求码
     */
    public static final int RESULT_DEPT_CHOOSE = 5;

    @ViewById ViewGroup img_title_left;
    @ViewById ViewGroup img_title_right;
    @ViewById ViewGroup layout_wfinstance_data;
    @ViewById ViewGroup ll_dept;
    @ViewById TextView tv_dept;
    @ViewById ViewGroup ll_project;
    @ViewById TextView tv_project;
    @ViewById Button btn_add;
    @ViewById TextView tv_WfTemplate;
    @ViewById TextView tv_bizform;
    @ViewById GridView gridView_photo;
    @ViewById EditText edt_memo;
    @Extra
    String projectId;
    @Extra
    String projectTitle;

    public String deptId;

    //要提交的数据的展示容器
    @ViewById LinearLayout wfinstance_data_container;
    WfInstanceAdd wfInstanceAdd = new WfInstanceAdd();
    ArrayList postValue = new ArrayList<>();

    /**
     * 审批 内容 数据
     * 要提交的数据  xnq
     */
    private ArrayList<HashMap<String, Object>> submitData = new ArrayList<HashMap<String, Object>>();
    //审批内容 新建一个的对象 集合
    private List<WfinstanceViewGroup> WfinObj = new ArrayList<WfinstanceViewGroup>();
    private BizForm mBizForm;
    private ArrayList<WfTemplate> wfTemplateArrayList;
    private ArrayList<Attachment> lstData_Attachment = new ArrayList<>();
    private ArrayList<Boolean> isRequiredList = new ArrayList<>();
    private SignInGridViewAdapter signInGridViewAdapter;
    private String uuid = StringUtil.getUUID();
    //选择流程
    private AlertDialog dialog_follow;
    private String mTemplateId;
    private PostBizExtData bizExtData;

    @AfterViews
    void init() {
        super.setTitle("新建审批");
        img_title_left.setOnTouchListener(Global.GetTouch());
        img_title_right.setOnTouchListener(Global.GetTouch());
        btn_add.setOnTouchListener(Global.GetTouch());
        ll_project.setOnClickListener(click);
        init_gridView_photo();
        //getTempWfintance();
        projectAddWfinstance();
        setDefaultDept();
    }

    /**
     * 设置默认的部门 信息
     */
    public void setDefaultDept() {
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
     * 获取附件
     */
    private void getAttachments() {
        Utils.getAttachments(uuid, new RCallback<ArrayList<Attachment>>() {
            @Override
            public void success(final ArrayList<Attachment> attachments,final Response response) {
                lstData_Attachment = attachments;
                init_gridView_photo();
            }

            @Override
            public void failure(final RetrofitError error) {
                Toast("获取附件失败");
                super.failure(error);
            }
        });
    }

    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            switch (v.getId()) {
                case R.id.ll_project:
                    Bundle bundle2 = new Bundle();
                    bundle2.putInt("from", BaseActivity.WFIN_ADD);
                    bundle2.putInt(ExtraAndResult.EXTRA_STATUS, 1);
                    app.startActivityForResult(WfInstanceAddActivity.this, ProjectSearchActivity.class,
                            MainApp.ENTER_TYPE_RIGHT,
                            ExtraAndResult.REQUSET_PROJECT, bundle2);
                    break;

                default:
                    break;
            }
        }
    };

    /**
     * 获取审批模板
     */
    void getTempWfintance() {

        WfInstance wfInstance = DBManager.Instance().getWfInstance();
        if (wfInstance == null) {
            return;
        }

        if (!TextUtils.isEmpty(wfInstance.wftemplateId)) {
            mTemplateId = wfInstance.wftemplateId;
        }

        if (wfInstance.bizForm != null) {
            mBizForm = wfInstance.bizForm;
            intBizForm();
        }
        edt_memo.setText(wfInstance.memo);

    }

    void init_gridView_photo() {
        signInGridViewAdapter = new SignInGridViewAdapter(this, lstData_Attachment, true, true, true, 0);
        SignInGridViewAdapter.setAdapter(gridView_photo, signInGridViewAdapter);
    }

    void initUI_Dialog_WfTemplate() {
        if (wfTemplateArrayList == null || wfTemplateArrayList.size() == 0) {
            Toast("错误:没有配置流程!");
            mTemplateId = "";
            tv_WfTemplate.setText("");
            return;
        }

        CommonAdapter followAdapter = new CommonAdapter<WfTemplate>(mContext, wfTemplateArrayList, R.layout.item_listview_product_select) {
            @Override
            public void convert(final ViewHolder holder,final WfTemplate w) {
                holder.setText(R.id.tv, w.getTitle());
            }
        };

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_products_select, null);
        ListView listView_follow = (ListView) layout.findViewById(R.id.listView);
        listView_follow.setAdapter(followAdapter);
        listView_follow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent,final View view,final int position,final long id) {
                mTemplateId = wfTemplateArrayList.get(position).getId();
                tv_WfTemplate.setText(wfTemplateArrayList.get(position).getTitle());
                dialog_follow.dismiss();
            }
        });

        builder.setView(layout);
        dialog_follow = builder.create();

        if (!TextUtils.isEmpty(mTemplateId)) {
            for (int i = 0; i < wfTemplateArrayList.size(); i++) {
                WfTemplate t = wfTemplateArrayList.get(i);
                if (t.getId() == mTemplateId) {
                    tv_WfTemplate.setText(t.getTitle());
                }
            }
        }
        mTemplateId = wfTemplateArrayList.get(0).getId();
        tv_WfTemplate.setText(wfTemplateArrayList.get(0).getTitle());
    }

    @Override
    protected void onActivityResult(final int requestCode,final int resultCode,final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        switch (requestCode) {

            //类型选择返回
            case RESULT_WFINSTANCT_TYPE:
                mBizForm = (BizForm) data.getSerializableExtra(BizForm.class.getName());
                if (mBizForm != null) {
                    intBizForm();
                }
                break;

            case SelectPicPopupWindow.GET_IMG:
                try {
                    ArrayList<SelectPicPopupWindow.ImageInfo> pickPhots = (ArrayList<SelectPicPopupWindow.ImageInfo>) data.getSerializableExtra("data");
                    for (SelectPicPopupWindow.ImageInfo item : pickPhots) {
                        Uri uri = Uri.parse(item.path);
                        File newFile = Global.scal(this, uri);

                        if (newFile != null && newFile.length() > 0) {
                            if (newFile.exists()) {
                                Utils.uploadAttachment(uuid,12,newFile).subscribe(new CommonSubscriber(this) {
                                    @Override
                                    public void onNext(final Serializable serializable) {
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

            /*附件删除*/
            case FinalVariables.REQUEST_DEAL_ATTACHMENT:

                final Attachment delAttachment = (Attachment) data.getSerializableExtra("delAtm");
                HashMap<String,Object> map = new HashMap<String, Object>();
                map.put("bizType",12);
                map.put("uuid", uuid);
                RestAdapterFactory.getInstance().build(Config_project.API_URL_ATTACHMENT()).create(IAttachment.class).remove(String.valueOf(delAttachment.getId()),map, new RCallback<Attachment>() {
                    @Override
                    public void success(final Attachment attachment,final Response response) {
                        Toast("删除附件成功!");
                        lstData_Attachment.remove(delAttachment);
                        init_gridView_photo();
                    }

                    @Override
                    public void failure(final RetrofitError error) {
                        Toast("删除附件失败!");
                        super.failure(error);
                    }
                });
                break;

            /*选择部门回调*/
            case RESULT_DEPT_CHOOSE:
                UserInfo userInfo = (UserInfo) data.getSerializableExtra(DepartmentChoose.class.getName());
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

    /**
     * 审批 类型 选择 初始化 流程列表
     */
    private void intBizForm() {

        tv_bizform.setText(mBizForm.getName());
        wfInstanceAdd.setBizformId(mBizForm.getId());

        if (null == mBizForm.getFields()) {
            Toast("该审批类型没有配置流程，请重新选择!");
            return;
        }

        layout_wfinstance_data.setVisibility(View.VISIBLE);
        submitData.clear();
        wfinstance_data_container.removeAllViews();
        addTypeData();

        RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(IWfInstance.class).getWfTemplate(mBizForm.getId(), new RCallback<ArrayList<WfTemplate>>() {
            @Override
            public void success(final ArrayList<WfTemplate> bizFormFieldsPaginationX,final Response response) {
                HttpErrorCheck.checkResponse("获取审批流程", response);
                wfTemplateArrayList = bizFormFieldsPaginationX;
                initUI_Dialog_WfTemplate();
            }

            @Override
            public void failure(final RetrofitError error) {
                HttpErrorCheck.checkError(error);
                Toast("获取审批流程失败");
                super.failure(error);
            }
        });
    }

    @Click({R.id.img_title_left, R.id.btn_add, R.id.layout_WfTemplate, R.id.layout_wfinstance, R.id.ll_dept})
    void onClick(final View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, RESULT_CANCELED, null);
                break;
            case R.id.btn_add:
                addTypeData();
                break;
            case R.id.layout_WfTemplate:
                if (mBizForm == null) {
                    Toast("请选择类型");
                    break;
                } else if (dialog_follow != null && !dialog_follow.isShowing()) {
                    dialog_follow.show();
                }
                break;
            case R.id.layout_wfinstance://到选择类型页面
                app.startActivityForResult(this, WfInstanceTypeSelectManageActivity.class, MainApp.ENTER_TYPE_RIGHT, RESULT_WFINSTANCT_TYPE, null);
                break;
            case R.id.ll_dept:
                app.startActivityForResult(this, DepartmentChoose.class, MainApp.ENTER_TYPE_RIGHT, RESULT_DEPT_CHOOSE, null);
                break;
            default:
                break;
        }
    }

    public void info() {
        if (WfinObj == null)
            return;
        HashMap<String, Object> mapInfo = WfinObj.get(0).getInfoData();
        for (Map.Entry<String, Object> entry : mapInfo.entrySet()) {
            LogUtil.dll("KEY:" + entry.getKey() + "Value:" + entry.getValue());
        }
    }

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
        WfinstanceViewGroup viewGroup = new WfinstanceViewGroup(this, mBizForm.getFields(), submitData);
        viewGroup.bindView(submitData.size() > 0 ? submitData.size() - 1 : submitData.size(), wfinstance_data_container);
        WfinObj.add(viewGroup);//新增一个内容 就存起来
        addIsRequired();

    }

//        @Click(R.id.layout_delete2)
//        void delete() {
//            bizFormFieldsListViewAdapter.setEmpty();
//            layout_edit.setVisibility(View.GONE);
//        }


    void addIsRequired(){
        LogUtil.dll("执行 addIsRequired");
        for(int i = 0;i<mBizForm.getFields().size();i++){
            isRequiredList.add(mBizForm.getFields().get(i).isRequired());
        }
    }


    /**
     * 确认新建审批
     */
    @Click(R.id.img_title_right)
    void submit() {
        if (TextUtils.isEmpty(mTemplateId)) {
            Toast("请选择流程");
            return;
        }
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
            try{
                for (BizFormFields field : mBizForm.getFields()) {
                    for (String key : map_Values.keySet()) {
                        if (!TextUtils.equals(field.getId(), key)) {
                            continue;
                        }
                        postValue.add(map_Values.get(key));
                        String value = (String) map_Values.get(key);
                        jsonMapValues.put(key, value);
                    }
                }
                workflowValues.add(jsonMapValues);
            }catch(NullPointerException e){
                e.printStackTrace();
                Toast("操作失败！");
                return;
            }
        }

        for(int i = 0;i<postValue.size();i++){
            if(TextUtils.isEmpty(postValue.get(i).toString()) && isRequiredList.get(i)){
                Toast("请填写\"必填项\"");
                return;
            }
        }

        bizExtData = new PostBizExtData();
        HashMap<String, Object> map = new HashMap<>();
        map.put("bizformId", mBizForm.getId());   //表单Id
        map.put("title", mBizForm.getName() + " " + tv_WfTemplate.getText().toString());//类型名加流程名
        map.put("deptId", deptId);                //部门 id
        map.put("workflowValues", workflowValues);//流程 内容
        map.put("wftemplateId", mTemplateId);//流程模板Id
        map.put("projectId", projectId);//项目Id
        map.put("bizCode", mBizForm.getBizCode());//流程类型
        if (uuid != null && lstData_Attachment.size() > 0) {
            bizExtData.setAttachmentCount(lstData_Attachment.size());
            map.put("attachmentUUId", uuid);
            map.put("bizExtData",bizExtData);
        }
        map.put("memo", edt_memo.getText().toString().trim()); //备注
        showLoading("");
        RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(IWfInstance.class).addWfInstance(map, new RCallback<WfInstance>() {
            @Override
            public void success(final WfInstance wfInstance,final Response response) {
                cancelLoading();
                if (wfInstance != null) {
                    isSave = false;
                    wfInstance.ack = true;
                    Intent intent = getIntent();
                    intent.putExtra("data", wfInstance);
                    app.finishActivity(WfInstanceAddActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
                }
            }

            @Override
            public void failure(final RetrofitError error) {
                HttpErrorCheck.checkError(error);
                cancelLoading();
                super.failure(error);
            }
        });
        LogUtil.dll("新建审批发送数据:" + MainApp.gson.toJson(map));
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
