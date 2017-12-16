package com.cegepsth.asb.acousticsoundboard;

import android.media.MediaPlayer;
import android.provider.MediaStore;

public class AudioTask {

    public static final String ACTION_PLAY_SOUND = "play-sound";
    public static final String ACTION_PLAY_FAVORITE_SOUND = "play-favorite-sound";
    public static final String ACTION_STOP_SOUND = "stop-sound";
    public static final String ACTION_PAUSE_SOUND = "pause-sound";
    public static final String ACTION_RESUME_SOUND = "resume-sound";

    public static void executeTask(MediaPlayer player, String action) {
        switch (action){
            case ACTION_PLAY_SOUND:
                playSound(player);
                break;
            case ACTION_PLAY_FAVORITE_SOUND:
                playSound(player);
                break;
            case ACTION_STOP_SOUND:
                stopSound(player);
                break;
            case ACTION_PAUSE_SOUND:
                pauseSound(player);
                break;
            case ACTION_RESUME_SOUND:
                resumeSound(player);
                break;
            default:
                break;
        }
    }

    private static void playSound(MediaPlayer player) {
        player.start();
    }

    private static void stopSound(MediaPlayer player) {
        player.stop();
    }

    private static void pauseSound(MediaPlayer player) {
        player.pause();
    }

    private static void resumeSound(MediaPlayer player) {
        player.seekTo(player.getCurrentPosition());
        player.start();
    }
}