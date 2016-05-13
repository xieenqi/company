package com.loyo.oa.v2.fragment;

import android.content.ContentResolver;
import android.content.Context;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.contact.ContactInfoActivity_;
import com.loyo.oa.v2.activity.contact.ContactsDepartmentActivity_;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Department;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.ViewHolder;
import com.loyo.oa.v2.tool.customview.MyLetterListView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * com.loyo.oa.v2.fragment
 * 描述 :【本部门】人员列表页
 * 作者 : ykb
 * 时间 : 15/8/24.
 */
public class ContactsInDepartmentFragment extends BaseFragment {

    public ExpandableListView expandableListView_user;
    public ContactsExpandableListAdapter userGroupExpandableListAdapter;
    public ArrayList<CommonItem> listDatas = new ArrayList<>();
    public MyLetterListView letterView;
    public AlphabetIndexer index;
    //    public ViewGroup layout_toast;
    public TextView tv_dialog;
    public String mIndex;
    public String depId;
    public boolean isMyDept;

    //必须大于adapter的viewType
    private int itemCount = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    /**
     * 初始化数据
     */
    private int initData() {

        depId = (null == getArguments() || !getArguments().containsKey("depId")) ? "" : getArguments().getString("depId");

        if (TextUtils.isEmpty(depId) && depId.length() == 0) {
            depId = MainApp.user.depts.get(0).getShortDept().getId();
        }

        if (MainApp.user.depts.size() != 0) {
            isMyDept = TextUtils.equals(depId, MainApp.user.depts.get(0).getShortDept().getId());
        }

        List<Department> departments = isMyDept ? null : Common.getLstDepartment(depId);
        List<User> users = isMyDept ? Common.getUsersByDeptId(depId, new ArrayList<User>()) : Common.getListUser(depId);

        CommonItem tempItem = new CommonItem();
        tempItem.tag = "";

        for (char i = 'A'; i <= 'Z'; i += (char) 1) {
            CommonItem item = new CommonItem();
            item.tag = String.valueOf(i);
            if (null != departments && !departments.isEmpty()) {
                for (int j = 0; j < departments.size(); j++) {
                    Department department = departments.get(j);
                    String names = TextUtils.isEmpty(department.getFullPinyin()) ? "" : department.getFullPinyin();
                    names = TextUtils.isEmpty(names) ? department.getSimplePinyin() : names;
                    if (!TextUtils.isEmpty(names)) {
                        char name = names.toUpperCase().charAt(0);
                        if (i == name) {
                            item.datas.add(department);
                        }
                    } else if (!tempItem.datas.contains(department)) {
                        tempItem.datas.add(department);
                    }
                }
            }

            if (null != users && !users.isEmpty()) {
                if (itemCount == 2)
                    itemCount = 3;

                for (int k = 0; k < users.size(); k++) {
                    User user = users.get(k);
                    if (isMyDept && user.isCurrentUser()) continue;
                    String names = TextUtils.isEmpty(user.fullPinyin) ? "" : user.fullPinyin;
                    names = TextUtils.isEmpty(names) ? user.fullPinyin : names;
                    if (!TextUtils.isEmpty(names)) {
                        char name = names.toUpperCase().charAt(0);
                        if (i == name) {
                            item.datas.add(user);
                        }
                    } else if (!tempItem.datas.contains(user)) {
                        tempItem.datas.add(user);
                    }
                }
            }
            if (!item.datas.isEmpty()) {
                listDatas.add(item);
            }
        }

        //加载没有tag的人员
        if (!tempItem.datas.isEmpty())
            listDatas.add(0, tempItem);
        if (isMyDept) {
            //加载自己
            CommonItem itemMe = new CommonItem();
            itemMe.tag = "我";
            itemMe.datas.add(MainApp.user);
            listDatas.add(0, itemMe);
        }

        StringBuffer sb = null;
        for (int i = 0; i < listDatas.size(); i++) {
            if (sb == null) {
                sb = new StringBuffer();
            }

            CommonItem ugd = listDatas.get(i);
            sb.append(ugd.tag.toUpperCase());
        }

        if (sb == null) {
            mIndex = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        } else {
            mIndex = "#".concat(sb.toString());
        }

        return 1;
    }

    /**
     * 绑定数据到视图
     *
     * @param view
     */
    private void bindViewData(View view) {
//        layout_toast = (ViewGroup) view.findViewById(R.id.layout_toast);
        tv_dialog = (TextView) view.findViewById(R.id.tv_dialog);
        IndexCursor cursor = new IndexCursor(listDatas);
        index = new AlphabetIndexer(cursor, 0, mIndex);
        letterView = (MyLetterListView) view.findViewById(R.id.letter_View);
        letterView.setKeyword(mIndex);
        letterView.setOnTouchingLetterChangedListener(new MyLetterListView.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(int selectionIndex, String sectionLetter, int state) {
                int position = index.getPositionForSection(selectionIndex);

                switch (state) {
                    case MyLetterListView.FINGER_ACTION_DOWN: // 手指按下
                        tv_dialog.setVisibility(View.VISIBLE);
                        tv_dialog.setText(sectionLetter);
                        scroll(position);
                        break;
                    case MyLetterListView.FINGER_ACTION_MOVE: // 手指滑动
                        tv_dialog.setText(sectionLetter);
                        scroll(position);
                        break;
                    case MyLetterListView.FINGER_ACTION_UP:
                        tv_dialog.setVisibility(View.GONE);// 手指离开
                        break;
                    default:
                        break;
                }
            }
        });
        expandableListView_user = (ExpandableListView) view.findViewById(R.id.expandableListView_user);
        init_expandableListView_user();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViewData(view);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    void scroll(int position) {
        int count = 0, groupPosition = 0;
        for (CommonItem d : listDatas) {
            for (int i = 0; i < d.datas.size(); i++) {
                if (count == position) {
                    expandableListView_user.setSelectedGroup(groupPosition);
                }
                count++;
            }
            groupPosition++;
        }
    }

