package com.overkill.gui;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.ParseException;

import com.overkill.ogame.game.Tools;

import android.content.Context;
import android.widget.ArrayAdapter;

/**
 * HtmlSelect reads and <select> tag and lets you read the key and value data
 * key and value are defined as <option value="VALUE">KEY</option>
 * @author ov3rk1ll
 *
 */
public class HtmlSelect{

	private ArrayList<String> mKeys;
	private ArrayList<String> mValues;
	
	public HtmlSelect(String html) {
		parse(html);
	}
	
	public HtmlSelect(File html) {
		parse(html); 
	}
	
	
	public Object getKey(int position) {
		return mKeys.get(position);
	}
	
	public String getValue(int position) {
		return mValues.get(position);
	}
	
	public ArrayAdapter<String> toAdapter(Context context){
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		for(String key : mKeys){
			adapter.add(key);
		}
		return adapter;
	}
	
	private void parse(File file){
		byte[] buffer = new byte[(int) file.length()];
	    BufferedInputStream f = null;
	    try {
	        f = new BufferedInputStream(new FileInputStream(file.getAbsolutePath()));
	        f.read(buffer);
	    } catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
	        if (f != null) try { f.close();	} catch (IOException e) { }
	    }
	    parse(new String(buffer));
	}
	
	private void parse(String html){
		html = html.trim();		
		if(!html.startsWith("<select") && !html.startsWith("<option"))
			throw new ParseException("String does not start with <select or <option");
		if(html.startsWith("<select"))
			html = html.substring(html.indexOf(">") + 1);
		html = html.replace("</select>", "");
		String[] options = html.split("</option>");
		mKeys = new ArrayList<String>(options.length);
		mValues = new ArrayList<String>(options.length);
		for(String option : options){	
			if(option.contains("value=\"") == false)
				continue;
			option = option.trim();
			String value = Tools.between(option, "value=\"", "\"").trim();
			String key = option.substring(option.indexOf(">") + 1).trim();
			mKeys.add(key);
			mValues.add(value);
		}
	}
	
}
