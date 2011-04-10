package com.overkill.ogame.game;

import android.util.Log;

public class FleetEvent {
	private int ID;
	private String ships;
	private String arrivalTime;
	private String info;
	private String mission;
	private String originName;
	private String originCoords;
	private String destinationName;
	private String destinationCoords;
	private boolean canCancel;
	private boolean isReturn;
	
	/*public FleetEvent(String body, GameClient game){
		ID = Integer.valueOf(Tools.between(body, "id=\"eventRow-", "\""));
		String detailsFleet = getLi(body, "detailsFleet").trim();
		detailsFleet = detailsFleet.substring(0, detailsFleet.indexOf(" "));
		arrivalTime = getLi(body, "arrivalTime");
		desc = getLi(body, "descFleet");
		mission = Tools.between(getLi(body, "missionFleet"), "<span>", "</span>");
		
		originName = getLi(body, "originFleet");
		originCoords = Tools.between(getLi(body, "coordsOrigin"), ">", "</");
		
		destinationName = getLi(body, "destFleet");
		destinationCoords = Tools.between(getLi(body, "destCoords"), ">", "</");
		
		//mFleet = game.get("page=eventListTooltip&ajax=1&eventID=" + String.valueOf(mEventID));
		
		Log.i("FleetEvent", detailsFleet + " ships (" + mission + ") from " + originName + originCoords + " to " + destName + destCoords + " arrival " + arrivalTime);
	}*/
	
	
	
	public FleetEvent(){
		
	}
	
	public FleetEvent(int ID){
		this.ID = ID;
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

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
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

	public String getDestinationName() {
		return destinationName;
	}

	public void setDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}

	public String getDestinationCoords() {
		return destinationCoords;
	}

	public void setDestinationCoords(String destinationCoords) {
		this.destinationCoords = destinationCoords;
	}

	public boolean canCancel() {
		return canCancel;
	}

	public void setCanCancel(boolean canCancel) {
		this.canCancel = canCancel;
	}

	public boolean isReturn() {
		return isReturn;
	}

	public void setReturn(boolean isReturn) {
		this.isReturn = isReturn;
	}	
}