    class IndexCursor implements Cursor {

        ArrayList<CommonItem> data;

        public IndexCursor(ArrayList<CommonItem> _data) {
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
            for (CommonItem d : listDatas) {
                for (Object o : d.datas) {
                    if (count == position) {
                        String fullPinyin = "";
                        String simplePinyin = "";
                        if (o instanceof Department) {
                            Department d1 = (Department) o;
                            fullPinyin = d1.getFullPinyin();
                            simplePinyin = d1.getSimplePinyin();
                        } else {
                            User u = (User) o;
                            fullPinyin = u.fullPinyin;
                            simplePinyin = u.fullPinyin;
                        }
                        if (!TextUtils.isEmpty(fullPinyin)) {
                            return fullPinyin.substring(0, 1).toUpperCase();
                        } else if (!TextUtils.isEmpty(simplePinyin)) {
                            return simplePinyin.substring(0, 1).toUpperCase();
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
            for (CommonItem d : listDatas) {
                count += d.datas.size();
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

    void init_expandableListView_user() {
        if (listDatas == null) {
            return;
        }

        expandableListView_user.setDivider(null);
        userGroupExpandableListAdapter = new ContactsExpandableListAdapter();
        expandableListView_user.setAdapter(userGroupExpandableListAdapter);
        expandableListView_user.setGroupIndicator(null);
        expandableListView_user.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                final Object o = listDatas.get(groupPosition).datas.get(childPosition);
                if (o instanceof User) {
                    User user = (User) o;
                    if (null != user) {
                        Bundle b = new Bundle();
                        b.putSerializable("user", user);
                        app.startActivity(getActivity(), ContactInfoActivity_.class, MainApp.ENTER_TYPE_ZOOM_OUT, false, b);
                    }
                } else {
                    Department d = (Department) o;
                    if (null != d) {
                        Bundle b = new Bundle();
                        b.putString("depId", d.getId());
                        b.putString("depName", d.getName());
                        app.startActivity(getActivity(), ContactsDepartmentActivity_.class, MainApp.ENTER_TYPE_ZOOM_OUT, false, b);
                    }
                }
                return true;
            }
        });
        for (int i = 0; i < listDatas.size(); i++) {
            expandableListView_user.expandGroup(i);
        }

        expandableListView_user.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });
    }

    //--------------------------------------------适配器---------------------------------------------
    private class ContactsExpandableListAdapter extends BaseExpandableListAdapter {
        LayoutInflater layoutInflater;

        public ContactsExpandableListAdapter() {
            layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getGroupCount() {
            return listDatas.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return listDatas.get(groupPosition).datas.size();
        }

        @Override
        public CommonItem getGroup(int groupPosition) {
            return listDatas.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {

            return listDatas.get(groupPosition).datas.get(childPosition);
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
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.item_usergroup_group, null);
            }
            TextView title = ViewHolder.get(convertView, R.id.textView_item_titel);
            title.setText(getGroup(groupPosition).tag);

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            final Object o = getChild(groupPosition, childPosition);

            if (convertView == null) {
                if (o instanceof User) {
                    convertView = layoutInflater.inflate(R.layout.item_contacts_child, null, false);
                } else {
                    convertView = layoutInflater.inflate(R.layout.item_contacts_department_child, null, false);
                }
            }
            if (o instanceof User) {
                User user = (User) o;
                ImageView img = ViewHolder.get(convertView, R.id.img);
                TextView tv_content = ViewHolder.get(convertView, R.id.tv_name);
                TextView tv_position = ViewHolder.get(convertView, R.id.tv_position);

                tv_content.setText(user.getRealname());

                /*部门名*/
                String departmentName = ((User) getChild(groupPosition, childPosition)).departmentsName;
                /*职位*/
                String jobName = ((User) getChild(groupPosition, childPosition)).role.name;

                if (null != user.shortPosition && !TextUtils.isEmpty(user.shortPosition.getName())) {
                    departmentName = departmentName.concat(" | " + user.shortPosition.getName());
                }

                tv_position.setText(departmentName + "  " + jobName);

                if (!TextUtils.isEmpty(user.avatar)) {
                    ImageLoader.getInstance().displayImage(user.avatar, img);
                }

            } else {
                Department d = (Department) o;
                TextView tv_content = ViewHolder.get(convertView, R.id.tv_content);
                String departmentName = d.getName();
                int userSize = Common.getUsersByDeptId(d.getId(), new ArrayList<User>()).size();
                String members = "(" + userSize + "人" + ")";
                departmentName = departmentName.concat(members);
                tv_content.setText(departmentName);
            }

            if (childPosition == getChildrenCount(groupPosition) - 1) {
                ViewHolder.get(convertView, R.id.devider).setVisibility(View.GONE);
            } else {
                ViewHolder.get(convertView, R.id.devider).setVisibility(View.VISIBLE);
            }

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        @Override
        public int getChildTypeCount() {
            return itemCount;
        }

        @Override
        public int getChildType(int groupPosition, int childPosition) {
            Object o = getChild(groupPosition, childPosition);

            return o instanceof Department ? 1 : 0;
        }
    }

    public class CommonItem {
        public String tag;
        public ArrayList<Object> datas = new ArrayList<>();
    }
}
