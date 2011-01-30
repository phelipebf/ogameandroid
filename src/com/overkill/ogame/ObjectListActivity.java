package com.overkill.ogame;

import java.util.ArrayList;

import com.overkill.ogame.game.BuildObject;
import com.overkill.ogame.game.BuildObjectAdapter;
import com.overkill.ogame.game.Planet;
import com.overkill.ogame.game.Tools;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ObjectListActivity extends ListActivity {
	String token = "";
	BuildObjectAdapter adapter;
	private Handler h_countdown = new Handler();
	private Runnable t_countdown;
	
	/************* Daten werde als Extra übergeben *************/
	String lastpageKey = "";
	String pageKey;
	String[] ulKey;
	String[] liKey;
	int ulCount = 0;
	String countdownKey;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tab_resources);	
		registerForContextMenu(getListView());
		pageKey = getIntent().getExtras().getString("pageKey");    
		ulKey = (getIntent().getExtras().getStringArray("ulKey")); 
		liKey = getIntent().getExtras().getStringArray("liKey");  
		//loadData(); 					
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		loadData();	
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	  super.onCreateContextMenu(menu, v, menuInfo);
	  AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
	  menu.add(0, 2, 0, R.string.more);
	  BuildObject b = (BuildObject)getListAdapter().getItem(info.position);
	  if(b.getTimeLeft() > 0)
		  menu.add(0, 1, 0, android.R.string.cancel);
	}
	
	public boolean onContextItemSelected(MenuItem item) {
		return false;
		  /* we still need it?
		  
		  AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		  final BuildObject b = (BuildObject)getListAdapter().getItem(info.position);
		  switch (item.getItemId()) {
		  case 1:
			Toast.makeText(this, "Unable to do that", Toast.LENGTH_SHORT).show();
		    return true;
		  case 2:
			    final AlertDialog.Builder alert = new AlertDialog.Builder(ObjectListActivity.this);
		    	alert.setTitle(R.string.more);
		    	alert.setMessage(R.string.loading);
		    	alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			    	public void onClick(DialogInterface dialog, int whichButton) {	
			    		dialog.cancel();
			    	  }
			    	});
		    	Thread t = new Thread(new Runnable() {					
					@Override
					public void run() {
						final String msg = b.getBuildTime(MainTabActivity.game);	
						runOnUiThread(new Runnable() {							
							@Override
							public void run() {
								alert.setMessage(msg);	
						    	alert.show();							
							}
						});						
					}
				});
		    	t.start();
			return true;
		  default:
		    return super.onContextItemSelected(item);
		  }*/
		}
	
	@Override
	protected void onListItemClick(ListView l, View v, final int position, long id) {
		super.onListItemClick(l, v, position, id);	
		final BuildObject b = (BuildObject)getListAdapter().getItem(position);
	    final AlertDialog.Builder dialog = new AlertDialog.Builder(ObjectListActivity.this);
		
		if("disabled".equals(b.getStatus())){ //not enough resources
		    dialog.setTitle(R.string.not_available);
		    dialog.setMessage(getString(R.string.buildable_in, Tools.sec2str(b.getBuildableIn(MainTabActivity.game.getCurrentPlanet()))));
		    dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
		    	public void onClick(DialogInterface dialog, int whichButton) {	
		    			dialog.cancel();
		    		}
		    	});
		    dialog.show();
		} else if("off".equals(b.getStatus())){ //not buildable yet
			final ProgressDialog loaderDialog = new ProgressDialog(this);					
			loaderDialog.setMessage(getString(R.string.loading));
			
		    dialog.setTitle(R.string.requirements);
		    dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
		    	public void onClick(DialogInterface dialog, int whichButton) {	
		    			dialog.cancel();
		    		}
		    	});
	    	Thread t = new Thread(new Runnable() {					
				@Override
				public void run() {
					final String msg = b.getTechnologyNeeded(MainTabActivity.game);	
					runOnUiThread(new Runnable() {							
						@Override
						public void run() {
							loaderDialog.cancel();
							dialog.setMessage(msg);	
							dialog.show();
						}
					});						
				}
			});
	    	loaderDialog.show();
	    	t.start();
		} else if(b.needsValue()){ //Object that can be build more than once
			dialog.setTitle(R.string.new_command);
			dialog.setMessage(getString(R.string.enter_amount));
			
	    	//Build Input field
	    	LinearLayout layout = new LinearLayout(this);
	    	layout.setOrientation(LinearLayout.VERTICAL);
	    	
	    	final EditText input = new EditText(this);
	    	input.setInputType(InputType.TYPE_CLASS_NUMBER);
	    	input.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
	    	layout.addView(input);
	    	
	    	final Button btn_all = new Button(this);
	    	btn_all.setText(String.valueOf(b.getMax(MainTabActivity.game.getCurrentPlanet())));
	    	btn_all.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
	    	btn_all.setOnClickListener(new Button.OnClickListener() {				
				@Override
				public void onClick(View v) {
					input.setText(btn_all.getText());
				}
			});
	    	layout.addView(btn_all);
	    	
	    	dialog.setView(layout);
	    	
	    	dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
		    	public void onClick(DialogInterface dialog, int whichButton) {
		    		String value = input.getText().toString();	    	
		    		askBuildWithTime(b, Integer.valueOf(value));
		    	}
		    });
	    	dialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
		    	public void onClick(DialogInterface dialog, int whichButton) {
		    		dialog.cancel();
		    	}
		    });
	    	dialog.show();
		} else {	//Object can only be built once	
			askBuildWithTime(b, 1);
		}
	}
	
	//get build time
	private void askBuildWithTime(final BuildObject b, final int amount) {

		final ProgressDialog loaderDialog = new ProgressDialog(this);					
		loaderDialog.setMessage(getString(R.string.loading));
		
    	Thread t = new Thread(new Runnable() {					
			@Override
			public void run() {
				//Build question for user
				String ask = "";
				if(amount > 1) {
					ask = getString(R.string.ask_with_count, amount, b.getName());
				} else {
					ask = getString(R.string.ask_no_count, b.getName());
				}
				ask += "\n" + getString(R.string.building_time, b.getBuildTime(MainTabActivity.game));
				final String msg = ask;
				runOnUiThread(new Runnable() {							
					@Override
					public void run() {
						loaderDialog.cancel();
						askBuild(b, amount, msg);
					}
				});						
			}
		});
    	loaderDialog.show();
    	t.start();		
	}
	
	//show a question to the user and sends the build request if user clicks yes
	private void askBuild(final BuildObject b, final int amount, final String msg){
		
		AlertDialog.Builder alert = new AlertDialog.Builder(ObjectListActivity.this);
    	alert.setTitle(R.string.new_command);
    	alert.setMessage(msg);
    	alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
	    	public void onClick(DialogInterface dialog, int whichButton) {
	    		final ProgressDialog loaderDialog = new ProgressDialog(ObjectListActivity.this);	
	    		loaderDialog.setMessage(getString(R.string.send_command));
	    		loaderDialog.show();	
	    		//thread to send build request to server
	    		final Thread buildThread = new Thread(new Runnable() {	
	    			@Override
	    			public void run() {				
	    				/*int c = */MainTabActivity.game.build(b.getId(), amount, pageKey, token);	
	    				// TODO alarm in c sec
	    				((MainTabActivity) getParent()).reloadTitleData();
	    				//Hide the loader and tell the user that the request was sent
	    				runOnUiThread(new Runnable() {					
	    					@Override
	    					public void run() {
	    						Toast.makeText(getApplicationContext(), getString(R.string.now_building, b.getName()), Toast.LENGTH_SHORT).show();
	    						loaderDialog.cancel();
	    						loadData();
	    					}
	    				});
	    			}
	    		});			
	    		buildThread.start();
	    	  }
	    	});
    	alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
	    	  public void onClick(DialogInterface dialog, int whichButton) {
	    		  dialog.cancel();
	    	  }
	    	});
    	alert.show();
	}
	
	private void loadData(){
		final ProgressDialog loader = new ProgressDialog(this);				
		loader.setMessage(getString(R.string.loading));
		
		Thread t_load = new Thread(new Runnable() {			
			@Override
			public void run() {
				String body = MainTabActivity.game.get("page=" + pageKey);
				//Token aus links
				if(body.contains("token=")){
					token = Tools.between(body, "token=", "'");
				}
				//Token aus formfield
				if(body.contains("name='token'")){
					token = Tools.between(body, "name='token' value='", "'");					
				}

				Planet currentPlanet = MainTabActivity.game.getCurrentPlanet();	
				currentPlanet.setGlobalTechtree(null);//reset techtree
				
				ArrayList<BuildObject> objectlist = Tools.parseObjectList(body, ulKey[0], liKey[0], currentPlanet, ObjectListActivity.this);
				for(int i=1; i < ulKey.length; i++){
					ArrayList<BuildObject> o = Tools.parseObjectList(body, ulKey[i], liKey[i], currentPlanet, ObjectListActivity.this);
					objectlist.addAll(o);
				}
				adapter = new BuildObjectAdapter(ObjectListActivity.this, R.layout.adapter_item_object, objectlist);
				((TextView)findViewById(R.id.txt_countdown)).post(new Runnable() {					
					@Override
					public void run() {
						setListAdapter(adapter);					
						final int countdownkey = adapter.getObjectWithCountdown();
						if(countdownkey >= 0){
							//remove old countdown (fixes stacking)
							h_countdown.removeCallbacks(t_countdown);
							final TextView countdown = (TextView)findViewById(R.id.txt_countdown);
							countdown.setVisibility(View.VISIBLE);
							countdown.setText(Tools.sec2str(adapter.getItem(countdownkey).getTimeLeft()));
							t_countdown = new Runnable() {
						   		 public void run() {
										adapter.getItem(countdownkey).countDown();
										if(adapter.getItem(countdownkey).getTimeLeft() <= 0){
											h_countdown.removeCallbacks(t_countdown);
											loadData();
										}else{
							   			 	countdown.post(new Runnable() {							
												@Override
												public void run() {
													countdown.setText(Tools.sec2str(adapter.getItem(countdownkey).getTimeLeft()));
												}
											});		   			 	
							   			 	h_countdown.postDelayed(this, 1000);
										}
						   		 }	    		   
							};
							t_countdown.run();
						}else{
							((TextView)findViewById(R.id.txt_countdown)).setVisibility(View.GONE);
						}	
						loader.cancel();	
					}
				});				
			}
		});
		loader.show();
		t_load.start();
	}
}
