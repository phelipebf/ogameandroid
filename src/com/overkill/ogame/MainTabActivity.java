package com.overkill.ogame;

import java.io.File;
import java.lang.reflect.Method;

import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.mobyfactory.uiwidgets.RadioStateDrawable;
import com.mobyfactory.uiwidgets.ScrollableTabActivity;
import com.overkill.gui.HtmlSelect;
import com.overkill.ogame.game.GameClient;
import com.overkill.ogame.game.NotificationSystem;
import com.overkill.ogame.game.Planet;
import com.overkill.ogame.game.Tools;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainTabActivity extends ScrollableTabActivity{
    private static final String TAG = "ogame";
    
    private int tab_shade_off; 
    private int tab_shade_on; 
    
	//Game instance
	public static GameClient game = null;
	
	//System for fleet and other info
	public static NotificationSystem notify = null;
	
	public boolean ready = false;
	
    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        ((TextView) findViewById(android.R.id.title)).setText(title);
    }
    
    public void setInfo(CharSequence title) {
    	((TextView) findViewById(R.id.subtitle)).setText(title);
    }
    
    public void setIcon(int icon) {
    	((ImageButton) findViewById(R.id.home_button)).setImageResource(icon);
    }
	
    @Override
    public void onCreate(Bundle savedInstanceState) {         	 
    	Log.i(TAG, "onCreate");   	
    	requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
    	super.onCreate(savedInstanceState);   
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.system_title_planet);     
        Tools.filesDir = getFilesDir().getAbsolutePath();
                        
        final SharedPreferences preferences = getSharedPreferences(TAG, 0);
        
        if(getIntent().hasExtra("username")){
	        final String username = getIntent().getExtras().getString("username");    
			final String password = getIntent().getExtras().getString("password");    		
			
			final int domain_i = getIntent().getExtras().getInt("country");
			final String domain =  getResources().getStringArray(R.array.countries)[domain_i];
			
			final int uni_i = getIntent().getExtras().getInt("universe");
			HtmlSelect select = new HtmlSelect(new File(getFilesDir().getAbsolutePath() + "/" + domain));
			final String universe = select.getValue(uni_i);
			
			final boolean save = getIntent().getExtras().getBoolean("save");
	        
			final ProgressDialog loader = new ProgressDialog(MainTabActivity.this);				
			loader.setMessage("Logging in...");
			loader.setCancelable(false);
	    	Thread t_login = new Thread(new Runnable() {					
				@Override
				public void run() {	
					try{
						Tools.trackLogin(domain, universe, preferences.getBoolean("show_ads", true));		
						
						game = new GameClient(MainTabActivity.this);
						final int state = game.login(universe, username, password);
						
				       	if(state == GameClient.LOGIN_WRONG_DATA || state == GameClient.LOGIN_CONNECTION_TIMEOUT || state == GameClient.LOGIN_UNKNOWN){
				       		//hide loader and tell user
				       		loader.cancel();
				       		runOnUiThread(new Runnable() {									
								@Override
								public void run() {
									String toastText = "";
									switch (state) {
									case GameClient.LOGIN_WRONG_DATA:
										toastText = getString(R.string.error_login);
										break;
									case GameClient.LOGIN_CONNECTION_TIMEOUT:
										toastText = getString(R.string.error_connection_timeout);
										break;
									case GameClient.LOGIN_UNKNOWN:
										toastText = getString(R.string.error_unknown);
										break;
									default:
										break;
									}
									Toast.makeText(MainTabActivity.this, toastText, Toast.LENGTH_SHORT).show();
									Intent intent = new Intent(MainTabActivity.this, LoginView.class);
									startActivity(intent);								
									finish();
								}
							});
				       		
				       	}else{ //LOGIN OK
				       		if(state == GameClient.LOGIN_SERVER_VERSION){
				       			runOnUiThread(new Runnable() {									
									@Override
									public void run() {
										Toast.makeText(MainTabActivity.this, "Warning\nThe server version may not be fully supported.\nPlease check for an update.", Toast.LENGTH_SHORT).show();
									}
								});
				       		}
					       	//should we save the login data
					       	if(save){
								SharedPreferences.Editor editor = preferences.edit();
								editor.putString("username", username);
								editor.putString("password", password);
								editor.putInt("universum", uni_i);
								editor.putInt("domain", domain_i);
								editor.commit();
							}else{
								//clean login preferences
								SharedPreferences.Editor editor = preferences.edit();
								editor.putString("username", "");
								editor.putString("password", "");
								editor.putInt("universum", 0);
								editor.putInt("domain", 0);
								editor.commit();
							}
					       	//run init function to build gui
					       	runOnUiThread(new Runnable() {							
								@Override
								public void run() {
									ready = true;
									initUI();								
								}
							});
					       	//hide loader
							loader.cancel();
				       	}
			            
					}catch(Exception ex){
			        	ex.printStackTrace();
			        }
				}
			});
			t_login.start();
			loader.show();
        }else{ //no login data as extra, only possible when opened from notification
        	ready = true;
			initUI();		
        }
    	
    }
    
    public void initUI(){    
    	SharedPreferences preferences = getSharedPreferences(TAG, Context.MODE_PRIVATE);
    	
    	tab_shade_on = Integer.valueOf(preferences.getString("tab_shade_on", Integer.toString(RadioStateDrawable.SHADE_GRAY)));
        tab_shade_off = Integer.valueOf(preferences.getString("tab_shade_off", Integer.toString(RadioStateDrawable.SHADE_BLUE)));
    	
    	if(preferences.getBoolean("show_ads", false)){
    		Log.i(TAG, "Show Ads!");
	    	//Load Ads
	    	AdRequest adRequest = new AdRequest();
	    	AdView adView = (AdView)this.findViewById(R.id.adView);
	        adView.loadAd(adRequest);
    	}
        
    	//create NotificationSystem and show it
    	if(preferences.getBoolean("fleetsystem_global", true)){
	       	notify = new NotificationSystem(MainTabActivity.this, MainTabActivity.game, preferences.getString("fleetsystem_sound", null));

	       	int intervall = 300; // Default interval (300sec -> 5min)
	       	try{
	       		intervall = Integer.valueOf(preferences.getString("fleetsystem_intervall", "300"));
	       	}catch(Exception e){
	       		// will use default
	       	}
	       	
	       	notify.setDelay(preferences.getInt("fleetsystem_reload_rate", intervall));
	       	notify.config(
	       		preferences.getBoolean("fleetsystem_alarm_hostile", false),
	       		preferences.getBoolean("fleetsystem_alarm_neutral", false),
	       		preferences.getBoolean("fleetsystem_alarm_friendly", false),
	       		preferences.getBoolean("fleetsystem_alarm_messages", false)
	       	);
	       	notify.init();
    	}
    	    	
    	//get current planet
        Planet p1 = game.getCurrentPlanet();
        p1.parse(game.get("page=fetchResources&ajax=1"));
        setTitle(p1.getName() + " " + p1.getCoordinates());
        setInfo(p1.getResources());
        setIcon(p1.getIcon());
                
        this.addTab(getString(R.string.tab_overview), R.drawable.navi_ikon_overview_a, tab_shade_off, tab_shade_on,
        	new Intent(this, Overview.class));
        
        this.addTab(getString(R.string.tab_resources), R.drawable.navi_ikon_resources_a, tab_shade_off, tab_shade_on,
    		new Intent(this, ObjectListActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    			.putExtra("pageKey", "resources")
    			.putExtra("ulKey", new String[]{"building","storage"})
    			.putExtra("liKey", new String[]{"supply","supply"})
    		);
        
        this.addTab(getString(R.string.tab_station), R.drawable.navi_ikon_station_a, tab_shade_off, tab_shade_on,
    		new Intent(this, ObjectListActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    			.putExtra("pageKey", "station")
    			.putExtra("ulKey", new String[]{"stationbuilding"})
    			.putExtra("liKey", new String[]{"station"})
    		);
        
        this.addTab(getString(R.string.tab_research), R.drawable.navi_ikon_research_a, tab_shade_off, tab_shade_on,
    		new Intent(this, ObjectListActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    			.putExtra("pageKey", "research")
    			.putExtra("ulKey", new String[]{"base", "base2", "base3", "base4"})
    			.putExtra("liKey", new String[]{"research", "research", "research", "research"})
    		);
        
        this.addTab(getString(R.string.tab_shipyard), R.drawable.navi_ikon_shipyard_a, tab_shade_off, tab_shade_on,
    		new Intent(this, ObjectListActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    			.putExtra("pageKey", "shipyard")
    			.putExtra("ulKey", new String[]{"military", "civil"})
    			.putExtra("liKey", new String[]{"military", "civil"})
    		); 
        
        this.addTab(getString(R.string.tab_defense), R.drawable.navi_ikon_defense_a, tab_shade_off, tab_shade_on, 
    		new Intent(this, ObjectListActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    			.putExtra("pageKey", "defense")
    			.putExtra("ulKey", new String[]{"defensebuilding"})
    			.putExtra("liKey", new String[]{"defense"})
    		);
        
        this.addTab(getString(R.string.menu_resources), R.drawable.navi_ikon_resources_a, tab_shade_off, tab_shade_on, 
    		new Intent(this, ResourceSettingsView.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        
        disableScrollbarFading((HorizontalScrollView)findViewById(R.id.bottomBar));
        commit();
        
        ((ImageButton)findViewById(R.id.home_button)).setOnClickListener(new ImageButton.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent serverIntent = new Intent(MainTabActivity.this, PlanetListActivity.class);
	            startActivity(serverIntent);
			}
		});
        
        ((ImageButton)findViewById(R.id.home_button)).setOnLongClickListener(new ImageButton.OnLongClickListener() {			
			@Override
			public boolean onLongClick(View v) {
				final CharSequence[] items = {getString(R.string.planet_rename_title), getString(R.string.planet_abandon_title)};
				AlertDialog.Builder builder = new AlertDialog.Builder(MainTabActivity.this);
				builder.setTitle("Select Action");				
				builder.setItems(items, new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int item) {
				    	switch (item) {
						case 0:
							showRenamePlanetDialog();
							break;
						case 1:
							showAbandonPlanetDialog();
							break;
						}
				    }
				});
				AlertDialog alert = builder.create();
				alert.show();
				return true;
			}
		});
        
        getReloadButton().setOnClickListener(new ImageButton.OnClickListener() {			
			@Override
			public void onClick(View v) {
				final Planet p1 = MainTabActivity.game.getCurrentPlanet();
				setInfo(p1.getResources());
			}
		});
        
        getReloadButton().setOnLongClickListener(new ImageButton.OnLongClickListener() {			
			@Override
			public boolean onLongClick(View v) {
				reloadTitleData(false);
				reloadNotificationData();
				return true;
			}
		});
    }
    
    private ImageButton getReloadButton() {
    	return ((ImageButton)findViewById(R.id.reload_button));
    }
    
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if(notify != null)
			notify.destroy();
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        	case R.id.fleet: startActivity(new Intent(this, MovementView.class)); return true;
        	case R.id.messages: startActivity(new Intent(this, MessageListView.class)); return true;
        	case R.id.galaxy: startActivity(new Intent(this, GalaxyView.class)); return true;
//        	case R.id.resourceSettings: startActivity(new Intent(this, ResourceSettingsView.class)); return true;
        }
        return false;
    }
    
    @Override
    public void onResume() {
    	Log.i(TAG, "onResume");
    	super.onResume();
    	if(ready){
    		reloadTitleData(false);
    	}    		
    }
    
    /**
     * Refreshes the title data
     * @param forceReload Refresh all Planet data (needed for rename)
     */
	public void reloadTitleData(final boolean forceReload){
		final ImageButton syncIndicator = getReloadButton();
		
		Thread t = new Thread(new Runnable() {			
			@Override
			public void run() {
				if(forceReload)
					MainTabActivity.game.loadPlanets();
				
				final Planet p1 = MainTabActivity.game.getCurrentPlanet();	
				p1.parse(MainTabActivity.game.get("page=fetchResources&ajax=1"));
				runOnUiThread(new Runnable() {
		            public void run() {		
		            	if(p1.isMoon())
		            		setTitle(p1.getName() + " " + p1.getCoordinates() + " " + getString(R.string.moon));
		            	else
					        setTitle(p1.getName() + " " + p1.getCoordinates());
		            	
				        setInfo(p1.getResources());
				        setIcon(p1.getIcon());

				        syncIndicator.setImageResource(android.R.drawable.ic_menu_rotate);							
		            }
				});			
			}
		});
		runOnUiThread(new Runnable() {
            public void run() {	
            	syncIndicator.setImageResource(android.R.drawable.ic_menu_recent_history);		
            }});
		t.start();
	}     
	
	/**
	 * Refresh notification data
	 */
	public void reloadNotificationData(){
		if(notify != null){
			final ImageButton syncIndicator = getReloadButton();
			
			Thread t = new Thread(new Runnable() {			
				@Override
				public void run() {
					notify.update();
					runOnUiThread(new Runnable() {
			            public void run() {		
					        syncIndicator.setImageResource(android.R.drawable.ic_menu_rotate);							
			            }
					});			
				}
			});
			runOnUiThread(new Runnable() {
	            public void run() {	
	            	syncIndicator.setImageResource(android.R.drawable.ic_menu_recent_history);		
	            }});
			t.start();
		}
	}
	
	public static void disableScrollbarFading(View view) {
	    try {
	        Method setScrollbarFadingEnabled = View.class.getDeclaredMethod("setScrollbarFadingEnabled", boolean.class);
	        setScrollbarFadingEnabled.setAccessible(true);
	        setScrollbarFadingEnabled.invoke(view, false);
	    } catch (Exception e) {
	        // OK, API level < 5
	    }
	}
	
	public void showRenamePlanetDialog(){
		AlertDialog.Builder dialog = new AlertDialog.Builder(MainTabActivity.this);
		final EditText input = new EditText(MainTabActivity.this);
		input.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		dialog.setTitle(R.string.planet_rename_title);
		dialog.setMessage(R.string.planet_rename_text);
		dialog.setView(input);
		dialog.setPositiveButton(android.R.string.ok, new OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String newPlanetName = input.getEditableText().toString();
				boolean result = MainTabActivity.game.renamePlanet(newPlanetName);
				if(result){
					Toast.makeText(MainTabActivity.this, "Success", Toast.LENGTH_SHORT).show();
					reloadTitleData(true);
					dialog.cancel();
				}else{
					Toast.makeText(MainTabActivity.this, "Failed", Toast.LENGTH_SHORT).show();
					dialog.cancel();
				}
			}
		});
		dialog.setNegativeButton(android.R.string.cancel, new OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();				
			}
		});
		dialog.show();
	}
	
	public void showAbandonPlanetDialog(){
		AlertDialog.Builder dialog = new AlertDialog.Builder(MainTabActivity.this);
		dialog.setTitle(R.string.planet_abandon_title);
		dialog.setMessage(getString(R.string.planet_abandon_text, MainTabActivity.game.getCurrentPlanet().getName()));
		dialog.setPositiveButton(android.R.string.ok, new OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				showAbandonPlanetValidationDialog();
			}
		});
		dialog.setNegativeButton(android.R.string.cancel, new OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();				
			}
		});
		dialog.show();
	}
	
	public void showAbandonPlanetValidationDialog(){
		AlertDialog.Builder dialog = new AlertDialog.Builder(MainTabActivity.this);
		final EditText input = new EditText(MainTabActivity.this);
		input.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		dialog.setTitle(R.string.planet_abandon_title);
		dialog.setMessage(R.string.planet_abandon_question);
		dialog.setView(input);
		dialog.setPositiveButton(android.R.string.ok, new OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String password = input.getEditableText().toString();
				boolean result = MainTabActivity.game.abandonPlanet(password);
				if(result){
					Toast.makeText(MainTabActivity.this, "Success", Toast.LENGTH_SHORT).show();
					dialog.cancel();
				}else{
					Toast.makeText(MainTabActivity.this, "Failed", Toast.LENGTH_SHORT).show();
					dialog.cancel();
				}
			}
		});
		dialog.setNegativeButton(android.R.string.cancel, new OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();				
			}
		});
		dialog.show();
	}
}