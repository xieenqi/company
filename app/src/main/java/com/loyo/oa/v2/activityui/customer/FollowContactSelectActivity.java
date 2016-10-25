package com.loyo.oa.v2.activityui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.model.Contact;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.BaseActivity;

import java.util.ArrayList;

/**
 * 写跟进 【选择客户联系人】
 * Created by xeq on 16/6/12.
 */
public class FollowContactSelectActivity extends BaseActivity {
    private TextView tv_title;
    private LinearLayout ll_back;
    private ListView lv_list;

    private ArrayList<Contact> listData = new ArrayList<>();
    private AdapterCustomerContact adapter;
    private String dataName = " ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_stage);
        init();
        getIntentData();
    }

    private void init() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("选择客户联系人");
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnTouchListener(Global.GetTouch());
        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        lv_list = (ListView) findViewById(R.id.lv_list);
        adapter = new AdapterCustomerContact();
        lv_list.setAdapter(adapter);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        listData = (ArrayList<Contact>) intent.getSerializableExtra(ExtraAndResult.EXTRA_DATA);
        dataName = intent.getStringExtra(ExtraAndResult.EXTRA_NAME);
        adapter.setData(listData);
    }

    class AdapterCustomerContact extends BaseAdapter {
        private ArrayList<Contact> data = new ArrayList<>();

        public void setData(ArrayList<Contact> newData) {
            if (null != newData) {
                this.data = newData;
                notifyDataSetChanged();
            }
        }

        @Override
        public int getCount() {
            return null == data ? 0 : data.size();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = LayoutInflater.from(FollowContactSelectActivity.this).inflate(R.layout.item_sale_stage, null);
            }
            TextView tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            ImageView iv_img = (ImageView) convertView.findViewById(R.id.iv_img);

            tv_name.setText(data.get(position).getName());
            if (dataName.equals(data.get(position).getName())) {
                iv_img.setVisibility(View.VISIBLE);
            } else {
                iv_img.setVisibility(View.INVISIBLE);
            }
            convertView.setOnTouchListener(Global.GetTouch());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra(ExtraAndResult.EXTRA_DATA, data.get(position));
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
            return convertView;
        }
    }
}
