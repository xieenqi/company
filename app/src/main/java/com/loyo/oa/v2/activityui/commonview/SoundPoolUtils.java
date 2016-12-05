package com.loyo.oa.v2.activityui.commonview;

import android.media.AudioManager;
import android.media.SoundPool;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;

/**
 * Created by xeq on 16/12/5.
 */

public class SoundPoolUtils {

    private static SoundPoolUtils instanc;
    private static SoundPool sp;
    private int shosho;

    public static SoundPoolUtils getInstanc() {
        if (instanc == null)
            instanc = new SoundPoolUtils();
        if (sp == null)
            sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        return instanc;
    }

    public void initRecordSengSuccess() {
        shosho = sp.load(MainApp.getMainApp(), R.raw.shosho, 1);
    }

    public void playRecordSengSuccess() {
        if (shosho != 0)
            sp.play(shosho, 1f, 1f, 0, 0, 0.8f);
    }

}
