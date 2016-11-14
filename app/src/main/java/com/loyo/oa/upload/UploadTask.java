package com.loyo.oa.upload;

import android.support.annotation.IntDef;
import android.text.TextUtils;

import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.security.MessageDigest;

/**
 * Created by EthanGong on 16/10/9.
 */

public class UploadTask {

    public static final int WAITING = 0;
    public static final int QUEUED = 1;
    public static final int PROCESSING = 2;
    public static final int UPLOADING = 3;
    public static final int UPLOADED = 4;
    public static final int FAILED = 5;
    public static final int CANCEL = 6;

    @IntDef({WAITING, QUEUED, PROCESSING, UPLOADING, UPLOADED, FAILED, CANCEL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface UploadTaskStatus {
    }

    @UploadTaskStatus
    int status = WAITING;
    double progress;
    String md5;
    String UUID;
    String filePath;
    String originPath;
    public String name;
    public long size;
    WeakReference<OSSAsyncTask> taskRef;

    public UploadTask() {
    }

    public UploadTask(String filePath, String UUID) {
        this.filePath = filePath;
        this.UUID = UUID;
        this.md5 = getMD5(filePath);
    }

    public UploadTask(String originPath, String filePath, String UUID) {
        this.originPath = originPath;
        this.filePath = filePath;
        this.UUID = UUID;
        this.md5 = getMD5(filePath != null ? filePath : originPath);
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public boolean isSameUploading(UploadTask task) {
        return TextUtils.equals(this.filePath, task.filePath)
                && TextUtils.equals(this.UUID, task.UUID);
    }

    public String getValidatePath() {
        return filePath != null ? filePath : originPath;
    }

    public String getKey() {
        if (!TextUtils.isEmpty(name)) {
            return UUID + "/" + name;
        }

        String path = getValidatePath();
        String extension = path.substring(path.lastIndexOf("."));
        if (extension == null) {
            extension = "";
        }

        return UUID + "/" + md5 + "." + extension;
    }

    public int getStatus() {
        return status;
    }

    public double getProgress() {
        return progress;
    }

    private String getMD5(String val) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            return null;
        }
        md5.update(val.getBytes());
        byte[] m = md5.digest();
        return getString(m);
    }

    private String getString(byte[] b) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            sb.append(b[i]);
        }
        return sb.toString();
    }

}
