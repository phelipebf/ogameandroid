package com.overkill.ogame.game;

/*
 * File: WordsAdapter.java
 * Platform: Android 1.5
 * Last update: 24.01.2010 
 * ©2010 OV3RK1LL 
 */
import java.util.ArrayList;
import com.overkill.ogame.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.TextView;

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
		Ship b = this.getItem(position);
		if (b != null) {			
			((TextView) v.findViewById(R.id.name)).setText(b.getName());
			((TextView) v.findViewById(R.id.menge)).setText(String.valueOf(b.getCount()));
			((TextView) v.findViewById(R.id.used)).setText(String.valueOf(b.getUsed()));
			((ImageView) v.findViewById(R.id.image)).setImageResource(b.getIcon());
			((ImageButton) v.findViewById(R.id.ship_add)).setOnClickListener(new ImageButton.OnClickListener() {				
				@Override
				public void onClick(View v) {
					FleetAdapter.this.getItem(position).add();
					FleetAdapter.this.notifyDataSetChanged();
				}
			});
			((ImageButton) v.findViewById(R.id.ship_remove)).setOnClickListener(new ImageButton.OnClickListener() {				
				@Override
				public void onClick(View v) {
					FleetAdapter.this.getItem(position).remove();
					FleetAdapter.this.notifyDataSetChanged();
				}
			});
			((ImageButton) v.findViewById(R.id.ship_all)).setOnClickListener(new ImageButton.OnClickListener() {				
				@Override
				public void onClick(View v) {
					FleetAdapter.this.getItem(position).setUsed(FleetAdapter.this.getItem(position).getCount());
					FleetAdapter.this.notifyDataSetChanged();
				}
			});
		}
		return v;
	}
}
