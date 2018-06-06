package com.martin.ads.omoshiroilib.filter.base;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.martin.ads.omoshiroilib.glessential.program.GL2DSTProgram;
import com.martin.ads.omoshiroilib.glessential.program.GLOESProgram;
import com.martin.ads.omoshiroilib.util.BufferUtils;
import com.martin.ads.omoshiroilib.util.TextureUtils;

/**
 * Created by Ads on 2017/01/26.
 */

public class Rotate2DFilter extends AbsFilter{
    private static final String TAG = "RotateFilter";
    private GL2DSTProgram gl2DSTProgram;

    //Rotate 90 CW
    public static final float TRIANGLES_DATA_CAMERA[] = {
            -1.0f, 1.0f, 0f,
            -1.0f, -1.0f, 0f,
            1.0f, 1.0f, 0f,
            1.0f, -1.0f, 0f
    };

    //videoTextureMatrix
    private float[] mSTMatrix = new float[16];

    public Rotate2DFilter(Context context) {
        super();
        plane.setVerticesBuffer(BufferUtils.getFloatBuffer(TRIANGLES_DATA_CAMERA,0));
        gl2DSTProgram =new GL2DSTProgram(context);
        Matrix.setIdentityM(mSTMatrix, 0);
    }

    @Override
    public void init() {
        gl2DSTProgram.create();
    }

    @Override
    public void destroy() {
        gl2DSTProgram.onDestroy();
    }

    @Override
    public void onDrawFrame(int textureId) {
        gl2DSTProgram.use();
        plane.uploadTexCoordinateBuffer(gl2DSTProgram.getTextureCoordinateHandle());
        plane.uploadVerticesBuffer(gl2DSTProgram.getPositionHandle());
        GLES20.glUniformMatrix4fv(gl2DSTProgram.getMuSTMatrixHandle(), 1, false, mSTMatrix, 0);
        TextureUtils.bindTexture2D(textureId, GLES20.GL_TEXTURE0, gl2DSTProgram.getUTextureSamplerHandle(),0);
        GLES20.glViewport(0,0,surfaceWidth,surfaceHeight);
        plane.draw();
    }

    public float[] getSTMatrix() {
        return mSTMatrix;
    }
}
