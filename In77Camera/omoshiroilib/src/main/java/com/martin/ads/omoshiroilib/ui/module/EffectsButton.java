package com.martin.ads.omoshiroilib.ui.module;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

/**
 * Created by Ads on 2017/5/27.
 * tilt button
 */

public class EffectsButton extends AppCompatButton
{
    private static String TAG = "EffectsButton";
    private boolean clickable = true;
    private ScaleAnimation downAnim = createDownAnim();
    private ScaleAnimation upAnimation = createUpAnim();
    private ScaleAnimation tmpUpAnim = createUpAnim();
    private OnClickEffectButtonListener onClickEffectButtonListener;
    private boolean shouldAbortAnim;
    private int preX;
    private int preY;
    private int[] locationOnScreen;
    private Animation.AnimationListener animationListener = new Animation.AnimationListener() {
        public void onAnimationEnd(Animation paramAnonymousAnimation) {
            //setSelected(!isSelected());
            clearAnimation();
            //Log.d(TAG, "onAnimationEnd: ");
            if (onClickEffectButtonListener != null) {
                onClickEffectButtonListener.onClickEffectButton();
            }
        }

        public void onAnimationRepeat(Animation paramAnonymousAnimation) {}

        public void onAnimationStart(Animation paramAnonymousAnimation) {}
    };

    public EffectsButton(Context paramContext) {
        this(paramContext, null);
    }

    public EffectsButton(Context paramContext, AttributeSet paramAttributeSet) {
        this(paramContext, paramAttributeSet, 0);
    }

    public EffectsButton(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        upAnimation.setAnimationListener(this.animationListener);
        locationOnScreen = new int[2];
        setGravity(Gravity.CENTER);
    }

    ScaleAnimation createUpAnim() {
        ScaleAnimation localScaleAnimation = new ScaleAnimation(1.2F, 1.0F, 1.2F, 1.0F, 1, 0.5F, 1, 0.5F);
        localScaleAnimation.setDuration(50L);
        localScaleAnimation.setFillEnabled(true);
        localScaleAnimation.setFillEnabled(false);
        localScaleAnimation.setFillAfter(true);
        return localScaleAnimation;
    }

    ScaleAnimation createDownAnim() {
        ScaleAnimation localScaleAnimation = new ScaleAnimation(1.0F, 1.2F, 1.0F, 1.2F, 1, 0.5F, 1, 0.5F);
        localScaleAnimation.setDuration(50L);
        localScaleAnimation.setFillEnabled(true);
        localScaleAnimation.setFillBefore(false);
        localScaleAnimation.setFillAfter(true);
        return localScaleAnimation;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        super.onTouchEvent(motionEvent);
        if (!this.clickable) {
            return false;
        }
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            clearAnimation();
            startAnimation(this.downAnim);
            this.shouldAbortAnim = false;
            getLocationOnScreen(this.locationOnScreen);
            preX = (this.locationOnScreen[0] + getWidth() / 2);
            preY = (this.locationOnScreen[1] + getHeight() / 2);
        }
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            clearAnimation();
            if (!this.shouldAbortAnim) {
                startAnimation(this.upAnimation);
            }
            this.shouldAbortAnim = false;
        }
        else if (motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
            clearAnimation();
            startAnimation(this.tmpUpAnim);
            this.shouldAbortAnim = false;
        }
        else if ((motionEvent.getAction() == MotionEvent.ACTION_MOVE) && (!this.shouldAbortAnim) && (!checkPos(motionEvent.getRawX(), motionEvent.getRawY()))) {
            this.shouldAbortAnim = true;
            clearAnimation();
            startAnimation(this.tmpUpAnim);
        }
        return true;
    }

    boolean checkPos(float rawX, float rawY) {
        rawX = Math.abs(rawX - this.preX);
        rawY = Math.abs(rawY - this.preY);
        return (rawX <= getWidth() / 2) && (rawY <= getHeight() / 2);
    }

    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    public void setOnClickEffectButtonListener(OnClickEffectButtonListener parama) {
        this.onClickEffectButtonListener = parama;
    }

    public interface OnClickEffectButtonListener {
        void onClickEffectButton();
    }
}
