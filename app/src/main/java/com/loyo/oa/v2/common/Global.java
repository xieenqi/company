package com.loyo.oa.v2.common;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.ViewUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public final class Global {
    protected Global() {
        throw new UnsupportedOperationException(); // 防止子类调用
    }

    //获取返回关闭手势的划动长度
    public static int GetBackGestureLength() {
        float density = MainApp.getMainApp().getResources().getDisplayMetrics().density;
        return (density < 3) ? 250 : 350;
    }

    public static ViewUtil.OnTouchListener_view_transparency GetTouch() {
        return ViewUtil.OnTouchListener_view_transparency.Instance();
    }

    public static void SetTouchView(View... views) {
        for (int i = 0; i < views.length; i++) {
            views[i].setOnTouchListener(ViewUtil.OnTouchListener_view_transparency.Instance());
        }
    }

    public static void ProcException(Exception ex) {
        try {
            CrashReport.postCatchedException(ex.fillInStackTrace());
            ex.printStackTrace();
        } catch (Exception e) {
        }
    }

    public static void ProcException(Throwable throwable) {
        try {
            CrashReport.postCatchedException(throwable);
            throwable.printStackTrace();
        } catch (Exception e) {
        }
    }

    public static void ProcDebugException(Exception ex) {
        if (!Config_project.is_developer_mode)
            return;

        try {
            CrashReport.postCatchedException(ex.fillInStackTrace());
            ex.printStackTrace();
        } catch (Exception e) {
        }
    }

    public static void setHeadImage(ImageView imageView, String avator) {
        if (imageView == null) return;

        if (!StringUtil.isEmpty(avator)) {
            ImageLoader.getInstance().displayImage(avator, imageView, MainApp.options_3);
        } else {
            imageView.setImageResource(R.drawable.img_default_user);
        }
    }

    static Toast mCurrentToast;

    public static void Toast(String msg) {
        if (null != mCurrentToast) {
            mCurrentToast.cancel();
        }

        mCurrentToast = Toast.makeText(MainApp.getMainApp().getBaseContext(), msg, Toast.LENGTH_SHORT);
        mCurrentToast.setGravity(Gravity.CENTER, 0, 0);
        mCurrentToast.show();
    }

    public static void ToastLong(String msg) {
        if (null != mCurrentToast) {
            mCurrentToast.cancel();
        }

        mCurrentToast = Toast.makeText(MainApp.getMainApp().getBaseContext(), msg, Toast.LENGTH_LONG);
        mCurrentToast.setGravity(Gravity.CENTER, 0, 0);
        mCurrentToast.show();
    }


    public static void Toast(int resId) {
        Toast(MainApp.getMainApp().getString(resId));
    }

//    public static boolean isWifiConnected(Context context) {
//        if (context != null) {
//            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
//                    .getSystemService(Context.CONNECTIVITY_SERVICE);
//            NetworkInfo activityNetwork = mConnectivityManager.getActiveNetworkInfo();
//            return activityNetwork != null && activityNetwork.getType() == ConnectivityManager.TYPE_WIFI;
//        }
//        return false;
//    }


    /**
     * 检查是否有网络
     *
     * @return
     */
    public static boolean isConnected() {

        ConnectivityManager mConnectivityManager = (ConnectivityManager) MainApp.getMainApp()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo != null) {
            return mNetworkInfo.isAvailable();
        }
        return false;
    }

    public static int getVersion() {
        try {
            Context context = MainApp.getMainApp();
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionCode;
        } catch (Exception e) {

        }

        return 1;
    }

    public static String getVersionName() {
        try {
            Context context = MainApp.getMainApp();
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Global.ProcException(e);
        }

        return "";
    }

    public static boolean IsOffice(String filename) {
//        Word：docx、docm、dotm、dotx
//        Excel：xlsx、xlsb、xls、xlsm
//        PowerPoint：pptx、ppsx、ppt、pps、pptm、potm、ppam、potx、ppsm
//?为毛没有doc,先不管

        String fileExt = null;

        int dot = filename.lastIndexOf(".");
        if ((dot > -1) && (dot < (filename.length() - 1))) {
            fileExt = filename.substring(dot + 1).toLowerCase();
        } else {
            return false;
        }

        if (fileExt.endsWith("docx") || fileExt.endsWith("doc") || fileExt.endsWith("docm") || fileExt.endsWith("dotx") || fileExt.endsWith("dotm") ||
                fileExt.endsWith("xlsx") || fileExt.endsWith("xlsb") || fileExt.endsWith("xls") || fileExt.endsWith("xlsm") ||
                fileExt.endsWith("pptx") || fileExt.endsWith("ppsx") || fileExt.endsWith("ppt") || fileExt.endsWith("pps") ||
                fileExt.endsWith("pptm") || fileExt.endsWith("potm") || fileExt.endsWith("ppam") || fileExt.endsWith("potx") || fileExt.endsWith("ppsm")) {
            return true;
        }

        return false;
    }

    public static int getAttachmentIcon(String filename) {
        String fileExt = null;

        int dot = filename.lastIndexOf(".");
        if ((dot > -1) && (dot < (filename.length() - 1))) {
            fileExt = filename.substring(dot + 1).toLowerCase();
        } else {
            return R.drawable.other_file;
        }

        if (fileExt.endsWith("docx") || fileExt.endsWith("doc") || fileExt.endsWith("docm") || fileExt.endsWith("dotx") || fileExt.endsWith("dotm")) {
            return R.drawable.img_file_doc;
        } else if (fileExt.endsWith("xlsx") || fileExt.endsWith("xlsb") || fileExt.endsWith("xls") || fileExt.endsWith("xlsm")) {
            return R.drawable.img_file_xls;
        } else if (fileExt.endsWith("pptx") || fileExt.endsWith("ppsx") || fileExt.endsWith("ppt") || fileExt.endsWith("pps") ||
                fileExt.endsWith("pptm") || fileExt.endsWith("potm") || fileExt.endsWith("ppam") || fileExt.endsWith("potx") || fileExt.endsWith("ppsm")) {
            return R.drawable.img_file_ppt;
        }

        return R.drawable.other_file;
    }

    /**
     * ScroView嵌套listView，手动计算ListView高度
     * */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            if (listItem != null) {
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public static Uri getOutputMediaFileUri() {
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "LeShare");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("LeShare", "failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp
                + ".jpg");

        return Uri.fromFile(mediaFile);
    }

    //压缩图片,并旋转图片
    public static File scal(Context context, Uri fileUri) throws IOException {
        String path = Global.getPath(context, fileUri);

        int degree = readPictureDegree(path);

        File outputFile = new File(path);
        LogUtil.dll("路径：" + path);
        long fileSize = outputFile.length();
        final long fileMaxSize = 100 * 1024;

        if (fileSize >= fileMaxSize) {
            LogUtil.dll("文件大小超限");
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            int height = options.outHeight / 3;
            int width = options.outWidth / 3;

            double scale = Math.sqrt((float) fileSize / fileMaxSize);
            options.outHeight = (int) (height / scale);
            options.outWidth = (int) (width / scale);
            options.inSampleSize = (int) (scale + 0.5);
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeFile(path, options);

            outputFile = getTempFile(context);
            FileOutputStream fos = null;
            fos = new FileOutputStream(outputFile);

            if (degree > 0) {
                bitmap = rotaingImageView(degree, bitmap);
            }

            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);

            fos.close();

            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }

        } else {
            LogUtil.dll("文件大小未超限");
            File tempFile = outputFile;
            outputFile = getTempFile(context);
            copyFileUsingFileChannels(tempFile, outputFile);
        }

        return outputFile;

    }

    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        System.out.println("angle2=" + angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }

        return resizedBitmap;
    }

    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    private static void copyFileUsingFileChannels(File source, File dest)
            throws IOException {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } finally {
            if (inputChannel != null) inputChannel.close();
            if (outputChannel != null) outputChannel.close();
        }
    }

    private static File getTempFile(Context context) {
        File file = null;
        try {
            String fileName = "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            file = File.createTempFile(fileName, ".jpg", context.getCacheDir());
        } catch (IOException e) {
        }
        return file;
    }

    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
