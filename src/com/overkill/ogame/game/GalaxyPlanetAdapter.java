package com.overkill.ogame.game;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Html;
import android.text.method.LinkMovementMethod;
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

	private SharedPreferences settings;
	
 	public GalaxyPlanetAdapter(Context context, int textViewResourceId, ArrayList<GalaxyPlanet> system) {
		super(context, textViewResourceId, system);
		
		final String TAG = "ogame";
		
		this.context = context;
		this.textViewResourceId = textViewResourceId;
		
		settings = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
 	}
 	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(this.textViewResourceId, parent, false);
		}

		TextView txtPlanetIndex = ((TextView) v.findViewById(R.id.txt_planet_index));
		TextView txtPlanetName = ((TextView) v.findViewById(R.id.txt_name));
		TextView txtActivity = ((TextView) v.findViewById(R.id.txt_activity));
		ImageView imgActivity = ((ImageView) v.findViewById(R.id.img_activity));
		
		TextView txtMoonActivity = ((TextView) v.findViewById(R.id.moon_txt_activity));
		ImageView imgMoonActivity = ((ImageView) v.findViewById(R.id.moon_img_activity));
		
		TextView txtPlayer = ((TextView) v.findViewById(R.id.txt_player));
		TextView txtRank = ((TextView) v.findViewById(R.id.txt_rank));
		TextView txtAlly = ((TextView) v.findViewById(R.id.txt_ally));
		ImageView imgPlanet = ((ImageView) v.findViewById(R.id.img_planet));
		ImageView imgDebris = ((ImageView) v.findViewById(R.id.img_debris));
		ImageView imgMoon = ((ImageView) v.findViewById(R.id.img_moon));

		txtActivity.setVisibility(View.INVISIBLE);
		imgActivity.setVisibility(View.INVISIBLE);
		
		txtMoonActivity.setVisibility(View.INVISIBLE);
		imgMoonActivity.setVisibility(View.INVISIBLE);
		
		txtPlayer.setVisibility(View.INVISIBLE);
		txtRank.setVisibility(View.INVISIBLE);
		txtAlly.setVisibility(View.INVISIBLE);
		imgPlanet.setVisibility(View.INVISIBLE);
		imgDebris.setVisibility(View.INVISIBLE);
		imgMoon.setVisibility(View.INVISIBLE);
		
		final GalaxyPlanet p = this.getItem(position);
		
		if(position + 1 < 10)
			txtPlanetIndex.setText("0" + String.valueOf(position + 1));
		else
			txtPlanetIndex.setText(String.valueOf(position + 1));
		
		if(p.isEmptySlot()) {
			txtPlanetName.setText(context.getString(R.string.galaxy_empty_slot));
		} else {
			txtPlanetName.setText(p.getPlanetName());

			if(p.getPlanetActivity() != null) {
				if(p.getPlanetActivity().equals(context.getString(R.string.galaxy_activity_now))){
					imgActivity.setVisibility(View.VISIBLE);
				}else{
					txtActivity.setVisibility(View.VISIBLE);
					txtActivity.setText(context.getString(R.string.galaxy_activity, p.getPlanetActivity()));
				}
			}
			
			if(p.getPlayerRank() != null) {
				txtRank.setVisibility(View.VISIBLE);
				txtRank.setText("#" + p.getPlayerRank());
			}
			
			if(p.getAllyName() != null) {
				txtAlly.setVisibility(View.VISIBLE);
				txtAlly.setText(Html.fromHtml("<a href=\"ogame://alliance/?allyid=" + String.valueOf(p.getAllyID()) + "\">[" + p.getAllyName() + "]</a>"));
				txtAlly.setMovementMethod(LinkMovementMethod.getInstance());
				txtAlly.setFocusable(false);
				
			}
			
			txtPlayer.setVisibility(View.VISIBLE);
			txtPlayer.setText(p.getPlayerName());
			txtPlayer.setTextColor(p.getPlayerColor());

			imgPlanet.setVisibility(View.VISIBLE);
			imgPlanet.setImageResource(p.getImage());
			
			if(p.hasMoon()) {
				imgMoon.setVisibility(View.VISIBLE);
				if(p.getMoonActivity() != null) {
					if(p.getMoonActivity().equals(context.getString(R.string.galaxy_activity_now))){
						imgMoonActivity.setVisibility(View.VISIBLE);
					}else{
						txtMoonActivity.setVisibility(View.VISIBLE);
						txtMoonActivity.setText(context.getString(R.string.galaxy_activity, p.getMoonActivity()));
					}
				}
			}
			
			if(p.getDebrisRecyclersNeeded() > 0) {				
				int recyclersNeededBold = Integer.valueOf(settings.getString("debris_marker", "2"));			
				String debrisIdentifier = "drawable/debris" + (p.getDebrisRecyclersNeeded() >= recyclersNeededBold ? "_green" : ""); 

				imgDebris.setImageResource(context.getResources().getIdentifier(debrisIdentifier, null, context.getPackageName()));
				imgDebris.setVisibility(View.VISIBLE);
				imgDebris.setOnClickListener(new ImageView.OnClickListener() {			
					@Override
					public void onClick(View v) {
						Toast.makeText(context, "m:" + p.getDebrisMetal() + ", k:" + p.getDebrisCrystal(), Toast.LENGTH_SHORT).show();
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
	
	@Override
	public boolean isEnabled(int position) {
		return true;
	}
}