package com.cegepsth.asb.acousticsoundboard;

/**
 * Created by NSA on 12/1/2017.
 */

import android.app.Activity;
import android.content.Intent;

public class ThemeManager
{
    public static int CurrentTheme = R.style.LightTheme;
    public static int DarkTheme = R.style.DarkTheme;

    public static int LightTheme = R.style.LightTheme;

    public static void changeToTheme(Activity activity, int theme)
    {
        CurrentTheme = theme;
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
    }

    public static void onActivityCreateSetTheme(Activity activity)
    {
        switch (CurrentTheme)
        {
            case R.style.DarkTheme:
                activity.setTheme(R.style.DarkTheme);
                break;
            case R.style.LightTheme:
                activity.setTheme(R.style.LightTheme);
                break;
            default:
                activity.setTheme(R.style.LightTheme);
        }
    }
}
