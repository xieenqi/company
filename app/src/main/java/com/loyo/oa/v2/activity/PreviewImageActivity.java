package com.loyo.oa.v2.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Attachment;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.tool.customview.HackyViewPager;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;

/**
 * 预览图片
 */
public class PreviewImageActivity extends Activity {

    private ViewPager mViewPager;
    private ArrayList<Attachment> mNewAttachments = null;
    private TextView delete;

    private int mPosition;
    private int mNewPosition = 0;
    private boolean isEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        isEdit = getIntent() == null || !getIntent().hasExtra("isEdit") ? false : getIntent().getBooleanExtra("isEdit", false);
        if (isEdit) {
            delete = (TextView) findViewById(R.id.delete_image);
            delete.setOnTouchListener(Global.GetTouch());
            delete.setVisibility(View.VISIBLE);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //删除
                    AlertDialog.Builder builder = new AlertDialog.Builder(PreviewImageActivity.this);
                    builder.setTitle("确认");
                    builder.setPositiveButton(getString(R.string.dialog_submit), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent();
                            intent.putExtra("delAtm", mNewAttachments.get(mPosition));
                            MainApp.getMainApp().finishActivity(PreviewImageActivity.this, MainApp.ENTER_TYPE_TOP, RESULT_OK, intent);
                        }
                    });

                    builder.setNegativeButton(getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setMessage("是否删除附件?");
                    builder.show();
                }
            });
        }


        if (getIntent().hasExtra("data")) {
            ArrayList<Attachment> attachments = (ArrayList<Attachment>) getIntent().getSerializableExtra("data");
            int position = getIntent().getIntExtra("position", 0);

            for (Attachment attachment : attachments) {
                if ((attachment.getAttachmentType() == Attachment.AttachmentType.IMAGE)) {
                    if (mNewAttachments == null) {
                        mNewAttachments = new ArrayList<>();
                    }

                    mNewAttachments.add(attachment);

                    if (attachment.equals(attachments.get(position))) {
                        mNewPosition = position;
                    }
                }
            }

            if (mNewAttachments == null || mNewAttachments.size() == 0) {
                Global.Toast("没有有效的图片!");
                this.finish();
            }
        } else {
            Global.Toast("没有有效的图片!");
            this.finish();
        }

        mViewPager = (HackyViewPager) findViewById(R.id.view_pager);
//        setContentView(mViewPager);

        mViewPager.setAdapter(new SamplePagerAdapter());
        mViewPager.setCurrentItem(mNewPosition);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mPosition = position;
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

//        if (getIntent().hasExtra("position")) {
//            int position = getIntent().getIntExtra("position", 0);
//            if (position < mNewAttachments.size()) {
//                mViewPager.setCurrentItem(position);
//            }
//        }

//        if (savedInstanceState != null) {
//            boolean isLocked = savedInstanceState.getBoolean(ISLOCKED_ARG, false);
//            ((HackyViewPager) mViewPager).setLocked(isLocked);
//        }

    }

    class SamplePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mNewAttachments.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());

            Attachment attachment = mNewAttachments.get(position);
            File imgFile = attachment.getFile();
            if (imgFile != null) {
                photoView.setImageURI(Uri.fromFile(imgFile));
            } else {
                ImageLoader.getInstance().displayImage(attachment.getUrl(), photoView);
            }

            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

}
