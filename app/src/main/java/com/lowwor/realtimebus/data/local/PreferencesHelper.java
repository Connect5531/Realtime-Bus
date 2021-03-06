package com.lowwor.realtimebus.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.f2prateek.rx.preferences2.RxSharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lowwor.realtimebus.R;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

import static com.lowwor.realtimebus.utils.CollectionUtils.removeDuplicateWithOrder;

@Singleton
public class PreferencesHelper {

    public static final String PREF_FILE_APP = "pref_file_app";

    private static final String PREF_KEY_AUTO_COMPLETE_LIST = "PREF_KEY_AUTO_COMPLETE_LIST";
    private static final String PREF_KEY_START_FROM = "PREF_KEY_START_FROM";
    private static final String PREF_KEY_LAST_QUERY = "PREF_KEY_LAST_QUERY";

    private final SharedPreferences mPref;
    private final SharedPreferences mSettingsPref;
    private final RxSharedPreferences mRxSharedPreferences;
    private final Resources mResources;
    private final Gson gson;

    @Inject
    public PreferencesHelper(Context context) {
        mPref = context.getSharedPreferences(PREF_FILE_APP, Context.MODE_PRIVATE);
        mSettingsPref = PreferenceManager.getDefaultSharedPreferences(context);
        mRxSharedPreferences = RxSharedPreferences.create(mPref);
        mResources = context.getResources();
        gson = new Gson();
    }

    public void clear() {
        mPref.edit().clear().apply();
        mSettingsPref.edit().clear().apply();
    }

    public void saveStartFromFirst(boolean isStartFromFirst) {
        mPref.edit().putBoolean(PREF_KEY_START_FROM, isStartFromFirst).apply();
    }

    public boolean getIsStartFromFirst() {
        return mPref.getBoolean(PREF_KEY_START_FROM, true);
    }

    public Observable<List<String>> getAutoCompleteAsObservable() {

        return mRxSharedPreferences.getString(PREF_KEY_AUTO_COMPLETE_LIST, getDefaultAutoCompleteString()).asObservable()
                .map(new Function<String, List<String>>() {
                    @Override
                    public List<String> apply(String json) throws Exception {
                        List<String> items = new ArrayList<>();
                        if (!TextUtils.isEmpty(json)) {
                            items = gson.fromJson(json, new TypeToken<List<String>>() {
                            }.getType());
                            return items;
                        } else {
                            items.add("3A");
                            return items;
                        }
                    }
                });
    }

    public void saveAutoCompleteItem(String item) {
        String json = mPref.getString(PREF_KEY_AUTO_COMPLETE_LIST, getDefaultAutoCompleteString());
        List<String> items = new ArrayList<>();
        if (json != null) {
            items = gson.fromJson(json, new TypeToken<List<String>>() {
            }.getType());
        }
        items.add(item);

        removeDuplicateWithOrder(items);
        mPref.edit().putString(PREF_KEY_AUTO_COMPLETE_LIST, gson.toJson(items)).apply();
    }

    private String getDefaultAutoCompleteString() {
        List<String> items = new ArrayList<>();
        items.add("3A");
        return gson.toJson(items);
    }

    public void saveLastQueryLine(String lastQueryStation) {
        mPref.edit().putString(PREF_KEY_LAST_QUERY, lastQueryStation).apply();
    }


    public String getLastQueryLine() {
        return mPref.getString(PREF_KEY_LAST_QUERY, "3A");
    }

    public boolean getShowNotification() {
        return mSettingsPref.getBoolean(mResources.getString(R.string.preferences_settings_key_notification), true);
    }

    public boolean getTrackBackground() {
        return mSettingsPref.getBoolean(mResources.getString(R.string.preferences_settings_key_track_background), true);
    }

    public boolean getShowPopupNotification() {
        return mSettingsPref.getBoolean(mResources.getString(R.string.preferences_settings_key_pop_up_notification), true);
    }

    public int getAutoRefreshInterval() {
        return Integer.valueOf(mSettingsPref.getString(mResources.getString(R.string.preferences_settings_key_auto_refresh_interval), "3"));
    }

}