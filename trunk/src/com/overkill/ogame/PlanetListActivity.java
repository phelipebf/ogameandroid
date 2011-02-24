package com.overkill.ogame;

import java.util.ArrayList;

import com.overkill.ogame.game.Planet;
import com.overkill.ogame.game.PlanetAdapter;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class PlanetListActivity extends ListActivity {
	ArrayList<Planet> planets = new ArrayList<Planet>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.planet_list);
		
		planets.addAll( MainTabActivity.game.getPlanets());
		
		for(int i=0;i<planets.size();i++){
			if(planets.get(i).hasMoon()){
				planets.add(i+1, planets.get(i).getMoon());
			}
		}
		
		PlanetAdapter adapter = new PlanetAdapter(this, R.layout.adapter_item_planet, planets);
		setListAdapter(adapter);	
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		if(planets.get(position).getId()==0){
			finish();
			return;
		}
		MainTabActivity.game.switchPlanet(planets.get(position).getId());
		Log.i("Planet", planets.get(position).getName() + "=" + planets.get(position).getId());
		Toast.makeText(this, getString(R.string.change_planet, planets.get(position).getName()), Toast.LENGTH_SHORT).show();
		finish();
	}
}
