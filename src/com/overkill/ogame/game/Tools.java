package com.overkill.ogame.game;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.util.Log;

import com.flurry.android.FlurryAgent;
import com.overkill.ogame.R;

public class Tools {
	
	public static String filesDir;
	
	/**
	 * Converts an html escapted Umlaut into the real char
	 * @param html
	 * @return
	 */
	public static String htmlconvert(String html){
		return html.replace("&auml;", "?").
					replace("&ouml;", "?").
					replace("&uuml;", "?").
					replace("&Auml;", "?").
					replace("&Ouml;", "?").
					replace("&Uuml;", "?");
	}
	
	public static void save(File file, InputStream is){
		Log.i("filesystem", "store " + file.getAbsolutePath());
	    BufferedOutputStream f = null;
	    try {
	        f = new BufferedOutputStream(new FileOutputStream(file));
	        int c;
	        while ((c = is.read()) != -1){
				f.write(c);
	        }
	    } catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
	        if (f != null) try { f.close();	} catch (IOException e) { }
	    }
	}
	
	/**
	 * Gets a Substring between two Strings
	 * @param string The String to search in
	 * @param begin The String to start reading
	 * @param end The String to end with
	 * @return The part of string between begin and end
	 */
	public static String between(String string, String begin, String end){
		try{
			int start = string.indexOf(begin) + begin.length();
			int ende = string.indexOf(end, start);
			return string.substring(start, ende);
		}catch(Exception ex){
			ex.printStackTrace();
			FlurryAgent.onError("Tools.between", "String: " + string + "\n" + "Begin: " + begin + "\n" + "End: " + end, "StringIndexOutOfBoundsException");
			return "";
		}
	}
	
	public static String between(String string, String begin, String end, String alternativ_end){
		try{
			int start = string.indexOf(begin) + begin.length();
			int ende1 = string.indexOf(end, start);
			int ende2 = string.indexOf(alternativ_end, start);
			int ende = ende1;
			if(ende > ende2)
				ende = ende2;
			return string.substring(start, ende);
		}catch(Exception ex){
			ex.printStackTrace();
			FlurryAgent.onError("Tools.between", "String: " + string + "\n" + "Begin: " + begin + "\n" + "End: " + end, "StringIndexOutOfBoundsException");
			return "";
		}
	}
		
	public static String sec2str(long time){
		if(time == 0) return "0s";
		String str = "";
		long day = time / 86400;
		time -= 86400 * day;
		long hour = time / 3600;
		time -= 3600 * hour;
		long min = time / 60;
		time -= 60 * min;
		if(day > 0)
			str+= day + "d ";
		if(hour > 0)
			str+= hour + "h ";
		if(min > 0)
			str+= min + "m ";
		if(time > 0)
			str+= time + "s ";
		return str.substring(0, str.length() - 1);
	}
	
	public static ArrayList<BuildObject> parseObjectList(Document document, String ulKey, String liKey, Planet planet, Context context){
		
		ArrayList<BuildObject> objectlist = new ArrayList<BuildObject>();		
		Elements ul = document.select("ul#" + ulKey);
		
		if(ul.size() == 0){
			Log.e("parseObjectList", ulKey + " not found");
			return objectlist;
		}		
		
		for(Element item : ul.select("li")) {
			
			String status = item.className();				
			String id = item.select("div").get(0).className().replace(liKey, "");
			if(id.contains(" "))
				id = id.substring(0, id.indexOf(" "));
			
			int timeleft = 0;
			String name = "";

			if(item.select("div").get(0).classNames().size() > 1){ //with countdown
				String script = document.select("script").not("script[src]").html();
				name = item.select("div").get(0).attr("title").substring(1); // cut off leading |
				timeleft = getCountdown(script, getCuetypeById(Integer.valueOf(id)));
			}else{
				name = item.select("span.textlabel").text();
			}
			
			if(name.contains("("))
				name = name.substring(0, name.indexOf("(")).trim();
			
			// Fix '.' if value >= 1000 / ',' will be removed for the same reason
			String level = item.select("span.level").text().replace(name, "").trim().replace(".", "").replace(",", "");
			
			// Remove the (+2) from Technokrat
			if(level.contains(" (+2)")){
				level = level.replace(" (+2)", "");
				level = String.valueOf(Integer.valueOf(level) + 2);
			}
			
			//Sets status to "on" so we can add the same defense/ship to cue again. 
			if(getCuetypeById(Integer.valueOf(id)) == Item.CUETYPE_MULTIPLE && timeleft > 0 && status.equals("off"))
				status = "on";
			
			BuildObject m = null;
			try{
				m = new BuildObject(context, Integer.valueOf(id), name, status, Integer.valueOf(level));
				m.setResources(
						Resources.calc(Integer.valueOf(id), Integer.valueOf(level), Item.RESOURCE_METAL), 
						Resources.calc(Integer.valueOf(id), Integer.valueOf(level), Item.RESOURCE_CRYSTAL), 
						Resources.calc(Integer.valueOf(id), Integer.valueOf(level), Item.RESOURCE_DEUTERIUM)
						);		
				m.setTimeLeft(timeleft);
				if(planet != null){
					m.checkRecources(
								planet.getMetal(), 
								planet.getCrystal(), 
								planet.getDeuterium()
							);		
					//Set status to disabled if we cant build it
					if(m.canBuild()==false && m.getStatus().equals("on")){
						m.setStatus("disabled");
					}
				}
			}catch(Exception ex){
				m = new BuildObject(context, 1, "error reading this item", "disabled");
			}
			objectlist.add(m);
		}
		return objectlist;
	}
		
	public static int getCuetypeById(int id){
		int i = Item.CUETYPE_BUILDING;
		if(id > 100)
			i = Item.CUETYPE_RESEARCH;
		if(id > 200)
			i = Item.CUETYPE_MULTIPLE;
		return i;
	}
	
	public static int getCountdown(String body, int quetype){
		switch(quetype){
			case Item.CUETYPE_BUILDING: 
				if(body.contains("new baulisteCountdown(getElementByIdWithCache(\"Countdown\"), ") == false)
					return 0;
				return Integer.valueOf(Tools.between(body, "new baulisteCountdown(getElementByIdWithCache(\"Countdown\"), ", ","));	
			case Item.CUETYPE_RESEARCH: 
				if(body.contains("new bauCountdown(" ) == false && body.contains("new baulisteCountdown(getElementByIdWithCache('researchCountdown'),") == false)
					return 0;
				if(body.contains("new baulisteCountdown(getElementByIdWithCache('researchCountdown'),")){
					String s = Tools.between(body, "new baulisteCountdown(getElementByIdWithCache('researchCountdown'), ", ",");
					return Integer.valueOf(s.trim());	
				}else{
					String s = Tools.between(body, "new bauCountdown(" , ");");
					String[] param = s.split(","); 
					return Integer.valueOf(param[1].trim());	
				}
			case Item.CUETYPE_MULTIPLE: 
				if(body.contains("new shipCountdown(" ) == false)
					return 0;
				String s2 = Tools.between(body, "new shipCountdown(" , ");");
				String[] param2 = s2.split(","); 
				return Integer.valueOf(param2[4].trim()) + ((Integer.valueOf(param2[6].trim()) - 1) * Integer.valueOf(param2[3].trim()));	
		}
		return 0;
	}	
	
	/**
	 * Returns the time left for a single ship
	 * @param body HTML String
	 * @param currentShip If set the real value of the currently built ship will be returned
	 * @return Seconds needed for a ship or seconds left for current ship if currentShip is true
	 */
	public static int getCountdownPerShip(String body, boolean currentShip){
		if(body.contains("new shipCountdown(" ) == false)
			return 0;
		String s2 = Tools.between(body, "new shipCountdown(" , ");");
		String[] param2 = s2.split(","); 
		if(currentShip)
			return Integer.valueOf(param2[4].trim());
		else
			return Integer.valueOf(param2[3].trim());	
	}
	
	public static int[] getMovementImageCountdownParameters(String script, int id){
		int data[] = new int[4];
		
		int offset = 0;
		
		int positon = script.indexOf("movementImageCountdown(");
		
		while(positon > 0){
			String function = script.substring(positon + "movementImageCountdown(".length(), script.indexOf(");", positon));
			String param[] = function.split(",");
			if(param[0].trim().contains("route_" + String.valueOf(id))){
				data[0] = Integer.valueOf(param[1].trim());
				data[1] = Integer.valueOf(param[2].trim());
				data[2] = Integer.valueOf(param[3].trim());
				data[3] = Integer.valueOf(param[4].trim());
				return data;
			}
			offset = positon + "movementImageCountdown(".length();
			positon = script.indexOf("movementImageCountdown(", offset);
		}			
		return null;
	}
	
	public static String getServerSpecificData(Context ctx, String server, String key){
		Log.d("getServerSpecificData", "Server: " + server + ", Key: " + key);
		Document xml = Jsoup.parse(Tools.readRawTextFile(ctx, R.raw.languagedata));
		Elements data = xml.select("data[server=" + server + "]");
		if(data.size() > 0){
			Elements item = data.first().select("string[name=" + key + "]");
			if(item.size() > 0){
				String dataTxt = item.first().text();
				Log.d("getServerSpecificData", "Data fetched: " + dataTxt);
				return dataTxt;
			}
		}
		return "";
	}
	
	public static String readRawTextFile(Context ctx, int resId) {
		InputStream inputStream = ctx.getResources().openRawResource(resId);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		int i;
		try {
			i = inputStream.read();
			while (i != -1) {
				byteArrayOutputStream.write(i);
				i = inputStream.read();
			}
			inputStream.close();
		} catch (IOException e) {
			return null;
		}
		return byteArrayOutputStream.toString();
	}
	
	public static void trackLogin(String domain, String universe, boolean showAds){
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("country", domain);
		parameters.put("universe", universe);
		parameters.put("showAds", String.valueOf(showAds));
		FlurryAgent.onEvent("Login", parameters);
	}
}
