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
    int secondResId;
    boolean isPlayerOnPause;
    boolean progressBarWork;
    boolean isReplay;
    boolean canReplay;
    boolean isPlay;
    int progress;
    int progressMax;
    Thread mThread;
    android.os.Handler mHandler;

    MediaPlayer mainMediaPlayer;
    MediaPlayer secondMediaPlayer;
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
        meditationView.findViewById(R.id.meditationRelativLayout).setOnClickListener(MeditationManger.this);

        mProgressBar = (ProgressBar) meditationView.findViewById(R.id.progressBar);
        mLinearLayout = (LinearLayout) meditationView.findViewById(R.id.meditationSettingsLL);
        volumeChangeBar = (SeekBar) meditationVolumeBar.findViewById(R.id.volumeBar);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.meditationRelativLayout:

                mLinearLayout.removeAllViews();

                break;

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
                    stopListeningProgress();
                    pause();
                    playButton.setBackgroundResource(R.drawable.play);
                } else {
                    startListeningProgress();
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
                stopListeningProgress();
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
        mainMediaPlayer = null;
        melodyPrepare();
        progressMax = MainActivity.timer * 60;
        mProgressBar.setMax(progressMax);
        mProgressBar.setProgress(progress);
        mProgressBar.setSecondaryProgress(progressMax);
        shareButton.setEnabled(false);
        shareButton.setBackgroundColor(Color.parseColor("#00000000"));
        volumeChangeBar.setProgress(3);

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

    private void stopListeningProgress() {

        if (mThread != null && mThread.isAlive())
        {

            progressBarWork = false;
            mThread = null;

        }

    }

    private void startListeningProgress() {

        if (!progressBarWork)
        {

            mThread = myThread();
            mThread.start();
            progressBarWork = true;

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
                    progress = 0;
                    mProgressBar.setProgress(progress);
                    shareButton.setBackgroundResource(R.drawable.share_med);
                    shareButton.setEnabled(true);
                    isPlay = false;
                    canReplay = false;
                    isReplay = true;
                    progressBarWork = false;

                } else {

                    progress = msg.what;
                    mProgressBar.setProgress(progress);

                }

            }
        };

    }

    private Thread myThread() {

        return new Thread(new Runnable() {
            @Override
            public void run() {

                progressBarWork = true;

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
                mainMediaPlayer = new MediaPlayer();
                mainMediaPlayer.setDataSource(context, melodyUri);
                mainMediaPlayer.prepare();
                mainMediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {

            mainMediaPlayer.start();

        }

        isPlayerOnPause = false;

    }

    private void secondMelodyPrepare() {

        switch (melodyButton.getId())
        {

            case R.id.meditationSettSubBarMusicButton:
                secondResId = R.raw.music1;
                break;
            case R.id.meditationSettSubBarMusic2Button:
                secondResId = R.raw.music2;
                break;
            case R.id.meditationSettSubBarBirdButton:
                secondResId = R.raw.birds;
                break;
            case R.id.meditationSettSubBarSeaButton:
                secondResId = R.raw.sea;
                break;

        }

    }

    private void melodyPrepare() {

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

    }

    private void pause() {

        if (mainMediaPlayer.isPlaying())
        {

            mainMediaPlayer.pause();
            isPlayerOnPause = true;

        }

    }

    public void stop() {

        if (mainMediaPlayer != null && (mainMediaPlayer.isPlaying() || isPlayerOnPause))
        {

            mainMediaPlayer.stop();
            mainMediaPlayer.release();
            mainMediaPlayer = null;
            isPlayerOnPause = false;

        }

    }

    private void onMelodyChooseButtonClick(View v) {

        setMelodyButton(v);
        secondMelodyPrepare();

//        Uri melodyUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + secondResId);
//
//        try {
//            secondMediaPlayer = new MediaPlayer();
//            secondMediaPlayer.setDataSource(context, melodyUri);
//            secondMediaPlayer.prepare();
//            secondMediaPlayer.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        if (mainMediaPlayer != null)
        {

            mainMediaPlayer.setVolume(progress * 20, progress * 20);

        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
