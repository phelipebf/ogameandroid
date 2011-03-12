package com.overkill.ogame.game;

import java.io.Serializable;

public class Player implements Serializable {
	private static final long serialVersionUID = 8667111822267022911L;
	
	int playerID;
	String playerName;
	int allianceID;
	String allianceName;
	
	public Player(int playerID){
		this.playerID = playerID;
	}
	
	public Player(int playerID, String playerName) {
		this.playerID = playerID;
		this.playerName = playerName;
	}

	public int getPlayerID() {
		return playerID;
	}

	public void setPlayerID(int playerID) {
		this.playerID = playerID;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public int getAllianceID() {
		return allianceID;
	}

	public void setAllianceID(int allianceID) {
		this.allianceID = allianceID;
	}

	public String getAllianceName() {
		return allianceName;
	}

	public void setAllianceName(String allianceName) {
		this.allianceName = allianceName;
	}	
}
