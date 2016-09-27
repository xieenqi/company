package com.loyo.oa.v2.tool;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customview.SweetAlertDialogView;
import com.loyo.oa.v2.customview.multi_image_selector.MultiImageSelectorActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * 【附件上传】自定义popwindow
 * <p/>
 * create by yyy on 2016/03/11
 */
public class SelectPicPopupWindow extends Activity implements OnClickListener {

    private static final String RESTORE_FILEURI = "fileUri";
    private List<String> mSelectPath;

    private TextView btn_take_photo;
    private TextView btn_pick_photo;
    private TextView btn_cancel;
    private LinearLayout layout;
    private Intent mIntent;
    private Uri fileUri;
    private int imgSize;
    private boolean addpg;
    private boolean localpic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ExitActivity.getInstance().addActivity(this);
        setContentView(R.layout.dialog_get_img);
        btn_take_photo = (TextView) this.findViewById(R.id.btn_take_photo);
        btn_pick_photo = (TextView) this.findViewById(R.id.btn_pick_photo);
        btn_cancel = (TextView) this.findViewById(R.id.btn_cancel);

        layout = (LinearLayout) findViewById(R.id.pop_layout);
        // 添加选择窗口范围监听可以优先获取触点，即不再执行onTouchEvent()函数，点击其他地方时执行onTouchEvent()函数销毁Activity
        layout.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), "提示：点击窗口外部关闭窗口！",
                        Toast.LENGTH_SHORT).show();
            }
        });

        btn_cancel.setOnClickListener(this);
        btn_pick_photo.setOnClickListener(this);
        btn_take_photo.setOnClickListener(this);

        /**判断是直接调用相机，还是弹出选相框*/
        if (null != getIntent() && null != getIntent().getExtras()) {
            addpg = getIntent().getBooleanExtra("addpg", false);
            localpic = getIntent().getBooleanExtra("localpic", false);
            imgSize = getIntent().getIntExtra("imgsize", 0);

            if (!addpg) {
                imgSize = 9;
            }

            if (!localpic) {
                takePhotoIntent();
            }
        }
    }

    @Override
    protected void onDestroy() {
        ExitActivity.getInstance().removeActivity(this);
        super.onDestroy();
    }

    // 实现onTouchEvent触屏函数但点击屏幕时销毁本Activity
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        fileUri = savedInstanceState.getParcelable(RESTORE_FILEURI);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (fileUri != null) {
            outState.putParcelable(RESTORE_FILEURI, fileUri);
        }
    }

    /*本地展示调用，记得前面加文件路径 "file://" */
    public static class ImageInfo implements Serializable {
        public String path;

        public ImageInfo(String path) {
            this.path = path;
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {

            /*拍照*/
            case R.id.btn_take_photo:
                try {
                    //拍照我们用Action为MediaStore.ACTION_IMAGE_CAPTURE，
                    //有些人使用其他的Action但我发现在有些机子中会出问题，所以优先选择这个
                    takePhotoIntent();
                } catch (Exception e) {
                    Global.ProcException(e);
                }
                break;

            /*从相册选*/
            case R.id.btn_pick_photo:
                //dealPermisson();

                Intent intent = new Intent(this, MultiImageSelectorActivity.class);
                // 是否显示拍摄图片
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
                // 最大可选择图片数量
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, imgSize);
                // 选择模式
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
                intent.putExtra(MultiImageSelectorActivity.EXTRA_CROP_CIRCLE, false);
                startActivityForResult(intent, MainApp.PICTURE);

                break;

            case R.id.btn_cancel:
                finish();
                break;

            default:
                break;
        }
    }

    /**
     * 处理拍照权限
     */
    private void takePhotoIntent() {
        if (PackageManager.PERMISSION_GRANTED ==
                getPackageManager().checkPermission("android.permission.WRITE_EXTERNAL_STORAGE", "com.loyo.oa.v2")
                && PackageManager.PERMISSION_GRANTED ==
                getPackageManager().checkPermission("android.permission.CAMERA", "com.loyo.oa.v2")) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            fileUri = Global.getOutputMediaFileUri();
            LogUtil.d("相机路径：" + fileUri);
            if (null != fileUri) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent, MainApp.PHOTO);
            } else {
                Global.Toast("相机不可用");
            }
        } else {

            final SweetAlertDialogView sDialog = new SweetAlertDialogView(this);
            sDialog.alertHandle(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sDialog.sweetAlertDialog.dismiss();
                    finish();
                }
            }, new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sDialog.sweetAlertDialog.dismiss();
                    Utils.doSeting(SelectPicPopupWindow.this);
                    finish();
                }
            },"提示","需要使用储存权限、相机权限\n请在”设置”>“应用”>“权限”中配置权限");
        }
    }

    /**
     * 处理相册权限
     */
    public void dealPermisson() {
        if (PackageManager.PERMISSION_GRANTED ==
                getPackageManager().checkPermission("android.permission.WRITE_EXTERNAL_STORAGE", "com.loyo.oa.v2")) {
            try {
                //选择照片的时候也一样，我们用Action为Intent.ACTION_GET_CONTENT，
                //有些人使用其他的Action但我发现在有些机子中会出问题，所以优先选择这个
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, MainApp.PICTURE);
            } catch (ActivityNotFoundException e) {
                Global.ProcException(e);
            }
        } else {

            final SweetAlertDialogView sDialog = new SweetAlertDialogView(this);
            sDialog.alertHandle(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sDialog.sweetAlertDialog.dismiss();
                    finish();
                }
            }, new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sDialog.sweetAlertDialog.dismiss();
                    Utils.doSeting(SelectPicPopupWindow.this);
                }
            },"提示","需要使用储存权限\n请在”设置”>“应用”>“权限”中配置权限");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            finish();
            return;
        }

        ArrayList<ImageInfo> pickArray = new ArrayList<>();
        mIntent = new Intent();

        switch (requestCode) {

            //拍照回调
            case MainApp.PHOTO:
                pickArray.add(new ImageInfo(fileUri.toString()));
                if (pickArray.isEmpty()) {
                    setResult(RESULT_CANCELED);
                } else {
                    mIntent.putExtra("data", pickArray);
                    setResult(RESULT_OK, mIntent);
                }
                break;

            //相册选择回调
            case MainApp.PICTURE:
                if (null != data) {
                    mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                    for (String path : mSelectPath) {
                        pickArray.add(new ImageInfo("file://" + path));
                    }
                    mIntent.putExtra("data", pickArray);
                    setResult(RESULT_OK, mIntent);
                }
                break;

        }
        finish();
    }
}
