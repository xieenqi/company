package com.loyo.oa.v2.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.beans.Department;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by loyocloud on 16/3/30.
 */
public class SelectUserDepartmentAdapter extends RecyclerView.Adapter<SelectUserDepartmentAdapter.ViewHolder> {

    private final Context mContext;
    private List<Department> mDepartments = new ArrayList<>();
    private int mSelectIndex = 0;
    private OnSelectIndexChangeCallback mIndexCallback;

    public SelectUserDepartmentAdapter(Context context, List<Department> datas) {
        this.mContext = context;
        updataList(datas);
    }

    private void updataList(List<Department> datas) {
        if (datas == null) {
            datas = new ArrayList<>();
        }
        mDepartments = datas;
        notifyDataSetChanged();
    }

    /**
     * 设置被选中的item
     *
     * @param index
     */
    public void setSelectIndex(int index) {
        if (index < 0 || index > mDepartments.size() - 1)
            return;
        int oldIndex = mSelectIndex;
        mSelectIndex = index;
        notifyItemChanged(oldIndex);
        if (mIndexCallback != null) {
            mIndexCallback.onChange(index);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = View.inflate(mContext, R.layout.item_selectcustomer_left_lv, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.detName.setText(mDepartments.get(position).getName());
        if (mDepartments.get(position).getXpath().split("/").length > 2) {
            holder.detName.setTextColor(Color.parseColor("#c9c9c9"));
        }

        if (position == mSelectIndex) {
            holder.convertView.setBackgroundResource(R.color.beogray);
        } else {
            holder.convertView.setBackgroundResource(R.color.white);
        }
        holder.convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position == mSelectIndex)
                    return;
                v.setBackgroundResource(R.color.beogray);
                setSelectIndex(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDepartments == null ? 0 : mDepartments.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView detName;
        private final View convertView;

        public ViewHolder(View itemView) {
            super(itemView);
            detName = (TextView) itemView.findViewById(R.id.item_selectdu_left_tv);
            convertView = itemView;
        }
    }

    public void setOnSelectIndexChangeCallback(OnSelectIndexChangeCallback callback) {
        this.mIndexCallback = callback;
    }

    public interface OnSelectIndexChangeCallback {
        /**
         * 当选择部门改变时回调设置部门用户...
         *
         * @param index
         */
        void onChange(int index);
    }
}
