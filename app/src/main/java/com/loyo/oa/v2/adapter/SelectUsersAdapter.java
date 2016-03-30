package com.loyo.oa.v2.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.beans.User;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by loyocloud on 16/3/30.
 */
public class SelectUsersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private List<User> users = new ArrayList<>();
    // 用于保存可见的的CheckBox, 优化响应速度
    private final List<CheckBox> mItemCheckBoxes = new ArrayList<>();

    public SelectUsersAdapter(Context context, List<User> users) {
        this.mContext = context;
        updataList(users);
    }

    public void updataList(List<User> users) {
        if (users == null) {
            users = new ArrayList<>();
        }
        this.users = users;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        View view = null;
        switch (viewType) {
            case 0:
                view = View.inflate(mContext, R.layout.item_header_selectdetuser, null);
                holder = new HeaderViewHolder(view);
                break;
            default:
                view = View.inflate(mContext, R.layout.item_selectcustomer_right_lv, null);
                holder = new ViewHolder(view);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (position) {
            case 0:
                HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
                headerViewHolder.checkBox.setChecked(isAllSelect());
                headerViewHolder.relAllcheck.setTag(headerViewHolder.checkBox);
                headerViewHolder.relAllcheck.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v.getTag();
                        boolean isSelectAll = cb.isChecked();
                        cb.setChecked(!isSelectAll);
                        for (int i = 0; i < mItemCheckBoxes.size(); i++) {
                            mItemCheckBoxes.get(i).setChecked(!isSelectAll);
                        }
                        for (int i = 0; i < users.size(); i++) {
                            User user = users.get(i);
                            user.setIndex(!isSelectAll);
                        }
                    }
                });
                break;
            default:
                ViewHolder userHolder = (ViewHolder) holder;
                User user = users.get(position - 1);
                String deptName = "无";
                String npcName = "无";
                /*部门名称*/
                try {
                    deptName = user.depts.get(0).getShortDept().getName();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                /*用户职称*/
                try {
                    npcName = user.getDepts().get(0).getTitle();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                userHolder.userName.setText(user.getRealname());
                userHolder.dept.setText(deptName);
                userHolder.worker.setText(npcName);
                /*选中赋值*/
                userHolder.checkBox.setChecked(user.isIndex());
                ImageLoader.getInstance().displayImage(user.getAvatar(), userHolder.heading);
                break;
        }
    }

    /**
     * 判断当前部门是否被全选
     *
     * @return
     */
    private boolean isAllSelect() {
        for (User user :
                users) {
            if (!user.isIndex()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int getItemCount() {
        return users.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView userName;
        private final TextView dept;
        private final TextView worker;
        private final ImageView heading;
        private final CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.item_selectdu_right_name);
            dept = (TextView) itemView.findViewById(R.id.item_selectdu_right_dept);
            worker = (TextView) itemView.findViewById(R.id.item_selectdu_right_worker);
            heading = (ImageView) itemView.findViewById(R.id.img_title_left);
            checkBox = (CheckBox) itemView.findViewById(R.id.item_selectdu_checkbox);
            mItemCheckBoxes.add(checkBox);
        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {

        private final RelativeLayout relAllcheck;
        private final CheckBox checkBox;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            relAllcheck = (RelativeLayout) itemView.findViewById(R.id.selectdetuser_allcheck);
            checkBox = (CheckBox) itemView.findViewById(R.id.selectdetuser_checkbox);
        }
    }
}
