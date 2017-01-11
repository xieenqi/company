package com.loyo.oa.v2.service;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.loyo.oa.v2.BuildConfig;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.home.api.HomeService;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.LoyoErrorChecker;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.UpdateTipActivity;
import com.loyo.oa.v2.tool.Utils;

import java.io.File;
import java.io.Serializable;

/**
 * 版本
 */
public class CheckUpdateService extends Service {

    public static final int PARAM_STOP_SELF = 2;
    public static final int PARAM_START_DOWNLOAD = 3;

    boolean isChecking = false, isToast = false;
    CompleteReceiver completeReceiver;
    DownloadManager downloadManager;
    long enqueue = 0;
    UpdateInfo mUpdateInfo;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        completeReceiver = new CompleteReceiver();
        registerReceiver(completeReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //检查更新时间
        if (intent != null && intent.getExtras() != null && intent.getExtras().containsKey("EXTRA_TOAST")) {
            isToast = intent.getBooleanExtra("EXTRA_TOAST", false);
        }

        int param = intent.getIntExtra("data", 0);
        if (param == PARAM_START_DOWNLOAD && mUpdateInfo != null) {
            downloadApp();
            return START_REDELIVER_INTENT;
        } else if (param == PARAM_STOP_SELF) {
            stopSelf();
            return START_REDELIVER_INTENT;
        }
        checkUpdate();
        return START_REDELIVER_INTENT;
    }

    void downloadApp() {

        if (enqueue == 0) {
            Global.Toast("WIFI环境,正在更新最新版...");
            LogUtil.d("版本更新地址:" + mUpdateInfo.apkUrl);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(mUpdateInfo.apkUrl))
                    .setTitle(getResources().getString(R.string.app_name))
                    .setDescription("下载" + mUpdateInfo.versionName)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, mUpdateInfo.apkName())
                    .setVisibleInDownloadsUi(true);

