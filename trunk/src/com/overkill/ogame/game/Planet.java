package com.overkill.ogame.game;

import org.json.JSONObject;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;

public class Planet {
	private int id;
	private String name;
	private String shortInfo;
	private String coordinates;
	private int fields_used;
	private int fields_max;
	
	private Drawable image;
	
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
	
	private boolean is_moon;
	
	public Planet(int id, String name, String image) {
		this.id = id;
		this.name = name;
		this.image = Tools.ImageOperations(image);
	}
	
	public Planet(String name, String image, String json) {
		this.name = name;
		this.image = Tools.ImageOperations(image);
		this.parse(json);
	}
	
	public Drawable getIcon(){
		return this.image;
	}
	
	public void parse(String jsonstring){
		try{
			timeLastAjaxCall = SystemClock.elapsedRealtime();
			
			JSONObject json = new JSONObject(jsonstring);

	    	JSONObject object = json.getJSONObject("metal");
	    	JSONObject resources = object.getJSONObject("resources");			
			this.metal = resources.getInt("actual");
			this.metalMax = resources.getInt("max");
			this.metalProduction = resources.getDouble("production");
	    	
			object = json.getJSONObject("crystal");
	    	resources = object.getJSONObject("resources");			
			this.crystal = resources.getInt("actual");
			this.crystalMax = resources.getInt("max");
			this.crystalProduction = resources.getDouble("production");
	    	
			object = json.getJSONObject("deuterium");
	    	resources = object.getJSONObject("resources");			
			this.deuterium = resources.getInt("actual");
			this.deuteriumMax = resources.getInt("max");
			this.deuteriumProduction = resources.getDouble("production");

	    	object = json.getJSONObject("energy");
	    	resources = object.getJSONObject("resources");			
	    	this.energy = Integer.valueOf(resources.getString("actualFormat").replace(".", ""));
			
		}catch(Exception ex){
			return;
		}		
	}	
	
	public void setShortInfo(String shortinfo){
		//Log.i("parse planet info", shortinfo);
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
	
	public String getUpdatedResources(){
		long timeDiff = SystemClock.elapsedRealtime() - timeLastAjaxCall;
		int m = this.metal + (int) (this.metalProduction * timeDiff / 1000);
		int k = this.crystal + (int) (this.crystalProduction * timeDiff / 1000);
		int d = this.deuterium + (int) (this.deuteriumProduction * timeDiff / 1000);
		return "M: " + m + " K: " + k + " D: " + d + " E: " + this.energy;
	}
	
	public String getResources(){
		return "M: " + this.metal + " K: " + this.crystal + " D: " + this.deuterium + " E: " + this.energy;
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
		return metal;
	}

	public int getCrystal() {
		return crystal;
	}

	public int getDeuterium() {
		return deuterium;
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
	
	
	
}
