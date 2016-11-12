package com.loyo.oa.v2.activityui.signinnew.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.signinnew.model.SigninNewListModel;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customview.RoundImageView;
import com.loyo.oa.v2.tool.DateTool;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by yyy on 16/11/12.
 */

public class SelfSigninNewListAdapter extends BaseAdapter{

    private Context mContext;
    private ArrayList<SigninNewListModel> listModel;

    public SelfSigninNewListAdapter(Context mContext,ArrayList<SigninNewListModel> listModel){
        this.mContext = mContext;
        this.listModel = listModel;
    }

    @Override
    public int getCount() {
        return listModel.size();
    }

    @Override
    public Object getItem(int position) {
        return listModel.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        SigninNewListModel signinNewListModel = listModel.get(position);
        if(null == convertView){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_sgninnew_selflist,null);
            holder.iv_heading = (RoundImageView)convertView.findViewById(R.id.iv_heading);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_address = (TextView) convertView.findViewById(R.id.tv_address);
            holder.tv_contact = (TextView) convertView.findViewById(R.id.tv_contact);
            holder.tv_position = (TextView) convertView.findViewById(R.id.tv_position);
            holder.tv_offset = (TextView) convertView.findViewById(R.id.tv_offset);
            holder.tv_create_time = (TextView) convertView.findViewById(R.id.tv_create_time);
            holder.tv_toast = (TextView) convertView.findViewById(R.id.tv_toast);
            holder.tv_memo = (TextView) convertView.findViewById(R.id.tv_memo);
            holder.tv_customer = (TextView) convertView.findViewById(R.id.tv_customer);
            holder.iv_comment = (ImageView) convertView.findViewById(R.id.iv_comment);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.iv_comment.setOnTouchListener(Global.GetTouch());

        ImageLoader.getInstance().displayImage(signinNewListModel.creator.avatar,holder.iv_heading);
        holder.tv_name.setText(signinNewListModel.creator.name);
        holder.tv_address.setText(signinNewListModel.address);
        holder.tv_contact.setText(signinNewListModel.contactName);
        holder.tv_position.setText(signinNewListModel.position);
        holder.tv_offset.setText(signinNewListModel.offsetDistance+"");
        holder.tv_toast.setText("@"+signinNewListModel.atNameAndDepts);
        holder.tv_memo.setText(signinNewListModel.memo);
        holder.tv_customer.setText(signinNewListModel.customerName);
        holder.tv_create_time.setText(DateTool.timet(signinNewListModel.createdAt+"","yyyy-MM-dd hh:mm"));

        holder.iv_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return convertView;
    }

    class ViewHolder {
        RoundImageView iv_heading;
        TextView tv_name;        /*创建人*/
        TextView tv_address;     /*地址*/
        TextView tv_customer;    /*客户名字*/
        TextView tv_contact;     /*联系人*/
        TextView tv_position;    /*客户定位*/
        TextView tv_offset;      /*偏差*/
        TextView tv_create_time; /*创建时间*/
        TextView tv_toast;       /*通知人员*/
        TextView tv_memo;        /*内容*/

        ImageView iv_comment;    /*评论*/
    }
}
