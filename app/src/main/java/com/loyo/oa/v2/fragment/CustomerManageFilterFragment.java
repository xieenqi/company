package com.loyo.oa.v2.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Item_info_Group;
import com.loyo.oa.v2.beans.Tag;
import com.loyo.oa.v2.beans.TagItem;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.point.ITag;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.customview.ExpandableListView_inScrollView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.client.Response;

public class CustomerManageFilterFragment extends Fragment {

    View view;
    ListView lv_subordinates;
    ExpandableListView_inScrollView expand_listview_label;
    Button confirmBtn;

    long mCustomerId;

    ArrayList<TagItem> mTagItems;
    ArrayList<Tag> tags = new ArrayList<>();

    SparseArray<CheckBox> map_last_checked = new SparseArray<>();
    SparseArray<SparseBooleanArray> map_cb = new SparseArray<>();


    public static String MSG_CUSTOMER_FILTER = "com.loyo.oa.v2.customer.filter";
    public static String MSG_CUSTOMER_TAGS_FILTER = "com.loyo.oa.v2.customer.tags.filter";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_signin_manager_filter, container, false);

            if (Common.getSubUsers().size() > 0) {
                lv_subordinates = (ListView) view.findViewById(R.id.lv_subordinates);

                final List<Map<String, String>> userList = new ArrayList<>();

                for (User user : Common.getSubUsers()) {
                    Map<String, String> item = new HashMap<>();
                    item.put("name", user.getRealname());
                    item.put("id", String.valueOf(user.getId()));
                    userList.add(item);
                }

                SimpleAdapter subAdapter = new SimpleAdapter(getActivity(), userList,
                        R.layout.item_search_subordinates_listview, new String[]{"name"}, new int[]{R.id.tv_username});

                lv_subordinates.setAdapter(subAdapter);
                lv_subordinates.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Map<String, String> user = userList.get(position);

                        String userId = user.get("id");
                        if (!StringUtil.isEmpty(userId)) {
                            Intent intent = new Intent();
                            intent.setAction(MSG_CUSTOMER_FILTER);
                            intent.putExtra("name", userId);
                            LocalBroadcastManager.getInstance(view.getContext()).sendBroadcastSync(intent);
                        }
                    }
                });
                lv_subordinates.setFocusable(false);
            }
            init();
            //重要,这句是保证事件不透传到底层视图
            view.findViewById(R.id.layout_body).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            handler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    ((ScrollView) view.findViewById(R.id.sv_main)).scrollTo(0, 0);
                }
            }, 500);
        }
        view.findViewById(R.id.layout_task_filter).setVisibility(View.VISIBLE);
        confirmBtn=(Button)view.findViewById(R.id.btn_customer_filter_confirm);
        confirmBtn.setVisibility(View.VISIBLE);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                checkItems();
            }
        });

        return view;
    }

    void init() {
        if (mTagItems == null) {
            mTagItems = new ArrayList<>();
        }

        expand_listview_label=(ExpandableListView_inScrollView)view.findViewById(R.id.expand_listview_label);
        expand_listview_label.setAdapter(adapter);


        expand_listview_label.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });

        expand_listview_label.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                TagItem tagItem=(TagItem)adapter.getChild(groupPosition,childPosition);
                if(tagItem.isChecked()){
                    tagItem.setIsChecked(false);
                    mTagItems.remove(tagItem);
                }else {
                    tagItem.setIsChecked(true);
                    mTagItems.add(tagItem);
                    Tag tag = (Tag) adapter.getGroup(groupPosition);
                    for (TagItem item : tag.getItems()) {
                        if(item.getId()!=tagItem.getId()) {
                            item.setIsChecked(false);
                            mTagItems.remove(item);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
//                expand();
//                CheckBox cb = (CheckBox) v.findViewById(R.id.cBox);
//                cb.toggle();
//
//                //设置check状态
//                map_cb.get(groupPosition).put(childPosition, cb.isChecked());
//
//                CheckBox lastCheckBox = map_last_checked.get(groupPosition);
//                if (lastCheckBox != null) {
//                    lastCheckBox.setChecked(false);
//                }
//
//                map_last_checked.put(groupPosition, cb);
//
//                //在items中删除同一group的TagItem
//                Tag tag = (Tag) adapter.getGroup(groupPosition);
//                for (TagItem i : tag.getItems()) {
//                    boolean find = false;
//                    for (TagItem selectedItem : mTagItems) {
//                        if (i.getId() == selectedItem.getId()) {
//                            mTagItems.remove(selectedItem);
//                            find = true;
//                            break;
//                        }
//                    }
//
//                    if (find) break;
//                }
//
//                TagItem i = (TagItem) adapter.getChild(groupPosition, childPosition);
//                if (cb.isChecked()) {
//                    mTagItems.add(i);
//                }

                return false;
            }
        });
        expand_listview_label.setFocusable(false);

        MainApp.getMainApp().getRestAdapter().create(ITag.class).GetTags(new RCallback<ArrayList<Tag>>()
        {
            @Override
            public void success(ArrayList<Tag> _tags, Response response)
            {
                tags = _tags;

//                for (int i = 0; i < tags.size(); i++) {
//                    SparseBooleanArray mapChild = new SparseBooleanArray();
//                    for (int j = 0; j < tags.get(i).getItems().size(); j++) {
//                        mapChild.put(j, false);
//                    }
//                    map_cb.put(i, mapChild);
//                }

                handler.sendEmptyMessage(0);
            }
        });
    }

    LabelsHandler handler = new LabelsHandler();

    class LabelsHandler extends BaseActivity.BaseHandler
    {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            for(Tag tag:tags){
                ArrayList<TagItem> items=tag.getItems();
                for(TagItem item:items){
                    for(TagItem _item:mTagItems){
                        if(item.getId()==_item.getId()){
                            item.setIsChecked(true);
                            break;
                        }
                    }
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 检查需要发送的标签
     */
    void checkItems() {
        StringBuffer sbItemId = null;
        String tagItemIds = "";

        for (TagItem item : mTagItems) {
            if (sbItemId == null) {
                sbItemId = new StringBuffer();
                sbItemId.append(String.valueOf(item.getId()));
            } else {
                sbItemId.append(",");
                sbItemId.append(String.valueOf(item.getId()));
            }
        }

        if (sbItemId != null) {
            tagItemIds = sbItemId.toString();
        }

        Intent intent = new Intent();
        intent.setAction(MSG_CUSTOMER_TAGS_FILTER);
        intent.putExtra("name", tagItemIds.toString());
        LocalBroadcastManager.getInstance(view.getContext()).sendBroadcastSync(intent);
    }

    void expand() {
        for (int i = 0; i < tags.size(); i++) {
            expand_listview_label.expandGroup(i, false);
        }
    }


    BaseExpandableListAdapter adapter = new BaseExpandableListAdapter() {
        @Override
        public int getGroupCount() {
            return tags.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return tags.get(groupPosition).getItems().size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return tags.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return tags.get(groupPosition).getItems().get(childPosition);
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

        Item_info_Group item_info_Group;
        Item_info_Child item_info_Child;

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.item_sign_show_group, null);
                item_info_Group = new Item_info_Group();
                item_info_Group.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                convertView.setTag(item_info_Group);
            } else {
                item_info_Group = (Item_info_Group) convertView.getTag();
            }

            Tag tag = tags.get(groupPosition);
            if (tag != null && tag.getName() != null) {
                item_info_Group.tv_title.setText(tag.getName());
            }

            return convertView;
        }

        @Override
        public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.item_customer_tag_big, null);
                item_info_Child = new Item_info_Child();
                item_info_Child.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                item_info_Child.cb = (CheckBox) convertView.findViewById(R.id.cBox);
                convertView.setTag(item_info_Child);
            } else {
                item_info_Child = (Item_info_Child) convertView.getTag();
            }

            TagItem item = (TagItem) getChild(groupPosition, childPosition);
            item_info_Child.tv_title.setText(item.getName());
            item_info_Child.cb.setChecked(item.isChecked());
//            item_info_Child.cb.setChecked(map_cb.get(groupPosition).get(childPosition));

            //回显标签
//            if (mTagItems != null) {
//                for (TagItem i : mTagItems) {
//                    if (i.getId() == item.getId()) {
//                        item_info_Child.cb.setChecked(true);
//
//                        map_last_checked.put(groupPosition, item_info_Child.cb);
//                    }
//                }
//            }

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        @Override
        public void notifyDataSetChanged()
        {
            super.notifyDataSetChanged();
            expand();
        }
    };

    class Item_info_Child {
        TextView tv_title;
        CheckBox cb;
    }
}
