package com.gotwingm.my.meditation;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import java.io.IOException;

public class MeditationManger extends MainActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    int resId;
    boolean isPlayerOnPause;
    boolean progressBarWork;
    boolean isReplay;
    boolean canReplay;
    boolean isPlay;
    int progress;
    int progressMax;
    Thread mThread;
    android.os.Handler mHandler;

    MediaPlayer mMediaPlayer;
    ProgressBar mProgressBar;
    LinearLayout mLinearLayout;
    View meditationSettingsBar;
    View meditationSettingsSubBar;
    View meditationVolumeBar;
    ImageView replayButton;
    ImageView shareButton;
    ImageButton okButton;
    ImageButton xButton;
    ImageButton playButton;
    SeekBar volumeChangeBar;
    static View melodyButton;

    MeditationManger() {

        mHandler = myHandler();

        meditationVolumeBar = layoutInflater.inflate(R.layout.volume_bar, null);
        meditationSettingsBar = layoutInflater.inflate(R.layout.meditation_settings_bar, null);

        playButton = (ImageButton) meditationView.findViewById(R.id.meditationPlayButton);
        replayButton = (ImageView) meditationView.findViewById(R.id.meditationReplayButton);
        shareButton = (ImageView) meditationView.findViewById(R.id.meditationShareButton);

        playButton.setOnClickListener(MeditationManger.this);
        meditationView.findViewById(R.id.meditationSettingsButton).setOnClickListener(MeditationManger.this);

        mProgressBar = (ProgressBar) meditationView.findViewById(R.id.progressBar);
        mLinearLayout = (LinearLayout) meditationView.findViewById(R.id.meditationSettingsLL);
        volumeChangeBar = (SeekBar) meditationVolumeBar.findViewById(R.id.volumeBar);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.meditationSettBarOKButton:

                openSettBar();

                break;

            case R.id.meditationSettBarXButton:

                openSettSubBar();

                break;

            case R.id.meditationSettSubBarMusicButton:

                onMelodyChooseButtonClick(v);

                break;

            case R.id.meditationSettSubBarMusic2Button:

                onMelodyChooseButtonClick(v);

                break;

            case R.id.meditationSettSubBarBirdButton:

                onMelodyChooseButtonClick(v);

                break;

            case R.id.meditationSettSubBarSeaButton:

                onMelodyChooseButtonClick(v);

                break;

            case R.id.meditationPlayButton:

                if (isReplay)
                {
                    shareButton.setBackgroundColor(Color.parseColor("#00000000"));
                    shareButton.setEnabled(false);
                    isReplay = false;

                }

                startListeningProgress();

                if (canReplay)
                    showReplayButton();

                if (isPlay)
                {
                    pause();
                    playButton.setBackgroundResource(R.drawable.play);
                } else {
                    play();
                    playButton.setBackgroundResource(R.drawable.pause);
                }

                isPlay = !isPlay;
                canReplay = true;

                break;

            case R.id.meditationReplayButton:

                stop();
                play();
                isReplay = true;
                hideReplayButton();
                progress = 0;
                mProgressBar.setProgress(progress);
                playButton.setBackgroundResource(R.drawable.pause);
                isPlay = true;
                canReplay = false;
                startListeningProgress();

                break;

            case R.id.meditationShareButton:

                mainViewFlipper.addView(layoutInflater.inflate(R.layout.share_view, null));
                mainViewFlipper.showNext();
                mainViewFlipper.removeViewAt(0);

                break;

            case R.id.meditationSettingsButton:

                openSettBar();

                break;
        }

    }

    public void makeMeditationView() {

        mThread = null;
        canReplay = false;
        isPlay = false;
        melodyButton = null;
        isPlayerOnPause = false;
        progress = 0;
        mMediaPlayer = null;
        melodyPrepare();
        progressMax = MainActivity.timer * 60;
        mProgressBar.setMax(progressMax);
        mProgressBar.setProgress(progress);
        mProgressBar.setSecondaryProgress(progressMax);
        shareButton.setEnabled(false);
        shareButton.setBackgroundColor(Color.parseColor("#00000000"));

        mainViewFlipper.addView(meditationView);
        mainViewFlipper.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.go_prev_in));
        mainViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.go_prev_out));
        mainViewFlipper.showNext();
        mainViewFlipper.removeViewAt(0);

        playButton.setBackgroundResource(R.drawable.play);
        hideReplayButton();

    }

    private void openSettBar() {

        meditationSettingsSubBar = layoutInflater.inflate(R.layout.meditation_settings_sub_bar, null);

        okButton = (ImageButton) meditationSettingsBar.findViewById(R.id.meditationSettBarOKButton);
        xButton = (ImageButton) meditationSettingsBar.findViewById(R.id.meditationSettBarXButton);

        mLinearLayout.removeAllViews();
        mLinearLayout.addView(meditationSettingsBar);
        mLinearLayout.addView(meditationVolumeBar);

        okButton.setEnabled(false);
        xButton.setEnabled(true);

        okButton.setOnClickListener(MeditationManger.this);
        xButton.setOnClickListener(MeditationManger.this);
        volumeChangeBar.setMax(5);
        volumeChangeBar.setProgress(3);
        volumeChangeBar.setOnSeekBarChangeListener(MeditationManger.this);

        okButton.setBackgroundResource(R.drawable.main_drawer_chosen_button_background);
        xButton.setBackgroundResource(R.drawable.main_drawer_button_background);

    }

    private void openSettSubBar() {

        mLinearLayout.removeAllViews();
        mLinearLayout.addView(meditationSettingsBar);
        mLinearLayout.addView(meditationSettingsSubBar);
        mLinearLayout.addView(meditationVolumeBar);

        okButton.setEnabled(true);
        xButton.setEnabled(false);

        okButton.setOnClickListener(MeditationManger.this);

        meditationSettingsSubBar.findViewById(R.id.meditationSettSubBarMusicButton).setOnClickListener(MeditationManger.this);
        meditationSettingsSubBar.findViewById(R.id.meditationSettSubBarMusic2Button).setOnClickListener(MeditationManger.this);
        meditationSettingsSubBar.findViewById(R.id.meditationSettSubBarBirdButton).setOnClickListener(MeditationManger.this);
        meditationSettingsSubBar.findViewById(R.id.meditationSettSubBarSeaButton).setOnClickListener(MeditationManger.this);

        okButton.setBackgroundResource(R.drawable.main_drawer_button_background);
        xButton.setBackgroundResource(R.drawable.main_drawer_chosen_button_background);

        if (melodyButton != null)
        {

            melodyButton.setBackgroundResource(R.drawable.main_drawer_chosen_button_background);

        }


    }

    private void setMelodyButton(View v) {

        if (melodyButton != null)
        {

            melodyButton.setBackgroundResource(R.drawable.main_drawer_button_background);

        }

        melodyButton = v;
        melodyButton.setBackgroundResource(R.drawable.main_drawer_chosen_button_background);

    }

    private void startListeningProgress() {

        if (mThread != null && mThread.isAlive())
        {

            progressBarWork = false;
            mThread = null;

        } else {

            mThread = myThread();
            mThread.start();

        }

    }

    private Handler myHandler() {

        return new android.os.Handler() {
            @Override
            public void handleMessage(Message msg) {

                if (msg.what == progressMax)
                {

                    playButton.setBackgroundResource(R.drawable.replay);
                    hideReplayButton();
                    mProgressBar.setProgress(0);
                    shareButton.setBackgroundResource(R.drawable.share_med);
                    shareButton.setEnabled(true);
                    isPlay = false;
                    canReplay = false;

                } else {

                    progress = msg.what;
                    mProgressBar.setProgress(progress);

                }

            }
        };

    }

    private Thread myThread() {

        progressBarWork = true;
        return new Thread(new Runnable() {
            @Override
            public void run() {

                while (progress <= progressMax && progressBarWork)
                {

                    progress++;
                    mHandler.sendEmptyMessage(progress);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    void hideReplayButton() {

        replayButton.setBackgroundColor(Color.parseColor("#00000000"));
        replayButton.setEnabled(false);

    }

    void showReplayButton() {

        replayButton.setBackgroundResource(R.drawable.replay);
        replayButton.setEnabled(true);
        replayButton.setOnClickListener(MeditationManger.this);

    }

    private void play(){

        if (!isPlayerOnPause)
        {

            Uri melodyUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + resId);

            try {
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setDataSource(context, melodyUri);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {

            mMediaPlayer.start();

        }

        isPlayerOnPause = false;

    }

    private void melodyPrepare() {

        if (mMediaPlayer == null)
        {

            switch (timer) {
                case 1:
                    resId = R.raw.g_one_min_eng;
                    break;
                case 5:
                    resId = R.raw.g_five_mins_eng;
                    break;
                case 10:
                    resId = R.raw.g_ten_mins_eng;
                    break;
                case 20:
                    resId = R.raw.g_twenty_mins_eng;
                    break;
                case 0:
                    resId = R.raw.g_children_eng;
                    break;
            }

        } else {

            switch (melodyButton.getId())
            {

                case R.id.meditationSettSubBarMusicButton:
                    resId = R.raw.music1;
                    break;
                case R.id.meditationSettSubBarMusic2Button:
                    resId = R.raw.music2;
                    break;
                case R.id.meditationSettSubBarBirdButton:
                    resId = R.raw.birds;
                    break;
                case R.id.meditationSettSubBarSeaButton:
                    resId = R.raw.sea;
                    break;

            }

        }

    }

    private void pause() {

        if (mMediaPlayer.isPlaying())
        {

            mMediaPlayer.pause();
            isPlayerOnPause = true;

        }

    }

    public void stop() {

        if (mMediaPlayer != null && (mMediaPlayer.isPlaying() || isPlayerOnPause))
        {

            mMediaPlayer.stop();
            mMediaPlayer.release();
            isPlayerOnPause = false;

        }

    }

    private void onMelodyChooseButtonClick(View v) {

        setMelodyButton(v);
        melodyPrepare();
        stop();
        play();

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        if (mMediaPlayer != null)
        {

            mMediaPlayer.setVolume(progress * 20, progress * 20);

        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
