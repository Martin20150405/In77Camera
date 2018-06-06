package com.martin.ads.omoshiroilib.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import com.martin.ads.omoshiroilib.constant.GLEtc;

import java.nio.ByteBuffer;

/**
 * Created by Ads on 2016/11/19.
 */

public class TextureUtils{
    private static final String TAG = "TextureUtils";

    public static void bindTexture2D(int textureId,int activeTextureID,int handle,int idx){
        if (textureId != GLEtc.NO_TEXTURE) {
            GLES20.glActiveTexture(activeTextureID);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
            GLES20.glUniform1i(handle, idx);
        }
    }

    public static void bindTextureOES(int textureId,int activeTextureID,int handle,int idx){
        if (textureId != GLEtc.NO_TEXTURE) {
            GLES20.glActiveTexture(activeTextureID);
            GLES20.glBindTexture(GLEtc.GL_TEXTURE_EXTERNAL_OES, textureId);
            GLES20.glUniform1i(handle, idx);
        }
    }

    public static int loadTextureFromResources(Context context, int resourceId,int imageSize[]){
        return getTextureFromBitmap(
                BitmapUtils.loadBitmapFromRaw(context,resourceId),
                imageSize);
    }

    public static int loadTextureFromAssets(Context context, String filePath,int imageSize[]){
        return getTextureFromBitmap(
                BitmapUtils.loadBitmapFromAssets(context,filePath),
                imageSize);
    }

    public static int getTextureFromBitmap(Bitmap bitmap,int imageSize[]){
        final int[] textureObjectIds=new int[1];
        GLES20.glGenTextures(1,textureObjectIds,0);
        if (textureObjectIds[0]==0){
            Log.d(TAG,"Failed at glGenTextures");
            return 0;
        }

        if (bitmap==null){
            Log.d(TAG,"Failed at decoding bitmap");
            GLES20.glDeleteTextures(1,textureObjectIds,0);
            return 0;
        }

        if(imageSize!=null && imageSize.length>=2){
            imageSize[0]=bitmap.getWidth();
            imageSize[1]=bitmap.getHeight();
        }

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureObjectIds[0]);

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D,0,bitmap,0);
        bitmap.recycle();

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,0);
        return textureObjectIds[0];
    }


    public static int loadTextureWithOldTexId(final Bitmap img, final int usedTexId) {
        int textures[] = new int[1];
        if (usedTexId == GLEtc.NO_TEXTURE) {
            return getTextureFromBitmap(img,null);
        } else {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, usedTexId);
            GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, img);
            textures[0] = usedTexId;
        }
        return textures[0];
    }

    public static int getTextureFromByteArray(byte[] bytes,int width,int height){
        if(bytes.length!=width*height*4) throw new RuntimeException("Illegal byte array");
        return getTextureFromByteBuffer(ByteBuffer.wrap(bytes),width,height);
    }

    public static int getTextureFromByteArrayWithOldTexId(byte[] bytes,int width,int height,int usedTexId){
        if(bytes.length!=width*height*4) throw new RuntimeException("Illegal byte array");
        return getTextureFromByteBufferWithOldTexId(ByteBuffer.wrap(bytes),width,height,usedTexId);
    }

    public static int getTextureFromByteBuffer(ByteBuffer byteBuffer,int width,int height){
        if(byteBuffer.array().length!=width*height*4) throw new RuntimeException("Illegal byte array");
        final int[] textureObjectIds=new int[1];
        GLES20.glGenTextures(1,textureObjectIds,0);
        if (textureObjectIds[0]==0){
            Log.d(TAG,"Failed at glGenTextures");
            return 0;
        }

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureObjectIds[0]);

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA,
                width,height, 0,
                GLES20.GL_RGBA,
                GLES20.GL_UNSIGNED_BYTE,
                byteBuffer);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,0);
        return textureObjectIds[0];
    }

    public static int getTextureFromByteBufferWithOldTexId(ByteBuffer byteBuffer,int width,int height,int usedTexId){
        if(byteBuffer.array().length!=width*height*4) throw new RuntimeException("Illegal byte array");
        int textures[] = new int[1];
        if (usedTexId == GLEtc.NO_TEXTURE) {
            return getTextureFromByteBuffer(byteBuffer,width,height);
        } else {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, usedTexId);
            GLES20.glTexSubImage2D(GLES20.GL_TEXTURE_2D,0,0,0,
                    width,height,
                    GLES20.GL_RGBA,
                    GLES20.GL_UNSIGNED_BYTE,
                    byteBuffer
            );
            textures[0] = usedTexId;
        }
        return textures[0];
    }
}
