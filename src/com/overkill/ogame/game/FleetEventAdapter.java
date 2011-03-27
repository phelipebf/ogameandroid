package com.overkill.ogame.game;

/*
 * File: WordsAdapter.java
 * Platform: Android 1.5
 * Last update: 24.01.2010 
 * �2010 OV3RK1LL 
 */
import java.util.ArrayList;
import com.overkill.ogame.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class FleetEventAdapter extends ArrayAdapter<FleetEvent> {

	private Context context;
	private int textViewResourceId;

 	public FleetEventAdapter(Context context, int textViewResourceId, ArrayList<FleetEvent> objects) {
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
		FleetEvent b = this.getItem(position);
		if (b != null) {			
			((TextView) v.findViewById(R.id.originName)).setText(b.getOriginName());
			((TextView) v.findViewById(R.id.originCoords)).setText(String.valueOf(b.getOriginCoords()));
			((TextView) v.findViewById(R.id.destName)).setText(String.valueOf(b.getDestName()));
			((TextView) v.findViewById(R.id.destCoords)).setText(String.valueOf(b.getDestCoords()));
			((TextView) v.findViewById(R.id.mission)).setText(String.valueOf(b.getMission()));
			((TextView) v.findViewById(R.id.info)).setText(String.valueOf(b.getArrivalTime()));			
		}
		return v;
	}
}
