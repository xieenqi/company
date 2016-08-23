package com.loyo.oa.v2.activityui.clue.bean;

import com.loyo.oa.v2.activityui.customer.bean.CustomerRegional;

/**
 * Created by EthanGong on 16/8/23.
 */

public class ClueDetail {
    public ClueSales sales;
    public ClueActivity activity;

    public class ClueSales {
        public String id;
        public String name;
        public String companyId;
        public String companyName;
        public String cellPhone;
        public String tel;

        public CustomerRegional region;
        public String address;
        public String source;
        public String remark;
        public int status;
        public String creatorId;
        public String creatorName;
        public String responsorId;
        public String responsorName;
        public long createAt;
        public long updateAt;
        public long lastActAt;

    }

    public class ClueActivity {
        public String contactId;
        public String contactName;
        public String content;
        public long createAt;
        public ClueActivityCreator creator;
        public String id;
        public long remindAt;
        public String typeId;


        public class ClueActivityCreator {
            public String id;
            public String name;
            public String avatar;

        }

    }
}

/**
 *  线索详情        v2.4       2016.08.23
 *
 *  activity : 跟进
 *  sales    : 线索
 *  activity.typeId : 拜访类型
 *
 *  JSON:
 *
{
	"data": {
		"activity": {
			"contactId": "",
			"contactName": "无",
			"content": "das",
			"createAt": 1471937847,
			"creator": {
				"avatar": "http://uimg-dev.ukuaiqi.com/297b33d9-99a0-451d-87e5-ddfa0b51e84f/6公海客户-导入弹窗.png",
				"depts": [
					{
						"shortDept": {
							"id": "5784cee3526f1566b05e3322",
							"name": "WEB研究院",
							"xpath": "5784cde6ebe07f834f000002/5784ce92526f1566b05e331e/5784cee3526f1566b05e3322"
						},
						"shortPosition": {
							"id": "5784cd90ffd90797a0faddb8",
							"name": "普通员工",
							"sequence": 4
						},
						"title": "前端开发"
					}
				],
				"gender": 1,
				"id": "57973a52526f152ebf56479f",
				"name": "袁国樵"
			},
			"id": "57bbfd37526f150730000009",
			"remindAt": 1471938180,
			"typeId": "5785a8d0ebe07f6eab000001"
		},
		"sales": {
			"address": "",
			"cellphone": "",
			"companyId": "5784cde6ebe07f834f000001",
			"companyName": "111111",
			"createAt": 1471933281,
			"creatorId": "57a01b41526f1537d86a8368",
			"creatorName": "后",
			"id": "57bbeb61526f1528f0000004",
			"lastActAt": 0,
			"name": "oooo",
			"region": {
				"city": "",
				"county": "",
				"province": ""
			},
			"remark": "",
			"responsorId": "57a01b41526f1537d86a8368",
			"responsorName": "后",
			"source": "",
			"status": 1,
			"updateAt": 1471933281
		}
	},
	"errcode": 0,
	"errmsg": "success"
}
 */

