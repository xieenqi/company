package com.loyo.oa.v2.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.adapter.SignInGridViewAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Attachment;
import com.loyo.oa.v2.beans.NewUser;
import com.loyo.oa.v2.beans.Project;
import com.loyo.oa.v2.beans.Reviewer;
import com.loyo.oa.v2.beans.Task;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.db.DBManager;
import com.loyo.oa.v2.point.IAttachment;
import com.loyo.oa.v2.point.ITask;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.CommonAdapter.CommonAdapter;
import com.loyo.oa.v2.tool.CommonAdapter.ViewHolder;
import com.loyo.oa.v2.tool.CommonSubscriber;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.SelectPicPopupWindow;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.tool.customview.DateTimePickDialog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;

@EActivity(R.layout.activity_tasks_add)
public class TasksAddActivity extends BaseActivity {
    @ViewById ViewGroup img_title_left;
    @ViewById ViewGroup img_title_right;
    @ViewById ViewGroup layout_del;
    @ViewById ViewGroup layout_responsiblePerson;
    @ViewById ViewGroup layout_deadline;
    @ViewById ViewGroup layout_remind;
    @ViewById ViewGroup layout_project;

    @ViewById ImageView img_title_right_toUsers;

    @ViewById TextView tv_responsiblePerson;
    @ViewById TextView tv_toUsers;
    @ViewById TextView tv_deadline;
    @ViewById TextView tv_remind;
    @ViewById TextView tv_Project;

    @ViewById Switch switch_approve;

    @ViewById EditText edt_content;
    @ViewById EditText edt_title;

    @ViewById GridView gridView_photo;

    @Extra Project project;

    SignInGridViewAdapter signInGridViewAdapter;

    NewUser responsiblePerson;
    ArrayList<Attachment> lstData_Attachment = new ArrayList<>();
    ArrayList<Reviewer> members = new ArrayList<>();
    String uuid=StringUtil.getUUID();
    long mDeadline;
    int mRemind;
    AlertDialog dialog_Product;

    @AfterViews
    void initUI() {
        super.setTitle("创建任务");

        Global.SetTouchView(img_title_left,
                img_title_right,
                layout_responsiblePerson,
                layout_deadline,
                layout_del,
                layout_remind,layout_project);

        init_gridView_photo();
        setTouchView(-1);
        getTempTask();
    }

    void getTempTask() {
        mTask = DBManager.Instance().getTask();
        if (mTask == null) {
            return;
        }

        edt_title.setText(mTask.getTitle());
        edt_content.setText(mTask.getContent());
        switch_approve.setChecked(mTask.isReviewFlag());

        if (!TextUtils.isEmpty(mTask.getResponsiblePersonId()) && !StringUtil.isEmpty(mTask.getResponsiblePersonName())) {
            User u = new User();
            u.setId(mTask.getResponsiblePersonId());
            u.setRealname(mTask.getResponsiblePersonName());
            setResponsiblePersion(u);
        }

        if (mTask.getPlanEndAt() > 0) {
            mDeadline = mTask.getPlanEndAt();
            tv_deadline.setText(app.df3.format(new Date(mDeadline * 1000)));
        }
        if(null!=project){
            tv_Project.setText(project.getTitle());
        }
    }

    void init_gridView_photo() {
        signInGridViewAdapter = new SignInGridViewAdapter(this, lstData_Attachment, true, true);
        SignInGridViewAdapter.setAdapter(gridView_photo, signInGridViewAdapter);
    }

