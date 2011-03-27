package com.overkill.ogame.game;

import android.util.Log;

public class FleetEvent {
	private int ID;
	private String ships;
	private String arrivalTime;
	private String desc;
	private String mission;
	private String originName;
	private String originCoords;
	private String destName;
	private String destCoords;
	
	public FleetEvent(String body, GameClient game){
		ID = Integer.valueOf(Tools.between(body, "id=\"eventRow-", "\""));
		String detailsFleet = getLi(body, "detailsFleet").trim();
		detailsFleet = detailsFleet.substring(0, detailsFleet.indexOf(" "));
		arrivalTime = getLi(body, "arrivalTime");
		desc = getLi(body, "descFleet");
		mission = Tools.between(getLi(body, "missionFleet"), "<span>", "</span>");
		
		originName = getLi(body, "originFleet");
		originCoords = Tools.between(getLi(body, "coordsOrigin"), ">", "</");
		
		destName = getLi(body, "destFleet");
		destCoords = Tools.between(getLi(body, "destCoords"), ">", "</");
		
		//mFleet = game.get("page=eventListTooltip&ajax=1&eventID=" + String.valueOf(mEventID));
		
		Log.i("FleetEvent", detailsFleet + " ships (" + mission + ") from " + originName + originCoords + " to " + destName + destCoords + " arrival " + arrivalTime);
	}
	
	private String getLi(String body, String key){
		return Tools.between(body, "<li class=\"" + key + "\">", "</li>");
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getShips() {
		return ships;
	}

	public void setShips(String ships) {
		this.ships = ships;
	}

	public String getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(String arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getMission() {
		return mission;
	}

	public void setMission(String mission) {
		this.mission = mission;
	}

	public String getOriginName() {
		return originName;
	}

	public void setOriginName(String originName) {
		this.originName = originName;
	}

	public String getOriginCoords() {
		return originCoords;
	}

	public void setOriginCoords(String originCoords) {
		this.originCoords = originCoords;
	}

	public String getDestName() {
		return destName;
	}

	public void setDestName(String destName) {
		this.destName = destName;
	}

	public String getDestCoords() {
		return destCoords;
	}

	public void setDestCoords(String destCoords) {
		this.destCoords = destCoords;
	}
	
	
}
