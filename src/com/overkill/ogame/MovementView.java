package com.overkill.ogame;

import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.overkill.ogame.game.FleetEvent;
import com.overkill.ogame.game.FleetEventAdapter;
import com.overkill.ogame.game.Tools;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.View;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MovementView extends ListActivity {
	@SuppressWarnings("rawtypes")
	ArrayAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
   	 	getWindow().requestFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
			
		setContentView(R.layout.activity_tab_movement);	
		
		registerForContextMenu(getListView());
		
		Thread t = new Thread(new Runnable() {			
		 			@Override
			public void run() {			

				//Read the current eventList data
				String body = MainTabActivity.game.get("page=eventList&ajax=1");
				//if the data contains at least on event
				if(body.contains("<div class=\"eventFleet\"")){
					body = Tools.between(body, "<div id=\"eventContent\" style=\"text-align:center\">", "<div id=\"eventFooter\">");
					//Split the events and put them into the adapter
					String[] eventshtml = body.split("<div class=\"eventFleet\"");
					ArrayList<FleetEvent> events = new ArrayList<FleetEvent>(eventshtml.length - 1);
					for(int i = 1; i < eventshtml.length; i++){
						events.add(new FleetEvent(eventshtml[i], MainTabActivity.game));
					}
					adapter = new FleetEventAdapter(MovementView.this, R.layout.adapter_item_fleetevent, events);
					adapter.setNotifyOnChange(true);						
				}	
				
				runOnUiThread(new Runnable() {					
					@Override
					public void run() {
						setListAdapter(adapter);	
						setProgressBarIndeterminateVisibility(false);												
					}
				});
			}

		
/*		Thread t = new Thread(new Runnable() {			
			@Override
			public void run() {			

				//Read the current eventList data
				Document html = Jsoup.parse(MainTabActivity.game.get("movement&ajax=1"));
				Elements fleets = html.select("div#fleet*");
				//if the data contains at least on event
				for(Element fleet : fleets){
					int id = Integer.valueOf(fleet.id().replace("fleet", ""));
				}
				
				runOnUiThread(new Runnable() {					
					@Override
					public void run() {
						setListAdapter(adapter);	
						setProgressBarIndeterminateVisibility(false);												
					}
				});
			}*/
		});
		setProgressBarIndeterminateVisibility(true);
		t.start();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	  super.onCreateContextMenu(menu, v, menuInfo);
	  //menu.add(0, 1, 0, "Spionagesonde senden");
	  //menu.add(0, 2, 0, "Abbrechen");
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		final FleetEvent tmp = (FleetEvent) getListAdapter().getItem(position);
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
    	alert.setTitle(R.string.fleet_info_title);
    	String info = "";
    	Document table = Jsoup.parse(MainTabActivity.game.get("page=eventListTooltip&ajax=1&eventID=" + String.valueOf(tmp.getID())));
    	Elements tr = table.select("tr");
    	for(int i = 0; i < tr.size(); i++){
    		info += tr.get(i).text() + "\n";
    	}
    	alert.setMessage(info);
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
