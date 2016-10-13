package com.loyo.oa.contactpicker;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.loyo.oa.contactpicker.adapter.PickDepartmentAdapter;
import com.loyo.oa.contactpicker.adapter.PickUserAdapter;
import com.loyo.oa.contactpicker.callback.OnDepartmentSelected;
import com.loyo.oa.contactpicker.model.PickDepartmentModel;
import com.loyo.oa.contactpicker.model.PickUserModel;
import com.loyo.oa.contactpicker.viewholder.PickDepartmentCell;
import com.loyo.oa.indexablelist.adapter.expand.StickyRecyclerHeadersDecoration;
import com.loyo.oa.indexablelist.widget.DividerDecoration;
import com.loyo.oa.indexablelist.widget.ZSideBar;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.db.OrganizationManager;
import com.loyo.oa.v2.db.bean.DBDepartment;
import com.loyo.oa.v2.db.bean.DBUser;
import com.loyo.oa.v2.tool.BaseActivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ContactPickerActivity extends BaseActivity implements View.OnClickListener, OnDepartmentSelected<PickDepartmentCell> {

    private LinearLayout ll_back;

    private RecyclerView departmentView;
    private RecyclerView userView;
    private ZSideBar zSideBar;

    private PickDepartmentAdapter departmentAdapter;
    private PickUserAdapter userAdapter;

    /* Data*/
    private List<PickDepartmentModel> departments;
    private int selectedDepartmentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_picker);
        initView();
        loadData();
    }

    private void initView() {

        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);

        departmentView = (RecyclerView) findViewById(R.id.department_view);
        departmentView.setLayoutManager(new LinearLayoutManager(this));
        departmentAdapter = new PickDepartmentAdapter(this);
        departmentView.setAdapter(departmentAdapter);
        departmentView.addItemDecoration(new DividerDecoration(this));
        departmentAdapter.setCallback(this);

        userView = (RecyclerView) findViewById(R.id.user_view);
        userView.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new PickUserAdapter(this);
        userView.setAdapter(userAdapter);

        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(userAdapter);
        userView.addItemDecoration(headersDecor);
        userView.addItemDecoration(new DividerDecoration(this));

        zSideBar = (ZSideBar) findViewById(R.id.contact_zsidebar);
        zSideBar.setupWithRecycler(userView);

    }

    private void loadData() {
        departments = departmentModelList();
        departmentAdapter.clearData();
        departmentAdapter.addData(departments);

        if (departments.size() > 0) {
            _loadUsersAtIndex(selectedDepartmentIndex);
        }
    }

    private void _loadUsersAtIndex(int index){

        DBDepartment department = departments.get(index).department;
        List<DBUser> users = department.allUsersSortedByPinyin();

        List<PickUserModel> result = new ArrayList<>();
        Iterator<DBUser> iterator = users.iterator();
        while (iterator.hasNext()) {
            result.add(PickUserModel.getPickModel(iterator.next()));
        }

        userAdapter.loadData(result);

        long end = System.currentTimeMillis();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back:
                onBackPressed();
                break;
            default:

                break;
        }
    }

    /**
     * 部门列表
     */
    private List<PickDepartmentModel> departmentModelList() {
        List<PickDepartmentModel> result = new ArrayList<>();

        DBDepartment company = OrganizationManager.shareManager().getsComany();
        List<DBDepartment> flats = company.flatDepartments();
        Iterator<DBDepartment> iterator = flats.iterator();
        while (iterator.hasNext()) {
            result.add(PickDepartmentModel.instance(iterator.next()));
        }

        return result;
    }

    @Override
    public void onDepartmentSelected(PickDepartmentCell object, int index) {
        if (index == selectedDepartmentIndex) {
            return;
        }

        selectedDepartmentIndex = index;
        _loadUsersAtIndex(selectedDepartmentIndex);
    }
}
