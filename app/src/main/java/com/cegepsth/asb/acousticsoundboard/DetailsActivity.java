package com.cegepsth.asb.acousticsoundboard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;

public class DetailsActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.soundplayer, menu);
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        DetailsFragment detailsFragment = new DetailsFragment();
        FragmentManager manager = getSupportFragmentManager();
        Bundle bundle = new Bundle();
        int id = getIntent().getIntExtra(SoundboardContract.SoundEntry._ID,0);
        bundle.putInt(SoundboardContract.SoundEntry._ID, id);
        detailsFragment.setArguments(bundle);
        manager.beginTransaction().replace(R.id.fl_details, detailsFragment).commit();
    }
}
