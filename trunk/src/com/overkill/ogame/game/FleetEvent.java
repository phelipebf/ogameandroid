package com.overkill.ogame.game;

import android.util.Log;

public class FleetEvent {	
	public static final int MISSION_NONE = 0;
	public static final int MISSION_ATTACK = 1;
	public static final int MISSION_UNION_ATTACK = 2;
	public static final int MISSION_TRANSPORT = 3;
	public static final int MISSION_DEPLOYMENT = 4;
	public static final int MISSION_HOLD = 5;
	public static final int MISSION_ESPIONAGE = 6;
	public static final int MISSION_COLONIZATION = 7;
	public static final int MISSION_HARVEST = 8;
	public static final int MISSION_DESTROY = 9;
	public static final int MISSION_EXPEDITION = 15;
	public static final int MISSION_MISSILE = 16;
	
	public static final int PLANETTYPE_PLANET = 1;
	public static final int PLANETTYPE_DEBRIS = 2;
	public static final int PLANETTYPE_MOON = 3;
	
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
	private int timeLeft;
	
	public FleetEvent(){
		
	}
	
	public FleetEvent(int ID){
		this.ID = ID;
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

	public int getTimeLeft() {
		return timeLeft;
	}

	public void setTimeLeft(int timeLeft) {
		if(timeLeft < 0)
			this.timeLeft = 0;
		else
			this.timeLeft = timeLeft;
	}	
	
	@Override
	public boolean equals(Object o) {
		Log.i("FleetEvent", ".equals()");
		FleetEvent f = (FleetEvent)o;
		return f.getOriginCoords().equals(this.getOriginCoords()) && 
			   f.getDestinationCoords().equals(this.getDestinationCoords()) && 
			   f.getMission().equals(this.getMission()) && 
			   f.getArrivalTime().equals(this.getArrivalTime());
	}
}
