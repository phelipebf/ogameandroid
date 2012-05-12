package com.overkill.ogame.game;
/*
 * File: WordsAdapter.java
 * Platform: Android 1.5
 * Last update: 24.01.2010 
 * �2010 OV3RK1LL 
 */
import java.util.ArrayList;

import com.overkill.ogame.MainTabActivity;
import com.overkill.ogame.PlanetListActivity;
import com.overkill.ogame.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

public class PlanetAdapter extends ArrayAdapter<Planet> { 
	
	private Context context;
	private PlanetListActivity activity;
	private int textViewResourceId;
	
	public PlanetAdapter(Context context, int textViewResourceId, ArrayList<Planet> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.activity = (PlanetListActivity) context;
		this.textViewResourceId = textViewResourceId;
	}	
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(this.textViewResourceId, null);
        }
        final Planet p = this.getItem(position);
        if (p != null) {            
        	ImageView planetImage = ((ImageView) v.findViewById(R.id.image));
        	TextView planeName = ((TextView) v.findViewById(R.id.name));
        	TextView planeInfo = ((TextView) v.findViewById(R.id.info));
        	        	
        	ImageView moonImage = ((ImageView) v.findViewById(R.id.image_moon));
        	
        	planetImage.setImageResource(p.getIcon());
        	planeName.setText(p.getName());
        	planeInfo.setText(p.getCoordinates() + " | " + p.getUsedFields() + "/" + p.getMaxFields());
        	
        	if(p.hasMoon()){
        		moonImage.setVisibility(View.VISIBLE);
        		moonImage.setImageResource(p.getMoon().getIcon());
        		moonImage.setOnClickListener(new ImageView.OnClickListener() {			
					@Override
					public void onClick(View v) {
						activity.planetClicked(p.getMoon());
					}
				});	
        	}else{
        		moonImage.setVisibility(View.INVISIBLE);
        	}
        }
        return v;
    }	
}
