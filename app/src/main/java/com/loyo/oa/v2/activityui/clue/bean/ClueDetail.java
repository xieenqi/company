package com.loyo.oa.v2.activityui.clue.bean;

import com.loyo.oa.v2.beans.BaseBean;

import java.io.Serializable;

/**
 * Created by EthanGong on 16/8/23.
 */

public class ClueDetail extends BaseBean {
    public Data data;

    public class Data implements Serializable {
        public ClueSales sales;
        public ClueActivity activity;
    }

    /**
     * 线索详情 跟进动态
     */
    public class ClueActivity implements Serializable {
        public String contactId;
        public String contactName;
        public String content;
        public long createAt;
        public ClueActivityCreator creator;
        public String id;
        public long remindAt;
        public String typeId;


        public class ClueActivityCreator implements Serializable {
            public String id;
            public String name;
            public String avatar;

        }

    }
}

/**
 * {
 * "errcode": 0,
 * "errmsg": "success",
 * "data": {
 * "sales": {
 * "id": "57bc0aae526f152c42000001",
 * "name": "模糊",
 * "companyId": "5784cde6ebe07f834f000001",
 * "companyName": "哈哈",
 * "cellphone": "566",
 * "tel": "555",
 * "region": {
 * "province": "",
 * "city": "",
 * "county": ""
 * },
 * "address": "弄",
 * "source": "",
 * "remark": "",
 * "status": 1,
 * "creatorId": "578599df526f1566b05e3326",
 * "creatorName": "魏诗语",
 * "responsorId": "578599df526f1566b05e3326",
 * "responsorName": "魏诗语",
 * "createAt": 1471941294,
 * "updateAt": 1471941294,
 * "lastActAt": 0
 * }
 * }
 * }
 */

