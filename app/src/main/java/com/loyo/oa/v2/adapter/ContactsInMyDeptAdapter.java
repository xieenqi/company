package com.loyo.oa.v2.adapter;


/**
 * 通讯录本部门适配器
 * Created by yyy on 15/12/30.
 */

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Department;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.beans.UserInfo;
import com.loyo.oa.v2.tool.LogUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class ContactsInMyDeptAdapter extends BaseAdapter implements SectionIndexer {

    private List<User> list = null;
    private Context mContext;
    private StringBuffer deptName;
    private String workName;

    public ContactsInMyDeptAdapter(Context mContext, List<User> list) {
        this.mContext = mContext;
        this.list = list;
    }

    public void updateListView(List<User> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup arg2) {

        ViewHolder viewHolder = null;
        final User mContent = list.get(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_medleft, null);

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

        /*部门名字*/
        try {
            deptName = new StringBuffer();
            for(UserInfo userInfo : this.list.get(position).getDepts()){
                deptName.append(userInfo.getShortDept().getName()+" ");
                LogUtil.dll(this.list.get(position).getRealname() + ":" + userInfo.getShortDept().getName());
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            deptName.append("无");
        }

        /*职位名字*/
        try {
            workName = this.list.get(position).role.name;
        } catch (NullPointerException e) {
            e.printStackTrace();
            workName = "无";
        }

        viewHolder.name.setText(this.list.get(position).getRealname());
        viewHolder.deptInf.setText(deptName.toString() + " " + workName);
        ImageLoader.getInstance().displayImage(this.list.get(position).getAvatar(), viewHolder.img);

        return view;
    }

    final static class ViewHolder {

        TextView deptInf;
        TextView tvLetter;
        TextView name;
        ImageView img;

    }

    public int getSectionForPosition(int position) {
        return list.get(position).getSortLetters().charAt(0);
    }

    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }

        return -1;
    }

    private String getAlpha(String str) {
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
