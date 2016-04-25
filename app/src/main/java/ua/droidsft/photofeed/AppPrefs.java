package ua.droidsft.photofeed;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Helper class for saving and getting app preferences.
 * Created by Vlad on 24.04.2016.
 */
public class AppPrefs {
    private static final String PREF_QUERY = "query";

    public static String getQuery(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_QUERY, null);
    }

    public static void saveQuery(Context context, String query) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_QUERY, query)
                .apply();
    }
}
