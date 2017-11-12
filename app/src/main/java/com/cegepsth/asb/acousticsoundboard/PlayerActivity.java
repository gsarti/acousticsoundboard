package com.cegepsth.asb.acousticsoundboard;

import android.media.Image;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class PlayerActivity extends AppCompatActivity {
    private MediaPlayer player;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // BUNDLE
        Bundle bundle = getIntent().getExtras();

        setContentView(R.layout.activity_player);
        // Init player
        createPlayer();
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                songPlaying = false;
                btnPlayingState.setImageResource(android.R.drawable.ic_media_play);
                if(repeatSong) {
                    playSong();
                }
            }
        });

        // Handle controls
        btnPlayingState = (ImageButton) findViewById(R.id.btnPlayingState);
        btnPlayingState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!songPlaying) {
                    playSong();
                } else if (player.isPlaying()) {
                    pauseSong();
                } else {
                    resumeSong();
                }
            }
        });
        ImageButton btnRepeat = (ImageButton) findViewById(R.id.btnRepeat);
        btnRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                repeatSong = !repeatSong;
            }
        });

        // Volume bar
        volumeSlider = (SeekBar) findViewById(R.id.volumeSlider);
        volumeSlider.setProgress(DEFAULT_VOLUME);
        float vol = ProgressToVolume(DEFAULT_VOLUME);
        player.setVolume(vol, vol);
        volumeSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                float volume = ProgressToVolume(i);
                player.setVolume(volume, volume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Mute Button
        ImageButton muteButton = (ImageButton) findViewById(R.id.btnMute);
        muteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                volumeSlider.setProgress(0);
                float vol = ProgressToVolume(0);
                player.setVolume(vol, vol);
            }
        });

        // Name
        TextView name = (TextView) findViewById(R.id.txtNom);
        name.setText(bundle.getString("Name"));
    }

    // Helper to handle player state
    public void createPlayer() {
        player = MediaPlayer.create(getApplicationContext(), R.raw.wrong);
    }

    public void playSong() {
        player.start();
        songPlaying = true;
        btnPlayingState.setImageResource(android.R.drawable.ic_media_pause);
    }

    public void stopSong() {
        player.stop();
    }

    public void pauseSong() {
        player.pause();
        btnPlayingState.setImageResource(android.R.drawable.ic_media_play);
    }

    public void resumeSong() {
        player.seekTo(player.getCurrentPosition());
        player.start();
        btnPlayingState.setImageResource(android.R.drawable.ic_media_pause);
    }

    public float ProgressToVolume(int progress) {
     return (float) (1 - (Math.log(100 - progress) / Math.log(100)));

    }
}
