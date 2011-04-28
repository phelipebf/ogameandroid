package com.overkill.ogame.game;

/*
 * File: WordsAdapter.java
 * Platform: Android 1.5
 * Last update: 24.01.2010 
 * ©2010 OV3RK1LL 
 */
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.overkill.ogame.FleetView;
import com.overkill.ogame.R;

public class FleetAdapter extends ArrayAdapter<Ship> {

	private Context context;
	private int textViewResourceId;

 	public FleetAdapter(Context context, int textViewResourceId, ArrayList<Ship> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.textViewResourceId = textViewResourceId;
//		this.sort(new Comparator<BuildObject>() {
//			public int compare(BuildObject object1, BuildObject object2) {
//				return object1.getName().compareTo(object2.getName());		
//			}});
	}
 	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) this.context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(this.textViewResourceId, parent, false);
		}
		final Ship b = this.getItem(position);
		if (b != null) {			
			((TextView) v.findViewById(R.id.name)).setText(b.getName());
			((TextView) v.findViewById(R.id.used)).setText(String.valueOf(b.getUsed()));
			((TextView) v.findViewById(R.id.total)).setText(String.valueOf(b.getTotal()));
			
			((ImageButton) v.findViewById(R.id.image)).setImageResource(b.getIcon());
			((ImageButton) v.findViewById(R.id.image)).setOnClickListener(new ImageButton.OnClickListener() {				
				@Override
				public void onClick(View v) {
					FleetAdapter.this.getItem(position).setUsed(FleetAdapter.this.getItem(position).getTotal());
					FleetAdapter.this.notifyDataSetChanged();
				}
			});

			final Button next = (Button) ((FleetView)context).findViewById(R.id.fleet1_next);
			
			((Button) v.findViewById(R.id.add1)).setOnClickListener(new Button.OnClickListener() {				
				@Override
				public void onClick(View v) {
					((FleetView)context).selectedShips++;
					FleetAdapter.this.getItem(position).add(1);
					next.setEnabled(true);
					FleetAdapter.this.notifyDataSetChanged();
				}
			});

			Button add10 = (Button) v.findViewById(R.id.add10);			
			if(b.getTotal() > 10) {
				add10.setVisibility(View.VISIBLE);
			} else {
				add10.setVisibility(View.GONE);				
			}
			add10.setOnClickListener(new Button.OnClickListener() {				
				@Override
				public void onClick(View v) {
					((FleetView)context).selectedShips += 10;
					FleetAdapter.this.getItem(position).add(10);
					next.setEnabled(true);
					FleetAdapter.this.notifyDataSetChanged();
				}
			});

			Button add100 = (Button) v.findViewById(R.id.add100);			
			if(b.getTotal() > 100) {
				add100.setVisibility(View.VISIBLE);
			} else {
				add100.setVisibility(View.GONE);				
			}
			add100.setOnClickListener(new Button.OnClickListener() {				
				@Override
				public void onClick(View v) {
					((FleetView)context).selectedShips += 100;
					FleetAdapter.this.getItem(position).add(100);
					next.setEnabled(true);
					FleetAdapter.this.notifyDataSetChanged();
				}
			});
			
			((Button) v.findViewById(R.id.reset)).setOnClickListener(new Button.OnClickListener() {				
				@Override
				public void onClick(View v) {
					((FleetView)context).selectedShips -= b.getUsed();
					if(((FleetView)context).selectedShips == 0) {
						next.setEnabled(false);
					}
					FleetAdapter.this.getItem(position).setUsed(0);
					FleetAdapter.this.notifyDataSetChanged();
				}
			});
		}
		return v;
	}
}
