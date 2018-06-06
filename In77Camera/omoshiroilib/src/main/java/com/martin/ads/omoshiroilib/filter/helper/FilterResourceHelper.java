package com.martin.ads.omoshiroilib.filter.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.martin.ads.omoshiroilib.util.BitmapUtils;

import java.io.File;

/**
 * Created by Ads on 2017/2/13.
 */


public class FilterResourceHelper {
    private static final String TAG = "FilterResourceHelper";

    public static void logAllFilters(){
        for(FilterType filterType:FilterType.values()){
            Log.d(TAG, "logAllFilters: "+filterType.name().toLowerCase());
        }
    }

    private final static String[] ORIGIN_THUMBS={
            "filter/thumbs/origin_thumb_1.jpg",
            "filter/thumbs/origin_thumb_2.jpg",
            "filter/thumbs/origin_thumb_3.jpg",
            "filter/thumbs/origin_thumb_4.jpg",
            "filter/thumbs/origin_thumb_5.jpg",
            "filter/thumbs/origin_thumb_6.jpg",
    };

    private static String getThumbName(int x){
        int pos= (x/3)%ORIGIN_THUMBS.length;
        return ORIGIN_THUMBS[pos];
    }

    @Deprecated
    public static void generateFilterThumbs(Context context,boolean debugMode){
        File thumbFolderPath = null;
        if(debugMode)
            thumbFolderPath=new File(
                Environment.getExternalStorageDirectory(), "/Omoshiroi/thumbs");
        else thumbFolderPath=new File(context.getFilesDir().getAbsolutePath(),"thumbs");
        if (!thumbFolderPath.exists())
            thumbFolderPath.mkdirs();
        int tot=0;
        for(FilterType filterType:FilterType.values()){
            Bitmap original=BitmapUtils.loadBitmapFromAssets(context, getThumbName(tot));
            String fileName=filterType.name().toLowerCase()+".jpg";
            File outputFile=new File(thumbFolderPath.getAbsolutePath(),fileName);
            Log.d(TAG, "generateFilterThumbs: saving to "+outputFile.getAbsolutePath());
            BitmapUtils.saveBitmapWithFilterApplied(
                    context,
                    filterType,
                    original,
                    outputFile.getAbsolutePath(),
                    null
                    );
            tot++;
        }
        Toast.makeText(context,"Finished generating filter thumbs",Toast.LENGTH_LONG).show();
    }

    public static String getSimpleName(FilterType filterType){
        String ret= filterType.name().toLowerCase();
        ret=ret.replaceAll("filter","");
        if(ret.endsWith("_")) ret=ret.substring(0,ret.length()-1);
        return ret.toUpperCase();
    }

    public static Bitmap getFilterThumbFromFile(Context context, FilterType filterType){
        return BitmapUtils.loadBitmapFromFile(new File(context.getFilesDir().getAbsolutePath(),"thumbs").getAbsolutePath()+"/"+filterType.name().toLowerCase()+".jpg");
    }

    public static Bitmap getFilterThumbFromAssets(Context context,FilterType filterType){
        return BitmapUtils.loadBitmapFromAssets(context, "filter/thumbs/"+filterType.name().toLowerCase()+".jpg");
    }

    public static int getTotalFilterSize(){
        return FilterType.values().length;
    }

}
