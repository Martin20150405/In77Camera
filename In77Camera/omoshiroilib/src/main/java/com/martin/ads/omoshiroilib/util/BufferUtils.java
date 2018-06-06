package com.martin.ads.omoshiroilib.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by Ads on 2016/6/25.
 */
public class BufferUtils {
    public static final int FLOAT_SIZE_BYTES = 4;
    public static FloatBuffer getFloatBuffer(final float[] array, int offset){
        FloatBuffer bb= ByteBuffer.allocateDirect(
                array.length * FLOAT_SIZE_BYTES)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(array);
        bb.position(offset);
        return bb;
    }

    public static final int SHORT_SIZE_BYTES = 2;
    public static ShortBuffer getShortBuffer(final short[] array, int offset){
        ShortBuffer bb=ByteBuffer.allocateDirect(
                array.length * SHORT_SIZE_BYTES)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer()
                .put(array);
        bb.position(offset);
        return bb;
    }
}
