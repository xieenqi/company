package com.loyo.oa.v2.activityui.order;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.clue.model.ClueListItem;
import com.loyo.oa.v2.activityui.order.bean.OrderListItem;
import com.loyo.oa.v2.activityui.order.common.OrderCommon;
import com.loyo.oa.v2.activityui.order.common.OrderType;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.order.api.OrderService;
import com.loyo.oa.v2.tool.BaseSearchActivity;
import com.loyo.oa.v2.tool.Utils;

import java.util.HashMap;

import rx.Observer;


public class OrderSearchOrPickerActivity extends BaseSearchActivity<OrderListItem> {
    //可传入参数定义
    public static final String EXTRA_TYPE = "type";//类型，是个人或者团队，，这里用枚举

    private OrderType type = OrderType.MY_ORDER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent=getIntent();
        if (null != intent) {
            type = (OrderType) intent.getSerializableExtra(EXTRA_TYPE);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void getData() {

        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", paginationX.getShouldLoadPageIndex());
        map.put("pageSize", paginationX.getPageSize());
        map.put("keyWords", strSearch);
        DefaultLoyoSubscriber<PaginationX<OrderListItem>> defaultLoyoSubscriber = new DefaultLoyoSubscriber<PaginationX<OrderListItem>>() {
            @Override
            public void onError(Throwable e) {
                fail(e);
            }

            @Override
            public void onNext(PaginationX<OrderListItem> orderListItemPaginationX) {
                success(orderListItemPaginationX);
            }
        };

        switch (type) {
            /*我的订单*/
            case MY_ORDER:
                subscriber=OrderService.getOrderMyList(map).subscribe (defaultLoyoSubscriber);
                break;
            /*团队订单*/
            case TEAM_ORDER:
                subscriber=OrderService.getOrderTeamList(map).subscribe(defaultLoyoSubscriber);
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


    @Override
    public void onListItemClick(View view, int position) {
        if (jumpNewPage) {
            Bundle b = new Bundle();
            b.putSerializable(ClueListItem.class.getName(), paginationX.getRecords().get(position));
            b.putString(ExtraAndResult.EXTRA_ID, paginationX.getRecords().get(position).id);
            b.putInt(ExtraAndResult.DYNAMIC_ADD_ACTION, ExtraAndResult.DYNAMIC_ADD_CULE);
//            b.putBoolean(ExtraAndResult.IS_TEAM, type==OrderType.MY_ORDER ? false : true);//重构前有，但是，发现传过去，并没有用到
            MainApp.getMainApp().startActivity(this, cls, MainApp.ENTER_TYPE_RIGHT, false, b);
        } else {
            Intent intent = new Intent();
            intent.putExtra("data", paginationX.getRecords().get(position));
            app.finishActivity(OrderSearchOrPickerActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
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
            return paginationX.isEnpty() ? 0 : paginationX.getLoadedTotalRecords();
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
            OrderListItem item = paginationX.getRecords().get(position);
            Holder holder = null;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_order_my_team, null);
                holder = new Holder();
                holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                holder.tv_status = (TextView) convertView.findViewById(R.id.tv_status);
                holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                holder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
                holder.tv_customer = (TextView) convertView.findViewById(R.id.tv_customer);
                holder.tv_product = (TextView) convertView.findViewById(R.id.tv_product);
                holder.ll_responsible = (LinearLayout) convertView.findViewById(R.id.ll_responsible);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            holder.setContent(item);
            return convertView;
        }

        class Holder {
            TextView tv_title, tv_status, tv_time, tv_name, tv_money, tv_customer, tv_product;
            LinearLayout ll_responsible;

            public void setContent(OrderListItem item) {
                ll_responsible.setVisibility(type == OrderType.TEAM_ORDER ? View.VISIBLE : View.GONE);
                tv_title.setText(item.title);
                OrderCommon.getOrderDetailsStatus(tv_status, item.status);
                tv_name.setText(item.directorName);
                tv_money.setText(Utils.setValueDouble(item.dealMoney));
                tv_customer.setText(item.customerName);
                tv_product.setText(item.proName);
                tv_time.setText(com.loyo.oa.common.utils.DateTool.getDateTimeFriendly(Long.valueOf(item.createdAt + "")));
            }
        }
    }

}
