package com.overkill.ogame;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.overkill.ogame.game.FleetEvent;
import com.overkill.ogame.game.FleetEventAdapter;

public class MovementView extends ListActivity {
	FleetEventAdapter adapter;
	
	private Handler h_countdown = new Handler();
	private Runnable t_countdown;
	
	int contextMenuPosition = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
   	 	getWindow().requestFeature(Window.FEATURE_INDETERMINATE_PROGRESS);			
		setContentView(R.layout.activity_tab_movement);			
		registerForContextMenu(getListView());	
		loadData();
	}
	
	public void loadData(){
		Thread t = new Thread(new Runnable() {			
		 	@Override
			public void run() {		
		 		// Get All Events (own + hostile)
		 		ArrayList<FleetEvent> allEvents = new ArrayList<FleetEvent>(Arrays.asList(MainTabActivity.game.getFleetEvents()));
		 		// Get own Events with cancel IDs
		 		ArrayList<FleetEvent> myEvents = new ArrayList<FleetEvent>(Arrays.asList(MainTabActivity.game.getCancelableFleetEvents()));
		 		// Merge cancel IDs to all events
		 		int eventCount = allEvents.size();
		 		for(int i = 0; i < eventCount; i++){
		 			int indexInMy = myEvents.indexOf(allEvents.get(i));
		 			if(indexInMy < 0){ // Not found
		 				continue;
		 			}
		 			allEvents.set(i, myEvents.get(indexInMy));
		 		}
		 		
				adapter = new FleetEventAdapter(MovementView.this,
						R.layout.adapter_item_fleetevent,
						allEvents);		
				runOnUiThread(new Runnable() {					
					@Override
					public void run() {
						setListAdapter(adapter);
						setProgressBarIndeterminateVisibility(false);	
						/*h_countdown.removeCallbacks(t_countdown);
						t_countdown = new Runnable() {
					   		 public void run() {
					   			 		for(int i = 0; i < adapter.getCount(); i++){
					   			 			adapter.getItem(i).setTimeLeft(adapter.getItem(i).getTimeLeft() - 1);
					   			 		}
					   			 		adapter.notifyDataSetChanged();
					   			 		if(adapter.getCount() > 0)
					   			 			h_countdown.postDelayed(this, 1000);
									}
						};   
						if(adapter.getCount() > 0)
							t_countdown.run();
						*/
					}
				});
			}
		});
		setProgressBarIndeterminateVisibility(true);
		t.start();
		
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	  super.onCreateContextMenu(menu, v, menuInfo);
	  AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
	  int position = info.position; 
	  if(adapter.getItem(position).canCancel()){
		  menu.add(0, android.R.string.cancel, 0, android.R.string.cancel);		
		  contextMenuPosition = position;
	  }	  
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		MainTabActivity.game.cancelFleetEvent(adapter.getItem(contextMenuPosition).getID());
		contextMenuPosition = -1;
		loadData();
		return super.onContextItemSelected(item);
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
    	alert.setTitle(R.string.fleet_info_title);
    	
    	alert.setMessage(adapter.getItem(position).getInfo());
    	alert.setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
	    	  public void onClick(DialogInterface dialog, int whichButton) {
	    		  dialog.cancel();
	    	  }
	    	});
    	alert.show();
	}

	public void onNewMissionClick(View view){
		Intent i = new Intent(this, FleetView.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
			.putExtra("tab", "fleet1");
		startActivity(i);
		finish();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		h_countdown.removeCallbacks(t_countdown);
	}
}
