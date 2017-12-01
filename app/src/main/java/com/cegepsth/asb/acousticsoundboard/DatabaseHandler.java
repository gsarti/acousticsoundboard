package com.cegepsth.asb.acousticsoundboard;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {
    public static final int DATABASE_REVISION = 1;
    public static final String DATABASE_NAME = "acousticdb";

    public static final String TABLE_SOUNDS = "Sounds";
    public static final String ID_KEY = "id";
    public static final String NAME_KEY = "name";
    public static final String PATH_KEY = "path";
    public static final String DURATION_KEY = "duration";
    public static final String IMAGE_KEY = "image";

    public static final String TABLE_SETTINGS = "Settings";
    public static final String WIDGETSOUND_KEY = "widgetSound";
    private String[] seed = {"Wrong", "You spin me", "Jai ldoua", "Cegep STH", "Fin de Session"};


    public DatabaseHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_REVISION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SOUNDS_TABLE = "CREATE TABLE " + TABLE_SOUNDS + "("
                + ID_KEY + " INTEGER PRIMARY KEY,"
                + NAME_KEY + " TEXT,"
                + PATH_KEY + " TEXT,"
                + DURATION_KEY + " INTEGER,"
                + IMAGE_KEY + " BLOB" + ")";
        String CREATE_SETTINGS_TABLE = "CREATE TABLE " + TABLE_SETTINGS + "("
                + ID_KEY + " INTEGER PRIMARY KEY,"
                + WIDGETSOUND_KEY + " INTEGER)";
        db.execSQL(CREATE_SOUNDS_TABLE);
        db.execSQL(CREATE_SETTINGS_TABLE);
        Seed(db);
    }

    public void Seed(SQLiteDatabase db){
        for (int i = 1; i <= seed.length; i++){
            String query = "INSERT INTO " + TABLE_SOUNDS + " (id,name,path,duration,image)\n" +
                           "VALUES (" + i + ", '" + seed[i-1] + "', '" + seed[i-1] + "', 0, null );";
            db.execSQL(query);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) { }
}
