package com.overkill.ogame;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.text.method.DigitsKeyListener;
import android.widget.EditText;

public class SettingsView extends PreferenceActivity {
	
	CheckBoxPreference show_ads;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        // Load the preferences from an XML resource
        getPreferenceManager().setSharedPreferencesName("ogame");
        addPreferencesFromResource(R.xml.preferences);
        
        EditTextPreference fleetsystem_intervall = (EditTextPreference)findPreference("fleetsystem_intervall");
        EditText editText = (EditText)fleetsystem_intervall.getEditText();
        editText.setKeyListener(DigitsKeyListener.getInstance(false,true));
        
        show_ads = (CheckBoxPreference)findPreference("show_ads");
        show_ads.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				boolean newState = (Boolean)newValue;
				if(newState == true){ // Ads turned on. We don't care about billing
					return true;
				}
				return false;
			}
		});
        
        
        
    }
}