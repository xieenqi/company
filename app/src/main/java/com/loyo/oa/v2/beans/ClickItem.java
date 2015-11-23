package com.loyo.oa.v2.beans;

/**
 * Created by pj on 15/6/29.
 */

public class ClickItem {
    public int imageViewRes;
    public String title;
    public Class<?> cls;

    public ClickItem(int _imageViewRes, String _title, Class<?> _cls) {
        imageViewRes = _imageViewRes;
        title = _title;
        cls = _cls;
    }
}