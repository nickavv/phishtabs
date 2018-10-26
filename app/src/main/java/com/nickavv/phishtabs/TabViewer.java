package com.nickavv.phishtabs;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.nickavv.phishtabs.objects.Song;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class TabViewer extends AppCompatActivity {

    private Song song;

    WebView tabView;
    SeekBar scrollSpeedSlider;

    //used for auto-scroll
    boolean showScrollControls = false;
    int scrollSpeed = 0;
    private Handler mHandler=new Handler();
    Animation slideIn, slideOut;
    LinearLayout scrollControls;

    String url = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabview);

        song = (Song) getIntent().getSerializableExtra("song");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(song.getTitle());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        slideIn = AnimationUtils.loadAnimation(this, R.anim.animation_slidein);
        slideOut = AnimationUtils.loadAnimation(this, R.anim.animation_slideout);

        scrollControls = (LinearLayout) findViewById(R.id.autoscroll_control_holder);
        scrollControls.setVisibility(View.GONE);

        scrollSpeedSlider = (SeekBar) findViewById(R.id.autoscroll_slider);
        scrollSpeedSlider.setMax(200);
        scrollSpeedSlider.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                scrollSpeed = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });


        tabView = (WebView) findViewById(R.id.tabviewer);

        //tabView.getSettings().setLoadWithOverviewMode(true);
        tabView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        tabView.getSettings().setUseWideViewPort(true);
        tabView.getSettings().setSupportZoom(true);
        tabView.getSettings().setBuiltInZoomControls(true);
        url = "http://emilstabs.org/files/tabs/" + song.getFileName();
        new Thread(new notFoundChecker()).start();
        tabView.loadUrl(url);

        tabView.post(new Runnable()
        {
            public void run()
            {
                if(scrollSpeed != 0) {
                    tabView.scrollBy(0, 1);
                    tabView.invalidate();
                }
                mHandler.postDelayed(this, 1000/(scrollSpeed+1));
            }
        });

    }

    // trigger sending a report email when we get 404s
    private void sendMail(String title) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "talkto@nicholasvv.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Phish Tabs Report: Missing File!");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi Nicholas, the tab file for " + title + " is missing, resulting in a 404! Please fix it as soon as possible");
        startActivity(Intent.createChooser(emailIntent, "Oops! Report this missing file please!"));
    }

    //Makes the auto-scroll control show or hide
    private void toggleScrollDrawer() {
        if(showScrollControls) {
            scrollControls.startAnimation(slideOut);
            scrollControls.setVisibility(View.GONE);
            showScrollControls = false;
        } else {
            scrollControls.setVisibility(View.VISIBLE);
            scrollControls.startAnimation(slideIn);
            showScrollControls = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tabviewer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // same as using a normal menu
        switch(item.getItemId()) {
            case R.id.action_top:
                tabView.scrollTo(0,0);
                break;
            case R.id.action_autoscroll:
                toggleScrollDrawer();
                break;
            case android.R.id.home:
                // This is called when the Home (Up) button is pressed
                // in the Action Bar.
                finish();
                return true;
        }

        return true;
    }

    private class notFoundChecker implements Runnable {

        @Override
        public void run() {
            URL requestURL = null;
            try {
                requestURL = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) requestURL.openConnection();
                if(connection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                    sendMail(song.getTitle());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}