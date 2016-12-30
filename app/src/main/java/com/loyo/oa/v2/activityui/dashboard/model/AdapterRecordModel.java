package com.loyo.oa.v2.activityui.dashboard.model;

import java.util.ArrayList;

/**
 * Created by EthanGong on 2016/12/30.
 */

public interface AdapterRecordModel {
    String getName();
    ArrayList<String> getItems();
    
    class RecordFollowupStatistic implements AdapterRecordModel {
        public Integer total;
        public Integer totalCustomer;
        public String userName;


        @Override
        public String getName() {
            return userName;
        }

        @Override
        public ArrayList<String> getItems() {
            return new ArrayList<String>(){{
                add(userName);
                add(String.valueOf(totalCustomer));
                add(String.valueOf(total));
            }};
        }
    }

    class RecordPhoneCallStatistic extends RecordFollowupStatistic {
        //电话录音
        public String totalLength;

        @Override
        public ArrayList<String> getItems() {
            return new ArrayList<String>(){{
                add(userName);
                add(String.valueOf(totalCustomer));
                add(String.valueOf(total));
                add(String.valueOf(totalLength));
            }};
        }
    }

    class RecordStockStatistic  implements AdapterRecordModel {
        public String id;
        public String name;
        //增量／存量
        public Integer count;
        public Integer addCount;

        @Override
        public String getName() {
            return name;
        }

        @Override
        public ArrayList<String> getItems() {
            return new ArrayList<String>(){{
                add(name);
                add(String.valueOf(count));
                add(String.valueOf(addCount));
            }};
        }
    }

    class RecordMoneyStatistic implements AdapterRecordModel {
        public String id;
        public String name;
        //订单数量和金额
        public String orderNum;
        public String targetNum;
        public String finish_rate;

        @Override
        public String getName() {
            return name;
        }

        @Override
        public ArrayList<String> getItems() {
            return new ArrayList<String>(){{
                add(name);
                add(targetNum);
                add(orderNum);
                add(finish_rate);
            }};
        }
    }
}
