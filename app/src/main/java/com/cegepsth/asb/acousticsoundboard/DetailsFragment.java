package com.cegepsth.asb.acousticsoundboard;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.cegepsth.asb.acousticsoundboard.databinding.FragmentDetailsBinding;

import static com.cegepsth.asb.acousticsoundboard.SoundboardContract.SoundEntry.SOUND_URI;

/**
 * Created by Gabriele Sarti on 17-12-17.
 */

public class DetailsFragment extends Fragment implements AudioService.ServiceCallbacks {

    private AudioService myService;
    private boolean bound = false;
    private ImageView mImage;
    private SeekBar mVolumeSlider;
    private ImageButton btnPlayingState;
    private ImageButton btnMute;
    private ImageButton btnRepeat;

    private FragmentDetailsBinding binding;
    private Sound mSound;
    private boolean mSoundPlaying = false;
    private boolean mPaused = false;
    private boolean mRepeatSong = false;
    private float mVolume;
    final private float DEFAULT_VOLUME = 50.0f;

    public DetailsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_details, container, false);
        View v = binding.getRoot();
        mImage = v.findViewById(R.id.img_details);
        mVolumeSlider = v.findViewById(R.id.vs_slider);
        btnPlayingState = v.findViewById(R.id.btnPlayingState);
        btnMute = v.findViewById(R.id.btnMute);
        btnRepeat = v.findViewById(R.id.btnRepeat);
        mVolume = DEFAULT_VOLUME;

        mVolumeSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mVolume = ProgressToVolume(i);
                Intent intent = new Intent(getActivity(), AudioService.class);
                intent.setAction(AudioTask.ACTION_SET_VOLUME);
                intent.putExtra("volume", mVolume);
                intent.putExtra("soundId", mSound.getId());
                getActivity().startService(intent);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        btnPlayingState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSoundPlaying) {
                    pauseSound();
                } else {
                    if (mPaused) {
                        resumeSound();
                    } else {
                        playSound();
                    }
                }
            }
        });

        btnMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopSound();
            }
        });

        btnRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRepeatSong = !mRepeatSong;
            }
        });

        int id = getArguments().getInt(SoundboardContract.SoundEntry._ID);
        Uri uri = ContentUris.withAppendedId(SOUND_URI, id);
        String[] projection = new String[]{SoundboardContract.SoundEntry._ID, SoundboardContract.SoundEntry.NAME_KEY, SoundboardContract.SoundEntry.PATH_KEY, SoundboardContract.SoundEntry.DURATION_KEY, SoundboardContract.SoundEntry.IMAGE_KEY};
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        mSound = new Sound();
        mSound.setId(id);
        mSound.setName(cursor.getString(cursor.getColumnIndex(SoundboardContract.SoundEntry.NAME_KEY)));
        mSound.setPath(cursor.getString(cursor.getColumnIndex(SoundboardContract.SoundEntry.PATH_KEY)));
        mSound.setDuration(cursor.getInt(cursor.getColumnIndex(SoundboardContract.SoundEntry.DURATION_KEY)));
        mSound.setImage(cursor.getBlob(cursor.getColumnIndex(SoundboardContract.SoundEntry.IMAGE_KEY)));
        cursor.close();
        binding.setSound(mSound);

        final byte[] imageByte = mSound.getImage();
        if (imageByte.length != 0) {
            mImage.setImageBitmap(BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length));
        }
        return v;
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // cast the IBinder and get MyService instance
            AudioService.LocalBinder binder = (AudioService.LocalBinder) service;
            myService = binder.getService();
            bound = true;
            myService.setCallbacks(DetailsFragment.this); // register
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }
    };

    private void createIntent(String action) {
        Intent intent = new Intent(getActivity(), AudioService.class);
        intent.setAction(action);
        intent.putExtra("soundId", mSound.getId());
        getContext().bindService(intent,serviceConnection, Context.BIND_AUTO_CREATE);
        getActivity().startService(intent);
    }

    public void playSound() {
        if (!mRepeatSong) {
            createIntent(AudioTask.ACTION_PLAY_SOUND);
        } else {
            createIntent(AudioTask.ACTION_PLAY_SOUND_REPEAT);
        }
        mSoundPlaying = true;
        btnPlayingState.setImageResource(android.R.drawable.ic_media_pause);
    }

    public void stopSound() {
        if (mSoundPlaying) {
            createIntent(AudioTask.ACTION_STOP_SOUND);
            mSoundPlaying = false;
            mRepeatSong = false;
        }
    }

    public void pauseSound() {
        createIntent(AudioTask.ACTION_PAUSE_SOUND);
        btnPlayingState.setImageResource(android.R.drawable.ic_media_play);
        mSoundPlaying = false;
        mPaused = true;
        mRepeatSong = false;
    }

    public void resumeSound() {
        createIntent(AudioTask.ACTION_RESUME_SOUND);
        btnPlayingState.setImageResource(android.R.drawable.ic_media_pause);
        mSoundPlaying = true;
        mPaused = false;
    }

    public float ProgressToVolume(int progress) {
        return (float) (1 - (Math.log(100 - progress) / Math.log(100)));
    }

    @Override
    public void soundFinished() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnPlayingState.setImageResource(android.R.drawable.ic_media_play);
            }
        });
        mSoundPlaying = false;
        mPaused = false;
    }
}
