package com.overkill.ogame;

import java.util.ArrayList;

import com.overkill.ogame.game.FleetAdapter;
import com.overkill.ogame.game.FleetEvent;
import com.overkill.ogame.game.FleetEventAdapter;
import com.overkill.ogame.game.Ship;
import com.overkill.ogame.game.Tools;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.ContextMenu;
import android.view.View;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class FleetView extends ListActivity {
	String task = "movement";
	@SuppressWarnings("rawtypes")
	ArrayAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
   	 	getWindow().requestFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		
		setContentView(R.layout.activity_tab_fleet);
		
		registerForContextMenu(getListView());
		
		if(getIntent().hasExtra("tab"))
			task = getIntent().getExtras().getString("tab");
		
		Thread t = new Thread(new Runnable() {			
			@Override
			public void run() {			
				if(task.equals("fleet1")){
					String body = MainTabActivity.game.get("page=fleet1");
					ArrayList<Ship> objects_mil = getShipList(body, "military", "button");
					ArrayList<Ship> objects_civ = getShipList(body, "civil", "button");
					
					objects_mil.addAll(objects_civ);
					adapter = new FleetAdapter(FleetView.this, R.layout.adapter_item_fleet, objects_mil);
				}else if(task.equals("movement")){			
					String body = MainTabActivity.game.get("page=eventList&ajax=1");
					if(body.contains("<div class=\"eventFleet\"")){
						body = Tools.between(body, "<div id=\"eventContent\" style=\"text-align:center\">", "<div id=\"eventFooter\">");
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
    	alert.setMessage(Html.fromHtml(MainTabActivity.game.get("page=eventListTooltip&ajax=1&eventID=" + String.valueOf(tmp.getEventId()))).toString());
    	alert.setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
	    	  public void onClick(DialogInterface dialog, int whichButton) {
	    		  dialog.cancel();
	    	  }
	    	});
    	alert.show();    	
	}
	
	private ArrayList<Ship> getShipList(String body, String ulKey, String liKey){
		String ul = "";
		
		if(body.contains("<ul id=\"" + ulKey + "\">")){
			ul = Tools.between(body, "<ul id=\"" + ulKey + "\">", "</ul>");
		}else{
			int start = 0; int end = 0;
			start = body.indexOf("<ul id=\"" + ulKey) + ("<ul id=\"" + ulKey).length();
			start = body.indexOf(">", start) + 1;
			end = body.indexOf("</ul>", start);
			ul = body.substring(start, end);
		}
		ArrayList<Ship> objectlist = new ArrayList<Ship>();		
		String[] items = ul.split("</li>");
		for(String item : items){
			if(item.contains("<li ") == false)
				continue;
			
			String status = Tools.between(item, "class=\"", "\"");
			if(status.equals("on") == false)
				continue;
			String id = Tools.between(item, "id=\"" + liKey + "", "\"", " ");
			
			String name = "";
			String level = "";
			
			name = Tools.between(item, "<span class=\"textlabel\">", "</span>", "<").trim();	
			int offset = item.indexOf("<span class=\"textlabel\">") + "<span class=\"textlabel\">".length();
			offset = item.indexOf("</span>", offset) + "</span>".length();
			int ende = item.indexOf("</span>", offset);
			int ende2 = item.indexOf("<span", offset);
			if((ende2 > 0) && (ende2 < ende))
				ende = ende2;
			level = item.substring(offset, ende).trim();
			
			Ship m = new Ship(Integer.valueOf(id), name, Integer.valueOf(level), getApplicationContext());	
			
			objectlist.add(m);
		}		
		return objectlist;
	}
	
}
