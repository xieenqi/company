package com.loyo.oa.contactpicker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.loyo.oa.contactpicker.callback.OnPickUserEvent;
import com.loyo.oa.contactpicker.model.PickedModel;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.customview.RoundImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by EthanGong on 2016/10/17.
 * Copy from SelectUserHelper.SelectDataAdapter
 */


public class PickedContactsAdapter extends BaseAdapter {

    private final Context mContext;
    private List<PickedModel> data = new ArrayList<PickedModel>();
    private OnPickUserEvent callback;

    public PickedContactsAdapter(Context context) {
        this.mContext = context;
    }

    public void loadData(List<PickedModel> data) {
        this.data.clear();
        this.data.addAll(data);
        this.notifyDataSetChanged();
    }

    public void setCallback(OnPickUserEvent callback) {
        this.callback = callback;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public PickedModel getItem(final int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(final int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        PickedContactsAdapter.Holder holder;
        if (convertView == null) {
            holder = new PickedContactsAdapter.Holder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_select_user, null);
            holder.head = (RoundImageView) convertView.findViewById(R.id.riv_head);
            holder.name = (TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(holder);
        } else {
            holder = (PickedContactsAdapter.Holder) convertView.getTag();
        }

        PickedModel model = getItem(position);
        if (model.isDepartment) {
            holder.name.setVisibility(View.VISIBLE);
            holder.head.setVisibility(View.INVISIBLE);
            holder.name.setText(model.getDisplayName());
        } else {
            holder.name.setVisibility(View.INVISIBLE);
            holder.head.setVisibility(View.VISIBLE);
            if (getItem(position).getDisplayAvatar() != null) {
                ImageLoader.getInstance().displayImage(getItem(position).getDisplayAvatar(), holder.head);
            }
        }
        return convertView;
    }

    class Holder {
        RoundImageView head;
        TextView name;
    }
}