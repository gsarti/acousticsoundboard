package com.cegepsth.asb.acousticsoundboard;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

public class PlayerActivity extends AppCompatActivity {
    private String songName;
    private boolean songPlaying = false;
    private boolean repeatSong = false;
    private ImageButton btnPlayingState;
    private SeekBar volumeSlider;
    final private int DEFAULT_VOLUME = 50;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.soundplayer, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    private void createIntent(String action){
        Intent intent = new Intent(this, AudioService.class);
        intent.setAction(action);
        intent.putExtra("song", songName);
        startService(intent);
    }

    public void playSong() {
        createIntent(AudioTask.ACTION_PLAY_SOUND);
        songPlaying = true;
        btnPlayingState.setImageResource(android.R.drawable.ic_media_pause);
    }

    public void stopSong() {
        createIntent(AudioTask.ACTION_STOP_SOUND);
    }

    public void pauseSong() {
        createIntent(AudioTask.ACTION_PAUSE_SOUND);
        btnPlayingState.setImageResource(android.R.drawable.ic_media_play);
    }

    public void resumeSong() {
        createIntent(AudioTask.ACTION_RESUME_SOUND);
        btnPlayingState.setImageResource(android.R.drawable.ic_media_pause);
    }

    public float ProgressToVolume(int progress) {
     return (float) (1 - (Math.log(100 - progress) / Math.log(100)));

    }
}
