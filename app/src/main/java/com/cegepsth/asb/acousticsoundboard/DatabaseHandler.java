package com.cegepsth.asb.acousticsoundboard;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.provider.BaseColumns._ID;
import static com.cegepsth.asb.acousticsoundboard.SoundboardContract.SettingsEntry.FAVORITESOUND_KEY;
import static com.cegepsth.asb.acousticsoundboard.SoundboardContract.SettingsEntry.TABLE_SETTINGS;
import static com.cegepsth.asb.acousticsoundboard.SoundboardContract.SoundEntry.DURATION_KEY;
import static com.cegepsth.asb.acousticsoundboard.SoundboardContract.SoundEntry.IMAGE_KEY;
import static com.cegepsth.asb.acousticsoundboard.SoundboardContract.SoundEntry.NAME_KEY;
import static com.cegepsth.asb.acousticsoundboard.SoundboardContract.SoundEntry.PATH_KEY;
import static com.cegepsth.asb.acousticsoundboard.SoundboardContract.SoundEntry.TABLE_SOUNDS;

public class DatabaseHandler extends SQLiteOpenHelper {
    public static final int DATABASE_REVISION = 1;
    public static final String DATABASE_NAME = "acousticdb";

    public DatabaseHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_REVISION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SOUNDS_TABLE = "CREATE TABLE " + TABLE_SOUNDS + "("
                + _ID + " INTEGER PRIMARY KEY,"
                + NAME_KEY + " TEXT,"
                + PATH_KEY + " TEXT,"
                + DURATION_KEY + " INTEGER,"
                + IMAGE_KEY + " BLOB" + ")";
        String CREATE_SETTINGS_TABLE = "CREATE TABLE " + TABLE_SETTINGS + "("
                + _ID + " INTEGER PRIMARY KEY,"
                + FAVORITESOUND_KEY + " INTEGER)";
        db.execSQL(CREATE_SOUNDS_TABLE);
        db.execSQL(CREATE_SETTINGS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) { }
}
