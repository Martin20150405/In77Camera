package com.martin.ads.omoshiroilib.camera;

/**
 * Created by Ads on 2017/1/30.
 */

public class FakeMat {
    private byte[] mFrame=null;

    public void init(int size) {
        if(mFrame==null || mFrame.length!=size)
            mFrame=new byte[size];
    }

    public void putData(byte[] data){
        if(mFrame!=null)
            System.arraycopy(data, 0, mFrame, 0, data.length);
    }

    public byte[] getFrame() {
        return mFrame;
    }
}
