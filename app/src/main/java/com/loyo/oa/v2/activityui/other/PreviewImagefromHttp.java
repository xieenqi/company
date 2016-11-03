package com.loyo.oa.v2.activityui.other;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.tool.BaseActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import uk.co.senab.photoview.PhotoView;

/**
 * 【预览网络图片】
 * Created by yyy on 16/8/26.
 */
public class PreviewImagefromHttp extends BaseActivity {

    private ProgressBar pb_progress;
    private PhotoView iv_show_httpimage;
    private ImageView back_image;
    private String url;
    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previewimage_fromhttp);
        initUi();
    }

    public void initUi() {

        mIntent = getIntent();
        url = mIntent.getStringExtra(ExtraAndResult.EXTRA_OBJ);

        pb_progress = (ProgressBar) findViewById(R.id.pb_progress);
        iv_show_httpimage = (PhotoView) findViewById(R.id.iv_show_httpimage);
        back_image = (ImageView) findViewById(R.id.back_image);

        back_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        /*ImageView进度条设置*/
        ImageLoader.getInstance().displayImage(url, iv_show_httpimage, MainApp.options_3, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
                pb_progress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                pb_progress.setVisibility(View.INVISIBLE);
                iv_show_httpimage.setImageResource(R.drawable.img_file_null);
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                pb_progress.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });
    }
}
