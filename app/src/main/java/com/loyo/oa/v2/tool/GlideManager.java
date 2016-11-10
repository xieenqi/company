package com.loyo.oa.v2.tool;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

/**
 * Created by EthanGong on 2016/10/14.
 */
public class GlideManager {
    private static GlideManager ourInstance = new GlideManager();

    private Context context;
    public static GlideManager getInstance() {
        return ourInstance;
    }

    private GlideManager() {
    }

    public void initWithContext(Context context) {
        this.context = context;
    }

    public RequestManager manager() {
        return Glide.with(context);
    }
}
