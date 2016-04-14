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
import com.loyo.oa.v2.tool.customview.HackyViewPager;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;

/**
 * 预览图片
 */
public class PreviewImageActivity extends BaseActivity {

    private ViewPager mViewPager;
    private ArrayList<Attachment> mNewAttachments = null;
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
//        mViewPager.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
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
                intent.putExtra("delAtm", mNewAttachments.get(mPosition));
                MainApp.getMainApp().finishActivity(PreviewImageActivity.this, MainApp.ENTER_TYPE_TOP, RESULT_OK, intent);
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

            PhotoView photoView = new PhotoView(container.getContext());
            Attachment attachment = mNewAttachments.get(position);
            File imgFile = attachment.getFile();
            LogUtil.d("预览图片的url：" + attachment.getUrl());
            if (imgFile != null) {
                photoView.setImageURI(Uri.fromFile(imgFile));
            } else {

                ImageLoader.getInstance().displayImage(attachment.getUrl(), photoView);

            }
            LogUtil.d("预览 转换 的url：" + bigImagUrl(attachment.getUrl()));
            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            photoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUtil.d("点击图片：");
                }
            });
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

    public String bigImagUrl(final String url) {
        String newUrl = url.replaceAll("loyocloud-01.img-cn-qingdao.aliyuncs.com", "loyocloud-01.oss-cn-qingdao.aliyuncs.com");

        return newUrl.replaceAll("@1e_1c_0o_0l_200h_200w_70q.src", "");
    }
}
