package com.loyo.oa.v2.activity.wfinstance;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.adapter.WfInstanceTypeSelectListViewAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.BizForm;
import com.loyo.oa.v2.beans.BizFormFields;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.WfTemplate;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.IWfInstance;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.ViewUtil;
import com.loyo.oa.v2.tool.customview.GeneralPopView;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshListView;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【审批类型选择】 adapter
 */
public class WfInstanceTypeSelectManageActivity extends BaseActivity implements View.OnClickListener, PullToRefreshBase.OnRefreshListener2 {

    ViewGroup img_title_left;

    public PullToRefreshListView listView_bizform;
    public WfInstanceTypeSelectListViewAdapter wfInstanceTypeSelectListViewAdapter;

    ArrayList<BizForm> lstData_BizForm = new ArrayList<>();
    PaginationX pagination = new PaginationX(20);
    BizForm bizForm;
    private boolean isTopAdd;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wfinstance_type_select);
        initUI();
        onPullDownToRefresh(null);
    }


    private void initUI() {
        super.setTitle("选择类别");
        img_title_left = (ViewGroup) findViewById(R.id.img_title_left);
        img_title_left.setOnClickListener(this);
        img_title_left.setOnTouchListener(new ViewUtil.OnTouchListener_view_transparency());

        listView_bizform = (PullToRefreshListView) findViewById(R.id.listView_bizform);
        listView_bizform.setMode(PullToRefreshBase.Mode.DISABLED);

        if (lstData_BizForm != null) {
            wfInstanceTypeSelectListViewAdapter = new WfInstanceTypeSelectListViewAdapter(this, lstData_BizForm, false);
            listView_bizform.setAdapter(wfInstanceTypeSelectListViewAdapter);
            listView_bizform.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                    bizForm = lstData_BizForm.get((int) id);
                    if (bizForm != null) {
                        showLoading("");
                        RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(IWfInstance.class).getWfBizForm(bizForm.getId(), new RCallback<BizForm>() {
                            @Override
                            public void success(final BizForm bizForm, final Response response) {
                                HttpErrorCheck.checkResponse("获取审批【类型】详情:", response);
                                if (null != bizForm) {
                                    bizForm.setFields(filedWfinstanceInfo(bizForm.getFields()));
                                    if (null == bizForm.getFields() || bizForm.getFields().size() == 0) {//没有审批内容
                                        final GeneralPopView dailog = showGeneralDialog(true, false, "该审批类别未设置(未启用)审批内容,\n请选择其它类别！");
                                        dailog.setNoCancelOnclick(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dailog.dismisDialog();
                                            }
                                        });
                                    } else {

                                        getBiaFormDitial(bizForm);
                                    }
                                }
                            }

                            @Override
                            public void failure(final RetrofitError error) {
                                HttpErrorCheck.checkError(error);
                                super.failure(error);
                            }
                        });

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

    private void getData_BizForm() {
        showLoading("");
        HashMap<String, Object> params = new HashMap<>();
        params.put("pageIndex", pagination.getPageIndex());
        params.put("pageSize", isTopAdd ? lstData_BizForm.size() >= 2000 ? lstData_BizForm.size() : 2000 : 2000);
//        params.put("pageSize", 1000);

        RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(IWfInstance.class).getWfBizForms(params, new RCallback<PaginationX<BizForm>>() {
            @Override
            public void success(final PaginationX<BizForm> bizFormPaginationX, final Response response) {
                HttpErrorCheck.checkResponse("获取审批【类型列表】", response);
                listView_bizform.onRefreshComplete();

                if (null != bizFormPaginationX) {
                    pagination = bizFormPaginationX;
                    pagination.records = filedBizFormInfo(pagination.records);
                    if (isTopAdd) {
                        lstData_BizForm.clear();
                    }
                    lstData_BizForm.addAll(pagination.getRecords());
                    wfInstanceTypeSelectListViewAdapter.notifyDataSetChanged();
                }
                LogUtil.d(MainApp.gson.toJson(bizFormPaginationX));
            }

            @Override
            public void failure(final RetrofitError error) {
                HttpErrorCheck.checkError(error);
//                Toast("获取审批类型失败");
                listView_bizform.onRefreshComplete();
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

    /**
     * 获取审批流程详情
     */
    private void getBiaFormDitial(final BizForm bizForm) {
        showLoading("");
        RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(IWfInstance.class).getWfTemplate(bizForm.getId(), new RCallback<ArrayList<WfTemplate>>() {
            @Override
            public void success(ArrayList<WfTemplate> bizFormFieldsPaginationX, Response response) {
                HttpErrorCheck.checkResponse("获取选择审批流程", response);
                bizFormFieldsPaginationX = filedPaginationInfo(bizFormFieldsPaginationX);
                if (null == bizFormFieldsPaginationX || bizFormFieldsPaginationX.size() == 0) {
                    // Toast("错误:没有配置流程!");
                    final GeneralPopView dailog = showGeneralDialog(true, false, "该审批类别未设置(未启用)审批流程,\n请选择其它类别！");
                    dailog.setNoCancelOnclick(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dailog.dismisDialog();
                        }
                    });
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra(BizForm.class.getName(), bizForm);
                app.finishActivity(WfInstanceTypeSelectManageActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
            }

            @Override
            public void failure(final RetrofitError error) {
                HttpErrorCheck.checkError(error);
//                Toast("获取审批流程失败");
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
    private ArrayList<WfTemplate> filedPaginationInfo(ArrayList<WfTemplate> field) {
        ArrayList<WfTemplate> newField = new ArrayList<>();
        if (null == field) {
            return newField;
        }
        for (WfTemplate ele : field) {
            if (ele.isEnable()) {
                newField.add(ele);
            }
        }
        return newField;
    }

    @Override
    public void onPullDownToRefresh(final PullToRefreshBase refreshView) {
        pagination.setPageIndex(1);
        isTopAdd = true;
        getData_BizForm();
    }

    @Override
    public void onPullUpToRefresh(final PullToRefreshBase refreshView) {
        pagination.setPageIndex(pagination.getPageIndex() + 1);
        isTopAdd = false;
        getData_BizForm();
    }
}
