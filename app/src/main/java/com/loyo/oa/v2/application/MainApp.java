package com.loyo.oa.v2.application;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.multidex.MultiDex;
import android.text.Editable;
import android.util.DisplayMetrics;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.photo.PhotoPicker;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.model.Department;
import com.loyo.oa.v2.activityui.customer.model.Industry;
import com.loyo.oa.v2.activityui.other.model.CellInfo;
import com.loyo.oa.v2.activityui.other.model.User;
import com.loyo.oa.v2.activityui.other.model.UserGroupData;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.http.ServerAPI;
import com.loyo.oa.v2.customermanagement.api.CustomerService;
import com.loyo.oa.v2.db.DBManager;
import com.loyo.oa.v2.db.OrganizationManager;
import com.loyo.oa.v2.jpush.HttpJpushNotification;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.LoyoErrorChecker;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.ExitActivity;
import com.loyo.oa.v2.tool.GlideManager;
import com.loyo.oa.v2.tool.ImageInfo;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.SharedUtil;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.Utils;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.jpush.android.api.JPushInterface;


public class MainApp extends Application {

    public static final String TAG = "com.loyo.oa.v2app";
    public static final int ENTER_TYPE_TOP = 1;
    public static final int ENTER_TYPE_BUTTOM = 2;
    public static final int ENTER_TYPE_LEFT = 3;
    public static final int ENTER_TYPE_RIGHT = 4;
    public static final int ENTER_TYPE_ZOOM_OUT = 5;
    public static final int ENTER_TYPE_ZOOM_IN = 6;
    public static final int GET_IMG = 1013;
    public static final int PHOTO = 1011;
    public static final int PICTURE = 1012;

    public static DisplayImageOptions options_3;
    private static MainApp mainApp;
    public static Gson gson;
    public static HttpJpushNotification jpushData;
    public static int permissionPage;

    public DisplayImageOptions options_rounded;
    private AnimationDrawable animationDrawable;
    public LogUtil logUtil;

    MainApplicationHandler handler;

    public double longitude = -1;
    public double latitude = -1;
    public String cityCode;
    public String address;
    public String message;
    public String region;//地区
    public static boolean isQQLogin = false;
    public boolean hasNewVersion = false;

    public ScaleAnimation animShow;  //显示动画
    public ScaleAnimation animHide;  //隐藏动画

    private List<Integer> menuIds = new ArrayList<>();
    private ClipboardManager cmb;

    //-------这些数据需要保存在本地-------------
    //下属
    public static ArrayList<UserGroupData> lstUserGroupData;
    public static ArrayList<Department> lstDepartment;//组织架构 的缓存

    static String token;
    public static User user;//InitDataService 在这里负值
    public CellInfo cellInfo;

    /* app是否在前台 */
    private static boolean isActive;
    ActionMode actionMode;


    public static String getToken() {
        if (!StringUtil.isEmpty(token)) {
            return token;
        }
        return SharedUtil.get(getMainApp().getBaseContext(), FinalVariables.TOKEN);
    }

    public static void setToken(String _token) {
        JPushInterface.resumePush(mainApp);
        token = _token;
        SharedUtil.put(getMainApp().getBaseContext(), FinalVariables.TOKEN, token);
        SharedUtil.put(getMainApp().getBaseContext(), ExtraAndResult.TOKEN_START, System.currentTimeMillis() + "");
    }


    public ArrayList<Industry> mIndustries = new ArrayList<>();

    void loadIndustryCodeTable() {

        CustomerService.getIndustry()
                .subscribe(new DefaultLoyoSubscriber<ArrayList<Industry>>(LoyoErrorChecker.SILENCE) {
                    public void onNext(ArrayList<Industry> industryArrayList) {
                        mIndustries = industryArrayList;
                    }
                });

    }
    //-------这些数据需要保存在本地-------------

    @Override
    public void onCreate() {
        super.onCreate();
        addActivityLifecycleCallback();
        mainApp = this;
        init();
        loadIndustryCodeTable();
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        GlideManager.getInstance().initWithContext(getApplicationContext());
        initLoadingConfig();
        menuIds.add(R.id.copy);
        menuIds.add(R.id.paste);
    }

