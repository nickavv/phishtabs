package com.nickavv.phishtabs;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.nickavv.phishtabs.api.RestAPI;
import com.nickavv.phishtabs.objects.Song;
import com.nickavv.phishtabs.utilities.SongListAdapter;

import java.util.ArrayList;
import java.util.List;

public class SongListFragment extends Fragment implements SearchView.OnQueryTextListener {

    //private ProgressBar spinny;

    private RecyclerView songList;

    private SongListAdapter adapter;

    private ArrayList<Song> allData = new ArrayList<>();

    private ArrayList<Song> coversOnly = new ArrayList<>();

    private ArrayList<Song> liveOnly = new ArrayList<>();

    private ArrayList<Song> currentDataset; //A pointer to whichever set of data we're using right now (to make filtering work)

    private Spinner songTypeDropdown;

    public SongListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_bar, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_song_list, container, false);

        songList = (RecyclerView) v.findViewById(R.id.songList);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        songList.setLayoutManager(llm);
        adapter = new SongListAdapter(allData, getContext());
        songList.setAdapter(adapter);

        songTypeDropdown = (Spinner) getActivity().findViewById(R.id.spinner_song_nav);
        songTypeDropdown.setVisibility(View.VISIBLE);
        final ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.songnav, R.layout.spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        songTypeDropdown.setAdapter(spinnerAdapter);
        songTypeDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        currentDataset = allData;
                        adapter.animateTo(allData);
                        break;
                    case 1:
                        liveOnly();
                        break;
                    case 2:
                        coversOnly();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing
            }
        });

        if(savedInstanceState != null) {
            songTypeDropdown.setSelection(savedInstanceState.getInt("dropdownState"));
        } else {
            new songsGetter().execute();
        }

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("dropdownState", songTypeDropdown.getSelectedItemPosition());
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<Song> filteredModelList = filter(currentDataset, newText);
        adapter.animateTo(filteredModelList);
        songList.scrollToPosition(0);
        return true;
    }

    private List<Song> filter(List<Song> models, String query) {
        query = query.toLowerCase();

        final List<Song> filteredModelList = new ArrayList<>();
        for (Song model : models) {
            final String text = model.getTitle().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    private void coversOnly() {
        if(coversOnly.isEmpty()) {
            for(Song song : allData) {
                if(song.isCoverOnly()) {
                    coversOnly.add(song);
                }
            }
        }
        currentDataset = coversOnly;
        adapter.animateTo(coversOnly);
    }

    private void liveOnly() {
        if(liveOnly.isEmpty()) {
            for(Song song : allData) {
                if(song.isLiveOnly()) {
                    liveOnly.add(song);
                }
            }
        }
        currentDataset = liveOnly;
        adapter.animateTo(liveOnly);
    }

    private class songsGetter extends AsyncTask<Void, Void, ArrayList<Song>> {

        @Override
        protected ArrayList<Song> doInBackground(Void... params) {
            return RestAPI.getAllSongs();
        }

        @Override
        protected void onPostExecute(ArrayList<Song> result) {
            //spinny.setVisibility(View.GONE);
            allData = result;
            currentDataset = allData;
            adapter.animateTo(allData);
        }
    }
}
