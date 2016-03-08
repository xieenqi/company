package com.loyo.oa.v2.tool;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.common.Global;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 新建客户选择照片的popwindow
 */
public class SelectPicPopupWindow extends Activity implements OnClickListener {

    public static final int GET_IMG = 10;
    private String tag = "SelectPicPopupWindow";
    private TextView btn_take_photo;//拍照
    private TextView btn_pick_photo;//从相册选
    private TextView btn_cancel;//取消
    private LinearLayout layout;
    private Uri fileUri;
    private static final String RESTORE_FILEURI = "fileUri";

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
        if (getIntent() != null && getIntent().getExtras() != null) {
            boolean localpic = getIntent().getBooleanExtra("localpic", false);
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

    public static class ImageInfo implements Serializable {
        public String path;

        public ImageInfo(String path) {
            this.path = path;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK) {
            finish();
            return;
        }

        ArrayList<ImageInfo> pickArray = new ArrayList<>();
        Intent i = new Intent();

        if (requestCode == 1) {
            //拍照
            pickArray.add(new ImageInfo(fileUri.toString()));
            if (pickArray.isEmpty()) {
                setResult(RESULT_CANCELED);
            } else {
                i.putExtra("data", pickArray);
                setResult(RESULT_OK, i);
            }
        } else if(requestCode == 2){
            //选择文件
            if (data.getData() != null) {
                pickArray.add(new ImageInfo(data.getData().toString()));
                i.putExtra("data", pickArray);
                setResult(RESULT_OK, i);
            }
        }
        finish();
        //选择完或者拍完照后会在这里处理，然后我们继续使用setResult返回Intent以便可以传递数据和调用
    }

    private void takePhotoIntent(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = Global.getOutputMediaFileUri();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, 1);
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
                try {
                    //选择照片的时候也一样，我们用Action为Intent.ACTION_GET_CONTENT，
                    //有些人使用其他的Action但我发现在有些机子中会出问题，所以优先选择这个
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_PICK);
                    intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

//                    intent.setType("image/*");
//                    //android 4.4
//                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
//                        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
//                    } else {
//                        intent.setAction(Intent.ACTION_GET_CONTENT);
//                    }

                    startActivityForResult(intent, 2);
                } catch (ActivityNotFoundException e) {
                    Global.ProcException(e);
                }
                break;
            case R.id.btn_cancel:
                finish();
                break;
            default:
                break;
        }
    }
}
