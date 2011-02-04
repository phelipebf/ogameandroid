package com.overkill.ogame.game;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.overkill.ogame.R;

public class GalaxyPlanetAdapter extends ArrayAdapter<GalaxyPlanet> {

	private Context context;
	private int textViewResourceId;

 	public GalaxyPlanetAdapter(Context context, int textViewResourceId, ArrayList<GalaxyPlanet> system) {
		super(context, textViewResourceId, system);
		this.context = context;
		this.textViewResourceId = textViewResourceId;
 	}
 	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) this.context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(this.textViewResourceId, parent, false);
		}
		((ImageView) v.findViewById(R.id.img_moon)).setVisibility(View.INVISIBLE);
		ImageView imgDebris = ((ImageView) v.findViewById(R.id.img_debris));
		imgDebris.setVisibility(View.INVISIBLE);
		final GalaxyPlanet p = this.getItem(position);
		if(p.isEmptySlot()) {
			((TextView) v.findViewById(R.id.txt_name)).setText("empty");
			((TextView) v.findViewById(R.id.txt_player)).setText("slot");			
		} else {
			((TextView) v.findViewById(R.id.txt_name)).setText(p.getPlanetName() + " " + p.getPlanetActivity());
			String player = p.getPlayerName();
			if(p.getPlayerRank() != null)
				player += " #" + p.getPlayerRank();
			((TextView) v.findViewById(R.id.txt_player)).setText(player);
			if(p.getDebrisRecyclersNeeded() > 0) {
				imgDebris.setVisibility(View.VISIBLE);
				imgDebris.setOnClickListener(new ImageView.OnClickListener() {			
					@Override
					public void onClick(View v) {
						Toast.makeText(context, "m:" + p.getDebrisMetal() + ", k:" + p.getDebrisCrystal(), Toast.LENGTH_LONG).show();
					}
				});				
			}
		}
		return v;
	}
	
	@Override
	public int getCount() {
		//we always have 15 planets 
		return 15;
	}
}