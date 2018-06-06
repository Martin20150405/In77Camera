package com.martin.ads.omoshiroilib.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.martin.ads.omoshiroilib.camera.IWorkerCallback;
import com.martin.ads.omoshiroilib.debug.removeit.GlobalConfig;
import com.martin.ads.omoshiroilib.filter.helper.FilterType;
import com.martin.ads.omoshiroilib.glessential.GLImageRender;
import com.martin.ads.omoshiroilib.debug.removeit.PixelBuffer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ads on 2016/11/8.
 */
public class BitmapUtils {
    private static final String TAG = "BitmapUtils";

    public static Bitmap getScreenShot(int width, int height){
        IntBuffer pixelBuffer = IntBuffer.allocate(width * height);
        GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE,
                pixelBuffer);
        int[] pixelMirroredArray = new int[width * height];
        int[] pixelArray = pixelBuffer.array();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                pixelMirroredArray[(height - i - 1) * width + j] = pixelArray[i * width + j];
            }
        }
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        return bmp;
    }

    public static void sendImage(int width, int height, Context context, FileUtils.FileSavedCallback fileSavedCallback) {
        final IntBuffer pixelBuffer = IntBuffer.allocate(width * height);

        //about 20-50ms
        long start = System.nanoTime();
        GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE,
                pixelBuffer);
        long end = System.nanoTime();

        Log.d(TAG, "glReadPixels time: " + (end - start)/1000000+" ms");

        //about 700-4000ms(png) 200-1000ms(jpeg)
        //use jpeg instead of png to save time
        //it will consume large memory and may take a long time, depends on the phone
        new SaveBitmapTask(pixelBuffer,width,height,context,fileSavedCallback).execute();
    }

    private static class SaveBitmapTask extends AsyncTask<Void, Integer, Boolean> {
        long start;

        IntBuffer rgbaBuf;
        int width, height;
        Context context;

        String filePath;
        FileUtils.FileSavedCallback fileSavedCallback;

        public SaveBitmapTask(IntBuffer rgbaBuf, int width, int height, Context context,FileUtils.FileSavedCallback fileSavedCallback) {
            this.rgbaBuf = rgbaBuf;
            this.width = width;
            this.height = height;
            this.context = context;
            File picFolder=GlobalConfig.context.getCacheDir();
            if (!picFolder.exists())
                picFolder.mkdirs();
            filePath= picFolder.getAbsolutePath()+FileUtils.getPicName();
            this.fileSavedCallback=fileSavedCallback;
        }

        @Override
        protected void onPreExecute() {
            start = System.nanoTime();
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            saveIntBufferAsBitmap(rgbaBuf, filePath , width, height);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            Log.d(TAG, "saveBitmap time: " + (System.nanoTime() - start)/1000000+" ms");
            super.onPostExecute(aBoolean);
            //Toast.makeText(context,"ScreenShot is saved to "+filePath, Toast.LENGTH_LONG).show();
            fileSavedCallback.onFileSaved(filePath);
        }
    }

    private static void saveIntBufferAsBitmap(IntBuffer buf, String filePath, int width, int height) {
        mkDirs(filePath);
        final int[] pixelMirroredArray = new int[width * height];
        Log.d(TAG, "Creating " + filePath);
        BufferedOutputStream bos = null;
        try {
            int[] pixelArray = buf.array();
            // rotate 180 deg with x axis because y is reversed
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    pixelMirroredArray[(height - i - 1) * width + j] = pixelArray[i * width + j];
                }
            }
            bos = new BufferedOutputStream(new FileOutputStream(filePath));
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bmp.copyPixelsFromBuffer(IntBuffer.wrap(pixelMirroredArray));
            bmp.compress(Bitmap.CompressFormat.JPEG, 90, bos);
            bmp.recycle();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void saveBitmap(final Bitmap bitmap, final String outputFilePath,final IWorkerCallback workerCallback) {
        mkDirs(outputFilePath);
        try {
            BufferedOutputStream bos = null;
            try {
                bos = new BufferedOutputStream(new FileOutputStream(outputFilePath));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bos);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bos != null) {
                    try {
                        bos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            if(workerCallback!=null)
                workerCallback.onPostExecute(null);
        } catch (final Exception e) {
                if(workerCallback!=null)
                    workerCallback.onPostExecute(e);
        }
    }

    public static void saveByteArray(final byte[] input, final String outputFilePath, final IWorkerCallback workerCallback,final Handler handler) {
        mkDirs(outputFilePath);
        FakeThreadUtils.postTask(new Runnable() {
            @Override
            public void run() {
                try {
                    FileOutputStream outputStream = new FileOutputStream(outputFilePath);
                    outputStream.write(input);
                    outputStream.flush();
                    outputStream.close();

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(workerCallback!=null)
                                workerCallback.onPostExecute(null);
                        }
                    });
                } catch (final Exception e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(workerCallback!=null)
                                workerCallback.onPostExecute(e);
                        }
                    });
                }
            }
        });
    }


    public static void saveBitmapWithFilterApplied(final Context context, final FilterType filterType,final Bitmap bitmap, final String outputFilePath, final IWorkerCallback workerCallback){
        FakeThreadUtils.postTask(new Runnable() {
            @Override
            public void run() {
                Logger.updateCurrentTime();
                GLImageRender imageRender=new GLImageRender(context,bitmap,filterType);
                PixelBuffer buffer = new PixelBuffer(bitmap.getWidth(), bitmap.getHeight());
                Logger.logPassedTime("new PixelBuffer");
                buffer.setRenderer(imageRender);
                Bitmap result = buffer.getBitmap();
                bitmap.recycle();
                Logger.logPassedTime("getBitmap");
                buffer.destroy();
                saveBitmap(result,outputFilePath,workerCallback);
                result.recycle();
                //FIXME: this sucks
                System.gc();
            }
        });

    }

    public static Bitmap loadBitmapFromFile(String filePath){
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inScaled=false;
        Bitmap bitmap= BitmapFactory.decodeFile(filePath);
        return bitmap;
    }

    public static Bitmap loadBitmapFromAssets(Context context,String filePath){
        InputStream inputStream = null;
        try {
            inputStream = context.getResources().getAssets().open(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(inputStream==null) return null;
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inScaled=false;
        Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
        return bitmap;
    }

    public static Bitmap loadBitmapFromRaw(Context context, int resourceId){
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inScaled=false;
        Bitmap bitmap= BitmapFactory.decodeResource(context.getResources(),resourceId,options);
        return bitmap;
    }

    public static void mkDirs(String fileName){
        File file=new File(fileName);
        if(!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
    }

    public static void savePNGBitmap(final Bitmap bitmap, final String outputFilePath) {
        mkDirs(outputFilePath);
        try {
            BufferedOutputStream bos = null;
            try {
                bos = new BufferedOutputStream(new FileOutputStream(outputFilePath));
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bos != null) {
                    try {
                        bos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap loadARGBBitmapFromRGBAByteArray(byte[] bytes,int width,int height,boolean ignoreAlphaChannel) {
        int totSize=width*height;
        if(bytes.length!=totSize*4) throw new RuntimeException("Illegal argument");
        int histogram[]=new int[totSize];
        for(int i=0,pos=0;i<totSize;i++,pos+=4){
            histogram[i]=0;
            histogram[i]|=(((int)bytes[pos+0])&0xff)<<16;
            histogram[i]|=(((int)bytes[pos+1])&0xff)<<8;
            histogram[i]|=(((int)bytes[pos+2])&0xff)<<0;
            histogram[i]|=ignoreAlphaChannel? 0xff000000:(((int)bytes[pos+3])&0xff)<<24;
        }
        return Bitmap.createBitmap(histogram, width,height, Bitmap.Config.ARGB_8888);
    }

}
