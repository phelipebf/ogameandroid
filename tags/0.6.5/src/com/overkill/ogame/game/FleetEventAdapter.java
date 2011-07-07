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
import android.widget.TextView;

public class FleetEventAdapter extends ArrayAdapter<FleetEvent> {

	private Context context;
	private int textViewResourceId;

 	public FleetEventAdapter(Context context, int textViewResourceId, ArrayList<FleetEvent> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.textViewResourceId = textViewResourceId;
	}
 	
	public FleetEventAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		this.context = context;
		this.textViewResourceId = textViewResourceId;
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
			((TextView) v.findViewById(R.id.mission)).setText(String.valueOf(b.getMission()));
			((TextView) v.findViewById(R.id.info)).setText(String.valueOf(b.getArrivalTime()));		
			if(b.isReturn()){
				((TextView) v.findViewById(R.id.originName)).setText(b.getDestinationName());
				((TextView) v.findViewById(R.id.originCoords)).setText(b.getDestinationCoords());	
				((TextView) v.findViewById(R.id.destName)).setText(b.getOriginName());
				((TextView) v.findViewById(R.id.destCoords)).setText(b.getOriginCoords());
			}else{
				((TextView) v.findViewById(R.id.originName)).setText(b.getOriginName());
				((TextView) v.findViewById(R.id.originCoords)).setText(b.getOriginCoords());
				((TextView) v.findViewById(R.id.destName)).setText(b.getDestinationName());
				((TextView) v.findViewById(R.id.destCoords)).setText(b.getDestinationCoords());	
			}
		}
		return v;
	}
}
