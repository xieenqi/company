package com.loyo.oa.photo;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.support.annotation.NonNull;

/**
 * Created by EthanGong on 2017/1/4.
 */

public class PhotoCapture {
    public final static int REQUEST_CODE = 555;

    public static PhotoCapture.PhotoCaptureBuilder builder() {
        return new PhotoCapture.PhotoCaptureBuilder();
    }

    public static class PhotoCaptureBuilder {


        public void start(@NonNull Activity activity) {
            start(activity, REQUEST_CODE);
        }

        public void start(@NonNull Fragment fragment) {
            start(fragment.getActivity(), REQUEST_CODE);
        }

        public void start(@NonNull Activity activity, int requestCode) {
            Intent intent = new Intent();
            intent.setClass(activity, PhotoCaptureActivity.class);
            activity.startActivityForResult(intent, requestCode);
        }

        public PhotoCaptureBuilder() {
        }
    }
}
