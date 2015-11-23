package com.loyo.oa.v2.beans;

import java.io.Serializable;

/**
 * com.loyo.oa.v2.beans
 * 描述 :客户附加信息
 * 作者 : ykb
 * 时间 : 15/9/24.
 */
public class CustomerAddition implements Serializable {

    private long Id;// (int64, optional): ,
    private String area;// (string, optional): ,
    private String car_brand;// (string, optional): ,
    private String car_framenum;// (string, optional): ,
    private String car_homedate;// (string, optional): ,
    private String car_models;// (string, optional): ,
    private String car_number;// (string, optional): ,
    private String car_personname;// (string, optional): ,
    private long customer_id;// (int64, optional): ,
    private String customerservice;// (string, optional): ,
    private String email;// (string, optional): ,
    private String familysituation;// (string, optional): ,
    private String getpassportunit; //(string, optional): ,
    private String guid;// (string, optional): ,
    private String idcardno;// (string, optional): ,
    private int memberlevel;// (int, optional): ,
    private String membernum;// (string, optional): ,
    private String memberregdate;// (string, optional): ,
    private String postaddress;// (string, optional): ,
    private String qq;// (string, optional): ,
    private String wechat;// (string, optional):

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCar_brand() {
        return car_brand;
    }

    public void setCar_brand(String car_brand) {
        this.car_brand = car_brand;
    }

    public String getCar_framenum() {
        return car_framenum;
    }

    public void setCar_framenum(String car_framenum) {
        this.car_framenum = car_framenum;
    }

    public String getCar_homedate() {
        return car_homedate;
    }

    public void setCar_homedate(String car_homedate) {
        this.car_homedate = car_homedate;
    }

    public String getCar_models() {
        return car_models;
    }

    public void setCar_models(String car_models) {
        this.car_models = car_models;
    }

    public String getCar_number() {
        return car_number;
    }

    public void setCar_number(String car_number) {
        this.car_number = car_number;
    }

    public String getCar_personname() {
        return car_personname;
    }

    public void setCar_personname(String car_personname) {
        this.car_personname = car_personname;
    }

    public long getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(long customer_id) {
        this.customer_id = customer_id;
    }

    public String getCustomerservice() {
        return customerservice;
    }

    public void setCustomerservice(String customerservice) {
        this.customerservice = customerservice;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFamilysituation() {
        return familysituation;
    }

    public void setFamilysituation(String familysituation) {
        this.familysituation = familysituation;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getGetpassportunit() {
        return getpassportunit;
    }

    public void setGetpassportunit(String getpassportunit) {
        this.getpassportunit = getpassportunit;
    }

    public String getIdcardno() {
        return idcardno;
    }

    public void setIdcardno(String idcardno) {
        this.idcardno = idcardno;
    }

    public int getMemberlevel() {
        return memberlevel;
    }

    public void setMemberlevel(int memberlevel) {
        this.memberlevel = memberlevel;
    }

    public String getMembernum() {
        return membernum;
    }

    public void setMembernum(String membernum) {
        this.membernum = membernum;
    }

    public String getMemberregdate() {
        return memberregdate;
    }

    public void setMemberregdate(String memberregdate) {
        this.memberregdate = memberregdate;
    }

    public String getPostaddress() {
        return postaddress;
    }

    public void setPostaddress(String postaddress) {
        this.postaddress = postaddress;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }
}
