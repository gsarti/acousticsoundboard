package com.cegepsth.asb.acousticsoundboard;

import android.app.IntentService;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;

public class AudioService extends IntentService {

    private static SparseArray<MediaPlayer> mediaPlayers = new SparseArray<>();
    MediaPlayer player;
    private final IBinder binder = new LocalBinder();
    private ServiceCallbacks serviceCallbacks;

    public AudioService() {
        super("AudioService");
    }

    public interface ServiceCallbacks {
        void soundFinished();
    }

    public class LocalBinder extends Binder {
        AudioService getService() {
            return AudioService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void setCallbacks(ServiceCallbacks callbacks) {
        serviceCallbacks = callbacks;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        if (action == AudioTask.ACTION_SET_VOLUME){
            float volume = intent.getFloatExtra("volume", 0);
            int id = intent.getIntExtra("soundId", 0);
            player = getPlayer(id);
            AudioTask.setVolume(player, volume);
        }
        else {
            int soundId = intent.getIntExtra("soundId", 0);
            if (action == AudioTask.ACTION_PLAY_FAVORITE_SOUND) {
                Uri uri = SoundboardContract.SettingsEntry.SETTINGS_URI;
                Cursor mCursor = getBaseContext().getContentResolver().query(uri, null, null, null, null);
                if (mCursor != null)
                    mCursor.moveToFirst();
                soundId = mCursor.getInt(mCursor.getColumnIndex(SoundboardContract.SettingsEntry.FAVORITESOUND_KEY));
            }
            final int id = soundId;
            player = getPlayer(soundId);
            if (action == AudioTask.ACTION_PLAY_SOUND_REPEAT){
                player.setLooping(true);
            }
            else{
                if (action == AudioTask.ACTION_PLAY_SOUND){
                    player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            mediaPlayers.delete(id);
                            serviceCallbacks.soundFinished();
                        }
                    });
                }
                player.setLooping(false);
            }
            AudioTask.executeTask(player, action);
            if (action == AudioTask.ACTION_STOP_SOUND){
                mediaPlayers.delete(soundId);
            }
        }
    }

    // Helper to handle player state
    public MediaPlayer createPlayer(int id) {
        Uri uri = ContentUris.withAppendedId(SoundboardContract.SoundEntry.SOUND_URI, id);
        String[] projection = new String[]{SoundboardContract.SoundEntry._ID, SoundboardContract.SoundEntry.NAME_KEY, SoundboardContract.SoundEntry.PATH_KEY, SoundboardContract.SoundEntry.DURATION_KEY, SoundboardContract.SoundEntry.IMAGE_KEY};
        Cursor mCursor = getBaseContext().getContentResolver().query(uri, projection, null, null, null);
        if (mCursor != null)
            mCursor.moveToFirst();
        Sound mSound = new Sound();
        mSound.setId(mCursor.getInt(mCursor.getColumnIndex(SoundboardContract.SoundEntry._ID)));
        mSound.setName(mCursor.getString(mCursor.getColumnIndex(SoundboardContract.SoundEntry.NAME_KEY)));
        mSound.setPath(mCursor.getString(mCursor.getColumnIndex(SoundboardContract.SoundEntry.PATH_KEY)));
        mSound.setDuration(mCursor.getInt(mCursor.getColumnIndex(SoundboardContract.SoundEntry.DURATION_KEY)));
        mSound.setImage(mCursor.getBlob(mCursor.getColumnIndex(SoundboardContract.SoundEntry.IMAGE_KEY)));
        mCursor.close();
        player = MediaPlayer.create(this, Uri.parse(mSound.getPath()));
        mediaPlayers.put(id,player);
        return player;
    }

    private MediaPlayer getPlayer(int id) {
        MediaPlayer mp = mediaPlayers.get(id);
        if (mp == null){
            return createPlayer(id);
        }
        else return mp;
    }
}