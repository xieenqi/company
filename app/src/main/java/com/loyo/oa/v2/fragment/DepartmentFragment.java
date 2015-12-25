package com.loyo.oa.v2.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.DepartmentUserActivity;
import com.loyo.oa.v2.adapter.DepartmentListViewAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.tool.LogUtil;

/**
 * 部门通讯录Fragment
 * */

@SuppressLint("ValidFragment")
public class DepartmentFragment extends Fragment {
    //    MainApp app = MainApp.getMainApp();
    DepartmentUserActivity departmentUserActivity;
    View view;

    public ListView listView_department;
    public DepartmentListViewAdapter departmentListViewAdapter;

    int select_type, show_type;

    String superDeptId;

    public DepartmentFragment() {

    }

    public DepartmentFragment(String  _deptId, int _select_type, int _show_type) {
        superDeptId = _deptId;
        select_type = _select_type;
        show_type = _show_type;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        departmentUserActivity = (DepartmentUserActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        app.logUtil.d("onCreateView");
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_department, container, false);

            init();
        }
        return view;
    }

    void init() {
        departmentListViewAdapter = new DepartmentListViewAdapter(departmentUserActivity,
                Common.getLstDepartment(superDeptId), select_type, show_type);

        listView_department = (ListView) view.findViewById(R.id.listView_department);
        listView_department.setAdapter(departmentListViewAdapter);
        listView_department.setItemsCanFocus(false);
//        listView_department.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

    }
}
