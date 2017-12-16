package com.cegepsth.asb.acousticsoundboard;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by NSA on 12/15/2017.
 */

public class SoundboardContract {

    public static final String AUTHORITY = "com.cegepsth.asb.acousticsoundboard";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_SOUNDS = "sounds";
    public static final String PATH_SETTINGS = "settings";

    public static final class SoundEntry implements BaseColumns {

        public static final Uri SOUND_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SOUNDS).build();

        public static final String TABLE_SOUNDS = "Sounds";

        public static final String NAME_KEY = "name";
        public static final String PATH_KEY = "path";
        public static final String DURATION_KEY = "duration";
        public static final String IMAGE_KEY = "image";
    }

    public static final class SettingsEntry implements BaseColumns {

        public static final Uri SETTINGS_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SETTINGS).build();

        public static final String TABLE_SETTINGS = "Settings";
        public static final String FAVORITESOUND_KEY = "favoriteSound";
    }
}