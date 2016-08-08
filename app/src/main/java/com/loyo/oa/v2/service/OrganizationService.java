package com.loyo.oa.v2.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import com.loyo.oa.v2.activityui.customer.bean.Department;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.db.OrganizationManager;
import com.loyo.oa.v2.point.IUser;
import com.loyo.oa.v2.tool.ListUtil;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.SharedUtil;

import java.util.ArrayList;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class OrganizationService extends IntentService {

    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FETCH_ALL = "com.loyo.oa.v2.service.action.FETCH_ALL";

    private static final String EXTRA_PARAM = "com.loyo.oa.v2.service.extra.PARAM";

    public OrganizationService() {
        super("OrganizationService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFetchAll(Context context) {
        Intent intent = new Intent(context, OrganizationService.class);
        intent.setAction(ACTION_FETCH_ALL);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FETCH_ALL.equals(action)) {
                //final String param1 = intent.getStringExtra(EXTRA_PARAM);
                handleActionFetchAll();
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFetchAll() {
        getOrganization();
    }

    /**
     * 后台 更新 组织架构
     */
    void getOrganization() {
        ArrayList<Department> lstDepartment_current = RestAdapterFactory.getInstance().build(FinalVariables.GET_ORGANIZATION)
                .create(IUser.class).getOrganization();

        if (!ListUtil.IsEmpty(lstDepartment_current)) {

            OrganizationManager.shareManager().saveOrganizitionToDB(lstDepartment_current);
            OrganizationManager.shareManager().loadOrganizitionDataToCache();


            LogUtil.d("更新 组织《《《《《《《《《《《《《《《《》》》》》》》》》》》 架构 json：完成");
        } else {
            LogUtil.d("更新 组织 架构  失败");
        }
    }
}
