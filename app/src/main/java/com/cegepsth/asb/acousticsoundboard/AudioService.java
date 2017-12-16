package com.cegepsth.asb.acousticsoundboard;

import android.app.IntentService;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

public class AudioService extends IntentService {

    MediaPlayer player;

    public AudioService() {
        super("AudioService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        int songId = intent.getIntExtra("songId", 0);
        if (action == AudioTask.ACTION_PLAY_FAVORITE_SOUND){
            Uri uri = SoundboardContract.SettingsEntry.SETTINGS_URI;
            Cursor mCursor = getBaseContext().getContentResolver().query(uri, null, null, null, null);
            if (mCursor != null)
                mCursor.moveToFirst();
            songId = mCursor.getInt(mCursor.getColumnIndex(SoundboardContract.SettingsEntry.FAVORITESOUND_KEY));
        }
        if (player == null) {
            createPlayer(songId);
        }
        AudioTask.executeTask(player, action);
    }

    // Helper to handle player state
    public void createPlayer(int id) {
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
    }
}