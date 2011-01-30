package com.overkill.ogame.game;

public class Resources {
	
	/**
	 * Calculate the resources needed for the requested level of a building
	 * @param building
	 * @param level
	 * @param resource
	 * @return
	 */
	public static int calc(int building, int level, int resource){
		switch(building){
			case Item.BUIDLING_METAL:
				switch(resource){
					case Item.RESOURCE_METAL: return (int) Math.ceil(60 * Math.pow(1.5, level));
					case Item.RESOURCE_CRYSTAL: return (int) Math.ceil(15 * Math.pow(1.5, level));
					case Item.RESOURCE_DEUTERIUM: return 0;
					case Item.RESOURCE_ENERGY: return -(int) Math.round(10 * level * Math.pow(1.1, level));
				}
			case Item.BUIDLING_CRYSTAL:
				switch(resource){
					case Item.RESOURCE_METAL: return (int) Math.ceil(48 * Math.pow(1.6, level));
					case Item.RESOURCE_CRYSTAL: return (int) Math.ceil(24 * Math.pow(1.6, level));
					case Item.RESOURCE_DEUTERIUM: return 0;
					case Item.RESOURCE_ENERGY: return -(int) Math.round(10 * level * Math.pow(1.1, level));
				}
			case Item.BUIDLING_DEUTERIUM:
				switch(resource){
					case Item.RESOURCE_METAL: return (int) Math.ceil(225 * Math.pow(1.5, level));
					case Item.RESOURCE_CRYSTAL: return (int) Math.ceil(75 * Math.pow(1.5, level));
					case Item.RESOURCE_DEUTERIUM: return 0;
					case Item.RESOURCE_ENERGY: return -(int) Math.round(20 * level * Math.pow(1.1, level));
				}
			case Item.BUIDLING_STORE_METAL:
				switch(resource){
					case Item.RESOURCE_METAL: return (int) Math.ceil(1000 * Math.pow(2, level));
					case Item.RESOURCE_CRYSTAL: return (int) Math.ceil(0 * Math.pow(2, level));
					case Item.RESOURCE_DEUTERIUM: return (int) Math.ceil(0 * Math.pow(2, level));
					case Item.RESOURCE_ENERGY: return 0;
				}
			case Item.BUIDLING_STORE_CRYSTAL:
				switch(resource){
				case Item.RESOURCE_METAL: return (int) Math.ceil(1000 * Math.pow(2, level));
				case Item.RESOURCE_CRYSTAL: return (int) Math.ceil(500 * Math.pow(2, level));
				case Item.RESOURCE_DEUTERIUM: return (int) Math.ceil(0 * Math.pow(2, level));
				case Item.RESOURCE_ENERGY: return 0;
				}
			case Item.BUIDLING_STORE_DEUTERIUM:
				switch(resource){
				case Item.RESOURCE_METAL: return (int) Math.ceil(1000 * Math.pow(2, level));
				case Item.RESOURCE_CRYSTAL: return (int) Math.ceil(1000 * Math.pow(2, level));
				case Item.RESOURCE_DEUTERIUM: return (int) Math.ceil(0 * Math.pow(2, level));
				case Item.RESOURCE_ENERGY: return 0;
				}
			case Item.BUIDLING_SOLAR:
				switch(resource){
					case Item.RESOURCE_METAL: return (int) Math.ceil(75 * (Math.pow(1.5, level)));
					case Item.RESOURCE_CRYSTAL: return (int) Math.ceil(30 * Math.pow(1.5, level));
					case Item.RESOURCE_DEUTERIUM: return 0;
					case Item.RESOURCE_ENERGY: return (int) Math.ceil(20 * level * Math.pow(1.1, level));
					}
			case Item.BUIDLING_FUSION:
				switch(resource){
					case Item.RESOURCE_METAL: return (int) Math.ceil(900 * Math.pow(1.8, level));
					case Item.RESOURCE_CRYSTAL: return (int) Math.ceil(360 * Math.pow(1.8, level));
					case Item.RESOURCE_DEUTERIUM: return (int) Math.ceil(180 * Math.pow(1.8, level));
					case Item.RESOURCE_ENERGY: return 0;
				}
			case Item.BUIDLING_ROBOTER:
				switch(resource){
					case Item.RESOURCE_METAL: return (int) Math.ceil(400 * (Math.pow(2, level)));
					case Item.RESOURCE_CRYSTAL: return (int) Math.ceil(120 * Math.pow(2, level));
					case Item.RESOURCE_DEUTERIUM: return (int) Math.ceil(200 * Math.pow(2, level));
					case Item.RESOURCE_ENERGY: return 0;
				}
			case Item.BUIDLING_WERFT:
				switch(resource){
					case Item.RESOURCE_METAL: return (int) Math.ceil(400 * (Math.pow(2, level)));
					case Item.RESOURCE_CRYSTAL: return (int) Math.ceil(200 * Math.pow(2, level));
					case Item.RESOURCE_DEUTERIUM: return (int) Math.ceil(100 * Math.pow(2, level));
					case Item.RESOURCE_ENERGY: return 0;
				}
			case Item.BUIDLING_LABOR:
				switch(resource){
					case Item.RESOURCE_METAL: return (int) Math.ceil(200 * (Math.pow(2, level)));
					case Item.RESOURCE_CRYSTAL: return (int) Math.ceil(400 * Math.pow(2, level));
					case Item.RESOURCE_DEUTERIUM: return (int) Math.ceil(200 * Math.pow(2, level));
					case Item.RESOURCE_ENERGY: return 0;
				}
			case Item.BUIDLING_RAKETENSILO:
				switch(resource){
					case Item.RESOURCE_METAL: return (int) Math.ceil(20000 * (Math.pow(2, level)));
					case Item.RESOURCE_CRYSTAL: return (int) Math.ceil(20000 * Math.pow(2, level));
					case Item.RESOURCE_DEUTERIUM: return (int) Math.ceil(1000 * Math.pow(2, level));
					case Item.RESOURCE_ENERGY: return 0;
				}
			case Item.BUIDLING_NANITENFABRIK:
				switch(resource){
					case Item.RESOURCE_METAL: return (int) Math.ceil(1000000 * (Math.pow(2, level)));
					case Item.RESOURCE_CRYSTAL: return (int) Math.ceil(500000 * Math.pow(2, level));
					case Item.RESOURCE_DEUTERIUM: return (int) Math.ceil(100000 * Math.pow(2, level));
					case Item.RESOURCE_ENERGY: return 0;
				}
			case Item.BUIDLING_TERRAFORMER:
				switch(resource){
					case Item.RESOURCE_METAL: return (int) Math.ceil(0 * Math.pow(2, level));
					case Item.RESOURCE_CRYSTAL: return (int) Math.ceil(50000 * Math.pow(2, level));
					case Item.RESOURCE_DEUTERIUM: return (int) Math.ceil(100000 * Math.pow(2, level));
					case Item.RESOURCE_ENERGY: return 0;
				}
			case Item.BUIDLING_ALIANZDEPOT:
				switch(resource){
					case Item.RESOURCE_METAL: return (int) Math.ceil(20000 * (Math.pow(2, level)));
					case Item.RESOURCE_CRYSTAL: return (int) Math.ceil(40000 * Math.pow(2, level));
					case Item.RESOURCE_DEUTERIUM: return (int) Math.ceil(0 * Math.pow(2, level));
					case Item.RESOURCE_ENERGY: return 0;
				}
				//--------------------------------------
			case Item.MOON_BASIS:
				switch(resource){
					case Item.RESOURCE_METAL: return (int) Math.ceil(20000 * (Math.pow(2, level)));
					case Item.RESOURCE_CRYSTAL: return (int) Math.ceil(40000 * Math.pow(2, level));
					case Item.RESOURCE_DEUTERIUM: return (int) Math.ceil(20000 * Math.pow(2, level));
					case Item.RESOURCE_ENERGY: return 0;
				}
			case Item.MOON_PHALANX:
				switch(resource){
					case Item.RESOURCE_METAL: return (int) Math.ceil(20000 * (Math.pow(2, level)));
					case Item.RESOURCE_CRYSTAL: return (int) Math.ceil(40000 * Math.pow(2, level));
					case Item.RESOURCE_DEUTERIUM: return (int) Math.ceil(20000 * Math.pow(2, level));
					case Item.RESOURCE_ENERGY: return 0;
				}
			case Item.MOON_SPRUNGTOR:
				switch(resource){
					case Item.RESOURCE_METAL: return (int) Math.ceil(2000000 * (Math.pow(2, level)));
					case Item.RESOURCE_CRYSTAL: return (int) Math.ceil(4000000 * Math.pow(2, level));
					case Item.RESOURCE_DEUTERIUM: return (int) Math.ceil(2000000 * Math.pow(2, level));
					case Item.RESOURCE_ENERGY: return 0;
				}
				//--------------------------------------
			case Item.RESEARCH_ENERGIE:
				switch(resource){
					case Item.RESOURCE_METAL: return (int) Math.ceil(0 * (Math.pow(2, level)));
					case Item.RESOURCE_CRYSTAL: return (int) Math.ceil(800 * Math.pow(2, level));
					case Item.RESOURCE_DEUTERIUM: return (int) Math.ceil(400 * Math.pow(2, level));
					case Item.RESOURCE_ENERGY: return 0;
				}
			case Item.RESEARCH_LASER:
				switch(resource){
					case Item.RESOURCE_METAL: return (int) Math.ceil(200 * (Math.pow(2, level)));
					case Item.RESOURCE_CRYSTAL: return (int) Math.ceil(100 * Math.pow(2, level));
					case Item.RESOURCE_DEUTERIUM: return (int) Math.ceil(0 * Math.pow(2, level));
					case Item.RESOURCE_ENERGY: return 0;
				}
			case Item.RESEARCH_IONEN:
				switch(resource){
					case Item.RESOURCE_METAL: return (int) Math.ceil(1000 * (Math.pow(2, level)));
					case Item.RESOURCE_CRYSTAL: return (int) Math.ceil(300 * Math.pow(2, level));
					case Item.RESOURCE_DEUTERIUM: return (int) Math.ceil(100 * Math.pow(2, level));
					case Item.RESOURCE_ENERGY: return 0;
				}
			case Item.RESEARCH_PLASMA:
				switch(resource){
					case Item.RESOURCE_METAL: return (int) Math.ceil(2000 * (Math.pow(2, level)));
					case Item.RESOURCE_CRYSTAL: return (int) Math.ceil(4000 * Math.pow(2, level));
					case Item.RESOURCE_DEUTERIUM: return (int) Math.ceil(1000 * Math.pow(2, level));
					case Item.RESOURCE_ENERGY: return 0;
				}
			case Item.RESEARCH_HYPERRAUM:
				switch(resource){
					case Item.RESOURCE_METAL: return (int) Math.ceil(0 * (Math.pow(2, level)));
					case Item.RESOURCE_CRYSTAL: return (int) Math.ceil(4000 * Math.pow(2, level));
					case Item.RESOURCE_DEUTERIUM: return (int) Math.ceil(2000 * Math.pow(2, level));
					case Item.RESOURCE_ENERGY: return 0;
				}
			case Item.RESEARCH_GRAVITON:
				switch(resource){
					case Item.RESOURCE_METAL: return (int) Math.ceil(0 * (Math.pow(2, level)));
					case Item.RESOURCE_CRYSTAL: return (int) Math.ceil(0 * Math.pow(2, level));
					case Item.RESOURCE_DEUTERIUM: return (int) Math.ceil(0 * Math.pow(2, level));
					case Item.RESOURCE_ENERGY: return -(int) Math.ceil(100000 * Math.pow(3, level));
				}
			case Item.RESEARCH_SPIONAGE:
				switch(resource){
					case Item.RESOURCE_METAL: return (int) Math.ceil(200 * (Math.pow(2, level)));
					case Item.RESOURCE_CRYSTAL: return (int) Math.ceil(1000 * Math.pow(2, level));
					case Item.RESOURCE_DEUTERIUM: return (int) Math.ceil(200 * Math.pow(2, level));
					case Item.RESOURCE_ENERGY: return 0;
				}
			case Item.RESEARCH_COMPUTER:
				switch(resource){
					case Item.RESOURCE_METAL: return (int) Math.ceil(0 * (Math.pow(2, level)));
					case Item.RESOURCE_CRYSTAL: return (int) Math.ceil(400 * Math.pow(2, level));
					case Item.RESOURCE_DEUTERIUM: return (int) Math.ceil(600 * Math.pow(2, level));
					case Item.RESOURCE_ENERGY: return 0;
				}
			case Item.RESEARCH_ASTRO:
				switch(resource){
					case Item.RESOURCE_METAL: return (int) Math.ceil(4000 * (Math.pow(1.75, level)));
					case Item.RESOURCE_CRYSTAL: return (int) Math.ceil(8000 * Math.pow(1.75, level));
					case Item.RESOURCE_DEUTERIUM: return (int) Math.ceil(4000 * Math.pow(1.75, level));
					case Item.RESOURCE_ENERGY: return 0;
				}
			case Item.RESEARCH_NETWORK:
				switch(resource){
					case Item.RESOURCE_METAL: return (int) Math.ceil(240000 * (Math.pow(2, level)));
					case Item.RESOURCE_CRYSTAL: return (int) Math.ceil(400000 * Math.pow(2, level));
					case Item.RESOURCE_DEUTERIUM: return (int) Math.ceil(160000 * Math.pow(2, level));
					case Item.RESOURCE_ENERGY: return 0;
				}
			case Item.RESEARCH_VERBRENNUNG:
				switch(resource){
					case Item.RESOURCE_METAL: return (int) Math.ceil(400 * (Math.pow(2, level)));
					case Item.RESOURCE_CRYSTAL: return (int) Math.ceil(0 * Math.pow(2, level));
					case Item.RESOURCE_DEUTERIUM: return (int) Math.ceil(600 * Math.pow(2, level));
					case Item.RESOURCE_ENERGY: return 0;
				}
			case Item.RESEARCH_IMPULS:
				switch(resource){
					case Item.RESOURCE_METAL: return (int) Math.ceil(2000 * (Math.pow(2, level)));
					case Item.RESOURCE_CRYSTAL: return (int) Math.ceil(4000 * Math.pow(2, level));
					case Item.RESOURCE_DEUTERIUM: return (int) Math.ceil(600 * Math.pow(2, level));
					case Item.RESOURCE_ENERGY: return 0;
				}
			case Item.RESEARCH_HYPERDRIVE:
				switch(resource){
					case Item.RESOURCE_METAL: return (int) Math.ceil(10000 * (Math.pow(2, level)));
					case Item.RESOURCE_CRYSTAL: return (int) Math.ceil(20000 * Math.pow(2, level));
					case Item.RESOURCE_DEUTERIUM: return (int) Math.ceil(6000 * Math.pow(2, level));
					case Item.RESOURCE_ENERGY: return 0;
				}
			case Item.RESEARCH_WAFFEN:
				switch(resource){
					case Item.RESOURCE_METAL: return (int) Math.ceil(800 * (Math.pow(2, level)));
					case Item.RESOURCE_CRYSTAL: return (int) Math.ceil(200 * Math.pow(2, level));
					case Item.RESOURCE_DEUTERIUM: return (int) Math.ceil(0 * Math.pow(2, level));
					case Item.RESOURCE_ENERGY: return 0;
				}
			case Item.RESEARCH_SCHILD:
				switch(resource){
					case Item.RESOURCE_METAL: return (int) Math.ceil(200 * (Math.pow(2, level)));
					case Item.RESOURCE_CRYSTAL: return (int) Math.ceil(600 * Math.pow(2, level));
					case Item.RESOURCE_DEUTERIUM: return (int) Math.ceil(0 * Math.pow(2, level));
					case Item.RESOURCE_ENERGY: return 0;
				}
			case Item.RESEARCH_PANZER:
				switch(resource){
					case Item.RESOURCE_METAL: return (int) Math.ceil(1000 * (Math.pow(2, level)));
					case Item.RESOURCE_CRYSTAL: return (int) Math.ceil(0 * Math.pow(2, level));
					case Item.RESOURCE_DEUTERIUM: return (int) Math.ceil(0 * Math.pow(2, level));
					case Item.RESOURCE_ENERGY: return 0;
				}
				//--------------------------
			case Item.SHIP_LIGHT:
				switch(resource){
					case Item.RESOURCE_METAL: return 3000;
					case Item.RESOURCE_CRYSTAL: return 1000;
					case Item.RESOURCE_DEUTERIUM: return 0;
					case Item.RESOURCE_ENERGY: return 0;
				}
			case Item.SHIP_HEAVY:
				switch(resource){
					case Item.RESOURCE_METAL: return 6000;
					case Item.RESOURCE_CRYSTAL: return 4000;
					case Item.RESOURCE_DEUTERIUM: return 0;
					case Item.RESOURCE_ENERGY: return 0;
				}
			case Item.SHIP_CRUISER:
				switch(resource){
					case Item.RESOURCE_METAL: return 20000;
					case Item.RESOURCE_CRYSTAL: return 7000;
					case Item.RESOURCE_DEUTERIUM: return 2000;
					case Item.RESOURCE_ENERGY: return 0;
				}
			case Item.SHIP_BATTLE_SHIP:
				switch(resource){
					case Item.RESOURCE_METAL: return 45000;
					case Item.RESOURCE_CRYSTAL: return 15000;
					case Item.RESOURCE_DEUTERIUM: return 0;
					case Item.RESOURCE_ENERGY: return 0;
				}
			case Item.SHIP_BATTLE_CRUISER:
				switch(resource){
					case Item.RESOURCE_METAL: return 30000;
					case Item.RESOURCE_CRYSTAL: return 40000;
					case Item.RESOURCE_DEUTERIUM: return 15000;
					case Item.RESOURCE_ENERGY: return 0;
				}
			case Item.SHIP_BOMBER:
				switch(resource){
					case Item.RESOURCE_METAL: return 50000;
					case Item.RESOURCE_CRYSTAL: return 25000;
					case Item.RESOURCE_DEUTERIUM: return 15000;
					case Item.RESOURCE_ENERGY: return 0;
				}
			case Item.SHIP_DESTROYER:
				switch(resource){
					case Item.RESOURCE_METAL: return 60000;
					case Item.RESOURCE_CRYSTAL: return 50000;
					case Item.RESOURCE_DEUTERIUM: return 15000;
					case Item.RESOURCE_ENERGY: return 0;
				}
			case Item.SHIP_DEATHSTAR:
				switch(resource){
					case Item.RESOURCE_METAL: return 5000000;
					case Item.RESOURCE_CRYSTAL: return 4000000;
					case Item.RESOURCE_DEUTERIUM: return 1000000;
					case Item.RESOURCE_ENERGY: return 0;
				}
			case Item.SHIP_SMALL_TRANS:
				switch(resource){
					case Item.RESOURCE_METAL: return 2000;
					case Item.RESOURCE_CRYSTAL: return 2000;
					case Item.RESOURCE_DEUTERIUM: return 0;
					case Item.RESOURCE_ENERGY: return 0;
				}
			case Item.SHIP_BIG_TRANS:
				switch(resource){
					case Item.RESOURCE_METAL: return 6000;
					case Item.RESOURCE_CRYSTAL: return 6000;
					case Item.RESOURCE_DEUTERIUM: return 0;
					case Item.RESOURCE_ENERGY: return 0;
				}
			case Item.SHIP_COLONY:
				switch(resource){
					case Item.RESOURCE_METAL: return 10000;
					case Item.RESOURCE_CRYSTAL: return 20000;
					case Item.RESOURCE_DEUTERIUM: return 10000;
					case Item.RESOURCE_ENERGY: return 0;
				}
			case Item.SHIP_RECYCLER:
				switch(resource){
					case Item.RESOURCE_METAL: return 10000;
					case Item.RESOURCE_CRYSTAL: return 6000;
					case Item.RESOURCE_DEUTERIUM: return 2000;
					case Item.RESOURCE_ENERGY: return 0;
				}
			case Item.SHIP_SPY:
				switch(resource){
					case Item.RESOURCE_METAL: return 0;
					case Item.RESOURCE_CRYSTAL: return 1000;
					case Item.RESOURCE_DEUTERIUM: return 0;
					case Item.RESOURCE_ENERGY: return 0;
				}
			case Item.SHIP_SOLARSAT:
				switch(resource){
					case Item.RESOURCE_METAL: return 0;
					case Item.RESOURCE_CRYSTAL: return 2000;
					case Item.RESOURCE_DEUTERIUM: return 500;
					case Item.RESOURCE_ENERGY: return 0;
				}
				//--------------------------	
			case Item.DEFENSE_ROCKET:
				switch(resource){
					case Item.RESOURCE_METAL: return 2000;
					case Item.RESOURCE_CRYSTAL: return 0;
					case Item.RESOURCE_DEUTERIUM: return 0;
					case Item.RESOURCE_ENERGY: return 0;
				}
			case Item.DEFENSE_SMALL_LASER:
				switch(resource){
					case Item.RESOURCE_METAL: return 1500;
					case Item.RESOURCE_CRYSTAL: return 500;
					case Item.RESOURCE_DEUTERIUM: return 0;
					case Item.RESOURCE_ENERGY: return 0;
				}
			case Item.DEFENSE_HEAVY_LASER:
				switch(resource){
					case Item.RESOURCE_METAL: return 6000;
					case Item.RESOURCE_CRYSTAL: return 2000;
					case Item.RESOURCE_DEUTERIUM: return 0;
					case Item.RESOURCE_ENERGY: return 0;
				}
			case Item.DEFENSE_ION:
				switch(resource){
					case Item.RESOURCE_METAL: return 2000;
					case Item.RESOURCE_CRYSTAL: return 6000;
					case Item.RESOURCE_DEUTERIUM: return 0;
					case Item.RESOURCE_ENERGY: return 0;
				}
			case Item.DEFENSE_GAUSS:
				switch(resource){
					case Item.RESOURCE_METAL: return 20000;
					case Item.RESOURCE_CRYSTAL: return 15000;
					case Item.RESOURCE_DEUTERIUM: return 2000;
					case Item.RESOURCE_ENERGY: return 0;
				}
			case Item.DEFENSE_PLASMA:
				switch(resource){
					case Item.RESOURCE_METAL: return 50000;
					case Item.RESOURCE_CRYSTAL: return 50000;
					case Item.RESOURCE_DEUTERIUM: return 30000;
					case Item.RESOURCE_ENERGY: return 0;
				}
			case Item.DEFENSE_SMALL_SHILD:
				switch(resource){
					case Item.RESOURCE_METAL: return 10000;
					case Item.RESOURCE_CRYSTAL: return 10000;
					case Item.RESOURCE_DEUTERIUM: return 0;
					case Item.RESOURCE_ENERGY: return 0;
				}
			case Item.DEFENSE_LARGE_SHILD:
				switch(resource){
					case Item.RESOURCE_METAL: return 50000;
					case Item.RESOURCE_CRYSTAL: return 50000;
					case Item.RESOURCE_DEUTERIUM: return 0;
					case Item.RESOURCE_ENERGY: return 0;
				}
			case Item.DEFENSE_ANTI_ROCKET:
				switch(resource){
					case Item.RESOURCE_METAL: return 8000;
					case Item.RESOURCE_CRYSTAL: return 0;
					case Item.RESOURCE_DEUTERIUM: return 2000;
					case Item.RESOURCE_ENERGY: return 0;
				}
			case Item.DEFENSE_INTERPLANET:
				switch(resource){
					case Item.RESOURCE_METAL: return 12500;
					case Item.RESOURCE_CRYSTAL: return 2500;
					case Item.RESOURCE_DEUTERIUM: return 10000;
					case Item.RESOURCE_ENERGY: return 0;
				}
		}
		return 0;
	}
}
