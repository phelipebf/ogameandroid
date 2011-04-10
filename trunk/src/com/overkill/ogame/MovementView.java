package com.overkill.ogame;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.overkill.ogame.game.FleetEvent;
import com.overkill.ogame.game.FleetEventAdapter;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MovementView extends ListActivity {
	FleetEventAdapter adapter;
	
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
				adapter = new FleetEventAdapter(MovementView.this,
						R.layout.adapter_item_fleetevent,
						new ArrayList<FleetEvent>(Arrays.asList(MainTabActivity.game.getFleetEvents())));		
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
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	  super.onCreateContextMenu(menu, v, menuInfo);
	  AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
	  int position = info.position; 
	  if(adapter.getItem(position).canCancel()){
		  menu.add(0, android.R.string.cancel, 0, android.R.string.cancel);		
		  contextMenuPosition = position;
	  }	  
	  //menu.add(0, 1, 0, "Spionagesonde senden");
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
}
