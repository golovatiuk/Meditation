package com.gotwingm.my.meditation;

import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;

import static android.widget.RelativeLayout.*;

/**
 *
 * @version 07.08.2015
 * @author Nickolay Golovatiuk
 */

public class MeditationManger extends MainActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private static final int FULL_ALIGN_BACKGROUND_COLOR = Color.parseColor("#00000000");

    public static MediaPlayer mainMediaPlayer;
    public static MediaPlayer secondMediaPlayer;

    public boolean isPlay;
    public boolean canReplay;
    public boolean progressBarWork;
    public LinearLayout settingsBarLinearLayout;

    private int resId;
    private int secondResId;
    private boolean isPlayerOnPause;
    private boolean isReplay;
    private boolean isSettingsSubBar;
    private int progress;
    private int progressMax;
    private int MPPosition;
    private Thread mThread;
    private android.os.Handler mHandler;
    private ProgressBar mProgressBar;
    private View meditationSettingsBar;
    private View meditationSettingsSubBar;
    private View meditationVolumeBar;
    private ImageView replayButton;
    private ImageView shareButton;
    private ImageButton okButton;
    private ImageButton xButton;
    private ImageButton playButton;
    private SeekBar volumeChangeBar;
    private static View melodyButton;

    MeditationManger() {

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.meditationRelativeLayout:
                settingsBarLinearLayout.removeAllViews();
                break;

            case R.id.meditationSettBarOKButton:

                isSettingsSubBar = false;

                if (secondMediaPlayer != null) {
                    secondMediaPlayer.pause();
//                    secondMediaPlayer.release();
//                    secondMediaPlayer = null;
//                    melodyButton.setBackgroundResource(R.drawable.general_button_background);
                }

                if (mainMediaPlayer != null && isPlay) {
                    mainMediaPlayer.start();
                    mainMediaPlayer.seekTo(MPPosition);
                } else {
//                    play();
                }
                openSettBar();
                break;

            case R.id.meditationSettBarXButton:
                isSettingsSubBar = true;
                openSettSubBar();

                if (melodyButton != null) {
                    secondMediaPlayerStart();
                }

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
                if (isReplay) {
                    shareButton.setBackgroundColor(FULL_ALIGN_BACKGROUND_COLOR);
                    shareButton.setEnabled(false);
                    isReplay = false;
                }

                if (isPlay) {
                    stopListening();
                    if (canReplay) {
                        showReplayButton();
                    }
                } else {
                    if (canReplay) {
                        hideReplayButton();
                    }
                    startListening();
                }

                isPlay = !isPlay;
                canReplay = true;
                break;

            case R.id.meditationReplayButton:
                onReplayButtonClick();
                break;

            case R.id.meditationShareButton:
                shareView = layoutInflater.inflate(R.layout.share_view, null);
                mainViewFlipper.addView(shareView);
                mainViewFlipper.showNext();
//                mainViewFlipper.removeViewAt(0);
                break;

            case R.id.mainSettingsButton:
                if (isSettingsSubBar) {
                    openSettSubBar();
                } else {
                    openSettBar();
                }
                break;

            case R.id.purchaseButton:
                preparePurchase();
                onBuyClick();
                break;
        }
    }

    public void makeMeditationView() {

        mHandler = myHandler();
        MPPosition = 0;

        meditationView = layoutInflater.inflate(R.layout.meditation_view, null);
        meditationVolumeBar = layoutInflater.inflate(R.layout.volume_bar, null);
        meditationSettingsBar = layoutInflater.inflate(R.layout.meditation_settings_bar, null);

        playButton = (ImageButton) meditationView.findViewById(R.id.meditationPlayButton);
        replayButton = (ImageView) meditationView.findViewById(R.id.meditationReplayButton);
        shareButton = (ImageView) meditationView.findViewById(R.id.meditationShareButton);

        playButton.setOnClickListener(MeditationManger.this);
        meditationView.findViewById(R.id.mainSettingsButton).setOnClickListener(MeditationManger.this);
        meditationView.findViewById(R.id.meditationRelativeLayout).setOnClickListener(MeditationManger.this);

        mProgressBar = (ProgressBar) meditationView.findViewById(R.id.progressBar);
        settingsBarLinearLayout = (LinearLayout) meditationView.findViewById(R.id.meditationSettingsLL);
        volumeChangeBar = (SeekBar) meditationVolumeBar.findViewById(R.id.volumeBar);
        settingsBarLinearLayout.findViewById(R.id.meditationSettingsLL).setOnClickListener(MeditationManger.this);

        if (timer == 10 || timer == 20) {
            showPurchaseScreen();
        } else {
            mThread = null;
            melodyButton = null;
            mainMediaPlayer = null;
            isPlayerOnPause = false;
            isSettingsSubBar = false;
            canReplay = false;
            isPlay = false;
            progress = 0;

            melodyPrepare();
            progressMax = MainActivity.timer * 60;
            mProgressBar.setMax(progressMax);
            mProgressBar.setProgress(progress);
            mProgressBar.setSecondaryProgress(progressMax);
            shareButton.setEnabled(false);
            shareButton.setBackgroundColor(FULL_ALIGN_BACKGROUND_COLOR);
            volumeChangeBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volumeChangeBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

            ((ImageView) meditationView.findViewById(R.id.meditationBackButtonSunImage)).setImageResource(sunIconId);

            mainViewFlipper.addView(meditationView);
            mainViewFlipper.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.go_prev_in));
            mainViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.go_prev_out));
            mainViewFlipper.showNext();
            ((TextView) meditationView.findViewById(R.id.listeningTextView)).setText(R.string.FiveMinutesMeditationTitle);
            ((TextView) meditationView.findViewById(R.id.meditationBackButtonTV)).setText(R.string.backButtonLabel);

            playButton.setBackgroundResource(playImageId);
            hideReplayButton();
        }
    }

    private void showPurchaseScreen() {

        View purchaseView;

        mainViewFlipper.addView(meditationView);
        mainViewFlipper.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.go_prev_in));
        mainViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.go_prev_out));
        mainViewFlipper.showNext();

        for (int i = 0; i < 5; i++) {
            ((RelativeLayout) meditationView.findViewById(R.id.meditationRelativeLayout)).removeViewAt(3);
        }

        purchaseView = layoutInflater.inflate(R.layout.purchase_view, null);
        ((RelativeLayout) meditationView.findViewById(R.id.meditationRelativeLayout)).addView(purchaseView);

