package com.overkill.ogame.game;

public class GalaxyPlanet {
	
	private Integer position;
	private boolean emptySlot = true;
	
	private String planetName;
	private String planetActivity;
	private String planetCoords;
	
	//Element moon;
	
	private String debrisMetal;
	private String debrisCrystal;
	private int debrisRecyclersNeeded = 0;
	
	private String playerName;
	private String playerRank;
	
	private String allyName;
	private String allyRank;
	private String allyMembers;
	
	public Integer getPosition() {
		return position;
	}
	public void setPosition(Integer position) {
		this.position = position;
	}	
	public boolean isEmptySlot() {
		return emptySlot;
	}
	public void setEmptySlot(boolean emptySlot) {
		this.emptySlot = emptySlot;
	}
	public String getPlanetName() {
		return planetName;
	}
	public void setPlanetName(String planetName) {
		this.planetName = planetName;
	}
	public String getPlanetActivity() {
		return planetActivity;
	}
	public void setPlanetActivity(String planetActivity) {
		this.planetActivity = planetActivity;
	}
	public String getPlanetCoords() {
		return planetCoords;
	}
	public void setPlanetCoords(String planetCoords) {
		this.planetCoords = planetCoords;
	}
	public String getPlayerName() {
		return playerName;
	}
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	public String getPlayerRank() {
		return playerRank;
	}
	public void setPlayerRank(String playerRank) {
		this.playerRank = playerRank;
	}
	public String getAllyName() {
		return allyName;
	}
	public void setAllyName(String allyName) {
		this.allyName = allyName;
	}
	public String getAllyRank() {
		return allyRank;
	}
	public void setAllyRank(String allyRank) {
		this.allyRank = allyRank;
	}
	public String getAllyMembers() {
		return allyMembers;
	}
	public void setAllyMembers(String allyMembers) {
		this.allyMembers = allyMembers;
	}
	public String getDebrisMetal() {
		return debrisMetal;
	}
	public void setDebrisMetal(String debrisMetal) {
		this.debrisMetal = debrisMetal;
	}
	public String getDebrisCrystal() {
		return debrisCrystal;
	}
	public void setDebrisCrystal(String debrisCrystal) {
		this.debrisCrystal = debrisCrystal;
	}
	public int getDebrisRecyclersNeeded() {
		return debrisRecyclersNeeded;
	}
	public void setDebrisRecyclersNeeded(String debrisRecyclersNeeded) {
		try {
			this.debrisRecyclersNeeded = Integer.parseInt(debrisRecyclersNeeded);
		} catch (NumberFormatException e) {
			this.debrisRecyclersNeeded = 0;
		}
	}
	@Override
	public String toString() {
		return "GalaxyPlanet [allyMembers=" + allyMembers + ", allyName="
				+ allyName + ", allyRank=" + allyRank + ", debrisCrystal="
				+ debrisCrystal + ", debrisMetal=" + debrisMetal
				+ ", debrisRecyclersNeeded=" + debrisRecyclersNeeded
				+ ", planetActivity=" + planetActivity + ", planetCoords="
				+ planetCoords + ", planetName=" + planetName + ", playerName="
				+ playerName + ", playerRank=" + playerRank + ", position="
				+ position + "]";
	}	

}
