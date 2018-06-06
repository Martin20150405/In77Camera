package com.martin.ads.omoshiroilib.util;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;

/**
 * Created by Ads on 2017/5/30.
 */

public class AnimationUtils {
    public static void displayAnim(View view, Context context, int animId, int targetVisibility){
        view.clearAnimation();
        Animation anim =
                android.view.animation.AnimationUtils.loadAnimation(context, animId);
        view.setVisibility(targetVisibility);
        view.startAnimation(anim);
    }
}
