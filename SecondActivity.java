package com.example.l1k1.musicprojetl3i;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.linkin.musicprojectl3.R;

public class SecondActivity extends AppCompatActivity {

    private String title;
    private String artist;

    private TextView titleView;
    private TextView artistView;

    private MusicController musicController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);

        titleView = (TextView)findViewById(R.id.titleView);
        artistView = (TextView)findViewById(R.id.artistView);
        Intent intent = getIntent();
        title = intent.getStringExtra(MainActivity.TITLE);
        artist = intent.getStringExtra(MainActivity.ARTIST);

        titleView.setText(title);
        artistView.setText(artist);
        //musicController.show(0);
    }
}
