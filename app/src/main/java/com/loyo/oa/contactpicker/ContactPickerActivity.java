package com.loyo.oa.contactpicker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loyo.oa.contactpicker.adapter.PickDepartmentAdapter;
import com.loyo.oa.contactpicker.adapter.PickUserAdapter;
import com.loyo.oa.contactpicker.adapter.PickedContactsAdapter;
import com.loyo.oa.contactpicker.callback.OnDepartmentSelected;
import com.loyo.oa.contactpicker.callback.OnPickUserEvent;
import com.loyo.oa.contactpicker.model.PickDepartmentModel;
import com.loyo.oa.contactpicker.model.PickUserModel;
import com.loyo.oa.contactpicker.model.PickedContacts;
import com.loyo.oa.contactpicker.model.PickedModel;
import com.loyo.oa.contactpicker.viewholder.PickDepartmentCell;
import com.loyo.oa.indexablelist.adapter.expand.StickyRecyclerHeadersDecoration;
import com.loyo.oa.indexablelist.widget.DividerDecoration;
import com.loyo.oa.indexablelist.widget.ZSideBar;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.customview.HorizontalScrollListView;
import com.loyo.oa.v2.db.OrganizationManager;
import com.loyo.oa.v2.db.bean.DBDepartment;
import com.loyo.oa.v2.db.bean.DBUser;
import com.loyo.oa.v2.service.OrganizationService;
import com.loyo.oa.v2.tool.BaseActivity;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static android.view.View.GONE;
import static com.loyo.oa.v2.R.id.btn_fetch;

public class ContactPickerActivity extends BaseActivity implements View.OnClickListener, OnDepartmentSelected<PickDepartmentCell>, OnPickUserEvent {

    /* UI */
    private LinearLayout ll_back;
    private LinearLayout selectAllContainer;
    private RelativeLayout noDataContainer;
    private RelativeLayout noCacheContainer;
    private ProgressWheel progressWheel;
    private ImageView noDataPlaceholder;
    private TextView tipView;
    private Button fetchButton;
    private CheckBox selectAllCheckBox;
    private RecyclerView departmentView;
    private RecyclerView userView;
    private ZSideBar zSideBar;
    private HorizontalScrollListView pickedView;

    /* Adapter */
    private PickDepartmentAdapter departmentAdapter;
    private PickUserAdapter userAdapter;
    private PickedContactsAdapter pickedAdapter;

    /* Data*/
    private List<PickDepartmentModel> departments;
    private int selectedDepartmentIndex = 0;
    private PickedContacts pickedContacts;

