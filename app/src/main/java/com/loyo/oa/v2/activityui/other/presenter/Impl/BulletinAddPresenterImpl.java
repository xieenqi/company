package com.loyo.oa.v2.activityui.other.presenter.Impl;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.other.presenter.BulletinAddPresenter;
import com.loyo.oa.v2.activityui.other.viewcontrol.BulletinAddView;
import com.loyo.oa.v2.announcement.api.AnnouncementService;
import com.loyo.oa.v2.beans.AttachmentForNew;
import com.loyo.oa.v2.beans.Bulletin;
import com.loyo.oa.v2.beans.Members;
import com.loyo.oa.v2.beans.NewUser;
import com.loyo.oa.v2.common.DialogHelp;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.LoyoErrorChecker;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 【发布通知】Presenter
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
    // private ArrayList<Attachment> mAttachment = new ArrayList<>();//照片附件的数据

    public BulletinAddPresenterImpl(BulletinAddView mBulletinAddView, Context mContext) {
        this.mBulletinAddView = mBulletinAddView;
        this.mContext = mContext;
    }

    /** 获取Members */
    @Override
    public Members getMembers() {
        return this.member;
    }

    /**
     * 通知人员回调处理
     * */
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

    /**
     * 组装附件
     * */
    @Override
    public ArrayList<Attachment> assembleAttachment(ArrayList<AttachmentForNew> attachments) {
        ArrayList<Attachment> newAttachment = new ArrayList<Attachment>();
        for (AttachmentForNew element : attachments) {
            Attachment obj = new Attachment();
            obj.setMime(element.getMime());
            obj.setOriginalName(element.getOriginalName());
            obj.setName(element.getName());
            newAttachment.add(obj);
        }
        return newAttachment;
    }

    /**
     * 格式验证
     * */
    @Override
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

    /**
     * 提交通知
     * */
    @Override
    public void requestBulletinAdd(String title, String content, String uuid, ArrayList<AttachmentForNew> attachments) {
        DialogHelp.showStatusLoading(false,mContext);
        HashMap<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("content", content);
        map.put("attachmentUUId", uuid);
        map.put("members", member);
        if (attachments != null) {
            map.put("attachments", assembleAttachment(attachments));
        }
        AnnouncementService.publishNotice(map)
                .subscribe(new DefaultLoyoSubscriber<Bulletin>(LoyoErrorChecker.COMMIT_DIALOG) {
                    @Override
                    public void onNext(final Bulletin bulletin) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                DialogHelp.cancelStatusLoading();
                                mBulletinAddView.onSuccess(bulletin);
                            }
                        },1000);
                    }
                });
    }

    /**
     * 上传附件
     * */
//    @Override
//    public void uploadAttachement(SweetAlertDialogView sweetAlertDialogView,
//                                  final ArrayList<ImageInfo> pickPhots, final String title,
//                                  final String content, final String uuid) {
//
//        sweetAlertDialogView.alertHandle(new SweetAlertDialog.OnSweetClickListener() {
//            @Override
//            public void onClick(SweetAlertDialog sweetAlertDialog) {
//                mBulletinAddView.dissweetAlert();
//            }
//        }, new SweetAlertDialog.OnSweetClickListener() {
//            @Override
//            public void onClick(SweetAlertDialog sweetAlertDialog) {
//                mBulletinAddView.dissweetAlert();
//                mBulletinAddView.showLoading();
//                try {
//                    uploadSize = 0;
//                    uploadNum = pickPhots.size();
//                    LogUtil.dee("pickPhots:" + MainApp.gson.toJson(pickPhots));
//                    for (ImageInfo item : pickPhots) {
//                        Uri uri = Uri.parse(item.path);
//                        File newFile = Global.scal(mContext, uri);
//                        if (newFile != null && newFile.length() > 0) {
//                            if (newFile.exists()) {
//                                TypedFile typedFile = new TypedFile("image/*", newFile);
//                                TypedString typedUuid = new TypedString(uuid);
//                                RestAdapterFactory.getInstance().build(Config_project.API_URL_ATTACHMENT()).create(IAttachment.class).newUpload(typedUuid, bizType, typedFile,
//                                        new RCallback<Attachment>() {
//                                            @Override
//                                            public void success(final Attachment attachments, final Response response) {
//                                                HttpErrorCheck.checkResponse("通知公告附件", response);
//                                                if (attachments != null) {
//                                                    mAttachment.add(attachments);
//                                                }
//                                                uploadSize++;
//                                                if (uploadSize == uploadNum) {
//                                                    requestBulletinAdd(title, content, uuid);
//                                                }
//                                            }
//
//                                            @Override
//                                            public void failure(final RetrofitError error) {
//                                                super.failure(error);
//                                                HttpErrorCheck.checkError(error);
//                                                mBulletinAddView.onError();
//                                            }
//                                        });
//                            }
//                        }
//                    }
//                } catch (Exception ex) {
//                    Global.ProcException(ex);
//                }
//            }
//        }, "提示", mContext.getString(R.string.app_bulletin_message));
//    }
}
