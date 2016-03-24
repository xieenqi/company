package com.loyo.oa.v2.tool;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.loyo.oa.v2.activity.PreviewImageActivity;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Attachment;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class BitmapUtil {
    protected BitmapUtil() {
        throw new UnsupportedOperationException(); // 防止子类调用
    }
//    /**
//     * @param view
//     * @return
//     */
//    public static Bitmap getBitmapFromView(View view) {
//        view.destroyDrawingCache();
//        view.measure(View.MeasureSpec.makeMeasureSpec(0,
//                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
//                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
//        view.setDrawingCacheEnabled(true);
//        Bitmap bitmap = view.getDrawingCache(true);
//        return bitmap;
//    }
//
//    /**
//     * 从资源中获取Bitmap
//     *
//     * @param act
//     * @param resId
//     * @return
//     */
//    public static Bitmap getBitmapFromResources(Activity act, int resId) {
//        Resources res = act.getResources();
//        return BitmapFactory.decodeResource(res, resId);
//    }
//
//    /**
//     * 获得SD卡中的图片
//     *
//     * @param path
//     * @return
//     */
//    public static Bitmap getBitmapFromPath(String path) {
//        return BitmapFactory.decodeFile(path);
//    }
//
//    /**
//     * 将Drawable转换成 Bitmap
//     *
//     * @param drawable
//     * @return
//     */
//    public static Bitmap convertDrawable2BitmapSimple(Drawable drawable) {
//        BitmapDrawable bd = (BitmapDrawable) drawable;
//        return bd.getBitmap();
//    }
//
//    /**
//     * Bitmap转换成 Drawable
//     *
//     * @param bitmap
//     * @return
//     */
//    public static Drawable convertBitmap2Drawable(Bitmap bitmap) {
//        BitmapDrawable bd = new BitmapDrawable(bitmap);
//        // 因为BtimapDrawable是Drawable的子类，最终直接使用bd对象即可。
//        return bd;
//    }
//
//    /**
//     * 将 byte[]转换成Bitmap
//     *
//     * @param b
//     * @return
//     */
//    public static Bitmap convertBytes2Bimap(byte[] b) {
//        if (b.length == 0) {
//            return null;
//        }
//        return BitmapFactory.decodeByteArray(b, 0, b.length);
//    }
//
//    /**
//     * 将 Bitmap转换成byte[]
//     *
//     * @param bm
//     * @param flag 1 jpg; 2 png;
//     * @return
//     */
//    public static byte[] convertBitmap2Bytes(Bitmap bm, int flag) {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        if (flag == 1)
//            bm.compress(CompressFormat.JPEG, 100, baos);
//        bm.compress(CompressFormat.PNG, 100, baos);
//        return baos.toByteArray();
//    }
//
//    /**
//     * 裁剪图片方法实现
//     *
//     * @param uri 进行裁剪的图片的uri
//     */
//    public static void startPhotoZoom(Activity mActivity, Uri uri,
//                                      int requestcode) {
//        Intent intent = new Intent("com.android.camera.action.CROP");
//        intent.setDataAndType(uri, "image/*");
//        // 设置裁剪
//        intent.putExtra("crop", "true");
//        // aspectX aspectY 是宽高的比例
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 1);
//        // outputX outputY 是裁剪图片宽高
//        intent.putExtra("outputX", 100);
//        intent.putExtra("outputY", 100);
//        intent.putExtra("return-data", true);
//        mActivity.startActivityForResult(intent, requestcode);
//    }
//
//    /**
//     * 将base64string转换成bitmap
//     *
//     * @param string
//     * @return
//     */
//    @TargetApi(Build.VERSION_CODES.FROYO)
//    public static Bitmap stringtoBitmap(String string) {
//        Bitmap bitmap = null;
//        try {
//            byte[] bitmapArray;
//            bitmapArray = Base64.decode(string, Base64.DEFAULT);
//            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
//                    bitmapArray.length);
//        } catch (Exception e) {
//            Global.ProcException(e);
//            return bitmap;
//        }
//        return bitmap;
//    }
//
//    /**
//     * 将Bitmap转换成BASE64字符串
//     *
//     * @param bitmap
//     * @return
//     */
//    @TargetApi(Build.VERSION_CODES.FROYO)
//    public static String bitmaptoBase64String(Bitmap bitmap) {
//        String string = null;
//        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
//        bitmap.compress(CompressFormat.PNG, 100, bStream);
//        byte[] bytes = bStream.toByteArray();
//        string = Base64.encodeToString(bytes, Base64.DEFAULT);
//        return string;
//    }
//
//    /**
//     * 获取裁剪后的圆形图片
//     *
//     * @param radius 半径
//     */
//    public static Bitmap getCroppedRoundBitmap(Bitmap bmp, int radius) {
//        Bitmap scaledSrcBmp;
//        // int diameter = radius * 2;
//        int diameter = radius;
//        // 为了防止宽高不相等，造成圆形图片变形，因此截取长方形中处于中间位置最大的正方形图片
//        int bmpWidth = bmp.getWidth();
//        int bmpHeight = bmp.getHeight();
//        int squareWidth;
//        int squareHeight;
//        int x, y;
//        Bitmap squareBitmap;
//        if (bmpHeight > bmpWidth) {// 高大于宽
//            squareWidth = squareHeight = bmpWidth;
//            x = 0;
//            y = (bmpHeight - bmpWidth) / 2;
//            // 截取正方形图片
//            squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth,
//                    squareHeight);
//        } else if (bmpHeight < bmpWidth) {// 宽大于高
//            squareWidth = squareHeight = bmpHeight;
//            x = (bmpWidth - bmpHeight) / 2;
//            y = 0;
//            squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth,
//                    squareHeight);
//        } else {
//            squareBitmap = bmp;
//        }
//        if (squareBitmap.getWidth() != diameter
//                || squareBitmap.getHeight() != diameter) {
//            scaledSrcBmp = Bitmap.createScaledBitmap(squareBitmap, diameter,
//                    diameter, true);
//        } else {
//            scaledSrcBmp = squareBitmap;
//        }
//        Bitmap output = Bitmap.createBitmap(scaledSrcBmp.getWidth(),
//                scaledSrcBmp.getHeight(), Config.ARGB_4444);
//        Canvas canvas = new Canvas(output);
//
//        Paint paint = new Paint();
//        Rect rect = new Rect(0, 0, scaledSrcBmp.getWidth(),
//                scaledSrcBmp.getHeight());
//
//        paint.setAntiAlias(true);
//        paint.setFilterBitmap(true);
//        paint.setDither(true);
//        canvas.drawARGB(0, 0, 0, 0);
//        canvas.drawCircle(scaledSrcBmp.getWidth() / 2,
//                scaledSrcBmp.getHeight() / 2, scaledSrcBmp.getWidth() / 2,
//                paint);
//        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
//        canvas.drawBitmap(scaledSrcBmp, rect, rect, paint);
//        return output;
//    }
//
//    /**
//     * 图片按比例大小压缩方法（根据Bitmap图片压缩）
//     */
//    public static Bitmap comp(Bitmap image) {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        image.compress(CompressFormat.JPEG, 100, baos);
//        if (baos.toByteArray().length / 1024 > 500) {// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
//            baos.reset();// 重置baos即清空baos
//            image.compress(CompressFormat.JPEG, 50, baos);// 这里压缩50%，把压缩后的数据存放到baos中
//        }
//        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
//        BitmapFactory.Options newOpts = new BitmapFactory.Options();
//        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
//        newOpts.inJustDecodeBounds = true;
//        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
//        newOpts.inJustDecodeBounds = false;
//        int photoW = newOpts.outWidth;
//        int photoH = newOpts.outHeight;
//        /* 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
//        float hh = 480f;// 这里设置高度为800f
//		float ww = 800f;// 这里设置宽度为480f
//		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
//		int be = 1;// be=1表示不缩放
//		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
//			be = (int) (newOpts.outWidth / ww);
//		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
//			be = (int) (newOpts.outHeight / hh);
//		}
//		if (be <= 0)
//			be = 1;
//		newOpts.inSampleSize = be;// 设置缩放比例
//*/
//        int scaleFactor = 1;
//        int max = 800;
//        scaleFactor = Math.max(photoW / max, photoH / max);
//
//        if (scaleFactor <= 0) {
//            scaleFactor = 1;
//        }
//        newOpts.inSampleSize = scaleFactor;// 设置缩放比例
//        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
//        isBm = new ByteArrayInputStream(baos.toByteArray());
//        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
//        // return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
//        return bitmap;
//    }
//
//    /**
//     * 质量压缩
//     *
//     * @param image
//     * @return
//     */
//    public static Bitmap compressImage(Bitmap image) {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        image.compress(CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
//        int options = 100;
//        while (baos.toByteArray().length / 1024 > 15) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
//            baos.reset();// 重置baos即清空baos
//            image.compress(CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
//            options -= 5;// 每次都减少5
//        }
//        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
//        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
//        return bitmap;
//    }
//
//    public int judgeImagePosition(String imagepath, String imageName) {
//        int position = 0;
//        File file = new File(imagepath);
//        if (!file.exists()) {
//            return position;
//        }
//        File[] array = file.listFiles();
//        if (array != null) {
//            for (int i = 0; i < array.length; i++) {
//                String name = array[i].getAbsolutePath();
//                if (name.equals(imagepath + imageName)) {
//                    position = i;
//                }
//            }
//        }
//
//        return position;
//    }
//
//    public static Bitmap getSmallBitmap(String filePath) {
//        final BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(filePath, options);
//
//        // Calculate inSampleSize
//        options.inSampleSize = calculateInSampleSize(options, 800, 480);
//
//        // Decode bitmap with inSampleSize set
//        options.inJustDecodeBounds = false;
//
//        return BitmapFactory.decodeFile(filePath, options);
//    }


    public static class ImageLoadingListener_ClickShowImg implements ImageLoadingListener {
        ImageView imageView;
        int cancelled_rid = 0;
        int failed_rid = 0;
        int started_rid = 0;
        int def_rid = 0;
        ArrayList<Attachment> attachments;
        int postion;
        boolean isEdit;

//        public ImageLoadingListener_ClickShowImg(ImageView imageView, int def_rid) {
//            this(imageView, def_rid, def_rid, def_rid, def_rid);
//        }

//        public ImageLoadingListener_ClickShowImg(ImageView imageView) {
//            this(imageView, 0, 0, 0, 0);
//        }

        public ImageLoadingListener_ClickShowImg(ImageView imageView, int _postion, ArrayList<Attachment> _attachments, int def_rid, boolean isEdit) {
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
            //fixes bug291 , ykb 07-13
            if (ListUtil.IsEmpty(attachments) || attachments.get(postion) == null) {
                return;
            }

            if (imageView != null && loadedImage != null) {

                if (attachments.get(postion).getFile() == null) {
                    Attachment att = attachments.get(postion);

                    File mediaStorageDir = new File(
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "LeShare");

                    if (!mediaStorageDir.exists()) {
                        mediaStorageDir.mkdir();
                    }

                    File file = new File(mediaStorageDir, att.getName());
                    if (!file.exists()) {
                        try {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            loadedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                            FileOutputStream fos = new FileOutputStream(file);
                            fos.write(baos.toByteArray());

                            fos.flush();
                            fos.close();

                            baos.flush();
                            baos.close();

                        } catch (Exception e) {
                            Global.ProcException(e);
                        }
                    }

                    att.saveFile(file);
                }

                imageView.setOnClickListener(new OnClickListener_showImg(imageView.getContext(), attachments, postion, isEdit));
                imageView.setImageBitmap(loadedImage);
            }
        }

        @Override
        public void onLoadingCancelled(String arg0, View arg1) {
//            Log.d("ImageLoadingListener_ClickShowImg", "onLoadingCancelled() ");
//            if (cancelled_rid != 0) {
//                imageView.setImageResource(cancelled_rid);
//            }
        }

        @Override
        public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
//            Log.d("ImageLoadingListener_ClickShowImg", "onLoadingFailed() ");
//            if (failed_rid != 0) {
//                imageView.setImageResource(def_rid);
//            }
        }

        @Override
        public void onLoadingStarted(String arg0, View arg1) {
//            Log.d("ImageLoadingListener_ClickShowImg", "onLoadingStarted() ");
//            if (started_rid != 0) {
            imageView.setImageResource(def_rid);
//            }
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
        ArrayList<Attachment> attachments;
        Context mContext;
        int position;
        boolean isEdit;


        public OnClickListener_showImg(Context context, ArrayList<Attachment> _attachments, int _postion, boolean _isEdit) {
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

            ArrayList<Attachment> newAttachment = new ArrayList<>();
            int newPosistion = 0;

            for (int i = 0; i < attachments.size(); i++) {
                Attachment attachment = attachments.get(i);
                if (attachment.getAttachmentType() != Attachment.AttachmentType.IMAGE)
                    continue;

                newAttachment.add(attachment);
            }
            for (int i = 0; i < newAttachment.size(); i++) {
                if (newAttachment.get(i).equals(attachments.get(position))) {
                    newPosistion = i;
                }
                LogUtil.d("yula预览的图片：" + newAttachment.get(i).url);
            }
//            Bundle bundle = new Bundle();
//            bundle.putSerializable("data", newAttachment);
//            bundle.putInt("position", newPosistion);
//            MainApp.getMainApp().startActivity((Activity) mContext, PreviewImageActivity.class, MainApp.ENTER_TYPE_BUTTOM, false, bundle);
//
            Bundle bundle = new Bundle();
            bundle.putSerializable("data", newAttachment);
            bundle.putInt("position", newPosistion);
            bundle.putBoolean("isEdit", isEdit);
//            MainApp.getMainApp().startActivity((Activity) mContext, PreviewImageActivity.class, MainApp.ENTER_TYPE_BUTTOM, false, bundle);
            MainApp.getMainApp().startActivityForResult((Activity) mContext, PreviewImageActivity.class,
                    MainApp.ENTER_TYPE_BUTTOM, FinalVariables.REQUEST_DEAL_ATTACHMENT, bundle);


        }
    }

}
