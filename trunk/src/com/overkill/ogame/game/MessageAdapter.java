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
import android.widget.TextView;

public class MessageAdapter extends ArrayAdapter<Message> {

	private Context context;
	private int textViewResourceId;

 	public MessageAdapter(Context context, int textViewResourceId, ArrayList<Message> objects) {
		super(context, textViewResourceId, objects);
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
		Message b = this.getItem(position);
		if (b != null) {			
			ImageView state = (ImageView) v.findViewById(R.id.state);
			if(b.getRead())
				state.setImageResource(R.drawable.post_read);
			else
				state.setImageResource(R.drawable.post_unread);
			((TextView) v.findViewById(R.id.subject)).setText(b.getSubject());
			((TextView) v.findViewById(R.id.from)).setText(b.getFrom());	
			((TextView) v.findViewById(R.id.date)).setText(b.getDate());	
		}
		return v;
	}
}
