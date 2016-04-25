package com.loyo.oa.v2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.beans.User;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.util.ArrayList;

/**
 * Created by loyo_dev1 on 16/4/25.
 */
public class ContactsMyDeptOtherAdapter extends BaseAdapter {

    private ArrayList<User> commyUsers;
    private Context mContext;

    public ContactsMyDeptOtherAdapter(ArrayList<User> commyUsers, Context mContext){
        this.commyUsers = commyUsers;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return commyUsers.size();
    }

    @Override
    public Object getItem(int position) {
        return commyUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        ViewHolder viewHolder = null;
        final User mContent = commyUsers.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_mydept_otheruser, null);

            viewHolder.name = (TextView) convertView.findViewById(R.id.contact_dept_othername);
            viewHolder.img = (ImageView) convertView.findViewById(R.id.contact_dept_img);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText(mContent.getRealname());
        ImageLoader.getInstance().displayImage(mContent.getAvatar(), viewHolder.img);

        return convertView;
    }

    class ViewHolder{
        ImageView img;
        TextView name;
    }
}
