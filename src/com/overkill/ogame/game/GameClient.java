package com.overkill.ogame.game;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.flurry.android.FlurryAgent;
import com.overkill.ogame.R;

import android.content.Context;
import android.util.Log;

/**
 * Controller für interaktion mit ogame server
 * @author Stephan
 *
 */
public class GameClient{
	public static final String TAG = "ogame-core";
	private final boolean D = true;
	private final String USER_AGENT = "Mozilla/5.0 (Linux; U; Android 2.2.1; en-us; generic) AppleWebKit/530.17 (KHTML, like Gecko) Version/4.0 Mobile Safari/530.17";
	
	//
	Context context;
	//Client to hold login cookies
	private DefaultHttpClient http = new DefaultHttpClient();
	//session parameter for url (s)
	private String session;
	
	//Login information
	private String universe;
	private String username;
	private String password;
	
	//Server path
	private String indexbase;
	private String imagebase;
	
	private ArrayList<Planet> planets;	
	private Planet current_planet;
	
	//Server Specific Data
	private String moon_regex = "";
	
	/**
	 * Creates a new Game object
	 * @param http The {DefaultHttpClient} containing cookies from login
	 * @param session The session-id passed on in the url
	 * @param base The base of the url, containing univers and tld
	 */
	/*public GameClient(Context context, DefaultHttpClient http, String session, String universe) {
		this.context = context;
		this.http = http;
		this.session = session;
		this.indexbase = "http://"  + universe + "/game/index.php?";
		this.imagebase = "http://"  + universe + "/game/";
		this.loadPlanets();
	}*/
	
	public GameClient(Context context){
		this.context = context;
		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		int timeoutConnection = 5000;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		// Set the default socket timeout (SO_TIMEOUT) 
		// in milliseconds which is the timeout for waiting for data.
		int timeoutSocket = 5000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		this.http.setParams(httpParameters);
	}
	
