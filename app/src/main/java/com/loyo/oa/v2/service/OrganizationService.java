package com.loyo.oa.v2.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.loyo.oa.v2.activityui.customer.model.Department;
import com.loyo.oa.v2.db.OrganizationManager;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.tool.ListUtil;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.user.api.UserService;

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
        if (_isFetchingOrganziationData) {
            return;
        }
        _isFetchingOrganziationData = true;
        UserService.asynGetOrganization()
                .subscribe(new DefaultLoyoSubscriber<ArrayList<Department>>() {
                    @Override
                    public void onError(Throwable e) {
                        _isFetchingOrganziationData = false;
                    }

                    @Override
                    public void onNext(ArrayList<Department> departments) {
                        if (!ListUtil.IsEmpty(departments)) {
                            OrganizationManager.clearOrganizationData();
                            OrganizationManager.shareManager().saveOrganizitionToDB(departments);
                            OrganizationManager.shareManager().loadOrganizitionDataToMemoryCache();
                            Intent it = new Intent("com.loyo.oa.v2.ORGANIZATION_UPDATED");
                            sendBroadcast(it);
                        } else {
                            LogUtil.d("更新 组织 架构  失败");
                        }

                        _isFetchingOrganziationData = false;
                    }
                });

        OrganizationManager.shareManager().loadOrganizitionDataToMemoryCache();
    }

    public static boolean isFetchingOrganziationData(){
        return _isFetchingOrganziationData;
    }
}
