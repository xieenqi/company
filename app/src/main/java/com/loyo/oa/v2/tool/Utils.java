package com.loyo.oa.v2.tool;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.customer.model.Contact;
import com.loyo.oa.v2.activityui.customer.model.Member;
import com.loyo.oa.v2.activityui.customer.model.NewTag;
import com.loyo.oa.v2.activityui.customer.model.Role;
import com.loyo.oa.v2.activityui.customer.model.TagItem;
import com.loyo.oa.v2.activityui.other.model.CellInfo;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.beans.NewUser;
import com.loyo.oa.v2.beans.UserInfo;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customview.SweetAlertDialogView;
import com.loyo.oa.v2.point.IAttachment;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;
import rx.Observable;

/**
 * com.loyo.oa.v2.tool
 * 描述 :工具类
 * 作者 : ykb
 * 时间 : 15/8/6.
 */
public class Utils {

    static ProgressDialog progressDialog;
    static ProgressDialog progressDialogAtt;
    static WindowManager windowManager;
    static boolean scrollFlag;
    static int lastVisibleItemPosition;

    protected Utils() {
        throw new UnsupportedOperationException(); // 防止子类调用
    }

    public static WindowManager getWindowHW(Context mContext) {
        windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        return windowManager;
    }

    /**
     * 根据当前选择的秒数还原时间点
     *
     */

    private static String getCheckTimeBySeconds(float progress,float totalSeconds2, String startTimeStr) {

        String return_h = "", return_m = "", return_s = "";
        float ms = (progress * totalSeconds2) / 100;
        String[] st = startTimeStr.split(":");
        int st_h = Integer.valueOf(st[0]);
        int st_m = Integer.valueOf(st[1]);
        int st_s = Integer.valueOf(st[2]);
        int total = st_h * 3600 + st_m * 60 + st_s;
        float mtotal = ms + total;
        int h = (int) (mtotal / 3600);
        int m = (int) ((mtotal % 3600) / 60);

        int s = (int) (mtotal % 60);
        return_h = h + "";
        return_m = m + "";
        return_s = s + "";

        return return_h + ":" + return_m + ":" + return_s;
    }


    /**
     * 计算连个时间之间的秒数
     */

    public static int totalSeconds(String startTime, String endTime) {

        String[] st = startTime.split(":");
        String[] et = endTime.split(":");

        int st_h = Integer.valueOf(st[0]);
        int st_m = Integer.valueOf(st[1]);
        int st_s = Integer.valueOf(st[2]);

        int et_h = Integer.valueOf(et[0]);
        int et_m = Integer.valueOf(et[1]);
        int et_s = Integer.valueOf(et[2]);

        int totalSeconds = (et_h - st_h) * 3600 + (et_m - st_m) * 60
                + (et_s - st_s);

        return totalSeconds;

    }

    /**
     * 根据当前选择的秒数还原时间点
     */

    public static String getCheckTimeBySeconds(int progress, String startTime) {

        String return_h = "", return_m = "", return_s = "";

        String[] st = startTime.split(":");

        int st_h = Integer.valueOf(st[0]);
        int st_m = Integer.valueOf(st[1]);
        int st_s = Integer.valueOf(st[2]);

        int h = progress / 3600;

        int m = (progress % 3600) / 60;

        int s = progress % 60;

        if ((s + st_s) >= 60) {

            int tmpSecond = (s + st_s) % 60;

            m = m + 1;

            if (tmpSecond >= 10) {
                return_s = tmpSecond + "";
            } else {
                return_s = "0" + (tmpSecond);
            }

        } else {
            if ((s + st_s) >= 10) {
                return_s = s + st_s + "";
            } else {
                return_s = "0" + (s + st_s);
            }

        }

        if ((m + st_m) >= 60) {

            int tmpMin = (m + st_m) % 60;

            h = h + 1;

            if (tmpMin >= 10) {
                return_m = tmpMin + "";
            } else {
                return_m = "0" + (tmpMin);
            }

        } else {
            if ((m + st_m) >= 10) {
                return_m = (m + st_m) + "";
            } else {
                return_m = "0" + (m + st_m);
            }

        }

        if ((st_h + h) < 10) {
            return_h = "0" + (st_h + h);
        } else {
            return_h = st_h + h + "";
        }

        return return_h + ":" + return_m + ":" + return_s;
    }

