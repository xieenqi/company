package com.loyo.oa.v2.activityui.other.presenter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.other.viewcontrol.BulletinAddView;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Bulletin;
import com.loyo.oa.v2.beans.Members;
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
 * Created by yyy on 16/10/9.
 */

public class BulletinAddPresenterImpl implements BulletinAddPresenter{

    private Context mContext;
    private BulletinAddView mBulletinAddView;

    private int bizType = 0;
    private int uploadSize;
    private int uploadNum;

    private ArrayList<Attachment> mAttachment = new ArrayList<>();//照片附件的数据

    public BulletinAddPresenterImpl(BulletinAddView mBulletinAddView,Context mContext){
        this.mBulletinAddView = mBulletinAddView;
        this.mContext = mContext;
    }


    @Override   /*格式验证*/
    public void verifyText(String title, String content,Members member) {
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
        mBulletinAddView.verifyPass();
    }


    @Override   /*提交通知*/
    public void requestBulletinAdd(HashMap<String,Object> map) {
        MainApp.getMainApp().getRestAdapter().create(INotice.class).publishNotice(map, new RCallback<Bulletin>() {
            @Override
            public void success(final Bulletin mBulletin, final Response response) {
                HttpErrorCheck.checkResponse("通知", response);
                if (mBulletin != null) {
                    mBulletinAddView.onSuccess(mBulletin);
                }else{
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
    public void uploadAttachement(SweetAlertDialogView sweetAlertDialogView, final ArrayList<SelectPicPopupWindow.ImageInfo> pickPhots, final HashMap<String,Object> map, final String uuid) {

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
                    LogUtil.dee("pickPhots:"+MainApp.gson.toJson(pickPhots));
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
                                                HttpErrorCheck.checkResponse("通知公告附件",response);
                                                if (attachments != null) {
                                                    mAttachment.add(attachments);
                                                }
                                                uploadSize++;
                                                if (uploadSize == uploadNum) {
                                                    requestBulletinAdd(map);
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
        },"提示",mContext.getString(R.string.app_bulletin_message));
    }
}
