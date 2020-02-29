package com.mohamed.halim.essa.moneywellspent.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.mohamed.halim.essa.moneywellspent.R;

public class SettingsActivity extends AppCompatActivity {
    public static final String PREFERENCE_TOTAL_KEY = "total-key";
    public static final float PREFERENCE_TOTAL_DEFAULT = 00.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener
            , Preference.OnPreferenceChangeListener {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.settings);
            PreferenceScreen preferenceScreen = getPreferenceScreen();
            SharedPreferences sharedPreferences = preferenceScreen.getSharedPreferences();
            int count = preferenceScreen.getPreferenceCount();
            for (int i = 0; i < count; i++) {
                Preference p = preferenceScreen.getPreference(i);
                if (!(p instanceof CheckBoxPreference)) {
                    String value = sharedPreferences.getString(p.getKey(), "");
                    setPreferenceSummary(p, value);
                }
            }
        }

        /**
         * set the summary of preference
         * and set change listener if edit text preference
         *
         * @param p     : to set the summary of
         * @param value : of the the preference
         */
        private void setPreferenceSummary(Preference p, String value) {
            if (p instanceof EditTextPreference) {
                p.setOnPreferenceChangeListener(this);
                p.setSummary(value);
            }
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Preference p = findPreference(key);
            if (p != null && !(p instanceof CheckBoxPreference)) {
                String value = sharedPreferences.getString(key, "");
                setPreferenceSummary(p, value);
            }
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            try {
                Float.valueOf((String) newValue);
                return true;
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Value can't be set to amount", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    }
}