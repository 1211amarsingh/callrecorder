package com.kv.callrecorder.Utility.visualizer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

abstract public class BaseVisualizer extends View {
    protected byte[] bytes;
    protected Paint paint;
    protected Visualizer visualizer;
    protected int color = Color.BLUE;

    public BaseVisualizer(Context context) {
        super(context);
        init(null);
        init();
    }

    public BaseVisualizer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
        init();
    }

    public BaseVisualizer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
        init();
    }

    private void init(AttributeSet attributeSet) {
        paint = new Paint();
    }

    /**
     * Set color to visualizer with color resource id.
     *
     * @param color color resource id.
     */
    public void setColor(int color) {
        this.color = color;
        this.paint.setColor(this.color);
    }

    /**
     * @deprecated will be removed in next version use {@link BaseVisualizer#setPlayer(int)} instead
     * @param mediaPlayer MediaPlayer
     */
    @Deprecated
    public void setPlayer(MediaPlayer mediaPlayer) {
        setPlayer(mediaPlayer.getAudioSessionId());
    }

    public void setPlayer(int audioSessionId) {
        visualizer = new Visualizer(audioSessionId);
        visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);

        visualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            @Override
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes,
                                              int samplingRate) {
                BaseVisualizer.this.bytes = bytes;
                invalidate();
            }

            @Override
            public void onFftDataCapture(Visualizer visualizer, byte[] bytes,
                                         int samplingRate) {
            }
        }, Visualizer.getMaxCaptureRate() / 2, true, false);

        visualizer.setEnabled(true);
    }

    public void release() {
        visualizer.release();
    }

    public Visualizer getVisualizer() {
        return visualizer;
    }

    protected abstract void init();
}