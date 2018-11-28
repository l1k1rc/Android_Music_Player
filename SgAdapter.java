package com.example.l1k1.musicprojetl3i;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.linkin.musicprojectl3.R;
import com.example.l1k1.musicprojetl3i.MainActivity;

import java.util.ArrayList;

/**
 * Class which will be used by the ListView from the class Adapter
 */
public class SgAdapter extends BaseAdapter {

    //for the song parsed
    private ArrayList<Song> songs;
    //layout for the activity
    private LayoutInflater layoutInflater;

    //Called in MainActivity class
    public SgAdapter(Context c, ArrayList<Song> theSongs) {
        this.songs = theSongs;
        this.layoutInflater = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    //Build the view
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        @SuppressLint("ViewHolder") LinearLayout songLay = (LinearLayout)layoutInflater.inflate(R.layout.song, parent, false);
        final TextView songView = (TextView)songLay.findViewById(R.id.song_title);
        TextView artistView = (TextView)songLay.findViewById(R.id.song_artist);
        Song currSong = songs.get(position);
        songView.setText(currSong.getTitle());
        artistView.setText(currSong.getArtist());
        songLay.setTag(position);
        return songLay;
    }
}
