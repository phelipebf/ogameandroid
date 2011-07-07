package com.overkill.ogame.game;

import org.json.JSONObject;
import org.jsoup.nodes.Document;

import android.os.SystemClock;
import android.util.Log;

public class Planet {
	public static final int TYPE_PLANET = 1;
	public static final int TYPE_DEBRIS = 2;
	public static final int TYPE_MOON = 3;
	
	private int id;
	private String name;
	private String shortInfo;
	private String coordinates;
	private int fields_used;
	private int fields_max;
	
	private int image;
	
	private int metal;
	private int crystal;
	private int deuterium;
	private int energy;

	private int metalMax;
	private int crystalMax;
	private int deuteriumMax;
	
	private double metalProduction;
	private double crystalProduction;
	private double deuteriumProduction;
	
	private long timeLastAjaxCall;
	
	private Planet moon = null;
	
	private Document globalTechtree = null;
	
	private boolean is_moon;
	
	public Planet(int id, String name, int image) {
		this.id = id;
		this.name = name;
		this.image = image;
	}
	
	public Planet(String name, int image, String json) {
		this.name = name;
		this.image = image;
		this.parse(json);
	}
	
	public int getIcon(){
		return this.image;
	}
	
	public void parse(String jsonstring){
		try{
			timeLastAjaxCall = SystemClock.elapsedRealtime();
			
			JSONObject json = new JSONObject(jsonstring);

	    	JSONObject object = json.getJSONObject("metal");
	    	JSONObject resources = object.getJSONObject("resources");			
			this.metal = resources.getInt("actual");
			this.setMetalMax(resources.getInt("max"));
			this.metalProduction = resources.getDouble("production");
	    	
			object = json.getJSONObject("crystal");
	    	resources = object.getJSONObject("resources");			
			this.crystal = resources.getInt("actual");
			this.setCrystalMax(resources.getInt("max"));
			this.crystalProduction = resources.getDouble("production");
	    	
			object = json.getJSONObject("deuterium");
	    	resources = object.getJSONObject("resources");			
			this.deuterium = resources.getInt("actual");
			this.setDeuteriumMax(resources.getInt("max"));
			this.deuteriumProduction = resources.getDouble("production");

	    	object = json.getJSONObject("energy");
	    	resources = object.getJSONObject("resources");			
	    	this.energy = Integer.valueOf(resources.getString("actualFormat").replace(".", ""));
			
		}catch(Exception ex){
			return;
		}		
	}	
	
	public void setShortInfo(String shortinfo){
		Log.i("parse planet info for " + this.name, shortinfo);
		int start = shortinfo.indexOf("[");
		int end = shortinfo.indexOf("]", start) + 1;
		this.coordinates = shortinfo.substring(start, end);
		start = shortinfo.indexOf("(") + 1;
		end = shortinfo.indexOf(")", start);
		String tmp[] = shortinfo.substring(start, end).split("/");
		this.fields_used = Integer.valueOf(tmp[0]);
		this.fields_max = Integer.valueOf(tmp[1]);
		this.shortInfo = shortinfo;
	}
	
	private long getTimeFromLastAjaxCall() {
		return SystemClock.elapsedRealtime() - timeLastAjaxCall;
	}
	
	public String getResources(){
		return "M: " + getMetal() + " K: " + getCrystal() + " D: " + getDeuterium() + " E: " + this.getEnergy();
	}
	
	public int getId(){
		return id;
	}
	
	public String getName() {
		return name;
	}

	public String getCoordinates(){
		return this.coordinates;
	}
	
	public int getUsedFields(){
		return this.fields_used;
	}
	
	public int getMaxFields(){
		return this.fields_max;
	}
	
	public String getShortInfo(){
		return this.shortInfo;
	}
	
	public int getMetal() {
		return this.metal + (int) (this.metalProduction * getTimeFromLastAjaxCall() / 1000);
	}

	public int getCrystal() {
		return this.crystal + (int) (this.crystalProduction * getTimeFromLastAjaxCall() / 1000);
	}

	public int getDeuterium() {
		return this.deuterium + (int) (this.deuteriumProduction * getTimeFromLastAjaxCall() / 1000);
	}

	public int getEnergy() {
		return energy;
	}
	
	public void setMoon(Planet moon){
		moon.isMoon(true);
		this.moon = moon;
	}
	
	public boolean hasMoon(){
		return !(this.moon == null);
	}
	
	public Planet getMoon(){
		return this.moon;
	}
	
	public boolean isMoon(){
		return this.is_moon;
	}
	
	public void isMoon(boolean isMoon){
		this.is_moon = isMoon;
	}
	
	public double getProduction(int resource){
		switch(resource){
			case Item.RESOURCE_METAL: return this.metalProduction;
			case Item.RESOURCE_CRYSTAL: return this.crystalProduction;
			case Item.RESOURCE_DEUTERIUM: return this.deuteriumProduction;
			default: return 0;		
		}
	}

	public Document getGlobalTechtree() {
		return globalTechtree;
	}

	public void setGlobalTechtree(Document globalTechtree) {
		this.globalTechtree = globalTechtree;
	}
		
	public int getGalaxy(){
		String parts[] = coordinates.substring(1).split(":"); //cut of leading [ 
		return Integer.valueOf(parts[0]);
	}
	
	public int getSystem(){
		String parts[] = coordinates.substring(1).split(":"); //cut of leading [ 
		return Integer.valueOf(parts[1]);
	}
	
	public int getPosition(){
		String parts[] = coordinates.substring(1, coordinates.length()-1).split(":"); //cut brackets 
		return Integer.valueOf(parts[2]);
	}

	public void setMetalMax(int metalMax) {
		this.metalMax = metalMax;
	}

	public int getMetalMax() {
		return metalMax;
	}

	public void setCrystalMax(int crystalMax) {
		this.crystalMax = crystalMax;
	}

	public int getCrystalMax() {
		return crystalMax;
	}

	public void setDeuteriumMax(int deuteriumMax) {
		this.deuteriumMax = deuteriumMax;
	}

	public int getDeuteriumMax() {
		return deuteriumMax;
	}
}
