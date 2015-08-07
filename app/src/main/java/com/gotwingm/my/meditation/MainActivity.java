package com.gotwingm.my.meditation;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

<<<<<<< HEAD
import com.gotwingm.my.meditation.reminder.RemindersManager;
=======
>>>>>>> parent of 61e9f17... 07.08

public class MainActivity extends Activity implements View.OnClickListener, SurfaceHolder.Callback {

    public static final String RES_PATH = "android.resource://";
    public static final String DIR_SEPARATOR = "/";
    public static final String STRING_EXTRA = "string extra";

    public static int timer;
    public static int videoId;
    public static int kidIconId;
    public static int sunIconId;
    public static int playImageId;
    public static int pauseImageId;
    public static int replayImageId;
    public static MediaPlayer backgroundAudioPlayer;
    public static View activityMain;
    public static ViewFlipper mainViewFlipper;
    public static LayoutInflater layoutInflater;
    public static View aboutView;
    public static View remindersView;
    public static View mainView;
    public static View meditationView;
    public static Context context;
    public static ViewFlipper settingsFlipper;
    public static AudioManager audioManager;

    private static MediaPlayer backgroundVideoPlayer;

    private Locale mLocale;
    private String locale;
    private Uri video;
    private Uri audio;

    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private DisplayMetrics mDisplayMetrics;
    private RemindersManager mRemindersManager;
    private AboutViewManager mAboutViewManager;
    private MeditationManger mMeditationManger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        context = MainActivity.this;
        timeOfADayUiChange();
        prepareBackgroundVideo();

        audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        mainViewFlipper = (ViewFlipper) findViewById(R.id.mainViewFlipper);

        activityMain = layoutInflater.inflate(R.layout.activity_main, null);
        aboutView = layoutInflater.inflate(R.layout.about_view, null);
        mainView = layoutInflater.inflate(R.layout.main_view, null);
        remindersView = layoutInflater.inflate(R.layout.reminders_view, null);
        meditationView = layoutInflater.inflate(R.layout.meditation_view, null);

        mRemindersManager = new RemindersManager();
        mAboutViewManager = new AboutViewManager();
        mMeditationManger = new MeditationManger();

        Intent intent = getIntent();

