package com.martin.ads.omoshiroilib.constant;

/**
 * Created by Ads on 2017/1/26.
 */

public enum Rotation {
    NORMAL, ROTATION_90, ROTATION_180, ROTATION_270;

    private Rotation() {
    }

    public int asInt() {
        switch(ordinal()) {
            case 0:
                return 0;
            case 1:
                return 90;
            case 2:
                return 180;
            case 3:
                return 270;
            default:
                throw new IllegalStateException("Unknown Rotation!");
        }
    }

    public static Rotation fromInt(int degree) {
        switch(degree) {
            case 0:
                return NORMAL;
            case 90:
                return ROTATION_90;
            case 180:
                return ROTATION_180;
            case 270:
                return ROTATION_270;
            case 360:
                return NORMAL;
            default:
                throw new IllegalStateException(degree + " is an unknown rotation. Needs to be either 0, 90, 180 or 270!");
        }
    }
}