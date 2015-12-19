package com.loyo.oa.v2.activity.tasks;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.loyo.oa.v2.activity.DepartmentUserActivity;
import com.loyo.oa.v2.activity.ProjectSearchActivity;
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
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.tool.customview.DateTimePickDialog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.apache.http.Header;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

@EActivity(R.layout.activity_tasks_edit) //本Activity的布局文件
public class TasksEditActivity extends BaseActivity {

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
    @Extra Task mTask;
    SignInGridViewAdapter signInGridViewAdapter;
    AlertDialog dialog_Product;

    @AfterViews //类似onCreate方法执行入口
    void initUI() {

        super.setTitle("编辑任务");
        Global.SetTouchView(img_title_left,
                img_title_right,
                layout_responsiblePerson,
                layout_deadline,
                layout_del,
                layout_remind,layout_project);

        UpdateUI();
        init_gridView_photo();
        setTouchView(-1);

    }

    void UpdateUI() {

        if(null!=mTask.getResponsiblePerson()) {
            tv_responsiblePerson.setText(mTask.getResponsiblePerson().getName());
        }

        tv_toUsers.setText(NewUser.GetNewUserNames(mTask.getJoinedUsers()));
        tv_deadline.setText(app.df3.format(new Date(mTask.getPlanEndAt())));
        tv_remind.setText(Task.GetRemindText(mTask.getRemindTime()));
        switch_approve.setChecked(mTask.isReviewFlag());
        edt_content.setText(mTask.getContent());
        edt_title.setText(mTask.getTitle());

    }

    void getAttachments(){
        Utils.getAttachments(mTask.getAttachmentUUId(), new RCallback<ArrayList<Attachment>>() {
            @Override
            public void success(ArrayList<Attachment> _attachments, Response response) {
                mTask.setAttachments(_attachments);
                init_gridView_photo();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast("获取附件失败");
                super.failure(error);
            }
        });
    }

    void init_gridView_photo() {
        signInGridViewAdapter = new SignInGridViewAdapter(this, mTask.getAttachments(), true, true);
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

                if (mTask.getPlanEndAt() <= 0) {
                    Toast("截止日期" + getString(R.string.app_no_null));
                    break;
                }

                if (mTask.getResponsiblePerson() == null || TextUtils.isEmpty(mTask.getResponsiblePerson().getId())) {
                    Toast("负责人" + getString(R.string.app_no_null));
                    break;
                }

                HashMap<String, Object> map = new HashMap<>();
                map.put("title", title);
                map.put("content", content);
                map.put("responsiblePersons", Arrays.asList(new Reviewer(mTask.getResponsiblePerson())));
                map.put("members", mTask.getMembers());
                map.put("planendAt", mTask.getPlanEndAt());
                map.put("remindflag", mTask.getRemindTime() > 0);
                map.put("remindtime", mTask.getRemindTime());
                map.put("reworkflag", switch_approve.isChecked());

                if (!TextUtils.isEmpty(mTask.getAttachmentUUId()) && mTask.getAttachments().size() > 0) {
                    map.put("attachmentUUId", mTask.getAttachmentUUId());
                }

                if (!TextUtils.isEmpty(mTask.getProjectId())) {
                    map.put("projectId", mTask.getProjectId());
                }

                RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(ITask.class).update(mTask.getId(), map)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<Task>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                            }

                            @Override
                            public void onNext(Task task) {
                                task.setAck(true);
                                Toast(getString(R.string.app_add) + getString(R.string.app_succeed));

                                Intent intent = new Intent();
                                intent.putExtra("data", task);
                                setResult(Activity.RESULT_OK, intent);
                                onBackPressed();
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
                        mTask.setPlanEndAt(DateTool.getDateToTimestamp(str, app.df3));
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
                mTask.getMembers().getUsers().clear();
                mTask.getMembers().getDepts().clear();
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

                mTask.setRemindTime(Task.RemindListSource.get(position));
                tv_remind.setText(Task.RemindList.get(position));
                dialog_Product.dismiss();
            }
        });
    }

    void setResponsiblePersion(User user) {
        NewUser responsiblePerson = user.toShortUser();
        mTask.setResponsiblePerson(responsiblePerson);
        tv_responsiblePerson.setText(responsiblePerson.getRealname());
    }

    void setJoinUsers(String joinedUserIds, String joinedUserName) {
        mTask.getMembers().getUsers().clear();
        mTask.getMembers().getDepts().clear();

        String[] userIds = joinedUserIds.split(",");
        String[] userNames = joinedUserName.split(",");

        for (int i = 0; i < userIds.length; i++) {
            //mTask.getMembers().add(new Reviewer(userIds[i], userNames[i]));
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
                Project project = (Project) data.getSerializableExtra("data");
                if (null != project) {
                    tv_Project.setText(project.getTitle());
                    mTask.setProjectId(project.getId());
                }else {
                    tv_Project.setText("无");
                    mTask.setProjectId("");
                }
                break;
            case DepartmentUserActivity.request_Code:
                User user = (User) data.getSerializableExtra(User.class.getName());
                if (user != null) {
                    setResponsiblePersion(user);
                } else {
                    String cc_user_id = data.getStringExtra(DepartmentUserActivity.CC_USER_ID);
                    String cc_user_name = data.getStringExtra(DepartmentUserActivity.CC_USER_NAME);
                    if(cc_user_id != null && cc_user_name !=null){
                        setJoinUsers(cc_user_id, cc_user_name);
                    }else{
                        Toast("未选择相关人员");
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
                                Utils.uploadAttachment(mTask.getAttachmentUUId(),newFile).subscribe(new CommonSubscriber(this) {
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
                        mTask.getAttachments().remove(delAttachment);
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

    public class AsyncHandler_Upload_New_Attachments extends BaseActivityAsyncHttpResponseHandler {
        File file;

        public void setBitmap(File imageFile) {
            file = imageFile;
        }

        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
            try {
                Attachment attachment = MainApp.gson.fromJson(getStr(arg2), Attachment.class);
                attachment.saveFile(file);
                mTask.getAttachments().add(0, attachment);

                init_gridView_photo();
            } catch (Exception e) {
                Global.ProcException(e);
            }
        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            Toast("上传附件失败！");
            super.onFailure(i, headers, bytes, throwable);
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
