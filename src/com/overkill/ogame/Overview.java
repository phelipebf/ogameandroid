package com.overkill.ogame;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.overkill.ogame.game.Item;
import com.overkill.ogame.game.Tools;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class Overview extends Activity {
	
	private long timeLastAjaxCall;
	private String info;
	
	private Handler h_countdown = new Handler();
	private Runnable t_countdown;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
        final TextView txt_info = (TextView)findViewById(R.id.txt_info);
        txt_info.setText(R.string.loading);
        Thread t = new Thread(new Runnable() {			
			@Override
			public void run() {
				info =  MainTabActivity.game.get("page=overview");	
				runOnUiThread(new Runnable() {					
					@Override
					public void run() {			
						try{
							txt_info.setText("");
							
							txt_info.append(Tools.between(info, "textContent[0] = \"", "\"") + " ");
							txt_info.append(Tools.between(info, "textContent[1] = \"", "\"").replace("<span>", "").replace("</span>", "").replace("<\\/span>", "").replace("\\/", "/") + "\n");
							
							txt_info.append(Tools.between(info, "textContent[2] = \"", "\"") + " ");
							txt_info.append(Tools.between(info, "textContent[3] = \"", "\"").replace("\\u00b0", "") + "\n");
							
							txt_info.append(Tools.between(info, "textContent[4] = \"", "\"") + " ");
							txt_info.append(MainTabActivity.game.getCurrentPlanet().getCoordinates() + "\n");
							
							txt_info.append(Tools.between(info, "textContent[6] = \"", "\"") + " ");
							int start = info.indexOf("textContent[7] = \"") + "textContent[7] = \"".length();
							start = info.indexOf(">", start) + 1;
							int end = info.indexOf("<", start);
							txt_info.append(info.substring(start, end));
							
							//get info about unread messages
							Document html = Jsoup.parse(info);
							Elements alertBox = html.select("#message_alert_box");
							if(alertBox.size() == 1) {
								String unreadMessages = alertBox.select("span").text().trim();
								MainTabActivity.notify.setMessages(Integer.valueOf(unreadMessages));
							}else{
								MainTabActivity.notify.setMessages(0);
							}
							
							timeLastAjaxCall = SystemClock.elapsedRealtime();						
							
							h_countdown.removeCallbacks(t_countdown);
							t_countdown = new Runnable() {
						   		 public void run() {
						   			 loadCountdown();			 	
						   			 h_countdown.postDelayed(this, 1000);
						   		 }
							};   		   					
							t_countdown.run();
						}catch(Exception ex){
							ex.printStackTrace();
							txt_info.setText(ex.getLocalizedMessage());
						}	
					}
				});
			}
        });
        t.start();
    }
		
	@Override
	protected void onPause() {
		super.onPause();
		h_countdown.removeCallbacks(t_countdown);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	private void loadCountdown(){		
		resetCountdown();
		
		boolean needCountdown = false;
		long diff = (SystemClock.elapsedRealtime() - timeLastAjaxCall) / 1000;
		
		try{
		
			Document html = Jsoup.parse(info);
			Elements boxes = html.select("table.construction");
			// 0 -> building | 1 -> research | 2 -> ships/defense
			
			for(int i = 0; i < boxes.size(); i++){
				Element box = boxes.get(i);
				if(box.select("tr.data").size() > 0){							
					String name = box.select("tr").get(0).text();
					String src = box.select("img").attr("src");
					String id = "";
					long time = Tools.getCountdown(info, i + 1) - diff;
									
					ImageView img = null;
					TextView txt = null;
					
					switch(i + 1){
						case Item.CUETYPE_BUILDING:
							img = (ImageView)findViewById(R.id.img_building);
							txt = (TextView)findViewById(R.id.txt_building);
							id = src.substring(src.lastIndexOf("_") + 1, src.indexOf("."));
							break;
						case Item.CUETYPE_RESEARCH:
							img = (ImageView)findViewById(R.id.img_reseach);
							txt = (TextView)findViewById(R.id.txt_research);
							id = src.substring(src.lastIndexOf("_") + 1, src.indexOf("."));
							break;									
						case Item.CUETYPE_MULTIPLE:
							img = (ImageView)findViewById(R.id.img_ships);
							txt = (TextView)findViewById(R.id.txt_ships);
							id = src.substring(src.lastIndexOf("/") + 1, src.indexOf("_"));
							String s2 = Tools.between(info, "new shipCountdown(" , ");");
							String[] param2 = s2.split(","); 
							int count = Integer.valueOf(param2[6].trim());
							count -= diff / Integer.valueOf(param2[3].trim());
							name += " (" + count + ")";
							break;									
					}
					
					if(time <= 0){
						img.setVisibility(View.INVISIBLE);
						txt.setVisibility(View.INVISIBLE);
						continue;
					}					
					
					needCountdown = true;
					
					img.setVisibility(View.VISIBLE);
					txt.setVisibility(View.VISIBLE);
					
					img.setImageResource(getResources().getIdentifier("drawable/supply" + id, null, getPackageName()));
					txt.setText(name + "\n" + Tools.sec2str(time));
				}
			}
			
			if(needCountdown == false)
				h_countdown.removeCallbacks(t_countdown);
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}	
	
	private void resetCountdown(){
		((ImageView)findViewById(R.id.img_building)).setVisibility(View.INVISIBLE);
		((ImageView)findViewById(R.id.img_reseach)).setVisibility(View.INVISIBLE);
		((ImageView)findViewById(R.id.img_ships)).setVisibility(View.INVISIBLE);
		((TextView)findViewById(R.id.txt_building)).setVisibility(View.INVISIBLE);
		((TextView)findViewById(R.id.txt_research)).setVisibility(View.INVISIBLE);
		((TextView)findViewById(R.id.txt_ships)).setVisibility(View.INVISIBLE);
	}
}
