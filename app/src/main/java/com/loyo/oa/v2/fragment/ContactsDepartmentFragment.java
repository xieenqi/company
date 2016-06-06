package com.loyo.oa.v2.fragment;

import android.content.ContentResolver;
import android.content.Context;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AlphabetIndexer;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.contact.ContactInfoActivity_;
import com.loyo.oa.v2.activity.contact.ContactsDepartmentActivity_;
import com.loyo.oa.v2.adapter.ContactsMyDeptOtherAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.ContactsGroup;
import com.loyo.oa.v2.beans.Department;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.ViewHolder;
import com.loyo.oa.v2.tool.customview.MyLetterListView;

import java.util.ArrayList;

/**
 * com.loyo.oa.v2.fragment
 * 描述 :【公司的部门】   列表页
 * 作者 : ykb
 * 时间 : 15/8/24.
 */
public class ContactsDepartmentFragment extends BaseFragment {

    private View view;
    private ExpandableListView expandableListView_user;
    private ContactsDepartmentExpandableListAdapter userGroupExpandableListAdapter;
    private ContactsMyDeptOtherAdapter otherDeptAdapter;
    private ArrayList<ContactsGroup> lstUserGroupData;
    private MyLetterListView letterView;
    private ListView listView_otheruser;
    private AlphabetIndexer index;
    private TextView tv_dialog;
    private String mIndex;
    private String myDeptId;
    private String myDeptName;
    public View headView;
    public View footView;
    public LayoutInflater mInflater;
    public TextView nameTv;
    public RelativeLayout item_mydept;
    public ArrayList<User> commyUsers = new ArrayList<>();//与部门同级的人员

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lstUserGroupData = Common.getContactsGroups(null);

        StringBuffer sb = null;

        for (int i = 0; i < lstUserGroupData.size(); i++) {

            if (sb == null) {
                sb = new StringBuffer();
            }

            ContactsGroup ugd = lstUserGroupData.get(i);
            sb.append(ugd.getGroupName().toUpperCase());
        }

