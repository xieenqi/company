package com.loyo.oa.v2.activityui.tasks;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.OrganizationalMember;
import com.loyo.oa.v2.beans.TaskCheckPoint;
import com.loyo.oa.v2.point.ICheckPoint;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【编辑 子任务】   页面
 * Created xnq 15/12/9.
 */
public class ChildTaskEdit extends BaseActivity {

    private LinearLayout img_title_left;
    private RelativeLayout img_title_right;
    private EditText et_info;
    private LinearLayout ll_responser;
    private Button btn_delete;
    private TextView tv_responser;
    private TextView tv_title_1;
    private TaskCheckPoint data;
    private String TaskId;
    private ArrayList<OrganizationalMember> reponserData;
    private OrganizationalMember newUser;//负责人 信息
    private Intent mIntent;
    private boolean isReponser;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_child_edit);
        initView();
        newUser=data.getResponsiblePerson();
    }

    private void initView() {

        mIntent = getIntent();
        data = (TaskCheckPoint) mIntent.getSerializableExtra("TaskEdit");
        TaskId = mIntent.getStringExtra("TaskId");
        reponserData = (ArrayList<OrganizationalMember>)mIntent.getSerializableExtra("allUsers");
        isReponser = mIntent.getBooleanExtra("isReponser",false);

        img_title_left = (LinearLayout) findViewById(R.id.img_title_left);
        img_title_right = (RelativeLayout) findViewById(R.id.img_title_right);
        et_info = (EditText) findViewById(R.id.et_info);
        tv_responser = (TextView) findViewById(R.id.tv_responser);
        tv_title_1 = (TextView) findViewById(R.id.tv_title_1);
        ll_responser = (LinearLayout) findViewById(R.id.ll_responser);
        btn_delete = (Button) findViewById(R.id.btn_delete);

        img_title_left.setOnClickListener(click);
        et_info.setText(data.getcontent());
        tv_responser.setText(data.getResponsiblePerson().getName());
        tv_title_1.setText("编辑子任务");

        /*判断是否为子任务负责人*/
        if(isReponser){
            btn_delete.setVisibility(View.VISIBLE);
            img_title_right.setVisibility(View.VISIBLE);

            img_title_right.setOnClickListener(click);
            ll_responser.setOnClickListener(click);
            btn_delete.setOnClickListener(click);
        }else{
            et_info.setEnabled(false);
            btn_delete.setVisibility(View.INVISIBLE);
            img_title_right.setVisibility(View.INVISIBLE);
        }
    }

    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            switch (v.getId()) {

                /*返回*/
                case R.id.img_title_left:
                    finish();
                    break;

                case R.id.img_title_right:
                    if (!TextUtils.isEmpty(et_info.getText().toString())) {
                        updateChildTaskInfo();
                    } else {
                        Toast("任务内容不能为空");
                    }
                    break;

                /*修改负责人*/
                case R.id.ll_responser:
                    responserSelect();
                    break;

                /*删除子任务*/
                case R.id.btn_delete:
                    deleteChildTask();
                    break;

                default:
                    break;
            }
        }
    };

    /**
     * 提交更新子任务   xnq
     */
    private void updateChildTaskInfo() {
        HashMap<String, Object> parmas = new HashMap<>();
        parmas.put("content", et_info.getText().toString());
        parmas.put("responsiblePerson",newUser);

        RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(ICheckPoint.class).updateChildTaskInfo(TaskId, data.getId(), parmas, new RCallback<TaskCheckPoint>() {
            @Override
            public void success(final TaskCheckPoint taskCheckPoint,final Response response) {
                Toast("更新子任务成功");
                MainApp.getMainApp().finishActivity(ChildTaskEdit.this, MainApp.ENTER_TYPE_TOP,RESULT_OK,new Intent());
            }
            @Override
            public void failure(final RetrofitError error) {
                Toast("更新子任务失败");
                LogUtil.d(error.getUrl()+"   错误信息 "+error.getMessage()+" 热舞id "+ MainApp.gson.toJson(data));
                super.failure(error);
            }
        });
    }

    /**
     * 删除 任务 的子任务 xnq
     */
    private void deleteChildTask(){
        RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(ICheckPoint.class).deleteChildTaskInfo(TaskId, data.getId(), new RCallback<TaskCheckPoint>() {
            @Override
            public void success(final TaskCheckPoint taskCheckPoint,final Response response) {
                Toast("删除子任务成功");
                MainApp.getMainApp().finishActivity(ChildTaskEdit.this, MainApp.ENTER_TYPE_TOP, RESULT_OK, new Intent());
            }

            @Override
            public void failure(final RetrofitError error) {
                Toast("删除子任务失败");
                super.failure(error);
            }
        });
    }

    /** xnq
     * 选择修改子任务的负责人
     */
    private void responserSelect(){
        Bundle b1 = new Bundle();
        b1.putSerializable("users", reponserData);
        app.startActivityForResult(this, ChildTaskResponserSelectActivity_.class, MainApp.ENTER_TYPE_RIGHT, 300, b1);
    }

    @Override
    protected void onActivityResult(final int requestCode,final int resultCode,final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK&&requestCode==300){
            newUser=(OrganizationalMember) data.getSerializableExtra("user");
            tv_responser.setText(newUser.getName());
        }
    }

    /*返回监听*/
    public boolean onKeyDown(final int keyCode,final KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
