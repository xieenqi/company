package com.loyo.oa.v2.activityui.worksheet.common;

import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.io.Serializable;
import java.lang.reflect.Type;

/**
 * Created by EthanGong on 16/9/2.
 *
 * 工单相关人员角色
 *
 */


public class WSRole implements Serializable {

    public static WSRole None       = new WSRole(0);      /* 无角色，默认 0 */
    public static WSRole Creator    = new WSRole(1 << 0); /* 创建者角色  1 */
    public static WSRole Responsor  = new WSRole(1 << 1); /* 负责人角色  2 */
    public static WSRole Dispatcher = new WSRole(1 << 2); /* 分派人角色  4 */

    private int roleCode = 0;
    private WSRole(int roleCode) {
        this.roleCode = roleCode;
    }

    public WSRole() {
        this.roleCode = 0;
    }

    public void addRole(WSRole role) {
        roleCode = roleCode | role.roleCode;
    }

    public void removeRole(WSRole role) {
        roleCode = roleCode & (~ role.roleCode);
    }

    public boolean hasRole(WSRole role) {
        int code = roleCode & (role.roleCode);
        return code != 0;
    }

    public  boolean equals(Object obj) {
        if (obj==null || obj.getClass() != WSRole.class) {
            return false;
        }
        return this.roleCode == ((WSRole)obj).roleCode;
    }

    public boolean isResponsor() {
        return hasRole(Responsor);
    }

    public boolean isDispatcher() {
        return hasRole(Dispatcher);
    }

    /** gson 序列化和反序列化 */
    public static class WSRoleSerializer implements JsonSerializer<WSRole>,
            JsonDeserializer<WSRole> {

        // 对象转为Json时调用,实现JsonSerializer<WSRole>接口
        @Override
        public JsonElement serialize(WSRole role, Type arg1,
                                     JsonSerializationContext arg2) {
            return new JsonPrimitive(role.roleCode);
        }

        // json转为对象时调用,实现JsonDeserializer<WSRole>接口
        @Override
        public WSRole deserialize(JsonElement json, Type typeOfT,
                                           JsonDeserializationContext context) throws JsonParseException {
            int roleCode = json.getAsInt();
            return new WSRole(roleCode);
        }
    }


    /* 测试 */

    public void debug() {
        Log.v("wsrole", "WSRole code = "+roleCode);
    }

    public static void test(){

        WSRole role = new WSRole();
        role.addRole(WSRole.Creator);
        role.debug();
        if (role.hasRole(Dispatcher)) {
            Log.v("wsrole", "error");
        }

        role.addRole(WSRole.Responsor);
        role.debug();
        if (role.hasRole(Dispatcher)) {
            Log.v("wsrole", "error");
        }
        role.addRole(WSRole.Dispatcher);
        role.debug();
        if (! role.hasRole(Dispatcher)) {
            Log.v("wsrole", "error");
        }

        role.removeRole(WSRole.Dispatcher);
        role.debug();
        if (role.hasRole(Dispatcher)) {
            Log.v("wsrole", "error");
        }
    }

}
