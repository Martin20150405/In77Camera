package com.martin.ads.omoshiroilib.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

import com.martin.ads.omoshiroilib.util.BitmapUtils;

/**
 * Created by Ads on 2017/1/31.
 */

public class CaptureAnimation extends View {
    private long mAnimStartTime;
    private Bitmap mFlashBitmap;
    Paint mPaint = new Paint();
    private RectF mPortClipRect;
    RectF mRectF = new RectF();
    private int mScreenHeight;
    private int mScreenWidth;

    public CaptureAnimation(Context context, AttributeSet attrs) {
        super(context, attrs);
        initDisplayMetrics();
        mFlashBitmap=BitmapUtils.loadBitmapFromAssets(context,"ui/capture/capture_animation.png");
    }

    private void initDisplayMetrics() {
        this.mScreenWidth = 1080;
        this.mScreenHeight = 1920;
        this.mPortClipRect = new RectF(0.0F, 0.0F, this.mScreenWidth, this.mScreenHeight);
    }

    public CaptureAnimation resetAnimationSize(int width,int height){
        this.mScreenWidth = width;
        this.mScreenHeight = height;
        this.mPortClipRect = new RectF(0.0F, 0.0F, this.mScreenWidth, this.mScreenHeight);
        Matrix matrix = new Matrix();
        matrix.postScale(
                (float)width/mFlashBitmap.getWidth(),
                (float)height/mFlashBitmap.getHeight());
        mFlashBitmap=Bitmap.createBitmap(mFlashBitmap,0,0,mFlashBitmap.getWidth(),mFlashBitmap.getHeight(),matrix,true);
        return this;
    }

    public void draw(Canvas paramCanvas) {
        super.draw(paramCanvas);
        long l = SystemClock.elapsedRealtime() - this.mAnimStartTime;
        if (l > 400L) {
            return;
        }
        paramCanvas.save();
        if (l < 200L) {
            paramCanvas.drawBitmap(this.mFlashBitmap, 0.0F, 0.0F, null);
        }
        else {
            int i = Color.argb(204, 255, 255, 255);
            this.mPaint.setColor(i);
            this.mPaint.setStyle(Paint.Style.STROKE);
            this.mPaint.setStrokeWidth(40.0F);
            this.mRectF.inset(-1.5F, -1.5F);
            paramCanvas.clipRect(this.mPortClipRect);

        }
        paramCanvas.drawRoundRect(this.mRectF, 50.0F, 50.0F, this.mPaint);
        paramCanvas.restore();
        invalidate();
    }

    public void startAnimation() {
        mAnimStartTime = SystemClock.elapsedRealtime();
        mRectF.set(this.mPortClipRect);
        invalidate();
    }
}
