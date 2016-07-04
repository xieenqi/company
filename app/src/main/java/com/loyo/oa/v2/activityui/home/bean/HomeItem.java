package com.loyo.oa.v2.activityui.home.bean;

import java.io.Serializable;

/**
 * Created by yyy on 16/5/27.
 */
public class HomeItem implements Serializable {

    public HomeItem(final int _imageViewRes, final String _title, final String _cls, final String _code, final int _tag) {
        imageViewRes = _imageViewRes;
        title = _title;
        cls = _cls;
        code = _code;
        tag = _tag;
    }

    public int imageViewRes;
    public String title;
    public String code;
    public int tag;

    public String cls;
}
