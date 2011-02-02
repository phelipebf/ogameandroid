package com.overkill.ogame.game;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.overkill.ogame.R;

public class GalaxyPlanetAdapter extends ArrayAdapter<GalaxyPlanet> {

	private Context context;
	private int textViewResourceId;
	private GalaxySystem system;

 	public GalaxyPlanetAdapter(Context context, int textViewResourceId, GalaxySystem system) {
		super(context, textViewResourceId);
		this.context = context;
		this.textViewResourceId = textViewResourceId;
		this.system = system;
 	}
 	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) this.context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(this.textViewResourceId, parent, false);
		}
		GalaxyPlanet p = this.system.getPlanet(position + 1);
		if (p != null) {			
			((TextView) v.findViewById(R.id.txt_name)).setText(p.getPlanetName());
			((TextView) v.findViewById(R.id.txt_player)).setText(String.valueOf(p.getPlayerName()));
		}
		return v;
	}
	
	@Override
	public int getCount() {
		//we always have 15 planets 
		return 15;
	}
}