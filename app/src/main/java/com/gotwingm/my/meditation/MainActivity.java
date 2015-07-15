package com.gotwingm.my.meditation;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.Arrays;


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

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.link0:

                openLinks(0, R.id.link0);

                break;

            case R.id.link1:

                openLinks(1, R.id.link1);

                break;

            case R.id.link2:

                openLinks(2, R.id.link2);

                break;

            case R.id.link3:

                openLinks(3, R.id.link3);

                break;

            case R.id.link4:

                openLinks(4, R.id.link4);

                break;

            case R.id.link5:

                openLinks(5, R.id.link5);

                break;

            case R.id.browserCloseButton:

                mainViewFlipper.showPrevious();

                mainViewFlipper.removeViewAt(1);

                break;

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

//        ScrollView scrollView = (ScrollView) findViewById(R.id.aboutTextScrollView);

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

    private void openLinks(int index, int id) {

        String[] linksArray = getResources().getStringArray(R.array.links);

        mainViewFlipper.addView(mLayoutInflater.inflate(R.layout.browser_view, null));
        mainViewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.sett_bar_in));
        mainViewFlipper.showNext();

        WebView webView = (WebView) findViewById(R.id.browserWebView);
        ((TextView) findViewById(R.id.browserTitle)).setText(((TextView) findViewById(id)).getText());

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.setWebViewClient(new MyBrowser());
        webView.loadUrl(linksArray[index]);

    }

    private class MyBrowser extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}