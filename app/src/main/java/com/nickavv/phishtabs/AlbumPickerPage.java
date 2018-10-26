package com.nickavv.phishtabs;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.nickavv.phishtabs.api.RestAPI;
import com.nickavv.phishtabs.objects.Album;
import com.nickavv.phishtabs.utilities.AlbumGridAdapter;

import java.util.ArrayList;

/**
 * Created by Nick on 11/8/2015.
 */
public class AlbumPickerPage extends Fragment implements AdapterView.OnItemClickListener {

    private ProgressBar spinny;

    private Spinner songTypeDropdown;

    private GridView albumGrid;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_albumpickerpage, container, false);

        spinny = (ProgressBar) rootView.findViewById(R.id.progressSpinny);
        albumGrid = (GridView) rootView.findViewById(R.id.albumGrid);
        albumGrid.setOnItemClickListener(this);

        songTypeDropdown = (Spinner) getActivity().findViewById(R.id.spinner_song_nav);
        songTypeDropdown.setVisibility(View.INVISIBLE);

        spinny.setVisibility(View.VISIBLE);
        new albumsByArtistGetter().execute(getArguments().getString("artist"));

        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Album album = (Album) parent.getItemAtPosition(position);
        Intent intent = new Intent(getContext(), AlbumDetails.class);
        intent.putExtra("album", album);
        startActivity(intent);
    }

    private class albumsByArtistGetter extends AsyncTask<String, Void, ArrayList<Album>> {

        @Override
        protected ArrayList<Album> doInBackground(String... params) {
            return RestAPI.getAlbums(params[0], getContext());
        }

        @Override
        protected void onPostExecute(ArrayList<Album> result) {
            spinny.setVisibility(View.GONE);
            albumGrid.setAdapter(new AlbumGridAdapter(result, getContext()));
        }
    }

}
