package com.martin.ads.omoshiroilib.filter.effect.xiuxiuxiu;

import android.content.Context;
import android.util.Log;

/**
 * Created by Ads on 2017/4/6.
 */

public class XiuXiuXiuFilterWrapper extends XiuXiuXiuAbsFilter {
    public XiuXiuXiuFilterWrapper(Context context,String FILTER_NAME) {
        super(context,
                "filter/fsh/xiuxiuxiu/"+FILTER_NAME.toLowerCase()+".glsl",
                "filter/textures/xiuxiuxiu/"+FILTER_NAME.toLowerCase()+".idx",
                "filter/textures/xiuxiuxiu/"+FILTER_NAME.toLowerCase()+".dat"
        );
        Log.d(TAG, "XiuXiuXiuFilterWrapper: "+FILTER_NAME);
    }
}
