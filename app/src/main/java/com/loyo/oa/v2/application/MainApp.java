package com.loyo.oa.v2.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.os.StrictMode;
import android.support.multidex.MultiDex;

import com.baidu.mapapi.SDKInitializer;
import com.google.gson.Gson;
import com.loyo.oa.v2.BuildConfig;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.beans.CellInfo;
import com.loyo.oa.v2.beans.Department;
import com.loyo.oa.v2.beans.Industry;
import com.loyo.oa.v2.beans.Province;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.beans.UserGroupData;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.ServerAPI;
import com.loyo.oa.v2.db.DBManager;
import com.loyo.oa.v2.jpush.HttpJpushNotification;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.ExitActivity;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
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
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

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

    private static MainApp mainApp;
    public static Gson gson;
    public static HttpJpushNotification jpushData;


    public DisplayImageOptions options_rounded;
    public static DisplayImageOptions options_3;

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
    public SimpleDateFormat df_api;//服务器返回的时间格式
    public SimpleDateFormat df_api_get;
    public SimpleDateFormat df_api_get2;
    public LogUtil logUtil;

    MainApplicationHandler handler;

    public double longitude = -1;
    public double latitude = -1;
    public String address;

    public boolean hasNewVersion = false;


    //-------这些数据需要保存在本地-------------
    //下属
    public static ArrayList<User> subUsers = new ArrayList<>();
    public static ArrayList<UserGroupData> lstUserGroupData;
    public static ArrayList<Department> lstDepartment;//组织架构 的缓存
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
        token = _token;
        SharedUtil.put(getMainApp().getBaseContext(), FinalVariables.TOKEN, token);
    }


    public ArrayList<Province> mProvinces = new ArrayList<>();
    public ArrayList<Industry> mIndustries = new ArrayList<>();

    /**
     * xnq
     * 加载地区编码
     */
    void loadAreaCodeTable() {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).getDistricts(new RCallback<ArrayList<Province>>() {
            @Override
            public void success(ArrayList<Province> provinces, Response response) {
                mProvinces = provinces;
                try {
                    LogUtil.d("districts加载地区编码:" + Utils.convertStreamToString(response.getBody().in()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

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
        loadAreaCodeTable();
        loadIndustryCodeTable();
           //    getWindowWH();
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
                    //System.out.print(" 获取的token ："+String.format("Bearer %s", MainApp.getToken()));

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

            restAdapter = new RestAdapter.Builder().setEndpoint(Config_project.SERVER_URL_LOGIN()).setLogLevel(RestAdapter.LogLevel.FULL).setRequestInterceptor(requestInterceptor).build();
        }

        return restAdapter;
    }



    void init() {
        CrashReport.initCrashReport(getApplicationContext(), "900001993", Config_project.is_developer_mode);  //初始化SDK
        SDKInitializer.initialize(this);

        if (BuildConfig.DEBUG) {
            try {
                Class c = Class.forName("com.squareup.leakcanary.LeakCanary");
                Method m = c.getMethod("install", Application.class);
                if (m != null) {
                    m.invoke(null, this);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        //        init_StrictMode();

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
        df8 = new SimpleDateFormat("yyyy-MM", Locale.getDefault());//设置日期格式
        df9 = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());//设置日期格式
        df10 = new SimpleDateFormat("yyyy年M月dd日 HH:mm", Locale.getDefault());//设置日期格式
        df11 = new SimpleDateFormat("dd日", Locale.getDefault());//设置日期格式
        df12 = new SimpleDateFormat("yyyy年M月dd日", Locale.getDefault());//设置日期格式
        df13 = new SimpleDateFormat("yyyy年M月", Locale.getDefault());//设置日期格式
        df_api = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());//设置日期格式
        df_api_get = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());//设置日期格式
        df_api_get2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+08:00", Locale.getDefault());//设置日期格式，2015-01-15T05:30:00+08:00
        gson = new Gson();

        DBManager.init(this);

        try {
            //user = DBManager.Instance().getUser();
            // subUsers = DBManager.Instance().getSubordinates();
        } catch (Exception ex) {
            Global.ProcDebugException(ex);
            ex.printStackTrace();
        }
    }

    void init_DisplayImageOptions() {
        options_rounded = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).displayer(new RoundedBitmapDisplayer(20)).build();

        options_3 = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).build();
    }

    void init_StrictMode() {
        if (Config_project.is_developer_mode) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    //                    .detectAll()
                    .detectDiskReads().detectDiskWrites().detectNetwork()   // or .detectAll() for all detectable problems
                    .penaltyLog()
                    //                    .penaltyDialog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    //                    .detectAll()
                    .detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
        }
    }

    public static void initImageLoader(Context context) {

        File cacheDir = StorageUtils.getOwnCacheDirectory(mainApp.getBaseContext(), "imageloader/Cache");

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.empty_photo)// 空uri时的默认图片
                .showImageOnFail(R.drawable.empty_photo)// 加载失败时的默认图片
                .cacheInMemory(true)// 是否缓存到内存
                .cacheOnDisc(true)// 是否缓存到磁盘
                .bitmapConfig(Bitmap.Config.RGB_565)// 图片格式比RGB888少消耗2倍内存
                .imageScaleType(ImageScaleType.EXACTLY)// 图片缩放方式
                .build();

        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(context).defaultDisplayImageOptions(defaultOptions).threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory().diskCacheFileNameGenerator(new Md5FileNameGenerator()).discCache(new UnlimitedDiscCache(cacheDir))//自定义缓存路径
                //.discCache(new UnlimitedDiscCache(new File("CRMcacheDir")))
                // .diskCacheSize(50 * 1024 * 1024) // 50 Mb
                .memoryCacheExtraOptions(800, 800).tasksProcessingOrder(QueueProcessingType.LIFO);

        if (Config_project.is_developer_mode) {
            builder.writeDebugLogs();
        }

        ImageLoaderConfiguration config = builder.build();

        // Initialize ImageLoader with configuration
        ImageLoader.getInstance().init(config);
    }

    public static MainApp getMainApp() {
        return mainApp;
    }

    public int diptoPx(float dipValue) {

        float density = getResources().getDisplayMetrics().density;
        //	 Log.d("diptoPx", "density:" + density);
        // Log.d("diptoPx", "return:" + (int) (dipValue * density + 0.5f));
        // DisplayMetrics dm = this.getApplicationContext().getResources().getDisplayMetrics();
        // Log.d("diptoPx", "dm.densityDpi/ 160:" + (float)dm.densityDpi/ 160f);
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
}
