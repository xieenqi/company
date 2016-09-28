package com.loyo.oa.v2.point;

import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.commonview.bean.OssToken;
import com.loyo.oa.v2.beans.AttachmentBatch;
import com.loyo.oa.v2.beans.AttachmentForNew;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
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


public interface IAttachment {

    //附件增加viewer
    @POST("/attachment/{id}/viewers/{userId}")
    void addViewer(@Path("id") String id, @Path("userId") String userId, retrofit.Callback<Attachment> cb);

    //附件删除viewer
    @DELETE("/attachment/{id}/viewers/{userId}")
    void removeViewer(@Path("id") String id, @Path("userId") String userId, retrofit.Callback<Attachment> cb);

    //设置附件公开
    @PUT("/attachment/{id}/public/{isopen}")
    void pub(@Path("id") String id,@Path("isopen") int isOpen, retrofit.Callback<Attachment> cb);

    //删除附件(新加入UUID)
    @DELETE("/attachment/{id}")
    void remove(@Path("id") String id,@QueryMap HashMap<String,Object> map, retrofit.Callback<Attachment> cb);

    @GET("/attachment/uuid/{uuid}")
    void getAttachments(@Path("uuid") String uuid, Callback<ArrayList<Attachment>> cb);

    /**
     * 上传附件
     * @param uuid
     * @param attachments
     */
    @Multipart
    @POST("/attachment/")
    Observable<Attachment> upload(@Part("uuid") TypedString uuid,@Part("bizType")int biz,@Part("attachments")TypedFile attachments);

    @Multipart
    @POST("/attachment/")
    void newUpload(@Part("uuid") TypedString uuid,@Part("bizType")int biz,@Part("attachments")TypedFile attachments,Callback<Attachment> callback);

    /**
     * 获取阿里云附件Token
     * */
    @POST("/attachment/sign")
    void getServerToken(Callback<OssToken> callback);

    @POST("/attachment/sign")
    OssToken syncGetServerToken();

    /**
     * 上传附件信息
     * */
    @POST("/attachment/batch")
    void setAttachementData(@Body ArrayList<AttachmentBatch> attachment,Callback<ArrayList<AttachmentForNew>> callback);

}