//        purchaseView.findViewById(R.id.purchaseButton).setOnClickListener(MeditationManger.this);
//        purchaseView.findViewById(R.id.restorePurchaseButton).setOnClickListener(MeditationManger.this);

        meditationView.findViewById(R.id.mainSettingsButton).setEnabled(false);
    }

    public void stopListening() {

        stopListeningProgress();
        pause();
        playButton.setBackgroundResource(playImageId);

    }

    public void startListening() {

        startListeningProgress();
        play();
        playButton.setBackgroundResource(pauseImageId);

    }

    private void openSettBar() {

        meditationSettingsSubBar = layoutInflater.inflate(R.layout.meditation_settings_sub_bar, null);

        okButton = (ImageButton) meditationSettingsBar.findViewById(R.id.meditationSettBarOKButton);
        xButton = (ImageButton) meditationSettingsBar.findViewById(R.id.meditationSettBarXButton);

        settingsBarLinearLayout.removeAllViews();
        settingsBarLinearLayout.addView(meditationSettingsBar);
        settingsBarLinearLayout.addView(meditationVolumeBar);

        okButton.setEnabled(false);
        xButton.setEnabled(true);

        okButton.setOnClickListener(MeditationManger.this);
        xButton.setOnClickListener(MeditationManger.this);
        volumeChangeBar.setOnSeekBarChangeListener(MeditationManger.this);

        okButton.setBackgroundResource(R.drawable.general_chosen_button_background);
        xButton.setBackgroundResource(R.drawable.general_button_background);

    }

    private void openSettSubBar() {

        settingsBarLinearLayout.removeAllViews();
        settingsBarLinearLayout.addView(meditationSettingsBar);
        settingsBarLinearLayout.addView(meditationSettingsSubBar);
        settingsBarLinearLayout.addView(meditationVolumeBar);

        okButton.setEnabled(true);
        xButton.setEnabled(false);

        okButton.setOnClickListener(MeditationManger.this);

        meditationSettingsSubBar.findViewById(R.id.meditationSettSubBarMusicButton).setOnClickListener(MeditationManger.this);
        meditationSettingsSubBar.findViewById(R.id.meditationSettSubBarMusic2Button).setOnClickListener(MeditationManger.this);
        meditationSettingsSubBar.findViewById(R.id.meditationSettSubBarBirdButton).setOnClickListener(MeditationManger.this);
        meditationSettingsSubBar.findViewById(R.id.meditationSettSubBarSeaButton).setOnClickListener(MeditationManger.this);

        okButton.setBackgroundResource(R.drawable.general_button_background);
        xButton.setBackgroundResource(R.drawable.general_chosen_button_background);

        if (melodyButton != null) {
            meditationSettingsSubBar.findViewById(melodyButton.getId())
                    .setBackgroundResource(R.drawable.general_chosen_button_background);
        }
    }

    private void setMelodyButton(View v) {

        if (melodyButton != null) {
            melodyButton.setEnabled(true);
            meditationSettingsSubBar.findViewById(melodyButton.getId()).setBackgroundResource(R.drawable.general_button_background);
        }

        melodyButton = v;
        melodyButton.setBackgroundResource(R.drawable.general_chosen_button_background);
    }

    public void stopListeningProgress() {

        if (mThread != null && mThread.isAlive()) {
            progressBarWork = false;
            mThread = null;
        }
    }

    private void startListeningProgress() {

        if (!progressBarWork) {
            mThread = myThread();
            mThread.start();
            progressBarWork = true;
        }
    }

    private Handler myHandler() {

        return new android.os.Handler() {
            @Override
            public void handleMessage(Message msg) {

                if (msg.what == progressMax) {
                    playButton.setBackgroundResource(replayImageId);
                    hideReplayButton();
                    progress = 0;
                    mProgressBar.setProgress(progress);
                    shareButton.setBackgroundResource(R.drawable.share_med);
                    shareButton.setEnabled(true);
                    shareButton.setOnClickListener(MeditationManger.this);
                    MPPosition = 0;
                    isPlay = false;
                    canReplay = false;
                    isReplay = true;
                    progressBarWork = false;
                    if (secondMediaPlayer != null) {
                        secondMediaPlayer.stop();
                        secondMediaPlayer.release();
                        secondMediaPlayer = null;
                    }
                    stop();

                } else {
                    progress = msg.what;
                    mProgressBar.setProgress(progress);
                    MPPosition = progress * 1000;
                }
            }
        };
    }

    private Thread myThread() {

        return new Thread(new Runnable() {
            @Override
            public void run() {

                progressBarWork = true;

                while (progress <= progressMax && progressBarWork) {
                    for (int i = 0; i < 10; i++) {
                        if (progressBarWork) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } else {
                            return;
                        }
                    }
                    progress++;
                    mHandler.sendEmptyMessage(progress);

                }
            }
        });

    }

    private void hideReplayButton() {

        replayButton.setBackgroundColor(FULL_ALIGN_BACKGROUND_COLOR);
        replayButton.setEnabled(false);

    }

    public void showReplayButton() {

        replayButton.setBackgroundResource(replayImageId);
        replayButton.setEnabled(true);
        replayButton.setOnClickListener(MeditationManger.this);

    }

    private void play() {

        Uri mainMelodyUri;

        if (!isSettingsSubBar) {
            if (!isPlayerOnPause) {
                mainMelodyUri = Uri.parse(RES_PATH + context.getPackageName() + DIR_SEPARATOR + resId);
                backgroundAudioPlayer.pause();

                try {
                    mainMediaPlayer = new MediaPlayer();
                    mainMediaPlayer.setDataSource(context, mainMelodyUri);
                    mainMediaPlayer.prepare();
                    mainMediaPlayer.start();
                    mainMediaPlayer.seekTo(MPPosition);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                backgroundAudioPlayer.pause();
                mainMediaPlayer.start();
            }
        } else {
            backgroundAudioPlayer.pause();
            if (secondMediaPlayer != null)
            secondMediaPlayer.start();
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

    private void secondMediaPlayerStart() {

        Uri secondMelodyUri;

        secondMelodyUri = Uri.parse(RES_PATH + context.getPackageName() + DIR_SEPARATOR + secondResId);

        if (secondMediaPlayer == null) {
            try {
                secondMediaPlayer = new MediaPlayer();
                secondMediaPlayer.setDataSource(context, secondMelodyUri);
                secondMediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            try {
                secondMediaPlayer.stop();
                secondMediaPlayer.release();
                secondMediaPlayer = new MediaPlayer();
                secondMediaPlayer.setDataSource(context, secondMelodyUri);
                secondMediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (isPlay) {
            secondMediaPlayer.start();

            if (mainMediaPlayer != null) {
                mainMediaPlayer.pause();
            }
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

    public void pause() {

        backgroundAudioPlayer.start();

        if (mainMediaPlayer != null && mainMediaPlayer.isPlaying()) {
            mainMediaPlayer.pause();
            isPlayerOnPause = true;
        }

        if (secondMediaPlayer != null) {
            secondMediaPlayer.pause();
        }

    }

    private void onMelodyChooseButtonClick(View v) {

        setMelodyButton(v);
        secondMelodyPrepare();
        secondMediaPlayerStart();
        v.setEnabled(false);
    }

    private void onReplayButtonClick() {

        MPPosition = 0;
        stop();
        play();
        isReplay = true;
        hideReplayButton();
        progress = 0;
        mProgressBar.setProgress(progress);
        playButton.setBackgroundResource(pauseImageId);
        isPlay = true;
        canReplay = false;
        stopListeningProgress();
        startListeningProgress();
    }

    public void stop() {

        if ((mainMediaPlayer != null) && (mainMediaPlayer.isPlaying() || isPlayerOnPause)) {
            mainMediaPlayer.stop();
            mainMediaPlayer.release();
            mainMediaPlayer = null;
            isPlayerOnPause = false;
        }
        backgroundAudioPlayer.start();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
