package com.loyo.oa.v2.beans;

import android.text.TextUtils;

import com.j256.ormlite.table.DatabaseTable;
import com.loyo.oa.v2.application.MainApp;

import java.io.Serializable;
import java.util.ArrayList;
@DatabaseTable
public class User implements Serializable {

    private String id;
    private String company_id;
    private String username;
    private int gender;
    private String mobile;
    private boolean avaactivatedtar;
    private String simplePinyin;
    private ArrayList<UserInfo> depts = new ArrayList<>();

    private String avatar;
    private String birthDay;
    private long createdAt;
    private String email;
    private String fullPinyin;
    private String realname;
    private String tel;
    private String title;
    private long updatedAt;
    private String departmentsName;
    private String superiorId;
    private boolean isBQQ;
    private String weixinId;
    private String weixinUnionId;
    private String name;


    private Role role;
    private Position shortPosition;

    private Department shortDept;

    public Department getShortDept() {
        return shortDept;
    }

    public void setShortDept(Department shortDept) {
        this.shortDept = shortDept;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getWeixinId() {
        return weixinId;
    }

    public void setWeixinId(String weixinId) {
        this.weixinId = weixinId;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public String getWeixinUnionId() {
        return weixinUnionId;
    }

    public void setWeixinUnionId(String weixinUnionId) {
        this.weixinUnionId = weixinUnionId;
    }

    public boolean isBQQ() {
        return isBQQ;
    }

    public void setIsBQQ(boolean isBQQ) {
        this.isBQQ = isBQQ;
    }

    public User() {
    }

    public User(String _id, String _RealName) {
        id = _id;
        realname = _RealName;
    }

    public String getDepartmentsName() {
        return departmentsName;
    }

    public void setDepartmentsName(String departmentsName) {
        this.departmentsName = departmentsName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public ArrayList<UserInfo> getDepts() {
        return depts;
    }

    public void setDepts(ArrayList<UserInfo> depts) {
        this.depts = depts;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullPinyin() {
        return fullPinyin;
    }

    public void setFullPinyin(String fullPinyin) {
        this.fullPinyin = fullPinyin;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRealname() {
        String rname=TextUtils.isEmpty(realname)?name:realname;
        return TextUtils.isEmpty(rname)?"":rname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public Position getShortPosition() {
        return shortPosition;
    }

    public void setShortPosition(Position shortPosition) {
        this.shortPosition = shortPosition;
    }

    public String getSimplePinyin() {
        return simplePinyin;
    }

    public void setSimplePinyin(String simplePinyin) {
        this.simplePinyin = simplePinyin;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSuperiorId() {
        return superiorId;
    }

    public void setSuperiorId(String superiorId) {
        this.superiorId = superiorId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o instanceof NewUser){
            return id.equals(((NewUser)o).getId());
        }

        if (!(o instanceof User)) {
            return false;
        }

        User user = (User) o;

        return id.equals(user.id);

    }

    public boolean isCurrentUser() {
        return equals(MainApp.user);
    }

    public NewUser toShortUser() {
        NewUser user = new NewUser();
        user.setId(this.getId());
        user.setName(this.getRealname());
        user.setAvatar(this.getAvatar());
//        if (null != depts) {
//            String deptId = "";
//            for (int i = 0; i < depts.size(); i++) {
//                deptId += depts.get(i).getShortDept().getId();
//                if (i < depts.size() - 1) {
//                    deptId += ",";
//                }
//            }
//            user.setDeptId(deptId);
//        }
        return user;
    }

    /**
     * 获取首字母当作GroupName
     * @return
     */
    public String getGroupName() {
        if (!TextUtils.isEmpty(getFullPinyin())) {
            return getFullPinyin().substring(0, 1).toUpperCase();
        } else if (!TextUtils.isEmpty(getSimplePinyin())) {
            return getSimplePinyin().substring(0, 1).toUpperCase();
        }

        return "";
    }
}
