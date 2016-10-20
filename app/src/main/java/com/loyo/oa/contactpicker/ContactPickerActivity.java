package com.loyo.oa.contactpicker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loyo.oa.contactpicker.adapter.PickDepartmentAdapter;
import com.loyo.oa.contactpicker.adapter.PickUserAdapter;
import com.loyo.oa.contactpicker.adapter.PickedContactsAdapter;
import com.loyo.oa.contactpicker.adapter.SearchPickUserAdapter;
import com.loyo.oa.contactpicker.callback.OnDepartmentSelected;
import com.loyo.oa.contactpicker.callback.OnPickUserEvent;
import com.loyo.oa.contactpicker.model.PickDepartmentModel;
import com.loyo.oa.contactpicker.model.PickUserModel;
import com.loyo.oa.contactpicker.model.PickedContacts;
import com.loyo.oa.contactpicker.model.PickedModel;
import com.loyo.oa.contactpicker.model.event.ContactPickedEvent;
import com.loyo.oa.contactpicker.model.result.StaffMemberCollection;
import com.loyo.oa.contactpicker.viewholder.PickDepartmentCell;
import com.loyo.oa.indexablelist.adapter.expand.StickyRecyclerHeadersDecoration;
import com.loyo.oa.indexablelist.widget.DividerDecoration;
import com.loyo.oa.indexablelist.widget.ZSideBar;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.event.AppBus;
import com.loyo.oa.v2.customview.HorizontalScrollListView;
import com.loyo.oa.v2.db.OrganizationManager;
import com.loyo.oa.v2.db.bean.DBDepartment;
import com.loyo.oa.v2.db.bean.DBUser;
import com.loyo.oa.v2.service.OrganizationService;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.StringUtil;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ContactPickerActivity extends BaseActivity implements View.OnClickListener, OnDepartmentSelected<PickDepartmentCell>, OnPickUserEvent {

    /* 常量 */
    public static final String SINGLE_SELECTION_KEY = "com.loyo.oa.contactpicker.SINGLE_SELECTION";
    public static final String DEPARTMENT_SELECTION_KEY = "com.loyo.oa.contactpicker.DEPARTMENT_SELECTION";
    public static final String STAFF_COLLECTION_KEY = "com.loyo.oa.contactpicker.STAFF_COLLECTION";
    public static final String REQUEST_KEY = "com.loyo.oa.contactpicker.ContactPickerActivity.REQUEST";
    public static final String SESSION_KEY = "com.loyo.oa.contactpicker.ContactPickerActivity.SESSION";

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
    private RecyclerView searchView;
    private ZSideBar zSideBar;
    private HorizontalScrollListView pickedView;
    private EditText searchField;
    private TextView titleView, confirmView;

    /* Adapter */
    private PickDepartmentAdapter departmentAdapter;
    private PickUserAdapter userAdapter;
    private SearchPickUserAdapter searchAdapter;
    private PickedContactsAdapter pickedAdapter;

    /* Data*/
    private List<PickDepartmentModel> departments;
    private int selectedDepartmentIndex = 0;
    private PickedContacts pickedContacts;
    private List<PickUserModel> searchBase = new ArrayList<>();
    private boolean singleSelection = false;
    private boolean deptSelection = true;
    private StaffMemberCollection previousSelection;
    private String requestIdentifer;
    private String sessionIdentifer;

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
        /** 是否单选，默认多选 */
        singleSelection = getIntent().getBooleanExtra(SINGLE_SELECTION_KEY,false);
        /** 是否可选部门，默认可选部门 */
        deptSelection = getIntent().getBooleanExtra(DEPARTMENT_SELECTION_KEY,true);

        if (singleSelection) {
            deptSelection = false;
        }

        previousSelection = (StaffMemberCollection) getIntent().getSerializableExtra(STAFF_COLLECTION_KEY);
        requestIdentifer = (String) getIntent().getSerializableExtra(REQUEST_KEY);
        sessionIdentifer = (String) getIntent().getSerializableExtra(SESSION_KEY);

        registerBroadcastReceiver();
        initView();
        loadData();
    }

    private void initView() {

        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);
        titleView = (TextView) findViewById(R.id.tv_title);
        titleView.setText("成员选择");
        confirmView = (TextView) findViewById(R.id.tv_add);
        confirmView.setOnClickListener(this);

        selectAllContainer = (LinearLayout) findViewById(R.id.select_all_container);
        noDataContainer = (RelativeLayout) findViewById(R.id.no_data_container);
        noCacheContainer = (RelativeLayout) findViewById(R.id.no_cache_container);
        progressWheel = (ProgressWheel) findViewById(R.id.progress_wheel);
        noDataPlaceholder = (ImageView) findViewById(R.id.no_data_placeholder);
        tipView = (TextView) findViewById(R.id.tip_view);
        fetchButton = (Button) findViewById(R.id.btn_fetch);
        fetchButton.setOnClickListener(this);

        selectAllCheckBox = (CheckBox) findViewById(R.id.select_all_checkbox);
        selectAllContainer.setOnClickListener(this);
        if (singleSelection || !deptSelection) {
            selectAllContainer.setVisibility(View.GONE);
        }

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

        searchView = (RecyclerView) findViewById(R.id.search_user_view);
        searchView.setLayoutManager(new LinearLayoutManager(this));
        searchAdapter = new SearchPickUserAdapter(this);
        searchView.setAdapter(searchAdapter);
        searchAdapter.setCallback(this);

        final StickyRecyclerHeadersDecoration headersDecor2 = new StickyRecyclerHeadersDecoration(searchAdapter);
        searchView.addItemDecoration(headersDecor2);
        searchView.addItemDecoration(new DividerDecoration(this));

        searchField = (EditText) findViewById(R.id.search_user_field);
        searchField.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(final CharSequence arg0, final int arg1, final int arg2, final int arg3) {
            }

            @Override
            public void beforeTextChanged(final CharSequence arg0, final int arg1, final int arg2, final int arg3) {
            }

            @Override
            public void afterTextChanged(final Editable s) {
                if (!TextUtils.isEmpty(s.toString())) {
                    doSearch(s.toString());
                }
                else {
                    searchField.clearFocus();
                }
            }
        });
        searchField.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    searchView.setVisibility(View.VISIBLE);
                }
                else {
                    searchView.setVisibility(View.INVISIBLE);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    searchAdapter.loadData(new ArrayList<PickUserModel>());
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
        else {
            progressWheel.setVisibility(View.VISIBLE);
            noDataPlaceholder.setVisibility(View.GONE);
            tipView.setText("加载中...");
            fetchButton.setVisibility(View.INVISIBLE);
            noCacheContainer.setVisibility(View.VISIBLE);
            selectAllContainer.setVisibility(View.INVISIBLE);
        }

        pickedContacts = new PickedContacts(deptSelection);

        Observable.just("loadData")
                .map(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String text) {
                        departments = departmentModelList();
                        if (previousSelection != null && (previousSelection.depts.size() > 0
                                || previousSelection.users.size()>0)) {

                            if (previousSelection.depts.size() > 0) {
                                List<String> previous = previousSelection.departmentIds();
                                for (String deptId:previous) {
                                    DBDepartment dept = OrganizationManager.shareManager().getDepartment(deptId);
                                    if (dept != null) {
                                        PickDepartmentModel model = PickDepartmentModel.getPickModel(dept);
                                        pickedContacts.addAllUsersOfDepartment(model);
                                    }
                                }
                            }
                            if (previousSelection.users.size()>0) {
                                List<String> previous = previousSelection.userIds();
                                for (String userId:previous) {
                                    DBUser user = OrganizationManager.shareManager().getUser(userId);
                                    if (user != null) {
                                        PickUserModel model = PickUserModel.getPickModel(user);
                                        pickedContacts.addUser(model);
                                    }
                                }
                            }
                        }
                        return true;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean suc) {
                        noCacheContainer.setVisibility(View.INVISIBLE);
                        selectAllContainer.setVisibility((singleSelection|| !deptSelection)?View.GONE:View.VISIBLE);
                        departmentAdapter.clearData();
                        departmentAdapter.addData(departments);

                        if (pickedContacts.getCount() > 0) {
                            pickedAdapter.loadData(pickedContacts.getPickedContacts());
                            confirmView.setText("确定" + ("(" + pickedContacts.getCount() + ")"));
                        }

                        if (departments.size() > 0) {
                            _loadUsersAtIndex(selectedDepartmentIndex);
                        }
                        else {
                            /** 无缓存组织架构数据 */
                            progressWheel.setVisibility(View.GONE);
                            noDataPlaceholder.setVisibility(View.VISIBLE);
                            tipView.setText("无组织架构数据");
                            fetchButton.setVisibility(View.VISIBLE);
                            noCacheContainer.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private void _loadUsersAtIndex(int index){

        DBDepartment department = departments.get(index).department;
        List<DBUser> users = department.allUsersSortedByPinyin();

        List<PickUserModel> result = new ArrayList<>();
        Iterator<DBUser> iterator = users.iterator();
        while (iterator.hasNext()) {
            result.add(PickUserModel.getPickModel(iterator.next()));
        }

        if (index == 0) {
            searchBase.addAll(result);
        }

        userAdapter.loadData(result);
        userAdapter.setDepartment(departments.get(index));
        userAdapter.setDepartmentAllSelected(departments.get(selectedDepartmentIndex).isSelected());
        userAdapter.setCallback(this);

        selectAllCheckBox.setSelected(departments.get(selectedDepartmentIndex).isSelected());
        noDataContainer.setVisibility((result.size() <= 0) ? View.VISIBLE : View.GONE);
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
            case R.id.tv_add:
                doResultAction();
            default:

                break;
        }
    }

    void doSearch(String text) {
        String key = text.trim();
        if (StringUtil.isEmpty(key)) {
            Global.Toast("请输入查询姓名!");
            return;
        }

        List<PickUserModel> result = new ArrayList<>();

        for (PickUserModel usrModel : searchBase) {
            if (usrModel.user == null) {
                continue;
            } else if (usrModel.user.name != null && usrModel.user.name.contains(key)) {
                result.add(usrModel);
                continue;
            } else if (usrModel.user.fullPinyin != null && usrModel.user.fullPinyin.startsWith(key)) {
                result.add(usrModel);
                continue;
            } else if (usrModel.user.simplePinyin != null && usrModel.user.simplePinyin.startsWith(key)) {
                result.add(usrModel);
                continue;
            } else if (usrModel.user.shortDeptNames != null && usrModel.user.shortDeptNames.contains(key)) {
                result.add(usrModel);
                continue;
            }
        }

        if (result.size() == 0) {
            Toast.makeText(this, "未搜索到相关结果", Toast.LENGTH_SHORT).show();
        }

        searchAdapter.loadData(result);
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

    /** 选择结束 */
    private void doResultAction() {
        if (pickedContacts.getCount() > 0) {
            StaffMemberCollection collection = pickedContacts.getStaffMemberCollection();
            ContactPickedEvent event = new ContactPickedEvent(collection);
            event.request = requestIdentifer;
            event.session = sessionIdentifer;
            AppBus.getInstance().post(event);
        }
        finish();
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

        if (singleSelection) {
            List<PickedModel> list = pickedContacts.getPickedContacts();
            for (PickedModel picked:list) {
                if (picked.isDepartment == false) {
                    ((PickUserModel)picked).setSelected(false);
                }
            }
            pickedContacts.clearSelection();
        }

        pickedContacts.addUser(model);
        selectAllCheckBox.setSelected(departments.get(selectedDepartmentIndex).isSelected());
        pickedAdapter.loadData(pickedContacts.getPickedContacts());
        userAdapter.notifyDataSetChanged();
        searchAdapter.notifyDataSetChanged();
        confirmView.setText("确定" + ("(" + pickedContacts.getCount() + ")"));
        selectAllCheckBox.setSelected(departments.get(selectedDepartmentIndex).isSelected());

        /** 选中一个用户，结束选择 */
        if (singleSelection) {
            doResultAction();
        }
    }

    @Override
    public void onDeleteUser(PickUserModel model) {
        pickedContacts.deleteUser(model);
        selectAllCheckBox.setSelected(departments.get(selectedDepartmentIndex).isSelected());
        userAdapter.notifyDataSetChanged();
        pickedAdapter.loadData(pickedContacts.getPickedContacts());
        searchAdapter.notifyDataSetChanged();
        confirmView.setText("确定" + ("(" + pickedContacts.getCount() + ")"));
        selectAllCheckBox.setSelected(departments.get(selectedDepartmentIndex).isSelected());
    }

    @Override
    public void onAddAllUsers(PickDepartmentModel model) {
        model.setSelected(true);
        pickedContacts.addAllUsersOfDepartment(model);
        userAdapter.notifyDataSetChanged();

        pickedAdapter.loadData(pickedContacts.getPickedContacts());
        searchAdapter.notifyDataSetChanged();
        confirmView.setText("确定" + ("(" + pickedContacts.getCount() + ")"));
        selectAllCheckBox.setSelected(departments.get(selectedDepartmentIndex).isSelected());
    }

    @Override
    public void onDeleteAllUsers(PickDepartmentModel model) {
        model.setSelected(false);
        pickedContacts.deleteAllUserOfDepartment(model);
        userAdapter.notifyDataSetChanged();

        pickedAdapter.loadData(pickedContacts.getPickedContacts());
        searchAdapter.notifyDataSetChanged();
        confirmView.setText("确定" + ("(" + pickedContacts.getCount() + ")"));
        selectAllCheckBox.setSelected(departments.get(selectedDepartmentIndex).isSelected());
    }
}
