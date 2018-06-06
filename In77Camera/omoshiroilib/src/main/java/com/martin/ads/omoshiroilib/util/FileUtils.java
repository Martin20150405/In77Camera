package com.martin.ads.omoshiroilib.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.martin.ads.omoshiroilib.debug.removeit.GlobalConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by Ads on 2016/11/28.
 */

public class FileUtils {
    private static final String TAG = "FileUtils";
    public static void copyFileFromAssets(Context context, String outputPath, String fileName, String inputPath){
        File file = new File(outputPath, fileName);
        Log.d(TAG, "copyFileFromAssets: "+file.getAbsolutePath());
        try
        {
            if (!file.exists()) {
                File fileParentDir = file.getParentFile();
                if (!fileParentDir.exists()) {
                    fileParentDir.mkdirs();
                }
                file.createNewFile();
            }else return;
            InputStream in = context.getResources().getAssets().open(inputPath);
            OutputStream out = new FileOutputStream(file);
            byte buffer[] = new byte[1024];
            int realLength;
            while ((realLength = in.read(buffer)) > 0) {
                out.write(buffer, 0, realLength);
            }
            in.close();
            out.close();
        }catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void copyFileFromTo(String outputPath, String fileName, String inputPath){
        File file = new File(outputPath, fileName);
        Log.d(TAG, "copyFileFromTo: "+file.getAbsolutePath());
        try {
            if (!file.exists()) {
                File fileParentDir = file.getParentFile();
                if (!fileParentDir.exists()) {
                    fileParentDir.mkdirs();
                }
                file.createNewFile();
            }else return;
            InputStream in = new FileInputStream(new File(inputPath,fileName));
            OutputStream out = new FileOutputStream(file);
            byte buffer[] = new byte[1024];
            int realLength;
            while ((realLength = in.read(buffer)) > 0) {
                out.write(buffer, 0, realLength);
            }
            in.close();
            out.close();
        }catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static File makeTempFile(String saveDir, String prefix, String extension) {
        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        final File dir = new File(saveDir);
        dir.mkdirs();
        return new File(dir, prefix + timeStamp + extension);
    }

    public static void upZipFile(Context context,String assetPath,String outputFolderPath){
        File desDir = new File(outputFolderPath);
        if (!desDir.isDirectory()) {
            desDir.mkdirs();
        }
        try {
            InputStream inputStream = context.getResources().getAssets().open(assetPath);
            ZipInputStream zipInputStream=new ZipInputStream(inputStream);
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                Log.d(TAG, "upZipFile: "+zipEntry.getName());
                if(zipEntry.isDirectory()) {
                    File tmpFile=new File(outputFolderPath,zipEntry.getName());
                    //Log.d(TAG, "upZipFile: folder "+tmpFile.getAbsolutePath());
                    if(!tmpFile.isDirectory())
                        tmpFile.mkdirs();
                } else {
                    File desFile = new File(outputFolderPath +"/"+ zipEntry.getName());
                    if(desFile.exists()) continue;
                    OutputStream out = new FileOutputStream(desFile);
                    //Log.d(TAG, "upZipFile: "+desFile.getAbsolutePath());
                    byte buffer[] = new byte[1024];
                    int realLength;
                    while ((realLength = zipInputStream.read(buffer)) > 0) {
                        out.write(buffer, 0, realLength);
                    }
                    zipInputStream.closeEntry();
                    out.close();
                }
            }
            zipInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File getFileOnSDCard(String path){
        File sdRoot = Environment.getExternalStorageDirectory();
        return new File(sdRoot,path);
    }

    public static String getPicName(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String filename="/Pic_" + simpleDateFormat.format(new Date())+".jpg";
        return filename;
    }

    public static String getVidName(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String filename="/Vid_" + simpleDateFormat.format(new Date())+".mp4";
        return filename;
    }

    public interface FileSavedCallback{
        void onFileSaved(String filePath);
    }
}