    private void initLoadingConfig() {
        LoadingLayout.getConfig()
                .setErrorText("出错啦~请稍后重试！")
                .setEmptyText("暂无数据!")
                .setNoNetworkText("网络不给力!")
//                .setErrorImage(R.drawable.define_error)
                .setEmptyImage(R.drawable.define_empty)
                .setNoNetworkImage(R.drawable.define_nonetwork)
                .setAllTipTextColor(R.color.text99)
                .setAllTipTextSize(16)
                .setReloadButtonText("重试")
                .setReloadButtonTextColor(R.color.title_bg1)
                .setReloadButtonBackgroundResource(R.drawable.retage_bule_none)
                .setReloadButtonTextSize(16);
    }


    private void addActivityLifecycleCallback() {

        this.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                //app 从后台唤醒，进入前台
                if (!isActive) {
                    isActive = true;
                }
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                if (!isAppOnForeground()) {
                    isActive = false;
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    public boolean isAppOnForeground() {
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = getApplicationContext().getPackageName();

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }

        return false;
    }

    /**
     * 启动动画
     */
    public void startAnim(TextView textView) {
        LogUtil.dee("startAnim:::" + textView.getBackground());
        animationDrawable = (AnimationDrawable) textView.getBackground();
        animationDrawable.start();
    }

    /**
     * 停止动画
     */
    public void stopAnim(TextView textView) {
        LogUtil.dee("stopAnim");
        animationDrawable = (AnimationDrawable) textView.getBackground();
        if (animationDrawable.isRunning()) {
            animationDrawable.stop();
        }
        animationDrawable.selectDrawable(0);
    }

    /**
     * 设置APP语言，地区等
     */
    public void switchLanguage() {
        Locale.setDefault(Locale.CHINA);
        Resources resources = getResources();// 获得res资源对象
        Configuration config = resources.getConfiguration();// 获得设置对象
        DisplayMetrics dm = resources.getDisplayMetrics();// 获得屏幕参数：主要是分辨率，像素等。
        config.locale = Locale.SIMPLIFIED_CHINESE; // 简体中文
        resources.updateConfiguration(config, dm);
    }

    void init() {

        animShow = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animShow.setDuration(120);//设置动画持续时间

        animHide = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animHide.setDuration(120);//设置动画持续时间

        logUtil = LogUtil.lLog();
        handler = new MainApplicationHandler();
        ServerAPI.init();
        initImageLoader(getApplicationContext());
        init_DisplayImageOptions();
        switchLanguage();

        cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

//        df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());//设置日期格式
//        df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());//设置日期格式
//        df3 = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault());//设置日期格式
//        df4 = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());//设置日期格式
//        df5 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());//设置日期格式
//        df6 = new SimpleDateFormat("HH:mm", Locale.getDefault());//设置日期格式
//        df7 = new SimpleDateFormat("MM.dd", Locale.getDefault());//设置日期格式
//        df8 = new SimpleDateFormat("yyyy.MM", Locale.getDefault());//设置日期格式
//        df9 = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());//设置日期格式
//        df10 = new SimpleDateFormat("yyyy年M月dd日 HH:mm", Locale.getDefault());//设置日期格式
//        df11 = new SimpleDateFormat("dd日", Locale.getDefault());//设置日期格式
//        df12 = new SimpleDateFormat("yyyy年M月dd日", Locale.getDefault());//设置日期格式
//        df13 = new SimpleDateFormat("yyyy年M月", Locale.getDefault());//设置日期格式
//        df14 = new SimpleDateFormat("yyyy年M月dd日 HH:mm:ss", Locale.getDefault());//设置日期格式
//        df15 = new SimpleDateFormat("yyyy.MM", Locale.getDefault());//设置日期格式
//        df_api = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());//设置日期格式
//        df_api_get = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());//设置日期格式
//        df_api_get2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+08:00", Locale.getDefault());//设置日期格式，2015-01-15T05:30:00+08:00
        gson = new Gson();
        Utils.openGPS(this);
        DBManager.init(this);
        OrganizationManager.init(this);
    }


    /**
     * 设置缓存的组织架构数据
     */

    void init_DisplayImageOptions() {
        options_rounded = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).
                considerExifParams(true).displayer(new RoundedBitmapDisplayer(20)).build();

        options_3 = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).build();
    }

    public static void initImageLoader(Context context) {

        File cacheDir = StorageUtils.getOwnCacheDirectory(mainApp.getBaseContext(), "imageloader/Cache");

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.img_default_user)// 空uri时的默认图片
                .showImageOnFail(R.drawable.default_image)// 加载失败时的默认图片
                .cacheInMemory(true)// 是否缓存到内存
                .cacheOnDisc(true)// 是否缓存到磁盘
                .bitmapConfig(Bitmap.Config.RGB_565)// 图片格式比RGB_888少消耗2倍内存RGB_565
                .imageScaleType(ImageScaleType.EXACTLY)// 图片缩放方式
                .build();

        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(context).
                defaultDisplayImageOptions(defaultOptions).
                threadPriority(Thread.NORM_PRIORITY - 2).
                denyCacheImageMultipleSizesInMemory().
                diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .discCache(new UnlimitedDiscCache(cacheDir))//自定义缓存路径
