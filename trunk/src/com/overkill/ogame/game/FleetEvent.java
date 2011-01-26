package com.overkill.ogame.game;

import android.util.Log;

public class FleetEvent {
	private int mEventID;
	private String mFleet;
	private String mArrivalTime;
	private String mDesc;
	private String mMission;
	private String mOriginName;
	private String mOriginCoords;
	private String mDestName;
	private String mDestCoords;
	
	public FleetEvent(String body, GameClient game){
		mEventID = Integer.valueOf(Tools.between(body, "id=\"eventRow-", "\""));
		String detailsFleet = getLi(body, "detailsFleet").trim();
		detailsFleet = detailsFleet.substring(0, detailsFleet.indexOf(" "));
		mArrivalTime = getLi(body, "arrivalTime");
		mDesc = getLi(body, "descFleet");
		mMission = Tools.between(getLi(body, "missionFleet"), "<span>", "</span>");
		
		mOriginName = getLi(body, "originFleet");
		mOriginCoords = Tools.between(getLi(body, "coordsOrigin"), ">", "</");
		
		mDestName = getLi(body, "destFleet");
		mDestCoords = Tools.between(getLi(body, "destCoords"), ">", "</");
		
		//mFleet = game.get("page=eventListTooltip&ajax=1&eventID=" + String.valueOf(mEventID));
		
		Log.i("FleetEvent", detailsFleet + " ships (" + mMission + ") from " + mOriginName + mOriginCoords + " to " + mDestName + mDestCoords + " arrival " + mArrivalTime);
	}
	
	private String getLi(String body, String key){
		return Tools.between(body, "<li class=\"" + key + "\">", "</li>");
	}
	
	public int getEventId(){
		return mEventID;
	}
	
	public String getOriginName(){
		return mOriginName;
	}
	
	public String getOriginCoord(){
		return mOriginCoords;
	}
	
	public String getDestName(){
		return mDestName;
	}
	
	public String getDestCoord(){
		return mDestCoords;
	}
	
	public String getMission(){
		return mMission;
	}
	
	public String getArrivalTime(){
		return mArrivalTime;
	}
}
