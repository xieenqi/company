package com.loyo.oa.v2.tool.customview.multi_image_selector;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.tool.customview.GeneralPopView;

import java.io.File;
import java.util.ArrayList;

/**
 * 多图选择
 * Created by Nereo on 2015/4/7.
 */
public class MultiImageSelectorActivity extends FragmentActivity implements MultiImageSelectorFragment.Callback {

    /**
     * 最大图片选择次数，int类型，默认9
     */
    public static final String EXTRA_SELECT_COUNT = "max_select_count";
    /**
     * 图片选择模式，默认多选
     */
    public static final String EXTRA_SELECT_MODE = "select_count_mode";
    /**
     * 是否显示相机，默认显示
     */
    public static final String EXTRA_SHOW_CAMERA = "show_camera";
    /**
     * 是否裁剪，默认不裁剪
     */
    public static final String EXTRA_CROP_CIRCLE = "crop_circle";

    /**
     * 选择结果，返回为 ArrayList&lt;String&gt; 图片路径集合
     */
    public static final String EXTRA_RESULT = "select_result";
    /**
     * 默认选择集
     */
    public static final String EXTRA_DEFAULT_SELECTED_LIST = "default_list";

    /**
     * 单选
     */
    public static final int MODE_SINGLE = 0;
    /**
     * 多选
     */
    public static final int MODE_MULTI = 1;

    private static final int REQUEST_CROP_IMAGE = 2;

    private ArrayList<String> resultList = new ArrayList<>();
    private TextView mSubmitButton;
    private int mDefaultCount;
    private boolean isCrop;
    private TextView tv_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default);
        // 返回按钮
        findViewById(R.id.ll_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("图片选择");
        if (PackageManager.PERMISSION_GRANTED ==
                getPackageManager().checkPermission("android.permission.WRITE_EXTERNAL_STORAGE", "com.loyo.oa.v2")
                && PackageManager.PERMISSION_GRANTED ==
                getPackageManager().checkPermission("android.permission.CAMERA", "com.loyo.oa.v2")) {
        } else {
//            showGeneralDialog(true, true, "需要使用储存权限、相机权限\n请在”设置”>“应用”>“权限”中配置权限");
            final GeneralPopView generalPopView = new GeneralPopView(this, true);
            generalPopView.show();
            generalPopView.setMessage("需要使用储存权限、相机权限\n请在”设置”>“应用”>“权限”中配置权限");
            generalPopView.setCanceledOnTouchOutside(false);
            generalPopView.setSureOnclick(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    generalPopView.dismiss();
//                            ActivityCompat.requestPermissions(ContactInfoEditActivity.this,
//                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                                    RESULT_OK);
//                            ActivityCompat.requestPermissions(ContactInfoEditActivity.this,
//                                    new String[]{Manifest.permission.CAMERA},
//                                    RESULT_OK);
                    Utils.doSeting(MultiImageSelectorActivity.this);
                    finish();
                }
            });
            generalPopView.setCancelOnclick(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    generalPopView.dismiss();
                    finish();
                }
            });
            return;
        }
        LogUtil.dee("进入相册选择");
        Intent intent = getIntent();
        mDefaultCount = intent.getIntExtra(EXTRA_SELECT_COUNT, 9);
        int mode = intent.getIntExtra(EXTRA_SELECT_MODE, MODE_MULTI);
        boolean isShow = intent.getBooleanExtra(EXTRA_SHOW_CAMERA, true);
        isCrop = intent.getBooleanExtra(EXTRA_CROP_CIRCLE, false);
        if (mode == MODE_MULTI && intent.hasExtra(EXTRA_DEFAULT_SELECTED_LIST)) {
            resultList = intent.getStringArrayListExtra(EXTRA_DEFAULT_SELECTED_LIST);
        }

        Bundle bundle = new Bundle();
        bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_COUNT, mDefaultCount);
        bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_MODE, mode);
        bundle.putBoolean(MultiImageSelectorFragment.EXTRA_SHOW_CAMERA, isShow);
        bundle.putStringArrayList(MultiImageSelectorFragment.EXTRA_DEFAULT_SELECTED_LIST, resultList);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.image_grid, Fragment.instantiate(this, MultiImageSelectorFragment.class.getName(), bundle))
                .commit();


        // 完成按钮
        mSubmitButton = (TextView) findViewById(R.id.tv_add);
        if (mode == MODE_MULTI)
            mSubmitButton.setVisibility(View.VISIBLE);
        if (resultList == null || resultList.size() <= 0) {
            mSubmitButton.setText("完成");
            mSubmitButton.setEnabled(false);
        } else {
            mSubmitButton.setText("完成(" + resultList.size() + "/" + mDefaultCount + ")");
            mSubmitButton.setEnabled(true);
        }
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (resultList != null && resultList.size() > 0) {
                    // 返回已选择的图片数据
                    Intent data = new Intent();
                    data.putStringArrayListExtra(EXTRA_RESULT, resultList);
                    setResult(RESULT_OK, data);
                    finish();
                }
            }
        });
    }

    @Override
    public void onSingleImageSelected(String path) {
        if (isCrop)
            goToCrop(path);
        else {
            Intent data = new Intent();
            resultList.add(path);
            data.putStringArrayListExtra(EXTRA_RESULT, resultList);
            setResult(RESULT_OK, data);
            finish();
        }
    }

    @Override
    public void onImageSelected(String path) {
        if (!resultList.contains(path)) {
            resultList.add(path);
        }
        // 有图片之后，改变按钮状态
        if (resultList.size() > 0) {
            mSubmitButton.setText("完成(" + resultList.size() + "/" + mDefaultCount + ")");
            if (!mSubmitButton.isEnabled()) {
                mSubmitButton.setEnabled(true);
            }
        }
    }

    @Override
    public void onImageUnselected(String path) {
        if (resultList.contains(path)) {
            resultList.remove(path);
            mSubmitButton.setText("完成(" + resultList.size() + "/" + mDefaultCount + ")");
        } else {
            mSubmitButton.setText("完成(" + resultList.size() + "/" + mDefaultCount + ")");
        }
        // 当为选择图片时候的状态
        if (resultList.size() == 0) {
            mSubmitButton.setText("完成");
            mSubmitButton.setEnabled(false);
        }
    }

    @Override
    public void onCameraShot(File imageFile) {
        if (imageFile != null) {
            if (isCrop)
                goToCrop(imageFile.getAbsolutePath());
            else {
                Intent data = new Intent();
                resultList.add(imageFile.getAbsolutePath());
                data.putStringArrayListExtra(EXTRA_RESULT, resultList);
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }

    /**
     * 裁剪图片
     *
     * @param imgPath
     */
    private void goToCrop(String imgPath) {
        Bundle b = new Bundle();
        b.putString("imgPath", imgPath);
        MainApp.getMainApp().startActivityForResult(this, CropImageActivity_.class, MainApp.ENTER_TYPE_BUTTOM, REQUEST_CROP_IMAGE, b);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CROP_IMAGE && resultCode == RESULT_OK) {
            Intent data1 = new Intent();
            resultList.add(data.getStringExtra("imgPath"));
            data1.putStringArrayListExtra(EXTRA_RESULT, resultList);
            setResult(RESULT_OK, data1);
            finish();
        }
    }
}
