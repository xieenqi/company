package com.loyo.oa.v2.activityui.contact.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.contact.ContactInfoActivity_;
import com.loyo.oa.v2.activityui.contact.ContactsDepartmentActivity_;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.activityui.customer.bean.Department;
import com.loyo.oa.v2.activityui.other.bean.User;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.db.OrganizationManager;
import com.loyo.oa.v2.db.bean.DBDepartment;
import com.loyo.oa.v2.db.bean.DBUser;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.tool.ViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

/**
 * com.loyo.oa.v2.ui.fragment
 * 描述 :部门里面成员和下属部门展示页
 * 作者 : ykb
 * 时间 : 15/9/6.
 */
public class ContactsSubdivisionsFragment extends BaseFragment {

    /* View */
    private View view;
    private ExpandableListView listView;

    /* Adaptor */
    private SubDepartmentChildListAdapter listAdapter;

    /* Data */
    ArrayList<HashMap<String, Object>> datasource;
    private String deptId;
    private String xpath;

    /* Helper */
    private MainApp app = MainApp.getMainApp();
    public PinyinComparator pinyinComparator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pinyinComparator = new PinyinComparator();
        loadData();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_sub_department, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupView();
        setupAdapterAndListener();
    }

    public void loadData() {
        deptId = null == getArguments() ? "" : getArguments().getString("depId");
        xpath = null == getArguments() ? "" : getArguments().getString("xpath");

        ArrayList<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
        // TODO:
        List<List<Object>> children = OrganizationManager.shareManager().getChildrenOf(deptId, xpath);
        List<Object> users = children.get(0);
        List<Object> depts = children.get(1);
        Collections.sort(users, pinyinComparator);
        Collections.sort(depts, pinyinComparator);



        if (users.size() >0 ) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("name", "人员");
            map.put("items", users);
            result.add(map);
        }
        if (depts.size() >0 ) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("name", "部门");
            map.put("items", depts);
            result.add(map);
        }

        datasource = result;
    }

    public void setupView() {
        listView = (ExpandableListView) view.findViewById(R.id.list_view);
        listView.setDivider(null);
        listView.setGroupIndicator(null);
    }

    public void setupAdapterAndListener() {

        listAdapter = new SubDepartmentChildListAdapter(datasource);
        listView.setAdapter(listAdapter);

        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
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
                    app.startActivity(getActivity(), ContactsDepartmentActivity_.class, MainApp.ENTER_TYPE_RIGHT, false, b);
                }
                else if (item.getClass()==DBUser.class) {
                    DBUser user = (DBUser) item;
                    Bundle b = new Bundle();
                    String xpath = user.anyDepartmentXpath();
                    b.putSerializable("userId", user.id!=null?user.id:"");
                    b.putSerializable("xpath", xpath!=null?xpath:"");
                    app.startActivity(getActivity(), ContactInfoActivity_.class, MainApp.ENTER_TYPE_RIGHT, false, b);
                }
                return true;
            }
        });
        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });

        for (int i = 0; i < datasource.size(); i++) {
            listView.expandGroup(i);
        }

    }

    /**
     * Inner Class
     */

    private class SubDepartmentChildListAdapter extends BaseExpandableListAdapter
    {
        LayoutInflater layoutInflater;
        ArrayList<HashMap<String, Object>> datasource;
        private int defaultAvatar;


        public SubDepartmentChildListAdapter(ArrayList<HashMap<String, Object>> d) {
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

    static final class PinyinComparator implements Comparator<Object> {

        public int compare(Object o1, Object o2) {
            if (o1.getClass() == DBDepartment.class) {
                return ((DBDepartment)o1).pinyin().compareTo(((DBDepartment)o2).pinyin());
            }
            else if (o1.getClass() == DBUser.class) {
                return ((DBUser)o1).pinyin().compareTo(((DBUser)o2).pinyin());
            }
            return 0;
        }
    }
}
