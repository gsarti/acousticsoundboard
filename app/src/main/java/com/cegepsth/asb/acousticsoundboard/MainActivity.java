package com.cegepsth.asb.acousticsoundboard;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.cegepsth.asb.acousticsoundboard.SoundboardContract.SoundEntry.SOUND_URI;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GeoFencer mGeofencer;
    private GoogleApiClient mClient;
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 111;
    FloatingActionButton btnAdd;
    MainFragment mMainFragment;
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
        Setup();
        btnAdd = (FloatingActionButton) findViewById(R.id.btn_Add);
        if (btnAdd != null) {
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, CAPTURE_AUDIO);
                    } else {
                        Toast.makeText(MainActivity.this, "Please install the app RecForger Lite to record", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        mMainFragment = new MainFragment();
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.fl_main, mMainFragment).commit();
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_FINE_LOCATION);


        mClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, this)
                .build();
        mClient.connect();
        mGeofencer = new GeoFencer(this, mClient);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_geofence:
                onAddPlaceButtonClicked(item.getActionView());
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
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Place place = PlacePicker.getPlace(this, data);
            if (place == null) {

                return;
            }

            String placeName = place.getName().toString();
            String placeAddress = place.getAddress().toString();
            String placeID = place.getId();


            String[] guids = new String[1];
            guids[0] = place.getId();
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mClient, guids);
            placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                @Override
                public void onResult(@NonNull PlaceBuffer places) {
                    Log.e("AAAAAAAAAAA", "Request");

                    boolean co = mClient.isConnected();

                    Toast.makeText(MainActivity.this, "Place Result", Toast.LENGTH_SHORT).show();
                    mGeofencer.updateGeofenceList(places);
                    mGeofencer.registerAllGeofences();
                }
            });
        } else if (resultCode == RESULT_OK && requestCode == CAPTURE_AUDIO) {
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
                    try {
                        File file = saveFile(uri, name);
                        Uri uri = SOUND_URI;
                        ContentValues values = new ContentValues();
                        values.put(SoundboardContract.SoundEntry.NAME_KEY, name);
                        values.put(SoundboardContract.SoundEntry.PATH_KEY, file.getPath());
                        values.put(SoundboardContract.SoundEntry.DURATION_KEY, (int) getDuration(file));
                        values.put(SoundboardContract.SoundEntry.IMAGE_KEY, new byte[]{});
                        getContentResolver().insert(uri, values);
                        mMainFragment.LoadUI();
                    } catch (IOException e) {
                        Log.e("IOERROR", "Error writing file");
                    }
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

    public void LoadUI() {
        Uri uri = SOUND_URI;
        Sound sound;
        List<Sound> soundList = new ArrayList<>();
        Cursor cursor = getContentResolver().query(uri, null, null, null, SoundboardContract.SoundEntry.NAME_KEY);
        if (cursor.moveToFirst()) {
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
    }

    public File saveFile(Uri sourceuri, String fileName) throws IOException {
        File dest = new File(getExternalFilesDir(null), fileName + ".mp3");
        InputStream inputStream = new FileInputStream(new File(getRealPathFromURI(getApplicationContext(), sourceuri)));
        OutputStream outputStream = new FileOutputStream(dest);
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        inputStream.close();
        outputStream.flush();
        outputStream.close();
        return dest;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e("AAAAAAAAAAA", "Connected");

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void onAddPlaceButtonClicked(View view) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Need permission", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            // Start a new Activity for the Place Picker API, this will trigger {@code #onActivityResult}
            // when a place is selected or with the user cancels.
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            Intent i = builder.build(this);
            startActivityForResult(i, 1);
        } catch (Exception e) {

        }
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Audio.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}