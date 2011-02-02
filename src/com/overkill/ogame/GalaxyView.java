package com.overkill.ogame;

import com.overkill.ogame.game.Galaxy;
import com.overkill.ogame.game.GalaxyPlanet;
import com.overkill.ogame.game.GalaxyPlanetAdapter;
import com.overkill.ogame.game.GalaxySystem;
import com.overkill.ogame.game.Planet;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

public class GalaxyView extends ListActivity {

    public void setTitle(CharSequence title) {
        super.setTitle(title);
        ((TextView) findViewById(android.R.id.title)).setText(title);
    }
    
    public void setInfo(CharSequence info) {
    	((TextView) findViewById(R.id.subtitle)).setText(info);
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
    	requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
    	super.onCreate(savedInstanceState);  
		setContentView(R.layout.activity_tab_resources);	
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.system_title_galaxy);   
		
		Planet origin = MainTabActivity.game.getCurrentPlanet();		
		Galaxy galaxy = new Galaxy();
		GalaxySystem system = galaxy.getSolarSystem(MainTabActivity.game, origin.getGalaxy(), origin.getSystem());		

		setTitle("OGame Galaxy View");
		setInfo(origin.getGalaxy() + ":" + origin.getSystem());

		GalaxyPlanetAdapter adapter = new GalaxyPlanetAdapter(this, R.layout.adapter_galaxy_parent, system);
		
		setListAdapter(adapter);		
		
	}
	
}
