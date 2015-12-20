package com.loyo.oa.v2.beans;

import com.loyo.oa.v2.activity.ProjectAddActivity;
import com.loyo.oa.v2.application.MainApp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * com.loyo.oa.v2.beans
 * 描述 :
 * 作者 : ykb
 * 时间 : 15/9/8.
 */
public class Project extends BaseBeans implements Serializable{

    public static final int STATUS_PROCESSING = 1;  //进行中
    public static final int STATUS_FINISHED = 0;    //已完成

    public String attachmentUUId;//(string, optional): ,
    public ArrayList<Attachment> attachments = new ArrayList<>();//(array[&{common Attachment}], optional): ,
    public String content;// (string, optional): ,
    public long createdAt;// (int64, optional): ,
    public User creator;// (&{organization User}, optional): ,
    public String id;// (int64, optional): ,
    public ArrayList<ProjectMember> managers = new ArrayList<>();//array[ProjectMember], optional): ,
    public ArrayList<ProjectMember> members = new ArrayList<>();//array[ProjectMember], optional): ,
    public int status;// (int, optional): ,
    public String title;// (string, optional): ,
    public int totalTask;//
    public int totalWorkReport;
    public int totalAttachment;
    public int totalWfinstance;
    public int totalDiscussion;
    //新增加的
    public boolean viewed;
    public BizExtData bizExtData;
    public ArchiveData archiveData;

    public class BizExtData {
        public int BizExtData;
        public int attachmentCount;
    }

    public class ArchiveData {
        public int discuss;
        public int approval;
        public int task;
        public int workreport;
        public int attachment;
    }


    public long getCreatedAt() {
        return createdAt * 1000;
    }


    public void setManagers(ArrayList<ProjectAddActivity.ManagersMembers> data) {
        ArrayList<ProjectMember> newData = new ArrayList<>();
        for (ProjectAddActivity.ManagersMembers element : data) {
            ProjectMember pm = new ProjectMember(element.user.id, element.canreadall);
            newData.add(pm);
        }
        this.managers = newData;
    }

    /**
     * 判断是否是负责人
     *
     * @return
     */
    public boolean isManager() {
//        int id = MainApp.user.getId();
        ArrayList<ProjectMember> responsers = this.managers;
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
        User creator = this.creator;

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
