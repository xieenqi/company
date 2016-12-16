package com.loyo.oa.v2.activityui.discuss.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.discuss.DiscussDetialActivity;
import com.loyo.oa.v2.activityui.discuss.HttpDiscussItem;
import com.loyo.oa.v2.activityui.discuss.MyDiscussActivity;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.tool.DateTool;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 【我的讨论】列表适配器
 * Created by xeq on 16/10/13.
 */

public class DiscussAdapter extends BaseAdapter {
    private List<HttpDiscussItem> datas = new ArrayList<>();
    private LayoutInflater inflater;
    private Context context;

    public DiscussAdapter(Context context) {
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
    public int getCount() {
        return null == datas ? 0 : datas.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DiscussViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_mydiscuss_layout, null);
            holder = new DiscussViewHolder(convertView);
        } else {
            holder = (DiscussViewHolder) convertView.getTag();
        }
        HttpDiscussItem info = datas.get(position);
        holder.tv_title.setText(info.title);
        holder.tv_time.setText(info.newUpdatedAt != 0 ? com.loyo.oa.common.utils.DateTool.getDateTimeFriendly(info.newUpdatedAt) : info.updatedAt.substring(11, 19));
        holder.tv_content.setText(info.creator.name + ":" + info.content);
        holder.openItem(datas.get(position));
        return convertView;
    }


    private class DiscussViewHolder {
        private ImageView iv_icon;
        private ImageView v_msgPoint;
        private TextView tv_title;
        private TextView tv_time;
        private TextView tv_content;
        private TextView tv_dateTime;
        View itemView;

        public DiscussViewHolder(final View itemView) {
            iv_icon = (ImageView) itemView.findViewById(R.id.iv_icon);
            v_msgPoint = (ImageView) itemView.findViewById(R.id.v_msgPoint);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_content = (TextView) itemView.findViewById(R.id.tv_content);
            tv_dateTime = (TextView) itemView.findViewById(R.id.tv_dateTime);
            itemView.setTag(this);
            this.itemView = itemView;
        }

        public void openItem(final HttpDiscussItem itemData) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    Intent intent = new Intent((MyDiscussActivity) context, DiscussDetialActivity.class);
                    intent.putExtra(ExtraAndResult.EXTRA_TYPE, itemData.bizType);
                    intent.putExtra(ExtraAndResult.EXTRA_UUID, itemData.attachmentUUId);
                    intent.putExtra(ExtraAndResult.EXTRA_TYPE_ID, itemData.bizId);
                    intent.putExtra(ExtraAndResult.EXTRA_ID, itemData.summaryId);
                    ((MyDiscussActivity) context).startActivityForResult(intent, ExtraAndResult.REQUEST_CODE);
                    ((MyDiscussActivity) context).overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
                }
            });
            switch (itemData.bizType) {
                case 1:
                    iv_icon.setImageResource(R.drawable.ic_disuss_report);
                    tv_dateTime.setVisibility(View.VISIBLE);
                    //TODO 这里没有严格替换
//                    tv_dateTime.setText(MainApp.getMainApp().df11.format(new Date(System.currentTimeMillis())));
                    tv_dateTime.setText(com.loyo.oa.common.utils.DateTool.getDateTimeFriendly(System.currentTimeMillis()/1000));
                    break;
                case 2:
                    iv_icon.setImageResource(R.drawable.ic_discuss_task);
                    tv_dateTime.setVisibility(View.GONE);
                    break;
                case 5:
                    iv_icon.setImageResource(R.drawable.ic_discuss_project);
                    tv_dateTime.setVisibility(View.GONE);
                    break;
                default:

                    break;

            }
            v_msgPoint.setVisibility(itemData.viewed ? View.INVISIBLE : View.VISIBLE);
        }
    }
}
