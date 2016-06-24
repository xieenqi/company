package com.loyo.oa.v2.common;

import com.loyo.oa.v2.tool.Config_project;

/**
 * 精简接口 数据 相关API  钉钉相关一套
 * Created by xeq on 16/6/13.
 */
public class ConfigAppURL {
    protected ConfigAppURL() {
        throw new UnsupportedOperationException(); // 防止子类调用
    }

    private static String FORMAL = "http://ukuaiqi.com";
//    private String STAGING = "http://112.74.66.99:7000";
//    private String TEST = "http://192.168.31.131:7000";

    private static String BAS_URL = Config_project.isRelease ? FORMAL : Config_project.IP + ":7000";
    /**
     * 审批url前段
     */
    public static String WFINSTANCE_URL = BAS_URL + "/api";
//    192.168.31.131 测试
//    112.74.66.99 staging
//    ukuaiqi.com 产品
}
