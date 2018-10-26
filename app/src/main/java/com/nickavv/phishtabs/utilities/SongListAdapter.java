package com.nickavv.phishtabs.utilities;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.nickavv.phishtabs.R;
import com.nickavv.phishtabs.TabViewer;
import com.nickavv.phishtabs.objects.Album;
import com.nickavv.phishtabs.objects.Song;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Nick on 11/8/2015.
 */
public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.SongViewHolder> implements SectionIndexer {

    private ArrayList<Song> data;

    private Context context;

    private Character[] sections;

    public SongListAdapter(ArrayList<Song> data, Context context) {
        this.data = new ArrayList<>(data);
        this.context = context;

        sections = new Character[36];
        int i = 0;
        for(char c :"#ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()) {
            sections[i] = Character.valueOf(c);
            i++;
        }
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.list_item_song, parent, false);
        SongViewHolder holder = new SongViewHolder(itemView);
        itemView.setOnClickListener(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(SongViewHolder holder, int position) {
        Song song = data.get(position);
        holder.songTitle.setText(song.getTitle());
        holder.bindSong(song);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public Object[] getSections() {
        return sections;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return 0;
    }

    @Override
    public int getSectionForPosition(int position) {
        if(position >= data.size() - 1) {
            position = data.size() - 1;
        }
        Song s = data.get(position);
        char first = s.getTitle().toUpperCase().charAt(0);
        if(first < 'A') {
            return 0;
        } else {
            return Arrays.asList(sections).indexOf(Character.valueOf(first));
        }
    }

    public class SongViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected TextView songTitle;

        private Song song;

        public SongViewHolder(View itemView) {
            super(itemView);

            songTitle = (TextView) itemView.findViewById(R.id.songTitle);
        }

        public void bindSong(Song song) {
            this.song = song;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, TabViewer.class);
            intent.putExtra("song", song);
            context.startActivity(intent);
        }
    }

    public Song removeItem(int position) {
        final Song model = data.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, Song model) {
        data.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final Song model = data.remove(fromPosition);
        data.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    public void animateTo(List<Song> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }
    private void applyAndAnimateRemovals(List<Song> newModels) {
        for (int i = data.size() - 1; i >= 0; i--) {
            final Song model = data.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }
    private void applyAndAnimateAdditions(List<Song> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final Song model = newModels.get(i);
            if (!data.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<Song> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final Song model = newModels.get(toPosition);
            final int fromPosition = data.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

}
