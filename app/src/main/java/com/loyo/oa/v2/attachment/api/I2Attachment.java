package com.loyo.oa.v2.attachment.api;

import com.loyo.oa.upload.alioss.OssToken;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.beans.AttachmentBatch;
import com.loyo.oa.v2.beans.AttachmentForNew;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.QueryMap;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;
import rx.Observable;


public interface I2Attachment {

    //附件增加viewer
    @POST("/attachment/{id}/viewers/{userId}")
    Observable<Attachment> addViewer(@Path("id") String id, @Path("userId") String userId);

    //附件删除viewer
    @DELETE("/attachment/{id}/viewers/{userId}")
    Observable<Attachment> removeViewer(@Path("id") String id, @Path("userId") String userId);

    //设置附件公开
    @PUT("/attachment/{id}/public/{isopen}")
    Observable<Attachment> pub(@Path("id") String id, @Path("isopen") int isOpen);

    //删除附件(新加入UUID)
    @DELETE("/attachment/{id}")
    Observable<Attachment> remove(@Path("id") String id, @QueryMap HashMap<String, Object> map);

    @GET("/attachment/uuid/{uuid}")
    Observable<ArrayList<Attachment>> getAttachments(@Path("uuid") String uuid);

    /**
     * 上传附件
     * @param uuid
     * @param attachments
     */
    @Multipart
    @POST("/attachment/")
    Observable<Attachment> upload(@Part("uuid") TypedString uuid, @Part("bizType") int biz, @Part("attachments") TypedFile attachments);

    @Multipart
    @POST("/attachment/")
    Observable<Attachment> newUpload(@Part("uuid") TypedString uuid, @Part("bizType") int biz, @Part("attachments") TypedFile attachments);

    /**
     * 获取阿里云附件Token
     * */
    @POST("/attachment/sign")
    Observable<OssToken> getServerToken();

    @POST("/attachment/sign")
    OssToken syncGetServerToken();

    /**
     * 上传附件信息
     * */
    @POST("/attachment/batch")
    Observable<AttachmentForNew> setAttachementData(@Body ArrayList<AttachmentBatch> attachment);

}
