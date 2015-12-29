package com.loyo.oa.v2.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.PreviewOfficeActivity;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Attachment;
import com.loyo.oa.v2.tool.BitmapUtil;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.SelectPicPopupWindow;
import com.loyo.oa.v2.tool.ViewUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;

public class SignInGridViewAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private ArrayList<Attachment> mListData;
    private Activity mActivity;
    private boolean mIsAdd;
    private boolean isCreator;
    ArrayList<File> mBitmaps = new ArrayList<>();
    //是否选择本地图片
    private boolean localpic = false;
    private Uri fileUri;

    public SignInGridViewAdapter(Activity mActivity, ArrayList<Attachment> lstData, boolean mIsAdd, boolean isCreator) {
        if (lstData == null) {
            lstData = new ArrayList<>();
        }

        this.mActivity = mActivity;
        this.mListData = Attachment.Sort(lstData);
        this.mIsAdd = mIsAdd;
        this.isCreator = isCreator;
        layoutInflater = LayoutInflater.from(mActivity);

        for (Attachment at : lstData) {
            if (at.getFile() != null) {
                mBitmaps.add(at.getFile());
            }
        }
    }

    public SignInGridViewAdapter(Activity mActivity, ArrayList<Attachment> lstData, boolean mIsAdd, boolean _localpic, boolean isCreator) {
        this(mActivity, lstData, mIsAdd, isCreator);
        localpic = _localpic;
    }

    public void setDataSource(ArrayList<Attachment> lstData) {
        this.mListData = lstData;
    }

    public ArrayList<Attachment> getDataSource() {
        return mListData;
    }

    @Override
    public int getCount() {
        int size = (mListData == null) ? 0 : mListData.size();

        return mIsAdd ? size + 1 : size;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        Item_info item_info;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_img_ct_browse, parent, false);
            item_info = new Item_info();
            item_info.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            item_info.textView = (TextView) convertView.findViewById(R.id.tv_filename);
            ViewUtil.setViewHigh(item_info.imageView, 1);

            convertView.setTag(item_info);
        } else {
            item_info = (Item_info) convertView.getTag();
        }

        if (position == mListData.size()) {
            item_info.imageView.setImageResource(R.drawable.icon_add_file);
            if (isCreator) {
                item_info.imageView.setOnClickListener(new OnClickListener_addImg());//添加图片
            }
            item_info.imageView.setScaleType(ImageView.ScaleType.CENTER);
        } else {

            final Attachment attachment = mListData.get(position);
            final boolean isPic = (attachment.getAttachmentType() == Attachment.AttachmentType.IMAGE);

            if (isPic) {
                ImageLoader.getInstance().loadImage(attachment.getUrl(), MainApp.options_3,
                        new BitmapUtil.ImageLoadingListener_ClickShowImg(item_info.imageView, position,
                                mListData, R.drawable.default_image, mIsAdd));
            } else {
                //                      显示文件
                item_info.imageView.setImageResource(R.drawable.other_file);
                item_info.textView.setText(attachment.getOriginalName());

                item_info.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LogUtil.d(" 预览图片的URL：" + attachment.getUrl());
                        //预览文件
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("data", attachment.getUrl());
                        MainApp.getMainApp().startActivity(mActivity, PreviewOfficeActivity.class, MainApp.ENTER_TYPE_RIGHT, false, bundle);
                    }
                });
            }
//
//            final File file = attachment.getFile();
//            if (file == null) {
//                if (isPic) {
//                    ImageLoader.getInstance().loadImage(attachment.getUrl(), MainApp.options_3, new BitmapUtil.ImageLoadingListener_ClickShowImg(item_info.imageView, position, mListData, R.drawable.default_image, mIsAdd));
//                } else {
//                    //                      显示文件
//                    item_info.imageView.setImageResource(R.drawable.other_file);
//                    item_info.textView.setText(attachment.getOriginalName());
//                    item_info.imageView.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            //预览文件
//                            Bundle bundle = new Bundle();
//                            bundle.putSerializable("data", attachment.getUrl());
//                            MainApp.getMainApp().startActivity(mActivity, PreviewOfficeActivity.class, MainApp.ENTER_TYPE_RIGHT, false, bundle);
//                        }
//                    });
//                }
//            } else {
//                InputStream is = null;
//                try {
//                    Uri uri = Uri.fromFile(file);
//                    if (uri != null) {
//                        is = mActivity.getContentResolver().openInputStream(uri);
//                        Drawable d = Drawable.createFromStream(is, null);
//                        item_info.imageView.setImageDrawable(d);
//                    }
//                } catch (Exception e) {
//                    Log.e("SignInGridViewAdapter", "Unable to set ImageView from URI: " + e.toString());
//                } finally {
//                    if (is != null) {
//                        try {
//                            is.close();
//                        } catch (Exception ex) {
//                            Global.ProcException(ex);
//                        }
//                    }
//                }
//                if (isPic) {
//                    item_info.imageView.setOnClickListener(new BitmapUtil.OnClickListener_showImg(mActivity, mListData, position, mIsAdd));
//                } else {
//                    item_info.imageView.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            //预览文件
//                            Bundle bundle = new Bundle();
//                            bundle.putSerializable("data", attachment.getUrl());
//                            MainApp.getMainApp().startActivity(mActivity, PreviewOfficeActivity.class, MainApp.ENTER_TYPE_RIGHT, false, bundle);
//                        }
//                    });
//                }
//            }
        }

        return convertView;
    }

    private class OnClickListener_addImg implements View.OnClickListener {

        @Override
        public void onClick(View v) {

/*            try {
                //拍照我们用Action为MediaStore.ACTION_IMAGE_CAPTURE，
                //有些人使用其他的Action但我发现在有些机子中会出问题，所以优先选择这个

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                fileUri = Global.getOutputMediaFileUri();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                mActivity.startActivityForResult(intent, 1);

            } catch (Exception e) {
                Global.ProcException(e);
            }*/

            Intent intent = new Intent(mActivity, SelectPicPopupWindow.class);
            intent.putExtra("localpic", localpic);
            mActivity.startActivityForResult(intent, SelectPicPopupWindow.GET_IMG);
        }
    }

    class Item_info {
        ImageView imageView;
        TextView textView;
    }

    public static void setAdapter(GridView gv, SignInGridViewAdapter adapter) {
        gv.setAdapter(adapter);
        if (adapter.getCount() % 3 == 0) {
            ViewUtil.setViewHigh(gv, (1f / 3f) * (adapter.getCount() / 3));
        } else {
            ViewUtil.setViewHigh(gv, (1f / 3f) * (adapter.getCount() / 3 + 1));
        }
    }
}
