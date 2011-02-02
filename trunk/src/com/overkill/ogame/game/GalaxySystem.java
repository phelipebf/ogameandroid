package com.overkill.ogame.game;

import java.util.HashMap;

public class GalaxySystem {

	private HashMap<Integer, GalaxyPlanet> system = new HashMap<Integer, GalaxyPlanet>();

	public HashMap<Integer, GalaxyPlanet> getSystem() {
		return system;
	}

	public void setSystem(HashMap<Integer, GalaxyPlanet> system) {
		this.system = system;
	}
	
	public GalaxyPlanet getPlanet(int position) {
		return system.get(position);
	}
	
	public void setPlanet(int position, GalaxyPlanet p) {
		system.put(position, p);
	}
	
	public void clearSystem() {
		system = new HashMap<Integer, GalaxyPlanet>();
	}
	
	
}