            try {
                enqueue = downloadManager.enqueue(request);
            } catch (Exception ex) {
                LogUtil.d("下载异常抛出");
                Global.ProcException(ex);
            }
        }
    }

    @Override
    public void onDestroy() {
        if (completeReceiver != null) {
            unregisterReceiver(completeReceiver);
        }
        super.onDestroy();
    }

    private void checkUpdate() {
        if (isChecking) {
            stopSelf();
            return;
        }

        isChecking = true;

        //应该仅在wifi下升级
        if (!Global.isConnected()) {
            if (isToast) {
                Toast.makeText(this, "没有网络连接，仅在有WiFi升级", Toast.LENGTH_SHORT).show();
            }
            isChecking = false;
            stopSelf();
            return;
        }


//        RestAdapterFactory.getInstance().build(FinalVariables.URL_CHECK_UPDATE).create(IMain.class).checkUpdate(new RCallback<UpdateInfo>() {
//            @Override
//            public void success(UpdateInfo updateInfo, Response response) {
////                HttpErrorCheck.checkResponse(response);
//                if (null == updateInfo) {
//                    LogUtil.d(" 版本信息为空 ");
//                    return;
//                }
//                mUpdateInfo = updateInfo;
//                LogUtil.dll("版本更新信息:" + MainApp.gson.toJson(updateInfo));
//                try {
//                    if (updateInfo.versionCode > Global.getVersion()) {
//                        //有新版本
//                        MainApp.getMainApp().hasNewVersion = true;
//                        if (updateInfo.autoUpdate && Utils.getNetworkType(MainApp.getMainApp()).contains("WIFI")) {//后台自动更新
//                            deleteFile();
//                            downloadApp();
//                        } else if (updateInfo.forceUpdate || isToast) {//弹窗提示更新
//                            deleteFile();
//                            Intent intentUpdateTipActivity = new Intent(CheckUpdateService.this, UpdateTipActivity.class);
//                            intentUpdateTipActivity.putExtra("data", updateInfo);
//                            intentUpdateTipActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intentUpdateTipActivity);
//                        } else {
//                            stopSelf();
//                        }
//
//                        //通知侧边栏有新版本
//                        Intent in = new Intent();
//                        in.setAction(ExtraAndResult.ACTION_USER_VERSION);
//                        in.putExtra(ExtraAndResult.EXTRA_DATA, "version");
//                        LocalBroadcastManager.getInstance(CheckUpdateService.this).sendBroadcast(in);
//                    } else {
//                        if (isToast) {
//                            Global.Toast("你的软件已经是最新版本");
//                        }
//                        stopSelf();
//                    }
//                    isChecking = false;
//                } catch (NullPointerException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
////                HttpErrorCheck.checkError(error);
//                super.failure(error);
//                Global.ProcException(error);
//                stopSelf();
//            }
//        });

        HomeService.checkUpdate().subscribe(new DefaultLoyoSubscriber<UpdateInfo>(LoyoErrorChecker.SILENCE) {
            @Override
            public void onNext(UpdateInfo updateInfo) {
                if (null == updateInfo) {
                    LogUtil.d(" 版本信息为空 ");
                    return;
                }
                mUpdateInfo = updateInfo;
                LogUtil.dll("版本更新信息:" + MainApp.gson.toJson(updateInfo));
                try {
                    if (updateInfo.versionCode > Global.getVersion()) {
                        //有新版本
                        MainApp.getMainApp().hasNewVersion = true;
                        if (updateInfo.autoUpdate && Utils.getNetworkType(MainApp.getMainApp()).contains("WIFI")) {//后台自动更新
                            deleteFile();
                            downloadApp();
                        } else if (updateInfo.forceUpdate || isToast) {//弹窗提示更新
                            deleteFile();
                            Intent intentUpdateTipActivity = new Intent(CheckUpdateService.this, UpdateTipActivity.class);
                            intentUpdateTipActivity.putExtra("data", updateInfo);
                            intentUpdateTipActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intentUpdateTipActivity);
                        } else {
                            stopSelf();
                        }

                        //通知侧边栏有新版本
                        Intent in = new Intent();
                        in.setAction(ExtraAndResult.ACTION_USER_VERSION);
                        in.putExtra(ExtraAndResult.EXTRA_DATA, "version");
                        LocalBroadcastManager.getInstance(CheckUpdateService.this).sendBroadcast(in);
                    } else {
                        if (isToast) {
                            Global.Toast("你的软件已经是最新版本");
                        }
                        stopSelf();
                    }
                    isChecking = false;
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                stopSelf();
            }
        });
    }

    /**
     * 删除APK
     */
    private void deleteFile() {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(path, mUpdateInfo.apkName());
        if (file.exists()) {
            file.delete();
        }
    }

    private void installApk() {
        File apkfile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), mUpdateInfo.apkName());
        if (!apkfile.exists()) {
            stopSelf();
            return;
        }

        /**
         * Android 7.0 自动升级bug
         * http://stackoverflow.com/questions/39147608/android-install-apk-with-intent-view-action-not-working-with-file-provider
         */

        if (Build.VERSION.SDK_INT >= 24 /*Build.VERSION_CODES.N*/) {
            Uri apkUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", apkfile);
            Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            intent.setData(apkUri);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        } else {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
            startActivity(i);
        }



        stopSelf();
    }

    class CompleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(enqueue);
            Cursor cursor = downloadManager.query(query);
            if (cursor.moveToFirst()) {
                int culumnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                if (DownloadManager.STATUS_SUCCESSFUL == cursor.getInt(culumnIndex)) {
                    installApk();
                }
            }

            stopSelf();
        }
    }

    public static class UpdateInfo implements Serializable {

        public String versionName;
        public String appDescription;
        public String apkUrl;
        public boolean forceUpdate;
        public boolean autoUpdate;
        public int versionCode;

        public String apkName() {
            return apkUrl.substring(apkUrl.lastIndexOf("/") + 1);
        }
    }

}
