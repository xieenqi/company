package com.loyo.oa.v2.activity.wfinstance;

import java.io.Serializable;
import java.util.List;

/**
 * Created by pj on 15/12/8.
 */
public class HttpWfinstanceMangerList<T> implements Serializable {
    public int pageIndex;
    public int pageSize;
    public List<Records> records;

    class Records{

    }

}
