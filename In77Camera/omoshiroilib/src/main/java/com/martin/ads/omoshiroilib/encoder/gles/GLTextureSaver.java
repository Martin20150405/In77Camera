package com.martin.ads.omoshiroilib.encoder.gles;

import android.content.Context;

import com.martin.ads.omoshiroilib.constant.GLEtc;
import com.martin.ads.omoshiroilib.filter.base.PassThroughFilter;

/**
 * Created by Ads on 2017/5/31.
 */

public class GLTextureSaver extends PassThroughFilter {

    private int savedTextureId;
    private int lastTextureId;
    private FrameAvailableCallback frameAvailableCallback;

    public GLTextureSaver(Context context) {
        super(context);
        frameAvailableCallback=null;
        savedTextureId= GLEtc.NO_TEXTURE;
    }

    @Override
    public void onDrawFrame(int textureId) {
        super.onDrawFrame(textureId);
        lastTextureId=textureId;
        if(frameAvailableCallback!=null)
            frameAvailableCallback.onFrameAvailable(textureId);
    }

    public void setSavedTextureId(int savedTextureId) {
        this.savedTextureId = savedTextureId;
    }

    public int getSavedTextureId() {
        return savedTextureId;
    }

    public int getLastTextureId() {
        return lastTextureId;
    }

    public void setFrameAvailableCallback(FrameAvailableCallback frameAvailableCallback) {
        this.frameAvailableCallback = frameAvailableCallback;
    }

    public interface FrameAvailableCallback{
        void onFrameAvailable(int textureId);
    }
}
