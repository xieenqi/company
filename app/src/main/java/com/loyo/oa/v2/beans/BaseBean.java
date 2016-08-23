package com.loyo.oa.v2.beans;

import java.io.Serializable;

/**
 * 所有 bean 的 父类
 * Created by xeq on 16/8/22.
 */
public class BaseBean <T> implements Serializable {
    public int errcode;
    public String errmsg;
}
