package com.loyo.oa.v2.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.LegworksListActivity_;
import com.loyo.oa.v2.activity.SignInActivity;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.PaginationLegWork;
import com.loyo.oa.v2.beans.TeamLegworkDetail;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.point.ILegwork;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.tool.ViewHolder;
import com.loyo.oa.v2.tool.customview.RoundImageView;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import retrofit.client.Response;

/**
 * com.loyo.oa.v2.fragment
 * 描述 :团队拜访列表
 * 作者 : ykb
 * 时间 : 15/9/22.
 */
public class SignInOfTeamFragment extends BaseFragment implements View.OnClickListener, PullToRefreshBase.OnRefreshListener2 {

    private ViewGroup imgTimeLeft;
    private ViewGroup imgTimeRight;
    private ViewGroup layout_date;
    private Button btn_add;
    private PullToRefreshListView lv;
    private TextView tv_time;
    private TextView tv_count_title1;
    private TextView tv_count_title2;

    private ArrayList<TeamLegworkDetail> legWorks = new ArrayList<>();
    private TeamSignInListAdapter adapter;
    private long endAt;
    private Calendar cal;
    private View mView;
    private PaginationLegWork legworkPaginationX = new PaginationLegWork(20);
    private boolean isTopAdd;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_signin, container, false);
            mView.findViewById(R.id.layout_count).setVisibility(View.VISIBLE);
            layout_date = (ViewGroup) mView.findViewById(R.id.layout_date);

            tv_time = (TextView) mView.findViewById(R.id.tv_time);
            tv_count_title1 = (TextView) mView.findViewById(R.id.tv_count_title1);
            tv_count_title2 = (TextView) mView.findViewById(R.id.tv_count_title2);

            imgTimeLeft = (ViewGroup) mView.findViewById(R.id.img_time_left);
            imgTimeRight = (ViewGroup) mView.findViewById(R.id.img_time_right);


            imgTimeLeft.setOnTouchListener(Global.GetTouch());
            imgTimeRight.setOnTouchListener(Global.GetTouch());
            imgTimeLeft.setOnClickListener(this);
            imgTimeRight.setOnClickListener(this);

            btn_add = (Button) mView.findViewById(R.id.btn_add);
            btn_add.setOnTouchListener(Global.GetTouch());
            btn_add.setOnClickListener(this);

            lv = (PullToRefreshListView) mView.findViewById(R.id.listView_legworks);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    User user = ((TeamLegworkDetail) adapter.getItem(i - 1)).getUser().toUser();
                    previewLegWorks(user);
                }
            });

            cal = Calendar.getInstance(Locale.CHINA);
            layout_date.setVisibility(View.VISIBLE);
            initTimeStr(System.currentTimeMillis());
            endAt = DateTool.getEndAt_ofDay();
            getData();
        }
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * 初始化时间显示
     *
     * @param mills
     */
    private void initTimeStr(long mills) {
        String time = app.df12.format(new Date(mills));
        tv_time.setText(time);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_time_left:
                previousDay();
                break;
            case R.id.img_time_right:
                nextDay();
                break;
            case R.id.btn_add:
                create();
                break;
        }
    }

    /**
     * 新增拜访
     */
    private void create() {
        startActivityForResult(new Intent(mActivity, SignInActivity.class), FinalVariables.REQUEST_CREATE_LEGWORK);
    }

    /**
     * 前一天
     */
    private void previousDay() {
        if (cal.get(Calendar.DAY_OF_MONTH) == cal.getActualMinimum(Calendar.DAY_OF_MONTH)) {
            if (cal.get(Calendar.MONTH) == cal.getActualMinimum(Calendar.MONTH)) {
                cal.set((cal.get(Calendar.YEAR) - 1), cal.getActualMaximum(Calendar.MONTH), cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            } else {
                cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            }
        } else {
            cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 1);
        }

        refreshData();
    }

    /**
     * 后一天
     */
    private void nextDay() {
        if (cal.get(Calendar.DAY_OF_MONTH) == cal.getActualMaximum(Calendar.DAY_OF_MONTH)) {
            if (cal.get(Calendar.MONTH) == cal.getActualMaximum(Calendar.MONTH)) {
                cal.set((cal.get(Calendar.YEAR) + 1), cal.getActualMinimum(Calendar.MONTH), 1);
            } else {
                cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);
                cal.set(Calendar.DAY_OF_MONTH, 1);
            }
        } else {
            cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + 1);
        }

        refreshData();
    }

    /**
     * 刷新数据
     */
    private void refreshData() {
        endAt = cal.getTime().getTime();
        initTimeStr(cal.getTime().getTime());
        onPullDownToRefresh(lv);
    }

    /**
     * 绑定数据
     */
    private void bindData() {
        if (null == adapter) {
            adapter = new TeamSignInListAdapter();
            lv.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
        String visitNum= String.valueOf(legworkPaginationX.getRecords().getVisitNum());
        String visitCustomerNum=String.valueOf(legworkPaginationX.getRecords().getCustNum());
        String teamVisitStr="团队共拜访 "+visitNum+" 次 ";
        String teamVisitCustomerStr="，共拜访 "+visitCustomerNum+" 位客户";
        tv_count_title1.setText(Utils.modifyTextColor(teamVisitStr,getResources().getColor(R.color.title_bg1),"团队共拜访 ".length(),"团队共拜访 ".length()+visitNum.length()));
        tv_count_title2.setText(Utils.modifyTextColor(teamVisitCustomerStr,getResources().getColor(R.color.title_bg1),"，共拜访 ".length(),"，共拜访 ".length()+visitCustomerNum.length()));
    }

    /**
     * 获取列表
     */
    private void getData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", "");
        map.put("deptId", MainApp.user.getDepts().get(0).getShortDept().getId());
        map.put("startAt", (endAt - DateTool.DAY_MILLIS) / 1000);
        map.put("endAt", endAt / 1000);
        map.put("custId", "");
        map.put("pageIndex", legworkPaginationX.getPageIndex());
        map.put("pageSize", isTopAdd ? legWorks.size() >= 20 ? legWorks.size() : 20 : 20);

        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ILegwork.class).getTeamLegworks(map, new RCallback<PaginationLegWork>() {
            @Override
            public void success(PaginationLegWork paginationX, Response response) {
                legworkPaginationX = paginationX;
                if (isTopAdd) {
                    legWorks.clear();
                }
                if (null != paginationX.getRecords()) {
                    legWorks.addAll(paginationX.getRecords().getDetail());
                } else {
                    legWorks.clear();
                }
                bindData();
            }
        });
    }

    /**
     * 查看下属签到
     *
     * @param user
     */
    private void previewLegWorks(User user) {
        Intent intent = new Intent(mActivity, LegworksListActivity_.class);
        intent.putExtra("data", user);
        startActivity(intent);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        legworkPaginationX.setPageIndex(1);
        isTopAdd = true;
        getData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        isTopAdd = false;
        legworkPaginationX.setPageIndex(legworkPaginationX.getPageIndex() + 1);
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
            //            TextView tv_customer_num = ViewHolder.get(view, R.id.tv_visitcustomers);

            ImageLoader.getInstance().displayImage(legWork.getUser().getAvatar(), avatar);
            tv_name.setText(legWork.getUser().getName());
            tv_visit_num.setText("拜访次数 " + legWork.getVisitCount());
            //            tv_customer_num.setText("拜访客户数 "+legWork.getTotalCustCount());
            //            tv_time.setText(DateTool.getDiffTime(legWork.getCreatedAt() * 1000));

            return view;
        }
    }

}