    /* Broadcasr */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            //Bundle b = intent.getExtras();
            if ( "com.loyo.oa.v2.ORGANIZATION_UPDATED".equals( intent.getAction() )){
                loadData();
            }
        }
    };

    @Override
    protected void onDestroy() {
        PickDepartmentModel.clearResueCache();
        PickUserModel.clearResueCache();
        unregisterBroadcastReceiver();
        super.onDestroy();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_picker);
        registerBroadcastReceiver();
        initView();
        loadData();
    }

    private void initView() {

        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);
        selectAllContainer = (LinearLayout) findViewById(R.id.select_all_container);
        noDataContainer = (RelativeLayout) findViewById(R.id.no_data_container);
        noCacheContainer = (RelativeLayout) findViewById(R.id.no_cache_container);
        progressWheel = (ProgressWheel) findViewById(R.id.progress_wheel);
        noDataPlaceholder = (ImageView) findViewById(R.id.no_data_placeholder);
        tipView = (TextView) findViewById(R.id.tip_view);
        fetchButton = (Button) findViewById(btn_fetch);
        fetchButton.setOnClickListener(this);

        selectAllCheckBox = (CheckBox) findViewById(R.id.select_all_checkbox);
        selectAllContainer.setOnClickListener(this);

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

        pickedView = (HorizontalScrollListView) findViewById(R.id.picked_contact_view);
        pickedAdapter = new PickedContactsAdapter(this);
        pickedView.setAdapter(pickedAdapter);
        pickedView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PickedModel model = (PickedModel)(parent.getAdapter().getItem(position));
                if (model.isDepartment) {
                    onDeleteAllUsers((PickDepartmentModel) model);
                }
                else {
                    onDeleteUser((PickUserModel) model);
                    userAdapter.notifyDataSetChanged();
                }
            }
        });


    }

    private void loadData() {

        if (/* 正在加载组织架构数据 */
                OrganizationManager.isOrganizationCached() == false
                        && OrganizationService.isFetchingOrganziationData()) {
            progressWheel.setVisibility(View.VISIBLE);
            noDataPlaceholder.setVisibility(View.GONE);
            tipView.setText("组织架构数据获取中...");
            fetchButton.setVisibility(View.INVISIBLE);
            noCacheContainer.setVisibility(View.VISIBLE);
            return;
        }

        pickedContacts = new PickedContacts();
        departments = departmentModelList();
        departmentAdapter.clearData();
        departmentAdapter.addData(departments);

        if (departments.size() > 0) {
            _loadUsersAtIndex(selectedDepartmentIndex);
        }
        else {
            /** 无缓存组织架构数据 */
            progressWheel.setVisibility(View.GONE);
            noDataPlaceholder.setVisibility(View.VISIBLE);
            tipView.setText("无组织架构数据");
            noCacheContainer.setVisibility(View.VISIBLE);
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
        userAdapter.setDepartment(departments.get(index));
        userAdapter.setDepartmentAllSelected(departments.get(selectedDepartmentIndex).isSelected());
        userAdapter.setCallback(this);

        selectAllCheckBox.setSelected(departments.get(selectedDepartmentIndex).isSelected());
        noDataContainer.setVisibility((result.size() <= 0) ? View.VISIBLE : GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.select_all_container:
                PickDepartmentModel model = departments.get(selectedDepartmentIndex);
                boolean selected = model.isSelected();
                if (selected) {
                    onDeleteAllUsers(model);
                }
                else {
                    onAddAllUsers(model);
                }
                selectAllCheckBox.setSelected(!selected);
                break;
            case R.id.btn_fetch:

                progressWheel.setVisibility(View.VISIBLE);
                noDataPlaceholder.setVisibility(View.GONE);
                tipView.setText("组织架构数据获取中...");
                fetchButton.setVisibility(View.INVISIBLE);
                noCacheContainer.setVisibility(View.VISIBLE);

                OrganizationService.startActionFetchAll(getApplicationContext());
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
            result.add(PickDepartmentModel.getPickModel(iterator.next()));
        }

        return result;
    }

    public void registerBroadcastReceiver(){
        IntentFilter filter = new IntentFilter("com.loyo.oa.v2.USER_EDITED");
        filter.addAction("com.loyo.oa.v2.ORGANIZATION_UPDATED");
        registerReceiver(mReceiver, filter);
    }

    public void unregisterBroadcastReceiver() {
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onDepartmentSelected(PickDepartmentCell object, int index) {
        if (index == selectedDepartmentIndex) {
            return;
        }

        selectedDepartmentIndex = index;
        _loadUsersAtIndex(selectedDepartmentIndex);
    }

    @Override
    public void onAddUser(PickUserModel model) {
        pickedContacts.addUser(model);
        selectAllCheckBox.setSelected(departments.get(selectedDepartmentIndex).isSelected());
        pickedAdapter.loadData(pickedContacts.getPickedContacts());

    }

    @Override
    public void onDeleteUser(PickUserModel model) {
        pickedContacts.deleteUser(model);
        selectAllCheckBox.setSelected(departments.get(selectedDepartmentIndex).isSelected());
        pickedAdapter.loadData(pickedContacts.getPickedContacts());
    }

    @Override
    public void onAddAllUsers(PickDepartmentModel model) {
        Log.v("pickevent", "onAddAllUsers");
        model.setSelected(true);
        pickedContacts.addAllUsersOfDepartment(model);
        userAdapter.notifyDataSetChanged();

        pickedAdapter.loadData(pickedContacts.getPickedContacts());
    }

    @Override
    public void onDeleteAllUsers(PickDepartmentModel model) {
        Log.v("pickevent", "onDeleteAllUsers");
        model.setSelected(false);
        pickedContacts.deleteAllUserOfDepartment(model);
        userAdapter.notifyDataSetChanged();

        pickedAdapter.loadData(pickedContacts.getPickedContacts());
    }
}
