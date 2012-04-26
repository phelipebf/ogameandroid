package com.overkill.ogame;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.text.method.DigitsKeyListener;
import android.widget.EditText;

public class SettingsView extends PreferenceActivity {
	
	CheckBoxPreference show_ads;
	private static final String paypalurl = "https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=JBA3WQ9LAFH8C&lc=US&currency_code=EUR&bn=PP%2dDonationsBF%3abtn_donate_SM%2egif%3aNonHosted";
    private static final String abouturl = "http://code.google.com/p/ogameandroid/people/list";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        // Load the preferences from an XML resource
    	getPreferenceManager().setSharedPreferencesName("ogame");
        addPreferencesFromResource(R.xml.preferences);
        
        EditTextPreference fleetsystem_intervall = (EditTextPreference)findPreference("fleetsystem_intervall");
        EditText editText = (EditText)fleetsystem_intervall.getEditText();
        editText.setKeyListener(DigitsKeyListener.getInstance(false,true));
        
        // TODO: what is this for?
//        ((CheckBoxPreference)findPreference("show_ads")).setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
//        	@Override
//        	public boolean onPreferenceChange(Preference preference, Object newValue) {
//        		return true;
//        	}
//		});
        
        ((Preference)findPreference("donate")).setEnabled(false);
        
        ((Preference)findPreference("donate_paypal")).setOnPreferenceClickListener(new OnPreferenceClickListener() {			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(paypalurl));
				startActivity(myIntent);
				return false;
			}
		});
        
        ((Preference)findPreference("about")).setOnPreferenceClickListener(new OnPreferenceClickListener() {			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(abouturl)));
				return false;
			}
		});             
        
        if(getIntent().hasExtra("donateOnly")){
        	Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(paypalurl));
			startActivity(myIntent);
			finish();
        }
    }
}