package com.overkill.ogame;

import java.util.ArrayList;
import java.util.List;

import com.overkill.gui.SortCursor;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.provider.MediaStore;

public class SettingsView extends PreferenceActivity {
	final String[] MEDIA_COLUMNS = new String[] {MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA};	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Load the preferences from an XML resource
        getPreferenceManager().setSharedPreferencesName("ogame");
        addPreferencesFromResource(R.xml.preferences);
        
        ListPreference listPref = (ListPreference)findPreference("fleetsystem_sound");
		final Cursor c = new SortCursor(new Cursor[] { getInternal(), getExternal() }, MediaStore.MediaColumns.TITLE);						
		final CharSequence[] keys = new CharSequence[c.getCount()];	
		final CharSequence[] values = new CharSequence[c.getCount()];	
		int col_tile = c.getColumnIndex(MediaStore.Audio.Media.TITLE);			
		for(int i=0;i<c.getCount();i++){
			keys[i] = c.getString(col_tile);
			values[i] = "file://" + c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA));
			c.moveToNext();
		}
        listPref.setEntries(keys);
        listPref.setEntryValues(values);
    }
    
    private static String constructBooleanTrueWhereClause(List<String> columns) {        
        if (columns == null) return null;        
        StringBuilder sb = new StringBuilder();
        for (int i = columns.size() - 1; i >= 0; i--) {
            sb.append(columns.get(i)).append("=1 or ");
        }        
        if (columns.size() > 0) {
            sb.setLength(sb.length() - 4);
        }        
        return sb.toString();
    }
    
    private Cursor getInternal(){
		List<String> columns = new ArrayList<String>();
		columns.add(MediaStore.Audio.AudioColumns.IS_NOTIFICATION);
		return getContentResolver().query(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, MEDIA_COLUMNS,constructBooleanTrueWhereClause(columns),null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		
	}
	
	private Cursor getExternal(){
		List<String> columns = new ArrayList<String>();
		columns.add(MediaStore.Audio.AudioColumns.IS_NOTIFICATION);
		final String status = Environment.getExternalStorageState();        
        return (status.equals(Environment.MEDIA_MOUNTED) || status.equals(Environment.MEDIA_MOUNTED_READ_ONLY))
                ? this.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MEDIA_COLUMNS,constructBooleanTrueWhereClause(columns), null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER)
                : null;
		
	}
  

}