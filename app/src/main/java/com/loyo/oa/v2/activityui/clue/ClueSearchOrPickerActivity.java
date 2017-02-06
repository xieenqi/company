package com.loyo.oa.v2.activityui.clue;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.common.utils.DateTool;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.clue.api.ClueService;
import com.loyo.oa.v2.activityui.clue.common.ClueType;
import com.loyo.oa.v2.activityui.clue.model.ClueListItem;
import com.loyo.oa.v2.activityui.followup.event.FollowUpRushEvent;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.tool.BaseSearchActivity;

import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;


public class ClueSearchOrPickerActivity extends BaseSearchActivity<ClueListItem> {
    //可传入参数定义
    public static final String EXTRA_TYPE = "type";//类型，是个人或者团队，，这里用枚举替换
    public static final String EXTRA_RESPONSEBLE_SHOW = "responsibleVisiblity";//是否显示负责人

    private ClueType type = ClueType.MY_CLUE;
    private boolean responsibleVisiblity=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent=getIntent();
        if (null != intent) {
            type = (ClueType) intent.getSerializableExtra(EXTRA_TYPE);
            canBeEmpty = intent.getBooleanExtra(EXTRA_CAN_BE_EMPTY, false);
            jumpNewPage = intent.getBooleanExtra(EXTRA_JUMP_NEW_PAGE, false);
            cls = (Class<?>) intent.getSerializableExtra(EXTRA_JUMP_PAGE_CLASS);
            responsibleVisiblity=intent.getBooleanExtra(EXTRA_RESPONSEBLE_SHOW,false);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void getData() {

        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", paginationX.getShouldLoadPageIndex());
        map.put("pageSize", paginationX.getPageSize());
        map.put("keyword", strSearch);
        switch (type) {
            /*我的线索*/
            case MY_CLUE:
                subscriber=ClueService.getMyClueList(map).subscribe(getDefaultLoyoSubscriber());
                break;
            /*团队线索*/
            case TEAM_CLUE:
                subscriber=ClueService.getTeamClueList(map).subscribe(getDefaultLoyoSubscriber());
                break;
            default:
                //如果没有获取到type 参数，就抛出异常
                throw new UnsupportedOperationException("type类型为空或者不支持！");
        }
    }

    @Override
    public boolean isShowHeadView() {
        return canBeEmpty;
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
        if (jumpNewPage) {
            Bundle b = new Bundle();
            b.putSerializable(ClueListItem.class.getName(), paginationX.getRecords().get(position));
            b.putString(ExtraAndResult.EXTRA_ID, paginationX.getRecords().get(position).id);
            b.putInt(ExtraAndResult.DYNAMIC_ADD_ACTION, ExtraAndResult.DYNAMIC_ADD_CULE);
            MainApp.getMainApp().startActivity(this, cls, MainApp.ENTER_TYPE_RIGHT, false, b);
        } else {
            Intent intent = new Intent();
            intent.putExtra("data", paginationX.getRecords().get(position));
            app.finishActivity(ClueSearchOrPickerActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
        }
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
                holder.tv_time.setText(DateTool.getDateTimeFriendly(clueListItem.lastActAt));
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

    /**
     * 新建跟进成功以后，关闭本页
     */
    @Subscribe
    public void onFollowUpRushEvent(FollowUpRushEvent event) {
        finish();
    }
}
