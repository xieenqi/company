package com.loyo.oa.v2.activityui.home.bean;

import com.loyo.oa.v2.permission.BusinessOperation;

import java.io.Serializable;

/**
 * Created by yyy on 16/5/27.
 */
public class HomeItem implements Serializable {

    public HomeItem(final int _imageViewRes,
                    final String _title,
                    final String _cls,
                    @BusinessOperation.Type final String _code,
                    final int _tag,
                    String umengAnalyticsId) {
        imageViewRes = _imageViewRes;
        title = _title;
        cls = _cls;
        code = _code;
        tag = _tag;
        this.umengAnalyticsId = umengAnalyticsId;
    }

    public int imageViewRes;
    public String title;
    @BusinessOperation.Type
    public String code;
    public int tag;

    public String cls;
    //红点的数据
    public String extra = "";
    public boolean viewed = true;

    public String umengAnalyticsId;
}
