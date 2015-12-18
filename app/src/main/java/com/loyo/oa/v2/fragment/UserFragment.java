package com.loyo.oa.v2.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.DepartmentUserActivity;
import com.loyo.oa.v2.adapter.UserGroupExpandableListAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.beans.UserGroupData;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.customview.MyLetterListView;

import java.util.ArrayList;

/**
 * 员工通讯录Fragment
 * */

@SuppressLint("ValidFragment")
public class UserFragment extends BaseFragment {

    DepartmentUserActivity departmentUserActivity;
    View view;

    ExpandableListView expandableListView_user;
    UserGroupExpandableListAdapter userGroupExpandableListAdapter;
    ArrayList<UserGroupData> lstUserGroupData;

    MyLetterListView letterView;
    AlphabetIndexer index;

    ViewGroup layout_toast;
    TextView tv_toast;

    String superDeptId;
    String mIndex;

    ArrayList<Integer> mUserIds;

    public UserFragment(String _deptId) {
        superDeptId = _deptId;
    }


    public UserFragment(String _deptId,ArrayList<Integer> userIds) {
        superDeptId = _deptId;
        mUserIds=userIds;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.lstUserGroupData = Common.getLstUserGroupData();
        departmentUserActivity = (DepartmentUserActivity) getActivity();

        StringBuffer sb = null;
//        boolean isAddAllmate = false;

        for (int i = 0; i < lstUserGroupData.size(); i++) {
            if (sb == null) {
                sb = new StringBuffer();
            }

            UserGroupData ugd = lstUserGroupData.get(i);
            sb.append(ugd.getGroupName().toUpperCase());

            //在最上面增加全体成员
//            if (StringUtil.isEmpty(ugd.getGroupName())
//                    && departmentUserActivity.select_type == DepartmentUserActivity.TYPE_SELECT_MULTUI){
//                isAddAllmate = true;
//                addAllUserMate(ugd);
//            }
        }

        if (sb == null) {
            mIndex = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        } else {
            mIndex = "#".concat(sb.toString());
        }

//        if (!isAddAllmate && departmentUserActivity.select_type == DepartmentUserActivity.TYPE_SELECT_MULTUI) {
//            UserGroupData ugd = new UserGroupData();
//            addAllUserMate(ugd);
//            lstUserGroupData.add(0,ugd);
//        }
    }

//    void addAllUserMate(UserGroupData ugd) {
//        boolean isHas = false;
//        for (User u : ugd.getLstUser()){
//            isHas = (u.getId() == -1);
//
//            if (isHas) break;
//        }
//
//        if (!isHas){
//            ugd.getLstUser().add(0,new User(-1,"全体成员"));
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_user, container, false);

            layout_toast = (ViewGroup) view.findViewById(R.id.layout_toast);
            tv_toast = (TextView) view.findViewById(R.id.tv_toast);

            IndexCursor cursor = new IndexCursor(lstUserGroupData);
            index = new AlphabetIndexer(cursor, 0, mIndex);

            letterView = (MyLetterListView) view.findViewById(R.id.letter_View);
            letterView.setKeyword(mIndex);
            letterView.setOnTouchingLetterChangedListener(new MyLetterListView.OnTouchingLetterChangedListener() {

                @Override
                public void onTouchingLetterChanged(int selectionIndex, String sectionLetter, int state) {
                    int position = index.getPositionForSection(selectionIndex);

                    switch (state) {
                        case MyLetterListView.FINGER_ACTION_DOWN: // 手指按下
                            layout_toast.setVisibility(View.VISIBLE);
                            tv_toast.setText(sectionLetter);
                            scroll(position);
                            break;
                        case MyLetterListView.FINGER_ACTION_MOVE: // 手指滑动
                            tv_toast.setText(sectionLetter);
                            scroll(position);
                            break;
                        case MyLetterListView.FINGER_ACTION_UP:
                            layout_toast.setVisibility(View.GONE);// 手指离开
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
        for (UserGroupData d : lstUserGroupData) {
            for (int i = 0; i < d.getLstUser().size(); i++) {
                if (count == position) {
                    expandableListView_user.setSelectedGroup(groupPosition);
                }
                count++;
            }
            groupPosition++;
        }
    }

    class IndexCursor implements Cursor {

        ArrayList<UserGroupData> data;

        public IndexCursor(ArrayList<UserGroupData> _data) {
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
            for (UserGroupData d : lstUserGroupData) {
                for (User u : d.getLstUser()) {
                    if (count == position) {

                        if (u.fullPinyin != null && u.fullPinyin.length() > 0) {
                            return u.fullPinyin.substring(0, 1).toUpperCase();
                        } else if (u.simplePinyin != null && u.simplePinyin.length() > 0) {
                            return u.simplePinyin.substring(0, 1).toUpperCase();
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
            for (UserGroupData d : lstUserGroupData) {
                count += d.getLstUser().size();
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
        if (lstUserGroupData == null) {
            return;
        }

        expandableListView_user = (ExpandableListView) view.findViewById(R.id.expandableListView_user);
        userGroupExpandableListAdapter = new UserGroupExpandableListAdapter(departmentUserActivity, lstUserGroupData);
        expandableListView_user.setAdapter(userGroupExpandableListAdapter);
        expandableListView_user.setGroupIndicator(null);
        expandableListView_user.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                UserGroupExpandableListAdapter.Item_info item_info = (UserGroupExpandableListAdapter.Item_info) v.getTag();
                //在每次获取点击的item时将对于的checkbox状态改变，同时修改map的值。
                item_info.cBox.toggle();

                switch (departmentUserActivity.select_type) {
                    //单选
                    case DepartmentUserActivity.TYPE_SELECT_SINGLE:

                        Intent data = new Intent();
                        data.putExtra(User.class.getName(), (User) userGroupExpandableListAdapter.getChild(groupPosition, childPosition));
                        app.finishActivity((Activity) v.getContext(), MainApp.ENTER_TYPE_LEFT, Activity.RESULT_OK, data);
                        break;
                    //多选
                    case DepartmentUserActivity.TYPE_SELECT_MULTUI:
                        if (item_info.cBox.isChecked()) {
                            userGroupExpandableListAdapter.isSelected_radio_group = groupPosition;
                            userGroupExpandableListAdapter.isSelected_radio_child = childPosition;
                        } else {
                            userGroupExpandableListAdapter.isSelected_radio_group = -1;
                            userGroupExpandableListAdapter.isSelected_radio_child = -1;
                        }

                        User user = (User) userGroupExpandableListAdapter.getChild(groupPosition, childPosition);
                        if (user == null) {
                            return true;
                        }

//                        if (user.getId() == -1) {
                        if ("-1".equals(user.id)) {
                            //选择全体人员,先删除已经选择人员,再全选
                            DepartmentUserActivity.sendCleanUsers(v.getContext());

                        } else {
                            DepartmentUserActivity.sendMultiSelectUsers(v.getContext(), user.id, user.getRealname(), "", null, item_info.cBox.isChecked());
                        }

                        break;
                }
                return true;
            }

        });

        expandableListView_user.setItemsCanFocus(false);
        expandableListView_user.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        for (int i = 0; i < lstUserGroupData.size(); i++) {
            expandableListView_user.expandGroup(i);
        }

        expandableListView_user.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });
    }
}
