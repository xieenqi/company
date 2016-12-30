/*
 *    Copyright 2015 Kaopiz Software Co., Ltd.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.loyo.oa.hud.progress;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

class Helper {

    private static float scale;
    private static Handler handler = new Handler(Looper.getMainLooper());

    public static int dpToPixel(float dp, Context context) {
        if (scale == 0) {
            scale = context.getResources().getDisplayMetrics().density;
        }
        return (int) (dp * scale);
    }

    public static void dispatchDelayed(Runnable runnable, int milli) {
        handler.postDelayed(runnable, milli);
    }

    public static void dispatch(Runnable runnable) {
        handler.post(runnable);
    }
}
