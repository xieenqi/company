package com.loyo.oa.v2.activityui.other;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.commonview.SelectDetUserActivity2;
import com.loyo.oa.v2.activityui.other.adapter.ImageGridViewAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.beans.Bulletin;
import com.loyo.oa.v2.beans.Members;
import com.loyo.oa.v2.beans.NewUser;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.IAttachment;
import com.loyo.oa.v2.point.INotice;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.SelectPicPopupWindow;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.customview.CusGridView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

/**
 * 【通知公告】发布页面
 */

@EActivity(R.layout.activity_bulletin_add)
public class BulletinAddActivity extends BaseActivity {

    @ViewById
    EditText edt_title;
    @ViewById
    EditText edt_content;
    @ViewById
    CusGridView gridView_photo;
    @ViewById
    ViewGroup layout_recevier;
    @ViewById
    TextView tv_recevier;

    private int bizType = 0;
    private int uploadSize;
    private int uploadNum;

    private Context mContext;
    private String uuid = StringUtil.getUUID();
    private ImageGridViewAdapter mGridViewAdapter;
    private ArrayList<Attachment> mAttachment = new ArrayList<>();//照片附件的数据
    private ArrayList<SelectPicPopupWindow.ImageInfo> pickPhots = new ArrayList<>();
    private Members member = new Members();
    private StringBuffer joinUserId, joinName;

    private String title;
    private String content;

    @AfterViews
    void init() {
        super.setTitle("发布通知");
        mContext = this;
        init_gridView_photo();
    }

    /**
     * 添加 图片 附件
     */
    void init_gridView_photo() {
        mGridViewAdapter = new ImageGridViewAdapter(this, true, true, 0, pickPhots);
        ImageGridViewAdapter.setAdapter(gridView_photo, mGridViewAdapter);
    }

    /**
     * 通知谁看
     */
    @Click(R.id.layout_recevier)
    void receiverClick() {
        SelectDetUserActivity2.startThisForAllSelect(BulletinAddActivity.this, joinUserId == null ? null : joinUserId.toString(), true);
    }

    @Click(R.id.img_title_left)
    void close() {
        onBackPressed();
    }

    @Click(R.id.img_title_right)
    void submit() {
        title = edt_title.getText().toString().trim();
        content = edt_content.getText().toString().trim();
        if (TextUtils.isEmpty(title)) {
            Global.ToastLong("标题不能为空");
            return;
        } else if (TextUtils.isEmpty(content)) {
            Global.ToastLong("内容不能为空");
            return;
        } else if (member.users.size() == 0 && member.depts.size() == 0) {
            Global.ToastLong("通知人员不能为空");
            return;
        }

        //没有附件
        if (pickPhots.size() == 0) {
            requestCommitTask();
            //有附件
        } else {
            newUploadAttachement();
        }
    }

    /**
     * 批量上传附件
     */
    private void newUploadAttachement() {
        showGeneralDialog(true, true, getString(R.string.app_bulletin_message));
        generalPopView.setSureOnclick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generalPopView.dismiss();
                showLoading("正在提交");
                try {
                    uploadSize = 0;
                    uploadNum = pickPhots.size();
                    for (SelectPicPopupWindow.ImageInfo item : pickPhots) {
                        Uri uri = Uri.parse(item.path);
                        File newFile = Global.scal(mContext, uri);
                        if (newFile != null && newFile.length() > 0) {
                            if (newFile.exists()) {
                                TypedFile typedFile = new TypedFile("image/*", newFile);
                                TypedString typedUuid = new TypedString(uuid);
                                RestAdapterFactory.getInstance().build(Config_project.API_URL_ATTACHMENT()).create(IAttachment.class).newUpload(typedUuid, bizType, typedFile,
                                        new RCallback<Attachment>() {
                                            @Override
                                            public void success(final Attachment attachments, final Response response) {
                                                if (attachments != null) {
                                                    mAttachment.add(attachments);
                                                }
                                                uploadSize++;
                                                if (uploadSize == uploadNum) {
                                                    requestCommitTask();
                                                }
                                            }

                                            @Override
                                            public void failure(final RetrofitError error) {
                                                super.failure(error);
                                                HttpErrorCheck.checkError(error);
                                            }
                                        });
                            }
                        }
                    }
                } catch (Exception ex) {
                    Global.ProcException(ex);
                }
            }
        });

        generalPopView.setCancelOnclick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generalPopView.dismiss();
            }
        });
    }


    public void requestCommitTask() {
        if (pickPhots.size() == 0) {
            showLoading("正在提交");
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("content", content);
        map.put("attachmentUUId", uuid);
        map.put("members", member);
        map.put("attachments", newData());
        LogUtil.d(" 通知 传递数据： " + MainApp.gson.toJson(map));
        app.getRestAdapter().create(INotice.class).publishNotice(map, new RCallback<Bulletin>() {
            @Override
            public void success(final Bulletin bulletin, final Response response) {
                HttpErrorCheck.checkResponse("add通知", response);
                if (bulletin != null) {
                    /*if (mAttachment != null) {
                        bulletin.attachmentUUId = uuid;
                        bulletin.attachments = mAttachment;
                    }*/
                    Intent intent = new Intent();
                    intent.putExtra("data", bulletin);
                    setResult(RESULT_OK, intent);
                }
                finish();
            }

            @Override
            public void failure(final RetrofitError error) {
                HttpErrorCheck.checkError(error);
                super.failure(error);
            }
        });
    }


    @OnActivityResult(SelectPicPopupWindow.GET_IMG)
    void onPhotoResult(final Intent data) {
        if (null != data) {
            pickPhots.addAll((ArrayList<SelectPicPopupWindow.ImageInfo>) data.getSerializableExtra("data"));
            init_gridView_photo();
        }
    }


    @OnActivityResult(FinalVariables.REQUEST_DEAL_ATTACHMENT)
    void onDeletePhotoResult(final int resultCode, final Intent data) {

    }

    @OnActivityResult(SelectDetUserActivity2.REQUEST_ALL_SELECT)
    void onDepartmentUserResult(final int resultCode, final Intent data) {
        if (resultCode != RESULT_OK || data == null) {
            return;
        }

        member = (Members) data.getSerializableExtra("data");
        joinName = new StringBuffer();
        joinUserId = new StringBuffer();
        if (member.users.size() == 0 && member.depts.size() == 0) {
            tv_recevier.setText("没有选择人员");
            joinUserId.reverse();
        } else {
            if (null != member.depts) {
                for (NewUser newUser : member.depts) {
                    joinName.append(newUser.getName() + ",");
                    joinUserId.append(newUser.getId() + ",");
                }
            }
            if (null != member.users) {
                for (NewUser newUser : member.users) {
                    joinName.append(newUser.getName() + ",");
                    joinUserId.append(newUser.getId() + ",");
                }
            }
            if (!TextUtils.isEmpty(joinName)) {
                joinName.deleteCharAt(joinName.length() - 1);
            }
            tv_recevier.setText(joinName.toString());
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