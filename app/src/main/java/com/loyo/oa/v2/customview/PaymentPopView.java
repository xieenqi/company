package com.loyo.oa.v2.customview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.loyo.oa.v2.R;

import java.util.Arrays;
import java.util.List;

/**
 * 【付款方式】
 * Created by yyy on 16/08/03.
 */
public class PaymentPopView extends Dialog {
    private TextView tv_title;
    private ListView lv_list;
    private List<String> data;
    private Context context;
    private LayoutInflater inflater;
    private VaiueCallback callback;

    public PaymentPopView(Context context, String[] data, String title) {
        super(context);
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = Arrays.asList(data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.paymentpopview);
        tv_title = (TextView) findViewById(R.id.tv_title);
        lv_list = (ListView) findViewById(R.id.lv_list);
        CurrentAdapter adapter = new CurrentAdapter();
        lv_list.setAdapter(adapter);
        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                callback.setValue(data.get(position), position + 1);
                PaymentPopView.this.dismiss();
            }
        });
    }

    public void setCallback(VaiueCallback callback) {
        this.callback = callback;
    }

    public interface VaiueCallback {
        void setValue(String value, int index);
    }

    class CurrentAdapter extends BaseAdapter {

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
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = inflater.inflate(R.layout.item_dialog_payment, null);
            }
            ((TextView) convertView.findViewById(R.id.tv_value)).setText(data.get(position));
            return convertView;
        }
    }
}