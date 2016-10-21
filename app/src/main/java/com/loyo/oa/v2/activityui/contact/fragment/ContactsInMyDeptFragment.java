package com.loyo.oa.v2.activityui.contact.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.contact.ContactInfoActivity_;
import com.loyo.oa.v2.activityui.work.adapter.ContactsInMyDeptAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.CharacterParser;
import com.loyo.oa.v2.customview.SideBar;
import com.loyo.oa.v2.db.OrganizationManager;
import com.loyo.oa.v2.db.bean.DBUser;
import com.loyo.oa.v2.db.sort.UserPinyinComparator;
import com.loyo.oa.v2.tool.BaseFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * 【本部门】人员列表页
 * <p/>
 * Created by yyy on 15/12/30.
 *
 * Update by ethangong 16/08/04
 * 重构
 */

public class ContactsInMyDeptFragment extends BaseFragment {

    /* View */
    public SideBar sideBar;
    public ListView sortListView;
    public LayoutInflater mInflater;

    /* adapter */
    public ContactsInMyDeptAdapter adapter;

    /* Data */
    public ArrayList<DBUser> myUserList;
    public ArrayList<Object> datasource;

    /* Helper */
    public CharacterParser characterParser;

    /* Method */


    /* Broadcasr */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            //Bundle b = intent.getExtras();
            if ( "com.loyo.oa.v2.USER_EDITED".equals( intent.getAction() )) {
                //String userId = b.getString("userId");
                adapter.notifyDataSetChanged();
            }
            else  if ( "com.loyo.oa.v2.ORGANIZATION_UPDATED".equals( intent.getAction() )){
                loadData();
                adapter.setDatasource(datasource);
                adapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        characterParser = CharacterParser.getInstance();
        registerBroadcastReceiver();
        loadData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_users, container, false);
        setupView(view);
        setupAdapterAndListener();
        return view;
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

    public void setupView(View view) {
        mInflater = LayoutInflater.from(getActivity());
        sortListView = (ListView) view.findViewById(R.id.expandableListView_user);
        sideBar = (SideBar) view.findViewById(R.id.sidrbar);
    }

    public void loadData(){
        myUserList = (ArrayList<DBUser>) OrganizationManager.shareManager().getCurrentUserSameDeptsUsers2();
        Collections.sort(myUserList, new UserPinyinComparator());
        this.buildData();
    }

    public void registerBroadcastReceiver(){
        IntentFilter filter = new IntentFilter("com.loyo.oa.v2.USER_EDITED");
        filter.addAction("com.loyo.oa.v2.ORGANIZATION_UPDATED");
        getContext().registerReceiver(mReceiver, filter);
    }

    public void unregisterBroadcastReceiver() {
        getContext().unregisterReceiver(mReceiver);
    }

    public void buildData(){
        if (myUserList==null)
            return;

        datasource = new ArrayList<Object>();
        String previouSectionTitle = null;
        Iterator<DBUser> iterator = myUserList.iterator();
        while (iterator.hasNext()) {
            DBUser user = iterator.next();
            if(user.id!= null && user.id.equals(MainApp.user.id)) {
                // 自己放在最上边
                datasource.add(0, "我");
                datasource.add(1, user);
            }
            else{ // 其他人按字母顺序依次排列
                if (!(user.getSortLetter().equals(previouSectionTitle))) {
                    previouSectionTitle = user.getSortLetter();
                    datasource.add(previouSectionTitle);
                }
                datasource.add(user);
            }
        }
    }

    public void setupAdapterAndListener(){
        //设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                int position = adapter.getPositionForSectionTitle(s);
                if (position != -1) {
                    sortListView.setSelection(position);
                }
            }
        });

        adapter = new ContactsInMyDeptAdapter(getActivity(), datasource);
        sortListView.setAdapter(adapter);
        sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Object item = datasource.get(position);
                if (item.getClass()!= DBUser.class){
                    return;
                }
                DBUser user = (DBUser) item;
                Bundle b = new Bundle();
                b.putSerializable("userId", user.id!=null?user.id:"");
                app.startActivity(getActivity(), ContactInfoActivity_.class, MainApp.ENTER_TYPE_RIGHT, false, b);

            }
        });
    }
}