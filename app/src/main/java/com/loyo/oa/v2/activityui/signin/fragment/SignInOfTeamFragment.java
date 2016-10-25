package com.loyo.oa.v2.activityui.signin.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.attendance.adapter.DataSelectAdapter;
import com.loyo.oa.v2.activityui.attendance.model.DataSelect;
import com.loyo.oa.v2.activityui.other.model.User;
import com.loyo.oa.v2.activityui.signin.LegworksListActivity_;
import com.loyo.oa.v2.activityui.signin.SignInActivity;
import com.loyo.oa.v2.activityui.signin.bean.PaginationLegWork;
import com.loyo.oa.v2.beans.TeamLegworkDetail;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.RecyclerItemClickListener;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.CustomRecyclerView;
import com.loyo.oa.v2.customview.RoundImageView;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshListView;
import com.loyo.oa.v2.point.ILegwork;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.tool.ViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 描述 :【团队拜访】 列表
 * com.loyo.oa.v2.ui.fragment
 * 作者 : ykb
 * 时间 : 15/9/22.
 */
public class SignInOfTeamFragment extends BaseFragment implements View.OnClickListener, PullToRefreshBase.OnRefreshListener2 {

    private ViewGroup layout_nodata;
    private Button btn_add;
    private PullToRefreshListView lv;
    private TextView tv_time;
    private TextView data_time_tv;
    private TextView tv_count_title1;
    private TextView tv_count_title2;
    private CustomRecyclerView recyclerView;

    private ArrayList<TeamLegworkDetail> legWorks = new ArrayList<>();
    private TeamSignInListAdapter adapter;
    private long endAt;
    private int windowW;
    private View mView;
    private PaginationLegWork legworkPaginationX = new PaginationLegWork(20);
    private boolean isTopAdd;
    private String duration;
    private int defaultPosition = 0;

