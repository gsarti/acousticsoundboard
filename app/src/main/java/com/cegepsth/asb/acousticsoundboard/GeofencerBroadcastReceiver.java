package com.cegepsth.asb.acousticsoundboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

/**
 * Created by NSA on 12/17/2017.
 */

public class GeofencerBroadcastReceiver extends BroadcastReceiver {
    public static final String TAG = GeofencerBroadcastReceiver.class.getSimpleName();


    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.e(TAG, String.format("Error code : %d", geofencingEvent.getErrorCode()));
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        // Check which transition type has triggered this event
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            Intent i = new Intent(context, AudioService.class);
            i.putExtra("songId", 0);
            i.setAction(AudioTask.ACTION_PLAY_FAVORITE_SOUND);
            context.startActivity(i);
        }
        // Send the notification
        //sendNotification(context, geofenceTransition);
    }
}
