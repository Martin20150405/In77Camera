package com.martin.ads.omoshiroilib.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.martin.ads.omoshiroilib.R;
import com.martin.ads.omoshiroilib.constant.MimeType;
import com.martin.ads.omoshiroilib.debug.removeit.GlobalConfig;
import com.martin.ads.omoshiroilib.ui.anim.RotateLoading;
import com.martin.ads.omoshiroilib.ui.module.EffectsButton;
import com.martin.ads.omoshiroilib.util.AnimationUtils;
import com.martin.ads.omoshiroilib.util.BitmapUtils;
import com.martin.ads.omoshiroilib.util.FakeThreadUtils;
import com.martin.ads.omoshiroilib.util.FileUtils;
import com.sdsmdg.tastytoast.TastyToast;

import java.io.File;

/**
 * Created by Ads on 2017/5/30.
 */

//FIXME:change it to save to temp dir
public class DecorateActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener{
    private static final String TAG = "DecorateActivity";
    public static final String SAVED_MEDIA_FILE="saved_media_file";
    public static final String SAVED_MEDIA_TYPE="saved_media_type";
    public static final String RAW_MEDIA="raw_media";
    private RelativeLayout decorateTool;

    private String filePath;
    private int mediaType;

    private ImageView imagePreview;
    //private VideoView videoPreview;
    //private IjkWrapper ijkWrapper = new IjkWrapper();
    private RotateLoading rotateLoading;
    private File mediaFile;
    private String desFolder;
    private String outputFilePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ijkWrapper.init();
        init();
    }

    private void saveFile(final Runnable callback){
        rotateLoading.start();
        outputFilePath=new File(desFolder,
                mediaFile.getName()).getAbsolutePath();
        new FakeThreadUtils.SaveFileTask(
                desFolder,
                mediaFile.getName(),
                mediaFile.getParent(),
                new FileUtils.FileSavedCallback() {
                    @Override
                    public void onFileSaved(String filePath) {
                        rotateLoading.stop();
                        callback.run();
                    }
                }
        ).execute();
    }

    private void init() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().hide();
        setContentView(R.layout.frag_decorate_picture);
        rotateLoading= (RotateLoading) findViewById(R.id.rotate_loading);
        imagePreview= (ImageView) findViewById(R.id.img_preview);
        //videoPreview= (VideoView) findViewById(R.id.video_preview);
        decorateTool= (RelativeLayout) findViewById(R.id.rl_frag_decorate_tool);
        filePath=getIntent().getStringExtra(SAVED_MEDIA_FILE);
        mediaType=getIntent().getIntExtra(SAVED_MEDIA_TYPE,-1);
        mediaFile=new File(filePath);

        desFolder=
                mediaType==MimeType.PHOTO?
                        FileUtils.getFileOnSDCard(GlobalConfig.In77Camera_PHOTO_PATH).getAbsolutePath():
                        FileUtils.getFileOnSDCard(GlobalConfig.In77Camera_VIDEO_PATH).getAbsolutePath();

        if(mediaType<0) finish();

        TextureView textureView = (TextureView) findViewById(R.id.video_view);
        textureView.setSurfaceTextureListener(this);

        if(mediaType== MimeType.PHOTO){
            //videoPreview.setVisibility(View.GONE);
            textureView.setVisibility(View.VISIBLE);
            imagePreview.setImageBitmap(BitmapUtils.loadBitmapFromFile(filePath));
        }else if(mediaType== MimeType.VIDEO){
            imagePreview.setVisibility(View.GONE);
            textureView.setVisibility(View.VISIBLE);
            //videoPreview.setVisibility(View.VISIBLE);
//            ijkWrapper.openRemoteFile(filePath);
//            ijkWrapper.prepare();
//            videoPreview.setVideoURI(Uri.parse(filePath));
//            Log.d(TAG, "init: "+filePath);
//            videoPreview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mp) {
//                    mp.start();
//                    mp.setLooping(true);
//                }
//            });
//            videoPreview.start();
        }
        decorateTool.bringToFront();
        AnimationUtils.displayAnim(decorateTool,DecorateActivity.this,R.anim.fadein,View.VISIBLE);

        EffectsButton decorateSaveBtn= (EffectsButton) findViewById(R.id.btn_frag_decorate_save);
        EffectsButton decorateCancelBtn= (EffectsButton) findViewById(R.id.btn_frag_decorate_cancel);
        EffectsButton decorateShareBtn= (EffectsButton) findViewById(R.id.btn_frag_decorate_share);

        decorateSaveBtn.setOnClickEffectButtonListener(new EffectsButton.OnClickEffectButtonListener() {
            @Override
            public void onClickEffectButton() {
                saveFile(new Runnable() {
                    @Override
                    public void run() {
                        TastyToast.makeText(getApplicationContext(), "已保存至SD卡", TastyToast.LENGTH_SHORT,
                                TastyToast.SUCCESS);
                        //Toast.makeText(DecorateActivity.this,"已保存至"+outputFilePath,Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        decorateCancelBtn.setOnClickEffectButtonListener(new EffectsButton.OnClickEffectButtonListener() {
            @Override
            public void onClickEffectButton() {
                File file=new File(filePath);
                if(file.exists()){
                    file.delete();
                }
                finish();
            }
        });

        decorateShareBtn.setOnClickEffectButtonListener(new EffectsButton.OnClickEffectButtonListener() {
            @Override
            public void onClickEffectButton() {
                saveFile(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        if(mediaType==MimeType.PHOTO)
                            intent.setType("image/*");
                        else if(mediaType==MimeType.VIDEO)
                            intent.setType("video/*");
                        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(outputFilePath)));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(Intent.createChooser(intent, "分享给朋友"));
                    }
                });
            }
        });

        findViewById(R.id.btn_frag_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mediaFile!=null && mediaFile.exists()){
            mediaFile.delete();
        }
//        if (videoPreview != null) {
//            videoPreview.suspend();
//        }
      //  ijkWrapper.destroy();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
       // ijkWrapper.setSurface(new Surface(surface));
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
      //  ijkWrapper.setSurface(null);
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    protected void onPause() {
        super.onPause();
       // ijkWrapper.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
       // ijkWrapper.resume();
    }
}
