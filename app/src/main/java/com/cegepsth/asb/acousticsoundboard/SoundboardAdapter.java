package com.cegepsth.asb.acousticsoundboard;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cegepsth.asb.acousticsoundboard.databinding.SoundItemBinding;

import java.util.List;

import static com.cegepsth.asb.acousticsoundboard.SoundboardContract.BASE_CONTENT_URI;
import static com.cegepsth.asb.acousticsoundboard.SoundboardContract.PATH_SETTINGS;
import static com.cegepsth.asb.acousticsoundboard.SoundboardContract.SoundEntry.SOUND_URI;

public class SoundboardAdapter extends RecyclerView.Adapter<SoundboardAdapter.SoundViewHolder> {

    private final OnDeleteListener mDeleteListener;
    private final SoundClickListener mClickListener;
    private List<Sound> mListeSound;
    private SoundItemBinding binding;

    public SoundboardAdapter(OnDeleteListener listener, List<Sound> soundList) {
        mListeSound = soundList;
        mDeleteListener = listener;
        mClickListener = (SoundClickListener)listener;
    }

    @Override
    public SoundViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        binding = DataBindingUtil.inflate(inflater, R.layout.sound_item, parent, false);
        return new SoundViewHolder(binding);
    }

    public interface OnDeleteListener {
        void onDeleteClicked();
    }

    public interface SoundClickListener {
        void onListItemClick(int id);
    }

    @Override
    public void onBindViewHolder(SoundViewHolder holder, int position) {
        Sound s = mListeSound.get(position);
        if (s == null) {
            return;
        }
        holder.itemView.setTag(s.getId());
        if (s.getImage().length > 0) {
            holder.mImageSound.setImageBitmap(BitmapFactory.decodeByteArray(s.getImage(), 0, s.getImage().length));
        }
        holder.bind(s);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mListeSound.size();
    }

    public class SoundViewHolder extends RecyclerView.ViewHolder implements OnDeleteListener, View.OnClickListener
    {
        public TextView mNameSound;
        public TextView mDurationSound;
        public ImageView mImageSound;
        public ImageButton mImgMore;
        public View mView;
        private final SoundItemBinding mBinding;

        public SoundViewHolder(final SoundItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;

            mNameSound = itemView.findViewById(R.id.tv_name_sound);
            mDurationSound = itemView.findViewById(R.id.tv_duration_sound);
            mImageSound = itemView.findViewById(R.id.iv_image_sound);
            mImgMore = itemView.findViewById(R.id.imgMore);
            mView = binding.getRoot();
            mView.setOnClickListener(this);
            mImgMore.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context ctx = view.getContext();
                    PopupMenu menu = new PopupMenu(ctx, view);
                    menu.getMenuInflater()
                            .inflate(R.menu.more, menu.getMenu());

                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            Context c = mNameSound.getContext();
                            int id = binding.getSound().getId();
                            Uri uri;
                            switch (item.getItemId()) {
                                case R.id.action_edit:
                                    Intent intent = new Intent(c, EditActivity.class);
                                    intent.putExtra("soundId", id);
                                    c.startActivity(intent);
                                    break;
                                case R.id.action_favorite:
                                    ContentValues content = new ContentValues();
                                    content.put(SoundboardContract.SettingsEntry.FAVORITESOUND_KEY, id);
                                    uri = ContentUris.withAppendedId(BASE_CONTENT_URI.buildUpon().appendPath(PATH_SETTINGS).build(), 1);
                                    c.getContentResolver().update(uri, content, null, null);
                                    showNotification(c);
                                    break;
                                case R.id.action_delete:
                                    uri = ContentUris.withAppendedId(SoundboardContract.SoundEntry.SOUND_URI, id);
                                    c.getContentResolver().delete(uri, null, null);
                                    mDeleteListener.onDeleteClicked();
                                    break;
                                case R.id.action_details:
                                    Intent i = new Intent(c, DetailsActivity.class);
                                    i.putExtra(SoundboardContract.SoundEntry._ID, id);
                                    c.startActivity(i);
                                    break;
                                default:
                                    Toast.makeText(c, "Une erreur est survenue!", Toast.LENGTH_SHORT).show();
                            }
                            return true;
                        }
                    });

                    menu.show();
                }
            });
        }

        public void bind(Sound sound) {
            mBinding.setSound(sound);
            mBinding.executePendingBindings();
        }

        public void showNotification(Context c){
            String name = mBinding.getSound().getName();
            byte[] image = mBinding.getSound().getImage();
            Bitmap bmp = image.length > 0 ? BitmapFactory.decodeByteArray(image,0, image.length) : BitmapFactory.decodeResource(c.getResources(), R.mipmap.ic_launcher);
            Intent i = new Intent(c, MainActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(c, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder b = new NotificationCompat.Builder(c);
            b.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(bmp)
                    .setContentTitle("New Acoustic favorite!")
                    .setContentText("\"" + name + "\" " + R.string.favorite_notification)
                    .setDefaults(Notification.DEFAULT_LIGHTS| Notification.DEFAULT_SOUND)
                    .setContentIntent(contentIntent)
                    .setContentInfo("Info");
            NotificationManager notificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1, b.build());
        }

        @Override
        public void onDeleteClicked() {
        }

        @Override
        public void onClick(View view) {
            mClickListener.onListItemClick(mBinding.getSound().getId());
        }
    }
}
