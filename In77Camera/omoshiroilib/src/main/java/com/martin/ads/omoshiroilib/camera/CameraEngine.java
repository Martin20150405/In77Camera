package com.martin.ads.omoshiroilib.camera;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;

import com.martin.ads.omoshiroilib.debug.removeit.GlobalConfig;
import com.martin.ads.omoshiroilib.glessential.CameraView;
import com.martin.ads.omoshiroilib.glessential.GLRender;
import com.martin.ads.omoshiroilib.util.FileUtils;
import com.martin.ads.omoshiroilib.util.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Ads on 2017/1/26.
 */
@SuppressWarnings("deprecation")
public class CameraEngine
        implements SurfaceTexture.OnFrameAvailableListener,
        Camera.AutoFocusCallback, Camera.PreviewCallback {

    private static final String TAG = "CameraEngine";
    //this should be adjustable
    private static final double preferredRatio=16.0/9;
    private SurfaceTexture mSurfaceTexture;
    private CameraView.RenderCallback renderCallback;
    private Camera camera;
    private Camera.Parameters mParams;
    private boolean cameraOpened;

    private byte[]              mBuffer;

    //frameWidth=size.height
    private int frameWidth;
    private int frameHeight;

    private FakeMat[] mFrameChain;
    private int mChainIdx;
    private Thread mWorkerThread;
    private boolean mStopThread;
    private boolean mCameraFrameReady;

    private CameraView.PreviewSizeChangedCallback previewSizeChangedCallback;

    private MediaRecorder mMediaRecorder;
    private Camera.Size previewSize;
    private int currentCameraId;

    private double lastZoomValueRec;
    private int lastZoomValue;

    private GLRender glRender;

    public CameraEngine() {
        frameWidth=480; frameHeight=640;
        cameraOpened=false;

        mChainIdx = 0;
        mFrameChain=new FakeMat[2];
        mFrameChain[0]=new FakeMat();
        mFrameChain[1]=new FakeMat();
    }

    public void setTexture(int mTextureID){
        mSurfaceTexture = new SurfaceTexture(mTextureID);
        mSurfaceTexture.setOnFrameAvailableListener(this);
    }

    public long doTextureUpdate(float[] mSTMatrix){
        mSurfaceTexture.updateTexImage();
        mSurfaceTexture.getTransformMatrix(mSTMatrix);
        return mSurfaceTexture.getTimestamp();
    }

    public void openCamera(boolean facingFront) {
        synchronized (this) {
            int facing=facingFront? Camera.CameraInfo.CAMERA_FACING_FRONT:Camera.CameraInfo.CAMERA_FACING_BACK;
            currentCameraId=getCameraIdWithFacing(facing);
            camera = Camera.open(currentCameraId);
            camera.setPreviewCallbackWithBuffer(this);
            initRotateDegree(currentCameraId);
            if (camera != null) {
                mParams = camera.getParameters();
                List<Camera.Size> supportedPictureSizesList=mParams.getSupportedPictureSizes();
                List<Camera.Size> supportedVideoSizesList=mParams.getSupportedVideoSizes();
                List<Camera.Size> supportedPreviewSizesList=mParams.getSupportedPreviewSizes();
                Logger.logCameraSizes(supportedPictureSizesList);
                Logger.logCameraSizes(supportedVideoSizesList);
                Logger.logCameraSizes(supportedPreviewSizesList);

                previewSize=choosePreferredSize(supportedPreviewSizesList,preferredRatio);
                Camera.Size photoSize=choosePreferredSize(supportedPictureSizesList,preferredRatio);

                frameHeight=previewSize.width;
                frameWidth=previewSize.height;
                Log.d(TAG, "openCamera: choose preview size"+previewSize.height+"x"+previewSize.width);
                mParams.setPreviewSize(frameHeight,frameWidth);

                mParams.setPictureSize(photoSize.width,photoSize.height);
                Log.d(TAG, "openCamera: choose photo size"+photoSize.height+"x"+photoSize.width);

                //mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                int size = frameWidth*frameHeight;
                size = size * ImageFormat.getBitsPerPixel(mParams.getPreviewFormat()) / 8;
                if (mBuffer==null || mBuffer.length!=size)
                    mBuffer = new byte[size];
                mFrameChain[0].init(size);
                mFrameChain[1].init(size);
                camera.addCallbackBuffer(mBuffer);
                camera.setParameters(mParams);
                cameraOpened=true;
            }
        }
    }

    public void startPreview(){
        lastZoomValueRec=lastZoomValue=0;
        if(camera!=null){
            try {
                camera.setPreviewTexture(mSurfaceTexture);
            } catch (IOException e) {
                e.printStackTrace();
            }
            previewSizeChangedCallback.updatePreviewSize(frameWidth,frameHeight);
            camera.startPreview();

            mCameraFrameReady = false;
            mStopThread = false;
            mWorkerThread = new Thread(new CameraWorker());
            mWorkerThread.start();
        }
    }

    public void stopPreview(){
        synchronized (this) {
            if(camera!=null){
                mStopThread = true;
                synchronized (this) {
                    this.notify();
                }
                mWorkerThread =  null;
                camera.stopPreview();
            }
        }
    }

    public void releaseCamera() {
        synchronized (this) {
            if (camera != null) {
                camera.setPreviewCallback(null);
                camera.release();
                camera = null;
            }
            cameraOpened = false;
        }
    }

    public void switchCamera(boolean facingFront){
        stopPreview();
        releaseCamera();
        openCamera(facingFront);
        startPreview();
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        renderCallback.renderImmediately();
    }

    //Camera.CameraInfo.CAMERA_FACING_FRONT
    //Camera.CameraInfo.CAMERA_FACING_BACK
    public static int getCameraIdWithFacing(int facing){
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == facing) {
                return camIdx;
            }
        }
        return 0;
    }

    public static int getNumberOfCameras(){
        return Camera.getNumberOfCameras();
    }


    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        Log.d(TAG, "onPreviewFrame: ");
        synchronized (this) {
            mFrameChain[mChainIdx].putData(data);
            mCameraFrameReady = true;

            //glRender.runOnDraw(GLRender.CMD_PROCESS_FRAME, mFrameChain[mChainIdx].getFrame(), camera);

            camera.addCallbackBuffer(mBuffer);
            this.notify();
        }
    }


    private class CameraWorker implements Runnable {
        @Override
        public void run() {
            do {
                boolean hasFrame = false;
                synchronized (CameraEngine.this) {
                    try {
                        while (!mCameraFrameReady && !mStopThread) {
                            CameraEngine.this.wait();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (mCameraFrameReady) {
                        mChainIdx = 1 - mChainIdx;
                        mCameraFrameReady = false;
                        hasFrame = true;
                    }
                }

                if (!mStopThread && hasFrame) {
                   //processCameraFrame(frameHeight,frameWidth,mFrameChain[1 - mChainIdx].getFrame());
                }
            } while (!mStopThread);
            Log.d(TAG, "Finish processing thread");
        }
    }


    public void setRenderCallback(CameraView.RenderCallback renderCallback) {
        this.renderCallback = renderCallback;
    }

    public boolean isCameraOpened() {
        return cameraOpened;
    }

    public void focusCamera(){
        synchronized (this) {
            if (camera != null) {
                camera.cancelAutoFocus();
                camera.autoFocus(this);
            }
        }
    }

    public void requestOpenFlashLight(boolean isTorch){
        synchronized (this) {
            if (camera != null) {
                Camera.Parameters p = camera.getParameters();
                if(isTorch){
                    p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                }else p.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                camera.setParameters(p);
            }
        }
    }

    public void requestZoom(double zoomValue){
        Log.d(TAG, "requestZoom: "+zoomValue+" "+lastZoomValueRec+" "+lastZoomValue);
        synchronized (this) {
            if (camera != null) {
                Camera.Parameters p = camera.getParameters();
                if(p.isZoomSupported()){
                    lastZoomValueRec +=zoomValue;
                    lastZoomValueRec=Math.max(0,Math.min(lastZoomValueRec,1.0));
                    int curZoom= (int) (lastZoomValueRec*p.getMaxZoom());
                    if(Math.abs(curZoom-lastZoomValue)>=1){
                        lastZoomValue= curZoom;
                        p.setZoom(lastZoomValue);
                    }
                }else return;
                camera.setParameters(p);
            }
        }
    }

    public void requestCloseFlashLight(){
        synchronized (this) {
            if (camera != null) {
                Camera.Parameters p = camera.getParameters();
                p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                camera.setParameters(p);
            }
        }
    }

    public PreviewSize getPreviewSize(){
        return new PreviewSize(frameWidth,frameHeight);
    }

    public void setPreviewSize(PreviewSize previewSize){
        frameWidth=previewSize.width;
        frameHeight=previewSize.height;
    }

    public class PreviewSize{
        private int width,height;

        public PreviewSize(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }

    public void setPreviewSizeChangedCallback(CameraView.PreviewSizeChangedCallback previewSizeChangedCallback) {
        this.previewSizeChangedCallback = previewSizeChangedCallback;
    }

    private static Camera.Size choosePreferredSize(List<Camera.Size> sizes,double aspectRatio) {
        List<Camera.Size> options = new ArrayList<>();
        for (Camera.Size option : sizes) {
            if(option.width==1280 && option.height==720)
                return option;
            if (Math.abs((int)(option.height * aspectRatio)-option.width)<10) {
                options.add(option);
            }
        }
        if (options.size() > 0) {
            return Collections.max(options, new CompareSizesByArea());
        } else {
            return sizes.get(sizes.size()-1);
        }
    }

    static class CompareSizesByArea implements Comparator<Camera.Size> {
        @Override
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.width * lhs.height -
                    (long) rhs.width * rhs.height);
        }
    }

    public boolean startRecordingVideo() {
        if (prepareMediaRecorder()) {
            try {
                mMediaRecorder.start();

                return true;
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        return false;
    }

    private boolean prepareMediaRecorder() {
        try {
           // final Activity activity = getActivity();
            //if (null == activity) return false;
            //final BaseCaptureInterface captureInterface = (BaseCaptureInterface) activity;

           // setCameraDisplayOrientation(mCamera.getParameters());
            mMediaRecorder = new MediaRecorder();
            camera.stopPreview();
            camera.unlock();
            mMediaRecorder.setCamera(camera);

  //          boolean canUseAudio = true;
            //boolean audioEnabled = !mInterface.audioDisabled();
            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            //    canUseAudio = ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;

//            if (canUseAudio && audioEnabled) {
                mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
//            } else if (audioEnabled) {
//                Toast.makeText(getActivity(), R.string.mcam_no_audio_access, Toast.LENGTH_LONG).show();
//            }
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);

            final CamcorderProfile profile = CamcorderProfile.get(currentCameraId, CamcorderProfile.QUALITY_HIGH);
            mMediaRecorder.setOutputFormat(profile.fileFormat);
            mMediaRecorder.setVideoFrameRate(profile.videoFrameRate);
            mMediaRecorder.setVideoSize(previewSize.width, previewSize.height);
            mMediaRecorder.setVideoEncodingBitRate(profile.videoBitRate);
            mMediaRecorder.setVideoEncoder(profile.videoCodec);

            mMediaRecorder.setAudioEncodingBitRate(profile.audioBitRate);
            mMediaRecorder.setAudioChannels(profile.audioChannels);
            mMediaRecorder.setAudioSamplingRate(profile.audioSampleRate);
            mMediaRecorder.setAudioEncoder(profile.audioCodec);


            Uri uri = Uri.fromFile(FileUtils.makeTempFile(
                    new File(Environment.getExternalStorageDirectory(),
                            "/Omoshiroi/videos").getAbsolutePath(),
                    "VID_", ".mp4"));

            mMediaRecorder.setOutputFile(uri.getPath());

//            if (captureInterface.maxAllowedFileSize() > 0) {
//                mMediaRecorder.setMaxFileSize(captureInterface.maxAllowedFileSize());
//                mMediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
//                    @Override
//                    public void onInfo(MediaRecorder mediaRecorder, int what, int extra) {
//                        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED) {
//                            Toast.makeText(getActivity(), R.string.mcam_file_size_limit_reached, Toast.LENGTH_SHORT).show();
//                            stopRecordingVideo(false);
//                        }
//                    }
//                });
//            }

            mMediaRecorder.setOrientationHint(90);
     //       mMediaRecorder.setPreviewDisplay(mPreviewView.getHolder().getSurface());

            mMediaRecorder.prepare();
            return true;

        } catch (Exception e) {
            camera.lock();
            e.printStackTrace();
            return false;
        }
    }


    public final void releaseRecorder() {
        if (mMediaRecorder != null) {
            //if (mIsRecording) {
                try {
                    mMediaRecorder.stop();
                } catch (Throwable t) {
                    //noinspection ResultOfMethodCallIgnored
                    //new File(mOutputUri).delete();
                    t.printStackTrace();
                }
                //mIsRecording = false;
            //}
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            camera.startPreview();
        }
    }

    public SurfaceTexture getSurfaceTexture() {
        return mSurfaceTexture;
    }

    int displayRotate;
    void initRotateDegree(int cameraId) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        Log.d(TAG,"cameraId: " + cameraId + ", rotation: " + info.orientation);
        int rotation = ((Activity)GlobalConfig.context).getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        displayRotate = (info.orientation - degrees + 360) % 360;
    }

    public int getDisplayRotate() {
        return displayRotate;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setGlRender(GLRender glRender) {
        this.glRender = glRender;
    }
}
