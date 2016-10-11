package com.loyo.oa.v2.activityui.contact.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.contact.ContactInfoActivity_;
import com.loyo.oa.v2.activityui.contact.ContactsDepartmentActivity_;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.customview.MyLetterListView;
import com.loyo.oa.v2.db.OrganizationManager;
import com.loyo.oa.v2.db.bean.DBDepartment;
import com.loyo.oa.v2.db.bean.DBUser;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.ViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * com.loyo.oa.v2.ui.fragment
 * 描述 :【公司的部门】   列表页
 * 作者 : ykb
 * 时间 : 15/8/24.
 *
 * Update by ethangong 2016/08/04 重构
 *
 */

public class ContactsDepartmentFragment extends BaseFragment {

    /* View */
    private View view;
    private ExpandableListView expandableListView_user;
    private MyLetterListView letterView;

    /* Adaptor */
    private ContactsDepartmentExpandableListAdapter userGroupExpandableListAdapter;

    /* Data */
    ArrayList<HashMap<String, Object>> datasource;

    /* Helper */
    public DBDepartmentPinyinComparator pinyinComparator;

    /* Broadcasr */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            //Bundle b = intent.getExtras();
            if ( "com.loyo.oa.v2.USER_EDITED".equals( intent.getAction() )) {
                //String userId = b.getString("userId");
                userGroupExpandableListAdapter.notifyDataSetChanged();
            }
            else  if ( "com.loyo.oa.v2.ORGANIZATION_UPDATED".equals( intent.getAction() )){
                loadData();
                userGroupExpandableListAdapter.datasource = datasource;
                userGroupExpandableListAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBroadcastReceiver();
        pinyinComparator = new DBDepartmentPinyinComparator();
        loadData();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterBroadcastReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_user, container, false);
            setupView();
            setupAdapterAndListener();
        }

        return view;
    }

    public void setupView() {
        letterView = (MyLetterListView) view.findViewById(R.id.letter_View);

        expandableListView_user = (ExpandableListView) view.findViewById(R.id.expandableListView_user);
        expandableListView_user.setDivider(null);
        expandableListView_user.setGroupIndicator(null);
    }

    public void loadData() {
        OrganizationManager orgManager = OrganizationManager.shareManager();
        DBDepartment company = orgManager.getsComany();

        List<DBDepartment> topDepartments = orgManager.subDepartmentsOfDepartment(company);
        List<DBUser> directUsers = orgManager.directUsersOfDepartment(company);
        List<DBDepartment> currentUserTopDepartments = orgManager.currentUserTopDepartments();

        Collections.sort(topDepartments, pinyinComparator);

        ArrayList<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
        Iterator<DBDepartment> deptsIterator = topDepartments.iterator();
        String previousKey = null;
        HashMap<String, Object> previousMap = null;
        ArrayList<DBDepartment> currentUserLevel1Depts = new ArrayList<DBDepartment>();

        while (deptsIterator.hasNext()) {
            DBDepartment dept = deptsIterator.next();
            Boolean isCurrentUserDept = false;
            for (int i = 0; i < currentUserTopDepartments.size(); i++) {
                DBDepartment theDept = currentUserTopDepartments.get(i);
                if (theDept.id.equals(dept.id)) {
                    currentUserLevel1Depts.add(dept);
                    isCurrentUserDept = true;
                    break;
                }
            }
            if (isCurrentUserDept) {
                continue;
            }

            String key = dept.getSortLetter();
            if (key.equals(previousKey)) {
                ArrayList<DBDepartment> items = (ArrayList<DBDepartment>)previousMap.get("items");
                items.add(dept);
                previousMap.put("items", items);
            }
            else {
                previousKey = key;
                previousMap = new HashMap<String, Object>();
                previousMap.put("name", key);
                ArrayList<DBDepartment> items = new ArrayList<DBDepartment>();
                items.add(dept);
                previousMap.put("items", items);
                result.add(previousMap);
            }
        }

        if (currentUserLevel1Depts.size() > 0) {
            HashMap<String, Object> currentUserMap = new HashMap<String, Object>();
            currentUserMap.put("name", "我");
            currentUserMap.put("items", currentUserLevel1Depts);
            result.add(0, currentUserMap);
        }

        if (directUsers.size() > 0) {
            HashMap<String, Object> level1Map = new HashMap<String, Object>();
            level1Map.put("name", "人员");
            level1Map.put("items", directUsers);
            result.add(level1Map);
        }

        datasource = result;

    }

    public void setupAdapterAndListener() {
        letterView.setOnTouchingLetterChangedListener(new MyLetterListView.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(int selectionIndex, String sectionLetter, int state) {
                int position = userGroupExpandableListAdapter.getNearestPositionForSectionLetter(sectionLetter);
                if (position!= -1) {
                    expandableListView_user.setSelectedGroup(position);
                }
            }
        });
        userGroupExpandableListAdapter = new ContactsDepartmentExpandableListAdapter(datasource);
        expandableListView_user.setAdapter(userGroupExpandableListAdapter);

        expandableListView_user.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                HashMap<String, Object> group = datasource.get(groupPosition);
                ArrayList<DBDepartment> items = (ArrayList<DBDepartment>)group.get("items");
                Object item = items.get(childPosition);
                if (item.getClass()==DBDepartment.class) {
                    DBDepartment dept = (DBDepartment)item;
                    Bundle b = new Bundle();
                    b.putString("depId", dept.id!=null?dept.id:"");
                    b.putString("depName", dept.name!=null?dept.name:"");
                    b.putString("xpath", dept.xpath!=null?dept.xpath:"");
                    app.startActivity(getActivity(), ContactsDepartmentActivity_.class, MainApp.ENTER_TYPE_RIGHT, false, b);
                }
                else if (item.getClass()==DBUser.class) {
                    DBUser user = (DBUser) item;
                    Bundle b = new Bundle();
                    b.putSerializable("userId", user.id!=null?user.id:"");
                    app.startActivity(getActivity(), ContactInfoActivity_.class, MainApp.ENTER_TYPE_RIGHT, false, b);
                }
                return true;
            }
        });
        expandableListView_user.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });

        for (int i = 0; i < datasource.size(); i++) {
            expandableListView_user.expandGroup(i);
        }

    }

    public void registerBroadcastReceiver(){
        IntentFilter filter = new IntentFilter("com.loyo.oa.v2.USER_EDITED");
        filter.addAction("com.loyo.oa.v2.ORGANIZATION_UPDATED");
        getContext().registerReceiver(mReceiver, filter);
    }

    public void unregisterBroadcastReceiver() {
        getContext().unregisterReceiver(mReceiver);
    }


    /**
     * inner class
     */

    // 部门适配器
    private class ContactsDepartmentExpandableListAdapter extends BaseExpandableListAdapter {

        LayoutInflater layoutInflater;
        ArrayList<HashMap<String, Object>> datasource;
        private int defaultAvatar;


        public ContactsDepartmentExpandableListAdapter(ArrayList<HashMap<String, Object>> d) {
            layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            datasource = d;
        }

        @Override
        public int getGroupCount() {
            if(datasource == null) return 0;

            return datasource.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            HashMap<String, Object> group = datasource.get(groupPosition);
            ArrayList<DBDepartment> items = (ArrayList<DBDepartment>)group.get("items");
            return items.size();
        }

        @Override
        public HashMap<String, Object> getGroup(int groupPosition) {
            return datasource.get(groupPosition);
        }

        @Override
        public DBDepartment getChild(int groupPosition, int childPosition) {
            HashMap<String, Object> group = datasource.get(groupPosition);
            ArrayList<DBDepartment> items = (ArrayList<DBDepartment>)group.get("items");
            return items.get(childPosition);
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

            HashMap<String, Object> group = datasource.get(groupPosition);
            String groupName = (String)group.get("name");

            if (convertView == null)
                convertView = layoutInflater.inflate(R.layout.item_contact_section, null);

            TextView title = ViewHolder.get(convertView, R.id.section_title);
            title.setText(groupName);

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            HashMap<String, Object> group = datasource.get(groupPosition);
            ArrayList<DBDepartment> items = (ArrayList<DBDepartment>)group.get("items");
            Object item = items.get(childPosition);

            if (item.getClass() == DBDepartment.class) {

                DBDepartment dept = (DBDepartment) item;
                DepartmentViewHolder holder = null;
                if (convertView == null || convertView.getTag().getClass()!= DepartmentViewHolder.class) {
                    holder = new DepartmentViewHolder();
                    convertView = layoutInflater.inflate(R.layout.item_contacts_department_child, null);
                    holder.tv_content = (TextView) convertView.findViewById(R.id.tv_mydept_content);
                    convertView.setTag(holder);
                }
                else {
                    holder = (DepartmentViewHolder)convertView.getTag();
                }

                holder.tv_content.setText(dept.name + " ( "+ dept.userNum + "人 ) ");
            }
            else if (item.getClass() == DBUser.class) {
                DBUser user = (DBUser) item;
                UserViewHolder holder = null;
                if (convertView == null || convertView.getTag().getClass()!= UserViewHolder.class) {
                    holder = new UserViewHolder();
                    convertView = layoutInflater.inflate(R.layout.item_contact_user, null);
                    holder.userName = (TextView) convertView.findViewById(R.id.user_name);
                    holder.dept = (TextView) convertView.findViewById(R.id.user_dept);
                    holder.avatarImage = (ImageView) convertView.findViewById(R.id.avatar_view);
                    convertView.setTag(holder);
                }
                else {
                    holder = (UserViewHolder)convertView.getTag();
                }

                holder.userName.setText(user.name);
                holder.dept.setText(user.shortDeptNames);
                if(null == user.avatar || user.avatar.isEmpty() || !user.avatar.contains("http")){
                    if (user.gender == 2) {
                        defaultAvatar = R.drawable.icon_contact_avatar;
                    } else {
                        defaultAvatar = R.drawable.img_default_user;
                    }
                    holder.avatarImage.setImageResource(defaultAvatar);
                }else{
                    ImageLoader.getInstance().displayImage(user.avatar, holder.avatarImage);
                }

            }



            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        public int getNearestPositionForSectionLetter(String letter) {

            if (letter == null)
                return -1;
            for(int i = 0; i < datasource.size(); i++) {
                HashMap<String, Object> group = datasource.get(i);
                String groupName = (String)group.get("name");
                if(groupName.equals(letter)) {
                    return i;
                }
            }
            return -1;

        }

    }

    static final class UserViewHolder {
        TextView dept;
        TextView userName;
        ImageView avatarImage;
    }

    static final class DepartmentViewHolder {
        TextView tv_content;
    }

    static final class DBDepartmentPinyinComparator implements Comparator<DBDepartment> {

        public int compare(DBDepartment o1, DBDepartment o2) {
            if ("@".equals(o1.getSortLetter())
                    || "#".equals(o2.getSortLetter())) {
                return -1;
            } else if ("#".equals(o1.getSortLetter())
                    || "@".equals(o2.getSortLetter())) {
                return 1;
            } else {
                return o1.pinyin().compareTo(o2.pinyin());
            }
        }
    }
}