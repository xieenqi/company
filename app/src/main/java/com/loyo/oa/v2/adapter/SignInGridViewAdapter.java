package com.loyo.oa.v2.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.ui.activity.other.PreviewOfficeActivity;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Attachment;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.tool.BitmapUtil;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.SelectPicPopupWindow;
import com.loyo.oa.v2.tool.ViewUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.io.File;
import java.util.ArrayList;


/**
 * 【附件GridView】通用
 * */

public class SignInGridViewAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private ArrayList<Attachment> mListData;
    private Activity mActivity;
    private ArrayList<File> mBitmaps = new ArrayList<>();
    private int fromPage;

    private boolean mIsAdd;
    private boolean isCreator;
    private boolean localpic = false; //是否可以选择相册

    public SignInGridViewAdapter(final Activity mActivity, ArrayList<Attachment> lstData, final boolean mIsAdd, final boolean isCreator, final int fromPage) {
        if (lstData == null) {
            lstData = new ArrayList<>();
        }
        this.fromPage = fromPage;
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

    public SignInGridViewAdapter(final Activity mActivity, final ArrayList<Attachment> lstData, final boolean mIsAdd, final boolean _localpic, final boolean isCreator, final int fromPage) {
        this(mActivity, lstData, mIsAdd, isCreator, fromPage);
        localpic = _localpic;
    }

    public void setDataSource(final ArrayList<Attachment> lstData) {
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
    public Object getItem(final int position) {
        return null;
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

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
        item_info.setContent(position);
        return convertView;
    }

    class Item_info {
        ImageView imageView;
        TextView textView;

        public void setContent(final int position) {

            if (position == mListData.size()) {
                if(mListData.size() == 9){
                    imageView.setVisibility(View.GONE);
                }else{
                    imageView.setImageResource(R.drawable.icon_add_file);
                    imageView.setBackgroundResource(R.drawable.icon_add_file);
                }
                if (isCreator) {
                    imageView.setOnClickListener(new OnClickListener_addImg());//添加图片
                }
                imageView.setScaleType(ImageView.ScaleType.CENTER);
            } else {

                final Attachment attachment = mListData.get(position);
                final boolean isImage = (attachment.getAttachmentType() == Attachment.AttachmentType.IMAGE);

                if (isImage) {
                    ImageLoader.getInstance().loadImage(mIsAdd ? attachment.getUrl() : setImgUrl(attachment.getUrl()), MainApp.options_3,
                            new BitmapUtil.ImageLoadingListener_ClickShowImg(imageView, position,
                                    mListData, R.drawable.default_image, mIsAdd));
                } else {
                    //显示文件
                    imageView.setImageResource(R.drawable.other_file);
                    textView.setText(attachment.getOriginalName());

                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View view) {
                            LogUtil.d(" 预览文件的URL：" + attachment.getUrl());
                            //预览文件
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("data", attachment.getUrl());
                            MainApp.getMainApp().startActivity(mActivity, PreviewOfficeActivity.class, MainApp.ENTER_TYPE_RIGHT, false, bundle);
                        }
                    });
                }
            }
        }
    }

    /**
     * 添加图片操作
     */
    private class OnClickListener_addImg implements View.OnClickListener {
        @Override
        public void onClick(final View v) {
            /*考勤*/
            if (fromPage == ExtraAndResult.FROMPAGE_ATTENDANCE) {
                if (mListData.size() == 3) {
                    Toast.makeText(mActivity, "最多只能上传3张考勤照片！", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(mActivity, SelectPicPopupWindow.class);
                intent.putExtra("localpic", localpic);
                mActivity.startActivityForResult(intent, SelectPicPopupWindow.GET_IMG);
            }

            /*拜访签到*/
            else if (mListData.size() <= 9) {
                LogUtil.dee("mListData:"+mListData.size());
                Intent intent = new Intent(mActivity, SelectPicPopupWindow.class);
                intent.putExtra("localpic", localpic);
                intent.putExtra("imgsize",(9-mListData.size()));
                intent.putExtra("addpg",true);
                mActivity.startActivityForResult(intent, SelectPicPopupWindow.GET_IMG);
            }
        }
    }


    public static void setAdapter(final GridView gv, final SignInGridViewAdapter adapter) {
        gv.setAdapter(adapter);
    }

    /**
     * 替换缩略图的url
     *
     * @param url
     * @return
     */
    public String setImgUrl(final String url) {
        //http://loyocloud-01.oss-cn-qingdao.aliyuncs.com/86bdfcb2-9a4e-4629-9f01-9d7f849ec6ae.png
//loyocloud-01.img-cn-qingdao.aliyuncs.com
        //@1e_1c_0o_0l_100h_100w_90q.src
//        String newUrl = url.replaceAll("loyocloud-01.oss-cn-qingdao.aliyuncs.com", "loyocloud-01.img-cn-qingdao.aliyuncs.com");
        String newUrl = url + "@1e_1c_0o_0l_400h_400w_80q.src";
        LogUtil.d("小图片的url：" + newUrl);
        return newUrl;

    }
}