        if (sb == null) {
            mIndex = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        } else {
            mIndex = "".concat(sb.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_user, container, false);
            mInflater = LayoutInflater.from(getActivity());

            /*我的部门View初始化*/
            headView = mInflater.inflate(R.layout.item_mydept_layout, null);
            nameTv = (TextView) headView.findViewById(R.id.tv_mydept_content);
            item_mydept = (RelativeLayout) headView.findViewById(R.id.des_mydept_layout);

            /*其他人员View初始化*/
            footView = mInflater.inflate(R.layout.fragment_other_user, null);
            listView_otheruser = (ListView) footView.findViewById(R.id.listView_otheruser);
            tv_dialog = (TextView) view.findViewById(R.id.tv_dialog);
            IndexCursor cursor = new IndexCursor(lstUserGroupData);
            index = new AlphabetIndexer(cursor, 0, mIndex);

            letterView = (MyLetterListView) view.findViewById(R.id.letter_View);
            //letterView.setKeyword(mIndex);
            letterView.setOnTouchingLetterChangedListener(new MyLetterListView.OnTouchingLetterChangedListener() {

                @Override
                public void onTouchingLetterChanged(int selectionIndex, String sectionLetter, int state) {
                    int position = index.getPositionForSection(selectionIndex);

                    switch (state) {
                        case MyLetterListView.FINGER_ACTION_DOWN: // 手指按下
//                            tv_dialog.setVisibility(View.VISIBLE);
                            tv_dialog.setText(sectionLetter);
                            scroll(position - 1);
                            break;
                        case MyLetterListView.FINGER_ACTION_MOVE: // 手指滑动
                            tv_dialog.setText(sectionLetter);
                            scroll(position - 1);
                            break;
                        case MyLetterListView.FINGER_ACTION_UP:
                            tv_dialog.setVisibility(View.GONE);// 手指离开
                            break;
                        default:
                            break;
                    }
                }
            });

            init_expandableListView_user();
        }
        return view;
    }

    void scroll(int position) {
        int count = 0, groupPosition = 0;
        for (ContactsGroup d : lstUserGroupData) {
            for (int i = 0; i < d.getDepartments().size(); i++) {
                if (count == position) {
                    expandableListView_user.setSelectedGroup(groupPosition);
                }
                count++;
            }
            groupPosition++;
        }
    }

    class IndexCursor implements Cursor {

        ArrayList<ContactsGroup> data;

        public IndexCursor(ArrayList<ContactsGroup> _data) {
            this.data = _data;
        }

        private int position;

        @Override
        public void copyStringToBuffer(int columnIndex, CharArrayBuffer buffer) {

        }

        @Override
        public short getShort(int columnIndex) {
            return 0;
        }

        @Override
        public void unregisterContentObserver(ContentObserver observer) {

        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public void setNotificationUri(ContentResolver cr, Uri uri) {

        }

        @Override
        public Uri getNotificationUri() {
            return null;
        }

        @Override
        public boolean getWantsAllOnMoveCalls() {
            return false;
        }

        @Override
        public void setExtras(Bundle extras) {

        }

        @Override
        public Bundle getExtras() {
            return null;
        }

        @Override
        public Bundle respond(Bundle extras) {
            return null;
        }

        @Override
        public int getInt(int columnIndex) {
            return 0;
        }

        @Override
        public long getLong(int columnIndex) {
            return 0;
        }

        @Override
        public float getFloat(int columnIndex) {
            return 0;
        }

        @Override
        public double getDouble(int columnIndex) {
            return 0;
        }

        @Override
        public int getType(int columnIndex) {
            return 0;
        }

        @Override
        public boolean isNull(int columnIndex) {
            return false;
        }

        @Override
        public void deactivate() {

        }

        @Override
        public boolean requery() {
            return false;
        }

        @Override
        public boolean isAfterLast() {
            return false;
        }

        @Override
        public int getColumnIndex(String columnName) {
            return 0;
        }

        @Override
        public int getColumnIndexOrThrow(String columnName) throws IllegalArgumentException {
            return 0;
        }

        @Override
        public String getColumnName(int columnIndex) {
            return null;
        }

        @Override
        public String[] getColumnNames() {
            return new String[0];
        }

        @Override
        public int getColumnCount() {
            return 0;
        }

        @Override
        public byte[] getBlob(int columnIndex) {
            return new byte[0];
        }

        @Override
        public String getString(int columnIndex) {
            int count = 0;
            for (ContactsGroup d : lstUserGroupData) {
                for (Department u : d.getDepartments()) {
                    if (count == position) {

                        if (u.getFullPinyin() != null && u.getFullPinyin().length() > 0) {
                            return u.getFullPinyin().substring(0, 1).toUpperCase();
                        } else if (u.getSimplePinyin() != null && u.getSimplePinyin().length() > 0) {
                            return u.getSimplePinyin().substring(0, 1).toUpperCase();
                        } else {
                            return "";
                        }
                    }
                    count++;
                }
            }
            return "";
        }

        @Override
        public void close() {

        }

        @Override
        public boolean isClosed() {
            return false;
        }

        @Override
        public void registerContentObserver(ContentObserver observer) {

        }

        @Override
        public int getCount() {
            int count = 0;
            for (ContactsGroup d : lstUserGroupData) {
                count += d.getDepartments().size();
            }

            return count;
        }

        @Override
        public int getPosition() {
            return position;
        }

        @Override
        public boolean move(int offset) {
            return false;
        }

        @Override
        public boolean moveToPosition(int position) {
            this.position = position;
            return true;
        }

        @Override
        public boolean moveToFirst() {
            return false;
        }

        @Override
        public boolean moveToLast() {
            return false;
        }

        @Override
        public boolean moveToNext() {
            return false;
        }

        @Override
        public boolean moveToPrevious() {
            return false;
        }

        @Override
        public boolean isFirst() {
            return false;
        }

        @Override
        public boolean isLast() {
            return false;
        }

        @Override
        public boolean isBeforeFirst() {
            return false;
        }
    }

    /**
     * 初始化列表 信息
     */
    void init_expandableListView_user() {

        if (lstUserGroupData == null) {
            return;
        }

        /**
         * 获取全公司与部门平级的人员
         * */
        ArrayList<Department> allDepet = Common.getLstDepartment();
        Department commy = allDepet.get(0);
        for (User users : commy.getUsers()) {
            commyUsers.add(users);
        }

        /**
         * 设置我的部门排在首位，整个部门中移除自己部门
         * */
        if (MainApp.user.depts.size() > 0) {
            myDeptId = MainApp.user.depts.get(0).getShortDept().getId();
            myDeptName = MainApp.user.depts.get(0).getShortDept().getName();
        } else {
            myDeptName = MainApp.user.role.name;
            myDeptId = MainApp.user.role.id;
        }

        int userSize = Common.getAllUsersByDeptId(myDeptId, new ArrayList<User>()).size();
        String members = "(" + userSize + "人)";
        if (null != myDeptName) {
            myDeptName = myDeptName.concat(members);
            nameTv.setText(myDeptName);
        }

        for (ContactsGroup contactsGroup : lstUserGroupData) {
            for (Department department : contactsGroup.getDepartments()) {
                if (null != myDeptId && myDeptId.equals(department.getId())) {
                    contactsGroup.getDepartments().remove(department);
                    break;
                }
            }
        }

        /**
         * 设置公司级别员工展示
         * */
        otherDeptAdapter = new ContactsMyDeptOtherAdapter(commyUsers, getActivity());
        listView_otheruser.setAdapter(otherDeptAdapter);
        Global.setListViewHeightBasedOnChildren(listView_otheruser);//解决ScrollView嵌套listView不展开问题。
        expandableListView_user = (ExpandableListView) view.findViewById(R.id.expandableListView_user);
        expandableListView_user.setDivider(null);
        userGroupExpandableListAdapter = new ContactsDepartmentExpandableListAdapter();
        expandableListView_user.addHeaderView(headView);
        expandableListView_user.addFooterView(footView);
        expandableListView_user.setAdapter(userGroupExpandableListAdapter);
        //Global.setListViewHeightBasedOnChildren(expandableListView_user);
        expandableListView_user.setGroupIndicator(null);
        expandableListView_user.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                ContactsGroup group = lstUserGroupData.get(groupPosition);
                Bundle b = new Bundle();
                b.putString("depId", group.getDepartments().get(childPosition).getId());
                b.putString("depName", group.getDepartments().get(childPosition).getName());
                app.startActivity(getActivity(), ContactsDepartmentActivity_.class, MainApp.ENTER_TYPE_ZOOM_OUT, false, b);
                return true;
            }
        });

        for (int i = 0; i < lstUserGroupData.size(); i++) {
            expandableListView_user.expandGroup(i);
        }

        expandableListView_user.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });

        /*本部门监听器*/
        item_mydept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle b = new Bundle();
                b.putString("depId", myDeptId);
                b.putString("depName", myDeptName);
                app.startActivity(getActivity(), ContactsDepartmentActivity_.class, MainApp.ENTER_TYPE_ZOOM_OUT, false, b);
            }
        });

        /*其他人员列表监听*/
        listView_otheruser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Bundle b = new Bundle();
                b.putSerializable("user", commyUsers.get(position));
                app.startActivity(getActivity(), ContactInfoActivity_.class, MainApp.ENTER_TYPE_ZOOM_OUT, false, b);
            }
        });
    }

    //-------------------------------------------------部门适配器---------------------------------------------
    private class ContactsDepartmentExpandableListAdapter extends BaseExpandableListAdapter {
        LayoutInflater layoutInflater;

        public ContactsDepartmentExpandableListAdapter() {
            layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getGroupCount() {
            return lstUserGroupData.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return lstUserGroupData.get(groupPosition).getDepartments().size();
        }

        @Override
        public ContactsGroup getGroup(int groupPosition) {
            return lstUserGroupData.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = layoutInflater.inflate(R.layout.item_usergroup_group, null);
            TextView title = ViewHolder.get(convertView, R.id.textView_item_titel);
            title.setText(getGroup(groupPosition).getGroupName());
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = layoutInflater.inflate(R.layout.item_contacts_department_child, null);
            final ContactsGroup group = getGroup(groupPosition);
            TextView tv_content = ViewHolder.get(convertView, R.id.tv_mydept_content);
            if (null != group.getDepartments() && !group.getDepartments().isEmpty()) {
                Department department = group.getDepartments().get(childPosition);
                if (null != department) {

                    String departmentName = department.getName();
                    //部门下的人员数量
//                    int userSize = Common.getAllUsersByDeptId(department.getId(), new ArrayList<User>()).size();
                    String members = "(" + department.userNum + "人)";
                    departmentName = departmentName.concat(members);
                    tv_content.setText(departmentName);

                }
            }
            if (childPosition == getChildrenCount(groupPosition) - 1)
                ViewHolder.get(convertView, R.id.line).setVisibility(View.GONE);
            else
                ViewHolder.get(convertView, R.id.line).setVisibility(View.VISIBLE);

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

    }
}
