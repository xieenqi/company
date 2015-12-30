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
import com.loyo.oa.v2.activity.ContactInfoActivity_;
import com.loyo.oa.v2.adapter.ContactsInMyDeptAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Department;
import com.loyo.oa.v2.beans.SortModel;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.common.CharacterParser;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.common.PinyinComparator;
import com.loyo.oa.v2.common.SideBar;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.LogUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 【本部门】人员列表页
 *
 * Created by yyy on 15/12/30.
 */
public class ContactsInMyDeptFragment extends BaseFragment {

    public SideBar sideBar;
    public ListView sortListView;
    public ContactsInMyDeptAdapter adapter;
    public CharacterParser characterParser;
    public PinyinComparator pinyinComparator;
    public LayoutInflater mInflater;

    public ArrayList<User> myUserList;
    public ArrayList<User> userAllList;
    public ArrayList<Department> deptSource;//部门数据源

    public int positions;

    public View headView;
    public ImageView heading;
    public TextView nameTv;
    public TextView deptInfoTv;
    public TextView catalogTv;
    public LinearLayout item_medleft_top;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_users, container, false);
        initView(view);
        return view;

    }

    public void initView(View view) {

        sortListView = (ListView) view.findViewById(R.id.expandableListView_user);
        sideBar = (SideBar) view.findViewById(R.id.sidrbar);
        mInflater = LayoutInflater.from(getActivity());

        headView = mInflater.inflate(R.layout.item_medleft, null);
        heading = (ImageView)headView.findViewById(R.id.img);
        nameTv =  (TextView)headView.findViewById(R.id.tv_name);
        deptInfoTv = (TextView)headView.findViewById(R.id.tv_position);
        catalogTv = (TextView)headView.findViewById(R.id.catalog);
        item_medleft_top = (LinearLayout)headView.findViewById(R.id.item_medleft_top);

        deptSource = Common.getLstDepartment();
        userAllList = new ArrayList<>();
        myUserList = new ArrayList<>();
        pinyinComparator = new PinyinComparator();
        characterParser = CharacterParser.getInstance();

        ImageLoader.getInstance().displayImage(MainApp.user.getAvatar(), heading);
        nameTv.setText(MainApp.user.getRealname());
        deptInfoTv.setText(MainApp.user.depts.get(0).getShortDept().getName());
        catalogTv.setText("我");

        /*全部人员获取*/
        for (int i = 0; i < MainApp.lstDepartment.size(); i++) {
            for (int k = 0; k < MainApp.lstDepartment.get(i).getUsers().size(); k++) {
                userAllList.add(MainApp.lstDepartment.get(i).getUsers().get(k));
            }
        }

       /*获取我的部门下标*/
        for(int i = 0;i<deptSource.size();i++){
            if(deptSource.get(i).getId().equals(MainApp.user.depts.get(0).getShortDept().getId())){
                positions = i;
                break;
            }
        }

        /*根据部门下标获取本部门人员*/
        myUserList.clear();
        for (User user : userAllList) {
            String xPath = user.depts.get(0).getShortDept().getXpath();
            if (xPath.contains(deptSource.get(positions).getXpath())) {
                myUserList.add(user);
            }
        }

        /*我的部门数据中，移除自己*/
        for(int i = 0;i<myUserList.size();i++){
            if(myUserList.get(i).getId().equals(MainApp.user.getId())){
                myUserList.remove(i);
                break;
            }
        }

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

                Bundle b = new Bundle();
                b.putSerializable("user", myUserList.get(position -1));
                app.startActivity(getActivity(), ContactInfoActivity_.class, MainApp.ENTER_TYPE_ZOOM_OUT, false, b);

            }
        });

        /*点击自己*/
        item_medleft_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle b = new Bundle();
                b.putSerializable("user",MainApp.user);
                app.startActivity(getActivity(), ContactInfoActivity_.class, MainApp.ENTER_TYPE_ZOOM_OUT, false, b);
            }
        });
    }

    /**
     * 遍历数据排序
     */
    void sortDataList(){

        for (User user : myUserList){
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
