package com.loyo.oa.v2.ui.activity.other.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.tool.SelectPicPopupWindow;
import com.loyo.oa.v2.tool.UploadImgUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.util.ArrayList;


/**
 * 【附件GridView】通用
 * */

public class ImageGridViewAdapter extends BaseAdapter {

    private Activity mActivity;
    private LayoutInflater layoutInflater;
    private ArrayList<SelectPicPopupWindow.ImageInfo> pickPhots;

    private int fromPage;
    private boolean mIsAdd;
    private boolean localpic = false; //是否可以选择相册

    public ImageGridViewAdapter(final Activity mActivity,final boolean mIsAdd,final boolean localpic,final int fromPage, ArrayList<SelectPicPopupWindow.ImageInfo> pickPhots) {
        if(null == pickPhots){
           pickPhots = new ArrayList<>();
       }
        this.fromPage  = fromPage;
        this.mActivity = mActivity;
        this.mIsAdd    = mIsAdd;
        this.pickPhots = pickPhots;
        this.localpic  = localpic;
        layoutInflater = LayoutInflater.from(mActivity);
    }

    @Override
    public int getCount() {
        int size = (pickPhots == null) ? 0 : pickPhots.size();
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

            if (position == pickPhots.size()) {
                if(pickPhots.size() == 9){
                    imageView.setVisibility(View.GONE);
                }else{
                    imageView.setImageResource(R.drawable.icon_add_file);
                    imageView.setBackgroundResource(R.drawable.icon_add_file);
                }
                imageView.setOnClickListener(new OnClickListener_addImg());//添加图片
                imageView.setScaleType(ImageView.ScaleType.CENTER);
            } else {
                ImageLoader.getInstance().displayImage(pickPhots.get(position).path, imageView, new UploadImgUtil.ImageLoadingListener_ClickShowImg(imageView, position,
                        pickPhots, R.drawable.default_image, mIsAdd));
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
                if (pickPhots.size() == 3) {
                    Toast.makeText(mActivity, "最多只能上传3张考勤照片！", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(mActivity, SelectPicPopupWindow.class);
                intent.putExtra("localpic", localpic);
                mActivity.startActivityForResult(intent, SelectPicPopupWindow.GET_IMG);
            }

            /*其他业务*/
            else if (pickPhots.size() <= 9) {
                Intent intent = new Intent(mActivity, SelectPicPopupWindow.class);
                intent.putExtra("localpic", localpic);
                intent.putExtra("imgsize",(9-pickPhots.size()));
                intent.putExtra("addpg",true);
                mActivity.startActivityForResult(intent, SelectPicPopupWindow.GET_IMG);
            }
        }
    }

    public static void setAdapter(final GridView gv, final ImageGridViewAdapter adapter) {
        gv.setAdapter(adapter);
    }
}
