package com.example.l1k1.musicprojetl3i;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.linkin.musicprojectl3.R;

import java.util.ArrayList;
import java.util.Random;

/**
 * {@link MusicService} is the class which will read a music selected by the user in a service activity, when
 * the app is closed, but not destroyed, the service can continue to read the music
 */
public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    private boolean shuffle=false;
    private Random rand;

    private String songTitle="";
    private static final int NOTIFY_ID=1;

    private MediaPlayer mediaPlayer;
    private ArrayList<Song> songs;
    private int songPosn;
    private final IBinder musicBinder= new BinderInstanceMusic();

    @Override
    public void onCreate() {
        super.onCreate();
        songPosn = 0;
        mediaPlayer = new MediaPlayer();

        mediaPlayer.setWakeMode(getApplicationContext(),PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        rand= new Random();
    }
    public void setList(ArrayList<Song> theSongs){
        this.songs=theSongs;
    }
    //To pay a song bye the ID
    public void playSong(){
        mediaPlayer.reset();
        Song playSong = songs.get(songPosn);
        songTitle=playSong.getTitle();
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
        if(mediaPlayer.getCurrentPosition()>0){
            mp.reset();
            playNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        Log.d("HEEEEEEE", "MARCHE OU PAS");
        Intent notIntent = new Intent(MusicService.this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(MusicService.this, 0, notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder noBuilder = new Notification.Builder(MusicService.this);

        noBuilder.setContentIntent(pendingIntent).setSmallIcon(R.drawable.play3).setTicker("test").setOngoing(true).setContentTitle("Playing").setContentText("test");
        Notification notification = new Notification();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            notification = noBuilder.build();
        }

         startForeground(1, notification);
    }
    class BinderInstanceMusic extends Binder {
        MusicService getService(){
            return MusicService.this;
        }
    }
    public int getPosn(){
        return mediaPlayer.getCurrentPosition();
    }

    public int getDur(){
        return mediaPlayer.getDuration();
    }

    public boolean isPng(){
        return mediaPlayer.isPlaying();
    }

    public void pausePlayer(){
        mediaPlayer.pause();
    }

    public void seek(int posn){
        mediaPlayer.seekTo(posn);
    }

    public void go(){
        mediaPlayer.start();
    }
    public void playPrev(){
        songPosn--;
        if(songPosn <0) songPosn=songs.size()-1;
        playSong();
    }
    public void playNext(){
        if(shuffle){
            int newSong= songPosn;
            while (newSong==songPosn){
                newSong=rand.nextInt(songs.size());
            }
            songPosn=newSong;
        }else{
        songPosn++;
            if(songPosn>= songs.size())
                songPosn=0;
        }
        playSong();
    }

    public void setShuffle() {
        if(shuffle) shuffle=false;
        else shuffle=true;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }
}