    public static String getStringTime(int cnt,String timeData) {
        int hour = cnt/3600;
        int min = cnt % 3600 / 60;
        int second = cnt % 60;
        return String.format(Locale.CHINA,timeData,hour,min,second);
    }


    /**
     * MD5加密
     * */
    public static String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }


    /**
     * 图片二值化
     * */
    public static Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    /**
     * 自动弹出软键盘
     * time: 设置弹出延迟时间，目的在于等页面渲染完成，否则自动弹出会失效
     */
    public static void autoEjetcEdit(final EditText edt, int time) {

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
                           public void run() {
                               InputMethodManager inputManager =
                                       (InputMethodManager) edt.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                               inputManager.showSoftInput(edt, 0);
                           }
                       },
                time);
    }

    /**
     * 获取屏幕宽度
     */
    public static int getWindowWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);

        int width = wm.getDefaultDisplay().getWidth();
        return width;
    }

    /**
     * 获取屏幕高度
     */
    public static int getWindowHeight(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);

        int height = wm.getDefaultDisplay().getHeight();
        return height;
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    public static String FormetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }


    /**
     * 图片类型
     */
    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }

    /**
     * @param list
     */
    private ArrayList removeDuplicateObj(ArrayList list) {
        // ................
        Set someSet = new HashSet(list);

        // 将Set中的集合，放到一个临时的链表中(tempList)
        Iterator iterator = someSet.iterator();
        ArrayList tempList = new ArrayList();
        int i = 0;
        while (iterator.hasNext()) {

            tempList.add(iterator.next().toString());
            i++;
        }
        return tempList;
    }

    /**
     * ScroView嵌套listView，手动计算ListView高度
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    /**
     * 深度拷贝
     */
    public static List deepCopy(List src) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(src);

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        List dest = (List) in.readObject();
        return dest;
    }


    /**
     * 深度拷贝(带泛型)
     */
    public static List<?> deepCopyT(List<?> src) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(src);

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        List<?> dest = (List<?>) in.readObject();
        return dest;
    }


    /**
     * 高斯模糊图片
     *
     * @param bitmap
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Bitmap blurBitmap(Bitmap bitmap) {

        //Let's create an empty bitmap with the same size of the bitmap we want to blur
        Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        //Instantiate a new Renderscript
        RenderScript rs = RenderScript.create(MainApp.getMainApp());
        try {
            //Create an Intrinsic Blur Script using the Renderscript
            ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

            //Create the Allocations (in/out) with the Renderscript and the in/out bitmaps
            Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
            Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);

            //Set the radius of the blur
            blurScript.setRadius(50.f);

            //Perform the Renderscript
            blurScript.setInput(allIn);
            blurScript.forEach(allOut);

            //Copy the final bitmap created by the out Allocation to the outBitmap
            allOut.copyTo(outBitmap);

            //recycle the original bitmap

            bitmap.recycle();
            //After finishing everything, we destroy the Renderscript.
            rs.destroy();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            LogUtil.d("高斯模糊Bitmap转换参数异常");
        }
        return outBitmap;
    }

    /**
     * 解决视图比屏幕大时无法builddrawingcache的bug
     *
     * @param v
     * @return
     */
    public static Bitmap loadBitmapFromView(View v) {
        final int width = v.getMeasuredWidth();
        final int height = v.getMeasuredHeight();
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        Canvas c = new Canvas(b);
        v.draw(c);
        return b;
    }

    /**
     * 附件上传
     *
     * @param uuid
     * @param file
     */
    public static synchronized Observable<Attachment> uploadAttachment(String uuid, int bizType, File file) {
        TypedFile typedFile = new TypedFile("image/*", file);
        TypedString typedUuid = new TypedString(uuid);
        return RestAdapterFactory.getInstance().build(Config_project.API_URL_ATTACHMENT()).create(IAttachment.class).upload(typedUuid, bizType, typedFile);
    }

    /**
     * 获取附件
     *
     * @param uuid
     * @param attachments
     */
    public static synchronized void getAttachments(String uuid, RCallback<ArrayList<Attachment>> attachments) {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_ATTACHMENT()).create(IAttachment.class).getAttachments(uuid, attachments);
    }

    /**
     * 获取参与人
     *
     * @param members
     * @return
     */
    public static String getMembers(ArrayList<Member> members) {
        if (null == members || members.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < members.size(); i++) {
            NewUser user = members.get(i).getUser();
            if (null == user) {
                continue;
            }
            if (!TextUtils.isEmpty(user.getName())) {
                builder.append(user.getName());
                if (i != members.size() - 1) {
                    builder.append(",");
                }
            }
        }
        return builder.toString();
    }

    /**
     * 构建标签
     *
     * @param tagItems
     * @return
     */
    public static ArrayList<TagItem> convertTagItems(ArrayList<NewTag> tagItems) {
        if (null == tagItems || tagItems.isEmpty()) {
            return new ArrayList<>();
        }
        ArrayList<TagItem> tags = new ArrayList<>();
        for (int i = 0; i < tagItems.size(); i++) {
            NewTag item = tagItems.get(i);
            TagItem tag = new TagItem();
            tag.setId(item.getItemId());
            tag.setName(item.getItemName());
            tag.setTagId(item.gettId());
            tag.setIsChecked(true);

            tags.add(tag);
        }

        return tags;
    }

    /**
     * 构建先客户需要的user
     *
     * @param userIds
     * @param userNames
     * @return
     */
    public static ArrayList<Member> convert2Members(String userIds, String userNames) {
        if (TextUtils.isEmpty(userIds) || TextUtils.isEmpty(userNames)) {
            return new ArrayList<>();
        }
        ArrayList<Member> members = new ArrayList<>();
        String ids[] = userIds.split(",");
        String names[] = userNames.split(",");
        for (int i = 0; i < ids.length; i++) {
            NewUser user = new NewUser();
            user.setName(names[i]);
            user.setId(ids[i]);

            Member member = new Member();
            member.setUser(user);

            members.add(member);
        }
        return members;
    }


    /**
     * 找出默认联系人
     *
     * @return
     */
    public static Contact findDeault(Customer customer) {
        ArrayList<Contact> contacts = customer.contacts;
        if (null == contacts || contacts.isEmpty()) {
            return null;
        }
        for (int i = 0; i < contacts.size(); i++) {
            Contact contact = contacts.get(i);
            if (contact.isDefault()) {
                return contact;
            }
        }
        return contacts.get(0);
    }

    /**
     * 等待进度条
     */
    public static void dialogShow(Context ct, String info) {
        progressDialog = new ProgressDialog(ct);
        progressDialog.setMessage(info);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public static void dialogDismiss() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    /**
     * 发送短信
     *
     * @param context
     * @param tel
     */
    public static void sendSms(final Context context, String tel) {
        if (TextUtils.isEmpty(tel)) {
            Global.Toast("电话号码为空");
            return;
        }
        if (PackageManager.PERMISSION_GRANTED ==
                context.getPackageManager().checkPermission("android.permission.SEND_SMS", "com.loyo.oa.v2")) {
            Uri uri = Uri.parse("smsto:" + tel);
            Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);
            sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            sendIntent.putExtra("sms_body", "");
            context.startActivity(sendIntent);
        } else {

            final SweetAlertDialogView sDialog = new SweetAlertDialogView(context);
            sDialog.alertHandle(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sDialog.sweetAlertDialog.dismiss();
                }
            }, new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sDialog.sweetAlertDialog.dismiss();
                    doSeting(context);
                }
            },"提示","需要使用短信权限、相机权限\n请在”设置”>“应用”>“权限”中配置权限");
        }
    }

    /**
     * 打电话
     *
     * @param context
     * @param tel
     */
    public static void call(final Context context, String tel) {
        if (TextUtils.isEmpty(tel)) {
            Global.Toast("号码为空");
            return;
        }

        if (PackageManager.PERMISSION_GRANTED ==
                context.getPackageManager().checkPermission("android.permission.CALL_PHONE", "com.loyo.oa.v2")) {
            Intent sendIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tel));
            sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(sendIntent);
        } else {
            final SweetAlertDialogView sDialog = new SweetAlertDialogView(context);
            sDialog.alertHandle(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sDialog.sweetAlertDialog.dismiss();
                }
            }, new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sDialog.sweetAlertDialog.dismiss();
                    doSeting(context);
                }
            },"提示","需要使用电话权限\n请在”设置”>“应用”>“权限”中配置权限");
        }
    }

    /**
     * 获取客户标签
     *
     * @param customer
     * @return
     */
    public static String getTagItems(Customer customer) {
        if (null == customer || null == customer.tags || customer.tags.isEmpty()) {
            return "无";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < customer.tags.size(); i++) {
            String tag = !TextUtils.isEmpty(customer.tags.get(i).getItemName()) ? customer.tags.get(i).getItemName() : "";
            if (!TextUtils.isEmpty(tag)) {
                if (i != customer.tags.size() - 1) {
                    tag = tag.concat("、");
                }
                builder.append(tag);
            }
        }
        return builder.toString();
    }

    /**
     * 打开路径规划（地图app或浏览器里的地图）
     *
     * @param context
     * @param toLat
     * @param toLng
     */
    public static void goWhere(final Context context, final double toLat, final double toLng, final String addres) {

        new LocationUtilGD(context, new LocationUtilGD.AfterLocation() {
            @Override
            public void OnLocationGDSucessed(String address, double longitude, double latitude, String radius) {
                Uri uri;
                if (hasMapApp(context)) {
                    uri = Uri.parse("geo: " + toLat + "," + toLng + "?q=" + addres);
                } else {
                    uri = Uri.parse("http://m.amap.com/?from=" + latitude + "," + longitude + "(" + address + ")&to=" + toLat + "," + toLng + "(" + addres + ")");
                }
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(it);
                LocationUtilGD.sotpLocation();
                UMengTools.sendLocationInfo(address, longitude, latitude);
            }

            @Override
            public void OnLocationGDFailed() {
                Global.Toast("获取当前位置失败,无法规划路径");
                LocationUtilGD.sotpLocation();
            }

//            @Override
//            public void OnLocationSucessed(String address, double longitude, double latitude, float radius) {
///*                Intent intent;
//                try {
//                    intent = Intent.getIntent("intent://map/direction?origin=latlng:"+latitude+","+longitude+"|name:我家&destination=大雁塔&mode=driving®ion=西安&referer=Autohome|GasStation#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
//                    context.startActivity(intent); //启动调用
//                } catch (URISyntaxException e) {
//                    e.printStackTrace();
//                }*/
//            }
//
//            @Override
//            public void OnLocationFailed() {
//
//            }
        }

        );
    }


    /**
     * 是否含有地图app
     *
     * @param context
     * @return
     */

    public static boolean hasMapApp(Context context) {
        return hasFitApp(context, Intent.ACTION_MAIN, Intent.CATEGORY_APP_MAPS);
    }

    /**
     * 查询手机是否装有含某个ACTION和CATEGORY的App
     *
     * @param context  上下文
     * @param action   对应Intent里的action
     * @param category 对应Intent里的category
     * @return 如果有匹配的app, 返回true;其他，返回false
     */
    public static boolean hasFitApp(Context context, String action, String category) {
        if (TextUtils.isEmpty(action) || TextUtils.isEmpty(category)) {
            return false;
        }
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent();
        intent.setAction(action);
        intent.addCategory(category);
        List<ResolveInfo> infos = packageManager.queryIntentActivities(intent, 0);
        if (infos != null && !infos.isEmpty()) {
            return true;
        }

        return false;
    }

