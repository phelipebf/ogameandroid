package com.overkill.ogame.game;

import android.content.Context;

public class Ship {
	private int id;
	private String name;
	private int icon;
	private int total;
	private int used;
	
	public Ship(int id, String name, int total, Context context) {
		this.id = id;
		this.name = name;
		this.total = total;
		this.icon = context.getResources().getIdentifier("drawable/supply" + this.id, null, context.getPackageName());
	}

	public void add(int value){
		used = used + value;
		if(used > total){
			used = total;
		}
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getUsed() {
		return used;
	}

	public void setUsed(int used) {
		this.used = used;
	}

	public int getIcon() {
		return icon;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}		
}