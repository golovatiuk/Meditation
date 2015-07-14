package com.gotwingm.my.meditation;


import android.media.MediaPlayer;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class BackgroundVideoManager extends MainActivity implements SurfaceHolder.Callback {

    SurfaceView mSurfaceView;
    SurfaceHolder mSurfaceHolder;
    MediaPlayer mMediaPlayer;


    BackgroundVideoManager() {

        mSurfaceView = (SurfaceView) findViewById(R.id.backgroundVideoSurface);
        mSurfaceHolder = mSurfaceView.getHolder();
        mMediaPlayer = new MediaPlayer();

        mSurfaceHolder.addCallback(this);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        mMediaPlayer.setDisplay(mSurfaceHolder);
        try {
            mMediaPlayer.setDataSource("android.resource://" + getPackageName() + "/" + R.raw.noon);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        mMediaPlayer.release();

    }
}
