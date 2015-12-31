package com.loyo.oa.v2.activity.project;

import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.User;

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
    public ArrayList<ProjectManaer> managers=new ArrayList<>();
    public ArrayList<ProjectMember> members=new ArrayList<>();
    public User creator;
    public String attachmentUUId;
    public long createdAt;
    public boolean viewed;
    public int status;
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
        public User user=new User();
        public boolean canReadAll;
    }


    public class ProjectMember implements Serializable {
        public User user=new User();
        public Dept dept;
        public boolean canReadAll;
    }

//    public class User {
//        public String id;
//        public String name;
//        public String avatar;
//    }

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


    public void setManagers(ArrayList<HttpProject.ProjectManaer> managers) {
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




