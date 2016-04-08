package com.loyo.oa.v2.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.beans.Department;
import com.loyo.oa.v2.beans.SelectDepData;
import com.loyo.oa.v2.beans.SelectUserData;
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
    private List<SelectUserData> users = new ArrayList<>();
    private boolean isAlone = false;
    private OnUserSelectCallback mUserSlectCallback;
    private SelectDepData mDepartment;
    private OnDepartmentAllSelectCallback mDepartmentCallback;

    public SelectUsersAdapter(Context context) {
        this.mContext = context;
    }

    public void updataList(List<SelectUserData> users) {
        if (users == null) {
            users = new ArrayList<>();
        }
        this.users = users;
        notifyDataSetChanged();
    }

    public SelectDepData getDepartment() {
        return mDepartment;
    }

    public void setDepartment(SelectDepData mDepartment) {
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
                if (mDepartment != null) {
                    headerViewHolder.checkBox.setChecked(mDepartment.isSelect());
                }
                headerViewHolder.relAllcheck.setTag(headerViewHolder.checkBox);
                headerViewHolder.tv_noUser.setVisibility(users.size() == 0 ? View.VISIBLE : View.GONE);
                headerViewHolder.relAllcheck.setEnabled(!(users.size() == 0));
                headerViewHolder.relAllcheck.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v.getTag();
                        boolean isSelectAll = cb.isChecked();
                        cb.setChecked(!isSelectAll);

                        mDepartment.setAllSelect(!isSelectAll);

                        if (mDepartmentCallback != null) {
                            if (mDepartmentCallback.onSelect(!isSelectAll)) {
                                notifyDataSetChanged();
                            }
                        }
                    }
                });
                break;
            default:
                ViewHolder userHolder = (ViewHolder) holder;
                final SelectUserData user = users.get(position - (isAlone() ? 0 : 1));
                String deptName = "无";
                String npcName = "无";
                /*部门名称*/
                deptName = user.getDeptName();
                /*用户职称*/
                npcName = user.getNpcName();
                userHolder.userName.setText(user.getName());
                userHolder.dept.setText(TextUtils.isEmpty(deptName) ? "无" : deptName);
                userHolder.worker.setText(TextUtils.isEmpty(npcName) ? "无" : npcName);
                /*选中赋值*/
                userHolder.checkBox.setChecked(user.isSelect());
                userHolder.convertView.setTag(userHolder.checkBox);
                userHolder.convertView.setEnabled(!mDepartment.isSelect());
                userHolder.convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isAlone()) {
                            CheckBox checkBox = (CheckBox) v.getTag();
                            boolean isSelect = checkBox.isChecked();
                            checkBox.setChecked(!isSelect);

                            //TODO : ....
                            user.setCallbackSelect(!isSelect);

                            if (!isAlone()) {
                                notifyItemChanged(0);
                            }
                        }
                        if (mUserSlectCallback != null) {
                            mUserSlectCallback.onUserSelect();
                        }
                    }
                });
                ImageLoader.getInstance().displayImage(user.getAvatar(), userHolder.heading);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return !isAlone() ? users.size() + 1 : users.size();
    }

    @Override
    public int getItemViewType(int position) {
        return !isAlone() && position == 0 ? -1 : position;
    }

    public void setOnUserSelectCallback(OnUserSelectCallback callback) {
        this.mUserSlectCallback = callback;
    }

    public void setOnDepartmentAllSelectCallback(OnDepartmentAllSelectCallback onDepartmentAllSelectCallback) {
        this.mDepartmentCallback = onDepartmentAllSelectCallback;
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
        private final TextView tv_noUser;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            relAllcheck = (RelativeLayout) itemView.findViewById(R.id.selectdetuser_allcheck);
            checkBox = (CheckBox) itemView.findViewById(R.id.selectdetuser_checkbox);
            tv_noUser = (TextView) itemView.findViewById(R.id.tv_noUser);
        }
    }

    public interface OnUserSelectCallback {
        void onUserSelect();
    }

    public interface OnDepartmentAllSelectCallback {
        boolean onSelect(boolean isSelect);
    }
}
