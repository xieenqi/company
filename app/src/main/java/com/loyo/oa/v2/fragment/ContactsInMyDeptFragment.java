package com.loyo.oa.v2.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.contact.ContactInfoActivity_;
import com.loyo.oa.v2.adapter.ContactsInMyDeptAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.beans.UserInfo;
import com.loyo.oa.v2.common.CharacterParser;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.common.PinyinComparator;
import com.loyo.oa.v2.common.SideBar;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Collections;

/**
 * 【本部门】人员列表页
 * <p/>
 * Created by yyy on 15/12/30.
 */
public class ContactsInMyDeptFragment extends BaseFragment {

    public SideBar sideBar;
    public ListView sortListView;
    public ContactsInMyDeptAdapter adapter;
    public CharacterParser characterParser;
    public PinyinComparator pinyinComparator;
    public LayoutInflater mInflater;
    public int defaultAvatar;

    public ArrayList<User> myUserList;
    public ArrayList<UserInfo> myDepts;
    public View view;
    public View headView;
    public ImageView heading;
    public TextView nameTv;
    public TextView deptInfoTv;
    public TextView catalogTv;
    public TextView tv_dialog;
    public LinearLayout item_medleft_top;
    public StringBuffer myDeptBuffer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_user_users, container, false);
        initView(view);
        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        /*及时刷新头像*/
        heading = (ImageView) headView.findViewById(R.id.img);
        if (null == MainApp.user.avatar || MainApp.user.avatar.isEmpty() || !MainApp.user.avatar.contains("http")) {
            if (MainApp.user.gender == 2) {
                defaultAvatar = R.drawable.icon_contact_avatar;
            } else {
                defaultAvatar = R.drawable.img_default_user;
            }
            heading.setImageResource(defaultAvatar);
        } else {
            ImageLoader.getInstance().displayImage(MainApp.user.getAvatar(), heading);
        }
    }

    public void initView(View view) {

        myDepts = MainApp.user.depts;
        myDeptBuffer = new StringBuffer();
        sortListView = (ListView) view.findViewById(R.id.expandableListView_user);
        sideBar = (SideBar) view.findViewById(R.id.sidrbar);
        mInflater = LayoutInflater.from(getActivity());
        tv_dialog = (TextView) view.findViewById(R.id.tv_dialog);
        headView = mInflater.inflate(R.layout.item_medleft, null);
        nameTv = (TextView) headView.findViewById(R.id.tv_name);
        deptInfoTv = (TextView) headView.findViewById(R.id.tv_position);
        catalogTv = (TextView) headView.findViewById(R.id.catalog);

        item_medleft_top = (LinearLayout) headView.findViewById(R.id.item_medleft_top);
        pinyinComparator = new PinyinComparator();
        characterParser = CharacterParser.getInstance();

        Utils.getDeptName(myDeptBuffer, myDepts);
        nameTv.setText(MainApp.user.getRealname());
        deptInfoTv.setText(myDeptBuffer.toString());
        catalogTv.setText("我");
        myUserList = Common.getMyUserDept();

        /*我的部门数据中，移除自己*/
        for (int i = 0; i < myUserList.size(); i++) {
            if (myUserList.get(i).getId().equals(MainApp.user.getId())) {
                myUserList.remove(i);
                break;
            }
        }
        sideBar.setTextView(tv_dialog);
        //设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    sortListView.setSelection(position);
                }
            }
        });

        // 根据a-z进行排序源数据
        sortDataList();
        Collections.sort(myUserList, pinyinComparator);
        adapter = new ContactsInMyDeptAdapter(getActivity(), myUserList);
        sortListView.addHeaderView(headView);
        sortListView.setAdapter(adapter);

        /*列表监听*/
        sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //adapterView.getAdapter().getItem(position);
                LogUtil.d("User数据:"+MainApp.gson.toJson(myUserList.get(position -1)));
                Bundle b = new Bundle();
                b.putSerializable("user", myUserList.get(position - 1));
                app.startActivity(getActivity(), ContactInfoActivity_.class, MainApp.ENTER_TYPE_ZOOM_OUT, false, b);

            }
        });

        /*点击自己*/
        item_medleft_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtil.d("User数据:"+MainApp.gson.toJson(MainApp.user));
                Bundle b = new Bundle();
                b.putSerializable("user", MainApp.user);
                app.startActivity(getActivity(), ContactInfoActivity_.class, MainApp.ENTER_TYPE_ZOOM_OUT, false, b);
            }
        });
    }

    /**
     * 遍历数据排序
     */
    void sortDataList() {
        for (User user : myUserList) {
            String pinyin = characterParser.getSelling(user.getRealname());
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                user.setSortLetters(sortString.toUpperCase());
            } else {
                user.setSortLetters("#");
            }
        }
    }
}
