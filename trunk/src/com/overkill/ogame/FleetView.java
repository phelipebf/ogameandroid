package com.overkill.ogame;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.overkill.ogame.game.FleetAdapter;
import com.overkill.ogame.game.Planet;
import com.overkill.ogame.game.Ship;
import com.overkill.ogame.game.Tools;

/**
 * Handles fleet control
 * Different states are set by and extra named tab. 
 * @author Paolo
 */
public class FleetView extends ListActivity {
	String task = "movement";
	@SuppressWarnings("rawtypes")
	ArrayAdapter adapter;

	String[] ulKey;
	
	public int selectedShips = 0;	
	
	private final String COLONIZATION_ID = "208";
	private final String MISSION_NONE = "0";
	private final String MISSION_UNION_ATTACK = "2"; 
	
	private String targetGalaxy = null;
	private String targetSystem = null;
	private String targetPosition = null;
	private String mission = MISSION_NONE;
	private String planetType = "1";
	private String union = "1";
	
	private int maxSpeed = 0;
	private int speedFactor = 0;	
	private ArrayList<String> shipIDs = new ArrayList<String>();
	private ArrayList<Integer> speeds = new ArrayList<Integer>();
	private ArrayList<Integer> completeConsumptions = new ArrayList<Integer>();
	private int storageCapacity = 0;

	private ArrayList<String> shortcuts = new ArrayList<String>();
	private ArrayList<String> combatForces = new ArrayList<String>();
	

	// 2 -> 3
	//index.php?page=fleet3&session=912bb66e8f11 POST
	
	//$('form[name=details]').serialize()
	//"type=1&mission=0&union=0&am202=2&galaxy=3&system=293&position=8&speed=10"
	

	
	// 3 -> 4
	//index.php?page=movement&session=912bb66e8f11
	
	//$('form[name=sendForm]').serialize()
	//"holdingtime=1&expeditiontime=1&galaxy=3&system=293&position=8&type=1&mission=4&union2=0&holdingOrExpTime=0&speed=10&am202=2&metal=0&crystal=0&deuterium=0"

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
   	 	getWindow().requestFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
				
		if(getIntent().hasExtra("tab")) {
			task = getIntent().getExtras().getString("tab");
		}
		
		if("fleet1".equals(task)) {
			setContentView(R.layout.activity_tab_fleet1);			
		} else if("fleet2".equals(task)) {
			setContentView(R.layout.activity_tab_fleet2);			
		}
		
		registerForContextMenu(getListView());
		
