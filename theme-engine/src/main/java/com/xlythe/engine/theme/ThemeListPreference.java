package com.xlythe.engine.theme;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;

import androidx.preference.ListPreference;
import androidx.preference.PreferenceManager;

import java.util.List;

public class ThemeListPreference extends ListPreference {
    public ThemeListPreference(Context context) {
        super(context);
        setup();
    }

    public ThemeListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    @TargetApi(21)
    public ThemeListPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup();
    }

    @TargetApi(21)
    public ThemeListPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setup();
    }

    private void setup() {
        // Grab all installed themes
        final List<App> themes = Theme.getApps(getContext());
        CharSequence[] themeEntry = new CharSequence[themes.size() + 1];
        CharSequence[] themeValue = new CharSequence[themes.size() + 1];

        // Set a default
        themeEntry[0] = getContext().getApplicationInfo().loadLabel(getContext().getPackageManager());
        themeValue[0] = getContext().getPackageName();

        // Add the rest to the preference
        for (int i = 1; i < themeEntry.length; i++) {
            themeEntry[i] = themes.get(i - 1).getName();
            themeValue[i] = themes.get(i - 1).getPackageName();
        }

        // Set the values
        setEntries(themeEntry);
        setEntryValues(themeValue);
        setDefaultValue(null);

        // Update the UI to display the selected theme name
        setSummary(getThemeTitle(themes, PreferenceManager.getDefaultSharedPreferences(getContext()).getString(getKey(), getContext().getPackageName())));
    }

    private String getThemeTitle(List<App> themes, Object packageName) {
        for (App a : themes) {
            if (a.getPackageName().equals(packageName)) {
                return a.getName();
            }
        }
        return getContext().getApplicationInfo().loadLabel(getContext().getPackageManager()).toString();
    }
}
