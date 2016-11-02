package com.loyo.oa.v2.activityui.setting.persenter;

import android.os.Handler;
import android.text.TextUtils;

import com.loyo.oa.v2.activityui.setting.viewcontrol.SettingVControl;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.tool.FileTool;
import com.loyo.oa.v2.tool.LogUtil;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

/**
 * Created by xeq on 16/11/2.
 */

public class SettingPControl implements SettingPersenter {
    private SettingVControl vControl;

    public SettingPControl(SettingVControl vControl) {
        this.vControl = vControl;
        bindingData();
        diskCacheInfo();
    }

    @Override
    public void getPageData(Object... pag) {

    }

    @Override
    public void bindPageData(Object obj) {

    }

    @Override
    public void sendPageData(Object obj) {

    }

    @Override
    public void bindingData() {
        if (MainApp.user != null && !TextUtils.isEmpty(MainApp.user.mobile)) {
            vControl.setCell(MainApp.user.mobile);
        }

    }

    /**
     * 设置缓存信息
     */
    @Override
    public void diskCacheInfo() {
        final File cacheDir = StorageUtils.getOwnCacheDirectory(MainApp.getMainApp(), "imageloader/Cache");
        LogUtil.d("缓存路径：" + cacheDir.getPath());
        new Thread(new Runnable() {

            @Override
            public void run() {
                final String length = FileTool.formatFileSize(cacheDir.getPath());
                new Handler().post(new Runnable() {

                    @Override
                    public void run() {
//                        tv_file.setText(length.equals("0B") ? "" : length);
//                        cancelLoading();
                    }
                });
                LogUtil.d("缓存路径文件大小：" + length);
            }
        }).start();
    }
}
