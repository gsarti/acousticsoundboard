package com.cegepsth.asb.acousticsoundboard;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import static com.cegepsth.asb.acousticsoundboard.SoundboardContract.BASE_CONTENT_URI;
import static com.cegepsth.asb.acousticsoundboard.SoundboardContract.PATH_SETTINGS;

public class SoundboardAdapter extends RecyclerView.Adapter<SoundboardAdapter.SoundViewHolder> {

    private List<Sound> mListeSound;

    public SoundboardAdapter(List<Sound> soundList) {
        mListeSound = soundList;
    }

    @Override
    public SoundViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.sound_item, parent, false);
        return new SoundViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SoundViewHolder holder, int position) {
        Sound s = mListeSound.get(position);
        if (s == null) {
            return;
        }
        holder.mNameSound.setText(s.getName());
        holder.mDurationSound.setText((s.getDuration() / 1000) + "");
        holder.itemView.setTag(s.getId());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mListeSound.size();
    }

    public class SoundViewHolder extends RecyclerView.ViewHolder {
        public TextView mNameSound;
        public TextView mDurationSound;
        public ImageView mImageSound;
        public ImageButton mImgMore;
        public View mView;

        public SoundViewHolder(View view) {
            super(view);

            mNameSound = itemView.findViewById(R.id.tv_name_sound);
            mDurationSound = itemView.findViewById(R.id.tv_duration_sound);
            mImageSound = itemView.findViewById(R.id.iv_image_sound);
            mImgMore = itemView.findViewById(R.id.imgMore);
            mView = view;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = mNameSound.getContext();
                    Intent intent = new Intent(context, AudioService.class);
                    intent.putExtra("songId", (int) view.getTag());
                    intent.setAction(AudioTask.ACTION_PLAY_SOUND);
                    context.startService(intent);
                }
            });
            mImgMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context ctx = view.getContext();
                    PopupMenu menu = new PopupMenu(ctx, view);
                    menu.getMenuInflater()
                            .inflate(R.menu.more, menu.getMenu());

                    //registering popup with OnMenuItemClickListener
                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            Context c = mNameSound.getContext();
                            switch (item.toString()) {
                                case "Edit":
                                    Intent intent = new Intent(c, MainActivity.class);
                                    c.startActivity(intent);
                                    break;
                                case "Favorite":
                                    ContentValues content = new ContentValues();
                                    content.put(SoundboardContract.SettingsEntry.FAVORITESOUND_KEY, (int)mView.getTag());
                                    Uri uri = ContentUris.withAppendedId(BASE_CONTENT_URI.buildUpon().appendPath(PATH_SETTINGS).build(), 1);
                                    c.getContentResolver().update(uri, content, null, null);
                                    Toast.makeText(c, "Favorite sound set!", Toast.LENGTH_SHORT).show();
                                    break;
                                case "Remove":
                                    break;
                            }
                            return true;
                        }
                    });

                    menu.show();
                }
            });
        }


    }
}
