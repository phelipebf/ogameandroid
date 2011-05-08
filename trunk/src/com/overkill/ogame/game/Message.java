package com.overkill.ogame.game;

import org.jsoup.nodes.Element;
import android.graphics.Color;

public class Message {
	public static final int ACTION_DELETE = 2;
	public static final int ACTION_RESTORE = 2;
	public static final int ACTION_READ = 12;
	
	public static final int FILTER_INBOX = 10;
	public static final int FILTER_BIN = 3;
	public static final int FILTER_ESPIONAGE = 7;
	public static final int FILTER_BATTLE = 5;
	public static final int FILTER_PLAYER = 6;
	public static final int FILTER_EXPEDITION = 8;
	public static final int FILTER_ALLIANCE = 2;
	public static final int FILTER_OTHER = 4;
	
	private int ID;
	private String from;
	private String to;
	private String subject;
	private String date;
	private String content;
	private String html;
	private int color;
	private boolean read;
	private boolean checked;
	
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
	
	public Message(int ID, String from, String subject, String date, boolean read, int color) {
		this.ID = ID;
		this.from = from;
		this.subject = subject;
		this.date = date;
		this.read = read;
		this.color = color;
	}

	public static Message parse(Element tr) {
		try{
			boolean read = !tr.classNames().contains("new");
			int ID = Integer.valueOf(tr.id().replace("TR", ""));
			String from = tr.select("td.from").text();
			String subject = tr.select("td.subject").text();
			String date = tr.select("td.date").text();
			int color = Color.WHITE;
			
			if(tr.select("td.subject > a > span").size() > 0){ //span tag with color
				if(tr.select("td.subject > a > span").first().className().contains("iwon"))
					color = Color.GREEN;
				else if(tr.select("td.subject > a > span").first().className().contains("draw"))
					color = Color.WHITE;
				else
					color = Color.RED;
			}			
			return new Message(ID, from, subject, date, read, color);
		}catch(Exception e){
			return new Message(0, "ogame.core.Message", "error", "", false);
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
	
	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public String toString(){
		if(read)
			return subject + " from " + from + " (" + date + ")";		
		else
			return "[new] " + subject + " from " + from + " (" + date + ")";		
	}
}
