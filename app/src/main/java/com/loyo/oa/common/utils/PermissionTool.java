package com.loyo.oa.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.loyo.oa.v2.application.MainApp;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * 权限申请的工具类,用来判断是否具有一个权限,申请被拒绝的时候,弹出提示,解释需要权限的原因
 * 用法:
 * 1、在需要的地方,requestPermission()的方法;
 * 2、在onRequestPermissionsResult()里面,调用requestPermissionsResult方法,自动判断授权状态回调,执行操作
 * Created by jie on 16/12/17.
 */
public class PermissionTool {

    /**
     * 申请单个权限许可
     *
     * @param activityOrFragment 申请权限的fragment或者是activity
     * @param permissions        需要的权限,对申请的权限都解释说明一下
     * @param rationale          如果用户永久拒绝了权限,弹出解释的内容
     * @param requestCode        申请编码,在activity或者fragment中onRequestPermissionsResult方法会返回,用来每一次申请
     * @return 全部true, 不具有, 那就返回false
     */
    public static boolean requestPermission(Object activityOrFragment, String permissions, String rationale, int requestCode) {
        return requestPermission(activityOrFragment, new String[]{permissions}, rationale, requestCode);
    }

    /**
     * 检查权限许可,可以传入一组权限,如果全部具有,那就返回true,如果有一个或多个不具有,那就返回false,
     * 并且弹出提示,向用户申请权限,如果用户拒绝了,会弹出提示,向用户解释为什么需要权限。
     * [注意] 如果是没有权限,这里是不阻塞的,是直接返回false,但是会会弹出提示申请
     *
     * @param activityOrFragment 申请权限的fragment或者是activity
     * @param permissions        需要的权限,对申请的权限都解释说明一下
     * @param rationale          如果用户永久拒绝了权限,弹出解释的内容
     * @param requestCode        申请编码,在activity或者fragment中onRequestPermissionsResult方法会返回,用来每一次申请
     * @return 全部具有, 那就返回true, 如果有一个或多个不具有, 那就返回false
     */
    public static boolean requestPermission(Object activityOrFragment, String[] permissions, String rationale, int requestCode) {
        Activity activity = null;
        Fragment fragment = null;
        Context context = null;
        if (activityOrFragment instanceof Activity) {
            activity = (Activity) activityOrFragment;
            context = activity;
        } else if (activityOrFragment instanceof Fragment) {
            fragment = (Fragment) activityOrFragment;
            context = fragment.getActivity();
        } else {
//            throw new UnsupportedOperationException("申请权限必须传入activity或者fragment");
            Log.e("权限申请","requestPermission: 申请权限必须传入activity或者fragment");
            return false;
        }
        int i = 0;
        String[] needRequest = new String[permissions.length];//当权限被拒绝时候,存一下下标,表示不具有哪些权限
        //检查是否具有权限
        for (String permission : permissions) {
            int checkResult = context.getPackageManager().checkPermission(permission, context.getPackageName());
            if (PackageManager.PERMISSION_GRANTED != checkResult) {
                //没有这个权限
                needRequest[i++] = permission;
            }
        }
        if (0 == i) {
            return true;//表示上述权限,全部具有,直接返回true
        } else {
            boolean showRationale = false;
            //没有一些权限,开始走申请流程
            if (null != activity) {
                //activity中申请权限
                for (int j = 0; j < i; j++) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(activity, needRequest[j])) {//如果用户永久拒绝了某个权限,就不能弹出提示,要解释原因
                        showRationale = true;
                        break;
                    }
                }
            } else {
                //fragment中申请权限
                for (int j = 0; j < i; j++) {
                    if (fragment.shouldShowRequestPermissionRationale(needRequest[j])) {//如果用户永久拒绝了某个权限,就不能弹出提示,要解释原因
                        showRationale = true;
                        break;
                    }
                }
            }
            if (showRationale) {
                Log.i("", "requestPermission: 弹出提示框框");
                //弹出提示,向用户解释申请许可的原因
                final Context finalContext = context;
                new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                        .setTitleText("权限申请")
                        .setContentText(rationale + "\n\n" + "请在”设置”>“应用”>“权限”中配置权限")//解释原因
                        .setCancelText("取消")
                        .setConfirmText("开启")
                        .showCancelButton(true)
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                //取消
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                toAppDetailSetting(finalContext);
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();
                
            } else {
                //可以直接弹出申请提示框
                if (null != activity) {
                    //activity中申请权限
                    ActivityCompat.requestPermissions(activity, needRequest, requestCode);

                } else {
                    //fragment中申请权限
                    fragment.requestPermissions(needRequest, requestCode);
                }
            }
            return false;
        }
    }

    /**
     * 申请权限,用户操作以后的回调,需要自行根据requestCode调用
     * 需要把本方法放在onRequestPermissionsResult里面,就可以了。
     *
     * 如果申请了一组权限,用户拒绝了其中一个,就认为是申请失败,毁掉用callback.fail()
     *
     * 你可以自行在activity或者fragment的onRequestPermissionsResult会滴哦啊方法中处理;
     *
     * @param permissions
     * @param grantResults
     * @param callBack
     */
    public static void requestPermissionsResult(String[] permissions, int[] grantResults, PermissionsResultCallBack callBack) {
        int len=permissions.length;
        for (int i = 0; i < len; i++) {
            Log.i("tttt", "requestPermissionsResult: permission:"+permissions[i]+",grantResult:"+grantResults[i]+",ok is:"+PackageManager.PERMISSION_GRANTED);
            if(null!=permissions[i]){
                if (PackageManager.PERMISSION_GRANTED != grantResults[i]) {
                    callBack.fail();
                    return;
                }
            }
        }
        callBack.success();
    }

    /**
     * 跳转到应用详情页面,主要是用来引导用户手动打开权限
     */
    private static void toAppDetailSetting(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", MainApp.getMainApp().getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", MainApp.getMainApp().getPackageName());
        }
        context.startActivity(localIntent);

    }

    //处理申请结果的回调
    public interface PermissionsResultCallBack {
        /**
         * 用户给予了权限,具有权限时候,调用本方法,完成没有完成的操作
         */
        void success();

        /**
         * 用户拒绝授权以后,调用本方法
         */
        void fail();
    }
}
