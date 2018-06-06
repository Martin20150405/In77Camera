package com.martin.ads.omoshiroilib.filter.imgproc;

import android.content.Context;
import android.opengl.GLES20;

import com.martin.ads.omoshiroilib.filter.base.AbsFilter;
import com.martin.ads.omoshiroilib.glessential.program.GLSimpleProgram;
import com.martin.ads.omoshiroilib.util.TextureUtils;

/**
 * Created by Ads on 2017/4/2.
 */

public class CustomizedGaussianBlurFilter extends AbsFilter {
    protected GLSimpleProgram glSimpleProgram;

    private float texelWidthOffset;
    private float texelHeightOffset;

    private boolean scale;

    public CustomizedGaussianBlurFilter(int blurRadius,double sigma) {
        super();
        glSimpleProgram=new GLSimpleProgram(
                generateCustomizedGaussianBlurVertexShader(blurRadius,sigma),
                generateCustomizedGaussianBlurFragmentShader(blurRadius,sigma)
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

    public CustomizedGaussianBlurFilter setTexelHeightOffset(float texelHeightOffset) {
        this.texelHeightOffset = texelHeightOffset;
        return this;
    }

    public CustomizedGaussianBlurFilter setTexelWidthOffset(float texelWidthOffset) {
        this.texelWidthOffset = texelWidthOffset;
        return this;
    }

    @Override
    public void onFilterChanged(int surfaceWidth, int surfaceHeight) {
        if(!scale)
            super.onFilterChanged(surfaceWidth, surfaceHeight);
        else super.onFilterChanged(surfaceWidth/4, surfaceHeight/4);
    }

    public CustomizedGaussianBlurFilter setScale(boolean scale) {
        this.scale = scale;
        return this;
    }

    private static String generateCustomizedGaussianBlurVertexShader(int blurRadius,double sigma){
        if (blurRadius < 1) {
            return "";
        }

        // First, generate the normal Gaussian weights for a given sigma
        double[] standardGaussianWeights = new double[blurRadius + 2];
        double sumOfWeights = 0.0;
        for (int currentGaussianWeightIndex = 0; currentGaussianWeightIndex < blurRadius + 1; currentGaussianWeightIndex++)
        {
            standardGaussianWeights[currentGaussianWeightIndex] = (1.0 / Math.sqrt(2.0 * Math.PI * Math.pow(sigma, 2.0)))
                    * Math.exp(-Math.pow(currentGaussianWeightIndex, 2.0) / (2.0 * Math.pow(sigma, 2.0)));

            if (currentGaussianWeightIndex == 0) {
                sumOfWeights += standardGaussianWeights[currentGaussianWeightIndex];
            }
            else {
                sumOfWeights += 2.0 * standardGaussianWeights[currentGaussianWeightIndex];
            }
        }

        // Next, normalize these weights to prevent the clipping of the Gaussian curve at the end of the discrete samples from reducing luminance
        for (int currentGaussianWeightIndex = 0; currentGaussianWeightIndex < blurRadius + 1; currentGaussianWeightIndex++) {
            standardGaussianWeights[currentGaussianWeightIndex] = standardGaussianWeights[currentGaussianWeightIndex] / sumOfWeights;
        }

        // From these weights we calculate the offsets to read interpolated values from
        int numberOfOptimizedOffsets = Math.min(blurRadius / 2 + (blurRadius % 2), 7);
        double[] optimizedGaussianOffsets = new double[numberOfOptimizedOffsets];

        for (int currentOptimizedOffset = 0; currentOptimizedOffset < numberOfOptimizedOffsets; currentOptimizedOffset++)
        {
            double firstWeight = standardGaussianWeights[currentOptimizedOffset*2 + 1];
            double secondWeight = standardGaussianWeights[currentOptimizedOffset*2 + 2];

            double optimizedWeight = firstWeight + secondWeight;

            optimizedGaussianOffsets[currentOptimizedOffset] = (firstWeight * (currentOptimizedOffset*2 + 1) + secondWeight * (currentOptimizedOffset*2 + 2)) / optimizedWeight;
        }

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
            stringBuilder.append(String.format(
                    "\tblurCoordinates[%d] = aTextureCoord.xy + singleStepOffset * %f;\n"+
                            "\tblurCoordinates[%d] = aTextureCoord.xy - singleStepOffset * %f;\n",
                    (currentOptimizedOffset * 2) + 1,
                    optimizedGaussianOffsets[currentOptimizedOffset],
                    (currentOptimizedOffset * 2) + 2,
                    optimizedGaussianOffsets[currentOptimizedOffset]
            ));
        }
        stringBuilder.append("}\n");
        return stringBuilder.toString();
    }

    private static String generateCustomizedGaussianBlurFragmentShader(int blurRadius,double sigma){
        if (blurRadius < 1) {
            return "";
        }

        // First, generate the normal Gaussian weights for a given sigma
        double[] standardGaussianWeights = new double[blurRadius + 2];
        double sumOfWeights = 0.0;
        for (int currentGaussianWeightIndex = 0; currentGaussianWeightIndex < blurRadius + 1; currentGaussianWeightIndex++) {
            standardGaussianWeights[currentGaussianWeightIndex] = (1.0 / Math.sqrt(2.0 * Math.PI
                    * Math.pow(sigma, 2.0))) * Math.exp(-Math.pow(currentGaussianWeightIndex, 2.0) / (2.0 * Math.pow(sigma, 2.0)));

            if (currentGaussianWeightIndex == 0) {
                sumOfWeights += standardGaussianWeights[currentGaussianWeightIndex];
            }
            else {
                sumOfWeights += 2.0 * standardGaussianWeights[currentGaussianWeightIndex];
            }
        }

        // Next, normalize these weights to prevent the clipping of the Gaussian curve at the end of the discrete samples from reducing luminance
        for (int currentGaussianWeightIndex = 0; currentGaussianWeightIndex < blurRadius + 1; currentGaussianWeightIndex++) {
            standardGaussianWeights[currentGaussianWeightIndex] = standardGaussianWeights[currentGaussianWeightIndex] / sumOfWeights;
        }

        // From these weights we calculate the offsets to read interpolated values from
        int numberOfOptimizedOffsets = Math.min(blurRadius / 2 + (blurRadius % 2), 7);
        int trueNumberOfOptimizedOffsets = blurRadius / 2 + (blurRadius % 2);

        StringBuilder stringBuilder=new StringBuilder();

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
                        standardGaussianWeights[0]
                        )
                );

