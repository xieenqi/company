package com.loyo.oa.v2.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
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
import com.loyo.oa.v2.adapter.SignInGridViewAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Attachment;
import com.loyo.oa.v2.beans.BizForm;
import com.loyo.oa.v2.beans.BizFormFields;
import com.loyo.oa.v2.beans.Parameters.WfInstanceAdd;
import com.loyo.oa.v2.beans.WfInstance;
import com.loyo.oa.v2.beans.WfTemplate;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.db.DBManager;
import com.loyo.oa.v2.point.IAttachment;
import com.loyo.oa.v2.point.IWfInstance;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.CommonAdapter.CommonAdapter;
import com.loyo.oa.v2.tool.CommonAdapter.ViewHolder;
import com.loyo.oa.v2.tool.CommonSubscriber;
import com.loyo.oa.v2.tool.Config_project;
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

import retrofit.RetrofitError;
import retrofit.client.Response;

@EActivity(R.layout.activity_wfinstance_add)
public class WfInstanceAddActivity extends BaseActivity {

    public static final int RESULT_WFINSTANCT_TYPE = 3;

    @ViewById ViewGroup img_title_left;
    @ViewById ViewGroup img_title_right;
    @ViewById ViewGroup layout_wfinstance_data;

    @ViewById Button btn_add;

    @ViewById TextView tv_WfTemplate;
    @ViewById TextView tv_bizform;

    @ViewById GridView gridView_photo;
    @ViewById EditText edt_memo;

    @Extra String projectId;

    //要提交的数据的展示容器
    @ViewById LinearLayout wfinstance_data_container;

    WfInstanceAdd wfInstanceAdd = new WfInstanceAdd();

    //要提交的数据
    private ArrayList<HashMap<String, Object>> submitData = new ArrayList<HashMap<String, Object>>();
    BizForm mBizForm;
    ArrayList<WfTemplate> wfTemplateArrayList;
    ArrayList<Attachment> lstData_Attachment = new ArrayList<>();

    SignInGridViewAdapter signInGridViewAdapter;
    String uuid = StringUtil.getUUID();

    //选择流程
    AlertDialog dialog_follow;
    String mTemplateId;

