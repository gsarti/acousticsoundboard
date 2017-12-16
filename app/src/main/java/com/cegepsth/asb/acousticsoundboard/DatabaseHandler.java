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

    private String[] seed = {"Wrong", "You spin me", "Jai ldoua", "Cegep STH", "Fin de Session"};

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
        Seed(db);
    }

    public void Seed(SQLiteDatabase db){
        // Settings
        String setting = "INSERT INTO " + TABLE_SETTINGS + " VALUES (" + 0 + ", " + 1 + ")";
        db.execSQL(setting);
        // Default Sounds
        for (int i = 1; i <= seed.length; i++){
            String query = "INSERT INTO " + TABLE_SOUNDS + " (id,name,path,duration,image)\n" +
                           "VALUES (" + i + ", '" + seed[i-1] + "', '" + seed[i-1] + "', 0, null );";
            db.execSQL(query);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) { }
}
