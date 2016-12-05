package com.loyo.oa.v2.activityui.setting.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.CustomerManagerActivity;
import com.loyo.oa.v2.activityui.setting.bean.SystemMessageItem;
import com.loyo.oa.v2.activityui.setting.bean.SystemMessageItemType;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.IMain;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by xeq on 16/11/7.
 */

public class AdapterSystemMessage extends BaseAdapter {

    private LayoutInflater inflater;
    private List<SystemMessageItem> data;
    private Activity context;

    public AdapterSystemMessage(Activity context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public void setData(List<SystemMessageItem> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Holder holder;
        final SystemMessageItem item = data.get(position);
        if (null == convertView) {
            holder = new Holder();
            convertView = inflater.inflate(R.layout.item_system_message, null);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            holder.view_ack = convertView.findViewById(R.id.view_ack);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.setContent(item);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.openItem(item);
            }
        });
        return convertView;
    }

    class Holder {
        TextView tv_title, tv_time;
        ImageView iv_icon;
        View view_ack;

        public void setContent(SystemMessageItem item) {
            tv_title.setText(item.title);
            tv_time.setText(DateTool.getDiffTime(item.createdAt));
            if (item.bizzType != null) {
                iv_icon.setImageResource(item.bizzType.getIcon());
            }
            view_ack.setVisibility(item.viewedAt == 0 ? View.VISIBLE : View.GONE);
        }

        public void openItem(SystemMessageItem item) {
            Intent intent = new Intent();
            if (item.bizzType.getValue() == 24) {
                return;//系统消息不做调转
            }  //此处特殊处理 客户 批量改 参与人、负责人
            else if (item.bizzType.getValue() == SystemMessageItemType.MSG_CUSTOMER.getValue() && item.jumpType != 0) {
                intent.setClass(context, CustomerManagerActivity.class);
                intent.putExtra(ExtraAndResult.EXTRA_TYPE, item.jumpType);
            } else {
                intent.setClass(context, item.bizzType.getItemClass());
                intent.putExtra(item.bizzType.getExtraName(), item.bizzId);
            }
            context.startActivity(intent);
            RestAdapterFactory.getInstance().build(Config_project.API_URL_STATISTICS()).create(IMain.class).
                    readSystemMessageOne(item.id, new Callback<Object>() {
                        @Override
                        public void success(Object o, Response response) {
                            HttpErrorCheck.checkResponse("读取一条系统消息", response);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                        }
                    });
        }
    }
}