//    /**
//     * 是否有权限
//     *
//     * @return
//     */
//    public static boolean hasRights() {
//        if (MainApp.user == null) {
//            return false;
//        }
//        Role role = MainApp.user.role;
//        if (null != role) {
//            if (role.getDataRange() != Role.SELF) {
//                return true;
//            }
//        }
//        return false;
//    }

    /**
     * 改变文字颜色
     *
     * @param content
     */
    public static SpannableStringBuilder modifyTextColor(String content, int color, int start, int end) {

        if (TextUtils.isEmpty(content) || (start >= content.length() || end > content.length() || start > end || end < 0 || start < 0)) {
            return null;
        }

        SpannableStringBuilder builder = new SpannableStringBuilder(content);
        // ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
        ForegroundColorSpan redSpan = new ForegroundColorSpan(color);
        builder.setSpan(redSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }

    /**
     * 改变文字颜色
     *
     * @param content
     */
    public static SpannableStringBuilder modifyTextHTTP(String content, int color, int start, int end) {

        if (TextUtils.isEmpty(content) || (start >= content.length() || end > content.length() || start > end || end < 0 || start < 0)) {
            return null;
        }

        SpannableStringBuilder builder = new SpannableStringBuilder(content);
        // ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
        ForegroundColorSpan redSpan = new ForegroundColorSpan(color);
        builder.setSpan(redSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        builder.setSpan(new URLSpan(content), 2, 5,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }

    public static CharSequence checkAutoLink(String content, Activity activity) {

        String url = "百度 https://www.baidu.com/，腾讯 http://www.qq.com/，淘宝 www.taobao.com/";//此处测试，就不用参数了

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(content);

        Pattern pattern = Pattern.compile("((http{0,1}|ftp)://[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)|(www.[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)");

        Matcher matcher = pattern.matcher(spannableStringBuilder);

        while (matcher.find()) {
            setClickableSpan(spannableStringBuilder, matcher, activity);
        }

//        Pattern pattern2 = Pattern.compile("([\\s>])((www|ftp)\\.[\\w\\\\x80-\\\\xff\\#$%&~/.\\-;:=,?@\\[\\]+]*)");
//
//        matcher.reset();
//
//        matcher = pattern2.matcher(spannableStringBuilder);-
//
//        while (matcher.find()) {
//            setClickableSpan(spannableStringBuilder, matcher);
//        }

        return spannableStringBuilder;

    }

    //给符合的设置自定义点击事件

    private static void setClickableSpan(final SpannableStringBuilder clickableHtmlBuilder, final Matcher matcher, final Activity activity) {

        int start = matcher.start();

        int end = matcher.end();

        final String url = matcher.group();

        ClickableSpan clickableSpan = new ClickableSpan() {

            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.setData(Uri.parse(url));
                activity.startActivity(intent);
//                Global.Toast("点击了超链接？？？？？？？？？？？");
            }

        };

        clickableHtmlBuilder.setSpan(clickableSpan, start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

    }

    /**
     * 获取手机信息
     *
     * @return
     */
    public static CellInfo getCellInfo() {
        CellInfo cellInfo = new CellInfo();
        cellInfo.setLoyoAgent(Build.BRAND + " " + Build.MODEL);
        cellInfo.setLoyoOSVersion(cellInfo.getLoyoPlatform() + Build.VERSION.RELEASE);
        cellInfo.setLoyoHVersion(cellInfo.getLoyoPlatform() + Build.HARDWARE);

        return cellInfo;
    }


    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     *
     * @param context
     * @return true 表示开启
     */
    public static boolean isGPSOPen(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        LoyoLog.e("Utils,isGPSOPen", "gps : " + gps + " network : " + network);
        if (gps || network) {
            return true;
        }
        return false;
    }

    /**
     * 强制用户打开GPS定位
     *
     * @param context
     */
    public static final void openGPS(Context context) {
        Intent GPSIntent = new Intent();
        GPSIntent.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
        GPSIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取手机当前网络链接的类型
     */
    public static String getNetworkType(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            String type = networkInfo.getTypeName();
            if ("WIFI".equalsIgnoreCase(type)) {
                return "WIFI网络";
            } else if ("MOBILE".equalsIgnoreCase(type)) {
                String proxyHost = android.net.Proxy.getDefaultHost();
                return TextUtils.isEmpty(proxyHost)
                        ? (isFastMobileNetwork(context) ? "3G以上网络" : "2G网络")
                        : "wap网络";
            }
        } else {
            return "没有链接网络";
        }
        return "";
    }

    /**
     * 网络的速度
     *
     * @param context
     * @return
     */
    public static boolean isFastMobileNetwork(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return false; // ~ 14-64 kbps
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return true; // ~ 400-1000 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return true; // ~ 600-1400 kbps
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return false; // ~ 100 kbps
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return true; // ~ 2-14 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return true; // ~ 700-1700 kbps
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return true; // ~ 1-23 Mbps
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return true; // ~ 400-7000 kbps
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return true; // ~ 1-2 Mbps
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return true; // ~ 5 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return true; // ~ 10-20 Mbps
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return false; // ~25 kbps
            case TelephonyManager.NETWORK_TYPE_LTE:
                return true; // ~ 10+ Mbps
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return false;
            default:
                return false;
        }
    }

    /**
     * 从一个输入流里写文件
     *
     * @param destFilePath 要创建的文件的路径
     * @param in           要读取的输入流
     * @return 写入成功返回true, 写入失败返回false
     */
    public static boolean writeFile(String destFilePath, InputStream in) {
        try {
            FileOutputStream fos = new FileOutputStream(destFilePath);
            int readCount = 0;
            int len = 1024;
            byte[] buffer = new byte[len];
            while ((readCount = in.read(buffer)) != -1) {
                fos.write(buffer, 0, readCount);
            }
            fos.flush();
            if (null != fos) {
                fos.close();
                fos = null;
            }
            if (null != in) {
                in.close();
                in = null;
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 删除一个文件
     *
     * @param filePath 要删除的文件路径名
     * @return true if this file was deleted, false otherwise
     */
    public static boolean deleteFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                return file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 创建一个文件，创建成功返回true
     *
     * @param filePath
     * @return
     */
    public static boolean createFile(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }

                return file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void writeImage(Bitmap bitmap, String destPath, int quality) {
        try {

            deleteFile(destPath);
            if (createFile(destPath)) {
                FileOutputStream out = new FileOutputStream(destPath);
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)) {
                    out.flush();
                    out.close();
                    out = null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断网络连接是否可用
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
        } else {
            NetworkInfo[] info = cm.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * 设置内容
     *
     * @param tv
     * @param content
     */
    public static void setContent(TextView tv, String content) {
        //        if(!TextUtils.isEmpty(content))
        tv.setText(content);
    }

    /**
     * 获取年龄
     *
     * @param birthYear
     * @return
     */
    public static int getAge(String birthYear) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        String nowStr = dateFormat.format(new Date(System.currentTimeMillis()));
        int now = Integer.parseInt(nowStr);
        int age = now - Integer.parseInt(birthYear);

        return age > 0 ? age : 0;
    }

    /**
     * InputStream转String
     */

    public static String convertStreamToString(InputStream is) {

        /*
          * To convert the InputStream to String we use the BufferedReader.readLine()
          * method. We iterate until the BufferedReader return null which means
          * there's no more data to read. Each line will appended to a StringBuilder
          * and returned as String.
          */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }


    /*取下班时间，最小值，最早下班时间*/
    public static long minOutTime(ArrayList<Long> array) {
        long min = 0;
        for (int i = 0; i < array.size(); i++) {
            for (int k = i + 1; k < array.size(); k++) {
                if (array.get(i) < array.get(k)) {
                    min = array.get(k);
                } else {
                    min = array.get(i);
                }
            }
        }
        return min;
    }

    /**
     * 获取部门名字和职位名字，包括多部门情况下
     */
    public static StringBuffer getDeptName(StringBuffer stringBuffer, ArrayList<UserInfo> list) {

        for (int i = 0; i < list.size(); i++) {
            stringBuffer.append(list.get(i).getShortDept().getName());
            if (!list.get(i).getTitle().isEmpty()
                    && list.get(i).getTitle().length() > 0) {
                stringBuffer.append(" | " + list.get(i).getTitle());
            }
            if (i != list.size() - 1) {
                stringBuffer.append(" ; ");
            }
        }

        return stringBuffer;
    }


    /**
     * 服务是否在运行
     *
     * @param name
     * @return
     */
    public static boolean isServiceRunning(String name) {
        MainApp app = MainApp.getMainApp();
        ActivityManager manager = (ActivityManager) app.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServiceInfos = manager.getRunningServices(500);
        if (null != runningServiceInfos && !runningServiceInfos.isEmpty()) {
            for (int i = 0; i < runningServiceInfos.size(); i++) {
                ActivityManager.RunningServiceInfo serviceInfo = runningServiceInfos.get(i);
                String serviceName = serviceInfo.service.getClassName();
                if (TextUtils.equals(serviceName, name)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 高斯模糊图片算法
     */
    public static Bitmap doBlur(Bitmap sentBitmap, int radius, boolean canReuseInBitmap) {

        Bitmap bitmap = null;
        if (canReuseInBitmap) {
            bitmap = sentBitmap;
        } else {
            if (null != sentBitmap.getConfig()) {
                bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
            } else {
                LogUtil.d("高斯错误");
                return sentBitmap;
            }
        }

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return (bitmap);
    }

    /**
     * 到APP的设置页面去配置权限
     *
     * @param context
     */
    public static void doSeting(Context context) {
        Uri packageURI = Uri.parse("package:" + "com.loyo.oa.v2");
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
        context.startActivity(intent);
    }

    /**
     * 科学计数法转换为 数字 小数只有两位
     *
     * @param obj
     * @return
     */
    public static String setValueDouble(Object obj) {
        if (null == obj) {
            return "没有内容";
        }
        Double d;
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.##");
        try {
            d = Double.valueOf(obj + "");
            BigDecimal bigDecimal = new BigDecimal(df.format(d) + "");
            return bigDecimal.toPlainString() + "";
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return "No Number";
        }
    }

    /**
     * 科学计数法转换为 数字 只显示整数部分
     *
     * @param obj
     * @return
     */
    public static String setValueDouble2(Object obj) {
        if (null == obj) {
            return "没有内容";
        }
        Double d;
        java.text.DecimalFormat df = new java.text.DecimalFormat("#");
        try {
            d = Double.valueOf(obj + "");
            BigDecimal bigDecimal = new BigDecimal(df.format(d) + "");
            return bigDecimal.toPlainString() + "";
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return "No Number";
        }
    }

    public static String getRingDuring(String mUri){
        String duration=null;
        android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();

        try {
            if (mUri != null) {
                HashMap<String, String> headers=null;
                if (headers == null) {
                    headers = new HashMap<String, String>();
                    headers.put("User-Agent", "Mozilla/5.0 (Linux; U; Android 4.4.2; zh-CN; MW-KW-001 Build/JRO03C) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 UCBrowser/1.0.0.001 U4/0.8.0 Mobile Safari/533.1");
                }
                mmr.setDataSource(mUri, headers);
            }

            duration = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);
        } catch (Exception ex) {
        } finally {
            mmr.release();
        }
        return duration;
    }

    /**
     * 设置小数位数控制
     * 限制输入小数的位数
     *
     * @param index
     */
    public static InputFilter decimalDigits(final int index) {
        InputFilter lengthfilter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                // 删除等特殊字符，直接返回
                if ("".equals(source.toString())) {
                    return null;
                }
                String dValue = dest.toString();
                String[] splitArray = dValue.split("\\.");
                if (splitArray.length > 1) {
                    String dotValue = splitArray[1];
                    int diff = dotValue.length() + 1 - index;
                    if (diff > 0) {
                        return source.subSequence(start, end - diff);
                    }
                }
                return null;
            }
        };
        return lengthfilter;
    }

    /**
     * 自动弹出软键盘
     * */
    public static void autoKeyBoard(Context mContext,EditText editText){
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0,InputMethodManager.SHOW_FORCED);
    }

    /**
     * 添加按钮，根据滑动显示隐藏(recyclerView)
     */
    public static void btnHideForRecy(RecyclerView recyclerView, final View btn) {

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //正数下滑 负数上滑
                if (dy > 0) {
                    if (btn.getVisibility() == View.VISIBLE)
                        btn.startAnimation(MainApp.getMainApp().animHide);
                    btn.setVisibility(View.INVISIBLE);
                } else {
                    if (btn.getVisibility() == View.INVISIBLE)
                        btn.startAnimation(MainApp.getMainApp().animShow);
                    btn.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * 关闭软键盘
     */
    public static void hideInputKeyboard(EditText et,Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }

    /**
     * 添加按钮,底部评论菜单,滑动隐藏(ListView)
     */
    public static void btnSpcHideForListViewTeam(final Context mContext,final ListView listView,final LinearLayout menu,final LinearLayout voice,final EditText editText) {
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                editText.setText("");
                hideInputKeyboard(editText,mContext);
                menu.setVisibility(View.GONE);
                voice.setVisibility(View.GONE);
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    scrollFlag = true;
                } else {
                    scrollFlag = false;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    /**
     * 添加按钮,底部评论菜单,滑动隐藏(ListView)
     */
    public static void btnSpcHideForListView(final Context mContext,final ListView listView, final View btn, final LinearLayout menu,final LinearLayout voice,final EditText editText) {
        lastVisibleItemPosition = 0;
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                editText.setText("");
                hideInputKeyboard(editText,mContext);
                menu.setVisibility(View.GONE);
                voice.setVisibility(View.GONE);
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    scrollFlag = true;
                } else {
                    scrollFlag = false;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (scrollFlag) {
                    if (firstVisibleItem > lastVisibleItemPosition) {
                        LogUtil.dee("上滑");
                        if (btn.getVisibility() == View.VISIBLE)
                            btn.startAnimation(MainApp.getMainApp().animHide);
                        btn.setVisibility(View.INVISIBLE);
                    }
                    if (firstVisibleItem < lastVisibleItemPosition) {
                        LogUtil.dee("下滑");
                        if (btn.getVisibility() == View.INVISIBLE)
                            btn.startAnimation(MainApp.getMainApp().animShow);
                        btn.setVisibility(View.VISIBLE);
                    }
                    if (firstVisibleItem == lastVisibleItemPosition) {
                        return;
                    }
                    lastVisibleItemPosition = firstVisibleItem;
                }
            }
        });
    }

    /**
     * 添加按钮，根据滑动显示隐藏(ListView)
     */
    public static void btnHideForListView(final ListView listView, final View btn) {
        lastVisibleItemPosition = 0;
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                LogUtil.dee("scrollState:"+scrollState);
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    scrollFlag = true;
                } else {
                    scrollFlag = false;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (scrollFlag) {
                    if (firstVisibleItem > lastVisibleItemPosition) {
                        LogUtil.dee("上滑");
                        if (btn.getVisibility() == View.VISIBLE)
                            btn.startAnimation(MainApp.getMainApp().animHide);
                        btn.setVisibility(View.INVISIBLE);
                    }
                    if (firstVisibleItem < lastVisibleItemPosition) {
                        LogUtil.dee("下滑");
                        if (btn.getVisibility() == View.INVISIBLE)
                            btn.startAnimation(MainApp.getMainApp().animShow);
                        btn.setVisibility(View.VISIBLE);
                    }
                    if (firstVisibleItem == lastVisibleItemPosition) {
                        return;
                    }
                    lastVisibleItemPosition = firstVisibleItem;
                }
            }
        });
    }

}
