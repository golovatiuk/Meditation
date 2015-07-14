package com.gotwingm.my.meditation;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.Image;
import android.os.Bundle;
import android.text.method.MovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;


public class MainActivity extends Activity implements View.OnClickListener{

    private static int timer;

    ViewFlipper mainViewFlipper;
    ViewFlipper aboutViewFlipper;
    LayoutInflater mLayoutInflater;
    Context mContext;

    float fromX;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mContext = getApplicationContext();
        mainViewFlipper = (ViewFlipper) findViewById(R.id.mainViewFlipper);
        mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        makeAboutFlipper();

        ((ImageView) findViewById(R.id.aboutBottomBarIndicator1)).setImageResource(R.drawable.indicator_activ);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.aboutViewCloseImageButton:

                mainViewFlipper.addView(mLayoutInflater.inflate(R.layout.main_screen, null));

                mainViewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.go_next_in));
                mainViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.go_next_out));
                mainViewFlipper.showNext();

                aboutViewFlipper.removeAllViews();

                break;

            case R.id.mainScreenLogo:

                mainViewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.go_prev_in));
                mainViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.go_prev_out));
                mainViewFlipper.showPrevious();

                ViewFlipper settingsFlipper2 = (ViewFlipper) findViewById(R.id.settingsFlipper);
                settingsFlipper2.removeAllViews();
                makeAboutFlipper();

                break;

            case R.id.mainScreenBackButton:
                finish();

            case R.id.mainSettingsButton:

                ViewFlipper settingsFlipper = (ViewFlipper) findViewById(R.id.settingsFlipper);

                settingsFlipper.addView(mLayoutInflater.inflate(R.layout.main_settings_bar, null));
                settingsFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.sett_bar_in));
                settingsFlipper.showNext();

                break;

            case R.id.min1:

                timer = 1;

                break;

            case R.id.min5:

                timer = 5;

                break;

            case R.id.min10:

                timer = 10;

                break;

            case R.id.min20:

                timer = 20;

                break;

            case R.id.kids:

                timer = 5;

                break;
        }
    }

    public ViewGroup.OnTouchListener setAboutViewFlipperOnTouchListener() {

        return new ViewGroup.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        fromX = event.getX();

                        break;

                    case MotionEvent.ACTION_UP:

                        float toX = event.getX();

                        if (fromX > toX) {

                            if (aboutViewFlipper.getDisplayedChild() == 2) {

                                break;

                            }

                            setNextIndicator(aboutViewFlipper.getDisplayedChild());

                            aboutViewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.go_next_in));
                            aboutViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext,R.anim.go_next_out));
                            aboutViewFlipper.showNext();

                        } else if (fromX < toX) {

                            if (aboutViewFlipper.getDisplayedChild() == 0) {

                                break;

                            }

                            setPreviousIndicator(aboutViewFlipper.getDisplayedChild());

                            aboutViewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.go_prev_in));
                            aboutViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.go_prev_out));
                            aboutViewFlipper.showPrevious();

                        }

                    default:
                        break;

                }

                return true;

            }
        };

    }

    private void makeAboutFlipper() {

        ((ImageView) findViewById(R.id.aboutBottomBarIndicator1)).setImageResource(R.drawable.indicator_activ);
        ((ImageView) findViewById(R.id.aboutBottomBarIndicator2)).setImageResource(R.drawable.indicator);
        ((ImageView) findViewById(R.id.aboutBottomBarIndicator3)).setImageResource(R.drawable.indicator);


        aboutViewFlipper = (ViewFlipper) findViewById(R.id.aboutViewFlipper);

        aboutViewFlipper.addView(mLayoutInflater.inflate(R.layout.about_text, null));
        aboutViewFlipper.addView(mLayoutInflater.inflate(R.layout.about_links, null));
        aboutViewFlipper.addView(mLayoutInflater.inflate(R.layout.about_authors, null));
        aboutViewFlipper.setOnTouchListener(setAboutViewFlipperOnTouchListener());

    }

    private void setNextIndicator(int index) {

        switch (index) {

            case 0:
                ((ImageView) findViewById(R.id.aboutBottomBarIndicator2)).setImageResource(R.drawable.indicator_activ);
                ((ImageView) findViewById(R.id.aboutBottomBarIndicator1)).setImageResource(R.drawable.indicator);
                break;
            case 1:
                ((ImageView) findViewById(R.id.aboutBottomBarIndicator3)).setImageResource(R.drawable.indicator_activ);
                ((ImageView) findViewById(R.id.aboutBottomBarIndicator2)).setImageResource(R.drawable.indicator);
                break;

        }
    }

    private void setPreviousIndicator(int index) {

        switch (index) {

            case 2:
                ((ImageView) findViewById(R.id.aboutBottomBarIndicator2)).setImageResource(R.drawable.indicator_activ);
                ((ImageView) findViewById(R.id.aboutBottomBarIndicator3)).setImageResource(R.drawable.indicator);
                break;
            case 1:
                ((ImageView) findViewById(R.id.aboutBottomBarIndicator1)).setImageResource(R.drawable.indicator_activ);
                ((ImageView) findViewById(R.id.aboutBottomBarIndicator2)).setImageResource(R.drawable.indicator);
                break;

        }
    }

}