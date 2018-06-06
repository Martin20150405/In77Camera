//package com.martin.ads.omoshiroilib.ui;
//
//import android.view.Surface;
//
//import java.io.IOException;
////
////import tv.danmaku.ijk.media.player.IMediaPlayer;
////import tv.danmaku.ijk.media.player.IjkMediaPlayer;
//
///**
// * Created by Ads on 2017/6/3.
// */
//
//public class IjkWrapper
////        implements
////        IMediaPlayer.OnPreparedListener {
////        protected IMediaPlayer mPlayer;
////        private IjkMediaPlayer.OnPreparedListener mPreparedListener;
////        private static final int STATUS_IDLE = 0;
////        private static final int STATUS_PREPARING = 1;
////        private static final int STATUS_PREPARED = 2;
////        private static final int STATUS_STARTED = 3;
////        private static final int STATUS_PAUSED = 4;
////        private static final int STATUS_STOPPED = 5;
////        private int mStatus = STATUS_IDLE;
////
////        public void init(){
////            mStatus = STATUS_IDLE;
////            mPlayer = new IjkMediaPlayer();
////            mPlayer.setOnPreparedListener(this);
////            mPlayer.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
////                @Override
////                public boolean onInfo(IMediaPlayer mp, int what, int extra) {
////                    return false;
////                }
////            });
////        }
////
////        public void setSurface(Surface surface){
////            if (getPlayer() != null){
////                getPlayer().setSurface(surface);
////            }
////        }
////
////        public void openRemoteFile(String url){
////            try {
////                mPlayer.setDataSource(url);
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
////        }
////
////        public IMediaPlayer getPlayer() {
////            return mPlayer;
////        }
////
////        public void prepare() {
////            if (mPlayer == null) return;
////            if (mStatus == STATUS_IDLE || mStatus == STATUS_STOPPED){
////                mPlayer.prepareAsync();
////                mPlayer.setLooping(true);
////                mStatus = STATUS_PREPARING;
////            }
////        }
////
////        public void stop(){
////            if (mPlayer == null) return;
////            if (mStatus == STATUS_STARTED || mStatus ==  STATUS_PAUSED){
////                mPlayer.stop();
////                mStatus = STATUS_STOPPED;
////            }
////        }
////
////        public void pause(){
////            if (mPlayer == null) return;
////            if (mPlayer.isPlaying() && mStatus == STATUS_STARTED) {
////                mPlayer.pause();
////                mStatus = STATUS_PAUSED;
////            }
////        }
////
////        private void start(){
////            if (mPlayer == null) return;
////            if (mStatus == STATUS_PREPARED || mStatus == STATUS_PAUSED){
////                mPlayer.start();
////                mStatus = STATUS_STARTED;
////            }
////
////        }
////
////        public void setPreparedListener(IMediaPlayer.OnPreparedListener mPreparedListener) {
////            this.mPreparedListener = mPreparedListener;
////        }
////
////        @Override
////        public void onPrepared(IMediaPlayer mp) {
////            mStatus = STATUS_PREPARED;
////            start();
////            if (mPreparedListener != null) mPreparedListener.onPrepared(mp);
////        }
////
////        public void resume() {
////            start();
////        }
////
////        public void destroy() {
////            stop();
////            if (mPlayer != null) {
////                mPlayer.setSurface(null);
////                mPlayer.release();
////            }
////            mPlayer = null;
////        }
//}
