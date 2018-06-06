package com.martin.ads.omoshiroilib.filter.base;

import android.opengl.GLES20;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ads on 2016/11/19.
 */

public class FilterGroup extends AbsFilter {
    private static final String TAG = "FilterGroup";
    protected FBO[] fboList;
    protected List<AbsFilter> filters;
    protected boolean isRunning;
    //if FilterGroup is in the middle
    //rebind fbo when draw the last filter
    private FBO fboToRebind;

    public FilterGroup() {
        super();
        filters=new ArrayList<AbsFilter>();
    }

    @Override
    public void init() {
        fboToRebind =null;
        for (AbsFilter filter : filters) {
            filter.init();
        }
        isRunning=true;
    }

    @Override
    public void destroy() {
        destroyFrameBuffers();
        for (AbsFilter filter : filters) {
            filter.destroy();
        }
        isRunning=false;
    }

    @Override
    public void onDrawFrame(int textureId) {
        runPreDrawTasks();
        if (fboList==null) {
            return ;
        }
        int size = filters.size();
        int previousTexture = textureId;
        for (int i = 0; i < size; i++) {
            AbsFilter filter = filters.get(i);
            Log.d(TAG, "onDrawFrame: "+i+" / "+size +" "+filter.getClass().getSimpleName()+" "+
                    filter.surfaceWidth+" "+filter.surfaceHeight);
            if (i < size - 1) {
                filter.setViewport();
                fboList[i].bind();
                GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
                if(filter instanceof FilterGroup)
                    ((FilterGroup) filter).setFboToRebind(fboList[i]);
                filter.onDrawFrame(previousTexture);
                fboList[i].unbind();
                previousTexture = fboList[i].getFrameBufferTextureId();
            }else{
                if(fboToRebind!=null)
                    fboToRebind.bind();
                filter.setViewport();
                filter.onDrawFrame(previousTexture);
            }
        }
        runPostDrawTasks();
    }

    @Override
    public void onFilterChanged(int surfaceWidth, int surfaceHeight) {
        super.onFilterChanged(surfaceWidth, surfaceHeight);
        int size = filters.size();
        for (int i = 0; i < size; i++) {
            filters.get(i).onFilterChanged(surfaceWidth, surfaceHeight);
        }
        if(fboList != null){
            destroyFrameBuffers();
            fboList=null;
        }
        if (fboList == null) {
            fboList = new FBO[size-1];
            for (int i = 0; i < size-1; i++) {
                AbsFilter filter=filters.get(i);
                fboList[i]=FBO.newInstance().create(filter.getSurfaceWidth(),filter.getSurfaceHeight());
            }
        }
    }

    private void destroyFrameBuffers() {
        fboToRebind =null;
        for(FBO fbo:fboList)
            fbo.destroy();
    }

    public void setFboToRebind(FBO fboToBind) {
        this.fboToRebind = fboToBind;
    }

    public void addFilter(final AbsFilter filter){
        if (filter==null) return;
        //If one filter is added multiple times,
        //it will execute the same times
        //BTW: Pay attention to the order of execution
        if (!isRunning){
            filters.add(filter);
        }
        else
            addPreDrawTask(new Runnable() {
            @Override
            public void run() {
                filter.init();
                filters.add(filter);
                onFilterChanged(surfaceWidth,surfaceHeight);
            }
        });
    }

    public void addFilterList(final List<AbsFilter> filterList){
        if (filterList==null) return;
        //If one filter is added multiple times,
        //it will execute the same times
        //BTW: Pay attention to the order of execution
        if (!isRunning){
            for(AbsFilter filter:filterList){
                filters.add(filter);
            }
        }
        else
            addPreDrawTask(new Runnable() {
                @Override
                public void run() {
                    for(AbsFilter filter:filterList){
                        filter.init();
                        filters.add(filter);
                    }
                    onFilterChanged(surfaceWidth,surfaceHeight);
                }
            });
    }

    public void switchLastFilter(final AbsFilter filter){
        if (filter==null) return;
        Log.d(TAG, "onFilterChanged: "+filter.getClass().getSimpleName());
        if (!isRunning){
            if(filters.size()>0) {
                filters.remove(filters.size()-1)
                        .destroy();
            }
            filters.add(filter);
        }
        else
            addPreDrawTask(new Runnable() {
                @Override
                public void run() {
                    if(filters.size()>0) {
                        filters.remove(filters.size()-1)
                                .destroy();
                    }
                    filter.init();
                    filters.add(filter);
                    onFilterChanged(surfaceWidth,surfaceHeight);
                }
            });
    }

    public void switchFilterAt(final AbsFilter filter,final int pos){
        if (filter==null || pos>=filters.size()) return;
        Log.d(TAG, "onFilterChanged: "+filter.getClass().getSimpleName());
        addPreDrawTask(new Runnable() {
            @Override
            public void run() {
                filter.init();
                List<AbsFilter> tmpList=new ArrayList<AbsFilter>();
                for(int i=0;i<filters.size();i++){
                    AbsFilter curFilter=filters.get(i);
                    if(i==pos) {
                        tmpList.add(filter);
                        curFilter.destroy();
                    }
                    else tmpList.add(curFilter);
                }
                filters.clear();
                for(AbsFilter curFilter:tmpList)
                    filters.add(curFilter);
                onFilterChanged(surfaceWidth,surfaceHeight);
            }
        });
    }
}
