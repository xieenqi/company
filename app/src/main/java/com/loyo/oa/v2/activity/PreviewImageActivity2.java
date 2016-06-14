package com.loyo.oa.v2.activity;

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
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.SelectPicPopupWindow;
import com.loyo.oa.v2.tool.customview.HackyViewPager;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import uk.co.senab.photoview.PhotoView;

/**
 * 预览图片
 */
public class PreviewImageActivity2 extends BaseActivity {

    private ViewPager mViewPager;
    private ArrayList<SelectPicPopupWindow.ImageInfo> mNewAttachments = null;
    private TextView delete;

    private int mPosition;
    private int mNewPosition = 0;
    private boolean isEdit;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        isEdit = getIntent() == null || !getIntent().hasExtra("isEdit") ? false : getIntent().getBooleanExtra("isEdit", false);
        if (isEdit) {
            delete = (TextView) findViewById(R.id.delete_image);
            delete.setOnTouchListener(Global.GetTouch());
            delete.setVisibility(View.VISIBLE);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    dialogToast();
                }
            });
        }


        if (getIntent().hasExtra("data")) {
            ArrayList<SelectPicPopupWindow.ImageInfo> attachments = (ArrayList<SelectPicPopupWindow.ImageInfo>) getIntent().getSerializableExtra("data");
            int position = getIntent().getIntExtra("position", 0);

            for (SelectPicPopupWindow.ImageInfo imageInfo : attachments) {
                    if (mNewAttachments == null) {
                        mNewAttachments = new ArrayList<>();
                    }

                    mNewAttachments.add(imageInfo);

                    if (imageInfo.equals(attachments.get(position))) {
                        mNewPosition = position;
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
        mViewPager.setAdapter(new SamplePagerAdapter());
        mViewPager.setCurrentItem(mNewPosition);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {
                mPosition = position;
            }

            @Override
            public void onPageSelected(final int position) {

            }

            @Override
            public void onPageScrollStateChanged(final int state) {

            }
        });
    }

    /**
     * 删除提示框
     */
    public void dialogToast() {
        showGeneralDialog(true, true, "是否删除附件?");
        //确认
        generalPopView.setSureOnclick(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                generalPopView.dismiss();
                Intent intent = new Intent();
                intent.putExtra("position",mPosition);
                MainApp.getMainApp().finishActivity(PreviewImageActivity2.this, MainApp.ENTER_TYPE_TOP, RESULT_OK, intent);
            }
        });

        //取消
        generalPopView.setCancelOnclick(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                generalPopView.dismiss();
            }
        });
    }

    class SamplePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mNewAttachments.size();
        }

        @Override
        public View instantiateItem(final ViewGroup container, final int position) {
            File imgFile = null;
            PhotoView photoView = new PhotoView(container.getContext());
            SelectPicPopupWindow.ImageInfo imageInfo = mNewAttachments.get(position);
            Uri uri = Uri.parse(imageInfo.path);
            try {
                imgFile = Global.scal(mContext, uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            LogUtil.d("预览图片的url：" + imageInfo.path);
            if (imgFile != null) {
                photoView.setImageURI(Uri.fromFile(imgFile));
            } else {
                ImageLoader.getInstance().displayImage(imageInfo.path, photoView);

            }
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            return photoView;
        }

        @Override
        public void destroyItem(final ViewGroup container, final int position, final Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(final View view, final Object object) {
            return view == object;
        }
    }
}
