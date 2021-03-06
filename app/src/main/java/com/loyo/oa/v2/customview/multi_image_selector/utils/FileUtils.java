package com.loyo.oa.v2.customview.multi_image_selector.utils;

import android.content.Context;
import android.os.Environment;

import com.loyo.oa.common.utils.DateTool;
import java.io.File;

/**
 * 文件操作类
 * Created by Nereo on 2015/4/8.
 */
public class FileUtils {
    protected FileUtils() {
        throw new UnsupportedOperationException(); // 防止子类调用
    }

    public static File createTmpFile(Context context) {

        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            // 已挂载
            File pic = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
            String fileName = "multi_image_" + DateTool.getFileNameByTime() + "";
            File tmpFile = new File(pic, fileName + ".jpg");
            return tmpFile;
        } else {
            File cacheDir = context.getCacheDir();
//            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
            String fileName = "multi_image_" + DateTool.getFileNameByTime() + "";
            File tmpFile = new File(cacheDir, fileName + ".jpg");
            return tmpFile;
        }

    }

}
