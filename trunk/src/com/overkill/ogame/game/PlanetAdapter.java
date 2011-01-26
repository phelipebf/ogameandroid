package com.overkill.ogame.game;
/*
 * File: WordsAdapter.java
 * Platform: Android 1.5
 * Last update: 24.01.2010 
 * ©2010 OV3RK1LL 
 */
import java.util.ArrayList;

import com.overkill.ogame.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ImageView;

public class PlanetAdapter extends ArrayAdapter<Planet> { 
	
	private Context context;
	private int textViewResourceId;
	
	public PlanetAdapter(Context context, int textViewResourceId, ArrayList<Planet> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.textViewResourceId = textViewResourceId;
	}	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(this.textViewResourceId, null);
            }
            Planet p = this.getItem(position);
            if (p != null) {            	
                    ((TextView) v.findViewById(R.id.name)).setText(p.getName());
                    if(p.isMoon())
                    	((TextView) v.findViewById(R.id.info)).setText(p.getCoordinates() + " | " + context.getString(R.string.moon));
                    else
                    	((TextView) v.findViewById(R.id.info)).setText(p.getCoordinates() + " | " + p.getUsedFields() + "/" + p.getMaxFields());
                    ((ImageView) v.findViewById(R.id.image)).setImageDrawable(p.getIcon());     
            }
            return v;
    }	
}
