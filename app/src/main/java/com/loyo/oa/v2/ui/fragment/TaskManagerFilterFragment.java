package com.loyo.oa.v2.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ScrollView;

import com.loyo.oa.v2.R;

public class TaskManagerFilterFragment extends Fragment implements View.OnClickListener {

    View view;

    Button btn_task_filter_confirm;

    ViewGroup layout_type1, layout_type2, layout_type3, layout_status1, layout_status2, layout_status3;

    CheckBox cb1, cb2, cb3, cb4, cb5, cb6;

    ListView lv_subordinates;

    private int type = 0, status = 0;

    public static String MSG_TASK_FILTER = "com.loyo.oa.v2.task.filter";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_tasks_manager_filter, container, false);

            layout_type1 = (ViewGroup) view.findViewById(R.id.layout_type1);
            layout_type2 = (ViewGroup) view.findViewById(R.id.layout_type2);
            layout_type3 = (ViewGroup) view.findViewById(R.id.layout_type3);
            layout_status1 = (ViewGroup) view.findViewById(R.id.layout_status1);
            layout_status2 = (ViewGroup) view.findViewById(R.id.layout_status2);
            layout_status3 = (ViewGroup) view.findViewById(R.id.layout_status3);

            layout_type1.setOnClickListener(this);
            layout_type2.setOnClickListener(this);
            layout_type3.setOnClickListener(this);
            layout_status1.setOnClickListener(this);
            layout_status2.setOnClickListener(this);
            layout_status3.setOnClickListener(this);

            cb1 = (CheckBox) view.findViewById(R.id.cb1);
            cb2 = (CheckBox) view.findViewById(R.id.cb2);
            cb3 = (CheckBox) view.findViewById(R.id.cb3);
            cb4 = (CheckBox) view.findViewById(R.id.cb4);
            cb5 = (CheckBox) view.findViewById(R.id.cb5);
            cb6 = (CheckBox) view.findViewById(R.id.cb6);

            /*if (Common.getSubUsers().size() > 0) {
                lv_subordinates = (ListView) view.findViewById(R.id.lv_subordinates);

                final List<Map<String, Object>> userList = new ArrayList<>();

                for (User user : Common.getSubUsers()) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("userId", user.getId());
                    item.put("name", user.getRealname());
                    userList.add(item);
                }

                SimpleAdapter subAdapter = new SimpleAdapter(getActivity(), userList,
                        R.layout.item_search_subordinates_listview, new String[]{"name"}, new int[]{R.id.tv_username});

                lv_subordinates.setAdapter(subAdapter);
                lv_subordinates.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Map<String, Object> user = userList.get(position);

                        int strSearch = (int)user.get("userId");
                        if (strSearch > 0) {
                            Intent intent = new Intent();
                            intent.setAction(MSG_TASK_FILTER);
                            intent.putExtra("userId", strSearch);
                            view.getContext().sendBroadcast(intent);
                        }
                    }
                });

                lv_subordinates.setFocusable(false);

                Global.setListViewHeightBasedOnChildren(lv_subordinates);
            }*/

            btn_task_filter_confirm = (Button) view.findViewById(R.id.btn_task_filter_confirm);
            btn_task_filter_confirm.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //发送广播
                    Intent intent = new Intent();
                    intent.setAction(MSG_TASK_FILTER);
                    intent.putExtra("type", type);
                    intent.putExtra("status", status);
                    v.getContext().sendBroadcast(intent);
                }

            });

            //重要,这句是保证事件不透传到底层视图
            view.findViewById(R.id.layout_body).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });

            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((ScrollView) view.findViewById(R.id.sv_main)).scrollTo(0, 0);
                }
            }, 500);

        }
        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.layout_type1:
                cb1.toggle();
                type = cb1.isChecked() ? 1 : 0;
                if (cb1.isChecked()) {
                    cb2.setChecked(false);
                    cb3.setChecked(false);
                }
                break;
            case R.id.layout_type2:
                cb2.toggle();
                type = cb2.isChecked() ? 2 : 0;
                if (cb2.isChecked()) {
                    cb1.setChecked(false);
                    cb3.setChecked(false);
                }
                break;
            case R.id.layout_type3:
                cb3.toggle();
                type = cb3.isChecked() ? 3 : 0;
                if (cb3.isChecked()) {
                    cb2.setChecked(false);
                    cb1.setChecked(false);
                }
                break;
            case R.id.layout_status1: //未完成
                cb4.toggle();
                status = cb4.isChecked() ? 1 : 0;
                if (cb4.isChecked()) {
                    cb5.setChecked(false);
                    cb6.setChecked(false);
                }
                break;
            case R.id.layout_status2: //待审核
                cb5.toggle();
                status = cb5.isChecked() ? 2 : 0;
                if (cb5.isChecked()) {
                    cb4.setChecked(false);
                    cb6.setChecked(false);
                }
                break;
            case R.id.layout_status3: //已完成
                cb6.toggle();
                status = cb6.isChecked() ? 3 : 0;
                if (cb6.isChecked()) {
                    cb4.setChecked(false);
                    cb5.setChecked(false);
                }
                break;
        }
    }
}
