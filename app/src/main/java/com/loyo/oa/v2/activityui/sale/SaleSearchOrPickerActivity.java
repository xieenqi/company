package com.loyo.oa.v2.activityui.sale;

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
import com.loyo.oa.v2.activityui.project.api.ProjectService;
import com.loyo.oa.v2.activityui.sale.api.SaleService;
import com.loyo.oa.v2.activityui.sale.bean.SaleRecord;
import com.loyo.oa.v2.activityui.sale.common.SaleType;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.Project;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.LoyoErrorChecker;
import com.loyo.oa.v2.tool.BaseSearchActivity;
import com.loyo.oa.v2.tool.Utils;

import java.util.HashMap;

import static com.loyo.oa.v2.activityui.sale.common.SaleType.MY_SALE_SEARCH;

/**
 * 描述 : 搜索，选择；
 */
public class SaleSearchOrPickerActivity extends BaseSearchActivity<SaleRecord> {
    public static final String EXTRA_PICKER_ID = "id";

    private SaleType type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent=getIntent();
        if (null != intent) {
            canBeEmpty = intent.getBooleanExtra(EXTRA_CAN_BE_EMPTY, false);
            jumpNewPage = intent.getBooleanExtra(EXTRA_JUMP_NEW_PAGE, false);
            cls = (Class<?>) intent.getSerializableExtra(EXTRA_JUMP_PAGE_CLASS);
            type= (SaleType) intent.getSerializableExtra(ExtraAndResult.EXTRA_TYPE);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void getData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", paginationX.getShouldLoadPageIndex());
        map.put("pageSize", paginationX.getPageSize());
        map.put("keyWords", strSearch);
        switch (type){
            case MY_SALE_SEARCH:
                subscriber=SaleService.getSaleMyList(map).subscribe(getSaleListSubscriber());
                break;
            case TEAM_SALE_SEARCH:
                subscriber=SaleService.getSaleTeamList(map).subscribe(getSaleListSubscriber());
                break;
            default:
                throw new UnsupportedOperationException("参数错误，请传入type");
        }
    }


    //获取一个订阅者，来处理结果
    private DefaultLoyoSubscriber<PaginationX<SaleRecord>> getSaleListSubscriber(){
        return new DefaultLoyoSubscriber<PaginationX<SaleRecord>>() {
            @Override
            public void onError(Throwable e) {
               fail(e);
            }

            @Override
            public void onNext(PaginationX<SaleRecord> saleRecordPaginationX) {
              success(saleRecordPaginationX);
            }
        };
    }


    @Override
    public boolean isShowHeadView() {
        return canBeEmpty;
    }

    @Override
    public void onListItemClick(View view, int position) {
        if (jumpNewPage) {
            Bundle b = new Bundle();
            b.putBoolean(ExtraAndResult.IS_TEAM, type == MY_SALE_SEARCH ? false : true);
            b.putSerializable(EXTRA_PICKER_ID, paginationX.getRecords().get(position).getId());
            b.putSerializable(SaleService.class.getName(), paginationX.getRecords().get(position));
            MainApp.getMainApp().startActivity(this, cls, MainApp.ENTER_TYPE_RIGHT, false, b);
        } else {
            Intent intent = new Intent();
            intent.putExtra("data", paginationX.getRecords().get(position));
            app.finishActivity(SaleSearchOrPickerActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
        }
    }

    @Override
    public BaseAdapter setAdapter() {
        return new CommonSearchAdapter();
    }

    public class CommonSearchAdapter extends BaseAdapter {


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
            SaleRecord item = paginationX.getRecords().get(position);
           Holder holder = null;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_saleteamlist, null);
                holder = new Holder();
                holder.creator = (TextView) convertView.findViewById(R.id.sale_teamlist_creator);
                holder.state = (TextView) convertView.findViewById(R.id.sale_teamlist_state);
                holder.guess = (TextView) convertView.findViewById(R.id.sale_teamlist_guess);
                holder.money = (TextView) convertView.findViewById(R.id.sale_teamlist_money);
                holder.title = (TextView) convertView.findViewById(R.id.sale_teamlist_title);
                holder.ll_creator = (LinearLayout) convertView.findViewById(R.id.ll_creator);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            holder.setContent(item);
            return convertView;
        }

        class Holder {
            TextView creator, state, guess, money, title;
            LinearLayout ll_creator;

            public void setContent(SaleRecord item) {
                title.setText(item.getName());
                money.setText(Utils.setValueDouble(item.getEstimatedAmount()) + "");
                String stageName = "初步接洽";
                if (!item.getStageNmae().isEmpty()) {
                    stageName = item.getStageNmae();
                }
                state.setText(stageName + "(" + item.getProb() + "%)");
                creator.setText(item.getCreateName());
                ll_creator.setVisibility(type == MY_SALE_SEARCH ? View.GONE : View.VISIBLE);
            }
        }
    }

}
