package com.loyo.oa.v2.beans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Task extends BaseBeans {

    public static final int STATUS_PROCESSING = 1;  //进行中
    public static final int STATUS_REVIEWING = 2;   //审核中
    public static final int STATUS_FINISHED = 3;    //已完成

    public static final List<String> RemindList = Arrays.asList("不提醒", "截止前10分钟", "截止前30分钟", "截止前1小时", "截止前3小时", "截止前1天");
    public static final List<Integer> RemindListSource = Arrays.asList(0, 10, 30, 60, 180, 1440);
    public String attachmentUUId;
    public String content;
    public long createdAt;
    public String id;
    public long planendAt;
    public String projectId;
    public boolean remindflag;
    public int remindtime;
    public boolean reviewFlag;
    public int score;
    public int status;
    public String title;
    public Project ProjectInfo;
    public ArrayList<Attachment> attachments;
    public ArrayList<TaskCheckPoint> checklists;
    public ArrayList<TaskReviewComment> reviewComments;
    public Members members = new Members();
    public ArrayList<Reviewer> responsiblePersons = new ArrayList<>();
    public NewUser responsiblePerson;
    public NewUser creator;

    /*保存本地使用*/
    public String responsiblePersonId;
    public String responsiblePersonName;
    public String TaskComment;


    public static final String GetRemindText(int remind) {
        int index = RemindListSource.indexOf(remind);
        if (index > 0) {
            return RemindList.get(index);
        }
        return "";
    }

    public Members getMembers() {
        return members;
    }

    public void setMembers(Members members) {
        this.members = members;
    }


    public Project getProject() {
        return ProjectInfo;
    }

    public void setProject(Project project) {
        this.ProjectInfo = project;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getTaskComment() {
        return TaskComment;
    }

    public void setTaskComment(String taskComment) {
        TaskComment = taskComment;
    }

    public String getResponsiblePersonName() {
        return responsiblePersonName;
    }

    public void setResponsiblePersonName(String responsiblePersonName) {
        this.responsiblePersonName = responsiblePersonName;
    }

    public String getResponsiblePersonId() {
        return responsiblePersonId;
    }

    public void setResponsiblePersonId(String responsiblePersonId) {
        this.responsiblePersonId = responsiblePersonId;
    }

    public boolean isAck() {
        if (creator.isCurrentUser()) {
            return true;
        }

        for (Reviewer responseUser : responsiblePersons) {
            if (responseUser.getUser() != null && responseUser.getUser().isCurrentUser()) {
                return responseUser.isViewed();
            }
        }

        return true;
    }

    public void setAck(boolean ack) {
        for (int i = 0; i < responsiblePersons.size(); i++) {
            NewUser u = responsiblePersons.get(i).getUser();
            if (u != null && u.isCurrentUser()) {
                responsiblePersons.get(i).setViewed(ack);
                return;
            }
        }
    }

    public Task() {
        reviewComments = new ArrayList<TaskReviewComment>();
        attachments = new ArrayList<Attachment>();
        checklists = new ArrayList<TaskCheckPoint>();
    }

    @Override
    String getOrderStr() {
        return String.valueOf(getCreatedAt());
    }

    public String getAttachmentUUId() {
        return attachmentUUId;
    }

    public void setAttachmentUUId(String attachmentUUId) {
        this.attachmentUUId = attachmentUUId;
    }

    public ArrayList<Attachment> getAttachments() {
        if (attachments == null) {
            return new ArrayList<>();
        }

        return attachments;
    }

    public void setAttachments(ArrayList<Attachment> attachments) {
        this.attachments = attachments;
    }

    public ArrayList<TaskCheckPoint> getchecklists() {
        if (checklists == null) {
            checklists = new ArrayList<>();
        }

        return checklists;
    }

    public String getContent() {
        return content == null ? "" : content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreatedAt() {
        if (String.valueOf(createdAt).length() == 10)
            return createdAt * 1000;

        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }


    public NewUser getCreator() {
        return creator;
    }

    public void setCreator(NewUser creator) {
        this.creator = creator;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getPlanEndAt() {
        return planendAt;
    }

    public void setPlanEndAt(long planEndAt) {
        this.planendAt = planEndAt;
    }

    public boolean isRemindFlag() {
        return remindflag;
    }

    public int getRemindTime() {
        return remindtime;
    }

    public void setRemindTime(int remindTime) {
        this.remindtime = remindTime;
    }

    public NewUser getResponsiblePerson() {

        return responsiblePerson;
    }

    public void setResponsiblePerson(NewUser responsiblePerson) {
        this.responsiblePerson = responsiblePerson;
    }

    public ArrayList<TaskReviewComment> getReviewComments() {
        return reviewComments;
    }

    public void setReviewComments(ArrayList<TaskReviewComment> reviewComments) {
        this.reviewComments = reviewComments;
    }

    public boolean isReviewFlag() {
        return reviewFlag;
    }

    public void setReviewFlag(boolean reviewFlag) {
        this.reviewFlag = reviewFlag;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTitle() {
        return title == null ? "" : title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getOrderStr2() {
        return status + "";
    }

    public ArrayList<NewUser> getJoinedUsers() {
        ArrayList<NewUser> user = new ArrayList<>();
        for (int i = 0; i < getMembers().users.size(); i++) {
            user.add(getMembers().users.get(i));
        }
        return user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        return id.equals(task.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
