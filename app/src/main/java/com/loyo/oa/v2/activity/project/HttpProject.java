package com.loyo.oa.v2.activity.project;

import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.beans.UserInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by pj on 15/12/29.
 */
public class HttpProject implements Serializable {
    public String id;
    public String name;
    public String title;
    public String content;
    public ArrayList<ProjectManaer> managers = new ArrayList<>();//负责人
    public ArrayList<ProjectMember> members = new ArrayList<>();//参与人
    public User creator;
    public String attachmentUUId;
    public long createdAt;
    public boolean viewed;
    public int status;//进行中【1】已结束【2】全部【0】
    public ArchiveData archiveData;
    public BizExtData bizExtData;


    public class BizExtData implements Serializable {
        public int discussCount;
        public int attachmentCount;
    }

    public class ArchiveData implements Serializable {
        public int approval;
        public int attachment;
        public int discuss;
        public int task;
        public int workreport;
    }

    public class ProjectManaer implements Serializable {
        public User user = new User();
        public boolean canReadAll;
    }


    public class ProjectMember implements Serializable {
        public User user = new User();
        public Dept dept = new Dept();
        public boolean canReadAll;
    }

    /**
     * @是否和项目相关 说  明:比较个人id相和部门id，并且是多部门情况下
     */
    public boolean isProjectRelevant() {
        String myId = MainApp.user.id;

        for (ProjectManaer manager : managers) {
            if (myId.equals(manager.user.id)) {
                return true;
            }
        }

        for (ProjectMember member : members) {
            if (null != member.user && myId.equals(member.user.id)) {
                return true;
            }

            for (UserInfo userInfo : MainApp.user.getDepts()) {
                if (null != member.dept && userInfo.getShortDept().getXpath().contains(member.dept.xpath)) {
                    //部门id改成部门XPath
                    return true;
                }
            }
        }

        if (myId.equals(creator.id)) {
            return true;
        }
        return false;
    }

    public class Dept implements Serializable {
        public String id;
        public String xpath;
        public String name;
    }


    public long getCreatedAt() {
        return createdAt * 1000;
    }

    /**
     * 判断是否是负责人
     *
     * @return
     */
    public boolean isManager() {
        boolean isFound = false;
        ArrayList<ProjectManaer> retManagers = this.managers;
        if (null != retManagers && !retManagers.isEmpty()) {
            for (int i = 0; i < retManagers.size(); i++) {
                User u = retManagers.get(i).user;
                if (null == u) {
                    continue;
                }
                if (u.isCurrentUser()) {
                    isFound = true;
                    break;
                }
            }
        }


        return isFound;
    }


    public void setManagers(final ArrayList<HttpProject.ProjectManaer> managers) {
        ArrayList<ProjectManaer> newData = new ArrayList<>();
        for (ProjectManaer element : managers) {
            ProjectManaer pm = new ProjectManaer();
            pm.canReadAll = element.canReadAll;
            pm.user = element.user;
            newData.add(pm);
        }
        this.managers = newData;
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


    public String getOrderStr() {
        return MainApp.getMainApp().df9.format(new Date(createdAt));
    }


    public String getId() {
        return id;
    }


    public String getOrderStr2() {
        return status == 1 ? "0" : "1";
    }


}




