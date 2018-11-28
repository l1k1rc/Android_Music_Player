package com.example.l1k1.musicprojetl3i;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.linkin.musicprojectl3.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * For the service :
 * -service object which will be a "pointer" on the service used {@link MusicService}
 * -ServiceObject object which will manage the connection/disconnection part of the service {onServiceConnected and onServiceDisconnected}
 * -BroadCastReceiver for the modifications done by the service
 * -bindService call in the onCreate method
 * -unBindService call in the onDestroy method
 */
public class MainActivity extends AppCompatActivity {

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    //for the data send by the intent
    public static final String TITLE="TITLE";
    public static final String ARTIST="ARTIST";

    //For the music informations recolted in the phone
    private ArrayList<Song> songList;

    //For the graphical part
    private ListView songView;
    //For the mediaPLayer which will launch the music
    private MusicService musicService; // the service object for our service created in MusicService class
    private Intent musicIntent;
    private boolean musicBound = false;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create the listView and the arrayList which will be in the adapter
        songView = (ListView) findViewById(R.id.song_list);
        songList = new ArrayList<Song>();

        // if the permission is obtained, the program can launch the activity
        if (checkPermissionREAD_EXTERNAL_STORAGE(this)) {
            Log.i("TAG", "HELLO THERE");
            getSongList(); // own method to parse the storage

            Collections.sort(songList, new Comparator<Song>() { // sort the song into the list
                @Override
                public int compare(Song o1, Song o2) {
                    return o1.getTitle().compareTo(o2.getTitle());
                }
            });
            SgAdapter songAdapter = new SgAdapter(MainActivity.this, songList);
            songView.setAdapter(songAdapter); // displaying
/*
            songView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                    startActivity(intent);
                }
            });*/

        }
    }

    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.BinderInstanceMusic binderInstanceMusic = (MusicService.BinderInstanceMusic) service;
            musicService = binderInstanceMusic.getService();
            musicBound = true;
            musicService.setList(songList);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (musicIntent == null) {
            musicIntent = new Intent(this, MusicService.class);
            bindService(musicIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(musicIntent);
        }
    }

    /*For The "onClick" part to interact with the listView when the user click on his music */
    /*Pass the informations with the intent towards the second activity to display a detailed part*/
    public void songPicked(View view) {
        musicService.setSong(Integer.parseInt(view.getTag().toString()));// view.getTag().toString() == position of the music
        musicService.playSong(); // method to launch a song selected
        Log.d("INFOOOOO", "Song picked and lauched");
        Intent intent = new Intent(MainActivity.this, SecondActivity.class);
        Log.d("TTTT",songList.get(Integer.parseInt(view.getTag().toString())).toString());
        Log.d("AAAAAA",songList.get(Integer.parseInt(view.getTag().toString())).getArtist());
        Log.d("TITLE",songList.get(Integer.parseInt(view.getTag().toString())).getTitle());
        intent.putExtra(ARTIST,songList.get(Integer.parseInt(view.getTag().toString())).getArtist());
        intent.putExtra(TITLE, songList.get(Integer.parseInt(view.getTag().toString())).getTitle());
        /**
         *
         * A FINIR
         * METTRE EN INTENT LES DONNEES DE LA MUSIC SELECTIONNEE
         *
         */
        startActivity(intent);
    }

    /* Allow to check the permission to have an access in the internal storage of the user's phone */
    /*Ask to the user the permission <-> in manifest PERMISSION EXTERNAL STORAGE */
    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context, Manifest.permission.READ_EXTERNAL_STORAGE); // appel à showDialog

                } else {
                    ActivityCompat.requestPermissions((Activity) context,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    /*When the check is ok*/
    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{permission},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    /* Allow to parse the internal storage to get the music format file and some information about it */
    public void getSongList() {
        ContentResolver musicResolver = getContentResolver();
        //path to follow to get the internal file
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        @SuppressLint("Recycle") Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST);
            /*ADD TO ANOTHER LIST FOR THE NEXT ACTIVITY */
            int albumColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int timeColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int imgColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.BOOKMARK);
            //Parse the internal storage
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                //Add the song to the listView
                //BE CAREFULL ::: songlist is taken by the adapter
                songList.add(new Song(thisId, thisTitle, thisArtist));
            }
            while (musicCursor.moveToNext());
        }

    }

    /* Menu on the right side in the app*/
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);

        return true;
    }

    /*Choices displaying by the menu */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.option:
                return true;
            case R.id.favoris:
                return true;
            case R.id.stats:
                SgAdapter songAdapter = new SgAdapter(this, songList);
                //setAdapter renvoie vers getView
                songView.setAdapter(songAdapter);
                return true;
            case R.id.quitter:
                finish();
                return true;
            case R.id.action_shuffle:
                break;
            case R.id.action_end:
                stopService(musicIntent);
                musicService = null;
                System.exit(0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /*When the app is killed*/
    @Override
    protected void onDestroy() {
        Toast.makeText(this, "End of service", Toast.LENGTH_SHORT).show();
        unbindService(musicConnection);
        stopService(musicIntent);
        musicService = null;
        super.onDestroy();
    }
}
