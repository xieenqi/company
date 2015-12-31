package com.loyo.oa.v2.activity.tasks;

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
import com.loyo.oa.v2.beans.NewUser;
import com.loyo.oa.v2.beans.TaskCheckPoint;
import com.loyo.oa.v2.point.ICheckPoint;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;

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
    private ArrayList<NewUser> reponserData;
    private NewUser newUser;//负责人 信息
    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_child_edit);
        initView();
        newUser=data.getResponsiblePerson();
    }

    private void initView() {

        mIntent = getIntent();
        data = (TaskCheckPoint) mIntent.getSerializableExtra("TaskEdit");
        TaskId = mIntent.getStringExtra("TaskId");
        reponserData = (ArrayList<NewUser>)mIntent.getSerializableExtra("allUsers");

        img_title_left = (LinearLayout) findViewById(R.id.img_title_left);
        img_title_left.setOnClickListener(click);
        img_title_right = (RelativeLayout) findViewById(R.id.img_title_right);
        img_title_right.setOnClickListener(click);
        et_info = (EditText) findViewById(R.id.et_info);
        et_info.setText(data.getcontent());
        tv_responser = (TextView) findViewById(R.id.tv_responser);
        tv_responser.setText(data.getResponsiblePerson().getName());
        tv_title_1 = (TextView) findViewById(R.id.tv_title_1);
        tv_title_1.setText("编辑子任务");
        ll_responser = (LinearLayout) findViewById(R.id.ll_responser);
        ll_responser.setOnClickListener(click);
        btn_delete = (Button) findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(click);
    }

    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
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

        app.getRestAdapter().create(ICheckPoint.class).updateChildTaskInfo(TaskId, data.getId(), parmas, new RCallback<TaskCheckPoint>() {
            @Override
            public void success(TaskCheckPoint taskCheckPoint, Response response) {
                Toast("更新子任务成功");
                MainApp.getMainApp().finishActivity(ChildTaskEdit.this, MainApp.ENTER_TYPE_TOP,RESULT_OK,new Intent());
            }
            @Override
            public void failure(RetrofitError error) {
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
        app.getRestAdapter().create(ICheckPoint.class).deleteChildTaskInfo(TaskId, data.getId(), new RCallback<TaskCheckPoint>() {
            @Override
            public void success(TaskCheckPoint taskCheckPoint, Response response) {
                Toast("删除子任务成功");
                MainApp.getMainApp().finishActivity(ChildTaskEdit.this, MainApp.ENTER_TYPE_TOP, RESULT_OK, new Intent());
            }

            @Override
            public void failure(RetrofitError error) {
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK&&requestCode==300){
            newUser=(NewUser) data.getSerializableExtra("user");
            tv_responser.setText(newUser.getName());
        }
    }

    /*返回监听*/
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
