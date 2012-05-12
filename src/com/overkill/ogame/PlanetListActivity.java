package com.overkill.ogame;

import java.util.ArrayList;

import com.overkill.ogame.game.Planet;
import com.overkill.ogame.game.PlanetAdapter;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class PlanetListActivity extends ListActivity {
	ArrayList<Planet> planets = new ArrayList<Planet>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		
		planets.addAll(MainTabActivity.game.getPlanets());
		
		PlanetAdapter adapter = new PlanetAdapter(this, R.layout.adapter_item_planet, planets);
		setListAdapter(adapter);	
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		if(planets.get(position).getId() == 0){
			finish();
			return;
		}
		MainTabActivity.game.switchPlanet(planets.get(position).getId());
		Toast.makeText(this, getString(R.string.change_planet, planets.get(position).getName()), Toast.LENGTH_SHORT).show();
		finish();
	}
	
	public void planetClicked(Planet p){
		if (p.getId() == 0) {
			finish();
			return;
		}
		MainTabActivity.game.switchPlanet(p.getId());
		Toast.makeText(this, getString(R.string.change_planet, p.getName()), Toast.LENGTH_SHORT).show();
		finish();
	}
}
