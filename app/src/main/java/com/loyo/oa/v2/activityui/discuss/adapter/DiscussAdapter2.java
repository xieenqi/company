package com.loyo.oa.v2.activityui.discuss.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.discuss.DiscussDetialActivity;
import com.loyo.oa.v2.activityui.discuss.HttpDiscussItem;
import com.loyo.oa.v2.activityui.discuss.MyDiscussActivity;
import com.loyo.oa.v2.activityui.discuss.viewholder.DiscussCell;
import com.loyo.oa.v2.common.ExtraAndResult;

import java.util.ArrayList;
import java.util.List;

/**
 * 【我的讨论】列表适配器
 * Created by xeq on 16/10/13.
 */

public class DiscussAdapter2 extends RecyclerView.Adapter<DiscussCell> {
    private List<HttpDiscussItem> datas = new ArrayList<>();
    private LayoutInflater inflater;
    private Context context;

    public DiscussAdapter2(Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public void updataList(List<HttpDiscussItem> data) {
        if (data == null) {
            data = new ArrayList<>();
        }
        datas.clear();
        datas.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public DiscussCell onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mydiscuss_layout, parent, false);
        return new DiscussCell(view);
    }

    @Override
    public void onBindViewHolder(DiscussCell holder, int position) {
        final HttpDiscussItem itemData = datas.get(position);
        holder.openItem(itemData);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Intent intent = new Intent(context, DiscussDetialActivity.class);
                intent.putExtra(ExtraAndResult.EXTRA_TYPE, itemData.bizType);
                intent.putExtra(ExtraAndResult.EXTRA_UUID, itemData.attachmentUUId);
                intent.putExtra(ExtraAndResult.EXTRA_TYPE_ID, itemData.bizId);
                intent.putExtra(ExtraAndResult.EXTRA_ID, itemData.summaryId);
                ((MyDiscussActivity) context).startActivityForResult(intent, ExtraAndResult.REQUEST_CODE);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        final HttpDiscussItem itemData = datas.get(position);
        return (itemData.id).hashCode();
    }

    @Override
    public int getItemCount() {
        return null == datas ? 0 : datas.size();
    }
}
