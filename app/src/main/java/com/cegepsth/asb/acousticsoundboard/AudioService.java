package com.cegepsth.asb.acousticsoundboard;

import android.app.IntentService;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;

public class AudioService extends IntentService{

    MediaPlayer player;

    public AudioService() {
        super("AudioService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        String songName = intent.getStringExtra("song");
        if (player == null){
            createPlayer(songName, false);
        }
        AudioTask.executeTask(player, action);
    }

    // Helper to handle player state
    public void createPlayer(String resource, boolean isURL) {
        if(isURL) {
            player = MediaPlayer.create(getApplicationContext(), Uri.parse(resource));
        } else {
            switch (resource) {
                case "Wrong":
                    player = MediaPlayer.create(getApplicationContext(), R.raw.wrong);
                    break;
                case "You spin me":
                    player = MediaPlayer.create(getApplicationContext(), R.raw.youspin);
                    break;
                case "Jai ldoua":
                    player = MediaPlayer.create(getApplicationContext(), R.raw.jaildoua);
                    break;
                case "Cegep STH":
                    player = MediaPlayer.create(getApplicationContext(), R.raw.cegepsth);
                    break;
                case "Fin de Session":
                    player = MediaPlayer.create(getApplicationContext(), R.raw.findesession);
                    break;
                default:
                    player = MediaPlayer.create(getApplicationContext(), R.raw.wrong);
            }
        }
    }
}