        if (!TextUtils.isEmpty(intent.getStringExtra(STRING_EXTRA))) {
            timer = 1;
            makeMainView();
        } else {
            mAboutViewManager.makeAboutView();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mainRelativeLayout:
                settingsFlipper.removeAllViews();
                break;

            case R.id.settingsInfoButton:
                mAboutViewManager.makeAboutView();
                mainViewFlipper.showNext();
                mainViewFlipper.removeViewAt(0);
                break;

            case R.id.settingsLanguageButton:
                mainViewFlipper.addView(layoutInflater.inflate(R.layout.settings_language_view, null));
                mainViewFlipper.showNext();
                mainViewFlipper.removeViewAt(0);
                break;

            case R.id.englishSettingsButton:
                changeLocation(v);
                break;

            case R.id.alephSettingsButton:
                changeLocation(v);
                break;

            case R.id.settingsLanguageCloseButton:
                makeMainView();
                mainViewFlipper.removeViewAt(0);
                break;

            case R.id.settingsShareButton:
                mainViewFlipper.addView(layoutInflater.inflate(R.layout.share_view, null));
                mainViewFlipper.showNext();
                mainViewFlipper.removeViewAt(0);
                break;

            case R.id.shareCloseButton:
                makeMainView();
                mainViewFlipper.removeViewAt(0);
                break;

            case R.id.facebookShareButton:
                share(R.id.facebookShareButton);
                break;

            case R.id.twitterShareButton:
                share(R.id.twitterShareButton);
                break;

            case R.id.settingsRemindersButton:
                mRemindersManager.makeRemindersView();
                break;

            case R.id.remindersManagerCloseButton:
                makeMainView();
                mainViewFlipper.removeViewAt(0);
                break;

            case R.id.browserCloseButton:
                mainViewFlipper.showPrevious();
                mainViewFlipper.removeViewAt(1);
                break;

            case R.id.aboutViewCloseButton:
                makeMainView();
                mainViewFlipper.removeViewAt(0);
                mAboutViewManager.aboutViewFlipper.removeAllViews();
                break;

            case R.id.mainScreenLogo:
                mAboutViewManager.makeAboutView();
                mainViewFlipper.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.go_prev_in));
                mainViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.go_prev_out));
                mainViewFlipper.showNext();
                mainViewFlipper.removeViewAt(0);
                break;

            case R.id.closeButton:
                finish();

            case R.id.meditationSettingsButton:
                settingsFlipper = (ViewFlipper) findViewById(R.id.settingsFlipper);
                settingsFlipper.addView(layoutInflater.inflate(R.layout.main_settings_bar, null));
                settingsFlipper.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.sett_bar_in));
                settingsFlipper.showNext();
                break;

            case R.id.meditationBackButton:
                makeMainView();
                mainViewFlipper.removeViewAt(0);
                mMeditationManger.stop();
                mMeditationManger.progressBarWork = false;
                mMeditationManger.mLinearLayout.removeAllViews();

                if (mMeditationManger.secondMediaPlayer != null) {
                    mMeditationManger.secondMediaPlayer.stop();
                    mMeditationManger.secondMediaPlayer.release();
                    mMeditationManger.secondMediaPlayer = null;
                }
                break;

            case R.id.min1:
                timer = 1;
                mMeditationManger.makeMeditationView();
                break;

            case R.id.min5:
                timer = 5;
                mMeditationManger.makeMeditationView();
                break;

            case R.id.min10:
                timer = 10;
                mMeditationManger.makeMeditationView();
                break;

            case R.id.min20:
                timer = 20;
                mMeditationManger.makeMeditationView();
                break;

            case R.id.kids:
                timer = 5;
                mMeditationManger.makeMeditationView();
                break;
        }
    }

    public void makeMainView() {

        meditationView = layoutInflater.inflate(R.layout.meditation_view, null);
        View mainView = layoutInflater.inflate(R.layout.main_view, null);
        ((ImageView) mainView.findViewById(R.id.mainSunImage)).setImageResource(sunIconId);
        mainView.findViewById(R.id.mainBackButtonSunImage).setBackgroundResource(sunIconId);
        ((ImageView) mainView.findViewById(R.id.kidImageView)).setImageResource(kidIconId);

        mainViewFlipper.addView(mainView);
        mainViewFlipper.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.go_next_in));
        mainViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.go_next_out));
        mainViewFlipper.showNext();

    }

    private void share (int id) {

        final String FACEBOOK_LAUNCHER = "com.facebook.katana";
        final String TWITTER_LAUNCHER = "com.twitter.android.PostActivity";

        String text = ((EditText) findViewById(R.id.shareMessageEditText)).getText().toString();
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        List<ResolveInfo> activitiesList = getPackageManager().queryIntentActivities(shareIntent, 0);

        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);

        switch (id) {
            case R.id.facebookShareButton:
                for (ResolveInfo info : activitiesList) {
                    if (info.activityInfo.packageName.toLowerCase().startsWith(FACEBOOK_LAUNCHER)) {
                        ComponentName componentName = new ComponentName(info.activityInfo.packageName, info.activityInfo.name);
                        shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                        shareIntent.setComponent(componentName);
                        startActivity(shareIntent);
                        break;
                    }
                }

                Log.d("###", "Can't find facebook launcher");

                break;

            case R.id.twitterShareButton:

                for (ResolveInfo info : activitiesList) {

                    if (TWITTER_LAUNCHER.equals(info.activityInfo.name)) {

                        ComponentName componentName = new ComponentName(info.activityInfo.packageName, info.activityInfo.name);

                        shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                        shareIntent.setComponent(componentName);
                        startActivity(shareIntent);

                        break;
                    }

                }

                Log.d("###", "Can't find twitter launcher");

                break;
        }

    }

    private void prepareBackgroundVideo() {

        mDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);

        mSurfaceView = (SurfaceView) findViewById(R.id.backgroundVideoSurface);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);

        backgroundAudioPlayer = new MediaPlayer();
        backgroundVideoPlayer = new MediaPlayer();
        video = Uri.parse(RES_PATH + getPackageName() + DIR_SEPARATOR + videoId);
        audio = Uri.parse(RES_PATH + getPackageName() + DIR_SEPARATOR + R.raw.sea);

    }

    private void changeLocation(View langButton) {

        switch (langButton.getId()) {
            case R.id.englishSettingsButton:
                locale = "en";
                break;
            case R.id.alephSettingsButton:
                locale = "he";
                break;
        }

        mLocale = new Locale(locale);
        Locale.setDefault(mLocale);
        Configuration configuration = new Configuration();
        configuration.locale = mLocale;
        context.getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());

    }

    private void timeOfADayUiChange() {

        GregorianCalendar calendar = new GregorianCalendar();
        int hourOfADay = calendar.get(Calendar.HOUR_OF_DAY);

        playImageId = R.drawable.play;
        pauseImageId = R.drawable.pause;
        replayImageId = R.drawable.replay;

        if (hourOfADay >= 6 && hourOfADay <= 9) {
            videoId = R.raw.morning;
            kidIconId = R.drawable.smiley_morning;
            sunIconId = R.drawable.sun_morning;
        } else if (hourOfADay >= 10 && hourOfADay <= 14) {
            videoId = R.raw.noon;
            kidIconId = R.drawable.smiley_noon;
            sunIconId = R.drawable.sun_noon;
        } else if (hourOfADay >= 15 && hourOfADay <= 19) {
            videoId = R.raw.evening;
            kidIconId = R.drawable.smiley_evening;
            sunIconId = R.drawable.sun_evening;
        } else {
            videoId = R.raw.night;
            kidIconId = R.drawable.smiley_night;
            sunIconId = R.drawable.sun_night;
            playImageId = R.drawable.play_night;
            pauseImageId = R.drawable.pause_night;
            replayImageId = R.drawable.replay_night;
        }

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        mSurfaceHolder.setFixedSize(mDisplayMetrics.widthPixels, mDisplayMetrics.heightPixels);

        try {
            backgroundVideoPlayer.setDataSource(context, video);
            backgroundVideoPlayer.setDisplay(mSurfaceHolder);
            backgroundVideoPlayer.setLooping(true);
            backgroundVideoPlayer.prepare();
            backgroundVideoPlayer.start();
            backgroundAudioPlayer.setDataSource(context, audio);
            backgroundAudioPlayer.setLooping(true);
            backgroundAudioPlayer.prepare();
            backgroundAudioPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}