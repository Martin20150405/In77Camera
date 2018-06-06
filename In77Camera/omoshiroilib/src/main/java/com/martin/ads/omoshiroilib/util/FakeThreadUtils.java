package com.martin.ads.omoshiroilib.util;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;

/**
 * Created by Ads on 2017/2/13.
 */

public class FakeThreadUtils {
    private static final String TAG = "FakeThreadUtils";
    public static void postTask(Runnable runnable) {
        new Thread(runnable).start();
    }

    public static class SaveFileTask extends AsyncTask<Void, Integer, Boolean> {
        private String outputPath,fileName,inputPath;
        private FileUtils.FileSavedCallback fileSavedCallback;

        public SaveFileTask(String outputPath, String fileName, String inputPath, FileUtils.FileSavedCallback fileSavedCallback) {
            this.outputPath = outputPath;
            this.fileName = fileName;
            this.inputPath = inputPath;
            this.fileSavedCallback = fileSavedCallback;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            FileUtils.copyFileFromTo(
                    outputPath,fileName,inputPath
            );
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            Log.d(TAG, "onPostExecute: "+outputPath+" "+fileName+" "+inputPath);
            //Toast.makeText(context,"ScreenShot is saved to "+filePath, Toast.LENGTH_LONG).show();
            fileSavedCallback.onFileSaved(new File(outputPath,fileName).getAbsolutePath());
        }
    }
}
