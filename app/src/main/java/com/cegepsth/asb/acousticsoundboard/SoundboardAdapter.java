package com.cegepsth.asb.acousticsoundboard;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class SoundboardAdapter extends RecyclerView.Adapter<SoundboardAdapter.SoundViewHolder> {

    private List<Sound> mListeSound;

    public SoundboardAdapter(List<Sound> soundList) {
        mListeSound = soundList;
    }

    @Override
    public SoundViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.sound_list, parent, false);
        return new SoundViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SoundViewHolder holder, int position) {
        Sound s = mListeSound.get(position);
        if (s == null){ return;}
        holder.mNameSound.setText(s.getName());
        holder.mDurationSound.setText(s.getDuration());
        holder.itemView.setTag(s.getPath());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mListeSound.size();
    }

    public class SoundViewHolder extends RecyclerView.ViewHolder{
        public TextView mNameSound;
        public TextView mDurationSound;
        public ImageView mImageSound;

        public SoundViewHolder(View view){
            super(view);
            mNameSound = itemView.findViewById(R.id.tv_name_sound);
            mDurationSound = itemView.findViewById(R.id.tv_duration_sound);
            mImageSound = itemView.findViewById(R.id.iv_image_sound);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = mNameSound.getContext();
                    Intent intent = new Intent(context, AudioService.class);
                    intent.putExtra("song", (String)view.getTag());
                    intent.setAction(AudioTask.ACTION_PLAY_SOUND);
                    context.startService(intent);
                }
            });
        }

    }
}
