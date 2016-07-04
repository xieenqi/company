package com.loyo.oa.v2.activityui.other.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2014/12/18 0018.
 */
public class Company implements Serializable {
    private long id;//: 1,
    private String name;//": "",
    private String fullname;//": "",
    private String account;//": "",
    private String address;//": "",
    private String tel;//": "",
    private String userCapacity;//": 0,
    private String storageCapacity;//": 0,
    private Boolean activated;//": false,
    private String serveStartAt;//": "0001-01-01T00:00:00Z",
    private String serveEndAt;//": "0001-01-01T00:00:00Z",
    private String createdAt;//": "0001-01-01T00:00:00Z",
    private String updatedAt;//": "0001-01-01T00:00:00Z"

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getUserCapacity() {
        return userCapacity;
    }

    public void setUserCapacity(String userCapacity) {
        this.userCapacity = userCapacity;
    }

    public String getStorageCapacity() {
        return storageCapacity;
    }

    public void setStorageCapacity(String storageCapacity) {
        this.storageCapacity = storageCapacity;
    }

    public Boolean getActivated() {
        return activated;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }

    public String getServeStartAt() {
        return serveStartAt;
    }

    public void setServeStartAt(String serveStartAt) {
        this.serveStartAt = serveStartAt;
    }

    public String getServeEndAt() {
        return serveEndAt;
    }

    public void setServeEndAt(String serveEndAt) {
        this.serveEndAt = serveEndAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
