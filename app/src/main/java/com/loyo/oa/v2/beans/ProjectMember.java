package com.loyo.oa.v2.beans;

import android.text.TextUtils;

import com.loyo.oa.v2.ui.activity.project.HttpProject;
import com.loyo.oa.v2.tool.ListUtil;

import java.util.ArrayList;

/**
 * com.loyo.oa.v2.beans
 * 描述 :
 * 作者 : ykb
 * 时间 : 15/9/8.
 */
public class ProjectMember extends BaseBeans {


    public ProjectMember(String userId, boolean canreadall) {
        this.userId = userId;
        this.canReadAll = canreadall;
    }

    public boolean canReadAll;// (bool, optional): ,
    public String id;// (int64, optional): ,
    public boolean ismanager; //(bool, optional): ,
    public long projectId;// (int64, optional): ,
    public User user;// (&{organization User}, optional): ,
    public String userId;// (int, optional):
    public Department dept;


//    public class Dept {
//        public String id;09p-
//        public String xpath;
//        public String name;
//    }

    public static String GetMenberUserIds(ArrayList<HttpProject.ProjectMember> memberList) {
        if (ListUtil.IsEmpty(memberList)) {
            return "";
        }

        StringBuffer sb = null;
        for (HttpProject.ProjectMember member : memberList) {
            if (sb == null) {
                sb = new StringBuffer();
            } else {
                sb.append(",");
            }

            if (null != member.user && null != member.user.id) {
                sb.append(member.user.id);
            }

            if (null != member.dept && null != member.dept.id) {
                sb.append(member.dept.id);
            }
        }

        return sb == null ? "" : sb.toString();
    }

    public static String GetMnagerUserIds(ArrayList<HttpProject.ProjectManaer> memberList) {
        if (ListUtil.IsEmpty(memberList)) {
            return "";
        }

        StringBuffer sb = null;
        for (HttpProject.ProjectManaer member : memberList) {
            if (sb == null) {
                sb = new StringBuffer();
            } else {
                sb.append(",");
            }

            sb.append(TextUtils.isEmpty(member.user.id) ? member.user.id : member.user.id);
        }

        return sb == null ? "" : sb.toString();
    }

    public static String GetUserNames(ArrayList<HttpProject.ProjectMember> memberList) {
        if (ListUtil.IsEmpty(memberList)) {
            return "";
        }

        StringBuffer sb = null;
        for (HttpProject.ProjectMember member : memberList) {
            if (sb == null) {
                sb = new StringBuffer();
            } else {
                sb.append(",");
            }

            if (null != member.user && null != member.user.name) {
                sb.append(member.user.name);
            }

            if (null != member.dept && null != member.dept.name) {
                sb.append(member.dept.name);
            }
        }

        return sb == null ? "" : sb.toString();
    }


    public static String getManagersName(ArrayList<HttpProject.ProjectManaer> memberList) {
        if (ListUtil.IsEmpty(memberList)) {
            return "";
        }

        StringBuffer sb = null;
        for (HttpProject.ProjectManaer member : memberList) {
            if (sb == null) {
                sb = new StringBuffer();
            } else {
                sb.append(",");
            }

            if (member.user != null) {
                sb.append(member.user.getRealname());
            }
        }

        return sb == null ? "" : sb.toString();
    }

    public static String getMembersName(ArrayList<HttpProject.ProjectMember> memberList) {
        if (ListUtil.IsEmpty(memberList)) {
            return "";
        }

        StringBuffer sb = null;
        for (HttpProject.ProjectMember member : memberList) {
            if (sb == null) {
                sb = new StringBuffer();
            } else {
                sb.append(",");
            }

            if (member.user != null) {
                sb.append(member.user.getRealname());
            }

            if (member.dept != null) {
                sb.append(member.dept.name);
            }
        }

        return sb == null ? "" : sb.toString();

    }

    @Override
    String getOrderStr() {
        return projectId + "";
    }

    @Override
    public String getId() {
        return "";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;

        if (this == o) {
            return true;
        }

        return this.userId == ((ProjectMember) o).userId;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
