package com.overkill.ogame.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.util.Log;

public class Galaxy {

	private String probeValue;
	private String recyclerValue;
	private String missileValue;
	private String slotUsed;
	private String slotTot;
	
	HashMap<String, GalaxySystem> solarSystems = new HashMap<String, GalaxySystem>();//cache

	/**
	 * false -> not enough Deuterium! You need 10 Units of Deuterium
	 * @param game
	 * @param galaxy
	 * @param system
	 * @return
	 */
	public boolean canLoadContent(GameClient game, int galaxy, int system) {
				
		List<NameValuePair> postData = new ArrayList<NameValuePair>(2);
        postData.add(new BasicNameValuePair("galaxy", String.valueOf(galaxy)));
        postData.add(new BasicNameValuePair("system", String.valueOf(system)));
        String jsonString = game.execute("page=galaxyCanLoad&ajax=1", postData);
        
        boolean canLoad = false;
		try {
			JSONObject json = new JSONObject(jsonString);
			JSONObject status = json.getJSONObject("status");
			canLoad = status.getBoolean("status");
	        
		} catch (JSONException e) {}
			
		return canLoad;
	}

	public GalaxySystem getSolarSystem(GameClient game, int galaxy, int system) {
		String key = galaxy + "-" + system;
		if(solarSystems.containsKey(key) == false) { //not in cache
			List<NameValuePair> postData = new ArrayList<NameValuePair>(2);
	        postData.add(new BasicNameValuePair("galaxy", String.valueOf(galaxy)));
	        postData.add(new BasicNameValuePair("system", String.valueOf(system)));
	        String html = game.execute("page=galaxyContent&ajax=1", postData);
			
	        GalaxySystem solarSystem = parseGalaxy(html, galaxy, system);
	        solarSystems.put(key, solarSystem);
		}
		return solarSystems.get(key);
	}
	
	/**
	 * Post data to server
	 * @param game
	 * @param mission 6=espionage?
	 * @param galaxy
	 * @param system
	 * @param planetPosition
	 * @param planetType 1=planet?
	 * @param shipCount
	 * @return
	 */
	public String sendShips(GameClient game, int mission, int galaxy, int system, int planetPosition, int planetType, int shipCount) {

		List<NameValuePair> postData = new ArrayList<NameValuePair>(2);
        postData.add(new BasicNameValuePair("mission", String.valueOf(mission)));
        postData.add(new BasicNameValuePair("galaxy", String.valueOf(galaxy)));
        postData.add(new BasicNameValuePair("system", String.valueOf(system)));
        postData.add(new BasicNameValuePair("position", String.valueOf(planetPosition)));
        postData.add(new BasicNameValuePair("type", String.valueOf(planetType)));
        postData.add(new BasicNameValuePair("shipCount", String.valueOf(shipCount)));
        String html = game.execute("page=minifleet&ajax=1", postData);
        return  parseResponse(html);
	}
	
