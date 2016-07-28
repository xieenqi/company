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
    public static final String EXTRA_STATUS = "extra_status";
    public static final String EXTRA_TYPE = "extra_type";
    public static final String EXTRA_UUID = "extra_uuid";
    public static final String EXTRA_TYPE_ID = "extra_type_id";
    public static final String EXTRA_BOOLEAN = "extra_boolean";

    public static final String RESULT_ID = "result_id";
    public static final String RESULT_NAME = "result_name";
    public static final String RESULT_DATA = "result_data";
    public static final String RESULT_OBJ = "result_obj";

    public static final String CC_DEPARTMENT_ID = "Department_id";
    public static final String CC_USER_ID = "User_id";
    public static final String CC_DEPARTMENT_NAME = "Department_name";
    public static final String CC_USER_NAME = "User_name";

    public static final String STR_SHOW_TYPE = "show_type";
    public static final String STR_SELECT_TYPE = "select_type";
    public static final String STR_SUPER_ID = "super_id";
    public static final String WELCOM_KEY = "welcom_key";

    public static final String HOME_ITEM = "home_item";

    public static final String IS_TEAM = "is_team";
    public static final String ORGANIZATION_DEPARTENT = "organization_departent";
    public static final String IS_ORGANIZATION_UPDATE = "is_organization_update";//组织架构是否更新
    public static final String APP_START = "app_start";
    public static final String ACTION_USER_VERSION = "update_user_version_info";
    public static final String TOKEN_START = "token_start";
    public static final String TOKEN_UPDATE = "token_update";

    public static final int TYPE_SELECT_SINGLE = 1;
    public static final int TYPE_SELECT_MULTUI = 0;
    public static final int TYPE_SELECT_EDT = 2;

    public static final int TYPE_SHOW_USER = 1;
    public static final int TYPE_SHOW_DEPT_USER = 0;
    public static final int FROMPAGE_ATTENDANCE = 101;

    public static final int REQUEST_CODE = 100;
    public static final int MSG_WHAT_GONG = 120;
    public static final int MSG_WHAT_VISIBLE = 130;
    public static final int MSG_WHAT_DIALOG = 140;
    public static final int MSG_WHAT_HIDEDIALOG = 150;

    public static final int REQUEST_CODE_CUSTOMER = 101;
    public static final int REQUEST_CODE_STAGE = 102;
    public static final int REQUEST_CODE_PRODUCT = 103;
    public static final int REQUEST_CODE_TYPE = 104;
    public static final int REQUEST_CODE_SOURCE = 105;
    public static final int REQUEST_EDIT = 106;


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
    /**
     * 操作状态
     */
    public static final int REQUSET_STATUS = 302;

    protected ExtraAndResult() {
        throw new UnsupportedOperationException(); // 防止子类调用
    }
}
