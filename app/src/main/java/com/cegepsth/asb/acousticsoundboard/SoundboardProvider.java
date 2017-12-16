package com.cegepsth.asb.acousticsoundboard;

/**
 * Created by NSA on 12/15/2017.
 */

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.cegepsth.asb.acousticsoundboard.SoundboardContract.SettingsEntry.SETTINGS_URI;
import static com.cegepsth.asb.acousticsoundboard.SoundboardContract.SoundEntry.SOUND_URI;

public class SoundboardProvider extends ContentProvider {

    public static final int SOUNDS = 100;
    public static final int SOUND_WITH_ID = 101;
    public static final int SETTINGS = 200;
    public static final int SETTINGS_WITH_ID = 201;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final String TAG = SoundboardProvider.class.getName();

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(SoundboardContract.AUTHORITY, SoundboardContract.PATH_SOUNDS, SOUNDS);
        uriMatcher.addURI(SoundboardContract.AUTHORITY, SoundboardContract.PATH_SOUNDS + "/#", SOUND_WITH_ID);
        uriMatcher.addURI(SoundboardContract.AUTHORITY, SoundboardContract.PATH_SETTINGS, SETTINGS);
        return uriMatcher;
    }

    private DatabaseHandler dbHandler;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        dbHandler = new DatabaseHandler(context);
        return true;
    }

    public Uri insertSound(Sound sound){
        Uri uri = SOUND_URI;
        ContentValues values = new ContentValues();
        values.put(SoundboardContract.SoundEntry.NAME_KEY, sound.getName());
        values.put(SoundboardContract.SoundEntry.PATH_KEY, sound.getPath());
        values.put(SoundboardContract.SoundEntry.DURATION_KEY, sound.getDuration());
        values.put(SoundboardContract.SoundEntry.IMAGE_KEY, sound.getImage());
        return insert(uri,values);
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = dbHandler.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case SOUNDS:
                long id = db.insert(SoundboardContract.SoundEntry.TABLE_SOUNDS, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(SoundboardContract.SoundEntry.SOUND_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    public List<Sound> getAllSounds(){
        Uri uri = SOUND_URI;
        Sound sound;
        List<Sound> soundList = new ArrayList<>();
        Cursor cursor = query(uri, null, null, null, SoundboardContract.SoundEntry.NAME_KEY);
        if (cursor.moveToFirst()){
            do {
                sound = new Sound();
                sound.setId(cursor.getInt(cursor.getColumnIndex(SoundboardContract.SoundEntry._ID)));
                sound.setName(cursor.getString(cursor.getColumnIndex(SoundboardContract.SoundEntry.NAME_KEY)));
                sound.setPath(cursor.getString(cursor.getColumnIndex(SoundboardContract.SoundEntry.PATH_KEY)));
                sound.setDuration(cursor.getInt(cursor.getColumnIndex(SoundboardContract.SoundEntry.DURATION_KEY)));
                sound.setImage(cursor.getBlob(cursor.getColumnIndex(SoundboardContract.SoundEntry.IMAGE_KEY)));
                soundList.add(sound);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return soundList;
    }

    public Settings getSettings(){
        Uri uri = SETTINGS_URI;
        Cursor cursor = query(uri, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        Settings settings = new Settings();
        settings.setId(cursor.getInt(cursor.getColumnIndex(SoundboardContract.SettingsEntry._ID)));
        settings.setFavoriteSong(cursor.getInt(cursor.getColumnIndex(SoundboardContract.SettingsEntry.FAVORITESOUND_KEY)));
        cursor.close();
        return settings;
    }

    public Sound getSound(int id){
        Uri uri = ContentUris.withAppendedId(SOUND_URI, id);
        String[] projection = new String[] {SoundboardContract.SoundEntry._ID, SoundboardContract.SoundEntry.NAME_KEY, SoundboardContract.SoundEntry.PATH_KEY, SoundboardContract.SoundEntry.DURATION_KEY, SoundboardContract.SoundEntry.IMAGE_KEY};
        Cursor cursor = query(uri, projection, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        Sound searchSound = new Sound();
        searchSound.setId(cursor.getInt(cursor.getColumnIndex(SoundboardContract.SoundEntry._ID)));
        searchSound.setName(cursor.getString(cursor.getColumnIndex(SoundboardContract.SoundEntry.NAME_KEY)));
        searchSound.setPath(cursor.getString(cursor.getColumnIndex(SoundboardContract.SoundEntry.PATH_KEY)));
        searchSound.setDuration(cursor.getInt(cursor.getColumnIndex(SoundboardContract.SoundEntry.DURATION_KEY)));
        searchSound.setImage(cursor.getBlob(cursor.getColumnIndex(SoundboardContract.SoundEntry.IMAGE_KEY)));
        cursor.close();
        return searchSound;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        final SQLiteDatabase db = dbHandler.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor retCursor;
        switch (match) {
            case SOUNDS:
                retCursor = db.query(SoundboardContract.SoundEntry.TABLE_SOUNDS,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case SOUND_WITH_ID:
                String id = uri.getPathSegments().get(1);
                retCursor = db.query(SoundboardContract.SoundEntry.TABLE_SOUNDS,
                        projection,
                        "_id=?",
                        new String[]{id},
                        null,
                        null,
                        sortOrder);
                break;
            case SETTINGS:
                retCursor = db.query(SoundboardContract.SettingsEntry.TABLE_SETTINGS,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    public int deleteSound(int id){
        Uri uri = ContentUris.withAppendedId(SETTINGS_URI, id);
        return delete(uri, null, null);
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHandler.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int soundsDeleted;
        switch (match) {
            case SOUND_WITH_ID:
                String id = uri.getPathSegments().get(1);
                soundsDeleted = db.delete(SoundboardContract.SoundEntry.TABLE_SOUNDS, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (soundsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return soundsDeleted;
    }

    public int updateSettings(int id, Settings settings){
        Uri uri = ContentUris.withAppendedId(SETTINGS_URI,id);
        ContentValues content = new ContentValues();
        content.put(SoundboardContract.SettingsEntry.FAVORITESOUND_KEY, settings.getFavoriteSong());
        return update(uri, content, null, null);
    }

    public int updateSound(int id, Sound sound){
        Uri uri = ContentUris.withAppendedId(SOUND_URI, id);
        ContentValues contentValues = new ContentValues();
        contentValues.put(SoundboardContract.SoundEntry._ID, sound.getId());
        contentValues.put(SoundboardContract.SoundEntry.NAME_KEY, sound.getName());
        contentValues.put(SoundboardContract.SoundEntry.PATH_KEY, sound.getPath());
        contentValues.put(SoundboardContract.SoundEntry.DURATION_KEY, sound.getDuration());
        contentValues.put(SoundboardContract.SoundEntry.IMAGE_KEY, sound.getImage());
        return update(uri, contentValues, null,null);
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        final SQLiteDatabase db = dbHandler.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int objectUpdated;

        switch (match) {
            case SOUND_WITH_ID:
                String id = uri.getPathSegments().get(1);
                objectUpdated = db.update(SoundboardContract.SoundEntry.TABLE_SOUNDS, values, "_id=?", new String[]{id});
                break;
            case SETTINGS_WITH_ID:
                String idS  = uri.getPathSegments().get(1);
                objectUpdated = db.update(SoundboardContract.SettingsEntry.TABLE_SETTINGS, values, "_id=?", new String[]{idS});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (objectUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return objectUpdated;
    }


    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Pas le temps");
    }
}