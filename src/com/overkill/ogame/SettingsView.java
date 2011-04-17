package com.overkill.ogame;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsView extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        // Load the preferences from an XML resource
        getPreferenceManager().setSharedPreferencesName("ogame");
        addPreferencesFromResource(R.xml.preferences);
    }
}