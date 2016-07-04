package com.loyo.oa.v2.beans;

import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.other.bean.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * com.loyo.oa.v2.beans
 * 描述 :通知  没个item详情
 * 作者 : ykb
 * 时间 : 15/8/28.
 */
public class Bulletin extends BaseBeans {
    public String attachmentUUId;
    public ArrayList<Attachment> attachments = new ArrayList<>();
    public String content;
    public long createdAt;
    public String deptName;
    public String id;
    public boolean isPublic;
    public String title;
    public User creator;
    public ArrayList<BulletinViewer> viewers = new ArrayList<>();

    public String getUserName() {
        if (creator != null) {
            return creator.getRealname();
        }

        return "";
    }
    public String getPosition() {

        if (creator != null && creator.shortPosition != null) {
            return creator.shortPosition.getName();
        }

        return "";
    }

    public String getDeptName() {
        if (creator != null && creator.shortDept != null ) {
//            return creator.getDepts().get(0).getName();
            return creator.shortDept.getName();
        }

        return "";
    }

//    public User getCreator() {
//        return creator;
//    }
//    public void setCreator(User creator) {
//        this.creator = creator;
//    }
//
//
//    public ArrayList<BulletinViewer> getViewers() {
//        return viewers;
//    }
//
//    public void setViewers(ArrayList<BulletinViewer> viewers) {
//        this.viewers = viewers;
//    }
//
//    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//
//
//    public boolean isPublic() {
//        return isPublic;
//    }
//
//    public void setIsPublic(boolean isPublic) {
//        this.isPublic = isPublic;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//
//
//    public long getCreatedAt() {
//        return createdAt;
//    }
//
//    public void setCreatedAt(long createdAt) {
//        this.createdAt = createdAt;
//    }
//
//    public String getContent() {
//        return content;
//    }
//
//    public void setContent(String content) {
//        this.content = content;
//    }
//
//    public ArrayList<Attachment> getAttachments() {
//        return attachments;
//    }
//
//    public void setAttachments(ArrayList<Attachment> attachments) {
//        this.attachments = attachments;
//    }
//
//    public String getAttachmentUUId() {
//        return attachmentUUId;
//    }
//
//    public void setAttachmentUUId(String attachmentUUId) {
//        this.attachmentUUId = attachmentUUId;
//    }



    @Override
    public String getId() {
        return id;
    }

    @Override
    String getOrderStr() {
        return MainApp.getMainApp().df3.format(new Date(createdAt * 1000));
    }


    class Creator {
//        public String Creator;
//        public String company_id;
//        public ArrayList<Depts> depts;
//        public ShortPosition shortPosition;
//
//        public String Creator;
//        public String Creator;
//        public String Creator;
//        public String Creator;

        class Depts {
            public String id;
            public String name;
            public String superiorId;
            public List<Object> users;
            public String xpath;
            public long createdAt;
            public long updatedAt;

        }
        class ShortPosition{
            public String id;
            public String name;
            public int companyId;
            public int sequence;
        }
    }


}
