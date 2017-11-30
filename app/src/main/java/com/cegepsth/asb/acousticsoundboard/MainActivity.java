package com.cegepsth.asb.acousticsoundboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AsyncDatabaseResponse {
    private String[] songName = {"Wrong", "You spin me", "Hello World", "Waddup", "Recycler View", "Jai ldoua"};
    private List<Sound> soundList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

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
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        new DatabaseConnector(this).getAllSounds();
        if (soundList.size() == 0){
            Sound s1 = new Sound("Wrong", "Wrong");
            Sound s2 = new Sound("Jai ldoua", "Jai ldoua");
            new DatabaseConnector(this).addSound(s1);
            new DatabaseConnector(this).addSound(s2);
            new DatabaseConnector(this).getAllSounds();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_play:
                intent = new Intent(this, PlayerActivity.class);
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

    @Override
    public void processFinish(Object o) {
        if (o instanceof List<?>) {
            soundList = (List<Sound>) o;
            mAdapter = new SoundboardAdapter(soundList);
            mRecyclerView.setAdapter(mAdapter);
        }
    }
}