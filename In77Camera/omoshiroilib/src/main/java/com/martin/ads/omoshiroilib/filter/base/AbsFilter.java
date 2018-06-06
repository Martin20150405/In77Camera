package com.martin.ads.omoshiroilib.filter.base;

import android.opengl.GLES20;

import com.martin.ads.omoshiroilib.glessential.object.Plane;

import java.nio.FloatBuffer;
import java.util.LinkedList;

/**
 * Created by Ads on 2016/11/19.
 */

public abstract class AbsFilter {
    protected static final String TAG = "AbsFilter";
    private final LinkedList<Runnable> mPreDrawTaskList;
    private final LinkedList<Runnable> mPostDrawTaskList;
    protected int surfaceWidth,surfaceHeight;

    protected Plane plane;


    public AbsFilter() {
        mPreDrawTaskList = new LinkedList<Runnable>();
        mPostDrawTaskList = new LinkedList<Runnable>();
        plane =new Plane(true);
    }

    abstract public void init();

    public void onPreDrawElements(){
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
    }

    abstract public void destroy();

    public void onFilterChanged(int surfaceWidth, int surfaceHeight){
        this.surfaceWidth=surfaceWidth;
        this.surfaceHeight=surfaceHeight;
    }

    void setViewport(){
        GLES20.glViewport(0, 0, surfaceWidth, surfaceHeight);
    }

    abstract public void onDrawFrame(final int textureId);


    public void runPreDrawTasks() {
        while (!mPreDrawTaskList.isEmpty()) {
            mPreDrawTaskList.removeFirst().run();
        }
    }

    public void addPreDrawTask(final Runnable runnable) {
        synchronized (mPreDrawTaskList) {
            mPreDrawTaskList.addLast(runnable);
        }
    }

    public void runPostDrawTasks() {
        while (!mPostDrawTaskList.isEmpty()) {
            mPostDrawTaskList.removeFirst().run();
        }
    }

    public void addPostDrawTask(final Runnable runnable) {
        synchronized (mPostDrawTaskList) {
            mPostDrawTaskList.addLast(runnable);
        }
    }

    public void setUniform1i(final int programId, final String name, final int intValue) {
        int location=GLES20.glGetUniformLocation(programId,name);
        GLES20.glUniform1i(location,intValue);
    }

    public void setUniform1f(final int programId, final String name, final float floatValue) {
        int location=GLES20.glGetUniformLocation(programId,name);
        GLES20.glUniform1f(location,floatValue);
    }

    public void setUniform2fv(final int programId, final String name,final float[] arrayValue) {
        int location=GLES20.glGetUniformLocation(programId,name);
        GLES20.glUniform2fv(location, 1, FloatBuffer.wrap(arrayValue));
    }

    public int getSurfaceWidth() {
        return surfaceWidth;
    }

    public int getSurfaceHeight() {
        return surfaceHeight;
    }

    public Plane getPlane() {
        return plane;
    }

    //TODO:remove it
    public AbsFilter resetPlane(boolean inGroup){
        plane.resetTextureCoordinateBuffer(inGroup);
        return this;
    }
}
