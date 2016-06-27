package com.loyo.oa.v2.ui.activity.wfinstance;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.adapter.WfInstanceTypeSelectListViewAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.BizForm;
import com.loyo.oa.v2.beans.BizFormFields;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.IWfInstance;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.ViewUtil;
import com.loyo.oa.v2.customview.GeneralPopView;
import java.util.ArrayList;
import java.util.HashMap;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【审批类型】选择
 *  v2.2 新版审批创建流程
 * create by yyy on 2016/06/07
 */
public class WfInTypeSelectActivity extends BaseActivity implements View.OnClickListener{

    public ViewGroup img_title_left;
    public ListView listView_bizform;
    public WfInstanceTypeSelectListViewAdapter wfInstanceTypeSelectListViewAdapter;
    public ArrayList<BizForm> lstData_BizForm = new ArrayList<>();
    public PaginationX pagination = new PaginationX(20);
    public BizForm mBizForm;
    public Bundle  mBundle;
    public Intent  mIntent;
    public static WfInTypeSelectActivity instance = null;

    public String projectId = null;
    public String projectTitle = null;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wfinstance_type_select);
        instance = this;
        initUI();
    }

    private void initUI() {
        super.setTitle("选择类别");
        try {
            projectId = getIntent().getExtras().getString("projectId");
            projectTitle = getIntent().getExtras().getString("projectTitle");
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        getData_BizForm();
        img_title_left = (ViewGroup) findViewById(R.id.img_title_left);
        img_title_left.setOnClickListener(this);
        img_title_left.setOnTouchListener(new ViewUtil.OnTouchListener_view_transparency());
        listView_bizform = (ListView) findViewById(R.id.listView_bizform);
        if (lstData_BizForm != null) {
            wfInstanceTypeSelectListViewAdapter = new WfInstanceTypeSelectListViewAdapter(this, lstData_BizForm, false);
            listView_bizform.setAdapter(wfInstanceTypeSelectListViewAdapter);
            listView_bizform.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                    mBizForm = lstData_BizForm.get((int) id);
                    if (mBizForm != null) {
                        getBizForm();
                    } else {
                        Toast("类型不存在");
                    }
                }
            });
        }
    }

    /**
     * 过滤审批内容没有被启用的数据
     *
     * @param field
     * @return
     */
    private ArrayList<BizFormFields> filedWfinstanceInfo(ArrayList<BizFormFields> field) {

        ArrayList<BizFormFields> newField = new ArrayList<>();
        if (null == field) {
            return newField;
        }
        for (BizFormFields ele : field) {
            if (ele.isEnable()) {
                newField.add(ele);
            }
        }
        return newField;
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, RESULT_CANCELED, null);
                break;

            default:
                break;
        }
    }

    /**
     * 获取审批类型详情
     * */
    private void getBizForm(){
        showLoading("");
        RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(IWfInstance.class).getWfBizForm(mBizForm.getId(), new RCallback<BizForm>() {
            @Override
            public void success(final BizForm bizForm, final Response response) {
                HttpErrorCheck.checkResponse("获取审批【类型】详情:", response);
                if (null != bizForm) {
                    bizForm.setFields(filedWfinstanceInfo(bizForm.getFields()));
                    if (null == bizForm.getFields() || bizForm.getFields().size() == 0) {
                        final GeneralPopView dailog = showGeneralDialog(true, false, "该审批类别未设置(未启用)审批内容,\n请选择其它类别！");
                        dailog.setNoCancelOnclick(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dailog.dismisDialog();
                            }
                        });
                    } else {
                        mBundle = new Bundle();
                        mBundle.putSerializable("bizForm", bizForm);
                        mBundle.putString("projectTitle", projectTitle);
                        mBundle.putString("projectId", projectId);
                        app.startActivityForResult(WfInTypeSelectActivity.this, ProcessSelectActivity.class, MainApp.ENTER_TYPE_RIGHT, 0, mBundle);
                    }
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
     * 获取审批类别列表
     * */
    private void getData_BizForm() {
        showLoading("");
        HashMap<String, Object> params = new HashMap<>();
        params.put("pageIndex", pagination.getPageIndex());
        params.put("pageSize", 2000);
        RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(IWfInstance.class).getWfBizForms(params, new RCallback<PaginationX<BizForm>>() {
            @Override
            public void success(final PaginationX<BizForm> bizFormPaginationX, final Response response) {
                HttpErrorCheck.checkResponse("获取审批【类型列表】", response);
                if (null != bizFormPaginationX) {
                    pagination = bizFormPaginationX;
                    pagination.records = filedBizFormInfo(pagination.records);
                    lstData_BizForm.addAll(pagination.getRecords());
                    wfInstanceTypeSelectListViewAdapter.notifyDataSetChanged();
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
     * 过滤审批liuc 流程没有被启用的数据
     *
     * @param field
     * @return
     */
    private ArrayList<BizForm> filedBizFormInfo(ArrayList<BizForm> field) {
        ArrayList<BizForm> newField = new ArrayList<>();
        for (BizForm ele : field) {
            if (ele.isEnable() && !"赢单审核".equals(ele.getName())) {//yaoq过滤赢单审核160422
                newField.add(ele);
            }
        }
        return newField;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == WfInstanceManageActivity.WFIN_FINISH_RUSH){
            app.finishActivity(WfInTypeSelectActivity.this, MainApp.ENTER_TYPE_LEFT,0x09, new Intent());
        }
    }
}
