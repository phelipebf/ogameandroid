package com.overkill.ogame.game;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.flurry.android.FlurryAgent;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class Tools {
	
	public static String filesDir;
	
	/**
	 * Converts an html escapted Umlaut into the real char
	 * @param html
	 * @return
	 */
	public static String htmlconvert(String html){
		return html.replace("&auml;", "ä").
					replace("&ouml;", "ö").
					replace("&uuml;", "ü").
					replace("&Auml;", "Ä").
					replace("&Ouml;", "Ö").
					replace("&Uuml;", "Ü");
	}
	
	public static Drawable ImageOperations(String address) {
		Log.i("ImageOperations", address);
		// TODO The image should be save somewhere to prevent permanent reloading of the same file
		/*//get filename
		String[] parts = address.split("/");
		String filename = parts[parts.length - 1];
		//need basepath
		File file = new File(filesDir + "/" + filename);
		Log.i("file", file.getAbsolutePath());
		if(file.exists() == false){*/
			try {
				URL url = new URL(address);
				InputStream is = (InputStream) url.getContent();
				Drawable d = Drawable.createFromStream(is, "src");
				//is.reset();
				//save(file, is);
				return d;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		/*}else{
			Drawable d = Drawable.createFromPath(file.getAbsolutePath());
			return d;
		}*/
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
		
	public static String sec2str(int sec){
		if(sec == 0) return "0s";
		String str = "";
		int day = sec / 86400;
		sec -= 86400 * day;
		int hour = sec / 3600;
		sec -= 3600 * hour;
		int min = sec / 60;
		sec -= 60 * min;
		if(day > 0)
			str+= day + "d ";
		if(hour > 0)
			str+= hour + "h ";
		if(min > 0)
			str+= min + "m ";
		if(sec > 0)
			str+= sec + "s ";
		return str.substring(0, str.length() - 1);
	}
	
	public static ArrayList<BuildObject> parseObjectList(String body, String ulKey, String liKey, Planet planet, Context context){
		if(body.contains(ulKey)==false){
			Log.e("parseObjectList", ulKey + " not found>>\n" + body);
			return new ArrayList<BuildObject>();
		}
		
		Document document = Jsoup.parse(body);
		
		Elements ul = document.select("ul#" + ulKey);
		Elements li = ul.select("li");
		
		
		ArrayList<BuildObject> objectlist = new ArrayList<BuildObject>();		
		
		for(int i = 0; i < li.size(); i++){
			Element item = li.get(i);
			
			String status = item.className();				
			String id = item.select("div").get(0).className().replace(liKey, "");
			if(id.contains(" "))
				id = id.substring(0, id.indexOf(" "));
			
			int timeleft = 0;
			String name = "";
			
			if(item.select("div").get(0).classNames().size() > 1){ //with countdown
				name = item.select("div").get(0).attr("title").substring(1); // cut off leading |
				timeleft = getCountdown(body, getQuetypeById(Integer.valueOf(id)));
			}else{
				name = item.select("span.textlabel").text();
			}
			// Fix '.' if value >= 1000
			String level = item.select("span.level").text().replace(name, "").trim().replace(".", "");
						
			//Log.i("li", id + " " + name + " (" + level + ") " + status);
			
			BuildObject m = new BuildObject(context, Integer.valueOf(id), name, status, Integer.valueOf(level));
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
			}
			objectlist.add(m);
		}
		return objectlist;
	}
		
	public static int getQuetypeById(int id){
		int i = Item.QUETYPE_BUILDING;
		if(id > 100)
			i = Item.QUETYPE_RESEARCH;
		if(id > 200)
			i = Item.QUETYPE_MULTIPLE;
		return i;
	}
	
	public static int getCountdown(String body, int quetype){
		switch(quetype){
			case Item.QUETYPE_BUILDING: 
				if(body.contains("new baulisteCountdown(getElementByIdWithCache(\"Countdown\"), ") == false)
					return 0;
				return Integer.valueOf(Tools.between(body, "new baulisteCountdown(getElementByIdWithCache(\"Countdown\"), ", ","));	
			case Item.QUETYPE_RESEARCH: 
				if(body.contains("new bauCountdown(" ) == false)
					return 0;
				String s = Tools.between(body, "new bauCountdown(" , ");");
				String[] param = s.split(","); 
				return Integer.valueOf(param[1].trim());	
			case Item.QUETYPE_MULTIPLE: 
				if(body.contains("new schiffbauCountdown(" ) == false)
					return 0;
				String s2 = Tools.between(body, "new schiffbauCountdown(" , ");");
				String[] param2 = s2.split(","); 
				return Integer.valueOf(param2[3].trim()) + ((Integer.valueOf(param2[1].trim()) - 1) * Integer.valueOf(param2[4].trim()));	
		}
		return 0;
	}	
}
