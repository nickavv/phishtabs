package com.nickavv.phishtabs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.http.HttpResponseCache;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private DrawerLayout mDrawerLayout;

    private Toolbar toolbar;

    private int navTab = 0;

    private Fragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Install the HTTP cache (we need to cache album arts or we'll be very sad)
        try {
            File httpCacheDir = new File(getApplicationContext().getCacheDir(), "http");
            long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
            HttpResponseCache.install(httpCacheDir, httpCacheSize);
        } catch (IOException e) {
                Log.d("PhishTabs", "HTTP response cache installation failed:" + e);
        }

        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if(activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            new AlertDialog.Builder(this, R.style.AppDialogTheme)
                    .setTitle(R.string.title_nonet)
                    .setMessage(R.string.text_nonet)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .show();
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ListView mDrawerList = (ListView) findViewById(R.id.navDrawer);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                this,  mDrawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        mDrawerList.setAdapter(new ArrayAdapter<String>(
                this,
                R.layout.activity_navbaritem,
                android.R.id.text1,
                getResources().getStringArray(R.array.navdrawer_list)));
        mDrawerList.setItemChecked(0, true);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mainFragment = getSupportFragmentManager().findFragmentById(R.id.container);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, mainFragment == null ? new AlbumPicker() : mainFragment)
                .commit();
        if(mainFragment == null || mainFragment instanceof AlbumPicker) {
            getSupportActionBar().setTitle(R.string.navtitle_albums);
        } else if(mainFragment instanceof SongListFragment) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id) {
            case R.id.action_about:
                new AlertDialog.Builder(this, R.style.AppDialogTheme)
                .setTitle(R.string.about_title)
                .setMessage(R.string.about_text)
                .setPositiveButton(android.R.string.ok, null)
                .show();
                break;
            case R.id.action_report_tab:
                sendMail(true);
                break;
            case R.id.action_report_bug:
                sendMail(false);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendMail(boolean tab) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "talkto@nicholasvv.com", null));
        if(tab) {
            //missing tab report
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Phish Tabs Report: Missing Song!");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi Nicholas, Phish Tabs is missing a song that is present on http://emilstabs.org/. It's [song title here]. Please fix it!");
        } else {
            //bug report
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Phish Tabs Report: Bug!");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi Nicholas, I found a bug in Phish Tabs! It's [describe bug here]. Please fix it!");
        }
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    public void selectItem(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (position) {
            case 0: fragmentManager.beginTransaction()
                    .replace(R.id.container,new AlbumPicker())
                    .commit();
                    getSupportActionBar().setDisplayShowTitleEnabled(true);
                    getSupportActionBar().setTitle(R.string.navtitle_albums);
                    break;
            case 1: fragmentManager.beginTransaction()
                    .replace(R.id.container, new SongListFragment())
                    .commit();
                    getSupportActionBar().setDisplayShowTitleEnabled(false);
                    break;
        }
        navTab = position;
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onStop() {
        super.onStop();

        //Flush cache entries out to disk so they'll be readable next time we open
        HttpResponseCache cache = HttpResponseCache.getInstalled();
        if (cache != null) {
            cache.flush();
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }
}