        for (int currentBlurCoordinateIndex = 0; currentBlurCoordinateIndex < numberOfOptimizedOffsets; currentBlurCoordinateIndex++) {
            double firstWeight = standardGaussianWeights[currentBlurCoordinateIndex * 2 + 1];
            double secondWeight = standardGaussianWeights[currentBlurCoordinateIndex * 2 + 2];
            double optimizedWeight = firstWeight + secondWeight;
            stringBuilder.append(String.format(
                    "\tsum += texture2D(sTexture, blurCoordinates[%d]) * %f;\n"+
                            "\tsum += texture2D(sTexture, blurCoordinates[%d]) * %f;\n",
                    (currentBlurCoordinateIndex * 2) + 1, optimizedWeight,
                    (currentBlurCoordinateIndex * 2) + 2, optimizedWeight
                    )
            );
        }

        // If the number of required samples exceeds the amount we can pass in via varyings, we have to do dependent texture reads in the fragment shader
        if (trueNumberOfOptimizedOffsets > numberOfOptimizedOffsets) {
            stringBuilder.append("\thighp vec2 singleStepOffset = vec2(texelWidthOffset, texelHeightOffset);\n");
            for (int currentOverlowTextureRead = numberOfOptimizedOffsets; currentOverlowTextureRead < trueNumberOfOptimizedOffsets; currentOverlowTextureRead++) {
                double firstWeight = standardGaussianWeights[currentOverlowTextureRead * 2 + 1];
                double secondWeight = standardGaussianWeights[currentOverlowTextureRead * 2 + 2];
                double optimizedWeight = firstWeight + secondWeight;
                double optimizedOffset = (firstWeight * (currentOverlowTextureRead * 2 + 1) + secondWeight * (currentOverlowTextureRead * 2 + 2)) / optimizedWeight;
                stringBuilder.append(String.format(
                        "\tsum += texture2D(sTexture, blurCoordinates[0] + singleStepOffset * %f) * %f;\n"+
                                "\tsum += texture2D(sTexture, blurCoordinates[0] - singleStepOffset * %f) * %f;\n",
                        optimizedOffset, optimizedWeight,
                        optimizedOffset, optimizedWeight
                        )
                );
            }
        }

        stringBuilder.append("\tgl_FragColor = sum;\n")
                .append("}\n");
        return stringBuilder.toString();
    }

    public static CustomizedGaussianBlurFilter initWithBlurRadiusInPixels(int blurRadiusInPixels) {
        // 7.0 is the limit for blur size for hardcoded varying offsets
        int calculatedSampleRadius = 0;
        if (blurRadiusInPixels >= 1){ // Avoid a divide-by-zero error here{
            // Calculate the number of pixels to sample from by setting a bottom limit for the contribution of the outermost pixel
            double minimumWeightToFindEdgeOfSamplingArea = 1.0/256.0;
            calculatedSampleRadius = (int) Math.floor(Math.sqrt(-2.0 * Math.pow(blurRadiusInPixels, 2.0) * Math.log(minimumWeightToFindEdgeOfSamplingArea *
                    Math.sqrt(2.0 * Math.PI * Math.pow(blurRadiusInPixels, 2.0))) ));
            calculatedSampleRadius += calculatedSampleRadius % 2; // There's nothing to gain from handling odd radius sizes, due to the optimizations I use
        }
        return new CustomizedGaussianBlurFilter(calculatedSampleRadius,blurRadiusInPixels);
    }
}
