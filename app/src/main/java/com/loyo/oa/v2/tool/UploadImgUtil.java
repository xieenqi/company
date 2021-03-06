package com.loyo.oa.v2.tool;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.loyo.oa.photo.PhotoPreview;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;


/**
 * 【附件工具】主要是监听
 * */

public class UploadImgUtil {

    protected UploadImgUtil() {
        throw new UnsupportedOperationException(); // 防止子类调用
    }

    public static class ImageLoadingListener_ClickShowImg implements ImageLoadingListener {
        ImageView imageView;
        int cancelled_rid = 0;
        int failed_rid = 0;
        int started_rid = 0;
        int def_rid = 0;
        ArrayList<ImageInfo> attachments;
        int postion;
        boolean isEdit;

        public ImageLoadingListener_ClickShowImg(ImageView imageView, int _postion, ArrayList<ImageInfo> _attachments, int def_rid, boolean isEdit) {
            this(imageView, def_rid, def_rid, def_rid, def_rid, isEdit);
            attachments = _attachments;
            postion = _postion;
        }

        public ImageLoadingListener_ClickShowImg(ImageView imageView, int def_rid, int cancelled_rid, int failed_rid, int started_rid, boolean isEdit) {
            this.imageView = imageView;
            this.def_rid = def_rid;
            this.cancelled_rid = cancelled_rid;
            this.failed_rid = failed_rid;
            this.started_rid = started_rid;
            this.isEdit = isEdit;
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (ListUtil.IsEmpty(attachments) || attachments.get(postion) == null) {
                return;
            }

            if (imageView != null && loadedImage != null) {
                imageView.setOnClickListener(new OnClickListener_showImg(imageView.getContext(), attachments, postion, isEdit));
                imageView.setImageBitmap(loadedImage);
            }
        }

        @Override
        public void onLoadingCancelled(String arg0, View arg1) {
        }

        @Override
        public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
        }

        @Override
        public void onLoadingStarted(String arg0, View arg1) {
            imageView.setImageResource(def_rid);
        }
    }

    /**
     * Drawable转化为Bitmap
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;

    }

    /**
     * Bitmap to Drawable
     *
     * @param bitmap
     * @param mcontext
     * @return
     */
    public static Drawable bitmapToDrawble(Bitmap bitmap, Context mcontext) {
        Drawable drawable = new BitmapDrawable(mcontext.getResources(), bitmap);
        return drawable;
    }


    public static class OnClickListener_showImg implements OnClickListener {
        ArrayList<ImageInfo> attachments;
        Context mContext;
        int position;
        boolean isEdit;


        public OnClickListener_showImg(Context context, ArrayList<ImageInfo> _attachments, int _postion, boolean _isEdit) {
            attachments = _attachments;
            position = _postion;
            isEdit = _isEdit;
            mContext = context;
        }

        @Override
        public void onClick(View v) {
            if (ListUtil.IsEmpty(attachments)) {
                return;
            }
            ArrayList<String> selectedPhotos = new ArrayList<>();

            for (int i = 0; i < attachments.size(); i++) {
                String path = attachments.get(i).path;
                if (path.startsWith("file://"));
                {
                    path = path.replace("file://", "");
                }
                selectedPhotos.add(path);
            }
            PhotoPreview.builder()
                    .setPhotos(selectedPhotos)
                    .setCurrentItem(position)
                    .setShowDeleteButton(true)
                    .start((Activity) mContext);

        }
    }
}
