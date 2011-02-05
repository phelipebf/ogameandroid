package com.overkill.ogame.game;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

/**
 * Controller für interaktion mit ogame server
 * @author Stephan
 *
 */
public class GameClient{
	private static final String TAG = "ogame-core";
	private final boolean D = true;
	private final String USER_AGENT = "Mozilla/5.0 (Linux; U; Android 2.2.1; en-us; generic) AppleWebKit/530.17 (KHTML, like Gecko) Version/4.0 Mobile Safari/530.17";
	
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
	
	/**
	 * Creates a new Game object
	 * @param http The {DefaultHttpClient} containing cookies from login
	 * @param session The session-id passed on in the url
	 * @param base The base of the url, containing univers and tld
	 */
	public GameClient(DefaultHttpClient http, String session, String universe) {
		this.http = http;
		this.session = session;
		this.indexbase = "http://"  + universe + "/game/index.php?";
		this.imagebase = "http://"  + universe + "/game/";
		this.loadPlanets();
	}
	
	public GameClient(){
		//nothing todo
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
		int start = body.indexOf("<div class=\"smallplanet\">");
		int end = 0;
		int i = 0;
		while(start > 0){
			start += "<div class=\"smallplanet\">".length();
			end = body.indexOf("</div>", start);
			String div = body.substring(start, end);
			int id = 0;
			if(div.contains("&cp="))
				id = Integer.valueOf(Tools.between(div, "&cp=", "\""));
			String img = this.imagebase + Tools.between(div, "src=\"", "\"");
			String name = Tools.between(div, "planet-name\">", "<");
			String info = Tools.between(div, "title=\"", "\">");
			
			Planet tmp = new Planet(id, name, img);
			tmp.setShortInfo(info);
			
			if(div.contains("<a class=\"moonlink")){
				String moon_div = Tools.between(div, "<a class=\"moonlink", "</a>");
				int moon_id = 0;
				if(moon_div.contains("&cp="))
					moon_id = Integer.valueOf(Tools.between(div, "&cp=", "\""));
				String moon_img = this.imagebase + Tools.between(moon_div, "src=\"", "\"");
				//replace img/planets/moon/moon_2_small.gif with img/planets/large/moon_2.gif
				String moon_img_nr = Tools.between(moon_img, "_", "_");
				moon_img = this.imagebase + "img/planets/large/moon_" + moon_img_nr + ".gif";
				String moon_name = Tools.between(moon_div, "title=\"", "\"");
				moon_name = moon_name.substring(moon_name.indexOf(" "), moon_name.lastIndexOf(" ")).trim();
				
				Planet moon = new Planet(moon_id, moon_name, moon_img);		
				moon.setShortInfo(info);
				tmp.setMoon(moon);				
			}
			
			//add planet to array
			this.planets.add(tmp);			
			//check if this planet is currently selected
			if(this.getPlanetNameFromOverview(body).equals(tmp.getName()))
				this.current_planet = tmp;		
			
			if(tmp.hasMoon() && this.getPlanetNameFromOverview(body).equals(tmp.getMoon().getName()))
				this.current_planet = tmp.getMoon();	
			
			start = body.indexOf("<div class=\"smallplanet\">", start);
			i++;
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
	public String execute(String url, List<NameValuePair> postData){
		try{
			HttpPost httppost = new HttpPost(this.indexbase + url + "&session=" + this.session);
			httppost.addHeader("User-Agent", USER_AGENT);
			httppost.setEntity(new UrlEncodedFormEntity(postData));
			Log.i(TAG, "execute " + httppost.getURI().toString() + " POST");
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
        int c = Tools.getCountdown(body, Tools.getQuetypeById(objectID));
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
	
	/**
	 * Reads messages from server
	 * @return All messages displayed on the first page
	 */
	public Message[] getMassages(){
		String html = Tools.between(get("page=messages&ajax=1"), "<tbody>", "</tbody>");
		String tr[] = html.split("</tr>");
		if(tr.length < 3){
			Message m[] = new Message[0];
			return m;
		}
		Message m[] = new Message[tr.length - 3];
		for(int i = 1; i <= m.length; i++){
			m[i-1] = Message.parse(tr[i].trim());
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
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		
		for(int msg_id : msg_ids)
			 nameValuePairs.add(new BasicNameValuePair("deleteMessageIds[]", String.valueOf(msg_id)));		
       
        nameValuePairs.add(new BasicNameValuePair("actionMode", "2"));
        this.execute("page=messages", nameValuePairs);
	}
}
