package com.loyo.oa.v2.customview.multi_level_interaction_menu;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.activityui.customer.bean.Industry;
import com.loyo.oa.v2.tool.OnMenuSelectCallback;

import java.util.ArrayList;

/**
 * 行业选择
 */
public class DialogFragmentIndustryCast extends DialogFragment {
    private ListView lv_industry;
    private IndustryMenuAdapter adapter;
    private OnMenuSelectCallback callback;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(0, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadView();
    }

    private void loadView() {
        View view = getView();
        lv_industry = (ListView) view.findViewById(R.id.lv_industry);
        adapter = new IndustryMenuAdapter();
        lv_industry.setAdapter(adapter);
        lv_industry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (null != callback) {
                    callback.onMenuSelected(adapter.getItem(i));
                }
                dismiss();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_industry_cast, null, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        Display mDisplay = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        window.setLayout((mDisplay.getWidth() * 4) / 5, (mDisplay.getHeight() * 4) / 5);
        window.setGravity(Gravity.CENTER);
    }

    public void show(FragmentManager manager, String tag, OnMenuSelectCallback menuSelectCallback) {
        callback = menuSelectCallback;
        super.show(manager, tag);
    }

    class IndustryMenuAdapter extends BaseAdapter {
        private ArrayList<Industry> mIndustries = MainApp.getMainApp().mIndustries;

        @Override
        public int getCount() {
            return mIndustries.size();
        }

        class Holder {
            TextView textView;
        }

        @Override
        public Industry getItem(int position) {
            return mIndustries.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder = null;
            if (null == convertView) {
                holder = new Holder();
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_one_stair_menu_item, null, false);
                holder.textView = (TextView) convertView.findViewById(R.id.text);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            if (((ListView) parent).isItemChecked(position)) {
                convertView.setBackgroundColor(Color.argb(100, 127, 204, 232));
            } else {
                convertView.setBackgroundColor(Color.argb(0, 0, 0, 0));
            }
            holder.textView.setText(mIndustries.get(position).getName());
            return convertView;
        }
    }
}
