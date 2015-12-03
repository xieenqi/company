package com.loyo.oa.v2.beans;

import android.text.TextUtils;

import com.loyo.oa.v2.application.MainApp;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * com.loyo.oa.v2.beans
 * 描述 :新版联系人
 * 作者 : ykb
 * 时间 : 15/9/30.
 */
public class Contact implements Serializable {
    private String id;
    private String name;
    private String tel;
    private long birth;
    private String qq;
    private String wx;
    private String wiretel;
    private String email;
    private String memo;
    private boolean isDefault;
    private ArrayList<ExtraData> extDatas =new ArrayList<>();


    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWiretel() {
        return wiretel;
    }

    public void setWiretel(String wiretel) {
        this.wiretel = wiretel;
    }

    public String getWx() {
        return wx;
    }

    public void setWx(String wx) {
        this.wx = wx;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public ArrayList<ExtraData> getExtDatas() {
        return extDatas;
    }

    public void setExtDatas(ArrayList<ExtraData> extDatas) {
        this.extDatas = extDatas;
    }

    public boolean isDefault(){
        return  isDefault;
    }
    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public String getBirthStr() {
        return birth<=0?"": MainApp.getMainApp().df4.format(birth*1000);
    }

    public long getBirth() {
        return birth;
    }

    public void setBirth(long birth) {
        this.birth = birth;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        return TextUtils.equals(getId(),((Contact)o).getId());
    }
}