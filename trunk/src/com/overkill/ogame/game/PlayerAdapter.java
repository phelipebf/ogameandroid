package com.overkill.ogame.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.overkill.ogame.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

public class PlayerAdapter extends ArrayAdapter<Player> { 
	
	private Context context;
	private int textViewResourceId;
	private ArrayList<Player> objects;
	private ArrayList<Player> filteredobjects;
	
	public PlayerAdapter(Context context, int textViewResourceId, ArrayList<Player> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.textViewResourceId = textViewResourceId;
		this.objects = objects;
		this.sort(this.objects);
		this.filteredobjects = this.objects;
	}	
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(this.textViewResourceId, null);
            }
            Player p = this.objects.get(position);
            if (p != null) {            
            	((RadioButton)v.findViewById(R.id.online)).setFocusable(false);
            	((RadioButton)v.findViewById(R.id.online)).setChecked(p.isOnline());
            	((TextView)v.findViewById(R.id.txt_playerName)).setText(p.getPlayerName());
            }
            return v;
    }	
	
	public int getCount(){
		return this.filteredobjects.size();
	}
	
	public int filter(String name){
		if(name.equals("")){
			this.filteredobjects = this.objects;
			return this.getCount();
		}
		int total = this.objects.size();
		this.filteredobjects.clear();
		for(int i = 0; i < total; i++){
			if(this.objects.get(i).getPlayerName().contains(name))
				this.filteredobjects.add(this.objects.get(i));
		}
		return this.getCount();
	}
	
	public void sort(ArrayList<Player> list){
		Collections.sort(list, new Comparator<Player>() {
			@Override
			public int compare(Player object1, Player object2) {
				return object1.getPlayerName().compareTo(object2.getPlayerName());
			}
		});
	}
}
