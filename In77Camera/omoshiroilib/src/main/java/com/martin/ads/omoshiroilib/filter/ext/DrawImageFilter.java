package com.martin.ads.omoshiroilib.filter.ext;

import android.content.Context;
import android.opengl.GLES20;

import com.martin.ads.omoshiroilib.filter.base.PassThroughFilter;
import com.martin.ads.omoshiroilib.glessential.object.Plane;
import com.martin.ads.omoshiroilib.glessential.texture.BitmapTexture;
import com.martin.ads.omoshiroilib.util.MatrixUtils;
import com.martin.ads.omoshiroilib.util.TextureUtils;

/**
 * Created by Ads on 2017/1/27.
 * Draw an image on the scene.
 */

//TODO:update it
public class DrawImageFilter extends PassThroughFilter {

    private Plane imagePlane;
    private BitmapTexture bitmapTexture;
    private String imagePath;

    public DrawImageFilter(Context context,String imagePath) {
        super(context);
        bitmapTexture=new BitmapTexture();
        this.imagePath=imagePath;
        imagePlane =new Plane(false);
    }

    @Override
    public void init() {
        super.init();
        bitmapTexture.load(context,imagePath);
    }

    @Override
    public void onDrawFrame(int textureId) {
        super.onDrawFrame(textureId);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        TextureUtils.bindTexture2D(bitmapTexture.getImageTextureId(), GLES20.GL_TEXTURE0,glPassThroughProgram.getTextureSamplerHandle(),0);
        imagePlane.uploadTexCoordinateBuffer(glPassThroughProgram.getTextureCoordinateHandle());
        imagePlane.uploadVerticesBuffer(glPassThroughProgram.getPositionHandle());
        MatrixUtils.updateProjectionFit(
                bitmapTexture.getImageWidth(),
                bitmapTexture.getImageHeight(),
                surfaceWidth,
                surfaceHeight,
                projectionMatrix);
        GLES20.glUniformMatrix4fv(glPassThroughProgram.getMVPMatrixHandle(), 1, false, projectionMatrix, 0);
        imagePlane.draw();
        GLES20.glDisable(GLES20.GL_BLEND);
    }

    @Override
    public void onFilterChanged(int surfaceWidth, int surfaceHeight) {
        super.onFilterChanged(surfaceWidth, surfaceHeight);
    }
}