    @Click({R.id.img_title_left, R.id.img_title_right, R.id.layout_responsiblePerson, R.id.layout_deadline, R.id.tv_toUsers, R.id.layout_del,R.id.layout_project})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, 0, null);
                break;
            case R.id.img_title_right:
                String title = edt_title.getText().toString().trim();
                if (TextUtils.isEmpty(title)) {
                    Toast(getString(R.string.app_title) + getString(R.string.app_no_null));
                    break;
                }

                String content = edt_content.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    Toast(getString(R.string.app_content) + getString(R.string.app_no_null));
                    break;
                }

                if (mDeadline <= 0) {
                    Toast("截止日期" + getString(R.string.app_no_null));
                    break;
                }

                if (responsiblePerson == null || TextUtils.isEmpty(responsiblePerson.getId())) {
                    Toast("负责人" + getString(R.string.app_no_null));
                    break;
                }

                HashMap<String, Object> map = new HashMap<>();
                map.put("title", title);
                map.put("content", content);
                map.put("responsiblePersons", Arrays.asList(new Reviewer(responsiblePerson)));
                map.put("members", members);
                map.put("actualendAt", mDeadline);
                map.put("remindflag", mRemind > 0);
                map.put("remindtime", mRemind);
                map.put("reworkflag", switch_approve.isChecked());

                if (uuid != null && lstData_Attachment.size() > 0) {
                    map.put("attachmentUUId", uuid);
                }

                if (null!=project) {
                    map.put("projectId", project.getId());
                }

                RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(ITask.class).create(map, new RCallback<Task>() {
                    @Override
                    public void success(Task task, Response response) {
                        task.setAck(true);
                        Toast(getString(R.string.app_add) + getString(R.string.app_succeed));

                        //不需要保存
                        isSave = false;

                        Intent intent = new Intent();
                        intent.putExtra("data", task);
                        setResult(Activity.RESULT_OK, intent);
                        onBackPressed();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        super.failure(error);
                        Log.e("TaskAddActivity", "onError", error);
                    }
                });

                break;
            case R.id.layout_responsiblePerson:
                Bundle bundle = new Bundle();
                bundle.putInt(DepartmentUserActivity.STR_SELECT_TYPE, DepartmentUserActivity.TYPE_SELECT_SINGLE);
                app.startActivityForResult(this, DepartmentUserActivity.class, MainApp.ENTER_TYPE_RIGHT, DepartmentUserActivity.request_Code, bundle);
                break;
            case R.id.layout_deadline:
                DateTimePickDialog dateTimePickDialog = new DateTimePickDialog(this, null);
                dateTimePickDialog.dateTimePicKDialog(new DateTimePickDialog.OnDateTimeChangedListener() {
                    @Override
                    public void onDateTimeChanged(int year, int month, int day, int hour, int min) {
                        String str = year + "." + String.format("%02d", (month + 1)) + "." + String.format("%02d", day) + String.format(" %02d", hour) + String.format(":%02d", min);

                        tv_deadline.setText(str);
                        mDeadline = DateTool.getDateToTimestamp(str, app.df3);
                    }
                });
                break;
            case R.id.tv_toUsers:
                Bundle bundle1 = new Bundle();
                bundle1.putInt(DepartmentUserActivity.STR_SHOW_TYPE, DepartmentUserActivity.TYPE_SHOW_USER);
                bundle1.putInt(DepartmentUserActivity.STR_SELECT_TYPE, DepartmentUserActivity.TYPE_SELECT_MULTUI);
                app.startActivityForResult(this, DepartmentUserActivity.class, MainApp.ENTER_TYPE_RIGHT, DepartmentUserActivity.request_Code, bundle1);
                break;
            case R.id.layout_del:
                members.clear();
                tv_toUsers.setText("");
                layout_del.setVisibility(View.GONE);
                img_title_right_toUsers.setVisibility(View.VISIBLE);
                break;
            case R.id.layout_project:
                Bundle bundle2 = new Bundle();
                bundle2.putBoolean("isSelect", true);
                app.startActivityForResult(this, ProjectSearchActivity.class, MainApp.ENTER_TYPE_RIGHT, FinalVariables.REQUEST_SELECT_PROJECT, bundle2);
                break;
        }
    }

    @Click(R.id.layout_remind)
    void remindonClick() {
        if (dialog_Product == null) {

            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.dialog_products_select, null, false);
            AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(layout);
            dialog_Product = builder.create();
        }
        dialog_Product.show();
        ListView listView_products = (ListView) dialog_Product.findViewById(R.id.listView);

        final RemindAdapter productsRadioListViewAdapter = new RemindAdapter(this, Task.RemindList, R.layout.item_listview_product_select);
        listView_products.setAdapter(productsRadioListViewAdapter);
        listView_products.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mRemind = Task.RemindListSource.get(position);
                tv_remind.setText(Task.RemindList.get(position));
                dialog_Product.dismiss();
            }
        });
    }

    void setResponsiblePersion(User user) {
        responsiblePerson = user.toShortUser();
        tv_responsiblePerson.setText(responsiblePerson.getRealname());
    }

    void getAttachments(){
        Utils.getAttachments(uuid, new RCallback<ArrayList<Attachment>>() {
            @Override
            public void success(ArrayList<Attachment> _attachments, Response response) {
                lstData_Attachment = _attachments;
                init_gridView_photo();
            }
            @Override
            public void failure(RetrofitError error) {
                Toast("获取附件失败");
                super.failure(error);
            }
        });
    }
    void setJoinUsers(String joinedUserIds, String joinedUserName) {
        members.clear();

        String[] userIds = joinedUserIds.split(",");
        String[] userNames = joinedUserName.split(",");

        for (int i = 0; i < userIds.length; i++) {
            members.add(new Reviewer(userIds[i], userNames[i]));
        }

        if (!TextUtils.isEmpty(joinedUserName)) {
            tv_toUsers.setText(joinedUserName);
            layout_del.setVisibility(View.VISIBLE);
            img_title_right_toUsers.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case FinalVariables.REQUEST_SELECT_PROJECT:
                Project _project = (Project) data.getSerializableExtra("data");
                project=_project;
                if (null != project) {
                    tv_Project.setText(project.getTitle());
                }else {
                    tv_Project.setText("无");
                }
                break;
            case DepartmentUserActivity.request_Code:
                User user = (User) data.getSerializableExtra(User.class.getName());
                if (user != null) {
                    setResponsiblePersion(user);
                } else {
                    String cc_user_id = data.getStringExtra(DepartmentUserActivity.CC_USER_ID);
                    String cc_user_name = data.getStringExtra(DepartmentUserActivity.CC_USER_NAME);
                    setJoinUsers(cc_user_id, cc_user_name);
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
                                Utils.uploadAttachment(uuid,newFile).subscribe(new CommonSubscriber(this) {
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
                final Attachment delAttachment = (Attachment) data.getSerializableExtra("delAtm");
                app.getRestAdapter().create(IAttachment.class).remove(String.valueOf(delAttachment.getId()), new RCallback<Attachment>() {
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
                break;
        }
    }

    Task mTask;

    //isSave=true时保存临时Task,=false时删除Task临时Task
    boolean isSave = true;

    @Override
    protected void onDestroy() {
        super.onDestroy();

        DBManager.Instance().deleteTask();

        if (isSave) {
            mTask = new Task();
            mTask.setTitle(edt_title.getText().toString().trim());
            mTask.setContent(edt_content.getText().toString().trim());
            mTask.setReviewFlag(switch_approve.isChecked());

            if (responsiblePerson != null) {
                //直接保存responsiblePerson会出错
                mTask.setResponsiblePersonId(responsiblePerson.getId());
                mTask.setResponsiblePersonName(responsiblePerson.getRealname());
            }

            if (!members.isEmpty()) {
                mTask.setMembers(members);
            }

            if (mDeadline > 0) {
                mTask.setPlanEndAt(mDeadline);
            }

            mTask.setAttachments(null);
            DBManager.Instance().putTask(MainApp.gson.toJson(mTask));
        }
    }

    public class RemindAdapter extends CommonAdapter<String> {
        public RemindAdapter(Context context, List<String> datas, int layoutId) {
            super(context, datas, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, String s) {
            holder.setText(R.id.tv, s);
        }
    }
}
