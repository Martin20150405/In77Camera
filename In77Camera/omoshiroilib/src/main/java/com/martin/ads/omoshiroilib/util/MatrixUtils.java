package com.martin.ads.omoshiroilib.util;

import android.opengl.Matrix;

/**
 * Created by Ads on 2017/1/27.
 */

public class MatrixUtils {
    public static void updateProjectionFit(int imageWidth, int imageHeight, int surfaceWidth, int surfaceHeight, float[] projectionMatrix) {
        float screenRatio=(float)surfaceWidth/surfaceHeight;
        float videoRatio=(float) imageWidth / imageHeight;
        if (videoRatio>screenRatio){
            Matrix.orthoM(projectionMatrix,0,-1f,1f,-videoRatio/screenRatio,videoRatio/screenRatio,-1f,1f);
        }else Matrix.orthoM(projectionMatrix,0,-screenRatio/videoRatio,screenRatio/videoRatio,-1f,1f,-1f,1f);
    }

    public static void updateProjectionCrop(int imageWidth, int imageHeight,int surfaceWidth,int surfaceHeight,float[] projectionMatrix) {
        float screenRatio=(float)surfaceWidth/surfaceHeight;
        float videoRatio=(float) imageWidth / imageHeight;
        //crop is just making the screen fit the image.
        //only one difference
        if (videoRatio<screenRatio){
            Matrix.orthoM(projectionMatrix,0,-1f,1f,-videoRatio/screenRatio,videoRatio/screenRatio,-1f,1f);
        }else Matrix.orthoM(projectionMatrix,0,-screenRatio/videoRatio,screenRatio/videoRatio,-1f,1f,-1f,1f);
    }
}
