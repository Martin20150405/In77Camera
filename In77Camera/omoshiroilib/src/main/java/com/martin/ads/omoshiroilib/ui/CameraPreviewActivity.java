package com.martin.ads.omoshiroilib.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.martin.ads.omoshiroilib.R;
import com.martin.ads.omoshiroilib.constant.MimeType;
import com.martin.ads.omoshiroilib.debug.removeit.GlobalConfig;
import com.martin.ads.omoshiroilib.encoder.MediaCodecUtils;
import com.martin.ads.omoshiroilib.filter.base.PassThroughFilter;
import com.martin.ads.omoshiroilib.filter.ext.BlurredFrameEffect;
import com.martin.ads.omoshiroilib.filter.helper.FilterResourceHelper;
import com.martin.ads.omoshiroilib.filter.helper.FilterType;
import com.martin.ads.omoshiroilib.flyu.EffectAdapter;
import com.martin.ads.omoshiroilib.flyu.hardcode.HardCodeData;
import com.martin.ads.omoshiroilib.glessential.CameraView;
import com.martin.ads.omoshiroilib.glessential.GLRootView;
import com.martin.ads.omoshiroilib.ui.module.EffectsButton;
import com.martin.ads.omoshiroilib.ui.module.RecordButton;
import com.martin.ads.omoshiroilib.util.BitmapUtils;
import com.martin.ads.omoshiroilib.util.DisplayUtils;
import com.martin.ads.omoshiroilib.util.FileUtils;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.ArrayList;
import java.util.List;

import static com.martin.ads.omoshiroilib.util.AnimationUtils.displayAnim;

public class CameraPreviewActivity extends AppCompatActivity{
    private static final String TAG = "CameraPreviewActivity";
    private static final boolean DEBUG=false;

    private CameraView cameraView;

    private CaptureAnimation captureAnimation;

    private boolean isRecording=false;

    private RecyclerView filterListView;
    private RecyclerView effectListView;

    private int surfaceWidth,surfaceHeight;

    //EffectsButtons
    private EffectsButton switchCameraBtn;
    private EffectsButton cameraSettingBtn;
    private RelativeLayout cameraSettingsFrag;
    private CameraSettingBtn cameraTouchBtn;
    private CameraSettingBtn cameraTimeLapseBtn;
    private CameraSettingBtn cameraLightBtn;
    private CameraSettingBtn cameraPictureTypeBtn;
    private EffectsButton switchFilterBtn;
    private EffectsButton appSettingBtn;
    //TODO
    private EffectsButton switchFaceBtn;
    private RelativeLayout bottomControlPanel;
    private RecordButton recordButton;

    private ImageView cameraActionTip;
    private ImageView cameraFocusView;

    private boolean canUseMediaCodec;

