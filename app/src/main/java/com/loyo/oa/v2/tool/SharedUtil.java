package com.loyo.oa.v2.tool;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;


/**
 * @description 操作sharedpreference的工具
 */
public final class SharedUtil {
    protected SharedUtil() {
        throw new UnsupportedOperationException(); // 防止子类调用
    }

    public static void remove(Context context, String name) {
        SharedPreferences base_share = context.getSharedPreferences(FinalVariables.BASE_SHARE, Context.MODE_PRIVATE);
        Editor editor = base_share.edit();
        try {
            editor.remove(name);
            editor.apply();
        } catch (Exception e) {
            Global.ProcException(e);
        }
    }

    /**
     * @param context 上下文
     * @param name    保存在shared文件中的key
     * @param value   保存在shared中的value
     * @描述 保存指定名称的值
     */
    public static void put(Context context, String name, String value) {
        SharedPreferences base_share = context.getSharedPreferences(FinalVariables.BASE_SHARE, Context.MODE_PRIVATE);
        Editor editor = base_share.edit();
        editor.putString(name, value);
        editor.apply();
    }

    /**
     * @param context 上下文
     * @param name    保存在shared文件中的key
     * @描述 获取指定名称的值
     */
    public static String get(Context context, String name) {
        SharedPreferences base_share = context.getSharedPreferences(FinalVariables.BASE_SHARE, Activity.MODE_PRIVATE);
        return base_share.getString(name, "");
    }

    /**
     * @param context 上下文
     * @param name    保存在shared文件中的key
     * @param value   保存在shared中的value
     * @描述 保存指定名称的值
     */
    public static void putBoolean(Context context, String name, boolean value) {
        SharedPreferences base_share = context.getSharedPreferences(FinalVariables.BASE_SHARE, Context.MODE_PRIVATE);
        Editor editor = base_share.edit();
        editor.putBoolean(name, value);
        editor.apply();
    }

    /**
     * @param context 上下文
     * @param name    保存在shared文件中的key
     * @描述 获取指定名称的值
     */
    public static boolean getBoolean(Context context, String name) {
        SharedPreferences base_share = context.getSharedPreferences(FinalVariables.BASE_SHARE, Activity.MODE_PRIVATE);
        return base_share.getBoolean(name, false);
    }


    /**
     * @描述 清除掉所有的键值对
     */

    public static void clearInfo(Context context) {
        SharedPreferences base_share = context.getSharedPreferences(FinalVariables.BASE_SHARE, Context.MODE_PRIVATE);
        Editor editor = base_share.edit();
        editor.clear();
        editor.apply();
    }

}
