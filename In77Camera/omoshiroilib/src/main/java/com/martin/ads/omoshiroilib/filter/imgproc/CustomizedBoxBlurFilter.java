package com.martin.ads.omoshiroilib.filter.imgproc;

import android.opengl.GLES20;

import com.martin.ads.omoshiroilib.filter.base.AbsFilter;
import com.martin.ads.omoshiroilib.glessential.program.GLSimpleProgram;
import com.martin.ads.omoshiroilib.util.TextureUtils;

/**
 * Created by Ads on 2017/4/2.
 */

public class CustomizedBoxBlurFilter extends AbsFilter {
    protected GLSimpleProgram glSimpleProgram;

    private float texelWidthOffset;
    private float texelHeightOffset;

    private boolean scale;

    public CustomizedBoxBlurFilter(int blurRadius) {
        super();
        glSimpleProgram=new GLSimpleProgram(
                generateCustomizedBoxBlurVertexShader(blurRadius),
                generateCustomizedBoxBlurFragmentShader(blurRadius)
                );
        texelWidthOffset=texelHeightOffset=0;
        scale=false;
    }

    @Override
    public void init() {
        glSimpleProgram.create();
    }

    @Override
    public void onPreDrawElements() {
        super.onPreDrawElements();
        glSimpleProgram.use();
        plane.uploadTexCoordinateBuffer(glSimpleProgram.getTextureCoordinateHandle());
        plane.uploadVerticesBuffer(glSimpleProgram.getPositionHandle());
    }

    @Override
    public void destroy() {
        glSimpleProgram.onDestroy();
    }

    @Override
    public void onDrawFrame(int textureId) {
        onPreDrawElements();
        setUniform1f(glSimpleProgram.getProgramId(),"texelWidthOffset",texelWidthOffset/surfaceWidth);
        setUniform1f(glSimpleProgram.getProgramId(),"texelHeightOffset",texelHeightOffset/surfaceHeight);

        TextureUtils.bindTexture2D(textureId, GLES20.GL_TEXTURE0,glSimpleProgram.getTextureSamplerHandle(),0);
        GLES20.glViewport(0,0,surfaceWidth,surfaceHeight);
        plane.draw();
    }

    public CustomizedBoxBlurFilter setTexelHeightOffset(float texelHeightOffset) {
        this.texelHeightOffset = texelHeightOffset;
        return this;
    }

    public CustomizedBoxBlurFilter setTexelWidthOffset(float texelWidthOffset) {
        this.texelWidthOffset = texelWidthOffset;
        return this;
    }

    @Override
    public void onFilterChanged(int surfaceWidth, int surfaceHeight) {
        if(!scale)
            super.onFilterChanged(surfaceWidth, surfaceHeight);
        else super.onFilterChanged(surfaceWidth/4, surfaceHeight/4);
    }

    public CustomizedBoxBlurFilter setScale(boolean scale) {
        this.scale = scale;
        return this;
    }
    public static String generateCustomizedBoxBlurVertexShader(int blurRadius){
        if (blurRadius < 1) {
            return "";
        }

        // From these weights we calculate the offsets to read interpolated values from
        int numberOfOptimizedOffsets = Math.min(blurRadius / 2 + (blurRadius % 2), 7);

        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(
                String.format("attribute vec4 aPosition;\n" +
                                "attribute vec4 aTextureCoord;\n" +
                                "uniform float texelWidthOffset;\n"+
                                "uniform float texelHeightOffset;\n"+
                                "varying vec2 blurCoordinates[%d];\n"+
                                "void main(){\n"+
                                "\tgl_Position = aPosition;\n"+
                                "\tvec2 singleStepOffset = vec2(texelWidthOffset, texelHeightOffset);\n",
                        1 + (numberOfOptimizedOffsets * 2)
                ))
                .append("\tblurCoordinates[0] = aTextureCoord.xy;\n");
        for (int currentOptimizedOffset = 0; currentOptimizedOffset < numberOfOptimizedOffsets; currentOptimizedOffset++){
            double optimizedOffset = (currentOptimizedOffset * 2) + 1.5;
            stringBuilder.append(String.format(
                    "\tblurCoordinates[%d] = aTextureCoord.xy + singleStepOffset * %f;\n"+
                            "\tblurCoordinates[%d] = aTextureCoord.xy - singleStepOffset * %f;\n",
                    (currentOptimizedOffset * 2) + 1,
                    optimizedOffset,
                    (currentOptimizedOffset * 2) + 2,
                    optimizedOffset
            ));
        }
        stringBuilder.append("}\n");
        return stringBuilder.toString();
    }

    public static String generateCustomizedBoxBlurFragmentShader(int blurRadius){
        if (blurRadius < 1) {
            return "";
        }
        int numberOfOptimizedOffsets = Math.min(blurRadius / 2 + (blurRadius % 2), 7);
        int trueNumberOfOptimizedOffsets = blurRadius / 2 + (blurRadius % 2);

        StringBuilder stringBuilder=new StringBuilder();

        double boxWeight = 1.0 / ((blurRadius * 2) + 1);

        stringBuilder
                .append("uniform sampler2D sTexture;\n"
                        +"uniform highp float texelWidthOffset;\n"
                        +"uniform highp float texelHeightOffset;\n")
                .append(String.format(
                        "varying highp vec2 blurCoordinates[%d];\n",
                        1 + (numberOfOptimizedOffsets * 2)
                        )
                )
                .append("void main(){\n")
                .append("\tlowp vec4 sum = vec4(0.0);\n")
                .append(String.format(
                        "\tsum += texture2D(sTexture, blurCoordinates[0]) * %f;\n",
                        boxWeight
                        )
                );

        for (int currentBlurCoordinateIndex = 0; currentBlurCoordinateIndex < numberOfOptimizedOffsets; currentBlurCoordinateIndex++) {
            stringBuilder.append(String.format(
                    "\tsum += texture2D(sTexture, blurCoordinates[%d]) * %f;\n"+
                            "\tsum += texture2D(sTexture, blurCoordinates[%d]) * %f;\n",
                    (currentBlurCoordinateIndex * 2) + 1, boxWeight * 2.0,
                    (currentBlurCoordinateIndex * 2) + 2, boxWeight * 2.0
                    )
            );
        }

        // If the number of required samples exceeds the amount we can pass in via varyings, we have to do dependent texture reads in the fragment shader
        if (trueNumberOfOptimizedOffsets > numberOfOptimizedOffsets) {
            stringBuilder.append("\thighp vec2 singleStepOffset = vec2(texelWidthOffset, texelHeightOffset);\n");
            for (int currentOverlowTextureRead = numberOfOptimizedOffsets; currentOverlowTextureRead < trueNumberOfOptimizedOffsets; currentOverlowTextureRead++) {
                double optimizedOffset = (double)(currentOverlowTextureRead * 2) + 1.5;
                stringBuilder.append(String.format(
                        "\tsum += texture2D(sTexture, blurCoordinates[0] + singleStepOffset * %f) * %f;\n"+
                                "\tsum += texture2D(sTexture, blurCoordinates[0] - singleStepOffset * %f) * %f;\n",
                        optimizedOffset, boxWeight * 2.0,
                        optimizedOffset, boxWeight * 2.0
                        )
                );
            }
        }

        stringBuilder.append("\tgl_FragColor = sum;\n")
                .append("}\n");
        return stringBuilder.toString();
    }
}
