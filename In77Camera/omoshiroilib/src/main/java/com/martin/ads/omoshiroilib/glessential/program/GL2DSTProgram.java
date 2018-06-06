package com.martin.ads.omoshiroilib.glessential.program;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import com.martin.ads.omoshiroilib.util.ShaderUtils;


/**
 * Created by Ads on 2016/11/8.
 * Translate YUV420SP(NV21) to RGBA
 * it may also work with YUV420P(YV12)
 * with STM/OES
 */
public class GL2DSTProgram extends GLAbsProgram{
    private static final String TAG = "GL2DSTProgram";
    private int muSTMatrixHandle;
    private int uTextureSamplerHandle;

    public GL2DSTProgram(Context context){
        super(context, "filter/vsh/base/oes.glsl", "filter/fsh/base/pass_through.glsl");
    }

    @Override
    public void create(){
        super.create();
        muSTMatrixHandle = GLES20.glGetUniformLocation(getProgramId(), "uSTMatrix");
        ShaderUtils.checkGlError("glGetUniformLocation uSTMatrix");
        if (muSTMatrixHandle == -1) {
            throw new RuntimeException("Could not get attrib location for uSTMatrix");
        }

        uTextureSamplerHandle= GLES20.glGetUniformLocation(getProgramId(),"sTexture");
        ShaderUtils.checkGlError("glGetUniformLocation uniform samplerExternalOES sTexture");
    }

    public int getMuSTMatrixHandle() {
        return muSTMatrixHandle;
    }

    public int getUTextureSamplerHandle() { return uTextureSamplerHandle; }
}
