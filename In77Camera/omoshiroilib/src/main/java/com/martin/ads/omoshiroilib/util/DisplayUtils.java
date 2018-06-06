package com.martin.ads.omoshiroilib.util;

import android.content.Context;

/**
 * Created by Ads on 2017/5/29.
 */

public class DisplayUtils {
    public static int getRefLength(Context context,float len) {
        return (int) (context.getResources().getDisplayMetrics().density * len + 0.5F);
    }
}