	private GalaxySystem parseGalaxy(String html, int galaxy, int system){
		
		GalaxySystem galaxySystem = new GalaxySystem();
		
		Document solarSystem = Jsoup.parse(html);
		probeValue = solarSystem.select("#probeValue").text();
		recyclerValue = solarSystem.select("#recyclerValue").text();
		missileValue = solarSystem.select("#missileValue").text();
		
		Elements slotValue = solarSystem.select("#slotValue");
		slotUsed = slotValue.select("#slotUsed").text();
		slotValue.remove("#slotUsed");
		slotTot = slotValue.text().trim().substring(1);
		
		for(Element tr : solarSystem.select("tr.row")) {
			GalaxyPlanet planet = null;
			
			int position = Integer.parseInt(tr.select("td.position").text());
			
			Element microplanet;
			//empty slots have microplanet1 - used have microplanet as class name
			if(tr.select("td.microplanet").isEmpty())
				microplanet = tr.select("td.microplanet1").get(0);
			else
				microplanet = tr.select("td.microplanet").get(0);
			if(microplanet.children().size() > 0) { //no planet
				planet = new GalaxyPlanet();
				planet.setPosition(position);
				
				planet.setPlanetName(microplanet.select("span.textNormal").text());
				//Check if there is an activity
				String planetActivity = microplanet.select("h4").html();
				if(planetActivity.lastIndexOf("</span>") + "</span>".length() == planetActivity.length()){
					//no activity
					planet.setPlanetActivity("");
				}else{
					planetActivity = planetActivity.substring(planetActivity.lastIndexOf("</span>") + "</span>".length());
					//trim off icon if there is one
					if(planetActivity.contains("<"))
						planetActivity = planetActivity.substring(0, planetActivity.indexOf("<")).trim();
					planet.setPlanetActivity(Tools.htmlconvert(planetActivity));
				}
				
				planet.setPlanetCoords(microplanet.select("#pos-planet").text());
				
				//TODO: moon data
				Element moon = tr.select("td.moon").get(0);
				
				Element debris = tr.select("td.debris").get(0);
				Elements debrisContent = debris.select("li.debris-content");
				if(debrisContent.size() > 0) { //no debris
					String debrisMetal = debrisContent.get(0).text();
					debrisMetal = debrisMetal.substring(debrisMetal.indexOf(": ") + 2);
					planet.setDebrisMetal(debrisMetal);
					String debrisCrystal = debrisContent.get(1).text();
					debrisCrystal = debrisCrystal.substring(debrisCrystal.indexOf(": ") + 2);
					planet.setDebrisCrystal(debrisCrystal);
					String debrisRecyclersNeeded = debris.select("li.debris-recyclers").get(0).text();
					debrisMetal = debrisRecyclersNeeded.substring(debrisRecyclersNeeded.indexOf(": ") + 2);
					planet.setDebrisRecyclersNeeded(debrisRecyclersNeeded);
				}
				
				Element player = tr.select("td.playername").get(0);
				planet.setPlayerName(player.select("h4 > span > span").text());
				//Don't try to read more if the player is us
				if(planet.getPlayerName().equals("") == false){
					String playerRank = player.select("li.rank").text();
					playerRank = playerRank.substring(playerRank.indexOf(": ") + 2);
					planet.setPlayerRank(playerRank);
				}else{
					planet.setPlayerName(player.select("span").text());
					planet.setPlayerRank("-");
				}
				
				Element allytag = tr.select("td.allytag").get(0);
				if(allytag.children().size() > 0) {	//no ally
					planet.setAllyName(allytag.select("h4 > span").text());
					String allyRank = allytag.select("li.rank").text();
					allyRank = allyRank.substring(allyRank.indexOf(": ")+2);
					planet.setAllyRank(allyRank);
					String allyMembers = allytag.select("li.members").text();
					allyMembers = allyMembers.substring(allyMembers.indexOf(": ") + 2);
					planet.setAllyMembers(allyMembers);	
				}
			}
			galaxySystem.setPlanet(position, planet);
		}
		return galaxySystem;
	}
	
	public String toString() {
		return "probeValue:" + probeValue 
			+ ", recyclerValue:" + recyclerValue 
			+ ", missileValue:" + missileValue
			+ ", slotUsed:" + slotUsed
			+ ", slotTot:" + slotTot;
	}
	
	
	/**
	 * Examples:
	 * 		"612 [3:286:12]" -> Fleet dispatch Error, no free fleet slots available [3:286:12]
	 * 		"600 2 2 0 0 1 1 [3:286:12]" -> Success, send espionage probe to: [3:286:12] (1)
	 */
	private String parseResponse(String response) {
		String[] retVals = response.split(" ");
		String result = "";
		switch(Integer.parseInt(retVals[0])) {
			case 600:
				result = "Success";
				switch(Integer.parseInt(retVals[6])) {
					case 1: 
						result += ", send espionage probe to: " + retVals[7];
					break;
					case 2: 
						result += ", send recycler to: " + retVals[7];
					break;
				}
				result += " (" + retVals[5] + ")";
			break;
			case 601:
				result = "An error has occurred " + retVals[1];
			break;
			case 602:
				result = "Error, there is no moon " + retVals[1];
			break;
			case 603:
				result = "Error, player can`t be approached because of newbie protection " + retVals[1];
			break;
			case 604:
				result = "Player is too strong to be attacked " + retVals[1];
			break;
			case 605:
				result = "Error, player is in vacation mode " + retVals[1];
			break;
			case 610:
				result = "Error, not enough ships available, send maximum number:"+retVals[1];
			break;
			case 611:
				result = "Error, no ships available " + retVals[1];
			break;
			case 612:
				result = "Error, no free fleet slots available " + retVals[1];
			break;
			case 613:
				result = "Error, you don`t have enough deuterium " + retVals[1];
			break;
			case 614:
				result = "Error, there is no planet there " + retVals[1];
			break;
			case 615:
				result = "Error, not enough cargo capacity " + retVals[1];
			break;
			case 616:
				result = "Multi-alarm " + retVals[1];
			break;
			case 617:
				result = "Admin or GM " + retVals[1];
			break;
			case 618:
				result = "Attack ban until 01.01.1970 01:00:00";
			break;
		}
		return result;
	}
	
}