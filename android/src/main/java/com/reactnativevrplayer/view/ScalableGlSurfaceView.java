package com.reactnativevrplayer.view;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;

import com.reactnativevrplayer.R;
import com.reactnativevrplayer.type.ScalableType;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class ScalableGlSurfaceView extends GLSurfaceView implements
        IMediaPlayer.OnVideoSizeChangedListener {

    protected IMediaPlayer mMediaPlayer;
    protected ScalableType mScalableType = ScalableType.NONE;


    public ScalableGlSurfaceView(Context context) {
        this(context, null);
    }

    public ScalableGlSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (attrs == null) return;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.scaleStyle, 0, 0);
        if (a == null) return;
        int scaletype = a.getInt(R.styleable.scaleStyle_scalableType, ScalableType.NONE.ordinal());
        a.recycle();
        mScalableType = ScalableType.values()[scaletype];
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mMediaPlayer == null) {
            return;
        }

        if (isPlaying()) {
            stop();
        }
        release();
    }

    @Override
    public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {

    }

    private void initializeMediaPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new IjkMediaPlayer();
            mMediaPlayer.setOnVideoSizeChangedListener(this);
        } else {
            reset();
        }
    }

    public void setRawData(@RawRes int id) throws IOException {
        AssetFileDescriptor afd = getResources().openRawResourceFd(id);
        setDataSource(afd);
    }

    public void setAssetData(@NonNull String assetName) throws IOException {
        AssetManager manager = getContext().getAssets();
        AssetFileDescriptor afd = manager.openFd(assetName);
        setDataSource(afd);
    }

    private void setDataSource(@NonNull AssetFileDescriptor afd) throws IOException {
        setDataSource(afd.getFileDescriptor());
        afd.close();
    }

    public void setDataSource(@NonNull String path) throws IOException {
        initializeMediaPlayer();
        mMediaPlayer.setDataSource(path);
    }

    public void setDataSource(@NonNull Context context, @NonNull Uri uri,
                              @Nullable Map<String, String> headers) throws IOException {
        initializeMediaPlayer();
        mMediaPlayer.setDataSource(context, uri, headers);
    }

    public void setDataSource(@NonNull Context context, @NonNull Uri uri) throws IOException {
        initializeMediaPlayer();
        mMediaPlayer.setDataSource(context, uri);
    }

    public void setDataSource(@NonNull FileDescriptor fd) throws IOException {
        initializeMediaPlayer();
        mMediaPlayer.setDataSource(fd);
    }

    public void prepare(@Nullable IMediaPlayer.OnPreparedListener listener)
            throws IOException, IllegalStateException {
        mMediaPlayer.setOnPreparedListener(listener);
        mMediaPlayer.prepareAsync();
    }

    public void prepareAsync(@Nullable IMediaPlayer.OnPreparedListener listener)
            throws IllegalStateException {
        mMediaPlayer.setOnPreparedListener(listener);
        mMediaPlayer.prepareAsync();
    }

    public void prepare() throws IOException, IllegalStateException {
        prepare(null);
    }

    public void prepareAsync() throws IllegalStateException {
        prepareAsync(null);
    }

    public void setOnErrorListener(@Nullable IMediaPlayer.OnErrorListener listener) {
        mMediaPlayer.setOnErrorListener(listener);
    }

    public void setOnCompletionListener(@Nullable IMediaPlayer.OnCompletionListener listener) {
        mMediaPlayer.setOnCompletionListener(listener);
    }

    public void setOnInfoListener(@Nullable IMediaPlayer.OnInfoListener listener) {
        mMediaPlayer.setOnInfoListener(listener);
    }

    public long getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    public long getDuration() {
        return mMediaPlayer.getDuration();
    }

    public int getVideoHeight() {
        return mMediaPlayer.getVideoHeight();
    }

    public int getVideoWidth() {
        return mMediaPlayer.getVideoWidth();
    }

    public boolean isLooping() {
        return mMediaPlayer.isLooping();
    }

    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    public void pause() {
        mMediaPlayer.pause();
    }

    public void seekTo(int msec) {
        mMediaPlayer.seekTo(msec);
    }

    public void setLooping(boolean looping) {
        mMediaPlayer.setLooping(looping);
    }

    public void setVolume(float leftVolume, float rightVolume) {
        mMediaPlayer.setVolume(leftVolume, rightVolume);
    }

    public void start() {
        mMediaPlayer.start();
    }

    public void stop() {
        mMediaPlayer.stop();
    }

    public void reset() {
        mMediaPlayer.reset();
    }

    public void release() {
        reset();
        mMediaPlayer.release();
        mMediaPlayer = null;
    }
}
