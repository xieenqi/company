package com.loyo.oa.v2.tool;

import java.io.Serializable;

/**
 * Created by EthanGong on 2016/11/14.
 */

public class ImageInfo implements Serializable {
    public String path;

    public ImageInfo(String path) {
        this.path = path;
    }
}