    private LinearLayoutManager layoutManager;
    private DataSelectAdapter dataSelectAdapter;
    private ArrayList<DataSelect> dataSelects;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_signin, container, false);
            mView.findViewById(R.id.layout_count).setVisibility(View.VISIBLE);

            recyclerView = (CustomRecyclerView) mView.findViewById(R.id.recy_data_select);
            tv_time = (TextView) mView.findViewById(R.id.tv_time);
            tv_count_title1 = (TextView) mView.findViewById(R.id.tv_count_title1);
            tv_count_title2 = (TextView) mView.findViewById(R.id.tv_count_title2);
            data_time_tv = (TextView) mView.findViewById(R.id.data_time_tv);
            layout_nodata = (ViewGroup) mView.findViewById(R.id.layout_nodata);

            btn_add = (Button) mView.findViewById(R.id.btn_add);
            btn_add.setVisibility(View.GONE);//暂时不做 增加 团队拜访的功能
            btn_add.setOnTouchListener(Global.GetTouch());
            btn_add.setOnClickListener(this);

            lv = (PullToRefreshListView) mView.findViewById(R.id.listView_legworks);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    User user = ((TeamLegworkDetail) adapter.getItem(i - 1)).user.toUser();
                    previewLegWorks(user);
                }
            });

            dataSelectInit();
            duration = DateTool.timet(dataSelects.get(0).mapOftime, "yyyy-MM-dd");
            endAt = DateTool.getEndAt_ofDay();
            onPullDownToRefresh(lv);
        }
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void dataSelectInit() {
        int year = Integer.parseInt(DateTool.getNowTime("yyyy"));

        dataSelects = DateTool.getYearAllofDay(2015, year);
        Collections.reverse(dataSelects);
        dataSelects.remove(dataSelects.size() - 1);
        windowW = Utils.getWindowHW(getActivity()).getDefaultDisplay().getWidth();
        data_time_tv.setText(dataSelects.get(0).yearMonDay);
        layoutManager = new LinearLayoutManager(getActivity(), 1, true);//true 反向显示 false 正常显示
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        dataSelectAdapter = new DataSelectAdapter(getActivity(), dataSelects, windowW, 2, 0);
        recyclerView.setAdapter(dataSelectAdapter);
        endAt = Long.valueOf(dataSelects.get(0).mapOftime);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                dataSelectAdapter.selectPosition(position);
                dataSelectAdapter.notifyDataSetChanged();
                data_time_tv.setText(dataSelects.get(position).yearMonDay);
                endAt = Long.valueOf(dataSelects.get(position).mapOftime);
                duration = DateTool.timet(dataSelects.get(position).mapOftime, "yyyy-MM-dd");
                onPullDownToRefresh(lv);
                defaultPosition = position;
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                create();
                break;
        }
    }

    /**
     * 新增团队拜访
     */
    private void create() {
        startActivityForResult(new Intent(mActivity, SignInActivity.class), FinalVariables.REQUEST_CREATE_LEGWORK);
    }

    /**
     * 绑定数据
     */
    private void bindData() {
        if (null == adapter) {
            if (null == legWorks || legWorks.size() == 0) {
                layout_nodata.setVisibility(View.VISIBLE);
                lv.setVisibility(View.GONE);
            } else {
                layout_nodata.setVisibility(View.GONE);
                lv.setVisibility(View.VISIBLE);
                adapter = new TeamSignInListAdapter();
                lv.setAdapter(adapter);
            }

        } else {
            adapter.notifyDataSetChanged();
        }
        if (legworkPaginationX.records == null) {
            return;
        }
        String visitNum = String.valueOf(legworkPaginationX.records.visitTotalNum);
        String visitCustomerNum = String.valueOf(legworkPaginationX.records.customerTotalNum);

        String teamVisitStr = "团队共拜访 " + visitNum + " 次 ";
        String teamVisitCustomerStr = "，共拜访 " + visitCustomerNum + " 位客户";
        tv_count_title1.setText(Utils.modifyTextColor(teamVisitStr, Color.parseColor("#2c9dfc"), "团队共拜访 ".length(), "团队共拜访 ".length() + visitNum.length()));
        tv_count_title2.setText(Utils.modifyTextColor(teamVisitCustomerStr, Color.parseColor("#2c9dfc"), "，共拜访 ".length(), "，共拜访 ".length() + visitCustomerNum.length()));
    }

    /**
     * 获取 团队拜访 列表
     */
    private void getData() {
        showLoading("");
        HashMap<String, Object> map = new HashMap<>();
        map.put("duration", duration);
        map.put("pageIndex", legworkPaginationX.pageIndex);
        map.put("pageSize", isTopAdd ? legWorks.size() >= 20 ? legWorks.size() : 20 : 20);
        RestAdapterFactory.getInstance().build(Config_project.SIGNLN_TEM).create(ILegwork.class)
                .getTeamLegworks(map, new RCallback<PaginationLegWork>() {
                    @Override
                    public void success(PaginationLegWork paginationX, Response response) {
                        HttpErrorCheck.checkResponse("团队客户拜访", response);
                        if (paginationX == null) {
                            Toast("服务端错误,没有数据");
                            return;
                        }
                        legworkPaginationX = paginationX;
                        if (isTopAdd) {
                            legWorks.clear();
                        }
                        if (null != paginationX.records && null != paginationX.records.data) {
                            legWorks.addAll(paginationX.records.data);
                        } else {
                            legWorks.clear();
                        }
                        bindData();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                        lv.onRefreshComplete();
                        layout_nodata.setVisibility(View.VISIBLE);
                        lv.setVisibility(View.GONE);
                        super.failure(error);
                    }
                });
    }

    /**
     * 【查看下属签到】item点击
     *
     * @param user
     */
    private void previewLegWorks(User user) {
        Intent intent = new Intent(mActivity, LegworksListActivity_.class);
        intent.putExtra("data", user);
        intent.putExtra("position", defaultPosition);
        intent.putExtra(ExtraAndResult.EXTRA_DATA, endAt);
        startActivity(intent);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        legworkPaginationX.pageIndex = 1;
        isTopAdd = true;
        getData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        isTopAdd = false;
        legworkPaginationX.pageIndex = (legworkPaginationX.pageIndex + 1);
        getData();
    }

    private class TeamSignInListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return legWorks.size();
        }

        @Override
        public Object getItem(int i) {
            return legWorks.isEmpty() ? null : legWorks.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (null == view) {
                view = LayoutInflater.from(mActivity).inflate(R.layout.item_team_signin, viewGroup, false);
            }

            final TeamLegworkDetail legWork = (TeamLegworkDetail) getItem(i);
            RoundImageView avatar = ViewHolder.get(view, R.id.iv_avatar);
            TextView tv_name = ViewHolder.get(view, R.id.tv_name);
            TextView tv_visit_num = ViewHolder.get(view, R.id.tv_visitnum);
            TextView tv_customer_num = ViewHolder.get(view, R.id.tv_visitcustomers);

            ImageLoader.getInstance().displayImage(legWork.user.getAvatar(), avatar);
            tv_name.setText(legWork.user.getName());
            tv_visit_num.setText("拜访次数 " + legWork.visitNum);
            tv_customer_num.setText("拜访客户数 " + legWork.customerNum);
            //tv_time.setText(DateTool.getDiffTime(legWork.getCreatedAt() * 1000));

            return view;
        }
    }

}

