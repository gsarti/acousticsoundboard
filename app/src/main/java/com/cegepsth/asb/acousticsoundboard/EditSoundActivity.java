package com.cegepsth.asb.acousticsoundboard;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.cegepsth.asb.acousticsoundboard.databinding.ActivityEditBinding;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.cegepsth.asb.acousticsoundboard.SoundboardContract.SoundEntry.SOUND_URI;

/**
 * Created by Gabriele Sarti on 16-12-17.
 */

public class EditSoundActivity extends AppCompatActivity {

    private Sound mSound;
    private EditText mName;
    private ImageView mImage;
    private Button mSave;
    private Button mChangeImage;
    private static final int REQUEST_IMAGE_GET = 6969;
    private ActivityEditBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit);
        int id = getIntent().getIntExtra("soundId", 0);
        Uri uri = ContentUris.withAppendedId(SOUND_URI, id);
        String[] projection = new String[] {SoundboardContract.SoundEntry._ID, SoundboardContract.SoundEntry.NAME_KEY, SoundboardContract.SoundEntry.PATH_KEY, SoundboardContract.SoundEntry.DURATION_KEY, SoundboardContract.SoundEntry.IMAGE_KEY};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
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
        mName = (EditText)findViewById(R.id.et_name);
        mImage = (ImageView)findViewById(R.id.img_sound);
        mSave = (Button)findViewById(R.id.btn_save);
        mChangeImage = (Button)findViewById(R.id.btn_change_image);
        final byte[] imageByte = mSound.getImage();
        if (imageByte.length != 0){
            mImage.setImageBitmap(BitmapFactory.decodeByteArray(imageByte,0, imageByte.length));
        }
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BitmapDrawable drawable = (BitmapDrawable)mImage.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                byte[] imageByteArray = outputStream.toByteArray();
                mSound.setImage(imageByteArray);
                mSound.setName(mName.getText().toString());
                Uri uri = ContentUris.withAppendedId(SOUND_URI, mSound.getId());
                ContentValues contentValues = new ContentValues();
                contentValues.put(SoundboardContract.SoundEntry._ID, mSound.getId());
                contentValues.put(SoundboardContract.SoundEntry.NAME_KEY, mSound.getName());
                contentValues.put(SoundboardContract.SoundEntry.PATH_KEY, mSound.getPath());
                contentValues.put(SoundboardContract.SoundEntry.DURATION_KEY, mSound.getDuration());
                contentValues.put(SoundboardContract.SoundEntry.IMAGE_KEY, mSound.getImage());
                getContentResolver().update(uri, contentValues, null, null);
                Toast.makeText(EditSoundActivity.this, "Saved", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(EditSoundActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        mChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_IMAGE_GET);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK) {
            Uri uri = data.getData();

            try {
                Bitmap image = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                mImage.setImageBitmap(image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        BitmapDrawable drawable = (BitmapDrawable)mImage.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        outState.putByteArray("image", outputStream.toByteArray());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        byte[] image = savedInstanceState.getByteArray("image");
        ByteArrayInputStream inputStream = new ByteArrayInputStream(image);
        mImage.setImageBitmap(BitmapFactory.decodeStream(inputStream));
    }
}
