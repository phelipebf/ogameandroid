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
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
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

/*
 * We need to 
 */
public class FleetView extends ListActivity {
	String task;

	FleetAdapter adapter;

	String[] ulKey = new String[]{"military", "civil"};
	
	public int selectedShips = 0;	
	
	private final String COLONIZATION_ID = "208";
		
	private final String MISSION_NONE = "0";
	private final String MISSION_ATTACK = "1"; 
	private final String MISSION_UNION_ATTACK = "2"; 
	private final String MISSION_TRANSPORT = "3"; 
	private final String MISSION_DEPLOYMENT = "4"; 
	private final String MISSION_ACS_DEFEND = "5"; 
	private final String MISSION_ESPIONAGE = "6"; 
	private final String MISSION_COLONIZATION = "7"; 
	private final String MISSION_RECYCLE = "8"; 
	private final String MISSION_MOON_DESTRUCTION = "10"; 
	private final String MISSION_EXPEDITION = "15"; 
	
	private String targetGalaxy = null;
	private String targetSystem = null;
	private String targetPosition = null;
	private String mission = MISSION_NONE;
	private String planetType = "1";
	private String union = "0";
	private HashMap<String, String> ships;
	private int speed;
	
	private int metal = 0;
	private int crystal = 0;
	private int deuterium = 0;
	private int remainingresources = 0;
	private int progress = 0;
	
	private int maxSpeed = 0;
	private int speedFactor = 0;	
	private ArrayList<String> shipIDs = new ArrayList<String>();
	private ArrayList<Integer> speeds = new ArrayList<Integer>();
	private ArrayList<Integer> completeConsumptions = new ArrayList<Integer>();
	private int storageCapacity = 0;

	private ArrayList<String> shortcuts = new ArrayList<String>();
	private ArrayList<String> combatForces = new ArrayList<String>();

	private ArrayList<String> missions = new ArrayList<String>();

	/**
	 * Starts the Activity with the give Tab string and closes the current one
	 * @param tab
	 */
	private void startTab(String tab){
		Intent fleet = new Intent(FleetView.this, FleetView.class)
		.putExtra("tab", tab)
		.putExtra("galaxy", targetGalaxy)
		.putExtra("system", targetSystem)
		.putExtra("position", targetPosition)
		.putExtra("planetType", planetType)
		.putExtra("mission", mission)
		.putExtra("union", union)
		.putExtra("speed", String.valueOf(speed))
		.putExtra("ships", ships);
	
		startActivity(fleet);  
		finish();
	}
		
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	        goBack();
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * Function with View parameter is needed for onClick button
	 * @param view
	 */
	public void goBack(View view){
		goBack();
	}
	
	/**
	 * Sends the user back one step
	 */
	public void goBack(){
		if("fleet1".equals(task)) {
			finish();
		} else if("fleet2".equals(task)) {
			startTab("fleet1");
		} else if("fleet3".equals(task)) {
			startTab("fleet2");
		}
	}	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
   	 	getWindow().requestFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
				
		if(getIntent().hasExtra("tab")) {
			task = getIntent().getExtras().getString("tab");
		} else {
			task = "fleet1";
		}
		
		if("fleet1".equals(task)) {
			setContentView(R.layout.activity_tab_fleet1);
		} else if("fleet2".equals(task)) {
			setContentView(R.layout.activity_tab_fleet2);			
		} else if("fleet3".equals(task)) {
			setContentView(R.layout.activity_tab_fleet3);			
		}
		
		registerForContextMenu(getListView());

		final ProgressDialog loaderDialog = new ProgressDialog(FleetView.this);					
		loaderDialog.setMessage(getString(R.string.loading));
		
