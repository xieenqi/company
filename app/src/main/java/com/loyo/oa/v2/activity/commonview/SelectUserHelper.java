package com.loyo.oa.v2.activity.commonview;

import android.content.Context;
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

    public interface SelectUserBase {

        public static final String NULL_AVATAR = "NULL_AVATAR";

        int getUserCount(); // 获取用户部门人数

        boolean isDepart(); // 是否是部门

        String getId(); // 获取部门或用户id

        String getDepartId(); //获取部门id

        boolean equalsId(String id); //判断id是否相等

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
            return null;
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
            ImageLoader.getInstance().displayImage(getItem(position).getAvater(), holder.head);
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
