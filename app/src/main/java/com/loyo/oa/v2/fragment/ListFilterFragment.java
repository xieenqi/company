package com.loyo.oa.v2.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.beans.FilterItem;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.BaseFragmentActivity;
import com.loyo.oa.v2.tool.ViewHolder;

import java.util.ArrayList;

/**
 * com.loyo.oa.v2.fragment
 * 描述 :
 * 作者 : ykb
 * 时间 : 15/9/7.
 */
public class ListFilterFragment extends BaseFragment {

    private ListView lv;
    private OnItemSelectedCallback callback;
    private ArrayList<FilterItem> datas = new ArrayList<>();
    private String parent;
    private MAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != getArguments()) {
            if (getArguments().containsKey("data")) {
                datas = (ArrayList<FilterItem>) getArguments().getSerializable("data");
            }
            if (getArguments().containsKey("type")) {
                parent = getArguments().getString("parent");
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_filter, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lv = (ListView) view.findViewById(R.id.lv_filter);
        if (null == adapter) {
            adapter = new MAdapter();
            lv.setAdapter(adapter);
        }
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                FilterItem o = adapter.getItem(i);
                if (o.getData() instanceof String) {
                    if (null != callback) {
                        callback.OnItemSelected(parent.concat(",").concat((String) adapter.getItem(i).getData()));
                    }
                } else {
                    view.findViewById(R.id.layout_child_filter_container).setVisibility(View.VISIBLE);
                    Bundle b = new Bundle();
                    b.putSerializable("data", o.getData());
                    b.putString("parent", parent);
                    ((BaseFragmentActivity) mActivity).getSupportFragmentManager().beginTransaction().
                            add(R.id.layout_child_filter_container, Fragment.instantiate(mActivity, getClass().getName(), b)).commit();
                }
            }
        });
    }

    private class MAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public FilterItem getItem(int i) {
            return datas.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            FilterItem o = getItem(i);
            if (null == view) {
                view = mActivity.getLayoutInflater().inflate(R.layout.item_filter_list_item, viewGroup, false);
            }
            TextView tv_content = ViewHolder.get(view, R.id.tv_content);
            String content = "";
            if (o.getData() instanceof String) {
                content = (String) o.getData();

            } else {
                content = o.getKey();
            }
            tv_content.setText(content);

            return view;
        }
    }

    /**
     * 设置结果回调接口
     *
     * @param callback
     */
    public void setCallback(OnItemSelectedCallback callback) {
        this.callback = callback;
    }

    public interface OnItemSelectedCallback {
        void OnItemSelected(String content);
    }
}