    private int timeCountDown;
    private TextView timeCountDownText;
    private GLRootView glRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    //TODO:change touch logic
    private void init(){
        GlobalConfig.context=this;
        if(DEBUG)
            FilterResourceHelper.generateFilterThumbs(CameraPreviewActivity.this,false);
        FileUtils.upZipFile(this,"filter/thumbs/thumbs.zip",getFilesDir().getAbsolutePath());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().hide();
        setContentView(R.layout.camera_preview);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        getWindow().setAttributes(params);

        glRootView= (GLRootView) findViewById(R.id.camera_view);
        cameraView=new CameraView(this,glRootView);

        cameraView.setScreenSizeChangedListener(new CameraView.ScreenSizeChangedListener() {
            @Override
            public void updateScreenSize(int width, int height) {
                Log.d(TAG, "updateScreenSize: "+width+" "+height);
                surfaceWidth=width;
                surfaceHeight=height;
                captureAnimation.resetAnimationSize(width,height);
            }
        });

        captureAnimation= (CaptureAnimation) findViewById(R.id.capture_animation_view);

        findViewById(R.id.record_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
//                int rotation = windowManager.getDefaultDisplay().getRotation();
//                Log.d(TAG, "onClick: "+rotation);
                if(isRecording){
                    cameraView.getCameraEngine().releaseRecorder();
                    showHint("stop record");
                }else {
                    cameraView.getCameraEngine().startRecordingVideo();
                    showHint("start record");
                }
                isRecording=!isRecording;
            }
        });

        filterListView= (RecyclerView) findViewById(R.id.filter_list);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        filterListView.setLayoutManager(linearLayoutManager);

        List<FilterType> filterTypeList=new ArrayList<>();
        for(int i=0;i<FilterType.values().length;i++){
            filterTypeList.add(FilterType.values()[i]);
            if(i==0) filterTypeList.add(FilterType.NONE);
        }
        FilterAdapter filterAdapter=new FilterAdapter(this,filterTypeList);
        filterListView.setAdapter(filterAdapter);
        filterAdapter.setOnFilterChangeListener(new FilterAdapter.OnFilterChangeListener() {
            @Override
            public void onFilterChanged(FilterType filterType) {
                cameraView.getGlRender().switchLastFilterOfCustomizedFilters(filterType);
            }
        });

        initButtons();
        cameraSettingsFrag= (RelativeLayout) findViewById(R.id.rl_camera_setting_content);
        cameraSettingsFrag.setVisibility(View.GONE);
        cameraActionTip= (ImageView) findViewById(R.id.iv_frag_camera_action_tip);
        bottomControlPanel= (RelativeLayout) findViewById(R.id.bottom_control_panel);

        canUseMediaCodec=
                MediaCodecUtils.checkMediaCodecVideoEncoderSupport()==MediaCodecUtils.CODEC_SUPPORTED
                    &&
                MediaCodecUtils.checkMediaCodecAudioEncoderSupport()==MediaCodecUtils.CODEC_SUPPORTED;
        recordButton.setRecordable(canUseMediaCodec);

        timeCountDownText= (TextView) findViewById(R.id.tv_frag_camera_time_lapse_number);

        effectListView= (RecyclerView) findViewById(R.id.effect_list);

        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);
        linearLayoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);

        effectListView.setLayoutManager(linearLayoutManager2);
        HardCodeData.initHardCodeData();
        EffectAdapter effectAdapter=new EffectAdapter(this, HardCodeData.itemList);
        effectListView.setAdapter(effectAdapter);
        effectAdapter.setOnEffectChangeListener(new EffectAdapter.OnEffectChangeListener() {
            @Override
            public void onFilterChanged(HardCodeData.EffectItem effectItem) {
            }
        });
    }

    private void hideAllControlBtn(){
        displayAnim(switchFilterBtn,GlobalConfig.context,R.anim.fast_faded_out,View.GONE);
        displayAnim(switchFaceBtn,GlobalConfig.context,R.anim.fast_faded_out,View.GONE);
        displayAnim(cameraSettingBtn,GlobalConfig.context,R.anim.fast_faded_out,View.GONE);
        displayAnim(switchCameraBtn,GlobalConfig.context,R.anim.fast_faded_out,View.GONE);
        displayAnim(appSettingBtn,GlobalConfig.context,R.anim.fast_faded_out,View.GONE);
        requestHideCameraSettingsFrag();
    }

    private void showAllControlBtn(){
        displayAnim(switchFilterBtn,GlobalConfig.context,R.anim.fast_faded_in,View.VISIBLE);
        displayAnim(switchFaceBtn,GlobalConfig.context,R.anim.fast_faded_in,View.VISIBLE);
        displayAnim(cameraSettingBtn,GlobalConfig.context,R.anim.fast_faded_in,View.VISIBLE);
        displayAnim(switchCameraBtn,GlobalConfig.context,R.anim.fast_faded_in,View.VISIBLE);
        //displayAnim(appSettingBtn,GlobalConfig.context,R.anim.fast_faded_in,View.VISIBLE);
    }

    private void initButtons() {
        switchFilterBtn=getEffectsBtn(R.id.btn_switch_filter);
        switchFilterBtn.setOnClickEffectButtonListener(new EffectsButton.OnClickEffectButtonListener() {
            @Override
            public void onClickEffectButton() {
                hideTips();
                switchFilterBtn.setSelected(false);
                requestShowFilterView();
            }
        });

        switchFaceBtn=getEffectsBtn(R.id.btn_switch_face);
        switchFaceBtn.setOnClickEffectButtonListener(new EffectsButton.OnClickEffectButtonListener() {
            @Override
            public void onClickEffectButton() {
                hideTips();
                switchFaceBtn.setSelected(false);
                requestShowEffectView();
            }
        });

        switchCameraBtn= getEffectsBtn(R.id.btn_switch_camera);
        switchCameraBtn.setOnClickEffectButtonListener(new EffectsButton.OnClickEffectButtonListener() {
            @Override
            public void onClickEffectButton() {
                switchCameraBtn.setSelected(!switchCameraBtn.isSelected());
                cameraView.getGlRender().switchCamera();
            }
        });
        cameraSettingBtn= getEffectsBtn(R.id.btn_camera_setting);
        cameraSettingBtn.setOnClickEffectButtonListener(new EffectsButton.OnClickEffectButtonListener() {
            @Override
            public void onClickEffectButton() {
                if(cameraSettingBtn.isSelected())
                    requestHideCameraSettingsFrag();
                else requestShowCameraSettingsFrag();
            }
        });

        cameraView.setRootViewClickListener(new CameraView.RootViewClickListener() {
            @Override
            public void onRootViewTouched(MotionEvent e) {
                displayFocusAnim(e);
            }

            @Override
            public void onRootViewClicked() {
                requestHideCameraSettingsFrag();
                hideTips();
                if(bottomControlPanel.getVisibility()==View.GONE){
                    requestHideFilterAndEffectView();
                    displayAnim(bottomControlPanel,GlobalConfig.context,R.anim.fast_faded_in,View.VISIBLE);
                }
            }

            @Override
            public void onRootViewLongClicked() {
                if(cameraTouchBtn.isSelected()){
                    requestTakePicture();
                }
            }
        });

       cameraTouchBtn= new CameraSettingBtn(R.id.btn_camera_touch,R.id.tv_camera_touch)
                       .register(new EffectsButton.OnClickEffectButtonListener() {
                           @Override
                           public void onClickEffectButton() {
                               cameraTouchBtn.changeState();
                           }
                       });
       cameraTimeLapseBtn=new CameraSettingBtn(R.id.btn_camera_time_lapse,R.id.tv_camera_time_lapse)
               .register(new EffectsButton.OnClickEffectButtonListener() {
                   @Override
                   public void onClickEffectButton() {
                       cameraTimeLapseBtn.changeState();
                   }
               });
       cameraLightBtn=new CameraSettingBtn(R.id.btn_camera_light,R.id.tv_camera_light)
               .register(new EffectsButton.OnClickEffectButtonListener() {
                   @Override
                   public void onClickEffectButton() {
                       cameraLightBtn.changeState();
                       if(cameraLightBtn.isSelected()){
                           cameraView.getCameraEngine().requestOpenFlashLight(true);
                       }else{
                           cameraView.getCameraEngine().requestCloseFlashLight();
                       }
                   }
               });
       cameraPictureTypeBtn=new CameraSettingBtn(R.id.btn_camera_picture_type,R.id.tv_camera_picture_type)
               .register(new EffectsButton.OnClickEffectButtonListener() {
                   @Override
                   public void onClickEffectButton() {
                       cameraPictureTypeBtn.changeState();
                       if(cameraPictureTypeBtn.isSelected()){
                            cameraView.getGlRender().switchFilterOfPostProcessAtPos(new BlurredFrameEffect(GlobalConfig.context),0);
                       }else{
                           cameraView.getGlRender().switchFilterOfPostProcessAtPos(new PassThroughFilter(GlobalConfig.context),0);
                       }
                   }
               });

        recordButton= (RecordButton) findViewById(R.id.btn_takePicture);
        recordButton.setClickListener(new RecordButton.ClickListener() {
            @Override
            public void onClick() {
                requestTakePicture();
            }

            @Override
            public void onLongClickStart() {
                hideTips();
                hideAllControlBtn();
                if(canUseMediaCodec){
                    cameraView.getGlRender().setFileSavedCallback(new FileUtils.FileSavedCallback() {
                        @Override
                        public void onFileSaved(String filePath) {
                            if(!GlobalConfig.PREVIEW_WHEN_SHOT) return;
                            startDecorateActivity(filePath, MimeType.VIDEO);
                        }
                    });
                    cameraView.getGlRender().startRecording();
                }else{
                    showHint(getString(R.string.not_support_media_codec));
                }
                Log.d(TAG, "onLongClickStart: ");
            }

            @Override
            public void onLongClickEnd() {
                showAllControlBtn();
                if(canUseMediaCodec){
                    cameraView.getGlRender().stopRecording();
                }
                Log.d(TAG, "onLongClickEnd: ");
            }
        });

        appSettingBtn= getEffectsBtn(R.id.btn_app_setting);
        appSettingBtn.setOnClickEffectButtonListener(new EffectsButton.OnClickEffectButtonListener() {
            @Override
            public void onClickEffectButton() {
                appSettingBtn.setSelected(!appSettingBtn.isSelected());
                showHint("Apps设置还木有做");
            }
        });
        cameraFocusView=(ImageView) findViewById(R.id.iv_focus_anim_view);
    }

    private void startDecorateActivity(String filePath, int type) {
        Intent intent = new Intent(CameraPreviewActivity.this, DecorateActivity.class);
        intent.putExtra(DecorateActivity.SAVED_MEDIA_FILE, filePath);
        intent.putExtra(DecorateActivity.SAVED_MEDIA_TYPE, type);
        startActivity(intent);
    }

    private void hideTips() {
        cameraActionTip.setVisibility(View.GONE);
    }

    private void displayFocusAnim(MotionEvent e){
        int dx = (int)(e.getX() - DisplayUtils.getRefLength(GlobalConfig.context, 150.0F) / 2);
        int dy = (int)(e.getY() - DisplayUtils.getRefLength(GlobalConfig.context, 150.0F) / 2);
        RelativeLayout.LayoutParams localLayoutParams = (RelativeLayout.LayoutParams)cameraFocusView.getLayoutParams();
        localLayoutParams.leftMargin = dx;
        localLayoutParams.topMargin = dy;
        cameraFocusView.setLayoutParams(localLayoutParams);
        cameraFocusView.clearAnimation();
        Animation anim =
                AnimationUtils.loadAnimation(GlobalConfig.context, R.anim.anim_camera_focus);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cameraFocusView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        cameraFocusView.setVisibility(View.VISIBLE);
        cameraFocusView.startAnimation(anim);
    }

    private void takePic(){
        hideTips();
        captureAnimation.setVisibility(View.VISIBLE);
        captureAnimation.startAnimation();
        //cameraView.getCameraEngine().takePhoto();
        //cameraView.getGlRender().getFilterGroup().addPostDrawTask(new Runnable() {
        cameraView.getGlRender().addPostDrawTask(new Runnable() {
            @Override
            public void run() {
                BitmapUtils.sendImage(
                        surfaceWidth,
                        surfaceHeight,
                        GlobalConfig.context,
                        new FileUtils.FileSavedCallback() {
                            @Override
                            public void onFileSaved(String filePath) {
                                if(!GlobalConfig.PREVIEW_WHEN_SHOT) return;
                                startDecorateActivity(filePath, MimeType.PHOTO);
                            }
                        }
                );
            }
        });
    }

    private void requestTakePicture(){
        if(cameraTimeLapseBtn.isSelected()){
            timeCountDown=4;
            Message message = timeCountDownHandler.obtainMessage(1);
            timeCountDownHandler.sendMessage(message);
        }else takePic();
    }

    private void requestShowFilterView(){
        if(!switchFilterBtn.isSelected()){
            displayAnim(filterListView,GlobalConfig.context,R.anim.anim_gallery_show,View.VISIBLE);
            displayAnim(bottomControlPanel,GlobalConfig.context,R.anim.fast_faded_out,View.GONE);
            switchFilterBtn.setSelected(true);
        }
    }

    private void requestShowEffectView(){
        if(!switchFaceBtn.isSelected()){
            displayAnim(effectListView,GlobalConfig.context,R.anim.anim_gallery_show,View.VISIBLE);
            displayAnim(bottomControlPanel,GlobalConfig.context,R.anim.fast_faded_out,View.GONE);
            switchFaceBtn.setSelected(true);
        }
    }

    private void requestHideFilterAndEffectView(){
        if(switchFilterBtn.isSelected()){
            displayAnim(filterListView,GlobalConfig.context,R.anim.anim_gallery_hide,View.GONE);
            switchFilterBtn.setSelected(false);
        }
        if(switchFaceBtn.isSelected()){
            displayAnim(effectListView,GlobalConfig.context,R.anim.anim_gallery_hide,View.GONE);
            switchFaceBtn.setSelected(false);
        }
    }

    private void requestShowCameraSettingsFrag(){
        if(!cameraSettingBtn.isSelected()){
            displayAnim(cameraSettingsFrag,GlobalConfig.context,R.anim.anim_setting_content_show,View.VISIBLE);
            cameraSettingBtn.setSelected(true);
        }
    }

    private void requestHideCameraSettingsFrag(){
        if(cameraSettingBtn.isSelected()){
            displayAnim(cameraSettingsFrag,GlobalConfig.context,R.anim.anim_setting_content_hide,View.GONE);
            cameraSettingBtn.setSelected(false);
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(cameraView!=null)
            cameraView.onPause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(cameraView!=null)
            cameraView.onResume();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(cameraView!=null)
            cameraView.onDestroy();
    }

    private void showHint(final String hint){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TastyToast.makeText(getApplicationContext(), hint, TastyToast.LENGTH_LONG,
                        TastyToast.INFO);
            }
        });

        //Toast.makeText(this,hint ,Toast.LENGTH_LONG).show();
    }

    private EffectsButton getEffectsBtn(int id){
        return (EffectsButton) findViewById(id);
    }

    private TextView getTextView(int id){
        return (TextView) findViewById(id);
    }

    class CameraSettingBtn{
        EffectsButton effectsButton;
        TextView hintText;

        CameraSettingBtn(int btnId,int textId){
            effectsButton=getEffectsBtn(btnId);
            hintText=getTextView(textId);
        }
        boolean isSelected(){
            return effectsButton.isSelected();
        }
        void changeState(){
            if(!effectsButton.isSelected()){
                effectsButton.setSelected(true);
                hintText.setTextColor(GlobalConfig.context.getResources().getColor(R.color.app_color));
            }else{
                effectsButton.setSelected(false);
                hintText.setTextColor(Color.WHITE);
            }
        }
        CameraSettingBtn register(EffectsButton.OnClickEffectButtonListener listener){
            effectsButton.setOnClickEffectButtonListener(listener);
            return this;
        }
    }

    final Handler timeCountDownHandler = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what) {
                case 1:
                    timeCountDown--;
                    if(timeCountDown > 0){
                        timeCountDownText.setText(""+timeCountDown);
                        displayAnim(timeCountDownText,GlobalConfig.context,R.anim.anim_text_scale,View.VISIBLE);
                        Message message = timeCountDownHandler.obtainMessage(1);
                        timeCountDownHandler.sendMessageDelayed(message, 1000);      // send message
                    }else{
                        timeCountDownText.setVisibility(View.GONE);
                        takePic();
                    }
            }
            super.handleMessage(msg);
        }
    };

}
