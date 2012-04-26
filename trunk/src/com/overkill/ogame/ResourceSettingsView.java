package com.overkill.ogame;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.overkill.ogame.game.BuildObject;
import com.overkill.ogame.game.BuildObjectAdapter;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

public class ResourceSettingsView extends ListActivity {
	private static final String TAG = "ogame";
	
	int lastEditableIndex = 5;
	
	BuildObjectAdapter adapter;
	boolean savedChanges = true;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_resource_settings);		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		load();
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, final int position, long id) {
		if(position > lastEditableIndex)
			return;
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
		
	private String getSelectedValue(Element select){
		Elements options = select.select("option");
		for(int i = 0; i < options.size(); i++){
			if(options.get(i).hasAttr("selected"))
				return options.get(i).attr("value");
		}
		return null;
	}
	
	public void setAllNull(View view){
		for(int i = 0; i <= lastEditableIndex; i++){
			adapter.getItem(i).setPercent(0);		
		}
		adapter.notifyDataSetChanged();
	}
	
	public void setAllMax(View view){
		for(int i = 0; i <= lastEditableIndex; i++){
			adapter.getItem(i).setPercent(100);		
		}
		adapter.notifyDataSetChanged();
	}
	
	public void save(View view){
		Thread t = new Thread(new Runnable() {			
			@Override
			public void run() {
				List<NameValuePair> postData = new ArrayList<NameValuePair>();
				postData.add(new BasicNameValuePair("saveSettings", "1"));
				for(int i = 0; i < adapter.getCount(); i++){
					BuildObject b = adapter.getItem(i);
			        postData.add(new BasicNameValuePair("last" + String.valueOf(b.getId()), String.valueOf(b.getPercent())));			
				}
				MainTabActivity.game.execute("page=resourceSettings", postData);
				load();
			}
		});
		setProgressBarIndeterminateVisibility(true);
		t.start();
	}
	
	public void load(){
		Thread t = new Thread(new Runnable() {			
			@Override
			public void run() {
				ArrayList<BuildObject> objects = new ArrayList<BuildObject>();
				Document html = Jsoup.parse(MainTabActivity.game.get("page=resourceSettings&ajax=1"));
				Elements tr = html.select("tr");
				//0: 	Faktor
				//1: 	Header
				//2: 	Basic Income
				//3-8: 	DATA
				
				for(int i = 3; i < tr.size(); i++){
					Elements td = tr.get(i).select("td");
					try{
						//0:	Name (... LEVEL)
						//1:	Empty
						//2:	Metal
						//3:	Crystal
						//4:	Deuterium
						//5: 	energyUse/energyMax
						//6:	HTML-SELECT (name="last[ID]")
						int id = 0;
						int offset = 0;
						
						if(td.get(1).text().length() != 0)
							offset = 1;
						
						// skip amplifier row
						if(td.size() > 6 && td.get(6).select("select").size() == 0)
							continue;
						
						if(td.size() > 6 && td.get(6).select("select").size() > 0)
							id = Integer.valueOf(td.get(6).select("select").attr("name").replace("last", ""));
						String name = td.get(0).text();
						int level = 0;
						if(name.contains("(")){
							level = Integer.valueOf(name.substring(name.lastIndexOf(" "), name.lastIndexOf(")")).trim());
							name = name.substring(0, name.indexOf("("));
						}
						name = name.replace(":", "").trim();
						BuildObject object = new BuildObject(ResourceSettingsView.this, id, name, "on", level);
						object.setResources(
							Integer.valueOf(td.get(2 - offset).attr("title").replace("|", "").replace(".", "")),
							Integer.valueOf(td.get(3 - offset).attr("title").replace("|", "").replace(".", "")),
							Integer.valueOf(td.get(4 - offset).attr("title").replace("|", "").replace(".", ""))
						);
						
						String energy = td.get(5 - offset).text().replace(".", "");
						if(energy.equals("-"))
							energy = "0";
						String e[] = energy.split("/");
						object.setEnergy(Integer.valueOf(e[0]));
						if(e.length > 1)
							object.setEnergyMax(Integer.valueOf(e[1]));
						
						if(td.size() > 6 && td.get(6).select("select").size() > 0){
							object.setPercent(Integer.valueOf(getSelectedValue(td.get(6).select("select").get(0))));
							object.setDisplayType(BuildObject.DISPLAY_TYPE_ALL);
						}else{
							object.setPercent(0);
						}
						
						if(i > 8){
							object.setDisplayType(BuildObject.DISPLAY_TYPE_HIDE_LEVEL);
						}
						
						objects.add(object);
					}catch (Exception e) {
						e.printStackTrace();
						Log.e("parsing error", "\n" + td.html());
					}
					
				}		
				adapter = new BuildObjectAdapter(ResourceSettingsView.this, R.layout.adapter_item_object, objects);
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
}
