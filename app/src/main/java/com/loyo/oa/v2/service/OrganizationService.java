package com.loyo.oa.v2.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.loyo.oa.v2.activityui.customer.bean.Department;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.db.OrganizationManager;
import com.loyo.oa.v2.point.IUser;
import com.loyo.oa.v2.tool.ListUtil;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.ArrayList;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class OrganizationService extends IntentService {

    private static boolean _isFetchingOrganziationData = false;

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
        _isFetchingOrganziationData = true;

        ArrayList<Department> lstDepartment_current = RestAdapterFactory.getInstance().build(FinalVariables.GET_ORGANIZATION)
                .create(IUser.class).getOrganization();
        OrganizationManager.shareManager().loadOrganizitionDataToMemoryCache();

        if (!ListUtil.IsEmpty(lstDepartment_current)) {



            OrganizationManager.clearOrganizationData();
            OrganizationManager.shareManager().saveOrganizitionToDB(lstDepartment_current);


            long start1 = System.currentTimeMillis();
            OrganizationManager.shareManager().loadOrganizitionDataToMemoryCache();

            long end1 = System.currentTimeMillis();

            Log.v("timetrack", "getOrganization = " + (end1-start1) + " ms");

            Intent it = new Intent("com.loyo.oa.v2.ORGANIZATION_UPDATED");
            sendBroadcast(it);


            LogUtil.d("更新 组织《《《《《《《《《《《《《《《《》》》》》》》》》》》 架构 json：完成");
        } else {
            LogUtil.d("更新 组织 架构  失败");
        }

        _isFetchingOrganziationData = false;
    }

    public static boolean isFetchingOrganziationData(){
        return _isFetchingOrganziationData;
    }
}
