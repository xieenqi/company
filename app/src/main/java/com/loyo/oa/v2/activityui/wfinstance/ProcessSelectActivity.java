package com.loyo.oa.v2.activityui.wfinstance;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.wfinstance.adapter.ProcessChooseAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.activityui.wfinstance.bean.BizForm;
import com.loyo.oa.v2.activityui.wfinstance.bean.WfTemplate;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.IWfInstance;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【审批流程】选择
 * v2.2 新版审批创建流程
 * Created by yyy on 2016/06/07
 */
public class ProcessSelectActivity extends BaseActivity {

    private ListView lv_deptList;
    private LinearLayout img_title_left;
    private Bundle mBundle;
    private BizForm mBizForm;
    private ArrayList<WfTemplate> wfTemplateArrayList;
    public static ProcessSelectActivity instance = null;

    public String projectId;
    public String projectTitle;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department_choose);
        instance = this;
        initView();
    }

    private void initView() {
        super.setTitle("选择流程");
        projectId = getIntent().getExtras().getString("projectId");
        projectTitle = getIntent().getExtras().getString("projectTitle");
        mBizForm = (BizForm) getIntent().getExtras().getSerializable("bizForm");
        img_title_left = (LinearLayout) findViewById(R.id.img_title_left);
        img_title_left.setOnClickListener(click);
        lv_deptList = (ListView) findViewById(R.id.lv_deptList);
        intBizForm();

        /**
         * 列表监听
         * */
        lv_deptList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                mBundle = new Bundle();
                mBundle.putSerializable("bizForm", mBizForm);
                mBundle.putString("title", wfTemplateArrayList.get(position).getTitle());
                mBundle.putString("mTemplateId", wfTemplateArrayList.get(position).getId());
                mBundle.putString("projectTitle", projectTitle);
                mBundle.putString("projectId", projectId);
                mBundle.putString("Process", wfTemplateArrayList.get(position).content);//流程说明
                app.startActivityForResult(ProcessSelectActivity.this, WfInAddActivity.class, MainApp.ENTER_TYPE_RIGHT, 0, mBundle);
            }
        });
    }

    /**
     * 获取审批流程数据
     */
    private void intBizForm() {
        if (!mBizForm.isEnable() && null == mBizForm.getFields()) {
            Toast("该审批类型没有配置流程，请重新选择!");
            return;
        }

        RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(IWfInstance.class).getWfTemplate(mBizForm.getId(), new RCallback<ArrayList<WfTemplate>>() {
            @Override
            public void success(final ArrayList<WfTemplate> bizFormFieldsPaginationX, final Response response) {
                HttpErrorCheck.checkResponse("获取审批流程", response);
                wfTemplateArrayList = bizFormFieldsPaginationX;
                final ProcessChooseAdapter adapter = new ProcessChooseAdapter(ProcessSelectActivity.this, bizFormFieldsPaginationX);
                lv_deptList.setAdapter(adapter);
            }

            @Override
            public void failure(final RetrofitError error) {
                HttpErrorCheck.checkError(error);
                Toast("获取审批流程失败");
                super.failure(error);
            }
        });
    }

    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            switch (v.getId()) {
                case R.id.img_title_left:
                    app.finishActivity(ProcessSelectActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_CANCELED, null);
                    break;

                default:
                    break;
            }
        }
    };

    /**
     * Ui刷新回调
     */
    @Subscribe
    public void rushListData(BizForm bizForm) {
        app.finishActivity(ProcessSelectActivity.this, MainApp.ENTER_TYPE_LEFT, WfInstanceManageActivity.WFIN_FINISH_RUSH, new Intent());
    }

/*    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == WfInstanceManageActivity.WFIN_FINISH_RUSH){
            app.finishActivity(ProcessSelectActivity.this, MainApp.ENTER_TYPE_LEFT, WfInstanceManageActivity.WFIN_FINISH_RUSH, new Intent());
        }
    }*/
}
