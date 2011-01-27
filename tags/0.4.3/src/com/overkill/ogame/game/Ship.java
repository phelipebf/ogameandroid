package com.overkill.ogame.game;

import android.content.Context;

public class Ship {
	private int id;
	private String name;
	private int icon;
	private int count;
	private int used;
	
	public Ship(int id, String name, int count, Context context) {
		this.id = id;
		this.name = name;
		this.count = count;
		this.icon = context.getResources().getIdentifier("drawable/supply" + this.id, null, context.getPackageName());
	}

	public void add(){
		used++;
		if(used > count){
			used = count;
		}
	}
	
	public void remove(){
		used--;
		if(used < 0){
			used = 0;
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

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getUsed() {
		return used;
	}

	public void setUsed(int used) {
		this.used = used;
	}
	
	public int getRest(){
		return count - used;
	}

	public int getIcon() {
		return icon;
	}
	
	
}
