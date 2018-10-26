package com.nickavv.phishtabs.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.nickavv.phishtabs.objects.Album;
import com.nickavv.phishtabs.objects.Song;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Nick on 9/12/2015.
 */
public class RestAPI {
    private static final String apiBaseUrl = "https://phishtabs.nicholasvv.com/";

    public static ArrayList<Album> getAlbums(String artist, Context context) {
        ArrayList<Album> result = new ArrayList<Album>();

        final String CACHE_FILE_PREFIX = "cached_albums_"+artist;

        //Download the album list, save it to cache, and then return it
        try {
            URL requestURL = new URL(apiBaseUrl + "albums/?artist=" + artist);
            HttpURLConnection connection = (HttpURLConnection) requestURL.openConnection();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            connection.getInputStream()));
            String jsonResponse = "";
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                jsonResponse += inputLine;
            }
            in.close();

            JSONArray jsonResult = new JSONArray(jsonResponse);
            for (int i = 0; i < jsonResult.length(); i++) {
                JSONObject row = jsonResult.getJSONObject(i);
                final String albumTitle = row.getString("name");

                final File cacheDir = context.getCacheDir();
                File[] cacheHit = cacheDir.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String filename) {
                        return filename.equals(CACHE_FILE_PREFIX + albumTitle);
                    }
                });
                if(cacheHit.length > 0) {
                    //We've already got this album cached! Good for us
                    try {
                        FileInputStream fis = new FileInputStream(cacheHit[0]);
                        ObjectInputStream ois = new ObjectInputStream(fis);
                        result.add((Album)ois.readObject());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Album album = new Album();
                    album.setAlbumTitle(row.getString("name"));

                    // Download album art (hopefully it's cached so this goes very fast)
                    String artUrl = row.getString("artURL");
                    try {
                        HttpURLConnection artConnection = (HttpURLConnection) new URL(artUrl).openConnection();
                        artConnection.connect();
                        InputStream input = artConnection.getInputStream();
                        Bitmap x = BitmapFactory.decodeStream(input);
                        album.setArt(x);
                    } catch (Exception e) {
                        //TODO: something
                        e.printStackTrace();
                    }

                    album.setPlaystoreUrl(row.getString("buyURL"));
                    album.setId(row.getLong("id"));
                    result.add(album);

                    //Save the resulting list to the cache for next time
                    FileOutputStream fos = new FileOutputStream(new File(cacheDir, CACHE_FILE_PREFIX + albumTitle));
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(album);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static ArrayList<Song> getSongsByAlbum(long albumId) {
        return getSongsFromUrl("songs/by-album/?album-id="+albumId);
    }

    public static ArrayList<Song> getAllSongs() {
        return getSongsFromUrl("songs/");
    }

    private static ArrayList<Song> getSongsFromUrl(String url) {
        ArrayList<Song> result = new ArrayList<>();

        try {
            URL requestURL = new URL(apiBaseUrl + url);
            HttpURLConnection connection = (HttpURLConnection) requestURL.openConnection();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            connection.getInputStream()));
            String jsonResponse = "";
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                jsonResponse += inputLine;
            }
            in.close();

            JSONArray jsonResult = new JSONArray(jsonResponse);
            for (int i = 0; i < jsonResult.length(); i++) {
                JSONObject row = jsonResult.getJSONObject(i);
                Song song = new Song();
                song.setTitle(row.getString("name"));
                song.setFileName(row.getString("filename"));
                song.setId(row.getLong("id"));
                song.setIsCoverOnly(row.getInt("isCover") == 1);
                song.setIsLiveOnly(row.getInt("isLive") == 1);
                result.add(song);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

}
