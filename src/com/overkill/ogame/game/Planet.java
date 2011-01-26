package com.overkill.ogame.game;

import org.json.JSONObject;
import android.graphics.drawable.Drawable;

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
			JSONObject json = new JSONObject(jsonstring);
			this.metal = this.getvalue(json, "metal");
			this.crystal = this.getvalue(json, "crystal");
			this.deuterium = this.getvalue(json, "deuterium");
			this.energy = this.getvalue(json, "energy");
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
	
	public String getResources(){
		return "M: " + this.metal + " K: " + this.crystal + " D: " + this.deuterium + " E: " + this.energy;
	}

	private int getvalue(JSONObject json, String key){
		try{
	    	JSONObject metal = json.getJSONObject(key);
	    	JSONObject resources = metal.getJSONObject("resources");
			return Integer.valueOf(resources.getString("actualFormat").replace(".", ""));
		}catch(Exception ex){
			return 0;
		}
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
	
	
	
}
