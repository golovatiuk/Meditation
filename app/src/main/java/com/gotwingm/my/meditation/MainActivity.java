package com.gotwingm.my.meditation;

import android.app.Activity;
import android.app.AlertDialog;
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

import reminder.RemindersManager;

/**
 * Class of the Main Activity.
 *
 * @version 07.08.2015
 * @author Nikolay Golovatiuk
 */
public class MainActivity extends Activity implements View.OnClickListener, SurfaceHolder.Callback {


    /** Raw resources path for uri parse */
    public static final String RES_PATH = "android.resource://";

    /** Raw resources path directory separator for uri parse */
    public static final String DIR_SEPARATOR = "/";

    /** Intent string extra key */
    public static final String STRING_EXTRA = "string extra";

    /** Duration of meditation */
    public static int timer;

    /** Background video ID */
    public static int videoId;

    /** Kid's meditation icon ID */
    public static int kidIconId;

    /** Sun image ID for general BACK button & main logo backgrounds */
    public static int sunIconId;

    /** Meditation play button ID */
    public static int playImageId;

    /** Meditation pause button ID */
    public static int pauseImageId;

    /** Meditation replay button ID */
    public static int replayImageId;

    /** Background audio's MediaPlayer */
    public static MediaPlayer backgroundAudioPlayer;

    /** MainActivity View with background video's SurfaceHolder */
    public static View activityMain;

    /** MainActivity's ViewFlipper */
    public static ViewFlipper mainViewFlipper;

    /** General system LayoutInflater service */
    public static LayoutInflater layoutInflater;

    /** About screen View */
    public static View aboutView;

    /** Reminders screen View */
    public static View remindersView;

    /** Main screen VIew */
    public static View mainView;

    /** Meditation screen View */
    public static View meditationView;

    /** Application Context */
    public static Context context;

    /** Main screen settings bar's ViewFlipper */
    public static ViewFlipper settingsFlipper;

    /** General AudioManager */
    public static AudioManager audioManager;

    /** Background video's MediaPlayer */
    private static MediaPlayer backgroundVideoPlayer;

    private static Locale locale;

    /** Object of ReminderManager class */
    private RemindersManager mRemindersManager;

    /** Object of AboutViewManager class */
    private AboutViewManager mAboutViewManager;

    /** Object of MeditationManager class */
    private MeditationManger mMeditationManger;

