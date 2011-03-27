package com.overkill.ogame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.overkill.ogame.game.GalaxyPlanet;
import com.overkill.ogame.game.GalaxyPlanetAdapter;
import com.overkill.ogame.game.GameClient;
import com.overkill.ogame.game.Planet;
import com.overkill.ogame.game.Tools;

public class GalaxyView extends ListActivity {

	private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;
	
	private int galaxy;
	private int system;
	private int position = 0;
	
	private String probeCount = "0";
	private int recyclerCount;
	private String missileCount = "0";
	private String slotsUsed = "0";
	private String slotsTotal = "0";
	
	HashMap<String, ArrayList<GalaxyPlanet>> solarSystems = new HashMap<String, ArrayList<GalaxyPlanet>>();//cache
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
    	requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
    	super.onCreate(savedInstanceState);  
		setContentView(R.layout.activity_tab_listview);	
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.system_title_galaxy);   
		
        
        
        
        gestureDetector = new GestureDetector(new MyGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event)) {
                    return true;
                }
                return false;
            }
        };
               
        getListView().setOnTouchListener(gestureListener);
        
		Planet origin = MainTabActivity.game.getCurrentPlanet();
		
		Uri uri = getIntent().getData();
        if(uri != null){
	        galaxy = Integer.valueOf(uri.getQueryParameter("galaxy"));
	        system = Integer.valueOf(uri.getQueryParameter("system"));
	        position = Integer.valueOf(uri.getQueryParameter("position"));
	    }else{
			galaxy = origin.getGalaxy();
			system = origin.getSystem();
			position = 0;
	    }
        
		//initialize spinner with galaxies
        final Spinner galaxySpinner = (Spinner) findViewById(R.id.galaxy_spinner);
        ArrayAdapter galaxiesAdapter = ArrayAdapter.createFromResource(
                this, R.array.galaxies, android.R.layout.simple_spinner_item);
        galaxiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        galaxySpinner.setAdapter(galaxiesAdapter);
        galaxySpinner.setSelection(galaxy-1);
        
        final EditText systemText = (EditText) findViewById(R.id.system);
        systemText.setText(String.valueOf(system));
        systemText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
        	@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				try {
					//first try to parse then load
					int selectedGalaxy = (Integer) galaxySpinner.getSelectedItemPosition()+1;
					int actualSystem = Integer.parseInt(systemText.getText().toString());
					
					galaxy = selectedGalaxy;
					system = actualSystem;
					load();					
				} catch (NumberFormatException e) {
					Toast.makeText(getApplicationContext(), "not possible", Toast.LENGTH_SHORT).show();
				}
				return false;
			}
		});
        
        ((ImageButton) findViewById(R.id.prev_system)).setOnClickListener(new ImageButton.OnClickListener() {			
			@Override
			public void onClick(View v) {
				prevSystem();
			}
		});	
        
        ((ImageButton) findViewById(R.id.next_system)).setOnClickListener(new ImageButton.OnClickListener() {			
			@Override
			public void onClick(View v) {
				nextSystem();
			}
		});
        
		load();
	}
	
	class MyGestureDetector extends SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                // right to left swipe
                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                	return prevSystem();
                }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                	return nextSystem();
                }
            } catch (Exception e) {
                // nothing
            }
            return false;
        }
    }
	
	private boolean prevSystem() {
    	system--;
    	if(system < 0)
    		system = 0;
    	load();
    	return true;
	}
	
	private boolean nextSystem() {
		system++;
    	if(system > 499)
    		system = 499;
    	load();
    	return true;
	}
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gestureDetector.onTouchEvent(event))
	        return true;
	    else
	    	return false;
    }
	
	private void load(){			
		getListView().setVisibility(View.INVISIBLE);
		((TextView)findViewById(R.id.txt_info)).setVisibility(View.VISIBLE);
		((TextView)findViewById(R.id.txt_info)).setText(getInfo());
		
		Thread t = new Thread(new Runnable() {			
			@Override
			public void run() {
				//final boolean canLoad = canLoadContent(MainTabActivity.game, newGalaxy, newSystem);
				final GalaxyPlanetAdapter adapter = new GalaxyPlanetAdapter(GalaxyView.this, R.layout.adapter_galaxy_parent,
						getSolarSystem(MainTabActivity.game, galaxy, system));
				runOnUiThread(new Runnable() {					
					@Override
					public void run() {
						setListAdapter(adapter);	
						getListView().setVisibility(View.VISIBLE);	
						((TextView)findViewById(R.id.txt_info)).setText(getInfo());	
						((EditText) findViewById(R.id.system)).setText(String.valueOf(system));
						if(position > 0){
							getListView().setSelection(position);
							getListView().setSelected(true);
						}
					}
				});
			}
		});		
		t.start();		
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, final int position, long id) {
		super.onListItemClick(l, v, position, id);	
		final GalaxyPlanet p = (GalaxyPlanet)getListAdapter().getItem(position);

		/*if(p.isEmptySlot() && ??? == 0) {
			Toast.makeText(getApplicationContext(), "no colony ship", Toast.LENGTH_SHORT).show();
			return;
		}*/
		
	    final AlertDialog.Builder dialog = new AlertDialog.Builder(GalaxyView.this);
		dialog.setTitle(R.string.more);
		if(p.isEmptySlot()) {
			dialog.setMessage(getApplicationContext().getString(R.string.galaxy_colonize, p.getPosition()));
			dialog.setNegativeButton(android.R.string.cancel, cancelDialog());
			dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					Toast.makeText(getApplicationContext(), "missing functionality", Toast.LENGTH_SHORT).show();
				}
			});
		}  else {
			dialog.setMessage(getApplicationContext().getString(R.string.galaxy_probes));
			dialog.setNegativeButton(android.R.string.cancel, cancelDialog());
			dialog.setPositiveButton(android.R.string.ok,  new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {					
					Thread t = new Thread(new Runnable() {			
						@Override
						public void run() {
							final String sendResult = sendProbe(MainTabActivity.game, galaxy, system, p.getPosition(), 1);
							runOnUiThread(new Runnable() {					
								@Override
								public void run() {
									Toast.makeText(getApplicationContext(), sendResult, Toast.LENGTH_SHORT).show();
								}
							});
						}
					});		
					t.start();					
				}
			});
		}
    	dialog.show();
	}
	
	private DialogInterface.OnClickListener cancelDialog() { 
		return new DialogInterface.OnClickListener() {
	    	public void onClick(DialogInterface dialog, int whichButton) {
	    		dialog.cancel();
	    	}
		};
    }

	/**
	 * false -> not enough Deuterium! You need 10 Units of Deuterium
	 * @param game
	 * @param galaxy
	 * @param system
	 * @return
	 */
	private boolean canLoadContent(GameClient game, int galaxy, int system) {
		
		if(isSolarSystemInCache(galaxy, system)) return true;
				
		List<NameValuePair> postData = new ArrayList<NameValuePair>(2);
        postData.add(new BasicNameValuePair("galaxy", String.valueOf(galaxy)));
        postData.add(new BasicNameValuePair("system", String.valueOf(system)));
        String jsonString = game.execute("page=galaxyCanLoad&ajax=1", postData);
        
        boolean canLoad = false;
		try {
			JSONObject json = new JSONObject(jsonString);
			canLoad = json.getBoolean("status");
	        
		} catch (JSONException e) {}
			
		return canLoad;
	}
	
	private boolean isSolarSystemInCache(int galaxy, int system) {
		return solarSystems.containsKey(galaxy + "-" + system);
	}

	private ArrayList<GalaxyPlanet> getSolarSystem(GameClient game, int galaxy, int system) {
		String key = galaxy + "-" + system;
		if(!isSolarSystemInCache(galaxy, system)) { //not in cache
			List<NameValuePair> postData = new ArrayList<NameValuePair>(2);
	        postData.add(new BasicNameValuePair("galaxy", String.valueOf(galaxy)));
	        postData.add(new BasicNameValuePair("system", String.valueOf(system)));
	        String html = game.execute("page=galaxyContent&ajax=1", postData);
			
	        ArrayList<GalaxyPlanet> solarSystem = parseGalaxy(html, galaxy, system);
	        solarSystems.put(key, solarSystem);
		}
		return solarSystems.get(key);
	}
	
	private String sendProbe(GameClient game, int galaxy, int system, int planetPosition, int shipCount) {
		return sendShips(game, 6, galaxy, system, planetPosition, 1, shipCount);
	}
	
	private String sendRecycler(GameClient game, int galaxy, int system, int planetPosition, int shipCount) {
		return sendShips(game, 8, galaxy, system, planetPosition, 2, shipCount);
	}
	
	/**
	 * Post data to server
	 * @param game
	 * @param mission 6=espionage, 7=colonize, 8=recycle
	 * @param galaxy
	 * @param system
	 * @param planetPosition
	 * @param planetType 1=planet, 2=debris, 3=moon
	 * @param shipCount
	 * @return
	 */
	private String sendShips(GameClient game, int mission, int galaxy, int system, int planetPosition, int planetType, int shipCount) {

		List<NameValuePair> postData = new ArrayList<NameValuePair>(2);
        postData.add(new BasicNameValuePair("mission", String.valueOf(mission)));
        postData.add(new BasicNameValuePair("galaxy", String.valueOf(galaxy)));
        postData.add(new BasicNameValuePair("system", String.valueOf(system)));
        postData.add(new BasicNameValuePair("position", String.valueOf(planetPosition)));
        postData.add(new BasicNameValuePair("type", String.valueOf(planetType)));
        postData.add(new BasicNameValuePair("shipCount", String.valueOf(shipCount)));
        String html = game.execute("page=minifleet&ajax=1", postData);
        return  parseResponse(html);
	}
	
	private ArrayList<GalaxyPlanet> parseGalaxy(String html, int galaxy, int system){
		
		ArrayList<GalaxyPlanet> galaxySystem = new ArrayList<GalaxyPlanet>();
		
		Document solarSystem = Jsoup.parse(html);
		probeCount = solarSystem.select("#probeValue").text();
		recyclerCount = Integer.parseInt(solarSystem.select("#recyclerValue").text());
		missileCount = solarSystem.select("#missileValue").text();
		
		Elements slotValue = solarSystem.select("#slotValue");
		slotsUsed = slotValue.select("#slotUsed").text();
		slotValue.remove("#slotUsed");
		slotsTotal = slotValue.text().trim().substring(2);
		
		for(Element tr : solarSystem.select("tr.row")) {
			GalaxyPlanet planet = new GalaxyPlanet();
			
			planet.setPosition(Integer.parseInt(tr.select("td.position").text()));
			
			//empty slots have microplanet1
			if(tr.select("td.microplanet").size() == 1) {
				planet.setEmptySlot(false);
				Element microplanet = tr.select("td.microplanet").get(0);
				
				String img = microplanet.attr("style");
				img = Tools.between(img, "(", ")");
				img = img.replace("img/planets/micro/", "").replace(".gif", "");
				img = img.substring(0, img.lastIndexOf("_"));
				planet.setImage(getResources().getIdentifier("drawable/planet_" + img, null, getPackageName()));
				
				Elements h4 = microplanet.select("h4");
				planet.setPlanetName(h4.select("span.textNormal").text());
				
				h4.remove("span.spacing");
				String planetActivity = "";
				if(h4.select("img").size() > 0){ //icon	
					planetActivity = getApplicationContext().getString(R.string.galaxy_activity_now);
				} else {
					planetActivity = h4.text().trim();
					if(planetActivity.indexOf("Activity:") >= 0) {
						planetActivity = planetActivity.substring(planetActivity.indexOf("Activity:")+9);
					} else {
						planetActivity = null;
					}
				}
				planet.setPlanetActivity(planetActivity);
				
				planet.setPlanetCoords(microplanet.select("#pos-planet").text());
								
				Element moon = tr.select("td.moon").get(0);
				if(moon.children().size() > 0) {
					planet.setMoon(true);
				}
				
				Element debris = tr.select("td.debris").get(0);
				Elements debrisContent = debris.select("li.debris-content");
				if(debrisContent.size() > 0) {
					String debrisMetal = debrisContent.get(0).text();
					debrisMetal = debrisMetal.substring(debrisMetal.indexOf(": ") + 2);
					planet.setDebrisMetal(debrisMetal);
					String debrisCrystal = debrisContent.get(1).text();
					debrisCrystal = debrisCrystal.substring(debrisCrystal.indexOf(": ") + 2);
					planet.setDebrisCrystal(debrisCrystal);
					String debrisRecyclersNeeded = debris.select("li.debris-recyclers").get(0).text();
					debrisRecyclersNeeded = debrisRecyclersNeeded.substring(debrisRecyclersNeeded.indexOf(": ") + 2);
					planet.setDebrisRecyclersNeeded(debrisRecyclersNeeded);
				}
				
				Element player = tr.select("td.playername").get(0);
				h4 = player.select("h4");				
				if(h4.isEmpty()) {//player is us
					planet.setPlayerName(player.select("span").get(0).text());
				} else {
					planet.setPlayerName(h4.select("span > span").text());
					String playerRank = player.select("li.rank").text();
					playerRank = playerRank.substring(playerRank.indexOf(": ") + 2);
					planet.setPlayerRank(playerRank);
					planet.setPlayerStatus(player.select("a.tipsGalaxy > span").attr("class"));			
				}
				
				Element allytag = tr.select("td.allytag").get(0);
				if(allytag.children().size() > 0) {	//no ally
					String allyName = allytag.select("h4 > span").text();
					allyName = allyName.substring(allyName.indexOf("Alliance ") + 9);
					planet.setAllyName(allyName);
					String allyRank = allytag.select("li.rank").text();
					allyRank = allyRank.substring(allyRank.indexOf(": ") + 2);
					planet.setAllyRank(allyRank);
					String allyMembers = allytag.select("li.members").text();
					allyMembers = allyMembers.substring(allyMembers.indexOf(": ") + 2);
					planet.setAllyMembers(allyMembers);	
				}
			}
			galaxySystem.add(planet);
		}
		return galaxySystem;
	}
	
	public String getInfo() {
		return "probe: " + probeCount 
			+ " recycler: " + recyclerCount 
			+ " missile: " + missileCount
			+ " slots: " + slotsUsed
			+ "/" + slotsTotal;
	}
	
	
	/**
	 * Examples:
	 * 		"612 [3:286:12]" -> Fleet dispatch Error, no free fleet slots available [3:286:12]
	 * 		"600 2 2 0 0 1 1 [3:286:12]" -> Success, send espionage probe to: [3:286:12] (1)
	 */
	private String parseResponse(String response) {
		String[] retVals = response.split(" ");
		String result = "";
		switch(Integer.parseInt(retVals[0])) {
			case 600:
				result = "Success";
				switch(Integer.parseInt(retVals[6])) {
					case 1: 
						result += ", send espionage probe to: " + retVals[7];
					break;
					case 2: 
						result += ", send recycler to: " + retVals[7];
					break;
				}
				result += " (" + retVals[5] + ")";
			break;
			case 601:
				result = "An error has occurred " + retVals[1];
			break;
			case 602:
				result = "Error, there is no moon " + retVals[1];
			break;
			case 603:
				result = "Error, player can't be approached because of newbie protection " + retVals[1];
			break;
			case 604:
				result = "Player is too strong to be attacked " + retVals[1];
			break;
			case 605:
				result = "Error, player is in vacation mode " + retVals[1];
			break;
			case 610:
				result = "Error, not enough ships available, send maximum number:"+retVals[1];
			break;
			case 611:
				result = "Error, no ships available " + retVals[1];
			break;
			case 612:
				result = "Error, no free fleet slots available " + retVals[1];
			break;
			case 613:
				result = "Error, you don't have enough deuterium " + retVals[1];
			break;
			case 614:
				result = "Error, there is no planet there " + retVals[1];
			break;
			case 615:
				result = "Error, not enough cargo capacity " + retVals[1];
			break;
			case 616:
				result = "Multi-alarm " + retVals[1];
			break;
			case 617:
				result = "Admin or GM " + retVals[1];
			break;
			case 618:
				result = "Attack ban until 01.01.1970 01:00:00";
			break;
		}
		return result;
	}
	
}
