package com.loyo.oa.v2.beans;

import android.text.TextUtils;

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
        this.canreadall = canreadall;
    }

    private boolean canreadall;// (bool, optional): ,
    private String id;// (int64, optional): ,
    private boolean ismanager; //(bool, optional): ,
    private long projectId;// (int64, optional): ,
    private User user;// (&{organization User}, optional): ,
    private String  userId;// (int, optional):

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public boolean ismanager() {
        return ismanager;
    }

    public void setIsmanager(boolean ismanager) {
        this.ismanager = ismanager;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isCanreadall() {
        return canreadall;
    }

    public void setCanreadall(boolean canreadall) {
        this.canreadall = canreadall;
    }

    public static String GetUserIds(ArrayList<ProjectMember> memberList) {
        if (ListUtil.IsEmpty(memberList)) {
            return "";
        }

        StringBuffer sb = null;
        for (ProjectMember member : memberList) {
            if (sb == null) {
                sb = new StringBuffer();
            } else {
                sb.append(",");
            }

            sb.append(TextUtils.isEmpty(member.getUserId()) ? member.getUserId() : member.getUser().getId());
        }

        return sb == null ? "" : sb.toString();
    }

    public static String GetUserNames(ArrayList<ProjectMember> memberList) {
        if (ListUtil.IsEmpty(memberList)) {
            return "";
        }

        StringBuffer sb = null;
        for (ProjectMember member : memberList) {
            if (sb == null) {
                sb = new StringBuffer();
            } else {
                sb.append(",");
            }

            if (member.getUser() != null) {
                sb.append(member.getUser().getRealname());
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

        return this.getUserId() == ((ProjectMember) o).getUserId();
    }
}
