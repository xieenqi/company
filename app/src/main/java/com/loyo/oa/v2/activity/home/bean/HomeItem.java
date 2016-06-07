package com.loyo.oa.v2.activity.home.bean;

/**
 * Created by yyy on 16/5/27.
 */
public class HomeItem {

    public int imageViewRes;
    public String title;
    public String code;
    public int    tag;
    public Class<?> cls;

    public HomeItem(final int _imageViewRes, final String _title, final Class<?> _cls, final String _code,final int _tag) {
        imageViewRes = _imageViewRes;
        title = _title;
        cls = _cls;
        code = _code;
        tag = _tag;
    }
}
