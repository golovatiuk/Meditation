package com.gotwingm.my.meditation;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ViewFlipper;

import java.util.List;
import java.util.Locale;


public class MainActivity extends Activity implements View.OnClickListener{

    public static int timer;
    public static String STRING_EXTRA = "string extra";
    public static String TIME_EXTRA = "time";

    private Locale mLocale;
    private String locale;
//    private VideoView mVideoView;
//    private Uri video;

    RemindersManager mRemindersManager;
    AboutViewManager mAboutViewManager;
    MeditationManger mMeditationManger;

    public static View activityMain;
    public static ViewFlipper mainViewFlipper;
    public static LayoutInflater layoutInflater;
    public static View aboutView;
    public static View remindersView;
    public static View mainView;
    public static View meditationView;
    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

//        setBackgroundVideo();

        context = MainActivity.this;
        layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
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

            timer = intent.getIntExtra(TIME_EXTRA, 1);
            makeMainView();

        } else {

            mAboutViewManager.makeAboutView();

        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
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

                break;

            case R.id.settingsShareButton:

                mainViewFlipper.addView(layoutInflater.inflate(R.layout.share_view, null));
                mainViewFlipper.showNext();
                mainViewFlipper.removeViewAt(0);

                break;

            case R.id.shareCloseButton:

                makeMainView();

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

                mRemindersManager.createAlarms();
                makeMainView();

                break;

            case R.id.browserCloseButton:

                mainViewFlipper.showPrevious();
                mainViewFlipper.removeViewAt(1);

                break;

            case R.id.aboutViewCloseButton:

                makeMainView();
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

                ViewFlipper settingsFlipper = (ViewFlipper) findViewById(R.id.settingsFlipper);

                settingsFlipper.addView(layoutInflater.inflate(R.layout.main_settings_bar, null));
                settingsFlipper.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.sett_bar_in));
                settingsFlipper.showNext();

                break;

            case R.id.meditationBackButton:

                makeMainView();
                mMeditationManger.stop();
                mMeditationManger.progressBarWork = false;
                mMeditationManger.mLinearLayout.removeAllViews();

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

                timer = 0;

                mMeditationManger.makeMeditationView();

                break;
        }
    }

    public void makeMainView() {

        mainViewFlipper.addView(layoutInflater.inflate(R.layout.main_view, null));

        mainViewFlipper.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.go_next_in));
        mainViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.go_next_out));
        mainViewFlipper.showNext();

        mainViewFlipper.removeViewAt(0);

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

        switch (id)
        {
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

                break;
        }

    }

//    private void setBackgroundVideo() {
//
//        video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.noon);
//
//        mVideoView = (VideoView) findViewById(R.id.backgroundVideoView);
//        mVideoView.setVideoURI(video);
//        mVideoView.start();
//        mVideoView.
//
//    }

    private void changeLocation(View langButton) {

        switch (langButton.getId())
        {

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

}