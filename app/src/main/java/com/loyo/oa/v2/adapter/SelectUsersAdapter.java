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
import com.loyo.oa.v2.beans.Department;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.tool.LogUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by loyocloud on 16/3/30.
 */
public class SelectUsersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private List<User> users = new ArrayList<>();
    private boolean isAlone = false;
    private Department mDepartment;
    private OnUserSelectCallback mUserSlectCallback;
    private OnDepartmentAllSelectCallback mDepartmentAllSelectCallback;

    public SelectUsersAdapter(Context context) {
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

    /**
     * 设置当前选中的部门信息
     *
     * @param mDepartment
     */
    public void setCurrentDepartment(Department mDepartment) {
        this.mDepartment = mDepartment;
    }

    /**
     * 设置是不是单选, 是单选隐藏全选的headerView，反之显示
     *
     * @param isAlone
     */
    public void setAlone(boolean isAlone) {
        this.isAlone = isAlone;
        notifyDataSetChanged();
    }

    public boolean isAlone() {
        return isAlone;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        View view = null;
        if (viewType == -1) {
            view = View.inflate(mContext, R.layout.item_header_selectdetuser1, null);
            holder = new HeaderViewHolder(view);
        } else {
            view = View.inflate(mContext, R.layout.item_selectcustomer_right_lv1, null);
            holder = new ViewHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case -1:
                HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
                headerViewHolder.checkBox.setChecked(mDepartment.isIndex());
                headerViewHolder.relAllcheck.setTag(headerViewHolder.checkBox);
                headerViewHolder.relAllcheck.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v.getTag();
                        boolean isSelectAll = cb.isChecked();
                        cb.setChecked(!isSelectAll);
//                        for (int i = 0; i < users.size(); i++) {
//                            User user = users.get(i);
//                            user.setIndex(!isSelectAll);
//                        }
                        if (mDepartmentAllSelectCallback != null) {
                            if (mDepartmentAllSelectCallback.onSelect(!isSelectAll)) {
                                notifyDataSetChanged();
                            }
                        }
                    }
                });
                break;
            default:
                ViewHolder userHolder = (ViewHolder) holder;
                final User user = users.get(position - (isAlone() ? 0 : 1));
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
                userHolder.convertView.setTag(userHolder.checkBox);
                userHolder.convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckBox checkBox = (CheckBox) v.getTag();
                        boolean isSelect = checkBox.isChecked();
                        checkBox.setChecked(!isSelect);
                        user.setIndex(!isSelect);
                        if (!isAlone()) {
                            notifyItemChanged(0);
                        }
                        if (mUserSlectCallback != null) {
                            mUserSlectCallback.onUserSelect(mDepartment, user);
                        }
                    }
                });
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
        if (users.isEmpty())
            return false;
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
        return users.isEmpty() ? 0 : !isAlone() ? users.size() + 1 : users.size();
    }

    @Override
    public int getItemViewType(int position) {
        return !isAlone() && position == 0 ? -1 : position;
    }

    /**
     * 设置用户单选的回调
     *
     * @param callback
     */
    public void setOnUserSelectCallback(OnUserSelectCallback callback) {
        this.mUserSlectCallback = callback;
    }

    /**
     * 设置部门全选和取消的回调
     *
     * @param callback
     */
    public void setOnDepartmentAllSelectCallback(OnDepartmentAllSelectCallback callback) {
        this.mDepartmentAllSelectCallback = callback;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView userName;
        private final TextView dept;
        private final TextView worker;
        private final ImageView heading;
        private final CheckBox checkBox;
        private final View convertView;

        public ViewHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.item_selectdu_right_name);
            dept = (TextView) itemView.findViewById(R.id.item_selectdu_right_dept);
            worker = (TextView) itemView.findViewById(R.id.item_selectdu_right_worker);
            heading = (ImageView) itemView.findViewById(R.id.img_title_left);
            checkBox = (CheckBox) itemView.findViewById(R.id.item_selectdu_checkbox);
            convertView = itemView;
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

    public interface OnDepartmentAllSelectCallback {
        boolean onSelect(boolean isSelect);
    }

    public interface OnUserSelectCallback {
        void onUserSelect(Department department, User user);
    }
}