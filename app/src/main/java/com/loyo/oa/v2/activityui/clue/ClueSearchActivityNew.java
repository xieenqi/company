package com.loyo.oa.v2.activityui.clue;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.common.utils.DateTool;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.clue.api.ClueService;
import com.loyo.oa.v2.activityui.clue.common.ClueType;
import com.loyo.oa.v2.activityui.clue.model.ClueListItem;
import com.loyo.oa.v2.activityui.wfinstance.WfinstanceInfoActivity_;
import com.loyo.oa.v2.activityui.wfinstance.api.WfinstanceService;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.WfInstanceRecord;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.LoyoErrorChecker;
import com.loyo.oa.v2.tool.BaseSearchActivity;

import java.util.HashMap;


public class ClueSearchActivityNew extends BaseSearchActivity<ClueListItem> {
    //可传入参数定义
    public static final String EXTRA_TYPE = "type";//类型，是1:个人或者2:团队
    public static final String EXTRA_RESPONSEBLE_SHOW = "responsibleVisiblity";
    public static final String EXTRA_JUMP_NEW_PAGE = "jumpNewPage";
    public static final String EXTRA_JUMP_PAGE_CLASS = "class";
    public static final String EXTRA_CAN_BE_EMPTY = "canBeEmpty";
    public static final String EXTRA_PICKER_ID = "Id";

    private int type = 0;
    private boolean jumpNewPage = false;
    private Class<?> cls;
    private boolean canBeEmpty = false;
    private boolean responsibleVisiblity=true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent=getIntent();
        if (null != intent) {
            type = intent.getIntExtra(EXTRA_TYPE, 0);
            canBeEmpty = intent.getBooleanExtra(EXTRA_CAN_BE_EMPTY, false);
            jumpNewPage = intent.getBooleanExtra(EXTRA_JUMP_NEW_PAGE, false);
            cls = (Class<?>) intent.getSerializableExtra(EXTRA_JUMP_PAGE_CLASS);
            responsibleVisiblity=intent.getBooleanExtra(EXTRA_RESPONSEBLE_SHOW,true);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void getData() {

        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", paginationX.getShouldLoadPageIndex());
        map.put("pageSize", paginationX.getPageSize());
        map.put("keyWords", strSearch);
        switch (type) {
            /*我的线索*/
            case 1:
                ClueService.getMyClueList(map).subscribe(getDefaultLoyoSubscriber());
                break;
            /*团队线索*/
            case 2:
                ClueService.getTeamClueList(map).subscribe(getDefaultLoyoSubscriber());

                break;
            default:
                //如果没有获取到type 参数，就抛出异常
                throw new UnsupportedOperationException("type类型为空或者不支持！");
        }
    }

    //订阅者，处理网络请求事件
    private DefaultLoyoSubscriber<PaginationX<ClueListItem>> getDefaultLoyoSubscriber(){
        return new DefaultLoyoSubscriber<PaginationX<ClueListItem>>() {
            @Override
            public void onError(Throwable e) {
                fail(e);
            }
            @Override
            public void onNext(PaginationX<ClueListItem> clueListItepaginationX) {
                success(clueListItepaginationX);
            }
        };
    }
    @Override
    public void onListItemClick(View view, int position) {
        Intent mIntent = new Intent(getApplicationContext(), WfinstanceInfoActivity_.class);
        mIntent.putExtra(ExtraAndResult.EXTRA_ID, paginationX.getRecords().get(position).id);
        startActivity(mIntent);
    }

    @Override
    public BaseAdapter setAdapter() {
        return new ClueSearchAdapter();
    }

    public class ClueSearchAdapter extends BaseAdapter {


        public void setAdapter() {
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return paginationX.getLoadedTotalRecords();
        }

        @Override
        public Object getItem(int position) {
            return 0;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            ClueListItem clueListItem = paginationX.getRecords().get(position);
            Holder holder = null;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_teamclue, null);
                holder = new Holder();
                holder.tv_company_name = (TextView) convertView.findViewById(R.id.tv_company_name);
                holder.tv_customer = (TextView) convertView.findViewById(R.id.tv_customer);
                holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                holder.ll_responsible = (LinearLayout) convertView.findViewById(R.id.ll_responsible);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            holder.tv_name.setText(clueListItem.name);
            holder.tv_company_name.setText(clueListItem.companyName);
            holder.tv_customer.setText(clueListItem.name);
            if (clueListItem.lastActAt != 0) {
//                holder.tv_time.setText(DateTool.timet(clueListItem.lastActAt + "", "yyyy-MM-dd HH:mm"));
                holder.tv_time.setText(com.loyo.oa.common.utils.DateTool.getDateTimeFriendly(clueListItem.lastActAt));
            } else {
                holder.tv_time.setText("--");
            }
            holder.ll_responsible.setVisibility(responsibleVisiblity ? View.VISIBLE : View.GONE);
            return convertView;
        }

        class Holder {
            TextView tv_company_name; /* 公司名称 */
            TextView tv_customer;     /* 负责人 */
            TextView tv_time;         /* 跟进时间 */
            TextView tv_name;         /* 客户名称 */
            LinearLayout ll_responsible;
        }
    }
}
