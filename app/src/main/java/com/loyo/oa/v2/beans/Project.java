package com.loyo.oa.v2.beans;

import com.loyo.oa.v2.application.MainApp;

import java.util.ArrayList;
import java.util.Date;

/**
 * com.loyo.oa.v2.beans
 * 描述 :
 * 作者 : ykb
 * 时间 : 15/9/8.
 */
public class Project extends BaseBeans {

    public static final int STATUS_PROCESSING = 1;  //进行中
    public static final int STATUS_FINISHED = 0;    //已完成

    private String attachmentUUId;//(string, optional): ,
    private ArrayList<Attachment> attachments = new ArrayList<>();//(array[&{common Attachment}], optional): ,
    private String content;// (string, optional): ,
    private long createdAt;// (int64, optional): ,
    private User creator;// (&{organization User}, optional): ,
    private String id;// (int64, optional): ,
    private ArrayList<ProjectMember> managers = new ArrayList<>();//array[ProjectMember], optional): ,
    private ArrayList<ProjectMember> members = new ArrayList<>();//array[ProjectMember], optional): ,
    private int status;// (int, optional): ,
    private String title;// (string, optional): ,
    private int totalTask;//
    private int totalWorkReport;
    private int totalAttachment;
    private int totalWfinstance;
    private int totalDiscussion;

    public int getTotalDiscussion() {
        return totalDiscussion;
    }

    public void setTotalDiscussion(int totalDiscussion) {
        this.totalDiscussion = totalDiscussion;
    }

    public int getTotalWfinstance() {
        return totalWfinstance;
    }

    public void setTotalWfinstance(int totalWfinstance) {
        this.totalWfinstance = totalWfinstance;
    }

    public int getTotalAttachment() {
        return totalAttachment;
    }

    public void setTotalAttachment(int totalAttachment) {
        this.totalAttachment = totalAttachment;
    }

    public int getTotalWorkReport() {
        return totalWorkReport;
    }

    public void setTotalWorkReport(int totalWorkReport) {
        this.totalWorkReport = totalWorkReport;
    }

    public int getTotalTask() {
        return totalTask;
    }

    public void setTotalTask(int totalTask) {
        this.totalTask = totalTask;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ArrayList<ProjectMember> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<ProjectMember> members) {
        this.members = members;
    }

    public ArrayList<ProjectMember> getManagers() {
        return managers;
    }

    public void setManagers(ArrayList<ProjectMember> managers) {
        this.managers = managers;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getCreatedAt() {
        return createdAt * 1000;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ArrayList<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(ArrayList<Attachment> attachments) {
        this.attachments = attachments;
    }

    public String getAttachmentUUId() {
        return attachmentUUId;
    }

    public void setAttachmentUUId(String attachmentUUId) {
        this.attachmentUUId = attachmentUUId;
    }

    /**
     * 判断是否是负责人
     *
     * @return
     */
    public boolean isManager() {
//        int id = MainApp.user.getId();
        ArrayList<ProjectMember> responsers = this.getManagers();
        if (null != responsers && !responsers.isEmpty()) {
            for (int i = 0; i < responsers.size(); i++) {
                User u = responsers.get(i).getUser();
                if (null == u) {
                    continue;
                }
                if (u.isCurrentUser()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断是否是创建者
     *
     * @return
     */
    public boolean isCreator() {
//        int id = MainApp.user.getId();
        User creator = this.getCreator();

        return (null != creator && creator.isCurrentUser());
    }

    @Override
    public String getOrderStr() {
        return MainApp.getMainApp().df9.format(new Date(getCreatedAt()));
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getOrderStr2() {
        return status == 1 ? "0" : "1";
    }
}
