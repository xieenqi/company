package com.loyo.oa.v2.common;

/**
 * 用于 intent 传递key
 * Created  xnq 15/12/24.
 */
public class ExtraAndResult {

    public static final String EXTRA_ID = "extra_id";
    public static final String EXTRA_NAME = "extra_name";
    public static final String EXTRA_DATA = "extra_data";
    public static final String EXTRA_OBJ = "extra_obj";

    public static final String RESULT_ID = "result_id";
    public static final String RESULT_NAME = "result_name";
    public static final String RESULT_DATA = "result_data";
    public static final String RESULT_OBJ = "result_obj";
    /**
     * 传递部门数据的请求码
     */
    public static final int REQUSET_PROJECT = 100;
    /**
     * 传递【点评人】数据的请求码
     */
    public static final int REQUSET_COMMENT = 200;
    /**
     * 传递【抄送人】数据的请求码
     */
    public static final int REQUSET_COPY_PERSONS = 301;
}
