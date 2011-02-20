package com.overkill.ogame;

import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.overkill.ogame.game.FleetAdapter;
import com.overkill.ogame.game.FleetEvent;
import com.overkill.ogame.game.FleetEventAdapter;
import com.overkill.ogame.game.Ship;
import com.overkill.ogame.game.Tools;

/**
 * Handles fleet control
 * Different states are set by and extra named tab. if tab isn't set the movement tab will be used
 * The different values of tab origin from the ingame use of the page parameter
 * @author Stephan
 *
 */
public class FleetView extends ListActivity {
	String task = "movement";
	@SuppressWarnings("rawtypes")
	ArrayAdapter adapter;

	String[] ulKey;
	
	public int selectedShips = 0;

	// 1 -> 2
	//index.php?page=fleet2&session=912bb66e8f11 POST
	
	//$('form[name=shipsChosen]').serialize()
	//"galaxy=3&system=293&position=7&type=1&mission=0&speed=10&am202=2"
	
	

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
		} else {
			setContentView(R.layout.activity_tab_movement);			
		}
		
		registerForContextMenu(getListView());
		
		Thread t = new Thread(new Runnable() {			
			@Override
			public void run() {			
				if("fleet1".equals(task)) {
					onCreateFleet1();
				} else if("movement".equals(task)){
					//Read the current eventList data
					String body = MainTabActivity.game.get("page=eventList&ajax=1");
					//if the date contains at least on event
					if(body.contains("<div class=\"eventFleet\"")){
						body = Tools.between(body, "<div id=\"eventContent\" style=\"text-align:center\">", "<div id=\"eventFooter\">");
						//Split the events and put them into the adapter
						String[] eventshtml = body.split("<div class=\"eventFleet\"");
						ArrayList<FleetEvent> events = new ArrayList<FleetEvent>(eventshtml.length - 1);
						for(int i = 1; i < eventshtml.length; i++){
							events.add(new FleetEvent(eventshtml[i], MainTabActivity.game));
						}
						adapter = new FleetEventAdapter(FleetView.this, R.layout.adapter_item_fleetevent, events);
						adapter.setNotifyOnChange(true);
						
					}
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
	
	private void onCreateFleet1() {
		ulKey = getIntent().getExtras().getStringArray("ulKey"); 
		
		String body = MainTabActivity.game.get("page=fleet1&ajax=1");
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
				Toast.makeText(getApplicationContext(), "next", Toast.LENGTH_SHORT).show();
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
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	  super.onCreateContextMenu(menu, v, menuInfo);
	  //menu.add(0, 1, 0, "Spionagesonde senden");
	  //menu.add(0, 2, 0, "Abbrechen");
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		if("movement".equals(task)){
			final FleetEvent tmp = (FleetEvent) getListAdapter().getItem(position);
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
	    	alert.setTitle(R.string.fleet_info_title);
	    	String info = "";
	    	Document table = Jsoup.parse(MainTabActivity.game.get("page=eventListTooltip&ajax=1&eventID=" + String.valueOf(tmp.getEventId())));
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
	}
	
}
