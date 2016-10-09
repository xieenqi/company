package com.loyo.oa.v2.activityui.work.adapter;


/**
 * 【通讯录本部门】适配器
 * Created by yyy on 15/12/30.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.other.model.User;
import com.loyo.oa.v2.tool.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class ContactsInMyDeptAdapter extends BaseAdapter implements SectionIndexer {

    private List<User> list = null;
    private Context mContext;
    private StringBuffer deptName;
    private int defaultAvatar;

    public ContactsInMyDeptAdapter(final Context mContext, final List<User> list) {
        this.mContext = mContext;
        this.list = list;
    }

    public int getCount() {
        return this.list.size();
    }

    public Object getItem(final int position) {
        return list.get(position);
    }

    public long getItemId(final int position) {
        return position;
    }

    public View getView(final int position, View view, final ViewGroup arg2) {

        ViewHolder viewHolder = null;
        final User mContent = list.get(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_contact_personnel, null);

            viewHolder.tvLetter = (TextView) view.findViewById(R.id.catalog);
            viewHolder.name = (TextView) view.findViewById(R.id.tv_name);
            viewHolder.deptInf = (TextView) view.findViewById(R.id.tv_position);
            viewHolder.img = (ImageView) view.findViewById(R.id.img);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        int section = getSectionForPosition(position);
        if (position == getPositionForSection(section)) {
            viewHolder.tvLetter.setVisibility(View.VISIBLE);
            viewHolder.tvLetter.setText(mContent.getSortLetters());
        } else {
            viewHolder.tvLetter.setVisibility(View.GONE);
        }

        User user = list.get(position);
        deptName = new StringBuffer();
        Utils.getDeptName(deptName, user.getDepts());
        viewHolder.name.setText(user.getRealname());
        viewHolder.deptInf.setText(deptName.toString());

        if(null == user.getAvatar() || user.getAvatar().isEmpty() || !user.getAvatar().contains("http")){
            if (user.gender == 2) {
                defaultAvatar = R.drawable.icon_contact_avatar;
            } else {
                defaultAvatar = R.drawable.img_default_user;
            }
            viewHolder.img.setImageResource(defaultAvatar);
        }else{
            ImageLoader.getInstance().displayImage(user.getAvatar(), viewHolder.img);
        }
        return view;
    }

    static final class ViewHolder {

        TextView deptInf;
        TextView tvLetter;
        TextView name;
        ImageView img;

    }

    public int getSectionForPosition(final int position) {
        return list.get(position).getSortLetters().charAt(0);
    }

    public int getPositionForSection(final int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }

        return -1;
    }

    private String getAlpha(final String str) {
        String sortStr = str.trim().substring(0, 1).toUpperCase();
        if (sortStr.matches("[A-Z]")) {
            return sortStr;
        } else {
            return "#";
        }
    }

    @Override
    public Object[] getSections() {
        return null;
    }
}