		Thread t = new Thread(new Runnable() {			
			@Override
			public void run() {

				if(getIntent().hasExtra("galaxy")) {
					targetGalaxy = getIntent().getExtras().getString("galaxy");
					targetSystem = getIntent().getExtras().getString("system");
					targetPosition = getIntent().getExtras().getString("position");
					planetType = getIntent().getExtras().getString("planetType");
				} else {
					Planet p = MainTabActivity.game.getCurrentPlanet();
					targetGalaxy = String.valueOf(p.getGalaxy());
					targetSystem = String.valueOf(p.getSystem());
					targetPosition = String.valueOf(p.getPosition());
					planetType = "1";
				}
				if(getIntent().hasExtra("mission")) {
					mission = getIntent().getExtras().getString("mission");
				} else {
					mission = MISSION_NONE;
				}
				
				if("fleet1".equals(task)) {
					onCreateFleet1();
				} else if("fleet2".equals(task)) {
					onCreateFleet2();
				} else { //fleet3
					onCreateFleet3();
				}
				
				runOnUiThread(new Runnable() {					
					@Override
					public void run() {
						setListAdapter(adapter);	
						setProgressBarIndeterminateVisibility(false);
						loaderDialog.cancel();
					}
				});			
			}
		});
		setProgressBarIndeterminateVisibility(true);
		t.start();
		loaderDialog.show();
	}
	
	private void onCreateFleet3() {
		
		final Document document = sendShips2();

		final Spinner missionSpinner = (Spinner) findViewById(R.id.mission);
        final ArrayAdapter<CharSequence> missionAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
        missionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        runOnUiThread(new Runnable() {			
			@Override
			public void run() {
				missionSpinner.setAdapter(missionAdapter);
				
				//add default mission
				if(MISSION_NONE.equals(mission)) {
	        		missionAdapter.add("-");
	        		missions.add(MISSION_NONE);
				}
				
				//add available missions
		        for(Element li : document.select("#missions > li")) {
		        	if("on".equals(li.attr("class"))) {
		        		missionAdapter.add(li.select("span").html());
		        		missions.add(li.attr("id").substring(6));
		        		//onclick -> updateHoldingOrExpTime(); updateVariables();
		        	}
		        }

		        //check for selected mission
		        for (int i = 0; i < missions.size(); i++) {
	        		if(mission.equals(missions.get(i))) {
	        			missionSpinner.setSelection(i);
	        		}
		        }
			}
		});
        missionSpinner.setOnItemSelectedListener(new ListView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int index, long arg3) {
				mission = missions.get(index);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
        });
		
        String maxresources = document.select("#maxresources").text();
        final String target = document.select("#roundup > ul > li").get(0).select("span").text();
        final String duration = document.select("#duration").text();
        final String arrivalTime = document.select("#arrivalTime").text();
        final String returnTime = document.select("#returnTime").text();
        final String consumption = document.select("#consumption > span").text();
        
        /*Log.i("onCreateFleet3", "target=" + target 
        		+ ", maxresources=" + maxresources
        		+ ", duration=" + duration
        		+ ", arrivalTime=" + arrivalTime
        		+ ", returnTime=" + returnTime
        		+ ", consumption=" + consumption);*/

        maxresources = maxresources.replace(".", "");
        final int cargoSpace = Integer.parseInt(maxresources);
        remainingresources = cargoSpace;
        
        final TextView maxresourcesView = (TextView) findViewById(R.id.fleet_maxresources);
        final TextView remainingresourcesView = (TextView) findViewById(R.id.fleet_remainingresources);
        final TextView durationView = (TextView) findViewById(R.id.fleet_duration);        
        final TextView targetView = (TextView) findViewById(R.id.fleet_target);
        final TextView consumptionView = (TextView) findViewById(R.id.fleet_consumption);
        final TextView arrivalView = (TextView) findViewById(R.id.fleet_arrival);
        final TextView returnView = (TextView) findViewById(R.id.fleet_return);
        
        final TextView metalView = (TextView) findViewById(R.id.metal_amount);
        final TextView crystalView = (TextView) findViewById(R.id.crystal_amount);
        final TextView deuteriumView = (TextView) findViewById(R.id.deuterium_amount);
        
        final SeekBar metalSeekbar = (SeekBar) findViewById(R.id.metal_seekbar);
        final SeekBar crystalSeekbar = (SeekBar) findViewById(R.id.crystal_seekbar);
        final SeekBar deuteriumSeekbar = (SeekBar) findViewById(R.id.deuterium_seekbar);

        final Planet p = MainTabActivity.game.getCurrentPlanet();
        
        metalSeekbar.setMax(Math.min(p.getMetal(), remainingresources));
        metalSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			public void onStopTrackingTouch(SeekBar seekBar) {
				metal = seekBar.getProgress();
				remainingresources = remainingresources + progress - metal;
				remainingresourcesView.setText(String.valueOf(remainingresources));
				
		        crystalSeekbar.setMax(Math.max(crystal, Math.min(p.getCrystal(), remainingresources)));
		        crystalSeekbar.refreshDrawableState();
		        
		        deuteriumSeekbar.setMax(Math.max(deuterium, Math.min(p.getDeuterium(), remainingresources)));
		        deuteriumSeekbar.refreshDrawableState();
			}
			
			public void onStartTrackingTouch(SeekBar seekBar) {
				progress = seekBar.getProgress();
				Log.i("start", "progress:" + String.valueOf(progress));
			}
			
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				metalView.setText(String.valueOf(progress));
			}
		});        

        crystalSeekbar.setMax(Math.min(p.getCrystal(), remainingresources));
        crystalSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			public void onStopTrackingTouch(SeekBar seekBar) {
				crystal = seekBar.getProgress();
				remainingresources = remainingresources + progress - crystal;
				remainingresourcesView.setText(String.valueOf(remainingresources));
				
		        metalSeekbar.setMax(Math.max(metal, Math.min(p.getMetal(), remainingresources)));
		        metalSeekbar.refreshDrawableState();
		        
		        deuteriumSeekbar.setMax(Math.max(deuterium, Math.min(p.getDeuterium(), remainingresources)));
		        deuteriumSeekbar.refreshDrawableState();
			}
			
			public void onStartTrackingTouch(SeekBar seekBar) {
				progress = seekBar.getProgress();
			}
			
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				crystalView.setText(String.valueOf(progress));
			}
		});

        deuteriumSeekbar.setMax(Math.min(p.getDeuterium(), remainingresources));
        deuteriumSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			public void onStopTrackingTouch(SeekBar seekBar) {
				deuterium = seekBar.getProgress();
				remainingresources = remainingresources + progress - deuterium;
				remainingresourcesView.setText(String.valueOf(remainingresources));
				
		        metalSeekbar.setMax(Math.max(metal, Math.min(p.getMetal(), remainingresources)));
		        metalSeekbar.refreshDrawableState();
		        
		        crystalSeekbar.setMax(Math.max(crystal, Math.min(p.getCrystal(), remainingresources)));
		        crystalSeekbar.refreshDrawableState();
			}
			
			public void onStartTrackingTouch(SeekBar seekBar) {
				progress = seekBar.getProgress();
			}
			
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				deuteriumView.setText(String.valueOf(progress));
			}
		});
        
        runOnUiThread(new Runnable() {			
			@Override
			public void run() {
				maxresourcesView.setText(String.valueOf(cargoSpace));
				remainingresourcesView.setText(String.valueOf(cargoSpace));
				durationView.setText(duration);
				arrivalView.setText(arrivalTime);
				returnView.setText(returnTime);
				consumptionView.setText(consumption);
				targetView.setText(target);
			}
		});
		
		final Button next = (Button) this.findViewById(R.id.fleet3_send);
		next.setOnClickListener(new Button.OnClickListener() {				
			@Override
			public void onClick(View v) {
				sendShips3();
			}
		});
	}
	
	private void onCreateFleet2() {
		
		final Document document = sendShips1();
		updateWidgetsFromVariables();
        
		String script = document.select("script").not("script[src]").html();		
		maxSpeed = Integer.parseInt(Tools.between(script, "maxSpeed = ", ";"));
		speedFactor = Integer.parseInt(Tools.between(script, "speedFactor = ", ";"));
		storageCapacity = Integer.parseInt(Tools.between(script, "storageCapacity = ", ";"));
		
		for(int i = 0; i < 13; i++) {
			if(script.indexOf("shipIDs[" + i + "]") > -1) {
				shipIDs.add(Tools.between(script, "shipIDs[" + i + "] = ", ";"));
				completeConsumptions.add(Integer.parseInt(Tools.between(script, "completeConsumptions[" + i + "] = ", ";")));
				speeds.add(Integer.parseInt(Tools.between(script, "speeds[" + i + "] = ", ";")));

			} else {
				break;
			}
		}

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
        final ArrayAdapter<CharSequence> acsAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
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
		final Button next = (Button) this.findViewById(R.id.fleet1_next);
		
		String body = MainTabActivity.game.get("page=fleet1");
		final Document document = Jsoup.parse(body);
		
		ArrayList<Ship> shiplist = new ArrayList<Ship>();
		for(int i = 0; i < ulKey.length; i++){
			ArrayList<Ship> o = Tools.parseFleet(document, ulKey[i], FleetView.this);
			shiplist.addAll(o);
		}
		adapter = new FleetAdapter(FleetView.this, R.layout.adapter_item_fleet, shiplist);
		
		//We have ships form a previous call
		if(getIntent().getExtras().containsKey("ships")){
			ships = (HashMap<String, String>) getIntent().getExtras().getSerializable("ships");
			for(int i = 0; i < adapter.getCount(); i++){
				if(ships.containsKey("am" + adapter.getItem(i).getId())){
					try{
						adapter.getItem(i).setUsed(Integer.valueOf(ships.get("am" + adapter.getItem(i).getId())));
					}catch(Exception e){
						adapter.getItem(i).setUsed(0);
					}
				}
			}
			runOnUiThread(new Runnable() {					
				@Override
				public void run() {
					next.setEnabled(true);
				}
			});
		}		
		
		next.setOnClickListener(new Button.OnClickListener() {				
			@Override
			public void onClick(View v) {
				Intent fleet2 = new Intent(FleetView.this, FleetView.class)
					.putExtra("tab", "fleet2");

				/*HashMap<String, String>*/ ships = new HashMap<String, String>();
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
				//startActivity(fleet2);   
				startTab("fleet2");
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
	private Document sendShips1() {

		List<NameValuePair> postData = new ArrayList<NameValuePair>();
        postData.add(new BasicNameValuePair("galaxy", targetGalaxy));
        postData.add(new BasicNameValuePair("system", targetSystem));
        postData.add(new BasicNameValuePair("position", targetPosition));
        postData.add(new BasicNameValuePair("type", planetType));
		postData.add(new BasicNameValuePair("mission", mission));
        postData.add(new BasicNameValuePair("speed", "10"));
        
        //HashMap<String, String> ships = (HashMap<String, String>) getIntent().getExtras().getSerializable("ships");
        ships = (HashMap<String, String>) getIntent().getExtras().getSerializable("ships");
        for (String name : ships.keySet()) {
        	String value = ships.get(name);
	        postData.add(new BasicNameValuePair(name, value));
		}
        String html = MainTabActivity.game.execute("page=fleet2", postData);
		
        return  Jsoup.parse(html);
	}	


	// 2 -> 3
	//index.php?page=fleet3&session=912bb66e8f11 POST	
	//$('form[name=details]').serialize()
	//"type=1&mission=0&union=0&am202=2&galaxy=3&system=293&position=8&speed=10"
	private Document sendShips2() {

		List<NameValuePair> postData = new ArrayList<NameValuePair>();
        postData.add(new BasicNameValuePair("galaxy", targetGalaxy));
        postData.add(new BasicNameValuePair("system", targetSystem));
        postData.add(new BasicNameValuePair("position", targetPosition));
        postData.add(new BasicNameValuePair("type", planetType));
		postData.add(new BasicNameValuePair("mission", mission));

		String union = (String) getIntent().getExtras().getSerializable("union");
        postData.add(new BasicNameValuePair("union", union));

		/*String*/ speed = Integer.valueOf((String) getIntent().getExtras().getSerializable("speed"));
        postData.add(new BasicNameValuePair("speed", String.valueOf(speed)));
        
        //HashMap<String, String> ships = (HashMap<String, String>) getIntent().getExtras().getSerializable("ships");
        ships = (HashMap<String, String>) getIntent().getExtras().getSerializable("ships");
        for (String name : ships.keySet()) {
        	String value = ships.get(name);
        	if(!"".equals(value)) {
        		postData.add(new BasicNameValuePair(name, value));
        	}
		}
        String html = MainTabActivity.game.execute("page=fleet3", postData);
		
        return  Jsoup.parse(html);
	}	


	// 3 -> 4
	//index.php?page=movement&session=912bb66e8f11
	//$('form[name=sendForm]').serialize()
	//"holdingtime=1&expeditiontime=1&galaxy=3&system=293&position=8&type=1&mission=4&union2=0&holdingOrExpTime=0&speed=10&am202=2&metal=0&crystal=0&deuterium=0"
	private void sendShips3() {

		List<NameValuePair> postData = new ArrayList<NameValuePair>();
        postData.add(new BasicNameValuePair("galaxy", targetGalaxy));
        postData.add(new BasicNameValuePair("system", targetSystem));
        postData.add(new BasicNameValuePair("position", targetPosition));
        postData.add(new BasicNameValuePair("type", planetType));
		postData.add(new BasicNameValuePair("mission", mission));
		postData.add(new BasicNameValuePair("holdingtime", "1")); //TODO
		postData.add(new BasicNameValuePair("expeditiontime", "1")); //TODO
		postData.add(new BasicNameValuePair("holdingOrExpTime", "0")); //TODO
		postData.add(new BasicNameValuePair("metal", String.valueOf(metal)));
		postData.add(new BasicNameValuePair("crystal", String.valueOf(crystal)));
		postData.add(new BasicNameValuePair("deuterium", String.valueOf(deuterium)));

		String union = (String) getIntent().getExtras().getSerializable("union");
        postData.add(new BasicNameValuePair("union2", union));

		String speed = (String) getIntent().getExtras().getSerializable("speed");
        postData.add(new BasicNameValuePair("speed", speed));
        
        //HashMap<String, String> ships = (HashMap<String, String>) getIntent().getExtras().getSerializable("ships");
        ships = (HashMap<String, String>) getIntent().getExtras().getSerializable("ships");
        for (String name : ships.keySet()) {
        	String value = ships.get(name);
        	if(!"".equals(value)) {
        		postData.add(new BasicNameValuePair(name, value));
        	}
		}
        String html = MainTabActivity.game.execute("page=movement", postData);
		
        startActivity(new Intent(this, MovementView.class));
        finish();
        // TODO close all fleet activities
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
		        /*int*/ speed = Integer.parseInt(getResources().getStringArray(R.array.speed)[speedSpinner.getSelectedItemPosition()]);
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

        //final HashMap<String, String> ships = (HashMap<String, String>) getIntent().getExtras().getSerializable("ships");
        ships = (HashMap<String, String>) getIntent().getExtras().getSerializable("ships");
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
			        Spinner speedSpinner = (Spinner) findViewById(R.id.speed);
			        /*int*/ speed = Integer.parseInt(getResources().getStringArray(R.array.speed)[speedSpinner.getSelectedItemPosition()]);
			        speed = speed / 10; //option values are 10,9,8... descriptions are 100,90,80...
					
//					Intent fleet3 = new Intent(FleetView.this, FleetView.class)
//						.putExtra("tab", "fleet3")
//						.putExtra("galaxy", targetGalaxy)
//						.putExtra("system", targetSystem)
//						.putExtra("position", targetPosition)
//						.putExtra("planetType", planetType)
//						.putExtra("mission", mission)
//						.putExtra("union", union)
//						.putExtra("speed", String.valueOf(speed))
//						.putExtra("ships", ships);
//					
//		            startActivity(fleet3);  
			        startTab("fleet3");
				}
			}
		});
	}
	
}
