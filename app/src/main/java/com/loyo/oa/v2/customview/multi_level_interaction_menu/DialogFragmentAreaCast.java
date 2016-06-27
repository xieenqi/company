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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.beans.City;
import com.loyo.oa.v2.beans.County;
import com.loyo.oa.v2.beans.CustomerRegional;
import com.loyo.oa.v2.beans.Province;
import com.loyo.oa.v2.tool.OnMenuSelectCallback;

import java.util.ArrayList;

/**
 *省市区选择
 */
public class DialogFragmentAreaCast extends DialogFragment {
    private ExpandableListView mExpandableListViewOne;
    private ListView mListViewTwo;
    private MultiMenuExpandableListAdapter multiMenuExpandableListAdapter;
    private OnMenuSelectCallback callback;
    private CustomerRegional regional = new CustomerRegional();
    private ArrayList<Province> provinces;


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
        mExpandableListViewOne = (ExpandableListView) view.findViewById(R.id.expandale_list_one);
        mExpandableListViewOne.setIndicatorBounds(10, 20);
        mExpandableListViewOne.setOnChildClickListener(new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                int[] selectPosition = multiMenuExpandableListAdapter.getSelectPostion();
                if (selectPosition[0] != groupPosition || selectPosition[1] != childPosition) {
                    regional.province=multiMenuExpandableListAdapter.getGroup(groupPosition).getName();
                    regional.city=multiMenuExpandableListAdapter.getChild(groupPosition, childPosition).getName();
                    mAreaThreeMenuAdapter.notifyDataSetChanged(multiMenuExpandableListAdapter.getChild(groupPosition, childPosition));
                    multiMenuExpandableListAdapter.setSelectPostion(groupPosition, childPosition);
                }
                return false;
            }
        });
        multiMenuExpandableListAdapter = new MultiMenuExpandableListAdapter(getActivity(),provinces);
        mExpandableListViewOne.setAdapter(multiMenuExpandableListAdapter);
        mListViewTwo = (ListView) view.findViewById(R.id.list_two);
        mListViewTwo.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (null != callback) {
                    regional.county=mAreaThreeMenuAdapter.getItem(position).getName();
                    callback.onMenuSelected(regional);
                }
                dismiss();
            }
        });
        mAreaThreeMenuAdapter = new AreaThreeMenuAdapter();
        mListViewTwo.setAdapter(mAreaThreeMenuAdapter);
    }

    private AreaThreeMenuAdapter mAreaThreeMenuAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_area_cast, null, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        Display mDisplay = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        window.setLayout((mDisplay.getWidth() * 4) / 5, (mDisplay.getHeight() * 4) / 5);
        window.setGravity(Gravity.CENTER);
    }

    public void show(ArrayList<Province> provinces,FragmentManager manager, String tag, OnMenuSelectCallback callback) {
        this.callback = callback;
        this.provinces = provinces;
        super.show(manager, tag);
    }

    class AreaThreeMenuAdapter extends BaseAdapter {
        private ArrayList<County> mCounties = new ArrayList<>();

        @Override
        public int getCount() {
            return mCounties.size();
        }

        class Holder {
            TextView textView;
        }

        @Override
        public County getItem(int position) {
            return mCounties.get(position);
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
            holder.textView.setText(mCounties.get(position).getName());
            return convertView;
        }

        public void notifyDataSetChanged(City city) {
            mCounties = city.getCounties();
            super.notifyDataSetChanged();
        }
    }
}