	/**
	 * Creates a Game object from login data
	 * @param universe Logindomain
	 * @param username Username
	 * @param password Password
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public GameClient(String universe, String username, String password) throws ClientProtocolException, IOException{
		this.login(universe, username, password);		
	}
	
	/**
	 * Tries to login with the given information and returns true or false if the attempt succeeds or fails 
	 * @param universe Logindomain
	 * @param username Username
	 * @param password Password
	 * @return true or false if the login-attempt succeeds or fails
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public boolean login(String universe, String username, String password) throws ClientProtocolException, IOException{
		http.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);
		HttpPost httppost = new HttpPost("http://" + universe + "/game/reg/login2.php");
		httppost.addHeader("User-Agent", USER_AGENT);
		if(D) Log.i(TAG, "Login at " + httppost.getURI().toString());		
		//build login post data
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("v", "2"));
        nameValuePairs.add(new BasicNameValuePair("is_utf8", "0"));
        nameValuePairs.add(new BasicNameValuePair("login", username));
        nameValuePairs.add(new BasicNameValuePair("pass", password));
        nameValuePairs.add(new BasicNameValuePair("submit", "Einloggen"));
        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        //execute request
        HttpResponse response = http.execute(httppost);		
        if(response == null){
        	// Request timed out
        	return false;
        }
        //read the target location (HTTP 302)
       	String state = response.getHeaders("Location")[0].getValue();
       	if(state.contains("error")){
       		return false;
       	}else{
       		this.session = Tools.between(state, "session=", "&");
       		this.username = username;
       		this.password = password;
       		this.universe = universe;
    		this.imagebase = "http://"  + this.universe + "/game/";
    		this.indexbase = this.imagebase + "index.php?";
    		this.moon_regex = Tools.getServerSpecificData(this.context, this.universe.substring(this.universe.indexOf(".") + 1), "moon_regex");    		
    		loadPlanets();
    		return true;
       	}       		
	}
	
	/**
	 * Tries to relogin
	 * @return true or false if the login-attempt succeeds or fails 
	 */
	private boolean login(){
		try {
			return this.login(this.universe, this.username, this.password);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Loads the planetlist and sets the currently selected planet id
	 */
	public void loadPlanets(){
		loadPlanets(this.get("page=overview"));		
	}
		
	/**
	 * Reads the planet list from a already received page
	 * @param body HTML code of the page to read from
	 */
	public void loadPlanets(String body){
		//empty array
		this.planets = new ArrayList<Planet>();
		//get the div containing our planets
		Document html = Jsoup.parse(body);
		Elements divs = html.select("div.smallplanet");
		for(int i = 0; i < divs.size(); i++){
			Element div = divs.get(i);
			String name = div.select("span.planet-name").text();
			String img = div.select("img.planetPic").attr("src");
			img = img.replace("img/planets/", "").replace(".gif", "");
			img = img.substring(0, img.lastIndexOf("_"));
			int img_id = this.context.getResources().getIdentifier("drawable/planet_" + img, null, context.getPackageName());
			if(img_id == 0){
				FlurryAgent.onError("planetImage", "drawable/planet_" + img, "loadPlanets");
				Log.e(TAG, "Unable to find drawable/planet_" + img);
				img_id = R.drawable.planet_default;
			}
			Element link = div.select("a").get(0);
			String info = link.attr("title");
			int id = 0;
			
			if(!link.attr("href").equals("#")){
				String href = link.attr("href");
				id = Integer.valueOf(href.substring(href.indexOf("&cp=") + "&cp=".length()));
			}
			
			Planet tmp = new Planet(id, name, img_id);
			tmp.setShortInfo(info);
			
			Elements moon = div.select("a.moonlink");
			if(moon.size() > 0){
				Element moon_div = moon.get(0);
				int moon_id = 0;
				String moon_href = moon_div.attr("href");
				if(!moon_href.equals("#")){
					moon_id = Integer.valueOf(moon_href.substring(moon_href.indexOf("&cp=") + "&cp=".length()));
				}
				String moon_img = moon_div.select("img").attr("src");
				String moon_img_nr = Tools.between(moon_img, "_", "_");
				int moon_img_id = this.context.getResources().getIdentifier("drawable/moon_" + moon_img_nr, null, context.getPackageName());
				String moon_name = moon_div.attr("title");
				
				if(this.moon_regex.equals("")){
					moon_name = moon_name.substring(moon_name.indexOf(" "), moon_name.lastIndexOf(" ")).trim();
				}else{
					try{
						Pattern pattern = Pattern.compile(this.moon_regex);				
						Matcher matcher = pattern.matcher(moon_name);
						moon_name = matcher.replaceAll("$1");
					}catch (Exception e) {
						moon_name = moon_name.substring(moon_name.indexOf(" "), moon_name.lastIndexOf(" ")).trim();
					}
				}
				
				Planet m = new Planet(moon_id, moon_name, moon_img_id);		
				m.setShortInfo(info);
				tmp.setMoon(m);
				
				// if planet is active but has an id then the moon is our current planet
				if(link.classNames().contains("active") && tmp.getId() != 0)
					this.current_planet = m;
				
			}
			
			if(tmp.getId() == 0)
				this.current_planet = tmp;
			
			this.planets.add(tmp);			
			
		}
	}
	
	/**
	 * Reads the planetname of the given overview-page
	 * @param body The HTML data of the overview
	 * @return The Planetname
	 */
	private String getPlanetNameFromOverview(String body){
		int start = body.indexOf("planetNameHeader\">") + "planetNameHeader\">".length();
		int end = body.indexOf("<", start);
		return body.substring(start, end).trim();
	}
	
	/**
	 * Runs a request on the OGame server
	 * @param url The url to request
	 * @param token The token for this request
	 * @return The HTTP-Satuscode of the reply
	 */
	public String execute(String url, String token){
		try{
			HttpGet httpget = new HttpGet(this.indexbase + url + "&session=" + this.session + "&token=" + token);
			httpget.addHeader("User-Agent", USER_AGENT);
			Log.i(TAG, "execute " + httpget.getURI().toString());
			HttpResponse response = this.http.execute(httpget);			
			ByteArrayOutputStream ostream = new ByteArrayOutputStream();
            response.getEntity().writeTo(ostream);
			return ostream.toString();
		}catch(Exception ex){
			return "";
		}
	}
	
	/**
	 * Runs a post request on the OGame server
	 * @param url The url to request
	 * @param postData The post data for this request
	 * @return The HTTP-Satuscode of the reply
	 */
	public synchronized String execute(String url, List<NameValuePair> postData){
		try{
			HttpPost httppost = new HttpPost(this.indexbase + url + "&session=" + this.session);
			httppost.addHeader("User-Agent", USER_AGENT);
			httppost.setEntity(new UrlEncodedFormEntity(postData));
			Log.i(TAG, "execute " + httppost.getURI().toString() + " " + httppost.getMethod());
			HttpResponse response = this.http.execute(httppost);
			/*if(response.getStatusLine().getStatusCode() != 200){
				this.login();
				return this.execute(url, postData, token);
			}*/
			ByteArrayOutputStream ostream = new ByteArrayOutputStream();
            response.getEntity().writeTo(ostream);
			return ostream.toString();
		}catch(Exception ex){
			return "";
		}
	}
	
	/**
	 * Runs a post request on the OGame server
	 * @param url The url to request
	 * @param postData The post data for this request
	 * @param token The token for this request
	 * @return The HTTP-Satuscode of the reply
	 */
	public String execute(String url, List<NameValuePair> postData, String token){
		postData.add(new BasicNameValuePair("token", token));
		return  execute(url, postData);
	}
	
	/**
	 * Sends a single build request to the server
	 * @param objectID The id of the {@link BuildObject}
	 * @param pageKey The page we are on
	 * @param token The token for this request
	 * @return The HTTP-Satuscode of the reply
	 */
	public int build(int objectID, String pageKey, String token){
		return this.build(objectID, 1, pageKey, token);
	}
	
	/**
	 * Sends a build request with the given amount to the server
	 * @param objectID The id of the {@link BuildObject}
	 * @param menge The amount of object to build
	 * @param pageKey The page we are on
	 * @param token The token for this request
	 * @return The HTTP-Satuscode of the reply
	 */
	public int build(int objectID, int menge, String pageKey, String token){
		if(menge == 0)
			return 0;
		List<NameValuePair> postData = new ArrayList<NameValuePair>(2);
        postData.add(new BasicNameValuePair("modus", "1"));
        postData.add(new BasicNameValuePair("type", String.valueOf(objectID)));
        postData.add(new BasicNameValuePair("menge", String.valueOf(menge)));
        String body = this.execute("page=" + pageKey, postData, token);
        int c = Tools.getCountdown(body, Tools.getCuetypeById(objectID));
		return c;//
	}
	
	/**
	 * Sends a cancel upgrade request to the server 
	 * @param objectID
	 * @param pageKey
	 */
	public void cancelBuild(int objectID, String pageKey) {
		List<NameValuePair> postData = new ArrayList<NameValuePair>();
        postData.add(new BasicNameValuePair("modus", "2"));
		if("resources".equals(pageKey) || "station".equals(pageKey)) {
			postData.add(new BasicNameValuePair("listid", "1"));
	        postData.add(new BasicNameValuePair("techid", String.valueOf(objectID)));			
		} else if("research".equals(pageKey)) {
	        postData.add(new BasicNameValuePair("type", String.valueOf(objectID)));			
		} else {
			return;
		}
        this.execute("page=" + pageKey, postData);	
	}
	
	public void demolishBuild(int demolish_id){
		this.get("page=resources&modus=3&type=" + String.valueOf(demolish_id));
	}
	
	/**
	 * Send the request to change the current planet
	 * @param id The id of the taget planet
	 */
	public void switchPlanet(int planetId){
		if(planetId != 0){
			loadPlanets(get("page=overview&cp=" + planetId));
		}
	}
	
	/**
	 * Returns the http-body of the given url
	 * @param url The url to load 
	 * @return The http-body of the given url
	 */
	public synchronized String get(String url){
		try{
			HttpGet httpget = new HttpGet(this.indexbase + url + "&session=" + this.session);
			httpget.addHeader("User-Agent", USER_AGENT);
			Log.i(TAG, "get " + httpget.getURI().toString());
			HttpResponse response = this.http.execute(httpget);
			ByteArrayOutputStream ostream = new ByteArrayOutputStream();
            response.getEntity().writeTo(ostream);
            // TODO check if connection is valid. If not call login function
            return ostream.toString();
		}catch(Exception ex){
			return "";
		}
	}	

	/**
	 * Returns the ArrayList containing every {@link Planet}
	 * @return The ArrayList containing every {@link Planet}
	 */
	public ArrayList<Planet> getPlanets(){
		return this.planets;
	}
	
	/**
	 * Returns the currently selected {@link Planet}
	 * @return The currently selected {@link Planet}
	 */
	public Planet getCurrentPlanet(){
		Log.i("Current Planet", this.current_planet.getName());
		return this.current_planet;
	}
	
	/**
	 * Returns path of the image folder
	 * @return path of the image folder
	 */
	public String getImageBase(){
		return this.imagebase;
	}
	
	public Message[] getMassages(){
		return getMassages(10);
	}
	
	/**
	 * Reads messages from server
	 * @return All messages displayed on the first page
	 */
	public Message[] getMassages(int displayCategory){
		Document html = Jsoup.parse(get("page=messages&displayCategory=" + String.valueOf(displayCategory) + "&ajax=1"));
		Elements tr = html.select("tr.entry"); 
		if(tr.size() <= 0){
			Message m[] = new Message[0];
			return m;
		}
		Message m[] = new Message[tr.size()];
		for(int i = 0; i < m.length; i++){
			m[i] = Message.parse(tr.get(i));
		}
		return m;		
	}
	
	/**
	 * Deletes the message with the given ID
	 * @param msg_id The ID to delete
	 */
	public void deleteMessage(int msg_id){
		this.deleteMessage(new int[]{msg_id});
	}
	
	/**
	 * Deletes all messages with the given IDs
	 * @param msg_ids The IDs to delete
	 */
	public void deleteMessage(int msg_ids[]){
		this.actionMessage(msg_ids, Message.ACTION_DELETE);
	}
	
	public void actionMessage(int msg_ids[], int action){
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		
		for(int msg_id : msg_ids)
			 nameValuePairs.add(new BasicNameValuePair("deleteMessageIds[]", String.valueOf(msg_id)));		
       
        nameValuePairs.add(new BasicNameValuePair("actionMode", String.valueOf(action)));
        this.execute("page=messages", nameValuePairs);
	}
	
	public void actionMessage(int msg_id, int action){
		this.actionMessage(new int[]{msg_id}, action);
	}
	
	/**
	 * Send a message to the given Player
	 * @param playerID The ID of the target
	 * @param subject Subject Text
	 * @param text Message Body
	 * @param relationMessageId 
	 * @param isAnswerMessage 
	 * @return Html data of the result page
	 */
	public String sendMessage(int playerID, String subject, String text, int isAnswerMessage, int relationMessageId){
		String url = "page=messages&to=" + playerID;
		if(isAnswerMessage != 0){
			url += "&isAnswerMessage=" + String.valueOf(isAnswerMessage);
		}
		if(relationMessageId != 0){
			url += "&relationMessageId=" + String.valueOf(relationMessageId);
		}
		
		List<NameValuePair> postData = new ArrayList<NameValuePair>();
		//They are using the German word on every server O_o
        postData.add(new BasicNameValuePair("betreff", subject));
        postData.add(new BasicNameValuePair("text", text));
		return execute(url, postData);
	}
	
	/**
	 * Finds ID and name for the Player closest to the given name
	 * @param playerName The name to search for
	 * @return ID and name of the first result
	 */
	public Player findPlayer(String playerName){
		Document html = Jsoup.parse(get("page=search&ajax=1&method=2&currentSite=1&searchValue=" + playerName));
		Elements tr = html.select("tr");
		if(tr.size() <= 1)
			return new Player(0);
		Element link = tr.get(1).select("td.action > a").get(0);
		String name = tr.get(1).select("td.userName").get(0).text();
		return new Player(Integer.valueOf(Tools.between(link.attr("onclick"), "to=", "&")), name);
	}
	
	/**
	 * Tries to rename current Planet
	 * @param newPlanetName New Name
	 * @return True if successful, false on any error
	 */
	public boolean renamePlanet(String newPlanetName){
		List<NameValuePair> postData = new ArrayList<NameValuePair>();
        postData.add(new BasicNameValuePair("newPlanetName", newPlanetName));
        String result = execute("page=planetRename", postData);
        //returns json like {"status":true,"newName":"...","errorbox":{"type":"fadeBox","text":"...","failed":0}}
        try{
        	JSONObject json = new JSONObject(result);
        	return json.getBoolean("status");
        }catch(Exception e){
        	return false;
        }        
	}
	
	public FleetEvent[] getCancelableFleetEvents(){
		Document html = Jsoup.parse(get("page=movement"));	    	
    	String script = html.select("script").not("script[src]").html();
		
		Elements events = html.select("div[id^=fleet]");
		
		FleetEvent fes[] = new FleetEvent[events.size()];
		
		Log.i("getFleetEvents", "count: " + fes.length);
		
		int count = 0;
		
		for(Element event : events){
			String id = event.id().replace("fleet", "");
			FleetEvent fe = new FleetEvent(Integer.valueOf(id));
			fe.setArrivalTime(event.select("span.absTime").text());
			fe.setMission(event.select("span.mission").text());
			
			fe.setOriginName(event.select("span.originPlanet").text());
			fe.setOriginCoords(event.select("span.originCoords").text());

			fe.setDestinationName(event.select("span.destinationPlanet").text());
			fe.setDestinationCoords(event.select("span.destinationCoords").text());
			
			fe.setCanCancel(event.select("span.reversal").size() > 0);
			
			if(fe.getDestinationName().length() == 0)
				fe.setDestinationName(fe.getMission());
			
			Element table = event.select("div#bl"+id + " > table").first();
			String info = "";
			
			Elements tr = table.select("tr");
	    	for(int i = 0; i < tr.size(); i++){
	    		info += tr.get(i).text() + "\n";
	    	}
			
	    	fe.setInfo(info);		
	    	
	    	int param[] = Tools.getMovementImageCountdownParameters(script, fe.getID());
	    	if(param != null){
	    		fe.setReturn(param[2] == 1);		    	
		    	fe.setTimeLeft(param[0]);
	    	}
	    	/*
	    	 new movementImageCountdown(
		        getElementByIdWithCache("route_12818421"),  // ID
		    0    3663,										// leftoverTime
		    1    11582,										// duration
		    2    1,											// isReturn
		    3    0    );										// isRTL
	    	 */
	    	
	    	fes[count] = fe;
	    	count++;			
		}		
		return fes;
	}
	
	public FleetEvent[] getFleetEvents(){
		Document html = Jsoup.parse(get("page=eventList&ajax=1"));	    	
		
		Elements events = html.select("div.eventFleet");
		
		FleetEvent fes[] = new FleetEvent[events.size()];
		
		int count = 0;
		
		for(Element event : events){
			String id = event.id().replace("eventRow-", "");
			FleetEvent fe = new FleetEvent(Integer.valueOf(id));
			fe.setArrivalTime(event.select("li.arrivalTime").text());
			fe.setMission(event.select("li.missionFleet").text());
			
			fe.setOriginName(event.select("li.originFleet").text());
			fe.setOriginCoords(event.select("li.coordsOrigin").text());

			fe.setDestinationName(event.select("li.destFleet").text());
			fe.setDestinationCoords(event.select("li.destCoords").text());
			
			fe.setCanCancel(event.select("span.reversal").size() > 0);
			
			if(fe.getDestinationName().length() == 0)
				fe.setDestinationName(fe.getMission());
			
			
			Element table = Jsoup.parse(get("page=eventListTooltip&ajax=1&eventID=" + id)).select("table").first();
			String info = "";
			
			Elements tr = table.select("tr");
	    	for(int i = 0; i < tr.size(); i++){
	    		info += tr.get(i).text() + "\n";
	    	}
			
	    	fe.setInfo(info);		
	    	
	    	/*
	    	 new movementImageCountdown(
		        getElementByIdWithCache("route_12818421"),  // ID
		    0    3663,										// leftoverTime
		    1    11582,										// duration
		    2    1,											// isReturn
		    3    0    );										// isRTL
	    	 */
	    	
	    	fes[count] = fe;
	    	count++;			
		}		
		return fes;
	}
	
	public ArrayList<Ship> getFleet(String[] ulKeys){
		final Document document = Jsoup.parse(this.get("page=fleet1"));
		
		ArrayList<Ship> objectlist = new ArrayList<Ship>();		
		for (String ulKey : ulKeys) {
			Elements ul = document.select("ul#" + ulKey);
			
			if(ul.size() == 0){
				continue;
			}		
			
			for(Element li : ul.select("li")) {
				
				String status = li.className();
				if(!"on".equals(status)) {
					continue;
				}
				
				String id = li.id().replace("button", "");
				String name = li.select("span.textlabel").text();
				String total = li.select("span.level").text().replace(name, "").trim().replace(".", "");
				
				Ship m = new Ship(Integer.valueOf(id), name, Integer.valueOf(total), context);
	
				objectlist.add(m);
			}
		}
		return objectlist;
	}
	
	public void cancelFleetEvent(int id){
		get("page=movement&return=" + String.valueOf(id));
	}
	
	public String getBaseUrl(){
		return this.imagebase;
	}
	
	public String getIndexUrl(){
		return this.indexbase;
	}
	
	public Player[] getBuddyList(){		
		Document html = Jsoup.parse(get("page=buddies&action=11"));
		Elements entries = html.select("tr");
		int count = entries.size() - 1; //remove header
		Player buddies[] = new Player[count];
		
		for(int i = 0; i < count; i++){
			Player p = new Player();
			Element row = entries.get(i + 1);
			Elements data = row.select("td");
			p.setPlayerName(data.get(1).text());
			p.setPoints(Integer.valueOf(data.get(2).text().replace(".", "")));
			p.setRank(Integer.valueOf(data.get(3).text().replace(".", "")));
			Element allyLink = data.get(4).select("a").first();
			p.setAllianceName(allyLink.text());
			String allyID = allyLink.attr("href");
			allyID = allyID.substring(allyID.indexOf("allyid=") + "allyid=".length());
			p.setAllianceID(Integer.valueOf(allyID));
			p.setOnline(data.get(6).select("span").first().hasClass("undermark"));
			String playerID = data.get(7).select("a").first().attr("onclick");
			playerID = Tools.between(playerID, "to=", "&");
			p.setPlayerID(Integer.valueOf(playerID));		
			buddies[i] = p;
		}
		
		return buddies;
	}
}
