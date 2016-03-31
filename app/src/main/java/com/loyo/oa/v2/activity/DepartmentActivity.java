package com.loyo.oa.v2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.adapter.DepartmentListViewAdapter;
import com.loyo.oa.v2.adapter.UserListViewAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Department;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.ViewUtil;

import java.util.ArrayList;

public class DepartmentActivity extends FragmentActivity implements View.OnClickListener {
    public static final int RESULT_ON_ACTIVITY_RETURN = 100;

    TextView tv_title;
    ViewGroup img_title_left;
    Button btn_title_right;

    public int select_type; //选择类型(0=多选，1=单选)
    public int show_type;   //页面类型（0=部门+人员,1=仅人员）
//    public String title;

    //部门Id
    public String deptId;
    String DeptName;
    MainApp app = MainApp.getMainApp();

    ListView listView_user, listView_department;
    ViewGroup layout_dept, layout_user, layout_checkall;
    CheckBox cb_all;

    DepartmentListViewAdapter deptAdapter;
    UserListViewAdapter userAdapter;

    private int count;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department);

        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();

            select_type = bundle.getInt(DepartmentUserActivity.STR_SELECT_TYPE, 0);
            show_type = bundle.getInt(DepartmentUserActivity.STR_SHOW_TYPE, 0);
//          title = bundle.getString(DepartmentUserActivity.STR_TITEL);
            deptId = bundle.getString(DepartmentUserActivity.STR_SUPER_ID, "");
            DeptName = bundle.getString(DepartmentUserActivity.STR_SUPER_NAME, "");
        }

        initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        syncButton();
    }

    private void initUI() {
//        tv_title_1 = (TextView) findViewById(R.id.tv_title_1);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(DeptName);

        img_title_left = (ViewGroup) findViewById(R.id.img_title_left);
        img_title_left.setOnClickListener(this);
        img_title_left.setOnTouchListener(new ViewUtil.OnTouchListener_view_transparency());

        btn_title_right = (Button) findViewById(R.id.btn_title_right);
        btn_title_right.setOnClickListener(this);

        layout_user = (ViewGroup) findViewById(R.id.layout_user);
        layout_dept = (ViewGroup) findViewById(R.id.layout_dept);

        layout_checkall = (ViewGroup) findViewById(R.id.layout_checkall);

        listView_user = (ListView) findViewById(R.id.listView_user);
        listView_department = (ListView) findViewById(R.id.listView_department);

        cb_all = (CheckBox) findViewById(R.id.cb_all);

        switch (select_type) {
            case DepartmentUserActivity.TYPE_SELECT_MULTUI:
                layout_checkall.setOnClickListener(this);
                break;
            case DepartmentUserActivity.TYPE_SELECT_SINGLE:
                layout_checkall.setVisibility(View.GONE);
                break;
            default:

                break;
        }

        ArrayList<Department> listDept = Common.getLstDepartment(deptId);
        if (listDept != null && listDept.size() > 0) {
            deptAdapter = new DepartmentListViewAdapter(this, listDept, select_type, show_type);
            listView_department.setAdapter(deptAdapter);
            Global.setListViewHeightBasedOnChildren(listView_department);
        } else {
            layout_dept.setVisibility(View.GONE);
        }

        if (show_type == DepartmentUserActivity.TYPE_SHOW_USER) {
            layout_checkall.setVisibility(View.GONE);
        }

        final ArrayList<User> listUser = Common.getListUser(deptId);
        if (listUser != null && listUser.size() > 0) {
            userAdapter = new UserListViewAdapter(this, listUser, select_type);
            listView_user.setAdapter(userAdapter);
            Global.setListViewHeightBasedOnChildren(listView_user);

            listView_user.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                    UserListViewAdapter.Item_info item_info = (UserListViewAdapter.Item_info) view.getTag();
                    item_info.cBox.toggle();

                    User user = listUser.get(position);
                    if (user == null) {
                        return;
                    }

                    //发送广播,返回负责人姓名
                    if (select_type == DepartmentUserActivity.TYPE_SELECT_SINGLE) {

                        Intent intent = new Intent();
                        intent.putExtra(User.class.getName(), user);
                        app.finishActivity(DepartmentActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
                    } else if (select_type == DepartmentUserActivity.TYPE_SELECT_MULTUI) {

                        DepartmentUserActivity.sendMultiSelectUsers(view.getContext(),
                                user.id, user.getRealname(), "", null, item_info.cBox.isChecked());
                        if(item_info.cBox.isChecked())
                            count++;
                        else
                            count--;
                        syncButton();

                    }
                }
            });
        } else {
            layout_user.setVisibility(View.GONE);
        }

//        if (title != null) {
//            tv_title_1.setText(title);
//        }
    }

    void syncButton() {
        btn_title_right.setText(String.format("确认(%d)", count));
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, RESULT_CANCELED, null);
                break;
            case R.id.btn_title_right:
                app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, new Intent());
                break;
            case R.id.layout_checkall:
                //1.只传当前部门Id和当前部门人数。
                //2.禁用部门和人员选择

                cb_all.toggle();

                if (deptAdapter != null) {
                    deptAdapter.setEnabled(!cb_all.isChecked());
                    listView_department.setAdapter(deptAdapter);

                    listView_department.setEnabled(!cb_all.isChecked());
                    listView_department.setFocusable(!cb_all.isChecked());

                    //变更确认的部门人数
                }

                if (userAdapter != null) {
                    userAdapter.setEnabled(!cb_all.isChecked());
                    listView_user.setAdapter(userAdapter);

                    listView_user.setEnabled(!cb_all.isChecked());
                    listView_user.setFocusable(!cb_all.isChecked());
                }
                if(cb_all.isChecked())
                    count=Common.getDepartmentUsersCount(deptId);
                else
                    count=0;
                Department d = Common.getDepartment(deptId);
                DepartmentUserActivity.sendMultiSelectUsers(v.getContext(), "", null, deptId,
                        d != null ? d.getName() : "", cb_all.isChecked());
                syncButton();

                break;

            default:

                break;
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_ON_ACTIVITY_RETURN) {
            if (select_type == DepartmentUserActivity.TYPE_SELECT_SINGLE) {
                app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, data);
            } else if (select_type == DepartmentUserActivity.TYPE_SELECT_MULTUI) {
                app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, new Intent());
            }
        }
    }
}
