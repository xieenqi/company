package com.loyo.oa.v2.common.compat;

import com.loyo.oa.contactpicker.model.result.StaffMember;
import com.loyo.oa.contactpicker.model.result.StaffMemberCollection;
import com.loyo.oa.v2.activityui.customer.bean.Member;
import com.loyo.oa.v2.beans.Members;
import com.loyo.oa.v2.beans.NewUser;

/**
 * Created by EthanGong on 2016/10/20.
 * 兼容
 */

public class Compat {

    /** 多用户／部门转化 */
    public static StaffMemberCollection convertMembersToStaffCollection(Members members) {

        if (members == null) {
            return null;
        }

        StaffMemberCollection collection = new StaffMemberCollection();
        for (NewUser dept:members.depts) {
            StaffMember staffMember = convertNewUserToStaffMember(dept);
            if (staffMember != null){
                collection.depts.add(staffMember);
            }
        }

        for (NewUser user:members.users) {
            StaffMember staffMember = convertNewUserToStaffMember(user);
            if (staffMember != null){
                collection.users.add(staffMember);
            }
        }


        return collection;
    }

    public static Members convertStaffCollectionToMembers(StaffMemberCollection collection) {

        if (collection == null){
            return null;
        }

        Members members = new Members();
        for (StaffMember staffMember:collection.depts) {
            NewUser dept = convertStaffMemberToNewUser(staffMember);
            if (dept != null){
                members.depts.add(dept);
            }
        }

        for (StaffMember staffMember:collection.users) {
            NewUser user = convertStaffMemberToNewUser(staffMember);
            if (user != null){
                members.users.add(user);
            }
        }

        return members;
    }

    /**  单用户转化 */
    public static StaffMemberCollection convertMemberToStaffCollection(Member member) {

        if (member == null) {
            return null;
        }

        return convertNewUserToStaffCollection(member.getUser());
    }

    public static Member convertStaffCollectionToMember(StaffMemberCollection collection) {
        NewUser user = convertStaffCollectionToNewUser(collection);
        if (user == null) {
            return null;
        }
        Member member = new Member();
        member.setUser(user);
        return member;
    }


    /** 单用户转化 */
    public static StaffMemberCollection convertNewUserToStaffCollection(NewUser user) {

        StaffMember staffMember = convertNewUserToStaffMember(user);

        if (staffMember == null){
            return null;
        }
        else {
            StaffMemberCollection collection = new StaffMemberCollection();
            collection.users.add(staffMember);
            return collection;
        }
    }

    public static  NewUser convertStaffCollectionToNewUser(StaffMemberCollection collection) {

        if (collection == null) {
            return null;
        }

        StaffMember staffMember = null;
        if (collection.users.size() >0) {
            staffMember = collection.users.get(0);
        }

        return convertStaffMemberToNewUser(staffMember);
    }

    public static StaffMember convertNewUserToStaffMember(NewUser user) {
        if (user == null) {
            return null;
        }
        StaffMember staffMember = new StaffMember();
        staffMember.id = user.getId();
        staffMember.name = user.getName();
        staffMember.avatar = user.getAvatar();
        staffMember.xpath = user.getXpath();
        return staffMember;
    }

    public static NewUser convertStaffMemberToNewUser(StaffMember staffMember) {
        if (staffMember == null){
            return null;
        }

        NewUser user = new NewUser();
        user.setAvatar(staffMember.avatar);
        user.setId(staffMember.id);
        user.setName(staffMember.name);
        user.setXpath(staffMember.xpath);
        return user;
    }

}
