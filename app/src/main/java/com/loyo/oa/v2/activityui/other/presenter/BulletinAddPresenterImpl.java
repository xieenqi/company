package com.loyo.oa.v2.activityui.other.presenter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.other.viewcontrol.BulletinAddView;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Bulletin;
import com.loyo.oa.v2.beans.Members;
import com.loyo.oa.v2.beans.NewUser;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.SweetAlertDialogView;
import com.loyo.oa.v2.point.IAttachment;
import com.loyo.oa.v2.point.INotice;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.SelectPicPopupWindow;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

/**
 * Created by yyy on 16/10/9
 */

public class BulletinAddPresenterImpl implements BulletinAddPresenter {

    private int bizType = 0;
    private int uploadSize;
    private int uploadNum;
    private StringBuffer joinUserId, joinName;

    private Context mContext;
    private Members member = new Members();
    private BulletinAddView mBulletinAddView;
    private ArrayList<Attachment> mAttachment = new ArrayList<>();//照片附件的数据

    public BulletinAddPresenterImpl(BulletinAddView mBulletinAddView, Context mContext) {
        this.mBulletinAddView = mBulletinAddView;
        this.mContext = mContext;
    }

    @Override
    public void dealDepartmentResult(Members member) {
        this.member = member;
        joinName = new StringBuffer();
        joinUserId = new StringBuffer();
        if (member.users.size() == 0 && member.depts.size() == 0) {
            mBulletinAddView.setReceiver("没有选择人员");
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
            mBulletinAddView.setReceiver(joinName.toString());
        }
    }

    @Override  /*组装附件*/
    public ArrayList<Attachment> assembleAttachment() {
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

    @Override   /*格式验证*/
    public void verifyText(String title, String content) {
        if (TextUtils.isEmpty(title)) {
            mBulletinAddView.verifyError(1);
            return;
        } else if (TextUtils.isEmpty(content)) {
            mBulletinAddView.verifyError(2);
            return;
        } else if (member.users.size() == 0 && member.depts.size() == 0) {
            mBulletinAddView.verifyError(3);
            return;
        }
        mBulletinAddView.verifySuccess(title, content);
    }


    @Override   /*提交通知*/
    public void requestBulletinAdd(String title, String content, String uuid) {

        HashMap<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("content", content);
        map.put("attachmentUUId", uuid);
        map.put("members", member);
        map.put("attachments", assembleAttachment());

        MainApp.getMainApp().getRestAdapter().create(INotice.class).publishNotice(map, new RCallback<Bulletin>() {
            @Override
            public void success(final Bulletin mBulletin, final Response response) {
                HttpErrorCheck.checkResponse("通知", response);
                if (mBulletin != null) {
                    mBulletinAddView.onSuccess(mBulletin);
                } else {
                    mBulletinAddView.onError();
                }
            }

            @Override
            public void failure(final RetrofitError error) {
                HttpErrorCheck.checkError(error);
                mBulletinAddView.onError();
                super.failure(error);
            }
        });
    }


    @Override   /*上传附件*/
    public void uploadAttachement(SweetAlertDialogView sweetAlertDialogView,
                                  final ArrayList<SelectPicPopupWindow.ImageInfo> pickPhots, final String title,
                                  final String content, final String uuid) {

        sweetAlertDialogView.alertHandle(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                mBulletinAddView.dissweetAlert();
            }
        }, new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                mBulletinAddView.dissweetAlert();
                mBulletinAddView.showLoading();
                try {
                    uploadSize = 0;
                    uploadNum = pickPhots.size();
                    LogUtil.dee("pickPhots:" + MainApp.gson.toJson(pickPhots));
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
                                                HttpErrorCheck.checkResponse("通知公告附件", response);
                                                if (attachments != null) {
                                                    mAttachment.add(attachments);
                                                }
                                                uploadSize++;
                                                if (uploadSize == uploadNum) {
                                                    requestBulletinAdd(title, content, uuid);
                                                }
                                            }

                                            @Override
                                            public void failure(final RetrofitError error) {
                                                super.failure(error);
                                                HttpErrorCheck.checkError(error);
                                                mBulletinAddView.onError();
                                            }
                                        });
                            }
                        }
                    }
                } catch (Exception ex) {
                    Global.ProcException(ex);
                }
            }
        }, "提示", mContext.getString(R.string.app_bulletin_message));
    }
}

