package com.example.l1k1.musicprojetl3i;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;

import java.util.ArrayList;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    private MediaPlayer mediaPlayer;
    private ArrayList<Song> songs;
    private int songPosn;
    private final IBinder musicBinder= new BinderInstanceMusic();

    @Override
    public void onCreate() {
        super.onCreate();
        songPosn = 0;
        mediaPlayer = new MediaPlayer();

        initMusicPlayer();

    }
    public void initMusicPlayer(){
        mediaPlayer.setWakeMode(getApplicationContext(),PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
    }
    public void setList(ArrayList<Song> theSongs){
        this.songs=theSongs;
    }
    public void playSong(){
        mediaPlayer.reset();
        Song playSong = songs.get(songPosn);
        long currentSong = playSong.getId();
        Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currentSong);
        try {
            mediaPlayer.setDataSource(getApplicationContext(), trackUri);
        }catch (Exception e){
            e.printStackTrace();
        }
        mediaPlayer.prepareAsync();
    }
    public void setSong(int songPosn){
        this.songPosn=songPosn;
    }
    @Override
    public IBinder onBind(Intent intent) {
        return musicBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mediaPlayer.stop();
        mediaPlayer.release();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }
    public class BinderInstanceMusic extends Binder {
        MusicService getService(){
            return MusicService.this;
        }
    }

}
