package com.loyo.oa.v2.activityui.order;

import android.widget.TextView;

import com.loyo.oa.v2.R;

/**
 * 处理订单公用相关事务
 * Created by xeq on 16/8/3.
 */
public class OrderCommon {

    public static void getOrderStatus(TextView view, int status) {
        if (status > 0) {
            String statusText = "";
            int statusBj = R.drawable.retange_blue;
            switch (status) {
                case 1:
                    statusText = "待审核";
                    statusBj = R.drawable.retange_blue;
                    break;
                case 2:
                    statusText = "未通过";
                    statusBj = R.drawable.retange_blue;
                    break;
                case 3:
                    statusText = "进行中";
                    statusBj = R.drawable.retange_blue;
                    break;
                case 4:
                    statusText = "已完成";
                    statusBj = R.drawable.retange_gray;
                    break;
                case 5:
                    statusText = "意外终止";
                    statusBj = R.drawable.retange_gray;
                    break;
                default:
            }
            view.setText(statusText);
            view.setBackgroundResource(statusBj);
        }
    }
}
