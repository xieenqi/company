package com.loyo.oa.v2.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.adapter.SignInGridViewAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Attachment;
import com.loyo.oa.v2.beans.Bulletin;
import com.loyo.oa.v2.beans.Members;
import com.loyo.oa.v2.beans.NewUser;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.IAttachment;
import com.loyo.oa.v2.point.INotice;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.CommonSubscriber;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.SelectPicPopupWindow;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.tool.customview.GeneralPopView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

@EActivity(R.layout.activity_bulletin_add)
public class BulletinAddActivity extends BaseActivity {

    @ViewById EditText edt_title;
    @ViewById EditText edt_content;
    @ViewById GridView gridView_photo;
    @ViewById ViewGroup layout_recevier;
    @ViewById TextView tv_recevier;

    String mUuid = StringUtil.getUUID();
    String cc_user_id, cc_department_id, cc_user_name, cc_department_name;
    SignInGridViewAdapter mGridViewAdapter;
    ArrayList<Attachment> mAttachment = new ArrayList<>();//照片附件的数据
    private ArrayList<NewUser> userss = new ArrayList<>();
    private ArrayList<NewUser> depts = new ArrayList<>();
    private Members member = new Members();

    @AfterViews
    void init() {
        super.setTitle("发布通知");
        init_gridView_photo();
    }

    /**
     * 添加 图片 附件
     */
    void init_gridView_photo() {
        mGridViewAdapter = new SignInGridViewAdapter(this, mAttachment, true, true, true, 0);
        SignInGridViewAdapter.setAdapter(gridView_photo, mGridViewAdapter);
    }

    /**
     * 通知谁看
     */
    @Click(R.id.layout_recevier)
    void receiverClick() {
        app.startActivityForResult(this, DepartmentUserActivity.class, MainApp.ENTER_TYPE_RIGHT, DepartmentUserActivity.request_Code, null);
    }

    @Click(R.id.img_title_left)
    void close() {
        //onBackPressed();
        finish();
    }

