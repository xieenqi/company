package com.loyo.oa.v2.activityui.customer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.event.CustomerLabelRushEvent;
import com.loyo.oa.v2.activityui.customer.model.Contact;
import com.loyo.oa.v2.activityui.customer.model.Item_info_Group;
import com.loyo.oa.v2.activityui.customer.model.NewTag;
import com.loyo.oa.v2.activityui.other.model.Tag;
import com.loyo.oa.v2.activityui.customer.model.TagItem;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.event.AppBus;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.ArrayList;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 客户标签选择【标签】
 */
public class CustomerLabelCopyActivity extends BaseActivity implements View.OnClickListener{

    private ViewGroup img_title_right;
    private ExpandableListView expand_listview_label;
    private ViewGroup img_title_left;
    private ArrayList<TagItem> mTagItems;
    private String mCustomerId;
    private ArrayList<Tag> tags = new ArrayList<>();
    private boolean canEdit;
    private int fromPage; /*0:详情 1:信息*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_label);
        init();
    }


    void init() {

        mTagItems = (ArrayList<TagItem>) getIntent().getSerializableExtra("tagitems");
        mCustomerId = getIntent().getStringExtra("customerId");
        canEdit = getIntent().getBooleanExtra("canEdit",false);
        fromPage = getIntent().getIntExtra("fromPage",0);

        img_title_right = (ViewGroup) findViewById(R.id.img_title_right);
        img_title_left = (ViewGroup) findViewById(R.id.img_title_left);
        expand_listview_label = (ExpandableListView) findViewById(R.id.expand_listview_label);

        img_title_right.setOnClickListener(this);
        img_title_left.setOnClickListener(this);

        if (null == mTagItems) {
            mTagItems = new ArrayList<>();
        }

        if (!canEdit) {
            img_title_right.setVisibility(View.GONE);
        }

        setTitle("标签");

        expand_listview_label.setAdapter(adapter);
        expand_listview_label.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(final ExpandableListView parent, final View v, final int groupPosition, final long id) {
                return true;
            }
        });

        if (canEdit) {
            expand_listview_label.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(final ExpandableListView parent, final View v, final int groupPosition, final int childPosition, final long id) {
                    TagItem tagItem = (TagItem) adapter.getChild(groupPosition, childPosition);
                    if (tagItem.isChecked()) {
                        tagItem.setIsChecked(false);
                        for (int i = 0; i < mTagItems.size(); i++) {
                            TagItem cacheItem = mTagItems.get(i);
                            if (TextUtils.equals(cacheItem.getId(), tagItem.getId())) {
                                mTagItems.remove(cacheItem);
                                i--;
                            }
                        }
                    } else {
                        Tag tag = (Tag) adapter.getGroup(groupPosition);
                        tagItem.setIsChecked(true);
                        tagItem.setTagId(tag.getId());
                        mTagItems.add(tagItem);
                        for (TagItem item : tag.getItems()) {
                            if (!TextUtils.equals(item.getId(), tagItem.getId())) {
                                item.setIsChecked(false);
                                //修复标签多选bug
                                for (int i = 0; i < mTagItems.size(); i++) {
                                    TagItem cacheItem = mTagItems.get(i);
                                    if (TextUtils.equals(cacheItem.getId(), item.getId())) {
                                        mTagItems.remove(cacheItem);
                                        i--;
                                    }
                                }
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                    expand();
                    return false;
                }
            });
        }

        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).GetTags(new RCallback<ArrayList<Tag>>() {
            @Override
            public void success(final ArrayList<Tag> _tags, final Response response) {
                tags = _tags;
                handler.sendEmptyMessage(0);
            }
        });
    }

    void expand() {
        for (int i = 0; i < tags.size(); i++) {
            expand_listview_label.expandGroup(i, false);
        }
    }

    LabelsHandler handler = new LabelsHandler();

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            /*返回*/
            case R.id.img_title_left:
                app.finishActivity((Activity) mContext, MainApp.ENTER_TYPE_LEFT, RESULT_CANCELED, null);
                break;

            /*确认*/
            case R.id.img_title_right:
                setPageResult();
                break;
        }



    }

    class LabelsHandler extends BaseHandler {

        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            for (Tag tag : tags) {
                ArrayList<TagItem> items = tag.getItems();
                for (TagItem item : items) {
                    item.setTagId(tag.getId());
                    for (TagItem _item : mTagItems) {
                        if (TextUtils.equals(item.getId(), _item.getId())) {
                            item.setIsChecked(true);
                            _item.setTagId(item.getTagId());
                        }
                    }
                }
            }
            adapter.notifyDataSetChanged();
            expand();
        }
    }

    /**
     * 设置标签
     * */
    private void setLabel(){
        showLoading("");
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).setCusLabel(mCustomerId, convertNewTags(), new RCallback<Contact>() {
            @Override
            public void success(final Contact contact, final Response response) {
                HttpErrorCheck.checkResponse(response);
                finish();
            }

            @Override
            public void failure(final RetrofitError error) {
               HttpErrorCheck.checkError(error);
            }
        });
    }

    /**
     * 构建新标签
     *
     * @return
     */
    private ArrayList<NewTag> convertNewTags() {
        if (null == mTagItems || mTagItems.isEmpty()) {
            return new ArrayList<>();
        }
        ArrayList<NewTag> tags = new ArrayList<>();
        for (int i = 0; i < mTagItems.size(); i++) {
            TagItem item = mTagItems.get(i);
            NewTag tag = new NewTag();
            tag.setItemId(item.getId());
            tag.setItemName(item.getName());
            tag.settId(item.getTagId());

            tags.add(tag);
        }

        return tags;
    }

    /**
     * 设置不同业务的处理方式
     * */
    void setPageResult() {
        /*客户详情*/
        if(fromPage == 0){
            setLabel();
        }
        /*客户信息*/
        else{
            CustomerLabelRushEvent event = new CustomerLabelRushEvent();
            event.bundle = new Bundle();
            event.bundle.putSerializable("data",convertNewTags());
            AppBus.getInstance().post(event);
            finish();
        }
    }

    BaseExpandableListAdapter adapter = new BaseExpandableListAdapter() {
        @Override
        public int getGroupCount() {
            return tags.size();
        }

        @Override
        public int getChildrenCount(final int groupPosition) {
            return tags.get(groupPosition).getItems().size();
        }

        @Override
        public Object getGroup(final int groupPosition) {
            return tags.get(groupPosition);
        }

        @Override
        public Object getChild(final int groupPosition, final int childPosition) {
            return tags.get(groupPosition).getItems().get(childPosition);
        }

        @Override
        public long getGroupId(final int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(final int groupPosition, final int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        Item_info_Group item_info_Group;
        Item_info_Child item_info_Child;

        @Override
        public View getGroupView(final int groupPosition, final boolean isExpanded, View convertView, final ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_sign_show_group, null);
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
        public View getChildView(final int groupPosition, final int childPosition, final boolean isLastChild, View convertView, final ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_customer_tag, null);
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
            convertView.setEnabled(canEdit);
            return convertView;
        }

        @Override
        public boolean isChildSelectable(final int groupPosition, final int childPosition) {
            return true;
        }
    };

    class Item_info_Child {
        TextView tv_title;
        CheckBox cb;
    }
}
