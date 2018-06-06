package com.martin.ads.omoshiroilib.encoder;

import android.annotation.TargetApi;
import android.media.AudioFormat;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Build;
import android.util.Log;

/**
 * Created by Ads on 2017/5/30.
 */

public class MediaCodecUtils {
    public static final int CODEC_SUPPORTED=1;
    public static final int CODEC_ERROR=-1;
    public static final int CODEC_REQ_API_NOT_SATISFIED=-2;

    private static final String TAG = "MediaCodecUtils";
    public static final int MIN_API_LEVEL_VIDEO=Build.VERSION_CODES.JELLY_BEAN_MR2;
    public static final int MIN_API_LEVEL_AUDIO=Build.VERSION_CODES.JELLY_BEAN;

    public static final String MIME_TYPE_VIDEO = "video/avc";
    public static final int TEST_WIDTH = 1280;
    public static final int TEST_HEIGHT = 720;
    public static final int TEST_VIDEO_BIT_RATE = 1000000;
    public static final int TEST_FRAME_RATE = 30;
    public static final int TEST_IFRAME_INTERVAL = 5;

    public static final String MIME_TYPE_AUDIO = "audio/mp4a-latm";
    public static final int TEST_SAMPLE_RATE = 44100;	// 44.1[KHz] is only setting guaranteed to be available on all devices.
    public static final int TEST_AUDIO_BIT_RATE = 64000;
    public static final int SAMPLES_PER_FRAME = 1024;	// AAC, bytes/frame/channel
    public static final int FRAMES_PER_BUFFER = 25; 	// AAC, frame/buffer/sec


    public static int getApiLevel(){
        return android.os.Build.VERSION.SDK_INT;
    }

    @TargetApi(MIN_API_LEVEL_VIDEO)
    public static int checkMediaCodecVideoEncoderSupport(){
        if(getApiLevel()<MIN_API_LEVEL_VIDEO){
            Log.d(TAG, "checkMediaCodecVideoEncoderSupport: Min API is 18");
            return CODEC_REQ_API_NOT_SATISFIED;
        }
        MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE_VIDEO, TEST_WIDTH, TEST_HEIGHT);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        format.setInteger(MediaFormat.KEY_BIT_RATE, TEST_VIDEO_BIT_RATE);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, TEST_FRAME_RATE);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, TEST_IFRAME_INTERVAL);
        MediaCodec mediaCodec;
        try {
            mediaCodec = MediaCodec.createEncoderByType(MIME_TYPE_VIDEO);
            mediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            mediaCodec.createInputSurface();
            mediaCodec.start();
            mediaCodec.stop();
            mediaCodec.release();
            mediaCodec = null;
        } catch (Exception ex) {
            Log.e(TAG, "Failed on creation of codec #", ex);
            return CODEC_ERROR;
        }
        return CODEC_SUPPORTED;
    }

    @TargetApi(MIN_API_LEVEL_AUDIO)
    public static int checkMediaCodecAudioEncoderSupport(){
        if(getApiLevel()<MIN_API_LEVEL_AUDIO){
            Log.d(TAG, "checkMediaCodecAudioEncoderSupport: Min API is 16");
            return CODEC_REQ_API_NOT_SATISFIED;
        }
        final MediaFormat audioFormat = MediaFormat.createAudioFormat(MIME_TYPE_AUDIO, TEST_SAMPLE_RATE, 1);
        audioFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
        audioFormat.setInteger(MediaFormat.KEY_CHANNEL_MASK, AudioFormat.CHANNEL_IN_MONO);
        audioFormat.setInteger(MediaFormat.KEY_BIT_RATE, TEST_AUDIO_BIT_RATE);
        audioFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1);
        MediaCodec mediaCodec;
        try {
            mediaCodec = MediaCodec.createEncoderByType(MIME_TYPE_AUDIO);
            mediaCodec.configure(audioFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            mediaCodec.start();
            mediaCodec.stop();
            mediaCodec.release();
            mediaCodec = null;
        } catch (Exception ex) {
            Log.e(TAG, "Failed on creation of codec #", ex);
            return CODEC_ERROR;
        }
        return CODEC_SUPPORTED;
    }
}