    @Click(R.id.img_title_right)
    void submit() {
        final String title = edt_title.getText().toString().trim();
        final String content = edt_content.getText().toString().trim();
        if (TextUtils.isEmpty(title)) {
            Global.ToastLong("标题不能为空");
            return;
        } else if (TextUtils.isEmpty(content)) {
            Global.ToastLong("内容不能为空");
            return;
        } else if (TextUtils.isEmpty(cc_user_id) && TextUtils.isEmpty(cc_department_id)) {
            Global.ToastLong("通知人员不能为空");
            return;
        }


        final GeneralPopView generalPopView = new GeneralPopView(this,getString(R.string.app_bulletin_message),true);
        generalPopView.show();
        generalPopView.setCanceledOnTouchOutside(true);
        //确认
        generalPopView.setSureOnclick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                generalPopView.dismiss();
                HashMap<String, Object> map = new HashMap<>();
                map.put("title", title);
                map.put("content", content);
                map.put("attachmentUUId", mUuid);
                map.put("members", member);
                map.put("attachments", newData());
                LogUtil.d(" 通知 传递数据： " + MainApp.gson.toJson(map));
                app.getRestAdapter().create(INotice.class).publishNotice(map, new RCallback<Bulletin>() {
                    @Override
                    public void success(Bulletin bulletin, Response response) {
                        HttpErrorCheck.checkResponse("add通知", response);
                        if (bulletin != null) {
                            if (mAttachment != null) {
                                bulletin.attachmentUUId = mUuid;
                                bulletin.attachments = mAttachment;
                            }
                            Intent intent = new Intent();
                            intent.putExtra("data", bulletin);
                            setResult(RESULT_OK, intent);
                        }
                        finish();
                        // onBackPressed();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                        super.failure(error);
                    }
                });

            }
        });
        //取消
        generalPopView.setCancelOnclick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generalPopView.dismiss();
            }
        });
    }

    /**
     * 获取附件
     */
    private void getAttachments() {
        Utils.getAttachments(mUuid, new RCallback<ArrayList<Attachment>>() {
            @Override
            public void success(ArrayList<Attachment> attachments, Response response) {
                mAttachment = attachments;
                init_gridView_photo();
            }

            @Override
            public void failure(RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
                Toast("获取附件失败");
            }
        });
    }

    @OnActivityResult(SelectPicPopupWindow.GET_IMG)
    void onPhotoResult(Intent data) {
        try {
            ArrayList<SelectPicPopupWindow.ImageInfo> pickPhots = (ArrayList<SelectPicPopupWindow.ImageInfo>) data.getSerializableExtra("data");
            for (SelectPicPopupWindow.ImageInfo item : pickPhots) {
                Uri uri = Uri.parse(item.path);
                File newFile = Global.scal(this, uri);
                if (newFile != null && newFile.length() > 0) {
                    if (newFile.exists()) {
                        Utils.uploadAttachment(mUuid,0,newFile).subscribe(new CommonSubscriber(this) {
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
    }

    @OnActivityResult(FinalVariables.REQUEST_DEAL_ATTACHMENT)
    void onDeletePhotoResult(int resultCode, Intent data) {
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        final Attachment delAttachment = (Attachment) data.getSerializableExtra("delAtm");
        LogUtil.d("删除附件id " + delAttachment.getId());
        RestAdapterFactory.getInstance().build(Config_project.API_URL_ATTACHMENT()).create(IAttachment.class).remove(delAttachment.getId(), new RCallback<Attachment>() {
            @Override
            public void success(Attachment attachment, Response response) {
                Toast("删除附件成功!");
                mAttachment.remove(delAttachment);
                mGridViewAdapter.setDataSource(mAttachment);
                mGridViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast("删除附件失败!");
                super.failure(error);
            }
        });
    }

    @OnActivityResult(DepartmentUserActivity.request_Code)
    void onDepartmentUserResult(int resultCode, Intent data) {
        if (resultCode != RESULT_OK || data == null) {
            return;
        }

        cc_department_id = data.getStringExtra(DepartmentUserActivity.CC_DEPARTMENT_ID);
        cc_department_name = data.getStringExtra(DepartmentUserActivity.CC_DEPARTMENT_NAME);
        cc_user_id = data.getStringExtra(DepartmentUserActivity.CC_USER_ID);
        cc_user_name = data.getStringExtra(DepartmentUserActivity.CC_USER_NAME);
        String cc = null;
        if (cc_department_name != null && cc_user_name != null) {
            cc = cc_department_name + "," + cc_user_name;
        } else if (cc_department_name != null) {
            cc = cc_department_name;
        } else if (cc_user_name != null) {
            cc = cc_user_name;
        }

        if (cc != null) {
            tv_recevier.setText(cc);
        }

        setJoinUsers(cc_user_id, cc_user_name, cc_department_id, cc_department_name);
    }


    private void setJoinUsers(String joinedUserIds, String joinedUserName, String departIds, String departName) {
        userss.clear();
        depts.clear();
        if (!TextUtils.isEmpty(joinedUserIds) && !TextUtils.isEmpty(joinedUserName)) {
            String[] userIds = joinedUserIds.split(",");
            String[] userNames = joinedUserName.split(",");
            for (int i = 0; i < userIds.length; i++) {
                NewUser newUser = new NewUser();
                newUser.setName(userNames[i]);
                newUser.setId(userIds[i]);
                userss.add(newUser);
            }
            if (userss != null && userss.size() > 0) {
                member.users = userss;
            }
        }
        if (!TextUtils.isEmpty(departIds) && !TextUtils.isEmpty(departName)) {
            String[] dpIds = departIds.split(",");
            String[] dpNames = departName.split(",");
            for (int i = 0; i < dpIds.length; i++) {
                NewUser newUser = new NewUser();
                newUser.setName(dpNames[i]);
                newUser.setId(dpIds[i]);
                depts.add(newUser);
            }
            if (depts != null && depts.size() > 0) {
                member.depts = depts;
            }
        }
    }

    /**
     * 过滤 图片数据、
     */
    private ArrayList<Attachment> newData() {
        ArrayList<Attachment> newAttachment = new ArrayList<Attachment>();
        for (Attachment element : mAttachment) {
            Attachment obj = new Attachment();
            obj.setMime(element.getMime());
            obj.setOriginalName(element.getOriginalName());
            obj.setName(element.getName());
            newAttachment.add(obj);
        }
        return newAttachment;
    }
}
