package com.martin.ads.omoshiroilib.filter.effect.xiuxiuxiu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.martin.ads.omoshiroilib.filter.base.MultipleTextureFilter;
import com.martin.ads.omoshiroilib.util.BitmapUtils;
import com.martin.ads.omoshiroilib.util.FileUtils;
import com.martin.ads.omoshiroilib.util.ShaderUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Vector;

/**
 * Created by Ads on 2017/4/6.
 */

public class XiuXiuXiuAbsFilter extends MultipleTextureFilter {
    public static final boolean DUMP_DATA=false;
    private ByteBuffer mDataBuffer;
    private Vector<BitmapFileDescription> bitmapFileDescriptions;

    public XiuXiuXiuAbsFilter(Context context,
                              String fragmentShaderPath,
                              String filterResourceIndexPath,
                              String filterResourceBinaryPackPath) {
        super(context, fragmentShaderPath);
        readData(filterResourceIndexPath,filterResourceBinaryPackPath);
    }

    @Override
    public void init() {
        super.init();
        for(int i=0;i<textureSize;i++){
            BitmapFileDescription bitmapFileDescription=bitmapFileDescriptions.get(i);
            Bitmap bitmap=BitmapFactory.decodeByteArray(
                            mDataBuffer.array(),
                            mDataBuffer.arrayOffset() + bitmapFileDescription.startPos,
                            bitmapFileDescription.endPos);
            if(DUMP_DATA){
                File outputFile=new File(Environment.getExternalStorageDirectory(),
                        "/Omoshiroi/DumpedData/"+bitmapFileDescription.name);
                Log.d(TAG, "init: "+outputFile.getAbsolutePath());
                if (!outputFile.getParentFile().exists())
                    outputFile.getParentFile().mkdirs();
                BitmapUtils.savePNGBitmap(bitmap,
                        outputFile.getAbsolutePath()
                );
            }
            externalBitmapTextures[i].loadBitmap(bitmap);
        }
    }

    private void readData(String filterResourceIndexPath,
                          String filterResourceBinaryPackPath){
        bitmapFileDescriptions=new Vector<>();
        String fileName="tempFile."+System.currentTimeMillis();
        FileUtils.copyFileFromAssets(context,
                context.getCacheDir().getAbsolutePath(),
                fileName,
                filterResourceBinaryPackPath);
        File dataFile = new File(context.getCacheDir().getAbsolutePath(),fileName);
        //Log.d(TAG, "readData: "+dataFile.getAbsolutePath()+" "+dataFile.exists());
        if(!dataFile.exists()) return;

        String fileIndexContent= ShaderUtils.readAssetsTextFile(context,filterResourceIndexPath);
        //Log.d(TAG, "readData: "+fileIndexContent);

        String[] bitmapFileDescriptionStr = fileIndexContent.split(";");
        for (int i = 0; i < bitmapFileDescriptionStr.length; i++){
            String[] bitmapFileDescriptionStrSplit = bitmapFileDescriptionStr[i].split(":");
            if (bitmapFileDescriptionStrSplit.length == 3) {
                int startPos = Integer.parseInt(bitmapFileDescriptionStrSplit[1]);
                int endPos = Integer.parseInt(bitmapFileDescriptionStrSplit[2]);
                bitmapFileDescriptions.add(new BitmapFileDescription(bitmapFileDescriptionStrSplit[0],startPos,endPos));
            }
        }
//        for(BitmapFileDescription b:bitmapFileDescriptions){
//            Log.d(TAG, "readData: "+b.toString());
//        }

//        Log.d(TAG, "readData: "+dataFile.length());
        mDataBuffer=ByteBuffer.allocateDirect((int)dataFile.length());
        byte[] buf = new byte[512];
        try {
            int len;
            FileInputStream localFileInputStream = new FileInputStream(dataFile);
            while((len=localFileInputStream.read(buf))!=-1){
                mDataBuffer.put(buf,0,len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        textureSize=bitmapFileDescriptions.size();
        dataFile.delete();
    }

    private class BitmapFileDescription{
        String name;
        int startPos;
        int endPos;

        public BitmapFileDescription(String name, int startPos, int endPos) {
            this.name = name;
            this.startPos = startPos;
            this.endPos = endPos;
        }

        public String toString(){
            return "name: "+name+" startPos: "+startPos+" endPos: "+endPos;
        }
    }
}