		Thread t = new Thread(new Runnable() {			
			@Override
			public void run() {

				if(getIntent().hasExtra("galaxy")) {
					targetGalaxy = getIntent().getExtras().getString("galaxy");
					targetSystem = getIntent().getExtras().getString("system");
					targetPosition = getIntent().getExtras().getString("position");
					mission = getIntent().getExtras().getString("mission");
					planetType = getIntent().getExtras().getString("planetType");
				} else {
					Planet p = MainTabActivity.game.getCurrentPlanet();
					targetGalaxy = String.valueOf(p.getGalaxy());
					targetSystem = String.valueOf(p.getSystem());
					targetPosition = String.valueOf(p.getPosition());
					mission = "0";
					planetType = "1";
				}
				
				if("fleet1".equals(task)) {
					onCreateFleet1();
				} else if("fleet2".equals(task)) {
					onCreateFleet2();
				}
				runOnUiThread(new Runnable() {					
					@Override
					public void run() {
						setListAdapter(adapter);	
						setProgressBarIndeterminateVisibility(false);												
					}
				});
			}
		});
		setProgressBarIndeterminateVisibility(true);
		t.start();
	}
	
	private void onCreateFleet2() {
		
		final Document document = sendShips();
		updateWidgetsFromVariables();
        
		String script = document.select("script").not("script[src]").html();		
		maxSpeed = Integer.parseInt(Tools.between(script, "maxSpeed = ", ";"));
		speedFactor = Integer.parseInt(Tools.between(script, "speedFactor = ", ";"));
		storageCapacity = Integer.parseInt(Tools.between(script, "storageCapacity = ", ";"));
		
		//Log.i("fleet2", "maxSpeed="+maxSpeed+", speedFactor="+speedFactor+", storageCapacity="+storageCapacity);
		
		for(int i = 0; i < 13; i++) {
			if(script.indexOf("shipIDs[" + i + "]") > -1) {
				shipIDs.add(Tools.between(script, "shipIDs[" + i + "] = ", ";"));
				completeConsumptions.add(Integer.parseInt(Tools.between(script, "completeConsumptions[" + i + "] = ", ";")));
				speeds.add(Integer.parseInt(Tools.between(script, "speeds[" + i + "] = ", ";")));

			} else {
				break;
			}
		}
		//Log.i("fleet2", "shipIDs="+shipIDs+", completeConsumptions="+completeConsumptions+", speeds="+speeds);

		final Spinner shortcutsSpinner = (Spinner) findViewById(R.id.shortcuts);
        final ArrayAdapter<CharSequence> shortcutsAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
        shortcutsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        runOnUiThread(new Runnable() {			
			@Override
			public void run() {
		        shortcutsSpinner.setAdapter(shortcutsAdapter);				
		        for(Element option : document.select("#slbox > option")) {
		        	shortcutsAdapter.add(option.html());
		        	shortcuts.add(option.attr("value"));
		        }
			}
		});
        shortcutsSpinner.setOnItemSelectedListener(new ListView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				shortLinkChange(false);
				updateVariables();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
        });
        
        final Spinner acs = (Spinner) findViewById(R.id.acs);
        final ArrayAdapter<CharSequence> acsAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item);
        acsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        runOnUiThread(new Runnable() {			
			@Override
			public void run() {
				acs.setAdapter(acsAdapter);
				for(Element option : document.select("#aksbox > option")) {
		        	acsAdapter.add(option.html());
		        	combatForces.add(option.attr("value"));
		        }
			}
		});
        acs.setOnItemSelectedListener(new ListView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				shortLinkChange(true);
				updateVariables();
				handleUnion();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
        });

		final Spinner speedSpinner = (Spinner) findViewById(R.id.speed);
		speedSpinner.setOnItemSelectedListener(new ListView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				updateVariables();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
        });
        
        ((RadioButton) findViewById(R.id.radioPlanet)).setOnClickListener(new OnClickListener() {		
			@Override
            public void onClick(View v) {
                planetType = "1";
            }
        });
        ((RadioButton) findViewById(R.id.radioMoon)).setOnClickListener(new OnClickListener() {		
			@Override
            public void onClick(View v) {
            	planetType = "3";
            }
        });
        ((RadioButton) findViewById(R.id.radioDebris)).setOnClickListener(new OnClickListener() {		
			@Override
            public void onClick(View v) {
            	planetType = "2";
            }
        });
        		
		Button refresh = (Button)findViewById(R.id.refresh);
		refresh.setOnClickListener(new Button.OnClickListener() {			
			@Override
			public void onClick(View v) {
				updateVariablesFromWidgets();
				updateVariables();
			}
		});
		
		final Button next = (Button) this.findViewById(R.id.fleet1_next);
		next.setOnClickListener(new Button.OnClickListener() {				
			@Override
			public void onClick(View v) {
				trySubmit();
			}
		});
	}
	
	private void onCreateFleet1() {
		ulKey = getIntent().getExtras().getStringArray("ulKey"); 
		
		String body = MainTabActivity.game.get("page=fleet1");
		final Document document = Jsoup.parse(body);
		
		ArrayList<Ship> ships = new ArrayList<Ship>();
		for(int i = 0; i < ulKey.length; i++){
			ArrayList<Ship> o = Tools.parseFleet(document, ulKey[i], FleetView.this);
			ships.addAll(o);
		}
		adapter = new FleetAdapter(FleetView.this, R.layout.adapter_item_fleet, ships);
		
		final Button next = (Button) this.findViewById(R.id.fleet1_next);
		next.setOnClickListener(new Button.OnClickListener() {				
			@Override
			public void onClick(View v) {
				Intent fleet2 = new Intent(FleetView.this, FleetView.class)
					.putExtra("tab", "fleet2");

				HashMap<String, String> ships = new HashMap<String, String>();
				for(int i = 0; i < adapter.getCount(); i++) {
					Ship ship = (Ship) adapter.getItem(i);
					if(ship.getUsed() > 0) {
						ships.put("am" + ship.getId(), String.valueOf(ship.getUsed()));
					} else {
						ships.put("am" + ship.getId(), "");
					}
				}
				fleet2.putExtra("ships", ships);
				
				if(!"0".equals(mission)) {
					fleet2.putExtra("galaxy", targetGalaxy);
					fleet2.putExtra("system", targetSystem);
					fleet2.putExtra("position", targetPosition);
					fleet2.putExtra("mission", mission);
					fleet2.putExtra("planetType", planetType);
				}
	            startActivity(fleet2);   
			}
		});
		
		((Button) this.findViewById(R.id.fleet1_all)).setOnClickListener(new Button.OnClickListener() {				
			@Override
			public void onClick(View v) {
				selectedShips = 0;
				for(int i = 0; i < adapter.getCount(); i++) {
					Ship ship = (Ship) adapter.getItem(i);
					ship.setUsed(ship.getTotal());
					selectedShips += ship.getTotal();
				}
				next.setEnabled(true);
				adapter.notifyDataSetChanged();
			}
		});
		
		((Button) this.findViewById(R.id.fleet1_reset)).setOnClickListener(new Button.OnClickListener() {				
			@Override
			public void onClick(View v) {
				selectedShips = 0;
				for(int i = 0; i < adapter.getCount(); i++) {
					Ship ship = (Ship) adapter.getItem(i);
					ship.setUsed(0);
				}
				next.setEnabled(false);
				adapter.notifyDataSetChanged();
			}
		});
	}	

	// 1 -> 2
	//index.php?page=fleet2&session=912bb66e8f11 POST	
	//$('form[name=shipsChosen]').serialize()
	//"galaxy=3&system=293&position=7&type=1&mission=0&speed=10&am202=2"
	private Document sendShips() {

		List<NameValuePair> postData = new ArrayList<NameValuePair>();
        postData.add(new BasicNameValuePair("galaxy", targetGalaxy));
        postData.add(new BasicNameValuePair("system", targetSystem));
        postData.add(new BasicNameValuePair("position", targetPosition));
        postData.add(new BasicNameValuePair("type", planetType));
		postData.add(new BasicNameValuePair("mission", mission));
        postData.add(new BasicNameValuePair("speed", "10"));
        
        HashMap<String, String> ships = (HashMap<String, String>) getIntent().getExtras().getSerializable("ships");
        for (String name : ships.keySet()) {
        	String value = ships.get(name);
	        postData.add(new BasicNameValuePair(name, value));
		}
        String html = MainTabActivity.game.execute("page=fleet2", postData);
		
        return  Jsoup.parse(html);
	}	
	
	
	private void displayError(String errorCode) {
		String message = "Fleets can not be sent to this target: ";
		if("1".equals(errorCode)) {
			message += "Uninhabited planet";
		} else if("1d".equals(errorCode)) {
			message += "No debris field";
		} else if("2".equals(errorCode)) {
			message += "Player in vacation mode";
		} else if("3".equals(errorCode)) {
			message += "Admin or GM";
		} else if("4".equals(errorCode)) {
			message += "You have to research Astrophysics first.";
		} else if("5".equals(errorCode)) {
			message += "Noob protection";
		} else if("6".equals(errorCode)) {
			message += "This planet can not be attacked as the player is to strong!";
		}			
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
	}

	
	private void updateWidgetsFromVariables() {

       	runOnUiThread(new Runnable() {							
			@Override
			public void run() {
				((Spinner) findViewById(R.id.galaxy_spinner)).setSelection(Integer.parseInt(targetGalaxy)-1);
				((EditText) findViewById(R.id.system)).setText(targetSystem);
				((Spinner) findViewById(R.id.position)).setSelection(Integer.parseInt(targetPosition)-1);
				
				if("1".equals(planetType)) {
					((RadioButton) findViewById(R.id.radioPlanet)).setChecked(true);
				} else if("3".equals(planetType)) {
					((RadioButton) findViewById(R.id.radioMoon)).setChecked(true);
				} else if("2".equals(planetType)) {
					((RadioButton) findViewById(R.id.radioDebris)).setChecked(true);			
				}	
			}
		});		
	}
	
	private void updateVariablesFromWidgets() {

       	runOnUiThread(new Runnable() {							
			@Override
			public void run() {
		        Spinner galaxySpinner = (Spinner) findViewById(R.id.galaxy_spinner);
		        targetGalaxy = getResources().getStringArray(R.array.galaxies)[galaxySpinner.getSelectedItemPosition()];
		
		        EditText systemText = (EditText) findViewById(R.id.system);
				targetSystem = systemText.getText().toString();
				
				Spinner positionSpinner = (Spinner) findViewById(R.id.position);
		        targetPosition = getResources().getStringArray(R.array.positions)[positionSpinner.getSelectedItemPosition()];
			}
		});		
	}
	
	
	/********** fleet.js **********************/
	private double getDistance(int targetGalaxy, int targetSystem, int targetPosition) {

		Planet p = MainTabActivity.game.getCurrentPlanet();
		
		int diffGalaxy = Math.abs(p.getGalaxy() - targetGalaxy);
		int diffSystem = Math.abs(p.getSystem() - targetSystem);
		int diffPlanet = Math.abs(p.getPosition() - targetPosition);

		if(diffGalaxy != 0) {
			return diffGalaxy * 20000;
		} else if(diffSystem != 0) {
			return (diffSystem * 5 * 19) + 2700;
		} else if(diffPlanet != 0) {
			return (diffPlanet * 5) + 1000;
		} else {
			return 5;
		}
	}

	private long getDuration(int speed, double distance) {
		return Math.round(((35000 / speed * Math.sqrt(distance * 10 / maxSpeed) + 10) / speedFactor ));
	}

	private long getConsumption(long duration, double distance) {
		long consumptionCounter = 0;
		//long holdingConsumption = 0;

		int countedShips = 0;

		for(int i=0; i < shipIDs.size(); i++) {
			countedShips++;

			double shipSpeedValue = 35000 / (duration * speedFactor - 10) * Math.sqrt(distance * 10 / speeds.get(i));

			//holdingConsumption += completeConsumptions[i] * holdingTime;

			consumptionCounter += completeConsumptions.get(i) * distance / 35000 * ((shipSpeedValue / 10) + 1) * ((shipSpeedValue / 10) + 1);
		}

		if(countedShips>0) {
			consumptionCounter = Math.round(consumptionCounter) + 1;

			/*if(holdingTime>0) {
				consumptionCounter += Math.max(Math.floor(holdingConsumption/10),1);
			}*/

			return consumptionCounter;
		} else {
			return 0;
		}
	}

	private long getFreeStorage(long consumption) {
		long freeStorageCounter = storageCapacity;
		freeStorageCounter -= consumption;
		//freeStorageCounter -= (probeStorageCapacity-getConsumption(210));

		return freeStorageCounter;
	}

	private void updateVariables() {

		final int targetGalaxy = Integer.parseInt(this.targetGalaxy);
		final int targetSystem = Integer.parseInt(this.targetSystem);
		final int targetPosition = Integer.parseInt(this.targetPosition);
		
       	runOnUiThread(new Runnable() {							
			@Override
			public void run() {
		        Spinner speedSpinner = (Spinner) findViewById(R.id.speed);
		        int speed = Integer.parseInt(getResources().getStringArray(R.array.speed)[speedSpinner.getSelectedItemPosition()]);
		        speed = speed / 10; //option values are 10,9,8... descriptions are 100,90,80...
						
				double distance = getDistance(targetGalaxy, targetSystem, targetPosition);
				long duration = getDuration(speed, distance);
				long consumption = getConsumption(duration, distance);
				long cargoSpace = getFreeStorage(consumption);
				//cargoLeft = cargoSpace - metal - crystal - deuterium;
		
				SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy HH:mm:ss");
				Date d = new Date();	    
			    Date arrivalTime = new Date(d.getTime() + 1000 * duration);
			    Date returnTime = new Date(d.getTime() + 1000 * 2 * duration);
		
			    ((TextView) findViewById(R.id.fleet2_duration)).setText(Tools.sec2str(duration));
			    ((TextView) findViewById(R.id.fleet2_arrival)).setText(sdf.format(arrivalTime));
			    ((TextView) findViewById(R.id.fleet2_return)).setText(sdf.format(returnTime));
			    ((TextView) findViewById(R.id.fleet2_consumption)).setText(String.valueOf(consumption));
			    ((TextView) findViewById(R.id.fleet2_cargobays)).setText(String.valueOf(cargoSpace));
			}
       	});
	 
	}	
	

	/********** fleet2.js **********************/	
	private void shortLinkChange(boolean aks)
	{
		String value = "";
	    if(aks) {
	        Spinner acs = (Spinner) findViewById(R.id.acs);
	        value = combatForces.get(acs.getSelectedItemPosition());
	    } else {
	        Spinner shortcutsSpinner = (Spinner) findViewById(R.id.shortcuts);
	        value = shortcuts.get(shortcutsSpinner.getSelectedItemPosition());
	    }

	    if("-".equals(value)) {
	        return;
	    } else {
	        String[] parts = value.split("#");
	        
	        targetGalaxy = String.valueOf(parts[0]);
	        targetSystem = String.valueOf(parts[1]);
	        targetPosition = String.valueOf(parts[2]);
	        planetType = String.valueOf(parts[3]);
	        
	        updateWidgetsFromVariables();
	    }
	}

	private void handleUnion()
	{
        Spinner acs = (Spinner) findViewById(R.id.acs);
        String value = combatForces.get(acs.getSelectedItemPosition());

	    if("-".equals(value)) {
	        union = "0";
	        mission = MISSION_NONE;
	    } else {
	        String[] parts = value.split("#");
	        union = parts[5];
	        mission = MISSION_UNION_ATTACK;
	    }
	}


	private void trySubmit() {

	    updateVariablesFromWidgets();

		List<NameValuePair> postData = new ArrayList<NameValuePair>();
        postData.add(new BasicNameValuePair("galaxy", targetGalaxy));
        postData.add(new BasicNameValuePair("system", targetSystem));
        postData.add(new BasicNameValuePair("position", targetPosition));        
        postData.add(new BasicNameValuePair("type", planetType));

        //cannot send fleet to the current planet
		Planet p = MainTabActivity.game.getCurrentPlanet();
        if(targetGalaxy.equals(String.valueOf(p.getGalaxy())) &&
        		targetSystem.equals(String.valueOf(p.getSystem())) &&
        		targetPosition.equals(String.valueOf(p.getPosition())) &&
        		planetType.equals("1")) { //TODO: sending fleet from moon
        	return;
        }

        HashMap<String, String> ships = (HashMap<String, String>) getIntent().getExtras().getSerializable("ships");
        String amount = ships.get("am"+COLONIZATION_ID);
    	if(!"".equals(amount)) {
	        postData.add(new BasicNameValuePair("cs", "1"));
    	}

	    final String errorCode = MainTabActivity.game.execute("page=fleetcheck&ajax=1&espionage=", postData);

       	runOnUiThread(new Runnable() {						
			@Override
			public void run() {
				if (!"0".equals(errorCode)) {
					displayError(errorCode);
				} else {
					//TODO: go to fleet3
					String msg = targetGalaxy + ":" + targetSystem + ":" + targetPosition + ":" + planetType + ", mission:" + mission;
					Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
}
