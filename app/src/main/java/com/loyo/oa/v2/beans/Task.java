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

    public static final String GetRemindText(int remind) {
        int index = RemindListSource.indexOf(remind);
        if (index > 0) {
            return RemindList.get(index);
        }
        return "";
    }

    private long actualendAt;
    private String attachmentUUId;
    private String content;
    private long createdAt;
    private String customerIds;
    private String id;
    private long planendAt;
    private String projectId;
    private boolean remindflag;
    private int remindtime;
    private boolean reviewFlag;
    private int score;
    private boolean reviewed;
    private long reviewedAt;
    private int status;
    private String title;
    private long updatedAt;
    private Project ProjectInfo;

    private NewUser creator;

    private int discusscount;
    private int attachmentcount;

    private ArrayList<Attachment> attachments;
    private ArrayList<TaskCheckPoint> checklists;
    private ArrayList<TaskReviewComment> reviewComments;
    private ArrayList<Reviewer> members = new ArrayList<>();
    private ArrayList<Reviewer> responsiblePersons = new ArrayList<>();

    //    <!--保存本地使用-->
    private String responsiblePersonId;
    private String responsiblePersonName;
    private String TaskComment;
//    <!--保存本地使用-->


    public Project getProject() {
        return ProjectInfo;
    }

    public void setProject(Project project) {
        this.ProjectInfo = project;
    }

    public int getDiscusscount() {
        return discusscount;
    }

    public void setDiscusscount(int discusscount) {
        this.discusscount = discusscount;
    }

    public int getAttachmentcount() {
        return attachmentcount;
    }

    public void setAttachmentcount(int attachmentcount) {
        this.attachmentcount = attachmentcount;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public ArrayList<Reviewer> getMembers() {
        if (members == null){
            members = new ArrayList<>();
        }
        return members;
    }

    public void setMembers(ArrayList<Reviewer> members) {
        this.members = members;
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

        for (Reviewer member : members) {
            if (member.getUser() != null && member.getUser().isCurrentUser()) {
                return member.isViewed();
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

        for (int i = 0; i < members.size(); i++) {
            NewUser u = members.get(i).getUser();
            if (u != null && u.isCurrentUser()) {
                members.get(i).setViewed(ack);
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

    public long getActualEndAt() {
        return actualendAt;
    }

    public void setActualEndAt(long actualEndAt) {
        this.actualendAt = actualEndAt;
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

    public void setchecklists(ArrayList<TaskCheckPoint> checkList) {
        this.checklists = checkList;
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

    public String getCustomerIds() {
        return customerIds;
    }

    public void setCustomerIds(String customerIds) {
        this.customerIds = customerIds;
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

    public void setRemindFlag(boolean remindFlag) {
        this.remindflag = remindFlag;
    }

    public int getRemindTime() {
        return remindtime;
    }

    public void setRemindTime(int remindTime) {
        this.remindtime = remindTime;
    }

    public NewUser getResponsiblePerson() {
        if (responsiblePersons != null && responsiblePersons.size() > 0) {
            return responsiblePersons.get(0).getUser();
        }

        return null;
    }

    public void setResponsiblePerson(NewUser responsiblePerson) {
        responsiblePersons = new ArrayList<>(Arrays.asList(new Reviewer(responsiblePerson)));
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

    public boolean isReviewed() {
        return reviewed;
    }

    public void setReviewed(boolean reviewed) {
        this.reviewed = reviewed;
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
        return status+"";
    }

    public ArrayList<NewUser> getJoinedUsers() {
        ArrayList<NewUser> user = new ArrayList<>();

        for (Reviewer reviewer : getMembers()) {
            user.add(reviewer.getUser());
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