//                        .discCache(new UnlimitedDiscCache(new File("CRMcacheDir")))
//                         .diskCacheSize(50 * 1024 * 1024) // 50 Mb
                .memoryCacheExtraOptions(1080, 1920).tasksProcessingOrder(QueueProcessingType.LIFO);

        if (Config_project.is_developer_mode) {
            builder.writeDebugLogs();
        }

        ImageLoaderConfiguration config = builder.build();
        ImageLoader.getInstance().init(config);
    }

    public static MainApp getMainApp() {
        return mainApp;
    }

    public int diptoPx(float dipValue) {

        float density = getResources().getDisplayMetrics().density;
        return (int) (dipValue * density + 0.5f);
    }

    //    public int pxtoDip(float pxValue) {
    //
    //        float density = getResources().getDisplayMetrics().density;
    //        Log.d(tag, "density:" + density);
    //        return (int) (pxValue / density + 0.5f);
    //    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @return
     */
    public int pxTosp(float pxValue) {

        float fontScale = getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @return
     */
    public int spTopx(float spValue) {

        float fontScale = getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * @return p.x=宽；p.y=高
     */
    //    public Point getWindowWH() {
    //
    //        WindowManager wm = (WindowManager) this
    //                .getSystemService(Context.WINDOW_SERVICE);
    //        Point p = new Point();
    //        wm.getDefaultDisplay().getSize(p);
    //
    //        Log.d(tag, "p.x:" + p.x);
    //        Log.d(tag, "p.y:" + p.y);
    //        return p;
    //    }


    /**
     * 跳转相册，公用方法
     */
    public void startSelectImage(Activity mActivity, ArrayList<ImageInfo> pickPhots) {
        PhotoPicker.builder()
                .setPhotoCount((9 - pickPhots.size()))
                .setShowCamera(true)
                .setPreviewEnabled(false)
                .start(mActivity);
    }

    /**
     * @param activity
     * @param cls
     * @param enterType
     * @param isFinish
     */
    public void startActivity(Activity activity, Class<?> cls, int enterType, Boolean isFinish, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        //        if (Config_project.is_developer_mode) {
        //            try {
        //                Method m = StrictMode.class.getMethod("incrementExpectedActivityCount", Class.class);
        //                m.invoke(null, cls);
        //            } catch (Exception e) {
        //                Global.ProcException(e);
        //            }
        //            System.gc();
        //        }
        activity.startActivity(intent);
        switch (enterType) {
            case ENTER_TYPE_TOP:
                activity.overridePendingTransition(R.anim.enter_toptobuttom, R.anim.exit_toptobuttom);
                break;
            case ENTER_TYPE_BUTTOM:
                activity.overridePendingTransition(R.anim.enter_buttomtotop, R.anim.exit_buttomtotop);
                break;
            case ENTER_TYPE_LEFT:
                activity.overridePendingTransition(R.anim.enter_lefttoright, R.anim.exit_lefttoright);
                break;
            case ENTER_TYPE_RIGHT:
                activity.overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
                break;
            //            case ENTER_TYPE_ZOOM_IN:
            //                activity.overridePendingTransition(R.anim.enter_zoom_in, R.anim.enter_zoom_in);
            //                break;
            //            case ENTER_TYPE_ZOOM_OUT:
            //                activity.overridePendingTransition(R.anim.enter_zoom_out, R.anim.enter_zoom_out);
            //                break;
            default:
                break;
        }

        if (isFinish) {
            Message msg = new Message();
            msg.what = MainApplicationHandler.ACTIVITY_FINISH;
            msg.obj = activity;
            //            handler.sendMessageDelayed(msg, getResources().getInteger(R.integer.animator_activity));
            handler.sendMessage(msg);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public void startActivity(Activity activity, Class<?> cls, int enterType, boolean isFinish,
                              Bundle bundle, boolean FLAG_ACTIVITY_FORWARD_RESULT) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }

        if (FLAG_ACTIVITY_FORWARD_RESULT) {
            intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        }

        //        if (Config_project.is_developer_mode) {
        //            try {
        //                Method m = StrictMode.class.getMethod("incrementExpectedActivityCount", Class.class);
        //                m.invoke(null, cls);
        //            } catch (Exception e) {
        //                Global.ProcException(e);
        //            }
        //            System.gc();
        //        }
        activity.startActivity(intent);
        switch (enterType) {
            case ENTER_TYPE_TOP:
                activity.overridePendingTransition(R.anim.enter_toptobuttom, R.anim.exit_toptobuttom);
                break;
            case ENTER_TYPE_BUTTOM:
                activity.overridePendingTransition(R.anim.enter_buttomtotop, R.anim.exit_buttomtotop);
                break;
            case ENTER_TYPE_LEFT:
                activity.overridePendingTransition(R.anim.enter_lefttoright, R.anim.exit_lefttoright);
                break;
            case ENTER_TYPE_RIGHT:
                activity.overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
                break;
            //            case ENTER_TYPE_ZOOM_IN:
            //                activity.overridePendingTransition(R.anim.enter_zoom_in, R.anim.enter_zoom_in);
            //                break;
            //            case ENTER_TYPE_ZOOM_OUT:
            //                activity.overridePendingTransition(R.anim.enter_zoom_out, R.anim.enter_zoom_out);
            //                break;
            default:
                break;
        }

        if (isFinish) {
            Message msg = new Message();
            msg.what = MainApplicationHandler.ACTIVITY_FINISH;
            msg.obj = activity;
            //            handler.sendMessageDelayed(msg, getResources().getInteger(R.integer.animator_activity));
            handler.sendMessage(msg);
        }
    }

    /**
     * 页面跳转的方式  动画
     *
     * @param activity
     * @param cls
     * @param enterType
     */
    public void startActivityForResult(Activity activity, Class<?> cls, int enterType, int requestCode, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }

        activity.startActivityForResult(intent, requestCode);
        switch (enterType) {
            case ENTER_TYPE_TOP:
                activity.overridePendingTransition(R.anim.enter_toptobuttom, R.anim.exit_toptobuttom);
                break;
            case ENTER_TYPE_BUTTOM:
                activity.overridePendingTransition(R.anim.enter_buttomtotop, R.anim.exit_buttomtotop);
                break;
            case ENTER_TYPE_LEFT:
                activity.overridePendingTransition(R.anim.enter_lefttoright, R.anim.exit_lefttoright);
                break;
            case ENTER_TYPE_RIGHT:
                activity.overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
            default:
                break;
        }
    }

    public void finishActivity(Activity activity, int enterType, int resultCode, Intent intent) {

        if (intent != null) {
            activity.setResult(resultCode, intent);
        }

        activity.finish();
        switch (enterType) {
            case ENTER_TYPE_TOP:
                activity.overridePendingTransition(R.anim.enter_toptobuttom, R.anim.exit_toptobuttom);
                break;
            case ENTER_TYPE_BUTTOM:
                activity.overridePendingTransition(R.anim.enter_buttomtotop, R.anim.exit_buttomtotop);
                break;
            case ENTER_TYPE_LEFT:
                activity.overridePendingTransition(R.anim.enter_lefttoright, R.anim.exit_lefttoright);
                break;
            case ENTER_TYPE_RIGHT:
                activity.overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
                break;
            //            case ENTER_TYPE_ZOOM_IN:
            //                activity.overridePendingTransition(R.anim.enter_zoom_in, R.anim.enter_zoom_in);
            //                break;
            //            case ENTER_TYPE_ZOOM_OUT:
            //                activity.overridePendingTransition(R.anim.enter_zoom_out, R.anim.enter_zoom_out);
            //                break;
            default:
                break;
        }
        //        if (Config_project.is_developer_mode) {
        //            Message msg = new Message();
        //            msg.what = MainApplicationHandler.GC;
        //            msg.obj = activity;
        //            handler.sendMessageDelayed(msg, getResources().getInteger(R.integer.animator_activity));
        //        }
    }

    public void toActivity(Class<?> cls) {
        Activity activity_top = ExitActivity.getInstance().getTopActivity();

        if (activity_top != null) {
            mainApp.startActivity(activity_top, cls, MainApp.ENTER_TYPE_BUTTOM, false, null);
        }

        ExitActivity.getInstance().finishAllActivity();
    }

    private class MainApplicationHandler extends android.os.Handler {
        public static final int ACTIVITY_FINISH = 100;
        public static final int GC = 101;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ACTIVITY_FINISH:
                    if (msg.obj != null) {
                        Activity activity = (Activity) msg.obj;
                        if (!activity.isFinishing()) {
                            activity.finish();
                        }
                    }

                case GC:
                    //                    if (msg.obj != null) {
                    //                        Activity activity = (Activity) msg.obj;
                    //                        if (!activity.isFinishing()) {
                    //                            try {
                    //                                Method m = StrictMode.class.getMethod("incrementExpectedActivityCount", Class.class);
                    //                                m.invoke(null, activity.getClass());
                    //                            } catch (NoSuchMethodException e) {
                    //                                Global.ProcException(e);
                    //                            } catch (InvocationTargetException e) {
                    //                                Global.ProcException(e);
                    //                            } catch (IllegalAccessException e) {
                    //                                Global.ProcException(e);
                    //                            }
                    //                        }
                    //                    }

                    System.gc();
            }
        }
    }


    /**
     * 全局复制粘贴
     */
    public void setTextSelection(final TextView textView, final EditText editText, final Activity mActivity) {

        // 小于5.0不调用方法
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        final ActionMode.Callback callback2 = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                LogUtil.dee("onPrepareActionMode");
                MenuInflater menuInflater = mode.getMenuInflater();
                menuInflater.inflate(R.menu.selectionmenu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                LogUtil.dee("onPrepareActionMode");
                for (int i = 0; i < menu.size(); i++) {
                    MenuItem item = menu.getItem(i);
                    if (!menuIds.contains(item.getItemId()))
                        item.setVisible(false);
                }
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.copy:
                        if (null != textView) {
                            int min = 0;
                            int max = textView.getText().length();
                            if (textView.isFocused()) {
                                final int start = textView.getSelectionStart();
                                final int end = textView.getSelectionEnd();

                                min = Math.max(0, Math.min(start, end));
                                max = Math.max(0, Math.max(start, end));
                            }

                            cmb.setPrimaryClip(ClipData.newPlainText("paste_content", textView.getText().subSequence(min, max)));
                        }

                        if (null != editText) {
                            int min = 0;
                            int max = editText.getText().length();
                            if (editText.isFocused()) {
                                final int start = editText.getSelectionStart();
                                final int end = editText.getSelectionEnd();

                                min = Math.max(0, Math.min(start, end));
                                max = Math.max(0, Math.max(start, end));
                            }

                            cmb.setPrimaryClip(ClipData.newPlainText("paste_content", editText.getText().subSequence(min, max)));
                        }
                        mode.finish();
                        return true;

                    case R.id.paste:
                        if (null != editText && editText.isFocusable()) {
                            int index = editText.getSelectionStart();
                            Editable editable = editText.getText();
                            editable.insert(index, cmb.getPrimaryClip().getItemAt(0).coerceToText(mActivity));
                        }

                        mode.finish();
                        return true;
                }

                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                actionMode = null;
            }
        };

        if (null != textView)
            textView.setCustomSelectionActionModeCallback(callback2);
        if (null != editText)
            editText.setCustomSelectionActionModeCallback(callback2);
/*        editText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (actionMode != null) {
                    return false;
                }
                actionMode = mActivity.startActionMode(callback2, ActionMode.TYPE_FLOATING);
                return true;
            }
        });*/

    }

    @Override
    public void onTerminate() {
        // 程序终止的时候执行
        LogUtil.d("程序终止的时候执行");
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        // 低内存的时候执行
        LogUtil.d("低内存的时候执行");
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        // 程序在内存清理的时候执行
        LogUtil.d("程序在内存清理的时候执行");
        super.onTrimMemory(level);
    }
}
