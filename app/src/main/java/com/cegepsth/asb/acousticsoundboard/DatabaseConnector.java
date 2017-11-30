package com.cegepsth.asb.acousticsoundboard;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

public class DatabaseConnector extends AsyncTask<DatabaseConnector.ActionToPerform, Integer, Object> {

    private SQLiteDatabase db;
    private DatabaseHandler dbHandler;
    private Context context;
    public AsyncDatabaseResponse delegate = null;

    public DatabaseConnector(Context context){
        delegate = (AsyncDatabaseResponse)context;
        dbHandler = new DatabaseHandler(context);
        this.context = context;
    }

    public void getSound(int id){
        execute(new ActionToPerform("getSound", id, null, null));
    }

    public void addSound(Sound sound){
        execute(new ActionToPerform("addSound", 0, sound, null));
    }

    public void deleteSound(int id){
        execute(new ActionToPerform("deleteSound", id, null, null));
    }

    public void editSound(int id, Sound sound){
        execute(new ActionToPerform("editSound", id, sound, null));
    }

    public void getAllSounds(){
        execute(new ActionToPerform("getAllSounds", 0, null, null));
    }

    public void getSettings(){
        execute(new ActionToPerform("getSettings", 0, null, null));
    }

    public void editSettings(Settings settings){
        execute(new ActionToPerform("editSettings", 0, null, settings));
    }

    void getSoundCount(){
        execute(new ActionToPerform("getCount", 0, null, null));
    }

    @Override
    protected Object doInBackground(ActionToPerform... actionToPerforms) {
        String query = actionToPerforms[0].getAction();
        Sound sound;
        Settings settings;
        int id;
        Cursor cursor;
        List<Sound> soundList = new ArrayList<>();
        switch (query) {
            case "getSound":
                db = dbHandler.getReadableDatabase();
                id = actionToPerforms[0].id;
                cursor = db.query(DatabaseHandler.TABLE_SOUNDS,
                        new String[] {DatabaseHandler.ID_KEY, DatabaseHandler.NAME_KEY, DatabaseHandler.PATH_KEY, DatabaseHandler.DURATION_KEY, DatabaseHandler.IMAGE_KEY},
                        DatabaseHandler.ID_KEY + " =?",
                        new String[] { String.valueOf(id)},
                        null,
                        null,
                        null,
                        null);
                if (cursor != null)
                    cursor.moveToFirst();
                Sound searchSound = new Sound();
                searchSound.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.ID_KEY)));
                searchSound.setName(cursor.getString(cursor.getColumnIndex(DatabaseHandler.NAME_KEY)));
                searchSound.setPath(cursor.getString(cursor.getColumnIndex(DatabaseHandler.PATH_KEY)));
                searchSound.setDuration(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.DURATION_KEY)));
                searchSound.setImage(cursor.getBlob(cursor.getColumnIndex(DatabaseHandler.IMAGE_KEY)));
                cursor.close();
                db.close();
                return searchSound;
            case "addSound":
                db = dbHandler.getWritableDatabase();
                sound = actionToPerforms[0].sound;
                ContentValues values = new ContentValues();
                values.put(DatabaseHandler.NAME_KEY, sound.getName());
                values.put(DatabaseHandler.PATH_KEY, sound.getPath());
                values.put(DatabaseHandler.DURATION_KEY, sound.getDuration());
                values.put(DatabaseHandler.IMAGE_KEY, sound.getImage());
                db.insert(DatabaseHandler.TABLE_SOUNDS, null, values);
                db.close();
                return null;
            case "deleteSound":
                db = dbHandler.getWritableDatabase();
                id = actionToPerforms[0].id;
                db.delete(DatabaseHandler.TABLE_SOUNDS, DatabaseHandler.ID_KEY + " =?", new String[] { String.valueOf(id)});
                return null;
            case "editSound":
                db = dbHandler.getWritableDatabase();
                sound = actionToPerforms[0].sound;
                id = actionToPerforms[0].id;
                ContentValues contentValues = new ContentValues();
                contentValues.put(DatabaseHandler.ID_KEY, sound.getId());
                contentValues.put(DatabaseHandler.NAME_KEY, sound.getName());
                contentValues.put(DatabaseHandler.PATH_KEY, sound.getPath());
                contentValues.put(DatabaseHandler.DURATION_KEY, sound.getDuration());
                contentValues.put(DatabaseHandler.IMAGE_KEY, sound.getImage());
                db.update(DatabaseHandler.TABLE_SOUNDS, contentValues, DatabaseHandler.ID_KEY + " =" + id, null);
                return null;
            case "getSoundCount":
                db = dbHandler.getReadableDatabase();
                return DatabaseUtils.queryNumEntries(db,DatabaseHandler.TABLE_SOUNDS);
            case "getAllSounds":
                db = dbHandler.getReadableDatabase();
                cursor = db.query(DatabaseHandler.TABLE_SOUNDS,
                        null,
                        null,
                        null,
                        null,
                        null,
                        DatabaseHandler.NAME_KEY);
                if (cursor.moveToFirst()){
                    do {
                        sound = new Sound();
                        sound.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.ID_KEY)));
                        sound.setName(cursor.getString(cursor.getColumnIndex(DatabaseHandler.NAME_KEY)));
                        sound.setPath(cursor.getString(cursor.getColumnIndex(DatabaseHandler.PATH_KEY)));
                        sound.setDuration(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.DURATION_KEY)));
                        sound.setImage(cursor.getBlob(cursor.getColumnIndex(DatabaseHandler.IMAGE_KEY)));
                        soundList.add(sound);
                    } while (cursor.moveToNext());
                }
                cursor.close();
                db.close();
                return soundList;
            case "getSettings":
                db = dbHandler.getReadableDatabase();
                cursor = db.query(DatabaseHandler.TABLE_SETTINGS,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
                if (cursor != null)
                    cursor.moveToFirst();
                settings = new Settings();
                settings.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.ID_KEY)));
                settings.setmWidgetSoundKeyt(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.WIDGETSOUND_KEY)));
                cursor.close();
                db.close();
                return settings;
            case "editSettings":
                db = dbHandler.getWritableDatabase();
                settings = actionToPerforms[0].settings;
                ContentValues content = new ContentValues();
                content.put(DatabaseHandler.ID_KEY, settings.getId());
                content.put(DatabaseHandler.WIDGETSOUND_KEY, settings.getmWidgetSoundKey());
                db.update(DatabaseHandler.TABLE_SETTINGS, content, DatabaseHandler.ID_KEY + " =" + settings.getId(), null);
            default:
                return null;
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    //Appeller la méthode processFinish dans le MainActivity pour afficher
    //le ou les contacts dans le UI.
    @Override
    protected void onPostExecute(Object o) {
        delegate.processFinish(o);
    }

    public class ActionToPerform{
        private String action;
        private int id;
        private Sound sound;
        private Settings settings;

        ActionToPerform(String action, int id, Sound sound, Settings settings){
            this.action = action;
            this.id = id;
            this.sound = sound;
            this.settings = settings;
        }

        public String getAction(){
            return action;
        }
    }

}
