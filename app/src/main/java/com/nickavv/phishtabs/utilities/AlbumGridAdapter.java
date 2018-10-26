package com.nickavv.phishtabs.utilities;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.nickavv.phishtabs.objects.Album;

import java.util.ArrayList;

/**
 * Created by Nick on 11/8/2015.
 */
public class AlbumGridAdapter extends BaseAdapter {

    private ArrayList<Album> data;

    private Context context;

    public AlbumGridAdapter(ArrayList<Album> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return data.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(context);
            imageView.setAdjustViewBounds(true);

            //DiP conversion so it looks the same on all devices
//            final float scale = context.getResources().getDisplayMetrics().density;
//            int pixels = (int) (185 * scale + 0.5f);
//            imageView.setLayoutParams(new GridView.LayoutParams(pixels, pixels));

            //imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageDrawable(new BitmapDrawable(context.getResources(), data.get(position).getArt()));
        return imageView;
    }
}
