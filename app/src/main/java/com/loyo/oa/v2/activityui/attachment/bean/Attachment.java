package com.loyo.oa.v2.activityui.attachment.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.NewUser;
import com.loyo.oa.v2.activityui.other.model.User;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.db.DBManager;
import com.loyo.oa.v2.tool.DateTool;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

@DatabaseTable(tableName = "Attachment")
public class Attachment implements Serializable {

    public enum AttachmentType {
        IMAGE,
        OFFICE,
        OTHER
    }

    @DatabaseField
    public String id;

    User creator;

    @DatabaseField
    public String originalName;

    @DatabaseField
    public String name;

    @DatabaseField
    public String url;

    @DatabaseField
    public String localPath;

    public String size;
    public String humanizeSize;
    public String mime;
    public String createdAt;
    public String updatedAt;
    public File file;
    public Boolean isPublic;
    public ArrayList<NewUser> viewers = new ArrayList<>();

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public Boolean isPublic() {
        return isPublic;
    }

    public void SetIsPublic(Boolean b) {
        isPublic = b;
    }

    public ArrayList<NewUser> getViewers() {
        if (null == viewers)
            setViewers(new ArrayList<NewUser>());
        return viewers;
    }

    public void setViewers(ArrayList<NewUser> users) {
        viewers = users;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public File getFile() {
        if (file == null) {
            //从本地获取文件
            Attachment t = DBManager.Instance().getAttachment(this);
            if (t != null) {
                file = t.getFile();
            }
        }

        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void saveFile(File file) {
        this.file = file;
        DBManager.Instance().saveAttachment(this);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getHumanizeSize() {
        return humanizeSize;
    }

    public void setHumanizeSize(String humanizeSize) {
        this.humanizeSize = humanizeSize;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (null == o || getClass() != o.getClass())
            return false;

        if (this == o) {
            return true;
        }

        Attachment attachment = (Attachment) o;
        return this.url.equals(attachment.url);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public AttachmentType getAttachmentType() {
        String fileExt = null;
        String filename = getOriginalName();

        int dot = filename.lastIndexOf(".");
        if ((dot > -1) && (dot < (filename.length() - 1))) {
            fileExt = filename.substring(dot + 1).toLowerCase();
        } else {
            return AttachmentType.OTHER;
        }
        fileExt = fileExt.toLowerCase();
        if (fileExt.endsWith("jpg") || fileExt.endsWith("bmp") ||
                fileExt.endsWith("gif") || fileExt.endsWith("png") ||
                fileExt.endsWith("jpeg") || fileExt.endsWith("PNG")) {
            return AttachmentType.IMAGE;
        }

        if (Global.IsOffice(filename))
            return AttachmentType.OFFICE;

        return AttachmentType.OTHER;
    }

    public static ArrayList<Attachment> Sort(ArrayList<Attachment> attachments) {
        if (attachments == null || attachments.size() == 0)
            return new ArrayList<>();

        final MainApp app = MainApp.getMainApp();

        Collections.sort(attachments, new Comparator<Attachment>() {
            @Override
            public int compare(Attachment lhs, Attachment rhs) {

//                long l = DateTool.getDateToTimestamp(lhs.getCreatedAt(), app.df_api_get);
//                long r = DateTool.getDateToTimestamp(rhs.getCreatedAt(), app.df_api_get);
                long l= com.loyo.oa.common.utils.DateTool.getSecondStamp(lhs.getCreatedAt());
                long r= com.loyo.oa.common.utils.DateTool.getSecondStamp(rhs.getCreatedAt());
                return (l - r) > 0 ? -1 : 1;
            }
        });
        return attachments;
    }
}