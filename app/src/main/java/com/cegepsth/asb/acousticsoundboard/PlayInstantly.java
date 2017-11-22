package com.cegepsth.asb.acousticsoundboard;

import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class PlayInstantly extends AppCompatActivity {
    SoundPool soundPool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 100);

        float volume = 1;
        soundPool.play(R.raw.youspin, volume, volume, 1, 0, 1f);
        Toast.makeText(this, "PLAYING OP SONG", Toast.LENGTH_SHORT).show();
        finish();

    }
}
