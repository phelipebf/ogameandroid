package com.overkill.ogame;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.text.method.DigitsKeyListener;
import android.widget.EditText;

public class SettingsView extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        // Load the preferences from an XML resource
        getPreferenceManager().setSharedPreferencesName("ogame");
        addPreferencesFromResource(R.xml.preferences);
        
        EditTextPreference  fleetsystem_intervall = (EditTextPreference)findPreference("fleetsystem_intervall");
        EditText editText = (EditText)fleetsystem_intervall.getEditText();
        editText.setKeyListener(DigitsKeyListener.getInstance(false,true));
        
    }
}