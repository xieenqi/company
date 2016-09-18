package com.loyo.oa.v2.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.support.multidex.MultiDex;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import com.google.gson.Gson;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.other.bean.CellInfo;
import com.loyo.oa.v2.activityui.customer.bean.Department;
import com.loyo.oa.v2.activityui.customer.bean.Industry;
import com.loyo.oa.v2.activityui.other.bean.User;
import com.loyo.oa.v2.activityui.other.bean.UserGroupData;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.ServerAPI;
import com.loyo.oa.v2.customview.multi_image_selector.MultiImageSelectorActivity;
import com.loyo.oa.v2.db.DBManager;
import com.loyo.oa.v2.db.OrganizationManager;
import com.loyo.oa.v2.jpush.HttpJpushNotification;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.ExitActivity;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.SelectPicPopupWindow;
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
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.android.api.JPushInterface;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.Response;


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

    public static long nowTime;
    public static long lastTime;
    public static DisplayImageOptions options_3;
    private static MainApp mainApp;
    public static Gson gson;
    public static HttpJpushNotification jpushData;
    public boolean isCutomerEdit = false;//客户信息是否编辑过
    public static int permissionPage;

    public DisplayImageOptions options_rounded;

    public SimpleDateFormat df1;//设置日期格式
    public SimpleDateFormat df2;//设置日期格式
    public SimpleDateFormat df3;//设置日期格式
    public SimpleDateFormat df4;//设置日期格式
    public SimpleDateFormat df5;//设置日期格式
    public SimpleDateFormat df6;//设置日期格式
    public SimpleDateFormat df7;//设置日期格式
    public SimpleDateFormat df8;//设置日期格式
    public SimpleDateFormat df9;//设置日期格式
    public SimpleDateFormat df10;//设置日期格式
    public SimpleDateFormat df11;//设置日期格式
    public SimpleDateFormat df12;//设置日期格式
    public SimpleDateFormat df13;//设置日期格式
    public SimpleDateFormat df14;//设置日期格式
    public SimpleDateFormat df15;//设置日期格式
    public SimpleDateFormat df_api;//服务器返回的时间格式
    public SimpleDateFormat df_api_get;
    public SimpleDateFormat df_api_get2;
    public LogUtil logUtil;

    MainApplicationHandler handler;

    public double longitude = -1;
    public double latitude = -1;
    public String cityCode;
    public String address;
    public String message;
    public static boolean isQQLogin = false;
    public boolean hasNewVersion = false;
    public static HashMap<String, Object> rootMap;

    public ScaleAnimation animShow;  //显示动画
    public ScaleAnimation animHide;  //隐藏动画


    //-------这些数据需要保存在本地-------------
    //下属
    public static ArrayList<UserGroupData> lstUserGroupData;
    public static ArrayList<Department> lstDepartment;//组织架构 的缓存
    public static ArrayList<User> selectAllUsers; //选人功能 所有人员缓存

    static String token;
    public static User user;//InitDataService 在这里负值
    public CellInfo cellInfo;

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

        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).getIndustry(new RCallback<ArrayList<Industry>>() {
            @Override
            public void success(ArrayList<Industry> industries, Response response) {
                mIndustries = industries;

            }
        });

    }
    //-------这些数据需要保存在本地-------------

    @Override
    public void onCreate() {
        super.onCreate();
        mainApp = this;
        init();
        loadIndustryCodeTable();
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

    }

    static RestAdapter restAdapter = null;

    public RestAdapter getRestAdapter() {
        if (restAdapter == null) {
            if (cellInfo == null) {
                cellInfo = Utils.getCellInfo();
            }

            RequestInterceptor requestInterceptor = new RequestInterceptor() {
                @Override
                public void intercept(RequestFacade request) {
                    request.addHeader("Authorization", String.format("Bearer %s", MainApp.getToken()));
                    request.addHeader("LoyoPlatform", cellInfo.getLoyoPlatform());
                    request.addHeader("LoyoAgent", cellInfo.getLoyoAgent());
                    request.addHeader("LoyoOSVersion", cellInfo.getLoyoOSVersion());
                    request.addHeader("LoyoVersionName", Global.getVersionName());
                    request.addHeader("LoyoVersionCode", String.valueOf(Global.getVersion()));
                }
            };

            restAdapter = new RestAdapter.Builder().setEndpoint(Config_project.API_URL()).
                    setLogLevel(RestAdapter.LogLevel.FULL).setRequestInterceptor(requestInterceptor).build();
        }

        return restAdapter;
    }

    public RestAdapter getRestAdapter(int mode) {
        if (restAdapter != null) {
            if (cellInfo == null) {
                cellInfo = Utils.getCellInfo();
            }
            RequestInterceptor requestInterceptor = new RequestInterceptor() {
                @Override
                public void intercept(RequestFacade request) {
                    request.addHeader("Authorization", String.format("Bearer %s", MainApp.getToken()));
                    request.addHeader("LoyoPlatform", cellInfo.getLoyoPlatform());
                    request.addHeader("LoyoAgent", cellInfo.getLoyoAgent());
                    request.addHeader("LoyoOSVersion", cellInfo.getLoyoOSVersion());
                    request.addHeader("LoyoVersionName", Global.getVersionName());
                    request.addHeader("LoyoVersionCode", String.valueOf(Global.getVersion()));
                }
            };

            restAdapter = new RestAdapter.Builder().setEndpoint(Config_project.SERVER_URL_LOGIN()).setLogLevel(RestAdapter.LogLevel.FULL).
                    setRequestInterceptor(requestInterceptor).build();
        }

        return restAdapter;
    }


    void init() {

        animShow = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animShow.setDuration(120);//设置动画持续时间

        animHide = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animHide.setDuration(120);//设置动画持续时间

        CrashReport.initCrashReport(getApplicationContext(), "900037071", Config_project.is_developer_mode);  //初始化bugly SDK  900001993
        Configuration config = getResources().getConfiguration();
        config.locale = Locale.CHINA;
        getBaseContext().getResources().updateConfiguration(config, null);
        logUtil = LogUtil.lLog();
        handler = new MainApplicationHandler();
        ServerAPI.init();
        initImageLoader(getApplicationContext());
        init_DisplayImageOptions();
        df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());//设置日期格式
        df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());//设置日期格式
        df3 = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault());//设置日期格式
        df4 = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());//设置日期格式
        df5 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());//设置日期格式
        df6 = new SimpleDateFormat("HH:mm", Locale.getDefault());//设置日期格式
        df7 = new SimpleDateFormat("MM.dd", Locale.getDefault());//设置日期格式
        df8 = new SimpleDateFormat("yyyy.MM", Locale.getDefault());//设置日期格式
        df9 = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());//设置日期格式
        df10 = new SimpleDateFormat("yyyy年M月dd日 HH:mm", Locale.getDefault());//设置日期格式
        df11 = new SimpleDateFormat("dd日", Locale.getDefault());//设置日期格式
        df12 = new SimpleDateFormat("yyyy年M月dd日", Locale.getDefault());//设置日期格式
        df13 = new SimpleDateFormat("yyyy年M月", Locale.getDefault());//设置日期格式
        df14 = new SimpleDateFormat("yyyy年M月dd日 HH:mm:ss", Locale.getDefault());//设置日期格式
        df15 = new SimpleDateFormat("yyyy.MM", Locale.getDefault());//设置日期格式
        df_api = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());//设置日期格式
        df_api_get = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());//设置日期格式
        df_api_get2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+08:00", Locale.getDefault());//设置日期格式，2015-01-15T05:30:00+08:00
        gson = new Gson();
        Utils.openGPS(this);
        DBManager.init(this);
        OrganizationManager.init(this);

        try {
//            user = DBManager.Instance().getUser();
//             subUsers = DBManager.Instance().getSubordinates();
        } catch (Exception ex) {
            Global.ProcDebugException(ex);
            ex.printStackTrace();
        }
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //setOriginData();
            }
        }, 100);

    }

    /**
     * 设置缓存的组织架构数据
     */
//    void setOriginData() {
//        lstDepartment = DBManager.Instance().getOrganization();
//    }

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
     * */
    public void startSelectImage(Activity mActivity,ArrayList<SelectPicPopupWindow.ImageInfo> pickPhots){
        Intent intent = new Intent(mActivity, MultiImageSelectorActivity.class);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true /*是否显示拍摄图片*/);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, (9-pickPhots.size()) /*最大可选择图片数量*/);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI  /*选择模式*/);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_CROP_CIRCLE, false);
        mActivity.startActivityForResult(intent, PICTURE);
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


    @Override
    public void onTerminate() {
        // 程序终止的时候执行
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        // 低内存的时候执行
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        // 程序在内存清理的时候执行
        super.onTrimMemory(level);
    }
}
