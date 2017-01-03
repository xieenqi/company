package com.loyo.oa.v2.attachment.api;

import com.loyo.oa.upload.alioss.OssToken;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.beans.AttachmentBatch;
import com.loyo.oa.v2.beans.AttachmentForNew;
import com.loyo.oa.v2.network.RetrofitAdapterFactory;
import com.loyo.oa.v2.tool.Config_project;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;
import rx.Observable;

/**
 * Created by EthanGong on 2016/12/20.
 */

public class AttachmentService {
    public static Observable<OssToken> getServerToken() {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_ATTACHMENT())
                        .create(IAttachment.class)
                        .getServerToken()
                        .compose(RetrofitAdapterFactory.<OssToken>compatApplySchedulers());
    }

    public static OssToken syncGetServerToken() {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_ATTACHMENT())
                        .create(IAttachment.class)
                        .syncGetServerToken();
    }

    public static Observable<ArrayList<Attachment>> getAttachments(String uuid) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_ATTACHMENT())
                        .create(IAttachment.class)
                        .getAttachments(uuid)
                        .compose(RetrofitAdapterFactory.<ArrayList<Attachment>>compatApplySchedulers());
    }

    public static Observable<Attachment> newUpload(TypedString uuid, int biz, TypedFile attachments) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_ATTACHMENT())
                        .create(IAttachment.class)
                        .newUpload(uuid, biz, attachments)
                        .compose(RetrofitAdapterFactory.<Attachment>compatApplySchedulers());
    }

    public static Observable<Attachment> pub(String id, int isOpen) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_ATTACHMENT())
                        .create(IAttachment.class)
                        .pub(id, isOpen)
                        .compose(RetrofitAdapterFactory.<Attachment>compatApplySchedulers());
    }

    public static Observable<Attachment> removeViewer(String id, String userId) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_ATTACHMENT())
                        .create(IAttachment.class)
                        .removeViewer(id, userId)
                        .compose(RetrofitAdapterFactory.<Attachment>compatApplySchedulers());
    }

    public static Observable<Attachment> addViewer(String id, String userId) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_ATTACHMENT())
                        .create(IAttachment.class)
                        .addViewer(id, userId)
                        .compose(RetrofitAdapterFactory.<Attachment>compatApplySchedulers());
    }

    public static Observable<Attachment> remove(String id, HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_ATTACHMENT())
                        .create(IAttachment.class)
                        .remove(id, map)
                        .compose(RetrofitAdapterFactory.<Attachment>compatApplySchedulers());
    }

    public static Observable<ArrayList<AttachmentForNew>> setAttachementData(ArrayList<AttachmentBatch> attachment) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_ATTACHMENT())
                        .create(IAttachment.class)
                        .setAttachementData(attachment)
                        .compose(RetrofitAdapterFactory.<ArrayList<AttachmentForNew>>compatApplySchedulers());
    }

    public static Observable<ArrayList<Attachment>> setAttachementData2(ArrayList<AttachmentBatch> attachment) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_ATTACHMENT())
                        .create(IAttachment.class)
                        .setAttachementData2(attachment)
                        .compose(RetrofitAdapterFactory.<ArrayList<Attachment>>compatApplySchedulers());
    }
}
