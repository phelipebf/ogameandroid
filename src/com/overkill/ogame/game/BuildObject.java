package com.overkill.ogame.game;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;

/**
 * BuildObject ist ein baubares object
 * @author Stephan
 *
 */
public class BuildObject {
	private String name;
	private int id;
	private int icon;
	private int level = -1;
	private String status;
	private int timeLeft = 0;
	
	private int metall = 0;
	private int kristal = 0;
	private int deuterium = 0;
	
	private boolean hasMetall = false;
	private boolean hasKristal = false;
	private boolean hasDeuterium = false;
	
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
		this.icon = context.getResources().getIdentifier("drawable/supply" + this.id, null, context.getPackageName());
		this.level = level;
		this.setNeedsValue();
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
	
	public void setRecources(int metall, int kristal, int deuterium){
		this.metall = metall;
		this.kristal = kristal;
		this.deuterium = deuterium;
	}
	
	public int getMax(int curMetall, int curKrisal, int curDeut){
		int max_m = curMetall/metall; 
		int max_k = curKrisal/kristal; 
		int max_d = curDeut/deuterium; 
		return Math.min(Math.min(max_m, max_k), max_d);
	}
	
	public int getMax(Planet planet){
		int max_m = planet.getMetal(); 
		if(metall > 0) max_m = planet.getMetal() / metall; 
		int max_k = planet.getCrystal();
		if(kristal > 0) max_k = planet.getCrystal() / kristal; 
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
	
	public String getBuildTime(GameClient game){
		String html = game.get("page=resources&ajax=1&type=" + String.valueOf(this.id));
		html = Tools.between(html, "<span class=\"time\">", "</span>").trim();
		return html;
	}
	
	public String getTechTree(GameClient game){
		String ret = "";
		String html = game.get("page=globalTechtree");
		Document doc = Jsoup.parse(html);
		String imgSelector = "img[src$=tiny_" + String.valueOf(this.id) + ".jpg]";
		Elements images = doc.select(imgSelector);
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
	
	public void checkRecources(int metall, int kristal, int deuterium){
		this.hasMetall = false;
		this.hasKristal = false;
		this.hasDeuterium = false;
		if(metall >= this.metall)
			this.hasMetall = true;
		if(kristal >= this.kristal)
			this.hasKristal = true;
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
		return metall;
	}

	public int getKristal() {
		return kristal;
	}

	public int getDeuterium() {
		return deuterium;
	}

	public boolean hasMetall() {
		return hasMetall;
	}

	public boolean hasKristal() {
		return hasKristal;
	}

	public boolean hasDeuterium() {
		return hasDeuterium;
	}
	
	
}
