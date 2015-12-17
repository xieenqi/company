package com.loyo.oa.v2.tool;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Attachment;
import com.loyo.oa.v2.beans.CellInfo;
import com.loyo.oa.v2.beans.Contact;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.beans.Member;
import com.loyo.oa.v2.beans.NewTag;
import com.loyo.oa.v2.beans.NewUser;
import com.loyo.oa.v2.beans.Role;
import com.loyo.oa.v2.beans.TagItem;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.point.IAttachment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

     static ProgressDialog  progressDialog;


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

        //Create an Intrinsic Blur Script using the Renderscript
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        //Create the Allocations (in/out) with the Renderscript and the in/out bitmaps
        Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
        Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);

        //Set the radius of the blur
        blurScript.setRadius(25.f);

        //Perform the Renderscript
        blurScript.setInput(allIn);
        blurScript.forEach(allOut);

        //Copy the final bitmap created by the out Allocation to the outBitmap
        allOut.copyTo(outBitmap);

        //recycle the original bitmap
//        bitmap.recycle();

        //After finishing everything, we destroy the Renderscript.
        rs.destroy();

        return outBitmap;
    }

    /**
     * 解决视图比屏幕大时无法builddrawingcache的bug
     * @param v
     * @return
     */
    public static Bitmap loadBitmapFromView(View v) {
        final int width = v.getMeasuredWidth();
        final int height = v.getMeasuredHeight();
        Bitmap b = Bitmap.createBitmap( width, height, Bitmap.Config.RGB_565);

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
    public synchronized static Observable<Attachment> uploadAttachment(String uuid, File file) {
        TypedFile typedFile = new TypedFile("image/*", file);
        TypedString typedString = new TypedString(uuid);
        return RestAdapterFactory.getInstance().build(Config_project.API_URL_ATTACHMENT()).create(IAttachment.class).upload(typedString, typedFile);
    }

    /**
     * 获取附件
     *
     * @param uuid
     * @param attachments
     */
    public synchronized static void getAttachments(String uuid, RCallback<ArrayList<Attachment>> attachments) {
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
     * */
    public static void dialogShow(Context ct){
        progressDialog = new ProgressDialog(ct);
        progressDialog.setMessage("请稍候");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public static void dialogDismiss(){
        if(progressDialog!=null&&progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }


    /**
     * 发送短信
     *
     * @param context
     * @param tel
     */
    public static void sendSms(Context context, String tel) {
        if (TextUtils.isEmpty(tel)) {
            Global.Toast("电话号码为空");
            return;
        }
        Uri uri = Uri.parse("smsto:" + tel);
        Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);
        sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sendIntent.putExtra("sms_body", "");
        context.startActivity(sendIntent);
    }

    /**
     * 打电话
     *
     * @param context
     * @param tel
     */
    public static void call(Context context, String tel) {
        if (TextUtils.isEmpty(tel)) {
            Global.Toast("电话号码为空");
            return;
        }
        Intent sendIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tel));
        sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(sendIntent);
    }

    /**
     * 获取客户标签
     *
     * @param customer
     * @return
     */
    public static String getTagItems(Customer customer) {
        if (null == customer || null == customer.tags || customer.tags.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < customer.tags.size(); i++) {
            String tag = !TextUtils.isEmpty(customer.tags.get(i).getItemName()) ? customer.tags.get(i).getItemName() : "";
            if (!TextUtils.isEmpty(tag)) {
                if (i != customer.tags.size() - 1) {
                    tag = tag.concat(",");
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
    public static void goWhere(final Context context, final double toLat, final double toLng) {

        new LocationUtil(context, new LocationUtil.AfterLocation() {
            @Override
            public void OnLocationSucessed(String address, double longitude, double latitude, float radius) {
                Uri uri;
                if (hasMapApp(context)) {
                    uri = Uri.parse("geo: " + latitude + "," + longitude);

                } else {
                    uri = Uri.parse("http://m.amap.com/?from=" + latitude + "," + longitude + "(from)&to=" + toLat + "," + toLng + "(to)");
                }
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(it);
            }

            @Override
            public void OnLocationFailed() {
                Global.Toast("获取当前位置失败,无法规划路径");
            }
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

    /**
     * 是否有权限
     *
     * @return
     */
    public static boolean hasRights() {
        Role role = MainApp.user.getRole();
        if (null != role) {
            if (role.getDataRange() != Role.SELF) {
                return true;
            }
        }
        return false;
    }

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
     * 是否初始化小米推送
     *
     * @param context
     * @return
     */
    public static boolean shouldInitXm(Context context) {
        ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = context.getApplicationContext().getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
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
     * */

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
}
