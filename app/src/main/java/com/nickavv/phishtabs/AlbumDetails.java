package com.nickavv.phishtabs;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nickavv.phishtabs.api.RestAPI;
import com.nickavv.phishtabs.objects.Album;
import com.nickavv.phishtabs.objects.Song;
import com.nickavv.phishtabs.utilities.SongListAdapter;

import java.util.ArrayList;

public class AlbumDetails extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ProgressBar spinny;

    private RecyclerView songList;

    private SongListAdapter adapter;

    private Album album;

    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albumdetails);
        Intent intent = getIntent();
        album = (Album) intent.getSerializableExtra("album");

        ImageView headerImage = (ImageView) findViewById(R.id.header_image);
        headerImage.setImageDrawable(new BitmapDrawable(getResources(), album.getArt()));

        spinny = (ProgressBar) findViewById(R.id.progressSpinny);
        songList = (RecyclerView) findViewById(R.id.songList);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        songList.setLayoutManager(llm);
        adapter = new SongListAdapter(new ArrayList<Song>(), this);
        songList.setAdapter(adapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(album.getAlbumTitle());
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/music/album/" + album.getPlaystoreUrl()));
                if(!album.getPlaystoreUrl().equals("")) {
                    startActivity(intent);
                } else {
                    Snackbar snackbar = Snackbar.make(view, "This album is not available on Google Play Music", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spinny.setVisibility(View.VISIBLE);
        new songsByAlbumGetter().execute(album.getId());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Song song = (Song) parent.getItemAtPosition(position);
        Intent intent = new Intent(this, TabViewer.class);
        intent.putExtra("song", song);
        startActivity(intent);
    }

    private class songsByAlbumGetter extends AsyncTask<Long, Void, ArrayList<Song>> {

        @Override
        protected ArrayList<Song> doInBackground(Long... params) {
            return RestAPI.getSongsByAlbum(params[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<Song> result) {
            spinny.setVisibility(View.GONE);
            adapter.animateTo(result);
        }
    }
}
