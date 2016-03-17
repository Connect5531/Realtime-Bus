package com.lowwor.realtimebus.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import com.f2prateek.rx.preferences.RxSharedPreferences;
import com.lowwor.realtimebus.R;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

@Singleton
public class PreferencesHelper {

    public static final String PREF_FILE_APP = "pref_file_app";

    private static final String PREF_KEY_AUTO_COMPLETE = "PREF_KEY_AUTO_COMPLETE";
    private static final String PREF_KEY_START_FROM = "PREF_KEY_START_FROM";
    private static final String PREF_KEY_AUTO_REFRESH = "PREF_KEY_AUTO_REFRESH";
    private static final String PREF_KEY_LAST_QUERY = "PREF_KEY_LAST_QUERY";

    private final SharedPreferences mPref;
    private final SharedPreferences mSettingsPref;
    private final RxSharedPreferences mRxSharedPreferences;
    private final Resources mResources;

    @Inject
    public PreferencesHelper(Context context) {
        mPref = context.getSharedPreferences(PREF_FILE_APP, Context.MODE_PRIVATE);
        mSettingsPref = PreferenceManager.getDefaultSharedPreferences(context);
        mRxSharedPreferences = RxSharedPreferences.create(mPref);
        mResources = context.getResources();
    }

    public void clear() {
        mPref.edit().clear().apply();
    }

    public void saveStartFromFirst(boolean isStartFromFirst) {
        mPref.edit().putBoolean(PREF_KEY_START_FROM, isStartFromFirst).apply();
    }

    public boolean getIsStartFromFirst() {
        return mPref.getBoolean(PREF_KEY_START_FROM, true);
    }

    public Observable<Set<String>> getAutoCompleteAsObservable() {
        return mRxSharedPreferences.getStringSet(PREF_KEY_AUTO_COMPLETE).asObservable();
    }

    public void saveAutoCompleteItem(String item) {
        Set<String> mbuses = mPref.getStringSet(PREF_KEY_AUTO_COMPLETE, new HashSet<String>());
        mbuses.add(item);
        mPref.edit().putStringSet(PREF_KEY_AUTO_COMPLETE, mbuses).apply();
    }

    public void saveLastQueryLine(String lastQueryStation) {
        mPref.edit().putString(PREF_KEY_LAST_QUERY, lastQueryStation).apply();
    }


    public String getLastQueryLine() {
        return mPref.getString(PREF_KEY_LAST_QUERY, "3A");
    }

    public boolean getAutoRefresh() {
        return mPref.getBoolean(PREF_KEY_AUTO_REFRESH, false);

    }

    public void saveAutoRefresh(boolean isAutoRefresh) {
        mPref.edit().putBoolean(PREF_KEY_AUTO_REFRESH, isAutoRefresh).apply();
    }

    public boolean getShowNotification() {
        return mSettingsPref.getBoolean(mResources.getString(R.string.preferences_settings_key_notification), true);
    }

    public boolean getShowPopupNotification() {
        return mSettingsPref.getBoolean(mResources.getString(R.string.preferences_settings_key_pop_up_notification), true);
    }

    public int getAutoRefreshInterval() {
        return Integer.valueOf(mSettingsPref.getString(mResources.getString(R.string.preferences_settings_key_auto_refresh_interval), null));
    }

}