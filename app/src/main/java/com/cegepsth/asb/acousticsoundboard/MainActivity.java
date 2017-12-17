package com.cegepsth.asb.acousticsoundboard;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.cegepsth.asb.acousticsoundboard.SoundboardContract.SoundEntry.SOUND_URI;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton btnAdd;
    int CAPTURE_AUDIO = 0;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.soundboard, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnAdd = (FloatingActionButton) findViewById(R.id.btn_Add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, CAPTURE_AUDIO);
                } else {
                    Toast.makeText(MainActivity.this, "Please install the app RecForger Lite to record", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Setup();

        MainFragment mainFragment = new MainFragment();
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.fl_main, mainFragment).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_play:
                intent = new Intent(this, DetailsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void Setup() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean("firstTime", false)) {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                try {
                    createFile();
                } catch (IOException e) {
                    Log.e("IOEXCEPTION_FILE", "Error creating resource files");
                }

            } else {
                Log.e("STORAGE_ERROR", "No storage found");
            }
            ContentValues values = new ContentValues();
            values.put(SoundboardContract.SettingsEntry.FAVORITESOUND_KEY, 1);
            getContentResolver().insert(SoundboardContract.SettingsEntry.SETTINGS_URI, values);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.apply();
        }
    }

    private void createFile()
            throws IOException {

        int[] inputRawResources = new int[]{R.raw.wrong, R.raw.youspin, R.raw.jaildoua, R.raw.cegepsth, R.raw.findesession};
        String[] soundNames = {"Wrong", "You spin me", "Jai l'doua", "Cegep STH", "Fin de Session"};

        Resources resources = getApplicationContext().getResources();
        byte[] largeBuffer = new byte[1024 * 4];
        int bytesRead;
        for (int i = 0; i < inputRawResources.length; i++) {
            int resource = inputRawResources[i];
            String fileName = resource + ".mp3";
            File file = new File(getExternalFilesDir(null), fileName);
            OutputStream outputStream = new FileOutputStream(file);
            InputStream inputStream = resources.openRawResource(resource);
            while ((bytesRead = inputStream.read(largeBuffer)) > 0) {
                if (largeBuffer.length == bytesRead) {
                    outputStream.write(largeBuffer);
                } else {
                    final byte[] shortBuffer = new byte[bytesRead];
                    System.arraycopy(largeBuffer, 0, shortBuffer, 0, bytesRead);
                    outputStream.write(shortBuffer);
                }
            }
            inputStream.close();
            outputStream.flush();
            outputStream.close();
            ContentValues values = new ContentValues();
            values.put(SoundboardContract.SoundEntry.NAME_KEY, soundNames[i]);
            values.put(SoundboardContract.SoundEntry.PATH_KEY, file.getPath());
            values.put(SoundboardContract.SoundEntry.DURATION_KEY, (int) getDuration(file));
            values.put(SoundboardContract.SoundEntry.IMAGE_KEY, new byte[]{});
            getContentResolver().insert(SoundboardContract.SoundEntry.SOUND_URI, values);
        }
    }

    public long getDuration(File file) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(getApplicationContext(), Uri.fromFile(file));
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        retriever.release();
        return Long.parseLong(time);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == CAPTURE_AUDIO) {
            final Uri uri = data.getData();
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Set sound name");
            alert.setMessage("Please set the sound name:");

            // Set an EditText view to get user input
            final EditText input = new EditText(this);
            alert.setView(input);

            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String name = input.getText().toString();
                    //try {
                        String path = uri.getPath();
                        Uri uri = SOUND_URI;
                        ContentValues values = new ContentValues();
                        values.put(SoundboardContract.SoundEntry.NAME_KEY, name);
                        values.put(SoundboardContract.SoundEntry.PATH_KEY, path);
                        values.put(SoundboardContract.SoundEntry.DURATION_KEY, 1000);
                        values.put(SoundboardContract.SoundEntry.IMAGE_KEY, new byte[]{});
                        getContentResolver().insert(uri, values);
                    //} catch (IOException e){
                        Log.e("IOERROR", "Error writing file");
                   // }
                    dialog.dismiss();
                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.dismiss();
                }
            });

            alert.show();

        }
    }

    public File saveFile(Uri sourceuri, String fileName) throws IOException {
        File dest = new File(getExternalFilesDir(null), fileName + ".mp3");
        InputStream inputStream = getContentResolver().openInputStream(sourceuri);
        OutputStream outputStream = new FileOutputStream(dest);
        byte[] buffer = new byte[1024];
        int length = 0;
        while((length=inputStream.read(buffer)) > 0) {
            outputStream.write(buffer,0,length);
        }
        inputStream.close();
        outputStream.flush();
        outputStream.close();
        return dest;
    }

}