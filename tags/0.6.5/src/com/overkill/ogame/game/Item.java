package com.overkill.ogame.game;

/**
 * ItemIds
 * @author ov3rk1ll
 *
 */
public class Item {
	public static final int RESOURCE_METAL = 1;
	public static final int RESOURCE_CRYSTAL = 2;
	public static final int RESOURCE_DEUTERIUM = 3;
	public static final int RESOURCE_ENERGY = 4;
	
	public static final int BUIDLING_METAL = 1;
	public static final int BUIDLING_CRYSTAL = 2;
	public static final int BUIDLING_DEUTERIUM = 3;
	public static final int BUIDLING_SOLAR = 4;
	public static final int BUIDLING_FUSION = 12;
	
	public static final int BUIDLING_STORE_METAL = 22;
	public static final int BUIDLING_STORE_CRYSTAL = 23;
	public static final int BUIDLING_STORE_DEUTERIUM = 24;
	
	public static final int BUIDLING_ROBOTER = 14;	
	public static final int BUIDLING_WERFT = 21;	
	public static final int BUIDLING_LABOR = 31;
	public static final int BUIDLING_RAKETENSILO = 44;
	public static final int BUIDLING_NANITENFABRIK = 15;
	public static final int BUIDLING_TERRAFORMER = 33;
	public static final int BUIDLING_ALIANZDEPOT = 34;
	
	public static final int MOON_BASIS = 41;
	public static final int MOON_PHALANX = 42;
	public static final int MOON_SPRUNGTOR = 43;
	
	public static final int RESEARCH_ENERGIE = 113;
	public static final int RESEARCH_LASER = 120;
	public static final int RESEARCH_IONEN = 121;
	public static final int RESEARCH_PLASMA = 122;
	public static final int RESEARCH_HYPERRAUM = 114;
	public static final int RESEARCH_GRAVITON = 199;
	
	public static final int RESEARCH_SPIONAGE = 106;
	public static final int RESEARCH_COMPUTER = 108;
	public static final int RESEARCH_ASTRO = 124;
	public static final int RESEARCH_NETWORK = 123;
	
	public static final int RESEARCH_VERBRENNUNG = 115;
	public static final int RESEARCH_IMPULS = 117;
	public static final int RESEARCH_HYPERDRIVE = 118;
	
	public static final int RESEARCH_WAFFEN = 109;
	public static final int RESEARCH_SCHILD = 110;
	public static final int RESEARCH_PANZER = 111;
	
	public static final int SHIP_LIGHT = 204;
	public static final int SHIP_HEAVY = 205;
	public static final int SHIP_CRUISER = 206;
	public static final int SHIP_BATTLE_SHIP = 207;
	public static final int SHIP_BATTLE_CRUISER = 215;
	public static final int SHIP_BOMBER = 211;
	public static final int SHIP_DESTROYER = 213;
	public static final int SHIP_DEATHSTAR = 214;
	
	public static final int SHIP_SMALL_TRANS = 202;
	public static final int SHIP_BIG_TRANS = 203;
	public static final int SHIP_COLONY = 208;
	public static final int SHIP_RECYCLER = 209;
	public static final int SHIP_SPY = 210;
	public static final int SHIP_SOLARSAT = 212;
	
	public static final int DEFENSE_ROCKET = 401;
	public static final int DEFENSE_SMALL_LASER = 402;
	public static final int DEFENSE_HEAVY_LASER = 403;
	public static final int DEFENSE_ION = 405;
	public static final int DEFENSE_GAUSS = 404;
	public static final int DEFENSE_PLASMA = 406;
	
	public static final int DEFENSE_SMALL_SHILD = 407;
	public static final int DEFENSE_LARGE_SHILD = 408;

	public static final int DEFENSE_ANTI_ROCKET = 502;
	public static final int DEFENSE_INTERPLANET = 503;
	
	public static final int CUETYPE_BUILDING = 1;	
	public static final int CUETYPE_RESEARCH = 2;	
	public static final int CUETYPE_MULTIPLE = 3;
	
	public static final int[] NEEDS_VALUE = {
			SHIP_LIGHT,
			SHIP_HEAVY,
			SHIP_CRUISER,
			SHIP_BATTLE_SHIP,
			SHIP_BATTLE_CRUISER,
			SHIP_BOMBER,
			SHIP_DESTROYER,
			SHIP_DEATHSTAR,
			SHIP_SMALL_TRANS,
			SHIP_BIG_TRANS,
			SHIP_COLONY,
			SHIP_RECYCLER,
			SHIP_SPY,
			SHIP_SOLARSAT,
			DEFENSE_ROCKET,
			DEFENSE_SMALL_LASER,
			DEFENSE_HEAVY_LASER,
			DEFENSE_ION,
			DEFENSE_GAUSS,
			DEFENSE_PLASMA,
			DEFENSE_ANTI_ROCKET,
			DEFENSE_INTERPLANET
		};
}
