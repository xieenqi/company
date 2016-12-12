package com.loyo.oa.v2.activityui.dashboard.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.tool.BaseFragment;

/**
 * 【仪表盘】
 * Created by yyy on 16/12/9.
 */

public class HomeDashboardFragment extends BaseFragment implements View.OnClickListener{

    private FrameLayout layout_dashboard_container;
    private View mView;
    private RadioButton rb_customer,rb_clue;
    private CustomerDashBoardFrag customerDbfrag;
    private ClueDashBoardFrag clueDashBoardFrag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        initUI();
        return mView;
    }


    private void initUI(){
        rb_customer = (RadioButton) mView.findViewById(R.id.rb_customer);
        rb_clue = (RadioButton)  mView.findViewById(R.id.rb_clue);

        rb_customer.setOnClickListener(this);
        rb_clue.setOnClickListener(this);
        layout_dashboard_container = (FrameLayout) mView.findViewById(R.id.layout_dashboard_container);
        customerDbfrag = new CustomerDashBoardFrag();
        clueDashBoardFrag = new ClueDashBoardFrag();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.add(R.id.layout_dashboard_container, customerDbfrag).commit();

    }


    @Override
    public void onClick(View view) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction;
        switch (view.getId()){

            /*客户跟进*/
            case R.id.rb_customer:
                transaction = fm.beginTransaction();
                transaction.replace(R.id.layout_dashboard_container, customerDbfrag).commit();
                break;

            /*线索跟进*/
            case R.id.rb_clue:
                transaction = fm.beginTransaction();
                transaction.replace(R.id.layout_dashboard_container, clueDashBoardFrag).commit();
                break;

        }
    }
}
