package com.loyo.oa.photo;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.loyo.oa.common.utils.PermissionTool;
import com.loyo.oa.photo.utils.ImageCaptureManager;
import com.loyo.oa.v2.R;

import java.io.IOException;
import java.util.ArrayList;

import static com.loyo.oa.photo.PhotoPicker.KEY_SELECTED_PHOTOS;
import static com.loyo.oa.v2.common.Global.Toast;

public class PhotoCaptureActivity extends AppCompatActivity {

    private ImageCaptureManager captureManager;

    private static int PERMISSIN_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        captureManager = new ImageCaptureManager(this);
        setContentView(R.layout.__picker_activity_photo_capture);
        if (PermissionTool.requestPermission(this,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA }
                , "需要使用储存权限、相机权限", PERMISSIN_CODE))
        {
            openCamera();
        }

    }

    private void openCamera() {
        try {
            Intent intent = captureManager.dispatchTakePictureIntent();
            startActivityForResult(intent, ImageCaptureManager.REQUEST_TAKE_PHOTO);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ImageCaptureManager.REQUEST_TAKE_PHOTO ) {

            if (captureManager == null) {
                captureManager = new ImageCaptureManager(this);
            }
            if (resultCode == RESULT_OK) {
                final String path = captureManager.getCurrentPhotoPath();
                if (!TextUtils.isEmpty(path)) {
                    // captureManager.galleryAddPic();
                    Intent intent = new Intent();
                    ArrayList<String> selectedPhotos = new ArrayList<String>() {{
                        add(path);
                    }};
                    intent.putStringArrayListExtra(KEY_SELECTED_PHOTOS, selectedPhotos);
                    setResult(RESULT_OK, intent);
                }
                else {
                    setResult(RESULT_CANCELED);
                }
            }
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionTool.requestPermissionsResult(permissions, grantResults,
                new PermissionTool.PermissionsResultCallBack() {
            @Override
            public void success() {
                openCamera();
            }

            @Override
            public void fail() {
                Toast("需要使用储存权限、相机权限\n" +
                        "请在”设置”>“应用”>“权限”中配置权限");
                finish();
            }
        });
    }
}