    @AfterViews
    void init() {
        super.setTitle("新建审批");

        img_title_left.setOnTouchListener(Global.GetTouch());
        img_title_right.setOnTouchListener(Global.GetTouch());

        btn_add.setOnTouchListener(Global.GetTouch());

        init_gridView_photo();
        getTempWfintance();
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

            @Override
            public void failure(RetrofitError error) {
                Toast("获取附件失败");
                super.failure(error);
            }
        });
    }

    /**
     * 获取审批模板
     */
    void getTempWfintance() {

        WfInstance wfInstance = DBManager.Instance().getWfInstance();
        if (wfInstance == null) {
            return;
        }

        if (!TextUtils.isEmpty(wfInstance.getWftemplateId())) {
            mTemplateId = wfInstance.getWftemplateId();
        }

        if (wfInstance.getBizform() != null) {
            mBizForm = wfInstance.getBizform();
            setBizForm();
        }

        edt_memo.setText(wfInstance.getMemo());
    }

    void init_gridView_photo() {
        signInGridViewAdapter = new SignInGridViewAdapter(this, lstData_Attachment, true, true);
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
            public void convert(ViewHolder holder, WfTemplate w) {
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
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || data == null) {
            return;
        }

        switch (requestCode) {
            case RESULT_WFINSTANCT_TYPE:
                if (data.getExtras() != null && data.getSerializableExtra(BizForm.class.getName()) != null) {
                    mBizForm = (BizForm) data.getSerializableExtra(BizForm.class.getName());
                    if (mBizForm != null) {
                        setBizForm();
                    }
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
                                Utils.uploadAttachment(uuid, newFile).subscribe(new CommonSubscriber(this) {
                                    @Override
                                    public void onNext(Serializable serializable) {
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
                            init_gridView_photo();
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

    void setBizForm() {
        tv_bizform.setText(mBizForm.getName());
        wfInstanceAdd.setBizformId(mBizForm.getId());
        if(null==mBizForm.getFields()){
            Toast("该审批类型没有配置流程，请重新选择!");
            return;
        }
        layout_wfinstance_data.setVisibility(View.VISIBLE);

        submitData.clear();
        wfinstance_data_container.removeAllViews();
        addData();

        RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(IWfInstance.class).getWfTemplate(mBizForm.getId(), new RCallback<ArrayList<WfTemplate>>() {
            @Override
            public void success(ArrayList<WfTemplate> bizFormFieldsPaginationX, Response response) {
                wfTemplateArrayList = bizFormFieldsPaginationX;
                initUI_Dialog_WfTemplate();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast("获取审批流程失败");
                super.failure(error);
            }
        });
    }

    @Click({R.id.img_title_left, R.id.btn_add, R.id.layout_WfTemplate, R.id.layout_wfinstance})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, RESULT_CANCELED, null);
                break;
            case R.id.btn_add:
                addData();
                break;
            case R.id.layout_WfTemplate:
                if (mBizForm == null) {
                    Toast("请选择类型");
                    break;
                } else if (dialog_follow != null && !dialog_follow.isShowing()) {
                    dialog_follow.show();
                }
                break;

            case R.id.layout_wfinstance:
                app.startActivityForResult(this, WfInstanceTypeSelectManageActivity.class, MainApp.ENTER_TYPE_RIGHT, RESULT_WFINSTANCT_TYPE, null);
                break;
        }
    }

    /**
     * 新增数据
     */
    void addData() {
        if (mBizForm == null) {
            Toast("请选择类型");
            return;
        }

        if(null==mBizForm.getFields()){
            return;
        }

        HashMap<String, Object> newValues = new HashMap<String, Object>();
        for (BizFormFields field : mBizForm.getFields()) {
            newValues.put(field.getId(), "");
        }
        submitData.add(newValues);

        WfinstanceViewGroup viewGroup = new WfinstanceViewGroup(this, mBizForm.getFields(), submitData);
        viewGroup.bindView(submitData.size() > 0 ? submitData.size() - 1 : submitData.size(), wfinstance_data_container);
    }
    //    @Click(R.id.layout_delete2)
    //    void delete() {
    //        bizFormFieldsListViewAdapter.setEmpty();
    //        layout_edit.setVisibility(View.GONE);
    //    }

    @Click(R.id.img_title_right)
    void submit() {
        if (TextUtils.isEmpty(mTemplateId)) {
            Toast("请选择流程");
            return;
        }
        if (submitData.isEmpty()) {
            Toast("请输入审批内容");
            return;
        }

        Log.e(getClass().getSimpleName(), "commit wfinstance ,size : " + submitData.size());
        for (int k = 0; k < submitData.size(); k++) {
            HashMap<String, Object> map_Values = submitData.get(k);
            for (BizFormFields field : mBizForm.getFields()) {
                String value = (String) map_Values.get(field.getId());
                if (field.isRequired() && (!map_Values.keySet().contains(field.getId()) || TextUtils.isEmpty(value))) {
                    Toast("请填写\"必填项\"");
                    return;
                }
            }
        }

        HashMap<String, Object> jsonObject = new HashMap<>();

        app.logUtil.e(" 审批 : " + projectId);
        jsonObject.put("projectId", projectId);
        jsonObject.put("bizformId", mBizForm.getId());
        jsonObject.put("title", mBizForm.getName() + " " + tv_WfTemplate.getText().toString());
        jsonObject.put("wftemplateId", mTemplateId);

        ArrayList<HashMap<String, String>> workflowValues = new ArrayList<>();
        wfInstanceAdd.getWorkflowValuesAdd().getWfInstanceValuesDatas().clear();
        for (int k = 0; k < submitData.size(); k++) {
            HashMap<String, String> jsonMapValues = new HashMap<>();
            HashMap<String, Object> map_Values = submitData.get(k);
            for (BizFormFields field : mBizForm.getFields()) {
                for (String key : map_Values.keySet()) {
                    if (!TextUtils.equals(field.getId(), key)) {
                        continue;
                    }
                    String value = (String) map_Values.get(key);
                    jsonMapValues.put(key, value);
                }
            }
            workflowValues.add(jsonMapValues);
        }
        //        jsonObject_workflowValues.put("wfInstanceValuesDatas", jsonObject_wfInstanceValuesDatas);
        jsonObject.put("workflowValues", workflowValues);
        jsonObject.put("memo", edt_memo.getText().toString().trim());

        if (uuid != null && lstData_Attachment.size() > 0) {
            jsonObject.put("attachmentUUId", uuid);
        }


        RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(IWfInstance.class).addWfInstance(jsonObject, new RCallback<WfInstance>() {
            @Override
            public void success(WfInstance wfInstance, Response response) {
                if (wfInstance != null) {
                    Toast(getString(R.string.app_add) + getString(R.string.app_succeed));
                    isSave = false;
                    //如果不clear,会提示java.io.NotSerializableException
                    wfInstance.setAck(true);
                    Intent intent = getIntent();
                    intent.putExtra("data", wfInstance);
                    app.finishActivity(WfInstanceAddActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Toast("提交审批失败");
                super.failure(error);
            }
        });
    }

    boolean isSave = true;

    @Override
    protected void onDestroy() {
        super.onDestroy();

        DBManager.Instance().deleteWfInstance();

        WfInstance wfInstance = new WfInstance();
        wfInstance.setAttachments(null);
        wfInstance.setCreator(null);

        if (mBizForm != null) {
            wfInstance.setBizform(mBizForm);
        }

        if (!TextUtils.isEmpty(mTemplateId)) {
            wfInstance.setWftemplateId(mTemplateId);
        }

        wfInstance.setMemo(edt_memo.getText().toString().trim());

        if (isSave) {
            DBManager.Instance().putWfInstance(MainApp.gson.toJson(wfInstance));
        }
    }

}