    /** Background video's SurfaceHolder */
    private SurfaceHolder mSurfaceHolder;

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
            makeMainView();
        } else {
            mAboutViewManager.makeAboutView();
        }
    }

    /**
     * Called as part of the activity lifecycle when an activity is going into
     * the background, but has not (yet) been killed.  The counterpart to
     * {@link #onResume}.
     * <p/>
     * <p>When activity B is launched in front of activity A, this callback will
     * be invoked on A.  B will not be created until A's {@link #onPause} returns,
     * so be sure to not do anything lengthy here.
     * <p/>
     * <p>This callback is mostly used for saving any persistent state the
     * activity is editing, to present a "edit in place" model to the user and
     * making sure nothing is lost if there are not enough resources to start
     * the new activity without first killing this one.  This is also a good
     * place to do things like stop animations and other things that consume a
     * noticeable amount of CPU in order to make the switch to the next activity
     * as fast as possible, or to close resources that are exclusive access
     * such as the camera.
     * <p/>
     * <p>In situations where the system needs more memory it may kill paused
     * processes to reclaim resources.  Because of this, you should be sure
     * that all of your state is saved by the time you return from
     * this function.  In general {@link #onSaveInstanceState} is used to save
     * per-instance state in the activity and this method is used to store
     * global persistent data (in content providers, files, etc.)
     * <p/>
     * <p>After receiving this call you will usually receive a following call
     * to {@link #onStop} (after the next activity has been resumed and
     * displayed), however in some cases there will be a direct call back to
     * {@link #onResume} without going through the stopped state.
     * <p/>
     * <p><em>Derived classes must call through to the super class's
     * implementation of this method.  If they do not, an exception will be
     * thrown.</em></p>
     *
     * @see #onResume
     * @see #onSaveInstanceState
     * @see #onStop
     */
    @Override
    protected void onPause() {
        super.onPause();
        if(mMeditationManger.isPlay) {
            mMeditationManger.stopListening();
            mMeditationManger.isPlay = !mMeditationManger.isPlay;
            mMeditationManger.canReplay = true;
        }

        if (backgroundAudioPlayer.isPlaying()) {
            backgroundAudioPlayer.stop();
            backgroundAudioPlayer.release();
            backgroundAudioPlayer = null;
        }

        if (backgroundVideoPlayer.isPlaying()) {
            backgroundVideoPlayer.stop();
            backgroundVideoPlayer.release();
            backgroundVideoPlayer = null;
            mSurfaceHolder.removeCallback(this);
            mSurfaceHolder = null;
        }
    }

    /**
     * Called after {@link #onRestoreInstanceState}, {@link #onRestart}, or
     * {@link #onPause}, for your activity to start interacting with the user.
     * This is a good place to begin animations, open exclusive-access devices
     * (such as the camera), etc.
     * <p/>
     * <p>Keep in mind that onResume is not the best indicator that your activity
     * is visible to the user; a system window such as the keyguard may be in
     * front.  Use {@link #onWindowFocusChanged} to know for certain that your
     * activity is visible to the user (for example, to resume a game).
     * <p/>
     * <p><em>Derived classes must call through to the super class's
     * implementation of this method.  If they do not, an exception will be
     * thrown.</em></p>
     *
     * @see #onRestoreInstanceState
     * @see #onRestart
     * @see #onPostResume
     * @see #onPause
     */
    @Override
    protected void onResume() {
        super.onResume();
        timeOfADayUiChange();
        prepareBackgroundVideo();
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
                mMeditationManger.settingsBarLinearLayout.removeAllViews();

                if (MeditationManger.secondMediaPlayer != null) {
                    MeditationManger.secondMediaPlayer.stop();
                    MeditationManger.secondMediaPlayer.release();
                    MeditationManger.secondMediaPlayer = null;
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

    /**
     * Generate and show main view as current
     */
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

    /**
     * Handle a facebook or twitter share button choose
     *
     * @param id id of facebook's or twitter's share button
     */
    private void share (int id) {

        final String FACEBOOK_LAUNCHER = "com.facebook.katana";
        final String TWITTER_LAUNCHER = "com.twitter.android.PostActivity";

        ComponentName componentName;
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
                        componentName = new ComponentName(info.activityInfo.packageName, info.activityInfo.name);
                        shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                        shareIntent.setComponent(componentName);
                        startActivity(shareIntent);
                        break;
                    }
                }
                shareAlertDialog();
                break;

            case R.id.twitterShareButton:
                for (ResolveInfo info : activitiesList) {
                    if (TWITTER_LAUNCHER.equals(info.activityInfo.name)) {
                        componentName = new ComponentName(info.activityInfo.packageName, info.activityInfo.name);
                        shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                        shareIntent.setComponent(componentName);
                        startActivity(shareIntent);
                        break;
                    }
                }
                shareAlertDialog();
                break;
        }
    }

    /**
     * Generate background video & audio MediaPlayer
     * Set video player to background SurfaceView
     */
    private void prepareBackgroundVideo() {

        Uri videoUri;
        Uri audioUri;
        SurfaceView surfaceView;
        DisplayMetrics displayMetrics;

        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        surfaceView = (SurfaceView) findViewById(R.id.backgroundVideoSurface);
        mSurfaceHolder = surfaceView.getHolder();
        mSurfaceHolder.setFixedSize(displayMetrics.widthPixels, displayMetrics.heightPixels);
        mSurfaceHolder.addCallback(this);

        videoUri = Uri.parse(RES_PATH + getPackageName() + DIR_SEPARATOR + videoId);
        audioUri = Uri.parse(RES_PATH + getPackageName() + DIR_SEPARATOR + R.raw.sea);

        backgroundAudioPlayer = new MediaPlayer();
        backgroundVideoPlayer = new MediaPlayer();
        try {
            backgroundVideoPlayer.setDataSource(context, videoUri);
            backgroundVideoPlayer.setLooping(true);
            backgroundAudioPlayer.setDataSource(context, audioUri);
            backgroundAudioPlayer.setLooping(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Back button's press handler
     */
    @Override
    public void onBackPressed() {
        //Do nothing when Back button was pressed
    }

    /**
     * Start to play background video & audio MediaPlayer
     */
    private void startBackgroundVideo() {

        try {
            backgroundVideoPlayer.setDisplay(mSurfaceHolder);
            backgroundVideoPlayer.prepare();
            backgroundVideoPlayer.start();
            backgroundAudioPlayer.prepare();
            backgroundAudioPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Location chang's method
     *
     * @param langButton which location chang's button clicked
     */
    private void changeLocation(View langButton) {

        String location = null;
        Configuration configuration = new Configuration();

        switch (langButton.getId()) {
            case R.id.englishSettingsButton:
                location = "en";
                break;
            case R.id.alephSettingsButton:
                location = "he";
                break;
        }

        locale = new Locale(location);
        Locale.setDefault(locale);
        configuration.locale = locale;
        context.getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());

    }

    /**
     * Called by the system when the device configuration changes while your
     * activity is running.  Note that this will <em>only</em> be called if
     * you have selected configurations you would like to handle with the
     * {@link android.R.attr#configChanges} attribute in your manifest.  If
     * any configuration change occurs that is not selected to be reported
     * by that attribute, then instead of reporting it the system will stop
     * and restart the activity (to have it launched with the new
     * configuration).
     * <p/>
     * <p>At the time that this function has been called, your Resources
     * object will have been updated to return resource values matching the
     * new configuration.
     *
     * @param newConfig The new device configuration.
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
        if (locale != null) {
            newConfig.locale = locale;
            Locale.setDefault(locale);
            context.getResources().updateConfiguration(newConfig, context.getResources().getDisplayMetrics());
        }
    }

    /**
     * Prepare UI elements regarded of the part of the day
     */
    private void timeOfADayUiChange() {

        GregorianCalendar calendar = new GregorianCalendar();
        int hourOfADay = calendar.get(Calendar.HOUR_OF_DAY);

        playImageId = R.drawable.meditation_play;
        pauseImageId = R.drawable.meditation_pause;
        replayImageId = R.drawable.meditation_replay;

        if (hourOfADay >= 6 && hourOfADay <= 9) {
            videoId = R.raw.morning;
            kidIconId = R.drawable.general_smiley_morning;
            sunIconId = R.drawable.general_sun_morning;
        } else if (hourOfADay >= 10 && hourOfADay <= 14) {
            videoId = R.raw.noon;
            kidIconId = R.drawable.general_smiley_noon;
            sunIconId = R.drawable.general_sun_noon;
        } else if (hourOfADay >= 15 && hourOfADay <= 19) {
            videoId = R.raw.evening;
            kidIconId = R.drawable.general_smiley_evening;
            sunIconId = R.drawable.general_sun_evening;
        } else {
            videoId = R.raw.night;
            kidIconId = R.drawable.general_smiley_night;
            sunIconId = R.drawable.general_sun_night;
            playImageId = R.drawable.meditation_play_night;
            pauseImageId = R.drawable.meditation_pause_night;
            replayImageId = R.drawable.meditation_replay_night;
        }

    }

    /**
     * Show share's failed alert dialog
     */
    private void shareAlertDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getResources().getString(R.string.postingFailedTitle));
        builder.setMessage(getResources().getString(R.string.postingFailedMessage));
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    /**
     * Start background video & audio playing when SurfaceHolder was creating
     *
     * @param holder background video's SurfaceHolder
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startBackgroundVideo();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }
}