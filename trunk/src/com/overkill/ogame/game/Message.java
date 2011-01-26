package com.overkill.ogame.game;

import com.flurry.android.FlurryAgent;

public class Message {
	private int ID;
	private String from;
	private String to;
	private String subject;
	private String date;
	private String content;
	private boolean read;
	
	public Message(int ID) {
		this.ID = ID;
		this.read = false;
	}
	
	public Message(int ID, String from, String subject, String date) {
		this.ID = ID;
		this.from = from;
		this.subject = subject;
		this.date = date;
		this.read = false;
	}

	public Message(int ID, String from, String subject, String date, boolean read) {
		this.ID = ID;
		this.from = from;
		this.subject = subject;
		this.date = date;
		this.read = read;
	}

	public static Message parse(String html){
		try{
			int ID = Integer.valueOf(Tools.between(html,"id=\"", "TR"));
			String from = Tools.between(html,"<td class=\"from\">", "</td>");
			String date = Tools.between(html,"<td class=\"date\">", "</td>");
			String subject = Tools.between(html,"<td class=\"subject\">", "</a>");
			if(subject.contains("<span")){
				subject = subject.substring(subject.indexOf(">") + 1).trim(); //a
				subject = subject.substring(subject.indexOf(">") + 1, subject.lastIndexOf("<")).trim(); //span
			}else{
				subject = subject.substring(subject.lastIndexOf(">") + 1).trim();
			}
			while(subject.contains(">"))
				subject = Tools.between(subject, ">", "<");
			boolean read = !html.contains("entry trigger new");
			return new Message(ID, from, subject, date, read);		
		}catch(Exception e){
			FlurryAgent.onError("Message.parse", html, "Message.parse");
			return new Message(0, "ogame.core.Message", "parseing error", "", false);
		}
	}

	public int getID() {
		return ID;
	}

	public String getFrom() {
		return from;
	}

	public String getSubject() {
		return subject;
	}

	public String getDate() {
		return date;
	}
	
	public boolean getRead() {
		return read;
	}

	public String getContent(){
		return this.content;		
	}
	
	public String getTo(){
		return this.to;		
	}
	
	public void setContent(String content){
		this.content = content;		
	}
	
	public void setRead(boolean read){
		this.read = read;
	}
	
	public void setID(int iD) {
		ID = iD;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
	public void setTo(String to) {
		this.to = to;
	}

	
	
	public String toString(){
		if(read)
			return subject + " from " + from + " (" + date + ")";		
		else
			return "[new] " + subject + " from " + from + " (" + date + ")";		
	}
}
