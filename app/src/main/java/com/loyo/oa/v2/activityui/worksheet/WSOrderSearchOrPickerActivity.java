package com.loyo.oa.v2.activityui.worksheet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetOrder;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.order.api.OrderService;
import com.loyo.oa.v2.tool.BaseSearchActivity;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.worksheet.api.WorksheetService;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * 因为使用的数据接口不一样，所以，不能使用order的写好的组件
 */
public class WSOrderSearchOrPickerActivity extends BaseSearchActivity<WorksheetOrder> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void getData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", paginationX.getShouldLoadPageIndex());
        map.put("pageSize", paginationX.getPageSize());
        map.put("keyword", strSearch);
        WorksheetService.getWorksheetOrdersList(map)
                .subscribe(new DefaultLoyoSubscriber<PaginationX<WorksheetOrder>>(ll_loading) {
                    @Override
                    public void onError(Throwable e) {
                        fail(e);
                    }

                    @Override
                    public void onNext(PaginationX<WorksheetOrder> x) {
                        success(x);
                    }
                });
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
            app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
        }
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup, WorksheetOrder order) {
        Holder holder = null;
        if (convertView == null) {
            holder = new Holder();
            convertView = LayoutInflater.from(WSOrderSearchOrPickerActivity.this).inflate(R.layout.item_worksheet_order, null);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
            holder.tv_customer = (TextView) convertView.findViewById(R.id.tv_customer);
            holder.tv_product = (TextView) convertView.findViewById(R.id.tv_product);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.tv_title.setText(order.title);
        holder.tv_money.setText(Utils.setValueDouble(order.dealMoney));
        holder.tv_customer.setText(order.customerName);
        holder.tv_product.setText(order.proName);
        return convertView;

    }
    class Holder {
        TextView tv_title, tv_name, tv_money, tv_customer, tv_product;
    }
}
