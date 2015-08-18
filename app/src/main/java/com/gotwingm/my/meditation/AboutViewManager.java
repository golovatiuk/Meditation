package com.gotwingm.my.meditation;

import android.content.Intent;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;


public class AboutViewManager extends MainActivity implements View.OnClickListener, View.OnTouchListener {

    protected ViewFlipper aboutViewFlipper;

    private static float fromX;
    private View linksView;
    private View textView;
    private View creditsView;

    AboutViewManager() {
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
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

            case R.id.producer:
                mailToCreators(R.id.producer);
                break;

            case R.id.meditationGuidance:
                mailToCreators(R.id.meditationGuidance);
                break;

            case R.id.design:
                mailToCreators(R.id.design);
                break;

            case R.id.music:
                mailToCreators(R.id.music);
                break;

            default:
                break;
        }
    }

    public void makeAboutView() {

        aboutView = layoutInflater.inflate(R.layout.about_view, null);

        aboutViewFlipper = (ViewFlipper) aboutView.findViewById(R.id.aboutViewFlipper);
        linksView = layoutInflater.inflate(R.layout.about_links, null);
        textView = layoutInflater.inflate(R.layout.about_text, null);
        creditsView = layoutInflater.inflate(R.layout.about_credits, null);

        mainViewFlipper.addView(aboutView);
        aboutViewFlipper.addView(textView);
        aboutViewFlipper.addView(linksView);
        aboutViewFlipper.addView(creditsView);
        aboutViewFlipper.setOnTouchListener(AboutViewManager.this);

        linksView.findViewById(R.id.link0).setOnClickListener(AboutViewManager.this);
        linksView.findViewById(R.id.link1).setOnClickListener(AboutViewManager.this);
        linksView.findViewById(R.id.link2).setOnClickListener(AboutViewManager.this);
        linksView.findViewById(R.id.link3).setOnClickListener(AboutViewManager.this);
        linksView.findViewById(R.id.link4).setOnClickListener(AboutViewManager.this);
        linksView.findViewById(R.id.link5).setOnClickListener(AboutViewManager.this);

        creditsView.findViewById(R.id.producer).setOnClickListener(AboutViewManager.this);
        creditsView.findViewById(R.id.meditationGuidance).setOnClickListener(AboutViewManager.this);
        creditsView.findViewById(R.id.design).setOnClickListener(AboutViewManager.this);
        creditsView.findViewById(R.id.music).setOnClickListener(AboutViewManager.this);

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                fromX = event.getX();
                break;

            case MotionEvent.ACTION_MOVE:
                break;

            case MotionEvent.ACTION_UP:
                final float toX = event.getX();

                if (fromX > toX) {
                    if (aboutViewFlipper.getDisplayedChild() == 2) {
                        break;
                    }

                    setNextIndicator(aboutViewFlipper.getDisplayedChild());
                    aboutViewFlipper.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.go_next_in));
                    aboutViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(context,R.anim.go_next_out));
                    aboutViewFlipper.showNext();

                } else if (fromX < toX) {
                    if (aboutViewFlipper.getDisplayedChild() == 0) {
                        break;
                    }

                    setPreviousIndicator(aboutViewFlipper.getDisplayedChild());
                    aboutViewFlipper.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.go_prev_in));
                    aboutViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.go_prev_out));
                    aboutViewFlipper.showPrevious();
                }

            default: break;
        }
        return true;
    }

    private void setNextIndicator(int index) {

        switch (index) {
            case 0:
                ((ImageView) aboutView.findViewById(R.id.aboutBottomBarIndicator2)).setImageResource(R.drawable.about_indicator_active);
                ((ImageView) aboutView.findViewById(R.id.aboutBottomBarIndicator1)).setImageResource(R.drawable.about_indicator);
                break;

            case 1:
                ((ImageView) aboutView.findViewById(R.id.aboutBottomBarIndicator3)).setImageResource(R.drawable.about_indicator_active);
                ((ImageView) aboutView.findViewById(R.id.aboutBottomBarIndicator2)).setImageResource(R.drawable.about_indicator);
                break;
        }
    }

    private void setPreviousIndicator(int index) {

        switch (index) {
            case 2:
                ((ImageView) aboutView.findViewById(R.id.aboutBottomBarIndicator2)).setImageResource(R.drawable.about_indicator_active);
                ((ImageView) aboutView.findViewById(R.id.aboutBottomBarIndicator3)).setImageResource(R.drawable.about_indicator);
                break;

            case 1:
                ((ImageView) aboutView.findViewById(R.id.aboutBottomBarIndicator1)).setImageResource(R.drawable.about_indicator_active);
                ((ImageView) aboutView.findViewById(R.id.aboutBottomBarIndicator2)).setImageResource(R.drawable.about_indicator);
                break;
        }
    }

    public void openLinks(int index, int id) {

        String[] linksArray = context.getResources().getStringArray(R.array.links);
        View browserView = layoutInflater.inflate(R.layout.browser_view, null);

        mainViewFlipper.addView(browserView);
        mainViewFlipper.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.sett_bar_in));
        mainViewFlipper.showNext();

        backgroundAudioPlayer.pause();

        WebView webView = (WebView) browserView.findViewById(R.id.browserWebView);
        ((TextView) browserView.findViewById(R.id.browserTitle)).setText(((TextView) linksView.findViewById(id)).getText());
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.setWebViewClient(new MyBrowser());
        webView.loadUrl(linksArray[index]);

    }

    private void mailToCreators(int id) {
        Intent emailIntent;
        emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:"));

        switch (id) {
            case R.id.producer:
                emailIntent.putExtra(Intent.EXTRA_EMAIL, "kereny70@gmail.com");
                break;

            case R.id.design:
                emailIntent.putExtra(Intent.EXTRA_EMAIL, "nadia_g@netvision.net.il");
                break;

            default:
                break;
        }

        emailIntent.putExtra(Intent.EXTRA_SUBJECT, " ");
        if (emailIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(emailIntent);
        }
    }

    private class MyBrowser extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

}
