package com.loyo.oa.v2.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.contact.ContactInfoActivity_;
import com.loyo.oa.v2.activity.contact.ContactsDepartmentActivity_;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Department;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.ViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * com.loyo.oa.v2.fragment
 * 描述 :部门里面成员和下属部门展示页
 * 作者 : ykb
 * 时间 : 15/9/6.
 */
public class ContactsSubdivisionsFragment extends BaseFragment implements View.OnClickListener {

    private String deptId;
    MainApp app = MainApp.getMainApp();
    ListView listView_user, listView_department;
    ViewGroup layout_dept, layout_user;
    DepartmentListViewAdapter deptAdapter;
    UserListViewAdapter userAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deptId = null == getArguments() ? "" : getArguments().getString("depId");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_contacts_subdivisions, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI(view);
    }

    private void initUI(View view) {
        layout_dept = (ViewGroup) view.findViewById(R.id.layout_dept);
        layout_user = (ViewGroup) view.findViewById(R.id.layout_user);

        listView_user = (ListView) view.findViewById(R.id.listView_user);
        listView_department = (ListView) view.findViewById(R.id.listView_department);

        ArrayList<Department> listDept = Common.getLstDepartment(deptId);
        if (listDept != null && listDept.size() > 0) {
            deptAdapter = new DepartmentListViewAdapter(getActivity(), listDept);
            listView_department.setAdapter(deptAdapter);
            Global.setListViewHeightBasedOnChildren(listView_department);
        } else {
            layout_dept.setVisibility(View.GONE);
        }

        listView_department.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Department d = (Department) adapterView.getAdapter().getItem(i);
                if (null != d) {
                    Bundle b = new Bundle();
                    b.putString("depId", d.getId());
                    b.putString("depName", d.getName());
                    app.startActivity(getActivity(), ContactsDepartmentActivity_.class, MainApp.ENTER_TYPE_ZOOM_OUT, false, b);
                }
            }
        });

        final ArrayList<User> listUser = Common.getListUser(deptId);
        if (listUser != null && listUser.size() > 0) {
            userAdapter = new UserListViewAdapter(getActivity(), listUser);
            listView_user.setAdapter(userAdapter);
            Global.setListViewHeightBasedOnChildren(listView_user);
        } else {
            layout_user.setVisibility(View.GONE);
        }
        listView_user.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = listUser.get(position);
                if (user == null) {
                    return;
                }

                Bundle b = new Bundle();
                b.putSerializable("user", user);
                app.startActivity(getActivity(), ContactInfoActivity_.class, MainApp.ENTER_TYPE_ZOOM_OUT, false, b);
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                break;
        }
    }

    public class DepartmentListViewAdapter extends BaseAdapter {
        LayoutInflater mInflater;
        public ArrayList<Department> listDepartment;

        public DepartmentListViewAdapter(Context _context, ArrayList<Department> lstData) {
            mInflater = LayoutInflater.from(_context);
            listDepartment = lstData;
        }

        @Override
        public int getCount() {
            return listDepartment.size();
        }

        @Override
        public Object getItem(int position) {
            return listDepartment.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = mInflater.inflate(R.layout.item_contacts_department_child, null, false);
            }
            Department d = listDepartment.get(position);
            TextView tv_content = com.loyo.oa.v2.tool.ViewHolder.get(convertView, R.id.tv_content);
            String departmentName = d.getName();
            int userSize = Common.getUsersByDeptId(d.getId(), new ArrayList<User>()).size();
            String members = "(" + userSize + "人" + ")";
            departmentName = departmentName.concat(members);
            tv_content.setText(departmentName);

            if (position == listDepartment.size() - 1) {
                ViewHolder.get(convertView, R.id.devider).setVisibility(View.GONE);
            } else {
                ViewHolder.get(convertView, R.id.devider).setVisibility(View.VISIBLE);
            }
            return convertView;
        }
    }

    public class UserListViewAdapter extends BaseAdapter {

        ArrayList<User> listUser;

        public UserListViewAdapter(Context _context, ArrayList<User> lstData) {
            this.listUser = lstData;
        }

        @Override
        public int getCount() {
            return listUser.size();
        }

        @Override
        public Object getItem(int position) {
            return listUser.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = LayoutInflater.from(app).inflate(R.layout.item_contacts_child, null, false);
            }
            User user = listUser.get(position);
            ImageView img = ViewHolder.get(convertView, R.id.img);
            TextView tv_content = ViewHolder.get(convertView, R.id.tv_name);
            TextView tv_position = ViewHolder.get(convertView, R.id.tv_position);

            tv_content.setText(user.getRealname());
            String departmentName = user.departmentsName;
           // LogUtil.d(" re人员信息： "+MainApp.gson.toJson(user));
            if (null != user.shortPosition && !TextUtils.isEmpty(user.shortPosition.getName())) {
                departmentName = departmentName.concat(" | " + user.shortPosition.getName());
            }
            tv_position.setText(departmentName);

            if (!TextUtils.isEmpty(user.avatar)) {
                ImageLoader.getInstance().displayImage(user.avatar, img);
            }

            if (position == listUser.size() - 1) {
                ViewHolder.get(convertView, R.id.devider).setVisibility(View.GONE);
            } else {
                ViewHolder.get(convertView, R.id.devider).setVisibility(View.VISIBLE);
            }
            return convertView;
        }
    }

}
