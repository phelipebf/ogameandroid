package com.overkill.ogame;

import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.overkill.ogame.game.BuildObject;
import com.overkill.ogame.game.BuildObjectAdapter;

import android.R.integer;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

public class ResourceSettingsView extends ListActivity {

	BuildObjectAdapter adapter;
	boolean savedChanges = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		ArrayList<BuildObject> objects = new ArrayList<BuildObject>();
				
		Document html = Jsoup.parse(MainTabActivity.game.get("page=resourceSettings&ajax=1"));
		Elements tr = html.select("tr");
		//0: 	Faktor
		//1: 	Header
		//2: 	Basic Income
		//3-8: 	DATA
		
		for(int i = 3; i <= 8; i++){
			Elements td = tr.get(i).select("td");
			//0:	Name (... LEVEL)
			//1:	Empty
			//2:	Metal
			//3:	Crystal
			//4:	Deuterium
			//5: 	energyUse/energyMax
			//6:	HTML-SELECT (name="last[ID]")
			int id = 0;
			if(td.size() > 6)
				id = Integer.valueOf(td.get(6).select("select").attr("name").replace("last", ""));
			String name = td.get(0).text();
			int level = 0;
			if(name.contains("(")){
				level = Integer.valueOf(name.substring(name.lastIndexOf(" "), name.lastIndexOf(")")).trim());
				name = name.substring(0, name.indexOf("("));
			}
			BuildObject object = new BuildObject(this, id, name, "on", level);
			object.setResources(
						Integer.valueOf(td.get(2).text().replace(".", "")),
						Integer.valueOf(td.get(3).text().replace(".", "")),
						Integer.valueOf(td.get(4).text().replace(".", ""))
					);
			String energy = td.get(5).text().replace(".", "");
			String e[] = energy.split("/");
			object.setEnergy(Integer.valueOf(e[0]));
			if(e.length > 1)
				object.setEnergyMax(Integer.valueOf(e[1]));
			
			object.setPercent(Integer.valueOf(getSelectedValue(td.get(6).select("select").get(0))));
			
			objects.add(object);
			
		}		
		adapter = new BuildObjectAdapter(this, R.layout.adapter_item_object, objects);
		setListAdapter(adapter);
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, final int position, long id) {
		super.onListItemClick(l, v, position, id);
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("Select");
		dialog.setCancelable(true);
		dialog.setSingleChoiceItems(R.array.percent, adapter.getItem(position).getPercent() / 10 ,new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		    	adapter.getItem(position).setPercent(item * 10);
		    	adapter.notifyDataSetChanged();
		    	dialog.dismiss();
		    	savedChanges = false;
		    }
		});
		dialog.show();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	private String getSelectedValue(Element select){
		Elements options = select.select("option");
		for(int i = 0; i < options.size(); i++){
			if(options.get(i).hasAttr("selected"))
				return options.get(i).attr("value");
		}
		return null;
	}
	
	public void save(View view){
		
	}
	
}
