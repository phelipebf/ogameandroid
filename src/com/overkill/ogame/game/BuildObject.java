package com.overkill.ogame.game;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;

/**
 * BuildObject ist ein baubares object
 * @author Stephan
 *
 */
public class BuildObject {
	public static int DISPLAY_TYPE_VALUE = 0;
	public static int DISPLAY_TYPE_ALL = 1;
	public static int DISPLAY_TYPE_HIDE_LEVEL = 2;
	
	private String name;
	private int id;
	private int icon;
	private int level = -1;
	private String status;
	private int timeLeft = 0;
	
	private int metal = 0;
	private int crystal = 0;
	private int deuterium = 0;
	private int energyUse = 0;
	private int energyMax = 0;
	private int percent = 0;
	
	private boolean hasMetal = false;
	private boolean hasCrystal = false;
	private boolean hasDeuterium = false;
	
	private int displayType = DISPLAY_TYPE_VALUE;
	
	//false: menge = 1
	boolean needsValue = false;
	
	public BuildObject(Context context, int id, String name, String status) {
		this.id = id;
		this.name = name;
		this.status = status;
		this.icon = context.getResources().getIdentifier("drawable/supply" + this.id, null, context.getPackageName());
		this.setNeedsValue();
	}
	
	public BuildObject(Context context, int id, String name, String status, int level) {
		this.id = id;
		this.name = name;
		this.status = status;
		if(id == 0)
			this.icon = 0;
		else
			this.icon = context.getResources().getIdentifier("drawable/supply" + this.id, null, context.getPackageName());
		this.level = level;
		this.setNeedsValue();
		this.energyUse = Resources.calc(this.id, this.level + 1, Item.RESOURCE_ENERGY) - Resources.calc(this.id, this.level, Item.RESOURCE_ENERGY);
		if(this.id == Item.RESEARCH_GRAVITON)
			this.energyUse = Resources.calc(this.id, this.level + 1, Item.RESOURCE_ENERGY);
	}

	public void setNeedsValue(){
		needsValue = false;
		for(int i : Item.NEEDS_VALUE){
			if(i == this.id){
				needsValue = true;
				return;
			}
		}
	}
	
	public void setResources(int metall, int crystal, int deuterium){
		this.metal = metall;
		this.crystal = crystal;
		this.deuterium = deuterium;
	}
	
	public int getMax(int curMetall, int curKrisal, int curDeut){
		int max_m = curMetall/metal; 
		int max_k = curKrisal/crystal; 
		int max_d = curDeut/deuterium; 
		return Math.min(Math.min(max_m, max_k), max_d);
	}
	
	public int getMax(Planet planet){
		int max_m = planet.getMetal(); 
		if(metal > 0) max_m = planet.getMetal() / metal; 
		int max_k = planet.getCrystal();
		if(crystal > 0) max_k = planet.getCrystal() / crystal; 
		int max_d = planet.getDeuterium();
		if(deuterium > 0) max_d = planet.getDeuterium() / deuterium; 
		return Math.min(Math.min(max_m, max_k), max_d);
	}
	
	public void setTimeLeft(int sec){
		this.timeLeft = sec;
	}
	
//	public void setTimeLeft(String timestring){
//		this.timeLeft = Tools.str2sec(timestring);
//	}
	
	public String getBuildTime(GameClient game) {
				
		/*int robot = game.getCurrentPlanet().getRoboticsFactoryLevel();
		int nanity = game.getCurrentPlanet().getNaniteFactoryLevel();
		int buildTime = (int) Math.floor(3600 * (this.metal + this.crystal) / (2500 * (robot + 1)) * Math.pow(0.5, nanity));
		return Tools.sec2str(buildTime);*/
		String html = game.get("page=resources&ajax=1&type=" + String.valueOf(this.id));
		html = Tools.between(html, "<span class=\"time\">", "</span>").trim();
		return html;
	}
	
	public String getTechnologyNeeded(GameClient game){
		String ret = "";
		Planet currentPlanet = game.getCurrentPlanet();
		if(currentPlanet.getGlobalTechtree() == null) {
			String html = game.get("page=globalTechtree");
			currentPlanet.setGlobalTechtree(Jsoup.parse(html));
		}
		String imgSelector = "img[src$=tiny_" + String.valueOf(this.id) + ".jpg]";
		Elements images = currentPlanet.getGlobalTechtree().select(imgSelector);
		if(images.size() == 0) {
			ret = "not found";
		} else {
			Element img = images.get(0);
			Element tr = img.parent().parent();
			for (Element li : tr.select("li")) {
				//boolean isRed = "overmark".equals(li.attr("class"));
				ret += li.text() + "\n";
			}
		}
		return ret;
	}
	
	public int getBuildableIn(Planet p){
		int sec_metal = 0;
		if(this.metal - p.getMetal() > 0)
			sec_metal = (int) ((this.metal - p.getMetal()) / p.getProduction(Item.RESOURCE_METAL));
		
		int sec_crystal = 0;
		if(this.crystal - p.getCrystal() > 0)
			sec_crystal = (int) ((this.crystal - p.getCrystal()) / p.getProduction(Item.RESOURCE_CRYSTAL));
		
		int sec_deuterium = 0;
		if(this.deuterium - p.getDeuterium() > 0)
			sec_deuterium = (int) ((this.deuterium - p.getDeuterium()) / p.getProduction(Item.RESOURCE_DEUTERIUM));
		
		return Math.max(sec_metal, Math.max(sec_crystal, sec_deuterium));
	}
	
	public void checkRecources(int metal, int crystal, int deuterium){
		this.hasMetal = false;
		this.hasCrystal = false;
		this.hasDeuterium = false;
		if(metal >= this.metal)
			this.hasMetal = true;
		if(crystal >= this.crystal)
			this.hasCrystal = true;
		if(deuterium >= this.deuterium)
			this.hasDeuterium = true;
	}

	public int countDown(){
		this.timeLeft--;
		if(this.timeLeft < 0)
			this.timeLeft = 0;
		return this.timeLeft;
	}
	
	public String getName() {
		return name;
	}
	
	public int getLevel() {
		return level;
	}

	public String getStatus() {
		return status;
	}
	
	public int getTimeLeft() {
		return timeLeft;
	}
	
	public boolean needsValue() {
		return needsValue;
	}
	
	public int getId() {
		return id;
	}

	public int getIcon() {
		return icon;
	}

	public int getMetall() {
		return metal;
	}

	public int getCrystal() {
		return crystal;
	}

	public int getDeuterium() {
		return deuterium;
	}

	public boolean hasMetal() {
		return hasMetal;
	}

	public boolean hasCrystal() {
		return hasCrystal;
	}

	public boolean hasDeuterium() {
		return hasDeuterium;
	}

	public int getEnergy() {
		return energyUse;
	}

	public void setEnergy(int energyUse) {
		this.energyUse = energyUse;
	}

	public int getEnergyMax() {
		return energyMax;
	}

	public void setEnergyMax(int energyMax) {
		this.energyMax = energyMax;
	}

	public int getPercent() {
		return percent;
	}

	public void setPercent(int percent) {
		this.percent = percent;
	}
	
	public boolean canBuild(){
		return this.hasMetal && this.hasCrystal && this.hasDeuterium;
	}
	
	public void setStatus(String status){
		this.status = status;
	}

	public void setDisplayType(int displayType) {
		this.displayType = displayType;
	}

	public int getDisplayType() {
		return displayType;
	}
}
