package com.loyo.oa.v2.permission;

import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.activityui.customer.model.MembersRoot;

import java.util.EnumSet;
import java.util.HashMap;

import static com.loyo.oa.v2.application.MainApp.user;
import static com.loyo.oa.v2.permission.CustomerAction.ATTACHMENT_ADD;
import static com.loyo.oa.v2.permission.CustomerAction.CONTACT_ADD;
import static com.loyo.oa.v2.permission.CustomerAction.DELETE;
import static com.loyo.oa.v2.permission.CustomerAction.DUMP;
import static com.loyo.oa.v2.permission.CustomerAction.EDIT;
import static com.loyo.oa.v2.permission.CustomerAction.EXPORT;
import static com.loyo.oa.v2.permission.CustomerAction.FOLLOWUP_ADD;
import static com.loyo.oa.v2.permission.CustomerAction.IMPORT;
import static com.loyo.oa.v2.permission.CustomerAction.ORDER_ADD;
import static com.loyo.oa.v2.permission.CustomerAction.PARTICIPATED_PERSON_CHANGE;
import static com.loyo.oa.v2.permission.CustomerAction.PICK_IN;
import static com.loyo.oa.v2.permission.CustomerAction.PREVIEW;
import static com.loyo.oa.v2.permission.CustomerAction.REMINDER_ADD;
import static com.loyo.oa.v2.permission.CustomerAction.RESPONSIBLE_PERSON_CHANGE;
import static com.loyo.oa.v2.permission.CustomerAction.SALE_OPPORTUNITY_ADD;
import static com.loyo.oa.v2.permission.CustomerAction.TASK_ADD;
import static com.loyo.oa.v2.permission.CustomerAction.VISIT;
import static com.loyo.oa.v2.permission.CustomerAuthority.INVOLVED_VISITOR_LEVEL;
import static com.loyo.oa.v2.permission.CustomerAuthority.PARTICIPATED_PERSON_LEVEL;
import static com.loyo.oa.v2.permission.CustomerAuthority.RESPONSIBLE_PERSON_LEVEL;

/**
 * Created by EthanGong on 2016/11/28.
 */
public class PermissionManager {
    private static PermissionManager ourInstance = new PermissionManager();

    public static PermissionManager getInstance() {
        return ourInstance;
    }

    private HashMap<String, Permission> data = new HashMap<>();

    private MembersRoot crmConfigMemberEdit;

    private PermissionManager() {
    }

    public PermissionManager init(HashMap<String, Permission> map) {
        if (map != null) {
            data = map;
        }
        return this;
    }

    public PermissionManager loadCRMConfig(MembersRoot crmConfigMemberEdit) {
        if (crmConfigMemberEdit != null) {
            this.crmConfigMemberEdit = crmConfigMemberEdit;
        }
        return this;
    }

    public boolean hasPermission(@BusinessOperation.Type String module) {

        if (hasSuperPriority()) {
            return true;
        }

        Permission permission = data.get(module);
        if (permission == null || ! permission.isEnable()) {
            return false;
        }

        return true;
    }

    public boolean nonePermission(@BusinessOperation.Type String module) {
        return ! hasPermission(module);
    }


    public boolean teamPermission(@BusinessOperation.Type String module) {
        if (hasSuperPriority()) {
            return true;
        }

        Permission permission = data.get(module);
        if (permission == null || ! permission.isEnable()) {
            return false;
        }
        else if (permission.dataRange >= Permission.PERSONAL) {
            return false;
        }

        return true;
    }

    public int dataRange(@BusinessOperation.Type String module) {

        if (hasSuperPriority()) {
            return Permission.COMPANY;
        }

        Permission permission = data.get(module);
        if (permission == null) {
            return Permission.NONE;
        }
        else {
            return permission.dataRange;
        }
    }

    /* 超级管理员权限 */
    public boolean hasSuperPriority() {
        if (user != null) {
            return user.isSuperUser;
        }
        return false;
    }

