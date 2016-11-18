package com.loyo.oa.v2.activityui.followup.model;

import android.widget.TextView;

import java.lang.ref.WeakReference;

/**
 * Created by loyo_dev1 on 16/11/17.
 */

public class AudioViewModel {

    public String url;
    public long length;
    private boolean isAnim = false;
    public WeakReference<TextView> imageViewWeakReference;
}
