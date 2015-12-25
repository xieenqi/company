package com.loyo.oa.v2.point;

import com.loyo.oa.v2.beans.Attachment;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.Path;
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

    //删除附件
    @DELETE("/attachment/{id}")
    void remove(@Path("id") String id, retrofit.Callback<Attachment> cb);

    @GET("/attachment/uuid/{uuid}")
    void getAttachments(@Path("uuid") String uuid, Callback<ArrayList<Attachment>> cb);

    @GET("/attachment/{uuid}/count")
    void getAttachementsCount(@Path("uuid") String uuid,Callback<Attachment> callback);

    /**
     * 获取企业附件大小
     * @param companyId
     * @param callback
     */
    @GET("/attachment/{companyId}/sumsize")
    void getCompanyAttachmentSize(@Path("companyId") String companyId,Callback<Attachment> callback);

    /**
     * 上传附件
     * @param uuid
     * @param attachments
     */
    @POST("/attachment/")
    @Multipart
    Observable<Attachment> upload(@Part("uuid") TypedString uuid,@Part("attachments")TypedFile attachments);


    Observable<Attachment> upload1(@Path("uuid") String uuid,@Part("sdf")TypedFile file);


}
