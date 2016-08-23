package com.loyo.oa.v2.activityui.clue.bean;

import java.util.ArrayList;

/**
 * 【我的线索列表】
 * Created by yyy on 16/8/22.
 */
public class ClueList {

    public int errcode;
    public String errmsg;
    public mData data;

    public class mData {

        public int pageIndex;
        public int pageSize;
        public int totalRecords;
        public ArrayList<ClueListItem> records;

    }
}
