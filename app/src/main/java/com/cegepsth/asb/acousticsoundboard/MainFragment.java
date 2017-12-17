package com.cegepsth.asb.acousticsoundboard;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

import static com.cegepsth.asb.acousticsoundboard.SoundboardContract.SoundEntry.SOUND_URI;

/**
 * Created by Gabriele Sarti on 17-12-17.
 */

public class MainFragment extends Fragment implements SoundboardAdapter.OnDeleteListener,
        SoundboardAdapter.SoundClickListener {

    private boolean mTwoPane;
    private List<Sound> soundList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Context mContext;

    public MainFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        mContext = getActivity();
        ThemeManager.onActivityCreateSetTheme(getActivity());
        mRecyclerView = v.findViewById(R.id.rv_sounds);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(v.getContext(),LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        LoadUI();
        if (getActivity().findViewById(R.id.main_landscape) != null)
            mTwoPane = true;
        else
            mTwoPane = false;
        return v;
    }

    public void LoadUI(){
        Uri uri = SOUND_URI;
        Sound sound;
        Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, SoundboardContract.SoundEntry.NAME_KEY);
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
        mAdapter = new SoundboardAdapter(this, soundList);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onDeleteClicked() {
        LoadUI();
    }

    @Override
    public void onListItemClick(int id) {
        DetailsFragment detailsFragment = new DetailsFragment();
        if (mTwoPane){
            FragmentManager manager = getFragmentManager();
            Bundle bundle = new Bundle();
            bundle.putInt(SoundboardContract.SoundEntry._ID, id);
            detailsFragment.setArguments(bundle);
            manager.beginTransaction().replace(R.id.fl_details, detailsFragment).commit();
        }
        else {
            Intent intent = new Intent(getActivity(), AudioService.class);
            intent.putExtra("soundId", id);
            intent.setAction(AudioTask.ACTION_PLAY_SOUND);
            getActivity().startService(intent);
        }
    }
}