    private static EnumSet[][] TABLE;
    /* 静态块，初始化数据 */
    {
        EnumSet[][] table = {
                {
                        /* 负责人level, 正常客户 */
                        EnumSet.of(PREVIEW,      EDIT,         PARTICIPATED_PERSON_CHANGE,
                                   CONTACT_ADD,  FOLLOWUP_ADD, SALE_OPPORTUNITY_ADD,
                                   ORDER_ADD,    TASK_ADD,     ATTACHMENT_ADD,
                                   REMINDER_ADD, VISIT,        PICK_IN,
                                   DUMP,         IMPORT,       EXPORT,
                                   RESPONSIBLE_PERSON_CHANGE,  DELETE),
                        /* 负责人level, 公海客户 */
                        EnumSet.of(PREVIEW,      PICK_IN,      DELETE),
                        /* 负责人level, 回收客户 */
                        EnumSet.of(DELETE)
                },
                {
                        /* 参与人level, 正常客户 */
                        EnumSet.of(PREVIEW,
                                   CONTACT_ADD,  FOLLOWUP_ADD, SALE_OPPORTUNITY_ADD,
                                   ORDER_ADD,    TASK_ADD,     ATTACHMENT_ADD,
                                   REMINDER_ADD, VISIT,        EDIT),
                        /* 参与人level, 公海客户 */
                        EnumSet.of(PREVIEW,      PICK_IN,      DELETE),
                        /* 参与人level, 回收客户 */
                        EnumSet.of(DELETE)
                },
                {
                        /* 相关level, 正常客户 */
                        EnumSet.of(PREVIEW),
                        /* 相关level, 公海客户 */
                        EnumSet.of(PREVIEW,      PICK_IN,      DELETE),
                        /* 相关level, 回收客户 */
                        EnumSet.of(DELETE)
                },
                {
                        /* 功能模块关闭, 正常客户 */
                        EnumSet.noneOf(CustomerAction.class),
                        /* 功能模块关闭, 公海客户 */
                        EnumSet.noneOf(CustomerAction.class),
                        /* 功能模块关闭, 回收客户 */
                        EnumSet.noneOf(CustomerAction.class)
                }
        };
        TABLE = table;
    }

    public boolean hasCustomerAuthority(@Customer.RelationState int relationState,
                                        @Customer.CustomerState int state,
                                        CustomerAction action) {

        /* 超级管理员 */
        if (hasSuperPriority()) {
            return true;
        }

        /* 功能模块关闭 */
        if (!hasPermission(BusinessOperation.CUSTOMER_MANAGEMENT)) {
            return false;
        }

        /* 功能模块关闭 */
        @BusinessOperation.Type String operation = action.bizOp;
        if (!BusinessOperation.DEFAULT.equals(operation)
                && !hasPermission(operation)) {
            return false;
        }

        /* 客户状态不明 */
        if (state < Customer.NormalCustomer || state > Customer.RecycledCustomer) { //
            return false;
        }

        CustomerAuthority authorityLevel = getAuthorityLevel(relationState);
        EnumSet<CustomerAction> set = TABLE[authorityLevel.ordinal()][state-1];
        if (set.contains(action)) {

            // specially 参与人的编辑权限，判断CRM config
            if (CustomerAction.EDIT== action && authorityLevel == PARTICIPATED_PERSON_LEVEL) {
                return crmConfigMemberEdit!=null&&crmConfigMemberEdit.enabled();
            }

            return true;
        }

        return false;
    }

    public CustomerAuthority getAuthorityLevel(@Customer.RelationState int relationState) {

        CustomerAuthority level;
        switch(relationState) {
            case Customer.RelationResponsible:
                level = RESPONSIBLE_PERSON_LEVEL;
                break;
            case Customer.RelationParticipated:
                level = PARTICIPATED_PERSON_LEVEL;
                break;
            case Customer.RelationInvolved:
                level = INVOLVED_VISITOR_LEVEL;
                break;
            default:
                level = INVOLVED_VISITOR_LEVEL;
                break;
        }
        return level;
    }
}
