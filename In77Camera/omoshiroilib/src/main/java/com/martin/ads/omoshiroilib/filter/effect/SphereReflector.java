package com.martin.ads.omoshiroilib.filter.effect;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.martin.ads.omoshiroilib.filter.base.PassThroughFilter;
import com.martin.ads.omoshiroilib.glessential.object.Sphere;
import com.martin.ads.omoshiroilib.glessential.program.GLPassThroughProgram;
import com.martin.ads.omoshiroilib.util.TextureUtils;


/**
 * Created by Ads on 2016/11/19.
 * SphereReflector (球面反射)
 */

public class SphereReflector extends PassThroughFilter {
    private Sphere sphere;
    private GLPassThroughProgram glSphereProgram;

    private float[] modelMatrix = new float[16];
    private float[] viewMatrix = new float[16];

    private float[] modelViewMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    private float ratio;

    public SphereReflector(Context context) {
        super(context);
        sphere=new Sphere(8,75,150);
        glSphereProgram =new GLPassThroughProgram(context);
        initMatrix();
    }

    @Override
    public void init() {
        super.init();
        glSphereProgram.create();
    }

    @Override
    public void destroy() {
        super.destroy();
        glSphereProgram.onDestroy();
    }

    @Override
    public void onDrawFrame(int textureId) {
        super.onDrawFrame(textureId);
        glSphereProgram.use();
        sphere.uploadTexCoordinateBuffer(glSphereProgram.getTextureCoordinateHandle());
        sphere.uploadVerticesBuffer(glSphereProgram.getPositionHandle());

        Matrix.perspectiveM(projectionMatrix, 0, 90, ratio, 1f, 500f);

        Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, projectionMatrix, 0, modelViewMatrix, 0);

        GLES20.glUniformMatrix4fv(glSphereProgram.getMVPMatrixHandle(), 1, false, mMVPMatrix, 0);

        TextureUtils.bindTexture2D(textureId, GLES20.GL_TEXTURE0,glSphereProgram.getTextureSamplerHandle(),0);

        sphere.draw();
    }

    @Override
    public void onFilterChanged(int width, int height) {
        super.onFilterChanged(width,height);
        ratio=(float)width/ height;
    }

    private void initMatrix() {
        Matrix.setIdentityM(modelMatrix,0);
        Matrix.rotateM(modelMatrix,0,90.0f,0f,1f,0f);
        Matrix.setIdentityM(projectionMatrix,0);
        Matrix.setIdentityM(viewMatrix, 0);
        Matrix.setLookAtM(viewMatrix, 0,
                0f,10f,10f,
                0.0f, 0.0f,-1.0f,
                0.0f, 1.0f, 0.0f);
    }

}
