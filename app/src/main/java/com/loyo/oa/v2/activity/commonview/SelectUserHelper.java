package com.loyo.oa.v2.activity.commonview;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.beans.Department;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.beans.UserInfo;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.customview.RoundImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by loyocloud on 16/3/29.
 */
public class SelectUserHelper {
//
//    public interface SelectDepartmentCallback extends Serializable {
//        /**
//         * 回调部门被选中
//         *
//         * @param department  被选中的部门
//         * @param oldSelected 部门的上一个状态
//         * @param isUserSelet 是否是单个用户选择触发
//         */
//        void onSelectDepartment(Department department, boolean oldSelected, boolean isUserSelet);
//    }
//
//    public interface SelectUserCallback extends Serializable {
//        /**
//         * 用户选择回调
//         *
//         * @param user //
//         * @param notify
//         */
//        void onSelectUser(User user, boolean notify);
////    }

    public interface SelectUserBase {

        public static final String NULL_AVATAR = "NULL_AVATAR";

        int getUserCount(); // 获取用户部门人数

        boolean isDepart(); // 是否是部门

        String getId(); // 获取部门或用户id

        String getDepartId(); //获取部门id

        boolean equalsId(String id); //判断id是否相等

        String getName();

        String getAvater(); // 获取头像信息, 部门暂返回NULL_AVATAR
    }

    /**
     * 选择的人员的显示
     */
    public static class SelectDataAdapter extends BaseAdapter {

        private final Context mContext;
        private List<SelectUserBase> mSelectDatas = new ArrayList();

        public SelectDataAdapter(Context context) {
            this.mContext = context;
        }

        public void updataList(List<SelectUserBase> datas) {
            if (datas == null)
                datas = new ArrayList<>();
            this.mSelectDatas = datas;
            this.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mSelectDatas.size();
        }

        @Override
        public SelectUserBase getItem(final int position) {
            return mSelectDatas.get(position);
        }

        @Override
        public long getItemId(final int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            Holder holder = null;
            if (convertView == null) {
                holder = new Holder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_select_user, null);
                holder.head = (RoundImageView) convertView.findViewById(R.id.riv_head);
                holder.name = (TextView) convertView.findViewById(R.id.tv_name);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            SelectUserBase base = getItem(position);
            if (base.isDepart()) {
                holder.name.setVisibility(View.VISIBLE);
                holder.head.setVisibility(View.INVISIBLE);
                String name = base.getName();
                if (TextUtils.isEmpty(name)) {
                    holder.name.setText("暂无");
                } else {
                    if (name.length() == 1 && name.length() == 2) {
                        holder.name.setText(name);
                    } else {
                        String str = name.substring(0, 2);
                        holder.name.setText(str);
                    }
                }
            } else {
                holder.name.setVisibility(View.INVISIBLE);
                holder.head.setVisibility(View.VISIBLE);
                ImageLoader.getInstance().displayImage(getItem(position).getAvater(), holder.head);
            }
            //holder.name.setText(selectUserList.get(position).name);
            LogUtil.d("刷新的数九：" + mSelectDatas.size());
            return convertView;
        }

        class Holder {
            RoundImageView head;
            TextView name;
        }
    }
}
