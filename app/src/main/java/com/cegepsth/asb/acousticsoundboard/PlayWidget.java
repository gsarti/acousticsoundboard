package com.cegepsth.asb.acousticsoundboard;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * Implementation of App Widget functionality.
 */
public class PlayWidget extends AppWidgetProvider implements AsyncDatabaseResponse {

    private Sound sound;
    private Settings settings;
    private static Context mContext;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        mContext = context;
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.play_widget);
        DatabaseConnector db = new DatabaseConnector(context);
        db.getSettings();
        Intent intent = new Intent(context, AudioService.class);
        intent.putExtra("song", "Jai ldoua");
        intent.setAction(AudioTask.ACTION_PLAY_SOUND);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.imgWidget, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void processFinish(Object o) {
        if (o instanceof Settings){
            DatabaseConnector db = new DatabaseConnector(mContext);
            db.getSound(((Settings) o).getFavoriteSong());
        }

    }
}

